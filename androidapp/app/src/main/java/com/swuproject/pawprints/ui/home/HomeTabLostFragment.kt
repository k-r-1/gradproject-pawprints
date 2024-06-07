package com.swuproject.pawprints.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.swuproject.pawprints.databinding.FragmentHomeTabLostBinding
import com.swuproject.pawprints.network.LostReportResponse
import com.swuproject.pawprints.network.LostReports
import com.swuproject.pawprints.network.RetrofitService
import com.swuproject.pawprints.network.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeTabLostFragment : Fragment() {

    private var _binding: FragmentHomeTabLostBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: LostRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeTabLostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        fetchLostReports()
    }

    private fun setupRecyclerView() {
        adapter = LostRecyclerAdapter(arrayListOf())
        binding.hometabLostRecyclerview.layoutManager = LinearLayoutManager(context)
        binding.hometabLostRecyclerview.adapter = adapter
    }

    private fun fetchLostReports() {
        val service = ServiceBuilder.buildService(RetrofitService::class.java)
        val requestCall = service.getLostReports()

        requestCall.enqueue(object : Callback<List<LostReports>> {
            override fun onResponse(call: Call<List<LostReports>>, response: Response<List<LostReports>>) {
                if (response.isSuccessful) {
                    val lostReports = response.body()
                    if (lostReports != null) {
                        val lostReportDataList = lostReports.map { report ->
                            LostRecyclerData(
                                report.images.map { it.lostImagePath },
                                report.lostTitle,
                                "미상",
                                "미상",
                                "미상",
                                report.lostDate.toString(),
                                report.lostLocation,
                                report.lostDescription,
                                report.lostContact
                            )
                        }
                        adapter.updateData(lostReportDataList)
                    }
                } else {
                    // 실패 처리
                    Log.e("LostReportsAPI", "Error1: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<LostReports>>, t: Throwable) {
                // 실패 처리
                Log.e("LostReportsAPI", "Error2: ${t.message}")
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}