package com.swuproject.pawprints.ui.home

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.cloud.storage.Storage
import com.swuproject.pawprints.R
import com.swuproject.pawprints.common.FullScreenImageActivity
import com.swuproject.pawprints.common.MapActivity
import com.swuproject.pawprints.common.Utils
import com.swuproject.pawprints.databinding.ActivitySightReportBinding
import com.swuproject.pawprints.dto.SightReportResponse
import com.swuproject.pawprints.network.RetrofitClient
import com.swuproject.pawprints.network.RetrofitService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SightReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySightReportBinding

    // Google Cloud Storage 변수 선언
    private lateinit var storage: Storage

    private var selectedImageUri: Uri? = null // 단일 이미지 URI

    private val dogBreeds = arrayOf(
        "비글", "비숑 프리제", "보더 콜리", "보스턴 테리어", "불독",
        "치와와", "코커 스패니얼", "닥스훈트", "잉글리쉬 쉽독",
        "골든 리트리버", "이탈리안 그레이하운드", "진도", "래브라도 리트리버",
        "말티즈", "미니어처 핀셔", "파피용", "페키니즈", "포인터",
        "포메라니안", "푸들", "퍼그", "사모예드", "슈나우저", "셰퍼드",
        "셰틀랜드 쉽독", "시바 이누", "시추", "스피츠",
        "웰시 코기", "요크셔 테리어"
    ).sortedArray() + "기타"

    private val catBreeds = arrayOf(
        "아비시니안", "아메리칸 쇼트헤어", "벵갈", "봄베이",
        "브리티시 쇼트헤어", "칼리코", "메인쿤", "먼치킨",
        "노르웨이 숲 고양이", "페르시안", "래그돌", "러시안 블루",
        "스코티시 폴드", "샴", "시베리안", "스핑크스", "턱시도", "태비", "터키시 앙고라"
    ).sortedArray() + "기타"

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            binding.imageSection.findViewById<ImageView>(R.id.selected_image).apply {
                setImageBitmap(bitmap)
                visibility = View.VISIBLE // 이미지를 이미지뷰에 표시
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
        binding = ActivitySightReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 기본 액션바 숨기기
        Utils.hideActionBar(this)

        // 상태 표시줄 색상 변경
        Utils.setStatusBarColor(this, R.color.primary_pink)

        // 뒤로 가기 아이콘에 클릭 리스너 설정
        binding.iconBack.setOnClickListener {
            onBackPressed()
        }

        // Spinner에 표시될 항목 배열을 정의합니다.
        val petTypes = arrayOf("클릭하여 종류를 선택해 주세요", "개", "고양이")

        // Spinner에 항목을 설정할 ArrayAdapter를 초기화합니다.
        val petTypeAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, petTypes)
        petTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Spinner에 Adapter를 설정합니다.
        binding.petTypeSpinner.adapter = petTypeAdapter

        binding.petTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedType = parent.getItemAtPosition(position) as String

                    val breedAdapter = when (selectedType) {
                        "개" -> ArrayAdapter(
                            this@SightReportActivity,
                            android.R.layout.simple_spinner_item,
                            dogBreeds
                        )
                        "고양이" -> ArrayAdapter(
                            this@SightReportActivity,
                            android.R.layout.simple_spinner_item,
                            catBreeds
                        )
                        else -> ArrayAdapter(
                            this@SightReportActivity,
                            android.R.layout.simple_spinner_item,
                            emptyArray<String>()
                        )
                    }

                    breedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.petBreedSpinner.adapter = breedAdapter
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // 선택되지 않은 경우 처리할 내용을 여기에 추가할 수 있습니다.
                }
            }

        // 이미지 업로드 버튼 클릭 리스너 설정
        binding.imageUploadButton.setOnClickListener {
            getImage.launch("image/*")
        }

        // 선택된 이미지를 전체 화면으로 보기
        binding.imageSection.findViewById<ImageView>(R.id.selected_image).setOnClickListener {
            selectedImageUri?.let { uri ->
                val intent = Intent(this@SightReportActivity, FullScreenImageActivity::class.java)
                intent.putExtra("image_uri", uri.toString()) // 선택된 이미지 URI를 전달
                startActivity(intent)
            }
        }

        // 날짜 선택 부분 클릭 시 DatePickerDialog 띄우기
        binding.petDateText.setOnClickListener {
            showDatePickerDialog()
        }

        // 위치 설정 버튼 클릭 리스너
        binding.petAreaSelect.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            mapActivityResultLauncher.launch(intent)
        }

        // saveButton 클릭 리스너
        binding.saveButton.setOnClickListener {
            if (binding.petNameEditText?.text.isNullOrEmpty() ||
                binding.petTypeSpinner.selectedItem.toString() == "클릭하여 종류를 선택해 주세요" ||
                binding.petBreedSpinner.selectedItem.toString().isEmpty() ||
                binding.petAreaText.text.isNullOrEmpty() ||
                binding.petDateText.text.isNullOrEmpty() ||
                binding.petLocationEditText.text.isNullOrEmpty() ||
                binding.petFeatureEditText.text.isNullOrEmpty() ||
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

                            val sightType = when (binding.petTypeSpinner.selectedItem.toString()) {
                                "개" -> "dog"
                                "고양이" -> "cat"
                                else -> binding.petTypeSpinner.selectedItem.toString()
                            }

                            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), it.readBytes())
                            val body = MultipartBody.Part.createFormData("file", fileName, requestFile)

                            val userIdRequest = RequestBody.create("text/plain".toMediaTypeOrNull(), Utils.getUserId(this).toString())
                            val sightTitleRequest = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.petNameEditText?.text.toString())
                            val sightTypeRequest = RequestBody.create("text/plain".toMediaTypeOrNull(), sightType)
                            val sightBreedRequest = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.petBreedSpinner.selectedItem.toString())
                            val sightAreaLatRequest = RequestBody.create("text/plain".toMediaTypeOrNull(), selectedLat?.toString() ?: "")
                            val sightAreaLngRequest = RequestBody.create("text/plain".toMediaTypeOrNull(), selectedLng?.toString() ?: "")
                            val sightDateRequest = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.petDateText.text.toString())
                            val sightLocationRequest = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.petLocationEditText.text.toString())
                            val sightDescriptionRequest = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.petFeatureEditText.text.toString())

                            val call = retrofitService.createSightReport(
                                body,
                                userIdRequest,
                                sightTitleRequest,
                                sightTypeRequest,
                                sightBreedRequest,
                                sightAreaLatRequest,
                                sightAreaLngRequest,
                                sightDateRequest,
                                sightLocationRequest,
                                sightDescriptionRequest
                            )

                            call.enqueue(object : Callback<SightReportResponse> {
                                override fun onResponse(call: Call<SightReportResponse>, response: Response<SightReportResponse>) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(this@SightReportActivity, "목격 신고가 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show()
                                        finish()  // 액티비티 종료 - 이전 화면으로 돌아가기
                                    } else {
                                        val errorBody = response.errorBody()?.string()
                                        Log.e("SightReportActivity", "Failed to submit sight report: $errorBody")
                                        Toast.makeText(this@SightReportActivity, "신고 등록에 실패했습니다: $errorBody", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<SightReportResponse>, t: Throwable) {
                                    Log.e("SightReportActivity", "Error: ${t.message}")
                                }
                            })
                        } ?: run {
                            Toast.makeText(this, "이미지를 선택해 주세요.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("SightReportActivity", "File open failed: ${e.message}")
                        Toast.makeText(this, "파일을 여는 데 문제가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                } ?: run {
                    Toast.makeText(this, "이미지를 선택해 주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    // 파일 이름 가져오는 함수
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

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                binding.petDateText.text = formattedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }
}
