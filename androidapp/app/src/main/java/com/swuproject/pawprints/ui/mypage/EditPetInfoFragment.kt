package com.swuproject.pawprints.ui.mypage

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.swuproject.pawprints.R
import com.swuproject.pawprints.common.Utils
import com.swuproject.pawprints.databinding.FragmentEditPetInfoBinding
import com.swuproject.pawprints.network.Pet
import com.swuproject.pawprints.network.RetrofitClient
import com.swuproject.pawprints.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditPetInfoFragment : Fragment() {

    private var _binding: FragmentEditPetInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var retrofitService: RetrofitService
    private var selectedPet: Pet? = null // 선택된 반려동물 정보 저장
    private var selectedTextView: TextView? = null // 선택된 반려동물 이름 TextView 저장
    private var userId: String? = null // 사용자 ID 저장

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPetInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        retrofitService = RetrofitClient.getRetrofitService()

        // SharedPreferences에서 사용자 ID 가져오기
        userId = Utils.getUserId(requireContext())

        // 반려동물 목록 가져오기
        if (userId != null) {
            binding.petListSection.removeAllViews()
            val loadingTextView = TextView(requireContext()).apply {
                text = "반려동물 정보를 가져오는 중입니다..."
                textSize = 16f
            }
            binding.petListSection.addView(loadingTextView)
            fetchPets(userId!!)
        }

        return root
    }

    // 사용자 ID로 반려동물 목록 가져오기
    private fun fetchPets(userId: String) {
        retrofitService.getPetsByUserId(userId).enqueue(object : Callback<List<Pet>> {
            override fun onResponse(call: Call<List<Pet>>, response: Response<List<Pet>>) {
                if (response.isSuccessful) {
                    response.body()?.let { pets ->
                        displayPets(pets)
                    }
                }
            }

            override fun onFailure(call: Call<List<Pet>>, t: Throwable) {
                // 오류 처리
                binding.petListSection.removeAllViews()
                val errorTextView = TextView(requireContext()).apply {
                    text = "반려동물 정보를 가져오는 데 실패했습니다."
                    textSize = 16f
                }
                binding.petListSection.addView(errorTextView)
            }
        })
    }

    private fun displayPets(pets: List<Pet>) {
        binding.petListSection.removeAllViews() // 기존에 추가된 뷰들을 모두 제거

        if (pets.isNotEmpty()) { // 반려동물 목록이 비어있지 않은 경우
            for (pet in pets) {
                val petName = TextView(requireContext()).apply {
                    text = pet.name
                    textSize = 16f
                    setTextColor(Color.BLACK)
                    setPadding(16, 16, 16, 16)
                    setOnClickListener {
                        if (selectedTextView == this) {
                            // 두 번 클릭 시 선택 해제
                            selectedTextView?.setTextColor(Color.BLACK)
                            selectedTextView = null
                            selectedPet = null
                            binding.deleteButton.visibility = View.GONE
                            binding.editButton.visibility = View.GONE
                            binding.updateButton.visibility = View.GONE
                            binding.saveButton.visibility = View.GONE
                            clearPetDetails()
                            resetEditMode()
                        } else {
                            selectedPet = pet
                            displayPetDetails(pet)

                            // 이전에 선택된 TextView의 색상을 원래대로 돌림
                            selectedTextView?.setTextColor(Color.BLACK)
                            // 현재 선택된 TextView의 색상을 변경
                            this.setTextColor(resources.getColor(R.color.deep_pink))
                            // 현재 선택된 TextView를 저장
                            selectedTextView = this
                            binding.deleteButton.visibility = View.VISIBLE // 삭제 버튼 보이기
                            binding.editButton.visibility = View.VISIBLE // 수정 버튼 보이기
                            binding.saveButton.visibility = View.GONE // 저장 버튼 숨기기
                        }
                    }
                }
                binding.petListSection.addView(petName)
            }
        } else { // 반려동물 목록이 비어있는 경우
            val noDataTextView = TextView(requireContext()).apply {
                text = "반려동물 정보가 없습니다."
                textSize = 16f
                setTextColor(Color.BLACK)
                setPadding(16, 16, 16, 16)
            }
            binding.petListSection.addView(noDataTextView)
        }
    }

    private fun displayPetDetails(pet: Pet) {
        resetEditMode()

        binding.petNameTextView.text = pet.name
        binding.petAgeTextView.text = pet.age.toString()
        binding.petColorTextView.text = pet.color
        binding.petFeatureTextView.text = pet.feature

        // 종류와 성별 텍스트 설정
        binding.petTypeTextView.text = when (pet.type) {
            "Dog" -> "개"
            "Cat" -> "고양이"
            else -> pet.type
        }
        binding.petGenderTextView.text = pet.gender

        // 품종 설정
        if (getIndex(binding.petBreedSpinner, pet.breed) == -1) {
            // 품종이 스피너 항목에 없는 경우
            binding.petBreedTextView.text = pet.breed
            binding.petBreedSpinner.visibility = View.GONE
            binding.petBreedEditText.visibility = View.GONE
            binding.resetBreedButton.visibility = View.GONE
        } else {
            // 품종이 스피너 항목에 있는 경우
            binding.petBreedTextView.text = pet.breed
            binding.petBreedSpinner.visibility = View.GONE
            binding.petBreedEditText.visibility = View.GONE
            binding.resetBreedButton.visibility = View.GONE
        }
    }

    private fun clearPetDetails() {
        binding.petNameTextView.text = ""
        binding.petTypeTextView.text = ""
        binding.petBreedTextView.text = ""
        binding.petAgeTextView.text = ""
        binding.petGenderTextView.text = ""
        binding.petColorTextView.text = ""
        binding.petFeatureTextView.text = ""
    }

    private fun resetEditMode() {
        binding.petNameEditText.visibility = View.GONE
        binding.petTypeSpinner.visibility = View.GONE
        binding.petBreedSpinner.visibility = View.GONE
        binding.petBreedEditText.visibility = View.GONE
        binding.petAgeEditText.visibility = View.GONE
        binding.petGenderSpinner.visibility = View.GONE
        binding.petColorEditText.visibility = View.GONE
        binding.petFeatureEditText.visibility = View.GONE
        binding.resetBreedButton.visibility = View.GONE

        binding.petNameTextView.visibility = View.VISIBLE
        binding.petTypeTextView.visibility = View.VISIBLE
        binding.petBreedTextView.visibility = View.VISIBLE
        binding.petAgeTextView.visibility = View.VISIBLE
        binding.petGenderTextView.visibility = View.VISIBLE
        binding.petColorTextView.visibility = View.VISIBLE
        binding.petFeatureTextView.visibility = View.VISIBLE
    }

    private fun getPetTypeIndex(petType: String): Int {
        return when (petType) {
            "Dog" -> 1 // '개'는 인덱스 1
            "Cat" -> 2 // '고양이'는 인덱스 2
            else -> 0 // 기본값
        }
    }

    private fun getIndex(spinner: Spinner, myString: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(myString, ignoreCase = true)) {
                return i
            }
        }
        return -1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Navigate back to MypageFragment
                    findNavController().navigate(R.id.navigation_mypage)
                }
            })

        // Spinner에 표시될 항목 배열을 정의합니다.
        val petTypes = arrayOf("클릭하여 종류를 선택해주세요", "개", "고양이")
        val petGenders = arrayOf("클릭하여 성별을 선택해주세요", "암컷", "수컷")

        // Spinner에 항목을 설정할 ArrayAdapter를 초기화합니다.
        val petTypeAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, petTypes)
        petTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.petTypeSpinner.adapter = petTypeAdapter

        val petGenderAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, petGenders)
        petGenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.petGenderSpinner.adapter = petGenderAdapter

        // Spinner의 기본 선택값을 설정합니다.
        binding.petTypeSpinner.setSelection(0)
        binding.petGenderSpinner.setSelection(0)

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
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            dogBreeds
                        )
                        // '고양이'가 선택된 경우 catBreeds 배열을 어댑터로 설정합니다.
                        "고양이" -> ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            catBreeds
                        )
                        // 그 외의 경우 기본 안내 메시지를 어댑터로 설정합니다.
                        else -> ArrayAdapter(
                            requireContext(),
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

        // 수정 버튼 클릭 시 EditText와 Spinner를 나타나게 함
        binding.editButton.setOnClickListener {
            if (selectedPet != null) {
                binding.petNameEditText.visibility = View.VISIBLE
                binding.petTypeSpinner.visibility = View.VISIBLE
                binding.petBreedSpinner.visibility = View.VISIBLE
                binding.petAgeEditText.visibility = View.VISIBLE
                binding.petGenderSpinner.visibility = View.VISIBLE
                binding.petColorEditText.visibility = View.VISIBLE
                binding.petFeatureEditText.visibility = View.VISIBLE

                // 기존의 TextView는 숨기기
                binding.petNameTextView.visibility = View.GONE
                binding.petTypeTextView.visibility = View.GONE
                binding.petBreedTextView.visibility = View.GONE
                binding.petAgeTextView.visibility = View.GONE
                binding.petGenderTextView.visibility = View.GONE
                binding.petColorTextView.visibility = View.GONE
                binding.petFeatureTextView.visibility = View.GONE

                // 선택된 반려동물의 정보로 EditText와 Spinner를 설정
                binding.petNameEditText.setText(selectedPet?.name)
                binding.petAgeEditText.setText(selectedPet?.age.toString())
                binding.petColorEditText.setText(selectedPet?.color)
                binding.petFeatureEditText.setText(selectedPet?.feature)
                binding.petTypeSpinner.setSelection(getPetTypeIndex(selectedPet?.type ?: ""))
                binding.petGenderSpinner.setSelection(
                    when (selectedPet?.gender) {
                        "암컷" -> 1
                        "수컷" -> 2
                        else -> 0
                    }
                )

                if (selectedPet?.type == "Dog") {
                    binding.petBreedSpinner.adapter =
                        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dogBreeds)
                } else if (selectedPet?.type == "Cat") {
                    binding.petBreedSpinner.adapter =
                        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, catBreeds)
                }
                binding.petBreedSpinner.setSelection(getIndex(binding.petBreedSpinner, selectedPet?.breed ?: ""))

                binding.updateButton.visibility = View.VISIBLE // 업데이트 버튼 보이기
                binding.saveButton.visibility = View.GONE // 저장 버튼 숨기기
                binding.editButton.visibility = View.GONE // 수정 버튼 숨기기
            }
        }

        // 업데이트 버튼 클릭 시 반려동물 정보 수정
        binding.updateButton.setOnClickListener {
            if (selectedPet != null) {
                if (allFieldsFilled()) {
                    AlertDialog.Builder(requireContext())
                        .setMessage("수정하시겠습니까?")
                        .setPositiveButton("예") { dialog, which ->
                            // EditText와 Spinner의 값을 가져와서 반려동물 정보 수정
                            val updatedPet = selectedPet!!.copy(
                                name = binding.petNameEditText.text.toString(),
                                age = binding.petAgeEditText.text.toString().toInt(),
                                color = binding.petColorEditText.text.toString(),
                                feature = binding.petFeatureEditText.text.toString(),
                                type = if (binding.petTypeSpinner.selectedItem.toString() == "개") "Dog" else "Cat",
                                gender = binding.petGenderSpinner.selectedItem.toString(),
                                breed = if (binding.petBreedEditText.visibility == View.VISIBLE)
                                    binding.petBreedEditText.text.toString()
                                else
                                    binding.petBreedSpinner.selectedItem.toString()
                            )

                            retrofitService.updatePet(updatedPet.id, updatedPet).enqueue(object : Callback<Void> {
                                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(requireContext(), "반려동물 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show()
                                        fetchPets(userId!!)
                                        binding.updateButton.visibility = View.GONE // 업데이트 버튼 숨기기
                                        binding.editButton.visibility = View.VISIBLE // 수정 버튼 보이기
                                        resetEditMode()
                                    }
                                }

                                override fun onFailure(call: Call<Void>, t: Throwable) {
                                    Toast.makeText(requireContext(), "반려동물 정보 수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                        .setNegativeButton("아니오", null)
                        .show()
                } else {
                    AlertDialog.Builder(requireContext())
                        .setMessage("모든 필드를 채워주세요.")
                        .setPositiveButton("확인", null)
                        .show()
                }
            }
        }

        // 새로운 반려동물 등록 버튼 클릭 시 EditText와 Spinner를 나타나게 함
        binding.addButton.setOnClickListener {
            clearPetDetails()
            resetEditMode()

            // EditText 초기화
            binding.petNameEditText.setText("")
            binding.petAgeEditText.setText("")
            binding.petColorEditText.setText("")
            binding.petFeatureEditText.setText("")

            // Spinner 초기화
            binding.petTypeSpinner.setSelection(0)
            binding.petGenderSpinner.setSelection(0)
            binding.petBreedSpinner.setSelection(0)

            binding.petNameEditText.visibility = View.VISIBLE
            binding.petTypeSpinner.visibility = View.VISIBLE
            binding.petBreedSpinner.visibility = View.VISIBLE
            binding.petAgeEditText.visibility = View.VISIBLE
            binding.petGenderSpinner.visibility = View.VISIBLE
            binding.petColorEditText.visibility = View.VISIBLE
            binding.petFeatureEditText.visibility = View.VISIBLE

            // 기존의 TextView는 숨기기
            binding.petNameTextView.visibility = View.GONE
            binding.petTypeTextView.visibility = View.GONE
            binding.petBreedTextView.visibility = View.GONE
            binding.petAgeTextView.visibility = View.GONE
            binding.petGenderTextView.visibility = View.GONE
            binding.petColorTextView.visibility = View.GONE
            binding.petFeatureTextView.visibility = View.GONE

            binding.saveButton.visibility = View.VISIBLE // 저장 버튼 보이기
            binding.updateButton.visibility = View.GONE // 업데이트 버튼 숨기기
            binding.deleteButton.visibility = View.GONE // 삭제 버튼 숨기기
            binding.editButton.visibility = View.GONE // 수정 버튼 숨기기

            // 이전에 선택된 반려동물 선택 해제
            selectedTextView?.setTextColor(Color.BLACK)
            selectedTextView = null
            selectedPet = null
        }

        // 저장 버튼 클릭 시 새로운 반려동물 등록
        binding.saveButton.setOnClickListener {
            if (allFieldsFilled()) {
                AlertDialog.Builder(requireContext())
                    .setMessage("등록하시겠습니까?")
                    .setPositiveButton("예") { dialog, which ->
                        val newPet = Pet(
                            id = 0,
                            userId = userId!!,
                            name = binding.petNameEditText.text.toString(),
                            age = binding.petAgeEditText.text.toString().toInt(),
                            color = binding.petColorEditText.text.toString(),
                            feature = binding.petFeatureEditText.text.toString(),
                            type = if (binding.petTypeSpinner.selectedItem.toString() == "개") "Dog" else "Cat",
                            gender = binding.petGenderSpinner.selectedItem.toString(),
                            breed = if (binding.petBreedEditText.visibility == View.VISIBLE)
                                binding.petBreedEditText.text.toString()
                            else
                                binding.petBreedSpinner.selectedItem.toString()
                        )

                        retrofitService.addPet(newPet).enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(requireContext(), "새로운 반려동물이 등록되었습니다.", Toast.LENGTH_SHORT).show()
                                    fetchPets(userId!!)
                                    binding.saveButton.visibility = View.GONE // 저장 버튼 숨기기
                                    binding.editButton.visibility = View.VISIBLE // 수정 버튼 보이기
                                    resetEditMode()
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Toast.makeText(requireContext(), "반려동물 등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                    .setNegativeButton("아니오", null)
                    .show()
            } else {
                AlertDialog.Builder(requireContext())
                    .setMessage("모든 필드를 채워주세요.")
                    .setPositiveButton("확인", null)
                    .show()
            }
        }

        // 삭제 버튼 클릭 시 반려동물 삭제
        binding.deleteButton.setOnClickListener {
            if (selectedPet != null) {
                AlertDialog.Builder(requireContext())
                    .setMessage("삭제하시겠습니까?")
                    .setPositiveButton("예") { dialog, which ->
                        retrofitService.deletePet(selectedPet!!.id).enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(requireContext(), "반려동물이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                                    fetchPets(userId!!)
                                    clearPetDetails()
                                    binding.deleteButton.visibility = View.GONE // 삭제 버튼 숨기기
                                    binding.editButton.visibility = View.GONE // 수정 버튼 숨기기
                                    binding.updateButton.visibility = View.GONE // 업데이트 버튼 숨기기
                                    binding.editButton.visibility = View.VISIBLE // 수정 버튼 보이기
                                    resetEditMode()
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Toast.makeText(requireContext(), "반려동물 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                    .setNegativeButton("아니오", null)
                    .show()
            }
        }
    }

    private fun allFieldsFilled(): Boolean {
        val isOtherBreedSelected = binding.petBreedSpinner.selectedItem.toString() == "기타"

        return binding.petNameEditText.text.isNotEmpty() &&
                binding.petTypeSpinner.selectedItemPosition != 0 &&
                (!isOtherBreedSelected || binding.petBreedEditText.text.isNotEmpty()) &&
                binding.petAgeEditText.text.isNotEmpty() &&
                binding.petGenderSpinner.selectedItemPosition != 0 &&
                binding.petColorEditText.text.isNotEmpty() &&
                binding.petFeatureEditText.text.isNotEmpty()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
