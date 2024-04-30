package com.swuproject.pawprints.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.swuproject.pawprints.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding:FragmentHomeBinding? = null
    // test
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val tabTextList = listOf("실종 신고", "목격 신고")

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.homeViewpager.adapter = ViewPagerAdapter(requireActivity())

        TabLayoutMediator(binding.homeTabLayout, binding.homeViewpager) { tab, pos ->
            tab.text = tabTextList[pos]
        }.attach()


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}