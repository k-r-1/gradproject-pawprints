package com.swuproject.pawprints.common

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

object Utils {
    // 테스트2

    // 기본 액션바 숨기기
    fun hideActionBar(activity: AppCompatActivity) {
        activity.supportActionBar?.hide()
    }

    // SharedPreferences에서 사용자 ID 가져오기
    fun getUserId(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_id", null)
    }

    // 상태 표시줄 색상 변경
    fun setStatusBarColor(activity: AppCompatActivity, colorResId: Int) {
        val window = activity.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(colorResId)
        }
    }
}