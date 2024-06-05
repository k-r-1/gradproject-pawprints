package com.swuproject.pawprints.ui.matching

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.swuproject.pawprints.common.Utils
import com.swuproject.pawprints.databinding.FragmentMatchingBinding
import com.swuproject.pawprints.network.LostReport
import com.swuproject.pawprints.network.RetrofitClient
import com.swuproject.pawprints.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.swuproject.pawprints.network.Pet
import com.swuproject.pawprints.network.SimilarSighting


class MatchingFragment : Fragment() {

    private var _binding: FragmentMatchingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var retrofitService: RetrofitService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        retrofitService = RetrofitClient.getRetrofitService()

        // Utils를 사용하여 SharedPreferences에서 사용자 ID 가져오기
        val userId = Utils.getUserId(requireContext())

        // 반려동물 목록 가져오기
        // userId가 null이 아닌 경우 text_name에 표시
        if (userId != null) {
            fetchPets(userId)
        } else {
        }

        // 매칭 버튼 클릭 이벤트 설정
        val matchingButton: Button = binding.matchingButton
        matchingButton.setOnClickListener {
            if (userId != null) {
                // 매칭 결과를 불러오는 로직 구현
                findSimilarSightings(userId)
            } else {
            }
        }

        return root
    }

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
            }
        })
    }

    private fun displayPets(pets: List<Pet>) {
        for (pet in pets) {
            val petView = TextView(requireContext()).apply {
                text = pet.name
                textSize = 16f
                setOnClickListener {
                    // 펫 클릭 시 실종 신고 정보 가져오기
                    fetchLostReport(pet.id)
                }
            }
            binding.petListSection.addView(petView)
        }
    }

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

    private fun displayLostReport(lostReport: LostReport) {
        binding.lostReportSection.visibility = View.VISIBLE
        binding.lostReportTitle.text = lostReport.title
        binding.lostReportLocation.text = lostReport.location
        binding.lostReportDate.text = lostReport.date
        binding.lostReportDescription.text = lostReport.description
        binding.lostReportContact.text = lostReport.contact
    }

    private fun findSimilarSightings(userId: String) {
        // 매칭 로직 구현
        val requestBody = mapOf("userId" to userId)
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