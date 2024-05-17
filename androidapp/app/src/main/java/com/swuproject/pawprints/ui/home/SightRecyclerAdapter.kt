package com.swuproject.pawprints.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.swuproject.pawprints.R

class SightRecyclerAdapter(private val items: ArrayList<SightRecyclerData>) : RecyclerView.Adapter<SightRecyclerAdapter.ViewHolder>()

{
    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: SightRecyclerAdapter.ViewHolder, position: Int) {

        val item = items[position]
        val listener = View.OnClickListener { it ->
            Toast.makeText(it.context, "Clicked -> title : ${item.sight_title}, breed : ${item.sight_breed}", Toast.LENGTH_SHORT).show()
        }
        holder.apply {
            bind(listener, item)
            itemView.tag = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.item_hometab_sight_recycler, parent, false)
        return SightRecyclerAdapter.ViewHolder(inflatedView)
    }

    // 각 항목에 필요한 기능을 구현
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        private var photoImageView: ImageView = v.findViewById(R.id.iv_hometab_sightrecycler_photo)
        private var titleTextView: TextView = v.findViewById(R.id.tv_hometab_sightrecycler_title)
        private var breedTextView: TextView = v.findViewById(R.id.tv_hometab_sightrecycler_breed)
        private var areaTextView: TextView = v.findViewById(R.id.tv_hometab_sightrecycler_area)
        private var dateTextView: TextView = v.findViewById(R.id.tv_hometab_sightrecycler_date)
        private var locationTextView: TextView = v.findViewById(R.id.tv_hometab_sightrecycler_location)
        private var featureTextView: TextView = v.findViewById(R.id.tv_hometab_sightrecycler_feature)
        fun bind(listener: View.OnClickListener, item: SightRecyclerData) {
            item.sight_photo?.let { photoImageView.setImageDrawable(it) }
            titleTextView.text = item.sight_title
            breedTextView.text = item.sight_breed
            areaTextView.text = item.sight_area
            dateTextView.text = item.sight_date
            locationTextView.text = item.sight_location
            featureTextView.text = item.sight_feature

            view.setOnClickListener(listener)
        }
    }

}