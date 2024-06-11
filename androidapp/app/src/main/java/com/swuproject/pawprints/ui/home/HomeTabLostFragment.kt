package com.swuproject.pawprints.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.swuproject.pawprints.R
import com.swuproject.pawprints.databinding.FragmentHomeTabLostBinding
import com.swuproject.pawprints.databinding.FragmentHomeTabSightBinding
import com.swuproject.pawprints.dto.LostReportResponse
import com.swuproject.pawprints.dto.SightReportResponse
import com.swuproject.pawprints.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeTabLostFragment : Fragment() {

    lateinit var binding: FragmentHomeTabLostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeTabLostBinding.inflate(inflater, container, false)
        val rootView = binding.root

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.hometab_lostRecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)

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
                    callback(response.body() ?: emptyList())
                }
            }

            override fun onFailure(call: Call<List<LostReportResponse>>, t: Throwable) {
                // 실패 처리
            }
        })
    }
}
