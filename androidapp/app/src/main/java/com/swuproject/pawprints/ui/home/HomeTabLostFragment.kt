package com.swuproject.pawprints.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.swuproject.pawprints.databinding.FragmentHomeTabLostBinding
import com.swuproject.pawprints.network.LostReportResponse
import com.swuproject.pawprints.network.RetrofitClient
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
        val service = RetrofitClient.getRetrofitService()
        val requestCall = service.getLostReports()

        requestCall.enqueue(object : Callback<List<LostReportResponse>> {
            override fun onResponse(call: Call<List<LostReportResponse>>, response: Response<List<LostReportResponse>>) {
                if (response.isSuccessful) {
                    val lostReports = response.body()
                    if (lostReports != null) {
                        val lostReportDataList = lostReports.map { report ->
                            LostRecyclerData(
                                report.images.map { it.lostImagePath },
                                report.lostTitle,
                                report.lostBreed,
                                report.lostGender,
                                "미상",
                                report.lostDate,
                                report.lostLocation,
                                report.lostDescription,
                                report.lostContact
                            )
                        }
                        adapter.updateData(lostReportDataList)
                    }
                } else {
                    Log.e("LostReportsAPI", "Error1: ${response.code()} ${response.message()}")
                    response.errorBody()?.let {
                        Log.e("LostReportsAPI", "Error body: ${it.string()}")
                    }
                }
            }

            override fun onFailure(call: Call<List<LostReportResponse>>, t: Throwable) {
                Log.e("LostReportsAPI", "Error2: ${t.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
