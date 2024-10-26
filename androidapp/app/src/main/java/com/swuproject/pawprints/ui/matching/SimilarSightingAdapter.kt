package com.swuproject.pawprints.ui.matching

import android.content.Context
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.swuproject.pawprints.R
import com.swuproject.pawprints.network.SimilarSighting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SimilarSightingAdapter(private val context: Context) : RecyclerView.Adapter<SimilarSightingAdapter.ViewHolder>() {

    private val items = mutableListOf<SimilarSighting>()

    fun submitList(list: List<SimilarSighting>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_matchresult_match_recycler, parent, false)
        return ViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
        private val photo: ImageView = itemView.findViewById(R.id.iv_matchresult_recycler_photo)
        private val title: TextView = itemView.findViewById(R.id.tv_matchresult_recycler_title)
        private val breed: TextView = itemView.findViewById(R.id.tv_matchresult_recycler_breed)
        private val area: TextView = itemView.findViewById(R.id.tv_matchresult_recycler_area) // 지역_위도경도
        private val date: TextView = itemView.findViewById(R.id.tv_matchresult_recycler_date)
        private val location: TextView = itemView.findViewById(R.id.tv_matchresult_recycler_location) // 장소_주소
        private val feature: TextView = itemView.findViewById(R.id.tv_matchresult_recycler_feature)
        private val contact: TextView = itemView.findViewById(R.id.tv_matchresult_recycler_contact)

        fun bind(similarSighting: SimilarSighting) {
            val imageUrl = similarSighting.sight_image_path.replace("gs://", "https://storage.googleapis.com/")
            Glide.with(itemView.context).load(imageUrl).into(photo)
            title.text = similarSighting.sight_title
            breed.text = similarSighting.sight_breed

            // Nullable 타입을 처리하여 주소 변환
            val latitude = similarSighting.latitude ?: 0.0
            val longitude = similarSighting.longitude ?: 0.0

            // 위도, 경도를 이용해 주소로 변환
            CoroutineScope(Dispatchers.Main).launch {
                val address = withContext(Dispatchers.IO) {
                    getAddressFromLatLng(latitude, longitude)
                }
                area.text = address ?: "$latitude, $longitude" // 주소를 못 찾으면 위도, 경도 그대로 출력
            }

            date.text = formatDate(similarSighting.sight_date)
            location.text = similarSighting.sight_location
            feature.text = similarSighting.sight_description
            contact.text = if (!similarSighting.sight_contact.isNullOrEmpty()) {
                similarSighting.sight_contact
            } else {
                "연락처가 없습니다"
            }
        }

        // 날짜 포맷 변경 함수
        private fun formatDate(dateString: String?): String {
            if (dateString.isNullOrEmpty()) return ""

            // 받은 날짜 형식
            val inputFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
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
