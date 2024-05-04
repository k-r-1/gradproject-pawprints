package com.swuproject.pawprints.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import com.swuproject.pawprints.R

class EditPetInfoFragment : Fragment() {
    private lateinit var petNameEditText: EditText
    private lateinit var petTypeSpinner: Spinner
    private lateinit var petGenderSpinner: Spinner
    private lateinit var petAgeSpinner: Spinner
    private lateinit var petFeatureEditText: EditText
    private lateinit var petImageView: ImageView
    private lateinit var saveButton: Button
    private lateinit var petListSection: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_pet_info, container, false)

        // 뷰 초기화
        petNameEditText = view.findViewById(R.id.pet_name_edit_text)
        petTypeSpinner = view.findViewById(R.id.pet_type_spinner)
        petGenderSpinner = view.findViewById(R.id.pet_gender_spinner)
        petAgeSpinner = view.findViewById(R.id.pet_age_spinner)
        petFeatureEditText = view.findViewById(R.id.pet_feature_edit_text)
        petImageView = view.findViewById(R.id.pet_image)
        saveButton = view.findViewById(R.id.save_button)
        petListSection = view.findViewById(R.id.pet_list_section)

        // 스피너 설정
        setupSpinners()

        // 저장 버튼 클릭 이벤트 처리
        saveButton.setOnClickListener {
            val petInfo = getPetInfo()
            addPetInfoToList(petInfo)
        }

        // 예시 데이터 추가
        addPetInfoToList(PetInfo("몽구", "개", "수컷", "3살", "활발함"))
        addPetInfoToList(PetInfo("야옹이", "고양이", "암컷", "2살", "조용함"))

        return view
    }

    private fun setupSpinners() {
        val petTypes = arrayOf("개", "고양이", "기타")
        val petGenders = arrayOf("수컷", "암컷")
        val petAges = arrayOf("1살", "2살", "3살", "4살", "5살 이상")

        petTypeSpinner.adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_layout, petTypes).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        petGenderSpinner.adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_layout, petGenders).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        petAgeSpinner.adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_layout, petAges).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun getPetInfo(): PetInfo {
        val petName = petNameEditText.text.toString()
        val petType = petTypeSpinner.selectedItem.toString()
        val petGender = petGenderSpinner.selectedItem.toString()
        val petAge = petAgeSpinner.selectedItem.toString()
        val petFeature = petFeatureEditText.text.toString()

        return PetInfo(petName, petType, petGender, petAge, petFeature)
    }

    private fun addPetInfoToList(petInfo: PetInfo) {
        val petNameTextView = LayoutInflater.from(context).inflate(R.layout.layout_pet_info, null) as TextView
        petNameTextView.text = petInfo.petName

        petListSection.addView(petNameTextView)
    }

    data class PetInfo(
        val petName: String,
        val petType: String,
        val petGender: String,
        val petAge: String,
        val petFeature: String
    ) {
        override fun toString(): String {
            return "$petName, $petType, $petGender, $petAge, $petFeature"
        }
    }
}