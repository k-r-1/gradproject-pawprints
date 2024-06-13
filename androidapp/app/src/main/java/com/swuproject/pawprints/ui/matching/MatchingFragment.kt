package com.swuproject.pawprints.ui.matching

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.swuproject.pawprints.R
import com.swuproject.pawprints.common.Utils
import com.swuproject.pawprints.databinding.FragmentMatchingBinding
import com.swuproject.pawprints.dto.LostReportResponse
import com.swuproject.pawprints.network.LostReports
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
    private var selectedTextView: TextView? = null // 선택된 텍스트뷰 저장

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
            fetchLostPets(userId) // 실종된 반려동물만 가져오기
        }

        // 매칭 버튼 클릭 이벤트 설정
        binding.matchingButton.setOnClickListener {
            if (userId != null && selectedPetName != null) {
                binding.progressBar.visibility = View.VISIBLE // 로딩 시작
                findSimilarSightings(userId, selectedPetName!!)
            }
        }

        return root
    }

    // 사용자 ID로 실종된 반려동물 목록 가져오기
    private fun fetchLostPets(userId: String) {
        retrofitService.getLostPetsByUserId(userId).enqueue(object : Callback<List<Pet>> {
            override fun onResponse(call: Call<List<Pet>>, response: Response<List<Pet>>) {
                if (response.isSuccessful) {
                    response.body()?.let { pets ->
                        displayLostPets(pets)
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

    // 실종된 반려동물 목록을 화면에 표시
    private fun displayLostPets(pets: List<Pet>) {
        binding.petListSection.removeAllViews() // 기존에 추가된 뷰들을 모두 제거

        if (pets.isNotEmpty()) { // 반려동물 목록이 비어있지 않은 경우
            for (pet in pets) {
                val petName = TextView(requireContext()).apply {
                    text = pet.name
                    textSize = 16f
                    setTextColor(Color.BLACK)
                    setPadding(16, 16, 16, 16)
                    setOnClickListener {
                        // 이미 선택된 반려동물을 다시 클릭하면 선택 해제
                        if (selectedPetName == pet.name) {
                            selectedPetName = null
                            setTextColor(Color.BLACK)
                            selectedTextView = null
                            // 실종 신고 정보 초기화
                            clearLostReportInfo()
                            binding.matchingButton.visibility = View.GONE // 매칭 버튼 숨기기
                        } else {
                            selectedPetName = pet.name
                            setTextColor(resources.getColor(R.color.deep_pink))
                            selectedTextView?.setTextColor(Color.BLACK) // 이전 선택 해제
                            selectedTextView = this
                            fetchLostReport(pet.id)
                            binding.matchingButton.visibility = View.VISIBLE // 매칭 버튼 보이기
                        }
                    }
                }
                binding.petListSection.addView(petName)
            }
        } else { // 반려동물 목록이 비어있는 경우
            showError("실종된 반려동물 정보가 없습니다.")
        }
    }

    // 실종 신고 정보 초기화 메서드 추가
    private fun clearLostReportInfo() {
        binding.petImage.setImageResource(R.drawable.dog_sample2) // 기본 이미지 설정
        binding.lostReportSection.visibility = View.GONE // 실종 신고 섹션 숨기기
        binding.lostReportTitle.text = ""
        binding.lostReportBreed.text = ""
        binding.lostReportGender.text = ""
        binding.lostReportAge.text = ""
        binding.lostReportArea.text = ""
        binding.lostReportDate.text = ""
        binding.lostReportFeature.text = ""
        binding.lostReportLocation.text = ""
        binding.lostReportDescription.text = ""
        binding.lostReportContact.text = ""
    }


    // 오류 메시지 표시
    private fun showError(message: String) {
        binding.petListSection.removeAllViews()
        val errorTextView = TextView(requireContext()).apply {
            text = message
            textSize = 16f
        }
        binding.petListSection.addView(errorTextView)
    }

    // 반려동물 ID로 실종 신고 정보 가져오기
    private fun fetchLostReport(petId: Int) {
        retrofitService.getLostReportByPetId(petId).enqueue(object : Callback<LostReportResponse> { // 여기를 LostReportResponse로 변경
            override fun onResponse(call: Call<LostReportResponse>, response: Response<LostReportResponse>) { // 여기도 변경
                if (response.isSuccessful) {
                    response.body()?.let { lostReport ->
                        displayLostReport(lostReport)
                    }
                } else {
                    showErrorToast("실종 신고 정보를 가져오는 데 실패했습니다.")
                }
            }

            override fun onFailure(call: Call<LostReportResponse>, t: Throwable) { // 여기도 변경
                showErrorToast("실종 신고 정보를 가져오는 데 실패했습니다.")
            }
        })
    }

    // 토스트 메시지로 오류 표시
    private fun showErrorToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    // 실종 신고 정보를 화면에 표시
    private fun displayLostReport(lostReportResponse: LostReportResponse) {
        val firstImage = lostReportResponse.lostImages.firstOrNull()?.lostImagePath
        if (firstImage != null) {
            // gs:// URL을 https://storage.googleapis.com/로 변환
            val imageUrl = firstImage.replace("gs://", "https://storage.googleapis.com/")
            // Glide를 사용해 이미지 로드
            Glide.with(requireContext()).load(imageUrl).into(binding.petImage)
        } else {
            binding.petImage.setImageResource(R.drawable.dog_sample2) // 기본 이미지
        }
        binding.lostReportSection.visibility = View.VISIBLE
        binding.lostReportTitle.text = lostReportResponse.lostTitle
        binding.lostReportBreed.text = lostReportResponse.petBreed
        binding.lostReportGender.text = lostReportResponse.petGender
        binding.lostReportAge.text = lostReportResponse.petAge.toString()
        binding.lostReportArea.text = "${lostReportResponse.lostAreaLat.toString()}, ${lostReportResponse.lostAreaLng.toString()}"

        // 날짜 포맷 변경
        binding.lostReportDate.text = formatDate(lostReportResponse.lostDate)

        binding.lostReportFeature.text = lostReportResponse.petFeature
        binding.lostReportLocation.text = lostReportResponse.lostLocation
        binding.lostReportDescription.text = lostReportResponse.lostDescription
        binding.lostReportContact.text = lostReportResponse.lostContact
    }

    // 날짜 포맷 변경 함수
    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return ""

        // 받은 날짜 형식
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        // 원하는 출력 형식
        val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())

        return try {
            val date: Date = inputFormat.parse(dateString) ?: return dateString
            outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            dateString // 변환 실패 시 원본 문자열 반환
        }
    }

    private fun findSimilarSightings(userId: String, petName: String) {
        val requestBody = mapOf(
            "user_id" to userId,
            "pet_name" to petName
        )

        Log.d("MatchingFragment", "Request Body: $requestBody")

        val flaskRetrofitService = RetrofitClient.getFlaskRetrofitService()
        flaskRetrofitService.findSimilarSightings(requestBody).enqueue(object : Callback<List<SimilarSighting>> {
            override fun onResponse(call: Call<List<SimilarSighting>>, response: Response<List<SimilarSighting>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    response.body()?.let { similarSightings ->
                        val intent = Intent(requireContext(), MatchingResultActivity::class.java)
                        intent.putExtra("similarSightings", ArrayList(similarSightings))
                        startActivity(intent)
                    }
                } else {
                    showErrorToast("유사한 목격 사례를 찾는 데 실패했습니다.")
                    Log.e("MatchingFragment", "findSimilarSightings onResponse: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<SimilarSighting>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                showErrorToast("유사한 목격 사례를 찾는 데 실패했습니다.")
                Log.e("MatchingFragment", "findSimilarSightings onFailure: ${t.message}", t)
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
