package com.swuproject.pawprints.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.swuproject.pawprints.MainActivity
import com.swuproject.pawprints.R
import com.swuproject.pawprints.common.Utils
import com.swuproject.pawprints.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

class LoginActivity : AppCompatActivity() {

    private lateinit var buttonLogin: Button
    private lateinit var buttonSignUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // 기본 액션바 숨기기
        Utils.hideActionBar(this)

        // 상태 표시줄 색상 변경
        Utils.setStatusBarColor(this, R.color.primary_pink)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        buttonLogin = findViewById(R.id.btn_login)
//        buttonLogin.setOnClickListener {
//            // 로그인 버튼 클릭 시 MainActivity로 이동
//            val intent = Intent(this@LoginActivity, MainActivity::class.java)
//            startActivity(intent)
//            finish() // LoginActivity 종료
//        }
        // 로그인 버튼 클릭 리스너 설정
        findViewById<Button>(R.id.btn_login).setOnClickListener {
            signIn()
        }

        buttonSignUp = findViewById(R.id.btn_sign_up)
        buttonSignUp.setOnClickListener {
            // 회원가입 버튼 클릭 시 SignUpActivity로 이동
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

    }

    private fun signIn() {
        // 입력 필드에서 사용자 정보 가져오기
        val userId = findViewById<EditText>(R.id.edit_id).text.toString()
        val userPw = findViewById<EditText>(R.id.edit_pw).text.toString()

        // RetrofitService 인스턴스 가져오기
        val retrofitService = RetrofitClient.getRetrofitService()
        // 요청 바디 생성
        val requestBody = mapOf(
            "userId" to userId,
            "userPw" to userPw
        )

        // 로그인 API 호출
        retrofitService.signin(requestBody)
            .enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        val message = responseBody?.get("message") ?: "알 수 없는 오류"
                        // 로그인 성공 처리
                        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                        if (message == "로그인 성공") {
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        // 로그인 실패 처리
                        val errorMessage = "로그인 실패: ${response.code()} ${response.message()}"
                        Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        Log.e("LoginActivity", errorMessage)
                    }
                }

                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                    val errorMessage = when (t) {
                        is SocketTimeoutException -> "서버 응답 시간을 초과했습니다."
                        is ConnectException -> "서버에 연결할 수 없습니다. 서버가 실행 중인지 확인하세요."
                        else -> "네트워크 오류: ${t.message}"
                    }
                    Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    Log.e("LoginActivity", "네트워크 오류: ${t.message}", t)
                    t.cause?.let {
                        Log.e("LoginActivity", "원인: ${it.message}", it)
                    }
                }
            })
    }
}