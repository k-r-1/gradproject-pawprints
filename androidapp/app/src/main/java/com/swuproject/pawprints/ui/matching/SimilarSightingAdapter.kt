package com.swuproject.pawprints.ui.matching

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.swuproject.pawprints.R
import com.swuproject.pawprints.network.SimilarSighting
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SimilarSightingAdapter : RecyclerView.Adapter<SimilarSightingAdapter.ViewHolder>() {

    private val items = mutableListOf<SimilarSighting>()

    fun submitList(list: List<SimilarSighting>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_matchresult_match_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photo: ImageView = itemView.findViewById(R.id.iv_matchresult_recycler_photo)
        private val title: TextView = itemView.findViewById(R.id.tv_matchresult_recycler_title)
        private val breed: TextView = itemView.findViewById(R.id.tv_matchresult_recycler_breed)
        private val area: TextView = itemView.findViewById(R.id.tv_matchresult_recycler_area)
        private val date: TextView = itemView.findViewById(R.id.tv_matchresult_recycler_date)
        //private val location: TextView = itemView.findViewById(R.id.tv_matchresult_recycler_location)
        private val feature: TextView = itemView.findViewById(R.id.tv_matchresult_recycler_feature)

        fun bind(similarSighting: SimilarSighting) {
            val imageUrl = similarSighting.sight_image_path.replace("gs://", "https://storage.googleapis.com/")
            Glide.with(itemView.context).load(imageUrl).into(photo)
            title.text = similarSighting.sight_title
            breed.text = similarSighting.sight_breed
            area.text = similarSighting.sight_location
            date.text = formatDate(similarSighting.sight_date)
            feature.text = similarSighting.sight_description
        }

        // 날짜 포맷 변경 함수
        private fun formatDate(dateString: String?): String {
            if (dateString.isNullOrEmpty()) return ""

            // 받은 날짜 형식
            val inputFormat = SimpleDateFormat("EEE, dd, MMM yyyy HH:mm:ss z", Locale.ENGLISH)
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