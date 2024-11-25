package com.swuproject.pawprints.ui.mypage

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.swuproject.pawprints.R
import com.swuproject.pawprints.common.MapActivity
import com.swuproject.pawprints.databinding.FragmentEditLostReportBinding
import com.swuproject.pawprints.dto.LostReportEdit
import com.swuproject.pawprints.network.RetrofitClient
import com.swuproject.pawprints.network.RetrofitService
import com.swuproject.pawprints.dto.LostReportResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class EditLostReportFragment : Fragment(R.layout.fragment_edit_lost_report) {

    private lateinit var binding: FragmentEditLostReportBinding
    private var lostId: Int = 0 // 전달받은 lostId
    private lateinit var etTitle: EditText
    private lateinit var etBreed: TextView
    private lateinit var etGender: TextView
    private lateinit var etAge: TextView
    private lateinit var etAreaLat: TextView
    private lateinit var etAreaLng: TextView
    private lateinit var etDate: TextView
    private lateinit var etLocation: EditText
    private lateinit var etFeature: EditText
    private lateinit var etDescription:EditText
    private lateinit var etContact: EditText

    private var selectedImageUri: Uri? = null // 단일 이미지 URI



    private var selectedLat: Double? = null
    private var selectedLng: Double? = null

    // 기존 값 저장용 변수
    private var initialLat: Double? = null
    private var initialLng: Double? = null
    private var initialDate: String? = null

    private val retrofitService by lazy {
        RetrofitClient.getRetrofitService() // Retrofit 서버 사용
    }

    // 결과를 받아오는 ActivityResultLauncher 정의
    private val mapActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                // 반환된 주소 정보를 받아옴
                val selectedAddress = intent.getStringExtra("selected_address")
                selectedLat = intent.getDoubleExtra("selected_lat", 0.0)
                selectedLng = intent.getDoubleExtra("selected_lng", 0.0)

                selectedAddress?.let {
                    binding.tvEditLostrecyclerArea.text = it

                    // 위도, 경도를 로그와 토스트 메시지로 출력
                    Log.d("LostReportActivity", "Selected Latitude: $selectedLat")
                    Log.d("LostReportActivity", "Selected Longitude: $selectedLng")

                    Toast.makeText(requireContext(), "위도: $selectedLat, 경도: $selectedLng", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditLostReportBinding.bind(view)

        // Fragment로 전달받은 lostId를 전달받기
        lostId = arguments?.getInt("lostId") ?: 0
        Log.d("EditLostReportFragment", "Received lostId: $lostId")

        // 서버에서 데이터를 가져옴
        loadLostReport(lostId)

        etTitle = binding.etvEditLostrecyclerTitle
        etBreed = binding.tvEditLostrecyclerBreed
        etGender = binding.tvEditLostrecyclerGender
        etAge = binding.tvEditLostrecyclerAge
        etDate = binding.tvEditLostrecyclerDate
        etLocation = binding.tvEditLostrecyclerLocation
        etFeature = binding.tvEditLostrecyclerFeature
        etDescription = binding.tvEditLostrecyclerDescription
        etContact = binding.tvEditLostrecyclerContact

        // 날짜 선택 부분 클릭 시 DatePickerDialog 띄우기
        binding.tvEditLostrecyclerDate.setOnClickListener {
            showDatePickerDialog()
        }

        // 위치 설정 버튼 클릭 리스너
        binding.tvEditLostrecyclerAreaSelect.setOnClickListener {
            val intent = Intent(requireContext(), MapActivity::class.java)
            mapActivityResultLauncher.launch(intent)
        }

        binding.btnEditLostReport.setOnClickListener {
            val title = etTitle.text.toString()
            val date = etDate.text.toString()
            val location = etLocation.text.toString()
            val feature = etFeature.text.toString()
            val contact = etContact.text.toString()

            val latitude = selectedLat ?: initialLat ?: 0.0
            val longitude = selectedLng ?: initialLng ?: 0.0

            updateLostReport(lostId, title, date, location, feature, contact, latitude, longitude)
        }

        // 삭제 버튼 클릭 리스너
        binding.btnDeleteLostReport.setOnClickListener {
            val lostId = /* 해당 lostId 가져오기, 예: intent나 ViewModel에서 */

                AlertDialog.Builder(requireContext())
                    .setTitle("삭제 확인")
                    .setMessage("정말로 이 목격 신고를 삭제하시겠습니까?")
                    .setPositiveButton("삭제") { _, _ ->
                        // Retrofit 요청 실행
                        retrofitService.deleteLostReports(lostId).enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "삭제 완료", Toast.LENGTH_SHORT).show()
                                    requireActivity().onBackPressed() // 이전 화면으로
                                } else {
                                    Toast.makeText(context, "삭제 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                    .setNegativeButton("취소", null)
                    .show()
        }

    }
    private fun updateLostReport(
        lostId: Int, title: String,
        date: String, location: String, feature: String, contact: String,
        latitude: Double, longitude: Double
    ) {
        // DB 업데이트 작업 수행 (예시로 Repository 사용)
        val lostReportEdit = LostReportEdit(
            lostId = lostId,
            title = title,
            date = date,
            location = location,
            feature = feature,
            contact = contact,
            lostAreaLat = latitude,   // lat 값을 보냄
            lostAreaLng = longitude   // lng 값을 보냄
        )

        // RetrofitService를 통해 DB 업데이트 호출
        retrofitService.updateLostReport(lostId, lostReportEdit).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "목격 신고가 수정되었습니다.", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed() // 이전 화면으로
                } else {
                    Toast.makeText(requireContext(), "수정 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun loadLostReport(lostId: Int) {
        // Retrofit 비동기 호출
        retrofitService.getLostReportByLostId(lostId).enqueue(object : Callback<List<LostReportResponse>> {
            override fun onResponse(call: Call<List<LostReportResponse>>, response: Response<List<LostReportResponse>>) {
                if (response.isSuccessful) {
                    val lostReports = response.body()
                    Log.d("RetrofitDebug", "서버 응답 본문: ${response.body()}")
                    if (lostReports != null && lostReports.isNotEmpty()) {
                        // 여러 개의 보고서가 있을 수 있으므로 첫 번째 보고서를 사용
                        bindLostReportData(lostReports[0])
                    } else {
                        Log.e("RetrofitDebug", "Response body is null or empty")
                        Toast.makeText(requireContext(), "해당 lostId에 맞는 데이터가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // 응답 실패 시 더 상세한 오류 정보 출력
                    Log.e("RetrofitDebug", "Error: ${response.code()}, ${response.message()}")
                    Log.e("RetrofitDebug", "서버 응답 본문: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<LostReportResponse>>, t: Throwable) {
                // 네트워크 오류 등 요청 실패 시 상세 정보 출력
                Log.e("RetrofitDebug", "Request failed: ${t.message}", t)
                Toast.makeText(requireContext(), "서버와 연결에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun bindLostReportData(lostReport: LostReportResponse) {
        // 이미지 설정
        val firstImage = lostReport.lostImages.firstOrNull()?.lostImagePath
        if (firstImage != null) {
            val imageUrl = firstImage.replace("gs://", "https://storage.googleapis.com/")
            Glide.with(requireContext())
                .load(imageUrl)
                .into(binding.ivEditLostrecyclerPhoto)
        } else {
            binding.ivEditLostrecyclerPhoto.setImageResource(R.drawable.dog_sample2)
        }

        // 텍스트 필드 설정
        binding.etvEditLostrecyclerTitle.setText(lostReport.lostTitle)
        binding.tvEditLostrecyclerBreed.setText(lostReport.petBreed)
        binding.tvEditLostrecyclerGender.setText(lostReport.petGender)
        binding.tvEditLostrecyclerAge.setText(lostReport.petAge)

        // 위치 정보 설정
        val latitude = lostReport.lostAreaLat ?: 0.0
        val longitude = lostReport.lostAreaLng ?: 0.0

        CoroutineScope(Dispatchers.Main).launch {
            val address = withContext(Dispatchers.IO) {
                getAddressFromLatLng(latitude, longitude)
            }
            binding.tvEditLostrecyclerArea.text = address ?: "$latitude, $longitude"
        }

        // 날짜 설정
        binding.tvEditLostrecyclerDate.text = formatDate(lostReport.lostDate)

        // 나머지 정보 설정
        binding.tvEditLostrecyclerLocation.setText(lostReport.lostLocation)
        binding.tvEditLostrecyclerFeature.setText(lostReport.petFeature)
        binding.tvEditLostrecyclerDescription.setText(lostReport.lostDescription)
        binding.tvEditLostrecyclerContact.setText(
            if (lostReport.lostContact.isNullOrEmpty()) "연락처가 없습니다."
            else lostReport.lostContact)
    }

    // 위도와 경도를 주소로 변환하는 함수
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

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                binding.tvEditLostrecyclerDate.text = formattedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }
}
