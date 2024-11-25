package com.swuproject.pawprints.ui.mypage

import android.content.Context
import android.os.Bundle
import android.provider.Settings.Global.putString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.swuproject.pawprints.R
import com.swuproject.pawprints.common.Utils
import com.swuproject.pawprints.dto.SightReportResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MySightRecyclerAdapter(
    items: List<SightReportResponse>,
    context: Context
) : RecyclerView.Adapter<MySightRecyclerAdapter.ViewHolder>() {

    private val filteredItems: List<SightReportResponse>
    private val appContext: Context = context.applicationContext

    init {
        val userId = Utils.getUserId(appContext)
        filteredItems = items.filter { it.userId == userId }
    }

    override fun getItemCount(): Int = filteredItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredItems[position]
        holder.apply {
            bind(item)
            itemView.tag = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_report_recycler, parent, false)
        return ViewHolder(inflatedView, appContext)
    }

    class ViewHolder(v: View, private val context: Context) : RecyclerView.ViewHolder(v) {
        private var titleTextView: TextView = v.findViewById(R.id.tv_myReport_title)
        private var dateTextView: TextView = v.findViewById(R.id.tv_myReport_date)
        private var locationTextView: TextView = v.findViewById(R.id.tv_myReport_location)
        private var editButton: Button = v.findViewById(R.id.btnEdit)

        fun bind(item: SightReportResponse) {
            titleTextView.text = item.sightTitle
            dateTextView.text = formatDate(item.sightDate)
            locationTextView.text = item.sightLocation

            editButton.setOnClickListener {
                // SightReportResponse 객체를 직접 전달
                val bundle = Bundle()
                // 데이터 추가 ("sightId"는 키, item.sightId는 값)
                bundle.putInt("sightId", item.sightId)
                val navController = Navigation.findNavController(itemView)
                Log.d("NavigationDebug", "Current destination: ${navController.currentDestination?.id}")
                navController.navigate(R.id.action_mySightReportFragment_to_editSightReportFragment, bundle)
            }
        }

        private fun formatDate(dateString: String?): String {
            if (dateString.isNullOrEmpty()) return ""
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
            return try {
                val date: Date = inputFormat.parse(dateString) ?: return dateString
                outputFormat.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
                dateString
            }
        }
    }
}
