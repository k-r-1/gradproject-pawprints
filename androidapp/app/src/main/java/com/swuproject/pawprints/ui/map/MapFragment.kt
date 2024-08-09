package com.swuproject.pawprints.ui.map

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.overlay.Marker
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.util.MarkerIcons
import com.swuproject.pawprints.R
import com.swuproject.pawprints.databinding.FragmentMapBinding
import com.swuproject.pawprints.dto.SightReportResponse
import com.swuproject.pawprints.network.RetrofitClient
import com.swuproject.pawprints.ui.home.SightRecyclerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        // BottomSheet의 초기 상태 설정
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = (resources.displayMetrics.heightPixels * 0.05).toInt()

        // BottomSheet의 최대 높이 설정
        bottomSheet.post {
            bottomSheetBehavior.isFitToContents = false
            bottomSheetBehavior.halfExpandedRatio = 0.65f
        }

        // Handle 부분을 포함한 FrameLayout의 터치 리스너 설정
        val handleContainer = binding.bottomSheet.findViewById<FrameLayout>(R.id.handle_container)
        handleContainer.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Handle 부분 터치 시 BottomSheet의 드래그를 허용하도록 설정
                    bottomSheetBehavior.isDraggable = true
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    // 터치가 움직일 때도 BottomSheet의 드래그를 허용
                    bottomSheetBehavior.isDraggable = true
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // 터치가 끝나면 BottomSheet의 드래그를 허용하도록 설정
                    bottomSheetBehavior.isDraggable = true
                    false
                }
                else -> false
            }
        }

        // RecyclerView의 터치 리스너 설정
        recyclerView.setOnTouchListener { _, event ->
            // RecyclerView가 터치되면 BottomSheet의 드래그를 비활성화
            bottomSheetBehavior.isDraggable = false
            // RecyclerView의 터치 이벤트를 소비하지 않고 계속 전파
            false
        }

        // BottomSheetBehavior의 상태를 감지하여 상한 설정
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    // BottomSheet가 FULLY_EXPANDED로 가지 않도록 방지
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // 추가적인 스크롤 처리가 필요할 수 있음
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

        // 현재 위치 버튼
        naverMap.uiSettings.isLocationButtonEnabled = true

        // 위치 추적 모드 설정
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
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) {
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
