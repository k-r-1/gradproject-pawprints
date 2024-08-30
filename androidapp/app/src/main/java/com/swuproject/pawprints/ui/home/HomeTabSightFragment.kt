package com.swuproject.pawprints.ui.home

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeTabSightBinding.inflate(inflater, container, false)
        val rootView = binding.root

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.hometab_sightRecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)

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
                    // 데이터를 역순으로 정렬
                    val sortedList = response.body()?.reversed() ?: emptyList()
                    callback(sortedList)
                }
            }

            override fun onFailure(call: Call<List<SightReportResponse>>, t: Throwable) {
                // 실패 처리
            }
        })
    }

}
