package com.swuproject.pawprints.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.swuproject.pawprints.R
import com.swuproject.pawprints.common.Utils
import com.swuproject.pawprints.databinding.ActivityFilterBinding

class FilterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFilterBinding
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

    private var selectedBreed: String? = null
    private var lastCheckedId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 기본 액션바 숨기기
        Utils.hideActionBar(this)

        // 상태 표시줄 색상 변경
        Utils.setStatusBarColor(this, R.color.primary_pink)

        // 뒤로가기 아이콘 클릭 시
        binding.icBack.setOnClickListener {
            finish() // Activity 종료
        }

        binding.btnResetFilter.setOnClickListener {
            // 필터 초기화
            binding.rgPetType.clearCheck() // RadioButton 선택 해제
            binding.spinnerBreed.visibility = View.GONE // Spinner 숨기기
            binding.spinnerBreed.adapter = null // Spinner 초기화
            binding.etAreaSearch.text.clear() // 지역 검색 입력 필드 초기화
            selectedBreed = null // 선택된 품종 초기화
            binding.tvSelectedBreed.text = "선택된 품종: 없음"
        }

        // 라디오 버튼 선택 및 선택 해제 처리
        binding.rgPetType.setOnCheckedChangeListener { group, checkedId ->
            // 선택 해제하려면 기존 선택된 버튼과 동일한 ID인지 확인
            if (lastCheckedId == checkedId) {
                group.clearCheck()
                binding.spinnerBreed.visibility = View.GONE
                lastCheckedId = null // 선택 해제 후 ID 초기화
            } else {
                // 새로운 버튼 선택 시 스피너 설정
                when (checkedId) {
                    R.id.rb_dog -> {
                        setBreedSpinner(dogBreeds)
                        binding.spinnerBreed.visibility = View.VISIBLE
                    }
                    R.id.rb_cat -> {
                        setBreedSpinner(catBreeds)
                        binding.spinnerBreed.visibility = View.VISIBLE
                    }
                    else -> binding.spinnerBreed.visibility = View.GONE
                }
                lastCheckedId = checkedId // 선택된 ID 업데이트
            }
        }

        // 스피너에서 선택된 품종 텍스트뷰에 표시
        binding.spinnerBreed.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedBreed = parent.getItemAtPosition(position).toString()
                binding.tvSelectedBreed.text = "선택된 품종: $selectedBreed"
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedBreed = null
                binding.tvSelectedBreed.text = "선택된 품종: 없음"
            }
        }

        // 필터 적용 버튼 클릭 시
        binding.btnApplyFilter.setOnClickListener {
            val searchArea = binding.etAreaSearch.text.toString()
            val resultIntent = Intent().apply {
                putExtra("breedType", selectedBreed)
                putExtra("searchArea", searchArea)
            }
            setResult(RESULT_OK, resultIntent)
            finish() // 필터 화면 종료
        }
    }

    // 품종 스피너 설정 메서드
    private fun setBreedSpinner(breeds: Array<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, breeds)
        binding.spinnerBreed.adapter = adapter
    }
}
