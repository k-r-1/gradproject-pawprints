package com.swuproject.pawprints.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.swuproject.pawprints.R
import com.swuproject.pawprints.databinding.FragmentHomeTabLostBinding
import com.swuproject.pawprints.dto.LostReportResponse
import com.swuproject.pawprints.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeTabLostFragment : Fragment() {

    lateinit var binding: FragmentHomeTabLostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeTabLostBinding.inflate(inflater, container, false)
        val rootView = binding.root

        // RecyclerView 설정
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.hometab_lostRecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // SwipeRefreshLayout 새로고침 리스너 설정
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchLostReports { lostReports ->
                val adapter = LostRecyclerAdapter(lostReports, requireContext())
                recyclerView.adapter = adapter
                binding.swipeRefreshLayout.isRefreshing = false // 새로고침 완료 후 로딩 인디케이터 숨기기
            }
        }

        // 최초 데이터 로드
        fetchLostReports { lostReports ->
            val adapter = LostRecyclerAdapter(lostReports, requireContext())
            recyclerView.adapter = adapter
        }

        return rootView
    }

    private fun fetchLostReports(callback: (List<LostReportResponse>) -> Unit) {
        val retrofitService = RetrofitClient.getRetrofitService()
        retrofitService.getLostReports().enqueue(object : Callback<List<LostReportResponse>> {
            override fun onResponse(
                call: Call<List<LostReportResponse>>,
                response: Response<List<LostReportResponse>>
            ) {
                if (response.isSuccessful) {
                    val sortedList = response.body()?.reversed() ?: emptyList()
                    callback(sortedList)
                }
            }

            override fun onFailure(call: Call<List<LostReportResponse>>, t: Throwable) {
                // 실패 처리
                binding.swipeRefreshLayout.isRefreshing = false // 실패 시에도 로딩 인디케이터 숨기기
            }
        })
    }
}
