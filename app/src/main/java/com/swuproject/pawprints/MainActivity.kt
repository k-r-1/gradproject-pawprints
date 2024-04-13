package com.swuproject.pawprints

import android.os.Bundle
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.swuproject.pawprints.common.Utils
import com.swuproject.pawprints.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbarTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 기본 액션바 숨기기
        Utils.hideActionBar(this)

        // 상태 표시줄 색상 변경
        Utils.setStatusBarColor(this, R.color.primary_pink)

        // 커스텀 툴바의 제목 TextView 찾기
        toolbarTitle = findViewById(R.id.toolbar_title)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        // 프래그먼트 전환 시 툴바 제목 변경
        navController.addOnDestinationChangedListener { _, destination, _ ->
            toolbarTitle.text = when (destination.id) {
                R.id.navigation_predictedlocation -> "예상위치"
                R.id.navigation_matching -> "매칭"
                R.id.navigation_home -> "홈"
                R.id.navigation_map -> "지도"
                R.id.navigation_mypage -> "내정보"
                else -> ""
            }
        }
    }
}