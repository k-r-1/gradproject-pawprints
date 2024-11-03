package com.swuproject.pawprints.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.swuproject.pawprints.R

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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navigate back to MypageFragment
                findNavController().navigate(R.id.navigation_mypage)
            }
        })
    }

    private fun setupFaqList() {
        // FAQ 데이터를 추가합니다.
        faqList.add(FaqItem("Q. 매칭 기능은 어떻게 사용하나요?", "A. 하단의 <매칭> 메뉴로 들어가신 후, 상단에 표시된 사전 실종 신고된 반려동물 리스트에서 매칭할 반려동물을 선택하세요. 선택 후 매칭 버튼을 누르시면 매칭 기능이 실행됩니다."))
        faqList.add(FaqItem("Q. 반려동물 실종 신고 및 목격 신고는 어떻게 하나요?", "A. 하단의 <홈> 메뉴로 들어가셔서 [신고하기] 버튼을 누르시면 [실종신고]와 [목격신고] 버튼이 나타납니다. 실종 신고를 원하시면 [실종신고] 버튼을, 실종 동물을 목격하여 목격 신고를 원하시면 [목격신고] 버튼을 선택하시면 됩니다."))
        faqList.add(FaqItem("Q. 반려동물 리스트에서 반려동물 정보가 없다고 뜨는데, 반려동물은 어디서 추가할 수 있나요?", "A. 모든 기능을 이용하시려면 먼저 하단 메뉴의 <내정보>에서 [반려동물 관리] 메뉴로 들어가 반려동물 등록을 해주셔야 합니다. 이곳에서 새로운 반려동물을 등록하거나, 수정 및 삭제 기능도 이용하실 수 있습니다."))
        faqList.add(FaqItem("Q. 지도 메뉴는 어떤 기능인가요?", "A. 하단의 <지도> 메뉴는 현재 사용자의 위치를 기준으로, 목격 신고된 동물들의 위치를 지도에 시각적으로 표시해줍니다. 화면을 확대/축소하거나 이동하여 목격된 동물들이 어느 위치에 있는지 확인하실 수 있습니다. 각 마커를 클릭하면 해당 동물의 목격 신고 내용을 상세히 확인할 수 있습니다."))
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvFaq)
        val adapter = FaqAdapter(faqList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}
data class FaqItem(val question: String, val answer: String)