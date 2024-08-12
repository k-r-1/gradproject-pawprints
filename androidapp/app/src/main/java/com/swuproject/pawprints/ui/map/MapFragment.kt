package com.swuproject.pawprints.ui.map

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons
import com.swuproject.pawprints.R
import com.swuproject.pawprints.databinding.FragmentMapBinding
import com.swuproject.pawprints.dto.SightReportResponse
import com.swuproject.pawprints.network.RetrofitClient
import com.swuproject.pawprints.ui.home.SightRecyclerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val rootView: View = binding.root

        // RecyclerView 설정
        val recyclerView = binding.mapSightRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(context)

        fetchSightReports { sightReports ->
            val adapter = SightRecyclerAdapter(sightReports, requireContext())
            recyclerView.adapter = adapter
            addMarkers(sightReports)
        }

        // BottomSheetBehavior 설정
        val bottomSheet = binding.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = (resources.displayMetrics.heightPixels * 0.05).toInt()

        bottomSheet.post {
            bottomSheetBehavior.isFitToContents = false
            bottomSheetBehavior.halfExpandedRatio = 0.65f
        }

        val handleContainer = binding.bottomSheet.findViewById<FrameLayout>(R.id.handle_container)
        handleContainer.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    bottomSheetBehavior.isDraggable = true
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    bottomSheetBehavior.isDraggable = true
                    false
                }
                else -> false
            }
        }

        recyclerView.setOnTouchListener { _, _ ->
            bottomSheetBehavior.isDraggable = false
            false
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // 필요 시 구현
            }
        })

        // 지도 Fragment 초기화
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        mapFragment.getMapAsync(this)

        // 위치 소스 초기화
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        return rootView
    }

    private fun fetchSightReports(callback: (List<SightReportResponse>) -> Unit) {
        val retrofitService = RetrofitClient.getRetrofitService()
        retrofitService.getSightReports().enqueue(object : Callback<List<SightReportResponse>> {
            override fun onResponse(
                call: Call<List<SightReportResponse>>,
                response: Response<List<SightReportResponse>>
            ) {
                if (response.isSuccessful) {
                    callback(response.body() ?: emptyList())
                }
            }

            override fun onFailure(call: Call<List<SightReportResponse>>, t: Throwable) {
                // 실패 처리
            }
        })
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource

        naverMap.uiSettings.isLocationButtonEnabled = true
        naverMap.locationTrackingMode = LocationTrackingMode.Face
    }

    private fun addMarkers(sightReports: List<SightReportResponse>) {
        if (::naverMap.isInitialized) {
            for (report in sightReports) {
                val lat = report.sightAreaLat
                val lng = report.sightAreaLng
                if (lat != null && lng != null) {
                    val marker = Marker()
                    marker.icon = MarkerIcons.BLACK
                    marker.iconTintColor = ContextCompat.getColor(requireContext(), R.color.light_pink)
                    marker.position = LatLng(lat, lng)
                    marker.width = 75
                    marker.height = 100
                    marker.captionText = report.sightBreed.toString()
                    marker.captionHaloColor = ContextCompat.getColor(requireContext(), R.color.background_gray)
                    marker.map = naverMap

                    // 마커 클릭 리스너 설정
                    marker.setOnClickListener {
                        showSightReportPopup(report)
                        true
                    }
                }
            }
        }
    }

    private fun showSightReportPopup(report: SightReportResponse) {
        // 팝업 레이아웃을 inflate
        val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_sight_report, null)

        // 팝업 창의 내용을 설정
        val sightTitleTextView = popupView.findViewById<TextView>(R.id.tv_hometab_sightrecycler_title)
        val sightBreedTextView = popupView.findViewById<TextView>(R.id.tv_hometab_sightrecycler_breed)
        val sightAreaTextView = popupView.findViewById<TextView>(R.id.tv_hometab_sightrecycler_area)
        val sightDateTextView = popupView.findViewById<TextView>(R.id.tv_hometab_sightrecycler_date)
        val sightLocationTextView = popupView.findViewById<TextView>(R.id.tv_hometab_sightrecycler_location)
        val sightDescriptionTextView = popupView.findViewById<TextView>(R.id.tv_hometab_sightrecycler_feature)
        val sightImageView = popupView.findViewById<ImageView>(R.id.iv_hometab_sightrecycler_photo)

        // 데이터 설정
        sightTitleTextView.text = report.sightTitle ?: "제목 정보 없음"
        sightBreedTextView.text = report.sightBreed ?: "견종 정보 없음"
        sightAreaTextView.text = "${report.sightAreaLat}, ${report.sightAreaLng}"
        sightDateTextView.text = formatDate(report.sightDate)
        sightLocationTextView.text = report.sightLocation ?: "장소 정보 없음"
        sightDescriptionTextView.text = report.sightDescription ?: "설명 없음"

        // 첫 번째 이미지를 설정
        val firstImage = report.sightImages.firstOrNull()?.sightImagePath
        if (firstImage != null) {
            // gs:// URL을 https://storage.googleapis.com/로 변환
            val imageUrl = firstImage.replace("gs://", "https://storage.googleapis.com/")
            // Glide를 사용해 이미지 로드
            Glide.with(requireContext()).load(imageUrl).into(sightImageView)
        } else {
            sightImageView.setImageResource(R.drawable.dog_sample2) // 기본 이미지
        }

        // 팝업 창의 닫기 버튼 설정
        val closeButton = popupView.findViewById<ImageView>(R.id.close_button)

        // 팝업 윈도우 설정
        val popupWindow = PopupWindow(popupView,
            ViewGroup.LayoutParams.MATCH_PARENT, // 너비
            ViewGroup.LayoutParams.WRAP_CONTENT, // 높이
            true)

        // 닫기 버튼 클릭 시 팝업 윈도우 닫기
        closeButton.setOnClickListener {
            popupWindow.dismiss()
        }

        // 팝업 크기 조정
        popupWindow.width = (resources.displayMetrics.widthPixels * 0.8).toInt() // 너비를 80%로 설정
        popupWindow.height = ViewGroup.LayoutParams.WRAP_CONTENT // 높이는 내용에 맞춤

        // 팝업을 화면 중앙에 위치시키기 위한 설정
        popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)
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

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
