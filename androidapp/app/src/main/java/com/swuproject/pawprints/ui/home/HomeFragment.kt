package com.swuproject.pawprints.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
