package com.swuproject.pawprints.ui.home

import android.app.Activity
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
import com.swuproject.pawprints.R
import com.swuproject.pawprints.common.FullScreenImageActivity
import com.swuproject.pawprints.common.MapActivity
import com.swuproject.pawprints.common.Utils
import com.swuproject.pawprints.databinding.ActivitySightReportBinding

class SightReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySightReportBinding

    private var selectedImageUri: Uri? = null

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
        binding.petTypeSpinner.adapter = petTypeAdapter

        // Spinner의 기본 선택값을 설정합니다.
        binding.petTypeSpinner.setSelection(0)

        // petTypeSpinner의 아이템이 선택되었을 때의 동작을 정의합니다.
        binding.petTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // 선택된 아이템을 문자열로 가져옵니다.
                    val selectedPetType = parent.getItemAtPosition(position).toString()

                    // 선택된 종류에 따라 품종 배열을 설정합니다.
                    val breedAdapter = when (selectedPetType) {
                        // '개'가 선택된 경우 dogBreeds 배열을 어댑터로 설정합니다.
                        "개" -> ArrayAdapter(
                            this@SightReportActivity,
                            android.R.layout.simple_spinner_item,
                            dogBreeds
                        )
                        // '고양이'가 선택된 경우 catBreeds 배열을 어댑터로 설정합니다.
                        "고양이" -> ArrayAdapter(
                            this@SightReportActivity,
                            android.R.layout.simple_spinner_item,
                            catBreeds
                        )
                        // 그 외의 경우 기본 안내 메시지를 어댑터로 설정합니다.
                        else -> ArrayAdapter(
                            this@SightReportActivity,
                            android.R.layout.simple_spinner_item,
                            arrayOf("먼저 종류를 선택해 주세요")
                        )
                    }

                    // 드롭다운 뷰 리소스를 설정합니다.
                    breedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    // petBreedSpinner에 어댑터를 설정합니다.
                    binding.petBreedSpinner.adapter = breedAdapter
                    // petBreedSpinner의 기본 선택값을 설정합니다.
                    binding.petBreedSpinner.setSelection(0)
                    // "기타" 옵션이 선택되지 않은 경우 EditText를 숨기고 내용을 지웁니다.
                    binding.petBreedEditText.visibility = View.GONE
                    binding.resetBreedButton.visibility = View.GONE
                    binding.petBreedSpinner.visibility = View.VISIBLE
                    binding.petBreedEditText.text.clear()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        // petBreedSpinner의 아이템이 선택되었을 때의 동작을 정의합니다.
        binding.petBreedSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // "기타" 옵션이 선택된 경우 EditText를 표시하고 Spinner를 숨깁니다.
                    if (parent.getItemAtPosition(position) == "기타") {
                        binding.petBreedEditText.visibility = View.VISIBLE
                        binding.resetBreedButton.visibility = View.VISIBLE
                        binding.petBreedSpinner.visibility = View.GONE
                    } else {
                        // "기타" 옵션이 아닌 경우 EditText를 숨깁니다.
                        binding.petBreedEditText.visibility = View.GONE
                        binding.resetBreedButton.visibility = View.GONE
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        // 재설정 버튼 클릭 시 Spinner를 다시 표시하고 EditText를 숨깁니다.
        binding.resetBreedButton.setOnClickListener {
            binding.petBreedSpinner.visibility = View.VISIBLE
            binding.petBreedEditText.visibility = View.GONE
            binding.resetBreedButton.visibility = View.GONE
            binding.petBreedSpinner.setSelection(0)
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

    }

    private fun showFullImage() {
        selectedImageUri?.let {
            val intent = Intent(this, FullScreenImageActivity::class.java)
            intent.putExtra("image_uri", it.toString())
            startActivity(intent)
        }
    }
}
