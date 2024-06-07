package com.swuproject.pawprints.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.swuproject.pawprints.R
import com.swuproject.pawprints.ui.home.LostRecyclerData

class LostRecyclerAdapter(private var items: ArrayList<LostRecyclerData>) : RecyclerView.Adapter<LostRecyclerAdapter.ViewHolder>() {

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val listener = View.OnClickListener {
            Toast.makeText(it.context, "Clicked -> title : ${item.lostTitle}, breed : ${item.lostBreed}", Toast.LENGTH_SHORT).show()
            // 클릭 이벤트 처리할 내용을 추가할 수 있습니다.
        }
        holder.apply {
            bind(listener, item)
            itemView.tag = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.item_hometab_lost_recycler, parent, false)
        return ViewHolder(inflatedView)
    }

    fun updateData(newItems: List<LostRecyclerData>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        private var photoImageView: ImageView = v.findViewById(R.id.iv_hometab_lostrecycler_photo)
        private var titleTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_title)
        private var breedTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_breed)
        private var genderTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_gender)
        private var areaTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_area)
        private var dateTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_date)
        private var locationTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_location)
        private var featureTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_feature)
        private var contactTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_contact)

        fun bind(listener: View.OnClickListener, item: LostRecyclerData) {
            // 이미지 다운로드 및 표시 (예: Glide 사용)
            Glide.with(photoImageView.context)
                .load(item.lostImagePaths.firstOrNull()) // 첫 번째 이미지 경로를 사용
                .into(photoImageView)

            titleTextView.text = item.lostTitle
            breedTextView.text = item.lostBreed
            genderTextView.text = item.lostGender
            areaTextView.text = item.lostArea
            dateTextView.text = item.lostDate
            locationTextView.text = item.lostLocation
            featureTextView.text = item.lostDescription
            contactTextView.text = item.lostContact

            view.setOnClickListener(listener)
        }
    }
}
