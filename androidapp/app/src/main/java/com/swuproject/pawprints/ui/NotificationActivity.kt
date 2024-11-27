package com.swuproject.pawprints.ui

import android.os.Bundle
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.LocationServices
import com.swuproject.pawprints.R
import com.swuproject.pawprints.common.Utils
import com.swuproject.pawprints.dto.SightReportResponse
import com.swuproject.pawprints.network.RetrofitClient
import com.swuproject.pawprints.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationActivity : AppCompatActivity() {

    private lateinit var retrofitService: RetrofitService
    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private val maxDistance = 1.0 // km 단위

    private val notificationList = mutableListOf<SightReportResponse>() // 알림 내역 저장
    private lateinit var notificationAdapter: NotificationAdapter // RecyclerView 어댑터

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // 기본 액션바 숨기기
        Utils.hideActionBar(this)

        // 상태 표시줄 색상 변경
        Utils.setStatusBarColor(this, R.color.primary_pink)

        // RecyclerView 초기화
        val recyclerView: RecyclerView = findViewById(R.id.notification_recycler_view)
        notificationAdapter = NotificationAdapter(notificationList)
        recyclerView.adapter = notificationAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // RetrofitService 초기화
        retrofitService = RetrofitClient.getRetrofitService()

        // 알림 스위치 설정
        val notificationSwitch: Switch = findViewById(R.id.notification_switch)
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkNearbySightReports()
            }
        }
    }

    private fun checkNearbySightReports() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userLat = location.latitude
                val userLng = location.longitude

                retrofitService.getNearbySightReports(userLat, userLng, maxDistance)
                    .enqueue(object : Callback<List<SightReportResponse>> {
                        override fun onResponse(call: Call<List<SightReportResponse>>, response: Response<List<SightReportResponse>>) {
                            if (response.isSuccessful) {
                                val nearbyReports = response.body() ?: emptyList()
                                if (nearbyReports.isNotEmpty()) {
                                    notificationList.clear() // 기존 데이터 초기화
                                    notificationList.addAll(nearbyReports) // 새 데이터 추가
                                    notificationAdapter.notifyDataSetChanged() // RecyclerView 갱신
                                    sendNotification(nearbyReports.size)
                                } else {
                                    Toast.makeText(
                                        this@NotificationActivity,
                                        "근처에 목격된 신고가 없습니다.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    this@NotificationActivity,
                                    "API 호출 실패: ${response.code()} ${response.message()}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<List<SightReportResponse>>, t: Throwable) {
                            Toast.makeText(
                                this@NotificationActivity,
                                "API 호출 오류: ${t.localizedMessage}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
            } else {
                Toast.makeText(
                    this@NotificationActivity,
                    "위치 정보를 가져올 수 없습니다. 위치 권한을 확인하세요.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(
                this@NotificationActivity,
                "위치 가져오기 실패: ${exception.localizedMessage}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun sendNotification(reportCount: Int) {
        val notificationId = 1
        val channelId = "sight_report_channel"

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelName = "목격 신고 알림"
            val channelDescription = "근처에서 발생한 목격 신고에 대한 알림"
            val importance = android.app.NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = android.app.NotificationChannel(channelId, channelName, importance)
            notificationChannel.description = channelDescription

            val notificationManager = getSystemService(android.app.NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentTitle("목격 신고 알림")
            .setContentText("근처에 목격된 신고가 ${reportCount}건 있습니다.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(this).notify(notificationId, notification)
    }
}

