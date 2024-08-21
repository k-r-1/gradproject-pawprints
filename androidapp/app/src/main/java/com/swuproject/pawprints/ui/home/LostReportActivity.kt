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
import com.swuproject.pawprints.R
import com.swuproject.pawprints.common.FullScreenImageActivity
import com.swuproject.pawprints.common.MapActivity
import com.swuproject.pawprints.common.Utils
import com.swuproject.pawprints.databinding.ActivityLostReportBinding
import com.swuproject.pawprints.network.Pet
import com.swuproject.pawprints.network.RetrofitClient
import com.swuproject.pawprints.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class LostReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLostReportBinding
    private lateinit var retrofitService: RetrofitService
    private var selectedPetName: String? = null // 선택된 반려동물 이름 저장
    private var selectedTextView: TextView? = null // 선택된 텍스트뷰 저장
    private var selectedImageUri: Uri? = null

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

    // 결과를 받아오는 ActivityResultLauncher 정의
    private val mapActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                // 반환된 주소 정보를 받아옴
                val selectedAddress = intent.getStringExtra("selected_address")
                val selectedLat = intent.getDoubleExtra("selected_lat", 0.0)
                val selectedLng = intent.getDoubleExtra("selected_lng", 0.0)

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

        retrofitService = RetrofitClient.getRetrofitService()

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
        binding.petListSection.removeAllViews() // 기존에 추가된 뷰들을 모두 제거

        if (pets.isNotEmpty()) { // 반려동물 목록이 비어있지 않은 경우
            for (pet in pets) {
                val petName = TextView(this).apply {
                    text = pet.name
                    textSize = 16f
                    setTextColor(Color.BLACK)
                    setPadding(16, 16, 16, 16)
                    setOnClickListener {
                        if (selectedTextView == this) {
                            // 두 번 클릭 시 선택 해제
                            selectedTextView?.setTextColor(Color.BLACK)
                            selectedTextView = null
                            clearPetDetails()
                            hideReportSection()
                        } else {
                            // 이전에 선택된 TextView의 색상을 원래대로 돌림
                            selectedTextView?.setTextColor(Color.BLACK)
                            // 현재 선택된 TextView의 색상을 변경
                            this.setTextColor(resources.getColor(R.color.deep_pink))
                            // 현재 선택된 TextView를 저장
                            selectedTextView = this
                            displayPetDetails(pet)
                            showReportSection()
                        }
                    }
                }
                binding.petListSection.addView(petName)
            }
        } else { // 반려동물 목록이 비어있는 경우
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
}
