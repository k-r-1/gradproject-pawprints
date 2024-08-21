package com.swuproject.pawprints.common

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.swuproject.pawprints.R
import com.swuproject.pawprints.databinding.ActivityMapBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private var selectedLatLng: LatLng? = null
    private var selectedAddress: String? = null
    private val markers = mutableListOf<Marker>() // 추가된 마커 관리

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 기본 액션바 숨기기
        Utils.hideActionBar(this)

        // 상태 표시줄 색상 변경
        Utils.setStatusBarColor(this, R.color.primary_pink)

        // Initialize Naver Map
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)

        // Initialize FusedLocationSource
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        // 장소 선택 버튼 클릭 리스너
        binding.selectLocationButton.setOnClickListener {
            selectedLatLng?.let {
                returnSelectedLocation(it)
            }
        }

        // 주소 검색 버튼 클릭 리스너
        binding.searchButton.setOnClickListener {
            val address = binding.addressInput.text.toString()
            if (address.isNotEmpty()) {
                searchLocationByAddress(address)
            } else {
                Toast.makeText(this, "주소를 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource

        // 현재 위치 버튼 활성화
        naverMap.uiSettings.isLocationButtonEnabled = true

        // 위치 권한 체크 및 현재 위치 설정
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setInitialLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        // 지도 클릭 이벤트
        naverMap.setOnMapClickListener { _, latLng ->
            addMarker(latLng)
            getAddressFromLatLng(latLng)
        }

        // 위치 변경 리스너 설정
        naverMap.addOnLocationChangeListener { location ->
            val initialPosition = LatLng(location.latitude, location.longitude)
            val cameraUpdate = CameraUpdate.scrollTo(initialPosition)
            naverMap.moveCamera(cameraUpdate)
        }
    }

    private fun setInitialLocation() {
        // 위치 추적 모드 설정
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        val location = locationSource.lastLocation
        if (location != null) {
            val initialPosition = LatLng(location.latitude, location.longitude)
            val cameraUpdate = CameraUpdate.scrollTo(initialPosition)
            naverMap.moveCamera(cameraUpdate)
        } else {
            // 위치가 아직 설정되지 않았기 때문에 토스트를 띄우지 않고 기다립니다.
        }
    }

    private fun addMarker(latLng: LatLng) {
        selectedLatLng = latLng
        markers.forEach { it.map = null } // 기존 마커 제거
        markers.clear()

        val marker = Marker()
        marker.position = latLng
        marker.map = naverMap
        markers.add(marker) // 새 마커 추가
    }

    private fun getAddressFromLatLng(latLng: LatLng) {
        val geocoder = Geocoder(this, Locale.getDefault())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    val address = addresses[0].getAddressLine(0)
                    withContext(Dispatchers.Main) {
                        selectedAddress = address
                        binding.addressTextView.text = address
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MapActivity, "주소를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun searchLocationByAddress(address: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val addresses = geocoder.getFromLocationName(address, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    val location = addresses[0]
                    val latLng = LatLng(location.latitude, location.longitude)
                    withContext(Dispatchers.Main) {
                        addMarker(latLng)
                        val cameraUpdate = CameraUpdate.scrollTo(latLng)
                        naverMap.moveCamera(cameraUpdate)
                        selectedAddress = address
                        binding.addressTextView.text = address
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MapActivity, "해당 주소를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MapActivity, "주소 검색 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun returnSelectedLocation(latLng: LatLng) {
        val returnIntent = Intent().apply {
            putExtra("selected_lat", latLng.latitude)
            putExtra("selected_lng", latLng.longitude)
            putExtra("selected_address", selectedAddress)
        }
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setInitialLocation() // 권한이 부여되면 위치 설정
            } else {
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
