package com.swuproject.pawprints.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.swuproject.pawprints.R
import com.swuproject.pawprints.dto.SightReportResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SightRecyclerAdapter(private val items: List<SightReportResponse>, private val context: Context) : RecyclerView.Adapter<SightRecyclerAdapter.ViewHolder>() {

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val listener = View.OnClickListener {
            Toast.makeText(it.context, "Clicked -> title : ${item.sightTitle}, breed : ${item.sightBreed}", Toast.LENGTH_SHORT).show()
        }
        holder.apply {
            bind(listener, item)
            itemView.tag = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.item_hometab_sight_recycler, parent, false)
        return ViewHolder(inflatedView, context)
    }

    class ViewHolder(v: View, private val context: Context) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        private var photoImageView: ImageView = v.findViewById(R.id.iv_hometab_sightrecycler_photo)
        private var titleTextView: TextView = v.findViewById(R.id.tv_hometab_sightrecycler_title)
        private var breedTextView: TextView = v.findViewById(R.id.tv_hometab_sightrecycler_breed)
        private var areaTextView: TextView = v.findViewById(R.id.tv_hometab_sightrecycler_area)
        private var dateTextView: TextView = v.findViewById(R.id.tv_hometab_sightrecycler_date)
        private var locationTextView: TextView = v.findViewById(R.id.tv_hometab_sightrecycler_location)
        private var featureTextView: TextView = v.findViewById(R.id.tv_hometab_sightrecycler_feature)

        fun bind(listener: View.OnClickListener, item: SightReportResponse) {
            val firstImage = item.sightImages.firstOrNull()?.sightImagePath
            if (firstImage != null) {
                // gs:// URL을 https://storage.googleapis.com/로 변환
                val imageUrl = firstImage.replace("gs://", "https://storage.googleapis.com/")

                // Glide를 사용해 이미지 로드
                Glide.with(context).load(imageUrl).into(photoImageView)
            } else {
                photoImageView.setImageResource(R.drawable.dog_sample2) // 기본 이미지
            }
            titleTextView.text = item.sightTitle
            breedTextView.text = item.sightBreed
            areaTextView.text = "${item.sightAreaLat}, ${item.sightAreaLng}"
            dateTextView.text = formatDate(item.sightDate)
            locationTextView.text = item.sightLocation
            featureTextView.text = item.sightDescription

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
    }
}
