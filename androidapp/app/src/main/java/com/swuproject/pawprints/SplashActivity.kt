package com.swuproject.pawprints

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.swuproject.pawprints.common.Utils
import com.swuproject.pawprints.ui.LoginActivity

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DISPLAY_LENGTH = 1000L // 1초

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 기본 액션바 숨기기
        Utils.hideActionBar(this)

        // 상태 표시줄 색상 변경
        Utils.setStatusBarColor(this, R.color.primary_pink)

        // Handler를 사용하여 일정 시간 후에 적절한 액티비티로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            // SharedPreferences에서 로그인 상태 확인
            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

            val nextActivity = if (isLoggedIn) {
                // 로그인된 경우 MainActivity로 이동
                Intent(this, MainActivity::class.java)
            } else {
                // 로그인되지 않은 경우 LoginActivity로 이동
                Intent(this, LoginActivity::class.java)
            }

            startActivity(nextActivity)
            finish()
        }, SPLASH_DISPLAY_LENGTH)
    }
}
