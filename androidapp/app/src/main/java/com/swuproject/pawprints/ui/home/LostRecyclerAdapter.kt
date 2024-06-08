package com.swuproject.pawprints.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.swuproject.pawprints.databinding.ItemHometabLostRecyclerBinding

class LostRecyclerAdapter(private var items: List<LostRecyclerData>) :
    RecyclerView.Adapter<LostRecyclerAdapter.LostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LostViewHolder {
        val binding = ItemHometabLostRecyclerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return LostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LostViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<LostRecyclerData>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class LostViewHolder(private val binding: ItemHometabLostRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LostRecyclerData) {
            binding.tvHometabLostrecyclerTitle.text = item.lostTitle
            binding.tvHometabLostrecyclerBreed.text = item.lostBreed
            binding.tvHometabLostrecyclerGender.text = item.lostGender
            binding.tvHometabLostrecyclerArea.text = item.lostLocation
            binding.tvHometabLostrecyclerDate.text = item.lostDate
            binding.tvHometabLostrecyclerFeature.text = item.lostDescription
            binding.tvHometabLostrecyclerContact.text = item.lostContact

            // 이미지 로드
            if (item.lostImagePaths.isNotEmpty()) {
                Glide.with(binding.ivHometabLostrecyclerPhoto.context)
                    .load(item.lostImagePaths[0]) // 첫 번째 이미지를 로드
                    .into(binding.ivHometabLostrecyclerPhoto)
            }
        }
    }
}
