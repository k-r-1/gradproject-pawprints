package com.swuproject.pawprints.ui.home

import android.content.Context
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.swuproject.pawprints.R
import com.swuproject.pawprints.dto.LostReportResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LostRecyclerAdapter(private val items: List<LostReportResponse>, private val context: Context) : RecyclerView.Adapter<LostRecyclerAdapter.ViewHolder>() {

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val listener = View.OnClickListener {
            Toast.makeText(it.context, "Clicked -> title : ${item.lostTitle}, breed : ${item.petBreed}", Toast.LENGTH_SHORT).show()
        }
        holder.apply {
            bind(listener, item)
            itemView.tag = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.item_hometab_lost_recycler, parent, false)
        return ViewHolder(inflatedView, context)
    }

    class ViewHolder(v: View, private val context: Context) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        private var photoImageView: ImageView = v.findViewById(R.id.iv_hometab_lostrecycler_photo)
        private var titleTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_title)
        private var breedTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_breed)
        private var genderageTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_gender)
        private var areaTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_area)
        private var dateTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_date)
        private var locationTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_location)
        private var featureTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_feature) // 특징
        private var descriptionTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_description) // 설명
        private var contactTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_contact)

        fun bind(listener: View.OnClickListener, item: LostReportResponse) {
            val firstImage = item.lostImages.firstOrNull()?.lostImagePath
            if (firstImage != null) {
                // gs:// URL을 https://storage.googleapis.com/로 변환
                val imageUrl = firstImage.replace("gs://", "https://storage.googleapis.com/")

                // Glide를 사용해 이미지 로드
                Glide.with(context).load(imageUrl).into(photoImageView)
            } else {
                photoImageView.setImageResource(R.drawable.dog_sample2) // 기본 이미지
            }
            titleTextView.text = item.lostTitle
            breedTextView.text = item.petBreed
            genderageTextView.text = "${item.petGender} / ${item.petAge}"

            // Nullable 타입을 처리하여 주소 변환
            val latitude = item.lostAreaLat ?: 0.0
            val longitude = item.lostAreaLng ?: 0.0

            CoroutineScope(Dispatchers.Main).launch {
                val address = withContext(Dispatchers.IO) {
                    getAddressFromLatLng(latitude, longitude)
                }
                areaTextView.text = address ?: "$latitude, $longitude"
            }

            dateTextView.text = formatDate(item.lostDate)
            locationTextView.text = item.lostLocation
            featureTextView.text = item.petFeature
            descriptionTextView.text = item.lostDescription
            contactTextView.text = item.lostContact

            view.setOnClickListener(listener)
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

        // 위도와 경도를 주소로 변환하는 함수
        private fun getAddressFromLatLng(latitude: Double, longitude: Double): String? {
            val geocoder = Geocoder(context, Locale.getDefault())
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
    }
}
