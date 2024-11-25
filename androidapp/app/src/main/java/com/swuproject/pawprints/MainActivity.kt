package com.swuproject.pawprints

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.swuproject.pawprints.common.Utils
import com.swuproject.pawprints.databinding.ActivityMainBinding
import com.swuproject.pawprints.ui.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbarTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 로그인 상태 확인
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        if (!isLoggedIn) {
            // 로그인되지 않은 경우 로그인 액티비티로 이동
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

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
                R.id.navigation_poster -> "포스터 생성"
                R.id.navigation_matching -> "매칭"
                R.id.navigation_home -> "홈"
                R.id.navigation_map -> "지도"
                R.id.navigation_mypage -> "내정보"
                R.id.contactUsFragment -> "문의하기"
                R.id.editPetInfoFragment -> "반려동물 관리"
                R.id.editProfileFragment -> "개인정보 관리"
                R.id.faqFragment -> "자주하는 질문"
                R.id.myLostReportFragment -> "나의 실종 신고"
                R.id.mySightReportFragment -> "나의 목격 신고"
                else -> ""
            }
        }
    }
}