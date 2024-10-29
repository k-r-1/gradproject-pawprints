package com.swuproject.pawprints.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.swuproject.pawprints.R
import com.swuproject.pawprints.databinding.FragmentHomeTabSightBinding
import com.swuproject.pawprints.dto.SightReportResponse
import com.swuproject.pawprints.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeTabSightFragment : Fragment() {
    lateinit var binding: FragmentHomeTabSightBinding
    private val FILTER_REQUEST_CODE = 1002

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeTabSightBinding.inflate(inflater, container, false)
        val rootView = binding.root

        // RecyclerView 설정
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.hometab_sightRecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 필터 아이콘 클릭 시 필터링 화면으로 이동
        binding.icFilter.setOnClickListener {
            val intent = Intent(requireContext(), FilterActivity::class.java)
            startActivityForResult(intent, FILTER_REQUEST_CODE)
        }

        // SwipeRefreshLayout 새로고침 리스너 설정
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchSightReports { sightReports ->
                val adapter = SightRecyclerAdapter(sightReports, requireContext())
                recyclerView.adapter = adapter
                binding.swipeRefreshLayout.isRefreshing = false // 새로고침 완료 후 로딩 인디케이터 숨기기
            }
        }

        // 최초 데이터 로드
        fetchSightReports { sightReports ->
            val adapter = SightRecyclerAdapter(sightReports, requireContext())
            recyclerView.adapter = adapter
        }

        return rootView
    }

    private fun fetchSightReports(callback: (List<SightReportResponse>) -> Unit) {
        val retrofitService = RetrofitClient.getRetrofitService()
        retrofitService.getSightReports().enqueue(object : Callback<List<SightReportResponse>> {
            override fun onResponse(
                call: Call<List<SightReportResponse>>,
                response: Response<List<SightReportResponse>>
            ) {
                if (response.isSuccessful) {
                    val sortedList = response.body()?.reversed() ?: emptyList()
                    callback(sortedList)
                }
            }

            override fun onFailure(call: Call<List<SightReportResponse>>, t: Throwable) {
                // 실패 처리
                binding.swipeRefreshLayout.isRefreshing = false // 실패 시에도 로딩 인디케이터 숨기기
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILTER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val breedType = data?.getStringExtra("breedType")
            val searchArea = data?.getStringExtra("searchArea")

            fetchSightReports { sightReports ->
                // 필터링 조건에 맞는 목격 신고 데이터 필터링
                val filteredReports = sightReports.filter { report ->
                    (breedType == null || report.sightBreed == breedType) &&
                            (searchArea == null || report.sightLocation?.contains(searchArea, ignoreCase = true) == true)
                }

                val adapter = SightRecyclerAdapter(filteredReports, requireContext())
                binding.hometabSightRecyclerview.adapter = adapter
            }
        }
    }
}
