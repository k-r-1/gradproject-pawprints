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

class LostRecyclerAdapter(private val items: ArrayList<LostRecyclerData>) : RecyclerView.Adapter<LostRecyclerAdapter.ViewHolder>()

{
    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: LostRecyclerAdapter.ViewHolder, position: Int) {

        val item = items[position]
        val listener = View.OnClickListener { it ->
            Toast.makeText(it.context, "Clicked -> title : ${item.lost_title}, breed : ${item.lost_breed}", Toast.LENGTH_SHORT).show()
        }
        holder.apply {
            bind(listener, item)
            itemView.tag = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.item_hometab_lost_recycler, parent, false)
        return LostRecyclerAdapter.ViewHolder(inflatedView)
    }

    // 각 항목에 필요한 기능을 구현
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        private var titleTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_title)
        private var breedTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_breed)
        private var genderTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_gender)
        private var areaTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_area)
        private var dateTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_date)
        private var locationTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_location)
        private var featureTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_feature)
        private var contactTextView: TextView = v.findViewById(R.id.tv_hometab_lostrecycler_contact)

        fun bind(listener: View.OnClickListener, item: LostRecyclerData) {
            titleTextView.text = item.lost_title
            breedTextView.text = item.lost_breed
            genderTextView.text = item.lost_gender
            areaTextView.text = item.lost_area
            dateTextView.text = item.lost_date
            locationTextView.text = item.lost_location
            featureTextView.text = item.lost_feature
            contactTextView.text = item.lost_contact

            view.setOnClickListener(listener)
        }
    }

}