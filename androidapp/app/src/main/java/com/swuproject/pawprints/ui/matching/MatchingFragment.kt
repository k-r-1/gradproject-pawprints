package com.swuproject.pawprints.ui.matching

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.swuproject.pawprints.R
import com.swuproject.pawprints.common.Utils
import com.swuproject.pawprints.databinding.FragmentMatchingBinding
import com.swuproject.pawprints.network.LostReport
import com.swuproject.pawprints.network.RetrofitClient
import com.swuproject.pawprints.network.RetrofitService
import com.swuproject.pawprints.network.Pet
import com.swuproject.pawprints.network.SimilarSighting
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MatchingFragment : Fragment() {

    private var _binding: FragmentMatchingBinding? = null
    private val binding get() = _binding!!
    private lateinit var retrofitService: RetrofitService
    private var selectedPetName: String? = null // 선택된 반려동물 이름 저장

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchingBinding.inflate(inflater, container, false)
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

        // 매칭 버튼 클릭 이벤트 설정
        val matchingButton: Button = binding.matchingButton
        matchingButton.setOnClickListener {
            if (userId != null && selectedPetName != null) {
                // 매칭 결과를 불러오는 로직 구현
                findSimilarSightings(userId, selectedPetName!!)
            }
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
                        fetchLostReport(pet.id)
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

    // 반려동물 ID로 실종 신고 정보 가져오기
    private fun fetchLostReport(petId: Int) {
        retrofitService.getLostReportByPetId(petId).enqueue(object : Callback<LostReport> {
            override fun onResponse(call: Call<LostReport>, response: Response<LostReport>) {
                if (response.isSuccessful) {
                    response.body()?.let { lostReport ->
                        displayLostReport(lostReport)
                    }
                }
            }

            override fun onFailure(call: Call<LostReport>, t: Throwable) {
                // 오류 처리
            }
        })
    }

    // 실종 신고 정보를 화면에 표시
    private fun displayLostReport(lostReport: LostReport) {
        binding.lostReportSection.visibility = View.VISIBLE
        binding.lostReportTitle.text = lostReport.title
        binding.lostReportLocation.text = lostReport.location
        binding.lostReportDate.text = lostReport.date
        binding.lostReportDescription.text = lostReport.description
        binding.lostReportContact.text = lostReport.contact
    }

    // 유사한 목격 사례 찾기 요청
    private fun findSimilarSightings(userId: String, petName: String) {
        val requestBody = mapOf("userId" to userId, "pet_name" to petName)
        RetrofitClient.getFlaskRetrofitService().findSimilarSightings(requestBody).enqueue(object : Callback<List<SimilarSighting>> {
            override fun onResponse(call: Call<List<SimilarSighting>>, response: Response<List<SimilarSighting>>) {
                if (response.isSuccessful) {
                    response.body()?.let { similarSightings ->
                        displaySimilarSightings(similarSightings)
                    }
                }
            }

            override fun onFailure(call: Call<List<SimilarSighting>>, t: Throwable) {
                // 오류 처리
            }
        })
    }

    // 유사한 목격 사례를 화면에 표시
    private fun displaySimilarSightings(similarSightings: List<SimilarSighting>) {
        binding.similarSightingsSection.removeAllViews()
        for (sighting in similarSightings) {
            val sightingView = TextView(requireContext()).apply {
                text = sighting.description
                textSize = 16f
            }
            binding.similarSightingsSection.addView(sightingView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
