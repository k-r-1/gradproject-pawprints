package com.swuproject.pawprints.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.swuproject.pawprints.R
import com.swuproject.pawprints.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val tabTextList = listOf("실종 신고", "목격 신고")

    private var fabOpen: Animation? = null
    private var fabClose: Animation? = null

    private var isFabOpen = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.homeViewpager.adapter = ViewPagerAdapter(requireActivity())

        // TabLayout과 ViewPager2를 연결하고 탭 텍스트의 크기를 변경합니다.
        TabLayoutMediator(binding.homeTabLayout, binding.homeViewpager) { tab, position ->
            val tabView = LayoutInflater.from(context).inflate(R.layout.custom_home_tab, null)
            val textView = tabView.findViewById<TextView>(R.id.tv_custom_home_tab)
            textView.text = tabTextList[position]

            tab.customView = tabView
        }.attach()

        // 초기 탭 색상 설정
        updateTabTextColor(binding.homeTabLayout.selectedTabPosition)

        // 탭의 선택 상태를 감지하고 색상을 변경합니다.
        binding.homeTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position ?: 0
                updateTabTextColor(position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Unselected tab 처리는 필요 없습니다.
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Reselected tab 처리는 필요 없습니다.
            }
        })

        return root
    }

    private fun updateTabTextColor(selectedPosition: Int) {
        for (i in tabTextList.indices) {
            val tabView = binding.homeTabLayout.getTabAt(i)?.customView
            val textView = tabView?.findViewById<TextView>(R.id.tv_custom_home_tab)
            val isSelected = i == selectedPosition
            val textColorRes = if (isSelected) R.color.deep_pink else R.color.bottom_nav_not_selected
            textView?.setTextColor(ContextCompat.getColor(requireContext(), textColorRes))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            initialize()
            fabHomeReport.setOnClickListener { toggleFabMenu() }
            fabHomeLostreport.setOnClickListener {
                toggleFabMenu()
                fabHomeLostreport.setImageResource(R.drawable.icon_pen)
            }
            fabHomeSightreport.setOnClickListener {
                toggleFabMenu()
                fabHomeSightreport.setImageResource(R.drawable.icon_pen)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        fabOpen = null
        fabClose = null
    }

    private fun FragmentHomeBinding.initialize() {
        fabOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_close)
    }

    private fun toggleFabMenu() {
        isFabOpen = if (isFabOpen) {
            binding.fabHomeLostreport.startAnimation(fabClose)
            binding.fabHomeSightreport.startAnimation(fabClose)
            binding.fabHomeLostreport.visibility = View.INVISIBLE
            binding.fabHomeSightreport.visibility = View.INVISIBLE
            false
        } else {
            binding.fabHomeLostreport.startAnimation(fabOpen)
            binding.fabHomeSightreport.startAnimation(fabOpen)
            binding.fabHomeLostreport.visibility = View.VISIBLE
            binding.fabHomeSightreport.visibility = View.VISIBLE
            true
        }
    }
}
