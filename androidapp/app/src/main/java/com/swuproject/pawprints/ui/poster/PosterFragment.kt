package com.swuproject.pawprints.ui.poster

import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.swuproject.pawprints.R
import com.swuproject.pawprints.common.Utils
import com.swuproject.pawprints.databinding.FragmentPosterBinding
import com.swuproject.pawprints.dto.LostReportResponse
import com.swuproject.pawprints.network.Pet
import com.swuproject.pawprints.network.RetrofitClient
import com.swuproject.pawprints.network.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PosterFragment : Fragment() {

    private var _binding: FragmentPosterBinding? = null
    private val binding get() = _binding!!
    private lateinit var retrofitService: RetrofitService
    private var selectedPetName: String? = null // 선택된 반려동물 이름 저장
    private var selectedTextView: TextView? = null // 선택된 텍스트뷰 저장

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPosterBinding.inflate(inflater, container, false)
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
        binding.createPosterButton.setOnClickListener {
            if (userId != null && selectedPetName != null) {
                binding.progressBar.visibility = View.VISIBLE // 로딩 시작
//                findSimilarSightings(userId, selectedPetName!!)
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
                            binding.createPosterButton.visibility = View.GONE // 매칭 버튼 숨기기
                        } else {
                            selectedPetName = pet.name
                            setTextColor(resources.getColor(R.color.deep_pink))
                            selectedTextView?.setTextColor(Color.BLACK) // 이전 선택 해제
                            selectedTextView = this
                            fetchLostReport(pet.id)
                            binding.createPosterButton.visibility = View.VISIBLE // 매칭 버튼 보이기
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
        binding.posterSection.visibility = View.GONE // 실종 신고 섹션 숨기기
        binding.lostReportTitle.setText("")
        binding.lostReportBreed.setText("")
        binding.lostReportGender.setText("")
        binding.lostReportAge.setText("")
        binding.lostReportArea.setText("")
        binding.lostReportDate.setText("")
        binding.lostReportFeature.setText("")
        binding.lostReportLocation.setText("")
        binding.lostReportDescription.setText("")
        binding.lostReportContact.setText("")
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
        binding.posterSection.visibility = View.VISIBLE
        binding.lostReportTitle.setText(lostReportResponse.lostTitle) // EditText는 setText() 사용
        binding.lostReportBreed.setText(lostReportResponse.petBreed) // EditText는 setText() 사용
        binding.lostReportGender.setText(lostReportResponse.petGender) // EditText는 setText() 사용
        binding.lostReportAge.setText(lostReportResponse.petAge.toString()) // EditText는 setText() 사용

        // Nullable 타입을 처리하여 주소 변환
        val latitude = lostReportResponse.lostAreaLat ?: 0.0
        val longitude = lostReportResponse.lostAreaLng ?: 0.0

        CoroutineScope(Dispatchers.Main).launch {
            val address = withContext(Dispatchers.IO) {
                getAddressFromLatLng(latitude, longitude)
            }
            binding.lostReportArea.setText(address ?: "$latitude, $longitude")
        }

        // 날짜 포맷 변경
        binding.lostReportDate.setText(formatDate(lostReportResponse.lostDate)) // EditText는 setText() 사용

        binding.lostReportFeature.setText(lostReportResponse.petFeature) // EditText는 setText() 사용
        binding.lostReportLocation.setText(lostReportResponse.lostLocation) // EditText는 setText() 사용
        binding.lostReportDescription.setText(lostReportResponse.lostDescription) // EditText는 setText() 사용
        binding.lostReportContact.setText(lostReportResponse.lostContact) // EditText는 setText() 사용
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

    private fun getAddressFromLatLng(latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                addresses[0].getAddressLine(0) // 전체 주소 반환
            } else {
                null // 주소를 찾지 못한 경우
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null // 예외 발생 시 null 반환
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
