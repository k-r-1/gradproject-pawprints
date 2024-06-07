package com.swuproject.pawprints.ui.mypage

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
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
    private var selectedPetName: String? = null // 선택된 반려동물 이름 저장

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPetInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        retrofitService = RetrofitClient.getRetrofitService()

        // SharedPreferences에서 사용자 ID 가져오기
        val userId = Utils.getUserId(requireContext())

        // 반려동물 목록 가져오기
        if (userId != null) {
            binding.petListSection.removeAllViews()
            val loadingTextView = TextView(requireContext()).apply {
                text = "반려동물 정보를 가져오는 중입니다..."
                textSize = 16f
            }
            binding.petListSection.addView(loadingTextView)
            fetchPets(userId)
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

    // 반려동물 목록을 화면에 버튼으로 표시
    private fun displayPets(pets: List<Pet>) {
        binding.petListSection.removeAllViews() // 기존에 추가된 뷰들을 모두 제거

        if (pets.isNotEmpty()) { // 반려동물 목록이 비어있지 않은 경우
            val petNames = StringBuilder()

            for ((index, pet) in pets.withIndex()) {
                val petName = TextView(requireContext()).apply {
                    text = pet.name
                    textSize = 16f
                    setTextColor(Color.BLACK)
                    setPadding(16, 16, 16, 16)
                    setOnClickListener {
                        selectedPetName = pet.name
                    }
                }

                binding.petListSection.addView(petName)

                if (index < pets.size - 1) {
                    val separator = TextView(requireContext()).apply {
                        text = " | "
                        textSize = 16f
                        setTextColor(Color.BLACK)
                    }
                    binding.petListSection.addView(separator)
                    petNames.append(pet.name).append(" | ")
                } else {
                    petNames.append(pet.name)
                }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Spinner에 표시될 항목 배열을 정의합니다.
        val petTypes = arrayOf("클릭하여 종류를 선택해주세요", "개", "고양이")
        val petGenders = arrayOf("클릭하여 성별을 선택해주세요", "암컷", "수컷")

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
        binding.petTypeSpinner.adapter = petTypeAdapter

        val petGenderAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, petGenders)
        petGenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.petGenderSpinner.adapter = petGenderAdapter

        // Spinner의 기본 선택값을 설정합니다.
        binding.petTypeSpinner.setSelection(0)
        binding.petGenderSpinner.setSelection(0)

        // petTypeSpinner의 아이템이 선택되었을 때의 동작을 정의합니다.
        binding.petTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
        binding.petBreedSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
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

        // 저장 버튼 클릭 시 유효성 검사를 수행합니다.
        binding.saveButton.setOnClickListener {
            val selectedPetType = binding.petTypeSpinner.selectedItemPosition
            val selectedPetBreed = binding.petBreedSpinner.selectedItemPosition
            val selectedPetGender = binding.petGenderSpinner.selectedItemPosition

            // 유효성 검사: 모든 항목이 선택되었는지 확인
            if (selectedPetType == 0 || (selectedPetBreed == 0 && binding.petBreedEditText.visibility == View.GONE) || selectedPetGender == 0 || (binding.petBreedEditText.visibility == View.VISIBLE && binding.petBreedEditText.text.isEmpty())) {
                Toast.makeText(requireContext(), "모든 항목을 선택해 주세요.", Toast.LENGTH_SHORT).show()
            } else {
                // 폼 제출 로직: 예를 들어, 서버에 데이터 전송 또는 데이터베이스에 저장
                Toast.makeText(requireContext(), "정보가 저장되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}