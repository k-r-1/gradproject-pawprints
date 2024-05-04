package com.swuproject.pawprints.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.swuproject.pawprints.R
import com.swuproject.pawprints.databinding.FragmentFaqBinding

class FaqFragment : Fragment() {

    private val faqList = mutableListOf<FaqItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_faq, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFaqList()
        setupRecyclerView(view)
    }

    private fun setupFaqList() {
        // FAQ 데이터를 추가합니다.
        faqList.add(FaqItem("질문 1", "답변 1"))
        faqList.add(FaqItem("질문 2", "답변 2"))
        // 더 많은 FAQ 항목을 추가할 수 있습니다.
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvFaq)
        val adapter = FaqAdapter(faqList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}
data class FaqItem(val question: String, val answer: String)