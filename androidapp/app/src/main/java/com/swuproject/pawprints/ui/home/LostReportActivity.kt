package com.swuproject.pawprints.ui.home

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.cloud.storage.Storage
import com.swuproject.pawprints.R
import com.swuproject.pawprints.common.FullScreenImageActivity
import com.swuproject.pawprints.common.MapActivity
import com.swuproject.pawprints.common.Utils
import com.swuproject.pawprints.databinding.ActivityLostReportBinding
import com.swuproject.pawprints.dto.LostReportResponse
import com.swuproject.pawprints.network.Pet
import com.swuproject.pawprints.network.RetrofitClient
import com.swuproject.pawprints.network.RetrofitService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class LostReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLostReportBinding
    private var selectedPetName: String? = null // 선택된 반려동물 이름 저장
    private var selectedTextView: TextView? = null // 선택된 텍스트뷰 저장

    // Google Cloud Storage 변수 선언
    private lateinit var storage: Storage
    private var selectedImageUri: Uri? = null
    private var selectedPet: Pet? = null

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            binding.imageSection.findViewById<ImageView>(R.id.selected_image).apply {
                setImageBitmap(bitmap)
                visibility = View.VISIBLE // 이미지가 선택되면 보이도록 설정
            }
        }
    }

    private var selectedLat: Double? = null
    private var selectedLng: Double? = null

    // RetrofitClient를 통해 RetrofitService 가져오기
    private val retrofitService: RetrofitService by lazy {
        RetrofitClient.getRetrofitService()
    }


    // 결과를 받아오는 ActivityResultLauncher 정의
    private val mapActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                // 반환된 주소 정보를 받아옴
                val selectedAddress = intent.getStringExtra("selected_address")
                selectedLat = intent.getDoubleExtra("selected_lat", 0.0)
                selectedLng = intent.getDoubleExtra("selected_lng", 0.0)

                selectedAddress?.let {
                    binding.petAreaText.text = it

                    // 위도, 경도를 로그와 토스트 메시지로 출력
                    Log.d("SightReportActivity", "Selected Latitude: $selectedLat")
                    Log.d("SightReportActivity", "Selected Longitude: $selectedLng")

                    Toast.makeText(this, "위도: $selectedLat, 경도: $selectedLng", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 기본 액션바 숨기기
        Utils.hideActionBar(this)

        // 상태 표시줄 색상 변경
        Utils.setStatusBarColor(this, R.color.primary_pink)

        // 뒤로 가기 아이콘에 클릭 리스너 설정
        binding.iconBack.setOnClickListener {
            onBackPressed()
        }

        // SharedPreferences에서 사용자 ID 가져오기
        val userId = Utils.getUserId(this)

        // 반려동물 목록 가져오기
        if (userId != null) {
            binding.petListSection.removeAllViews()
            val loadingTextView = TextView(this).apply {
                text = "반려동물 정보를 가져오는 중입니다..."
                textSize = 16f
            }
            binding.petListSection.addView(loadingTextView)
            fetchPets(userId)
        }

        // 이미지 업로드 버튼 클릭 시
        binding.imageUploadButton.setOnClickListener {
            getImage.launch("image/*")
        }

        // 이미지 섹션 클릭 시 전체 화면으로 이미지 보기
        binding.imageSection.setOnClickListener {
            showFullImage()
        }

        // 지역 선택 버튼 클릭 시 MapActivity로 이동
        binding.petAreaSelect.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            mapActivityResultLauncher.launch(intent)  // MapActivity 호출
        }

        // 사용자가 날짜 입력 필드를 클릭할 때, 날짜 선택 다이얼로그를 표시함
        binding.lostReportDate.setOnClickListener {
            showDatePicker()
        }

        // reportButton 클릭 리스너
        binding.reportButton.setOnClickListener {
            // 모든 필드가 채워졌는지 확인하는 코드 추가 - 수정된 부분
            if (binding.lostReportTitle?.text.isNullOrEmpty() ||
                binding.lostReportAge?.text.isNullOrEmpty() ||
                binding.petAreaText.text.isNullOrEmpty() ||
                binding.lostReportDate.text.isNullOrEmpty() ||
                binding.lostReportLocation.text.isNullOrEmpty() ||
                binding.lostReportDescription.text.isNullOrEmpty() ||
                binding.lostReportContact.text.isNullOrEmpty() ||
                selectedImageUri == null ||
                selectedLat == null || selectedLng == null
            ) {
                Toast.makeText(this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show()
            } else {
                selectedImageUri?.let { uri ->
                    try {
                        val inputStream = contentResolver.openInputStream(uri)
                        inputStream?.let {
                            val fileName = getFileName(uri)

                            val requestFile =
                                RequestBody.create("image/*".toMediaTypeOrNull(), it.readBytes())
                            val body =
                                MultipartBody.Part.createFormData("file", fileName, requestFile)

                            selectedPet?.let { pet ->
                                val petId = pet.id
                                val petType = pet.type

                                val petIdRequest = RequestBody.create(
                                    "text/plain".toMediaTypeOrNull(), petId.toString()
                                )
                                val lostTitleRequest = RequestBody.create(
                                    "text/plain".toMediaTypeOrNull(),
                                    binding.lostReportTitle?.text.toString()
                                )
                                val petTypeRequest = RequestBody.create(
                                    "text/plain".toMediaTypeOrNull(), petType.toString()
                                )
                                val lostAreaLatRequest = RequestBody.create(
                                    "text/plain".toMediaTypeOrNull(), selectedLat?.toString() ?: ""
                                )
                                val lostAreaLngRequest = RequestBody.create(
                                    "text/plain".toMediaTypeOrNull(), selectedLng?.toString() ?: ""
                                )
                                val lostDateRequest = RequestBody.create(
                                    "text/plain".toMediaTypeOrNull(),
                                    binding.lostReportDate.text.toString()
                                )
                                val lostLocationRequest = RequestBody.create(
                                    "text/plain".toMediaTypeOrNull(),
                                    binding.lostReportLocation.text.toString()
                                )
                                val lostDescriptionRequest = RequestBody.create(
                                    "text/plain".toMediaTypeOrNull(),
                                    binding.lostReportDescription.text.toString()
                                )
                                val lostContactRequest = RequestBody.create(
                                    "text/plain".toMediaTypeOrNull(),
                                    binding.lostReportContact.text.toString()
                                )


                                val call = retrofitService.createLostReport(
                                    body,
                                    petIdRequest,
                                    lostTitleRequest,
                                    petTypeRequest,
                                    lostAreaLatRequest,
                                    lostAreaLngRequest,
                                    lostDateRequest,
                                    lostLocationRequest,
                                    lostDescriptionRequest,
                                    lostContactRequest
                                )

                                call.enqueue(object : Callback<LostReportResponse> {
                                    override fun onResponse(
                                        call: Call<LostReportResponse>,
                                        response: Response<LostReportResponse>
                                    ) {
                                        if (response.isSuccessful) {
                                            Toast.makeText(
                                                this@LostReportActivity,
                                                "목격 신고가 성공적으로 등록되었습니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            finish()  // 액티비티 종료 - 이전 화면으로 돌아가기
                                        } else {
                                            val errorBody = response.errorBody()?.string()
                                            Log.e(
                                                "LostReportActivity",
                                                "Failed to submit sight report: $errorBody"
                                            )
                                            Toast.makeText(
                                                this@LostReportActivity,
                                                "신고 등록에 실패했습니다: $errorBody",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<LostReportResponse>,
                                        t: Throwable
                                    ) {
                                        Log.e("LostReportActivity", "Error: ${t.message}")
                                    }
                                })
                            }
                        } ?: run {
                            Toast.makeText(this, "이미지를 선택해 주세요.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("LostReportActivity", "File open failed: ${e.message}")
                        Toast.makeText(this, "파일을 여는 데 문제가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                } ?: run {
                    Toast.makeText(this, "이미지를 선택해 주세요.", Toast.LENGTH_SHORT).show() }
            }
        }

    }

    private fun showFullImage() {
        selectedImageUri?.let {
            val intent = Intent(this, FullScreenImageActivity::class.java)
            intent.putExtra("image_uri", it.toString())
            startActivity(intent)
        }
    }

    // 사용자 ID로 반려동물 목록 가져오기
    private fun fetchPets(userId: String) {
        retrofitService.getPetsByUserId(userId).enqueue(object : Callback<List<Pet>> {
            override fun onResponse(call: Call<List<Pet>>, response: Response<List<Pet>>) {
                if (response.isSuccessful) {
                    response.body()?.let { pets ->
                        displayPets(pets)
                    }
                } else {
                    showError("반려동물 정보를 가져오는 데 실패했습니다.")
                }
            }

            override fun onFailure(call: Call<List<Pet>>, t: Throwable) {
                showError("반려동물 정보를 가져오는 데 실패했습니다.")
            }
        })
    }

    private fun displayPets(pets: List<Pet>) {
        binding.petListSection.removeAllViews()

        if (pets.isNotEmpty()) {
            for (pet in pets) {
                val petName = TextView(this).apply {
                    text = pet.name
                    textSize = 16f
                    setTextColor(Color.BLACK)
                    setPadding(16, 16, 16, 16)
                    setOnClickListener {
                        if (selectedTextView == this) {
                            // 선택 해제 로직
                            selectedTextView?.setTextColor(Color.BLACK)
                            selectedTextView = null
                            selectedPet = null // 선택 해제 시 selectedPet을 null로 설정
                            clearPetDetails()
                            hideReportSection()
                        } else {
                            // 새로 선택된 반려동물에 대한 처리
                            selectedTextView?.setTextColor(Color.BLACK)
                            this.setTextColor(resources.getColor(R.color.deep_pink))
                            selectedTextView = this
                            selectedPet = pet // 선택된 Pet 객체를 저장
                            displayPetDetails(pet)
                            showReportSection()
                        }
                    }
                }
                binding.petListSection.addView(petName)
            }
        } else {
            showError("반려동물 정보가 없습니다.")
        }
    }


    private fun displayPetDetails(pet: Pet) {
        binding.lostReportTitle.setText(pet.name)
        binding.lostReportBreed.setText(pet.breed)
        binding.lostReportAge.setText(pet.age.toString())
        binding.lostReportGender.setText(pet.gender)
        binding.lostReportFeature.setText(pet.feature)
    }

    private fun clearPetDetails() {
        binding.lostReportTitle.setText("")
        binding.lostReportBreed.setText("")
        binding.lostReportAge.setText("")
        binding.lostReportGender.setText("")
        binding.lostReportFeature.setText("")
    }

    private fun showReportSection() {
        binding.lostReportSection.visibility = View.VISIBLE
        binding.reportButton.visibility = View.VISIBLE
    }

    private fun hideReportSection() {
        binding.lostReportSection.visibility = View.GONE
        binding.reportButton.visibility = View.GONE
    }

    // 오류 메시지 표시
    private fun showError(message: String) {
        binding.petListSection.removeAllViews()
        val errorTextView = TextView(this).apply {
            text = message
            textSize = 16f
        }
        binding.petListSection.addView(errorTextView)
    }

    // 토스트 메시지로 오류 표시
    private fun showErrorToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // SimpleDateFormat을 사용하여 날짜를 원하는 형식으로 포맷
                val selectedDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)

                // 포맷된 날짜를 TextView에 표시
                binding.lostReportDate.text = formattedDate
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow("_display_name"))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result ?: "unknown_file"
    }
}
