package com.swuproject.pawprints.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.swuproject.pawprints.R
import com.swuproject.pawprints.dto.SightReportResponse
import com.swuproject.pawprints.network.RetrofitClient
import com.swuproject.pawprints.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationTrackingService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var retrofitService: RetrofitService
    private val maxDistance = 1.0 // km 단위

    private val notifiedReports = mutableSetOf<Int>() // 알림 보낸 신고 ID 저장

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        retrofitService = RetrofitClient.getRetrofitService()

        startForegroundService()
        startLocationUpdates()
    }

    private fun startForegroundService() {
        val channelId = "location_tracking_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "위치 기반 알림",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("위치 기반 알림 활성화")
            .setContentText("현재 위치를 기반으로 목격 신고를 확인 중입니다.")
            .setSmallIcon(R.drawable.icon_logo_removebg)
            .build()

        startForeground(1, notification)
    }

    private fun startLocationUpdates() {
        // 권한 확인
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            stopSelf() // 권한 없으면 서비스 중지
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10 * 60 * 1000 // 10분마다 위치 업데이트
        ).build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            mainLooper
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            if (location != null) {
                checkNearbySightReports(location.latitude, location.longitude)
            }
        }
    }

    private fun checkNearbySightReports(userLat: Double, userLng: Double) {
        retrofitService.getNearbySightReports(userLat, userLng, maxDistance)
            .enqueue(object : Callback<List<SightReportResponse>> {
                override fun onResponse(call: Call<List<SightReportResponse>>, response: Response<List<SightReportResponse>>) {
                    if (response.isSuccessful) {
                        val nearbyReports = response.body() ?: emptyList()
                        for (report in nearbyReports) {
                            if (notifiedReports.contains(report.sightId).not()) {
                                sendNotification(report)
                                notifiedReports.add(report.sightId)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<List<SightReportResponse>>, t: Throwable) {
                    // 네트워크 오류 처리
                }
            })
    }

    private fun sendNotification(report: SightReportResponse) {
        val channelId = "sight_report_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "목격 신고 알림",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.icon_logo_removebg)
            .setContentTitle("목격 신고 발견!")
            .setContentText("${report.sightTitle} 근처에서 발견되었습니다.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(report.sightId, notification) // 신고 ID로 고유 알림
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
