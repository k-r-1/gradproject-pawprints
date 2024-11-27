package com.swuproject.pawprints.ui

import android.app.Dialog
import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.swuproject.pawprints.R
import com.swuproject.pawprints.dto.SightReportResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PopupDetailDialog(context: Context, private val sightReport: SightReportResponse) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_sight_detail)

        // 팝업창 크기 설정
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // 뷰 초기화
        val imageView = findViewById<ImageView>(R.id.iv_hometab_sightrecycler_photo)
        val titleView = findViewById<TextView>(R.id.etv_editSightReport_title)
        val breedView = findViewById<TextView>(R.id.tv_hometab_sightrecycler_breed)
        val areaView = findViewById<TextView>(R.id.tv_hometab_sightrecycler_area)
        val dateView = findViewById<TextView>(R.id.tv_hometab_sightrecycler_date)
        val locationView = findViewById<TextView>(R.id.tv_hometab_sightrecycler_location)
        val featureView = findViewById<TextView>(R.id.tv_hometab_sightrecycler_feature)
        val contactView = findViewById<TextView>(R.id.tv_hometab_lostrecycler_contact)

        // 데이터 세팅
        titleView.text = sightReport.sightTitle ?: "제목 없음"
        breedView.text = sightReport.sightBreed ?: "종 정보 없음"

        // 위도와 경도로 주소 변환 (코루틴 사용)
        val latitude = sightReport.sightAreaLat ?: 0.0
        val longitude = sightReport.sightAreaLng ?: 0.0
        CoroutineScope(Dispatchers.Main).launch {
            val address = withContext(Dispatchers.IO) {
                getAddressFromLatLng(latitude, longitude)
            }
            areaView.text = address ?: "$latitude, $longitude"
        }

        // 날짜 포맷 변경
        dateView.text = formatDate(sightReport.sightDate)

        locationView.text = sightReport.sightLocation ?: "발견 장소 정보 없음"
        featureView.text = sightReport.sightDescription ?: "특징 정보 없음"
        contactView.text = if (sightReport.sightContact.isNullOrEmpty()) "연락처 정보 없음" else sightReport.sightContact

        // 이미지 로드
        val firstImage = sightReport.sightImages.firstOrNull()?.sightImagePath
        if (firstImage != null) {
            Glide.with(context)
                .load(firstImage.replace("gs://", "https://storage.googleapis.com/"))
                .placeholder(R.drawable.pet_image_background)
                .error(R.drawable.pet_image_background)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.pet_image_background)
        }
    }

    // 날짜 포맷 변경 함수
    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return ""

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())

        return try {
            val date: Date = inputFormat.parse(dateString) ?: return dateString
            outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            dateString
        }
    }

    // 위도와 경도를 주소로 변환하는 함수
    private fun getAddressFromLatLng(latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                addresses[0].getAddressLine(0)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
