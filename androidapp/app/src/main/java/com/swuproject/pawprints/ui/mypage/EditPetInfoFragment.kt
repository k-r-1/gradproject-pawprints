package com.swuproject.pawprints.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.swuproject.pawprints.R

class EditPetInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 레이아웃을 인플레이트하고 View 객체를 반환합니다.
        val view = inflater.inflate(R.layout.fragment_edit_pet_info, container, false)

        // Spinner와 EditText 뷰를 초기화합니다.
        val petTypeSpinner: Spinner = view.findViewById(R.id.pet_type_spinner)
        val petBreedSpinner: Spinner = view.findViewById(R.id.pet_breed_spinner)
        val petBreedEditText: EditText = view.findViewById(R.id.pet_breed_edit_text)
        val resetBreedButton: Button = view.findViewById(R.id.reset_breed_button)
        val petGenderSpinner: Spinner = view.findViewById(R.id.pet_gender_spinner)
        val saveButton: Button = view.findViewById(R.id.save_button)

        // Spinner에 표시될 항목 배열을 정의합니다.
        val petTypes = arrayOf("클릭하여 종류를 선택해 주세요", "개", "고양이")
        val petGenders = arrayOf("클릭하여 성별을 선택해 주세요", "암컷", "수컷")

        // 개와 고양이 품종 배열을 정의하고 가나다 순으로 정렬합니다.
        val dogBreeds = arrayOf(
            "비글", "비숑 프리제", "보더 콜리", "보스턴 테리어", "불독",
            "치와와", "코커 스패니얼", "닥스훈트", "잉글리쉬 쉽독",
            "골든 리트리버", "이탈리안 그레이하운드", "진도", "래브라도 리트리버",
            "말티즈", "미니어처 핀셔", "파피용", "페키니즈", "포인터",
            "포메라니안", "푸들", "퍼그", "사모예드", "슈나우저", "셰퍼드",
            "셰틀랜드 쉽독", "시바 이누", "시추", "스피츠",
            "웰시 코기", "요크셔 테리어"
        ).sortedArray() + "기타"

        val catBreeds = arrayOf(
            "아비시니안", "아메리칸 쇼트헤어", "벵갈", "봄베이",
            "브리티시 쇼트헤어", "칼리코", "메인쿤", "먼치킨",
            "노르웨이 숲 고양이", "페르시안", "래그돌", "러시안 블루",
            "스코티시 폴드", "샴", "시베리안", "스핑크스", "턱시도", "태비", "터키시 앙고라"
        ).sortedArray() + "기타"

        // Spinner에 항목을 설정할 ArrayAdapter를 초기화합니다.
        val petTypeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, petTypes)
        petTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        petTypeSpinner.adapter = petTypeAdapter

        val petGenderAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, petGenders)
        petGenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        petGenderSpinner.adapter = petGenderAdapter

        // Spinner의 기본 선택값을 설정합니다.
        petTypeSpinner.setSelection(0)
        petGenderSpinner.setSelection(0)

        // petTypeSpinner의 아이템이 선택되었을 때의 동작을 정의합니다.
        petTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // 선택된 아이템을 문자열로 가져옵니다.
                val selectedPetType = parent.getItemAtPosition(position).toString()

                // 선택된 종류에 따라 품종 배열을 설정합니다.
                val breedAdapter = when (selectedPetType) {
                    // '개'가 선택된 경우 dogBreeds 배열을 어댑터로 설정합니다.
                    "개" -> ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dogBreeds)
                    // '고양이'가 선택된 경우 catBreeds 배열을 어댑터로 설정합니다.
                    "고양이" -> ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, catBreeds)
                    // 그 외의 경우 기본 안내 메시지를 어댑터로 설정합니다.
                    else -> ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayOf("먼저 종류를 선택해 주세요"))
                }

                // 드롭다운 뷰 리소스를 설정합니다.
                breedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // petBreedSpinner에 어댑터를 설정합니다.
                petBreedSpinner.adapter = breedAdapter
                // petBreedSpinner의 기본 선택값을 설정합니다.
                petBreedSpinner.setSelection(0)
                // "기타" 옵션이 선택되지 않은 경우 EditText를 숨기고 내용을 지웁니다.
                petBreedEditText.visibility = View.GONE
                resetBreedButton.visibility = View.GONE
                petBreedSpinner.visibility = View.VISIBLE
                petBreedEditText.text.clear()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // petBreedSpinner의 아이템이 선택되었을 때의 동작을 정의합니다.
        petBreedSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // "기타" 옵션이 선택된 경우 EditText를 표시하고 Spinner를 숨깁니다.
                if (parent.getItemAtPosition(position) == "기타") {
                    petBreedEditText.visibility = View.VISIBLE
                    resetBreedButton.visibility = View.VISIBLE
                    petBreedSpinner.visibility = View.GONE
                } else {
                    // "기타" 옵션이 아닌 경우 EditText를 숨깁니다.
                    petBreedEditText.visibility = View.GONE
                    resetBreedButton.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // 재설정 버튼 클릭 시 Spinner를 다시 표시하고 EditText를 숨깁니다.
        resetBreedButton.setOnClickListener {
            petBreedSpinner.visibility = View.VISIBLE
            petBreedEditText.visibility = View.GONE
            resetBreedButton.visibility = View.GONE
            petBreedSpinner.setSelection(0)
        }

        // 저장 버튼 클릭 시 유효성 검사를 수행합니다.
        saveButton.setOnClickListener {
            val selectedPetType = petTypeSpinner.selectedItemPosition
            val selectedPetBreed = petBreedSpinner.selectedItemPosition
            val selectedPetGender = petGenderSpinner.selectedItemPosition

            // 유효성 검사: 모든 항목이 선택되었는지 확인
            if (selectedPetType == 0 || (selectedPetBreed == 0 && petBreedEditText.visibility == View.GONE) || selectedPetGender == 0 || (petBreedEditText.visibility == View.VISIBLE && petBreedEditText.text.isEmpty())) {
                Toast.makeText(requireContext(), "모든 항목을 선택해 주세요.", Toast.LENGTH_SHORT).show()
            } else {
                // 폼 제출 로직: 예를 들어, 서버에 데이터 전송 또는 데이터베이스에 저장
                Toast.makeText(requireContext(), "정보가 저장되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}