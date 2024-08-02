package com.swuproject.pawprints.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.LocationTrackingMode
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val rootView: View = binding.root

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.map_sightRecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)

        fetchSightReports { sightReports ->
            val adapter = SightRecyclerAdapter(sightReports, requireContext())
            recyclerView.adapter = adapter
        }

        // Initialize Naver Map
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        mapFragment.getMapAsync(this)

        // Initialize FusedLocationSource
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

        // Set location tracking mode
        naverMap.locationTrackingMode = LocationTrackingMode.Face
    }

    // Handle permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
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
