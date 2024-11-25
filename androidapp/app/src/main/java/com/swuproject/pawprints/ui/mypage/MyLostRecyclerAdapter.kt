package com.swuproject.pawprints.ui.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.swuproject.pawprints.R
import com.swuproject.pawprints.common.Utils
import com.swuproject.pawprints.dto.LostReportResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyLostRecyclerAdapter(
    items: List<LostReportResponse>,
    context: Context
) : RecyclerView.Adapter<MyLostRecyclerAdapter.ViewHolder>() {

    private val filteredItems: List<LostReportResponse> // 필터링된 데이터
    private val appContext: Context = context.applicationContext // 안전한 Context 저장

    init {
        // 초기화 시 userId와 일치하는 데이터만 필터링
        val userId = Utils.getUserId(appContext)
        filteredItems = items.filter { it.userId == userId }
    }

    override fun getItemCount(): Int = filteredItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredItems[position]
        val listener = View.OnClickListener {
            Toast.makeText(
                it.context,
                "Clicked -> title : ${item.lostTitle}, date : ${item.lostDate}",
                Toast.LENGTH_SHORT
            ).show()
        }
        holder.apply {
            bind(listener, item)
            itemView.tag = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_my_report_recycler, parent, false)
        return ViewHolder(inflatedView, appContext)
    }

    class ViewHolder(v: View, private val context: Context) : RecyclerView.ViewHolder(v) {
        private var titleTextView: TextView = v.findViewById(R.id.tv_myReport_title)
        private var dateTextView: TextView = v.findViewById(R.id.tv_myReport_date)
        private var locationTextView: TextView = v.findViewById(R.id.tv_myReport_location)
        private var editButton: Button = v.findViewById(R.id.btnEdit)
        fun bind(listener: View.OnClickListener, item: LostReportResponse) {
            titleTextView.text = item.lostTitle
            dateTextView.text = formatDate(item.lostDate)
            locationTextView.text = item.lostLocation

            editButton.setOnClickListener {
                // LostReportResponse 객체를 직접 전달
                val bundle = Bundle()
                // 데이터 추가 ("lostId"는 키, item.lostId는 값)
                bundle.putInt("lostId", item.lostId)
                val navController = Navigation.findNavController(itemView)
                Log.d("NavigationDebug", "Current destination: ${navController.currentDestination?.id}")
                navController.navigate(R.id.action_myLostReportFragment_to_editLostReportFragment, bundle)
            }
        }

        // 날짜 포맷 변경 함수
        private fun formatDate(dateString: String?): String {
            if (dateString.isNullOrEmpty()) return ""
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
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
