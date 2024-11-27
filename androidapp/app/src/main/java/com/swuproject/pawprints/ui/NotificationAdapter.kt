package com.swuproject.pawprints.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.swuproject.pawprints.R
import com.swuproject.pawprints.dto.SightReportResponse

class NotificationAdapter(private val notifications: List<SightReportResponse>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.notification_title)
        val thumbnailImage: ImageView = view.findViewById(R.id.notification_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.titleText.text = notification.sightTitle ?: "제목 없음"

        // Glide로 이미지 로드
        if (!notification.sightImages.isNullOrEmpty()) {
            val imagePath = notification.sightImages[0].sightImagePath?.replace("gs://", "https://storage.googleapis.com/")
            Glide.with(holder.itemView.context)
                .load(imagePath)
                .placeholder(R.drawable.pet_image_background) // 로드 중 기본 이미지
                .error(R.drawable.pet_image_background) // 로드 실패 시 기본 이미지
                .into(holder.thumbnailImage)
        } else {
            holder.thumbnailImage.setImageResource(R.drawable.pet_image_background) // 기본 이미지
        }

        // 클릭 이벤트 처리 - 팝업창 열기
        holder.itemView.setOnClickListener {
            val dialog = PopupDetailDialog(holder.itemView.context, notification)
            dialog.show()
        }
    }

    override fun getItemCount(): Int = notifications.size
}
