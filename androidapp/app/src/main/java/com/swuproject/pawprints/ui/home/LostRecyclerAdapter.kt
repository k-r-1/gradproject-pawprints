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
import com.swuproject.pawprints.databinding.ItemHometabLostRecyclerBinding
import com.swuproject.pawprints.dto.LostReportResponse

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
            areaTextView.text = "${item.lostAreaLat}, ${item.lostAreaLng}"
            dateTextView.text = item.lostDate
            locationTextView.text = item.lostLocation
            featureTextView.text = item.lostFeature
            descriptionTextView.text = item.lostDescription
            contactTextView.text = item.lostContact

            view.setOnClickListener(listener)
        }
    }
}