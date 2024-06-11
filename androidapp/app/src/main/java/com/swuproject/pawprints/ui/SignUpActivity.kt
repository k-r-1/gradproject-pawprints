package com.swuproject.pawprints.ui

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.swuproject.pawprints.R
import com.swuproject.pawprints.common.Utils
import com.swuproject.pawprints.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        // 기본 액션바 숨기기
        Utils.hideActionBar(this)

        // 상태 표시줄 색상 변경
        Utils.setStatusBarColor(this, R.color.primary_pink)

        // 뒤로 가기 아이콘에 클릭 리스너 설정
        findViewById<ImageView>(R.id.icon_back).setOnClickListener {
            onBackPressed()
        }

        // 회원가입 버튼 클릭 리스너 설정
        findViewById<Button>(R.id.btn_sign_up).setOnClickListener {
            if (findViewById<CheckBox>(R.id.checkbox_agree).isChecked) {
                signUp()
            } else {
                Toast.makeText(this, "개인정보 수집 및 이용 동의가 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 아이디 중복 확인 버튼 클릭 리스너 설정
        findViewById<Button>(R.id.btn_check_id).setOnClickListener {
            if (findViewById<EditText>(R.id.edit_id).text.toString().isEmpty()) {
                findViewById<TextView>(R.id.error_id).visibility = View.VISIBLE
            } else {
                findViewById<TextView>(R.id.error_id).visibility = View.GONE
                checkUserId()
            }
        }

        // 이메일 중복 확인 버튼 클릭 리스너 설정
        findViewById<Button>(R.id.btn_check_email).setOnClickListener {
            if (findViewById<EditText>(R.id.edit_email).text.toString().isEmpty()) {
                findViewById<TextView>(R.id.error_email).visibility = View.VISIBLE
            } else {
                findViewById<TextView>(R.id.error_email).visibility = View.GONE
                checkUserEmail()
            }
        }

        // CheckBox 클릭 리스너 설정
        findViewById<CheckBox>(R.id.checkbox_agree).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                findViewById<CheckBox>(R.id.checkbox_disagree).isChecked = false
            }
        }

        findViewById<CheckBox>(R.id.checkbox_disagree).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                findViewById<CheckBox>(R.id.checkbox_agree).isChecked = false
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun checkUserId() {
        val userId = findViewById<EditText>(R.id.edit_id).text.toString().trim()
        val retrofitService = RetrofitClient.getRetrofitService()

        retrofitService.checkUserId(userId).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                val responseBody = response.body()
                val message = when {
                    response.isSuccessful -> responseBody?.get("message") ?: "알 수 없는 오류"
                    response.code() == 409 -> "이미 사용 중인 아이디입니다."
                    else -> "오류: ${response.code()} ${response.message()}"
                }
                Toast.makeText(this@SignUpActivity, message, Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                val errorMessage = when (t) {
                    is SocketTimeoutException -> "서버 응답 시간을 초과했습니다."
                    is ConnectException -> "서버에 연결할 수 없습니다. 서버가 실행 중인지 확인하세요."
                    else -> "네트워크 오류: ${t.message}"
                }
                Toast.makeText(this@SignUpActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkUserEmail() {
        val userEmail = findViewById<EditText>(R.id.edit_email).text.toString().trim()
        val retrofitService = RetrofitClient.getRetrofitService()

        retrofitService.checkUserEmail(userEmail).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                val responseBody = response.body()
                val message = when {
                    response.isSuccessful -> responseBody?.get("message") ?: "알 수 없는 오류"
                    response.code() == 409 -> "이미 사용 중인 이메일입니다."
                    else -> "오류: ${response.code()} ${response.message()}"
                }
                Toast.makeText(this@SignUpActivity, message, Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                val errorMessage = when (t) {
                    is SocketTimeoutException -> "서버 응답 시간을 초과했습니다."
                    is ConnectException -> "서버에 연결할 수 없습니다. 서버가 실행 중인지 확인하세요."
                    else -> "네트워크 오류: ${t.message}"
                }
                Toast.makeText(this@SignUpActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun signUp() {
        // 입력 필드에서 사용자 정보 가져오기 (공백 제거)
        val userId = findViewById<EditText>(R.id.edit_id).text.toString().trim()
        val userPw = findViewById<EditText>(R.id.edit_pw).text.toString().trim()
        val userEmail = findViewById<EditText>(R.id.edit_email).text.toString().trim()
        val userName = findViewById<EditText>(R.id.edit_name).text.toString().trim()
        val userNickname = findViewById<EditText>(R.id.edit_nickname).text.toString().trim()
        val userPhone = findViewById<EditText>(R.id.edit_phone).text.toString().trim()

        // 입력 값 검증
        if (userId.isEmpty() || userPw.isEmpty() || userEmail.isEmpty() || userName.isEmpty() || userNickname.isEmpty() || userPhone.isEmpty()) {
            Toast.makeText(this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // RetrofitService 인스턴스 가져오기
        val retrofitService = RetrofitClient.getRetrofitService()
        // 요청 바디 생성
        val requestBody = mapOf(
            "userId" to userId,
            "userPw" to userPw,
            "userEmail" to userEmail,
            "userName" to userName,
            "userNickname" to userNickname,
            "userPhone" to userPhone
        )

        // 회원가입 API 호출
        retrofitService.signup(requestBody)
            .enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                    if (response.isSuccessful) {
                        // 회원가입 성공 처리
                        val responseBody = response.body()
                        val message = responseBody?.get("message") ?: "회원가입이 완료되었습니다."
                        Toast.makeText(this@SignUpActivity, message, Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        // 회원가입 실패 처리
                        val errorMessage = when (response.code()) {
                            400 -> "잘못된 요청입니다."
                            409 -> "중복된 아이디나 이메일입니다."
                            else -> "회원가입 실패: ${response.code()} ${response.message()}"
                        }
                        Toast.makeText(this@SignUpActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                    // 네트워크 오류 처리
                    val errorMessage = when (t) {
                        is SocketTimeoutException -> "서버 응답 시간을 초과했습니다."
                        is ConnectException -> "서버에 연결할 수 없습니다. 서버가 실행 중인지 확인하세요."
                        else -> "네트워크 오류: ${t.message}"
                    }
                    Toast.makeText(this@SignUpActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // 뒤로 가기 버튼을 눌렀을 때 SignUpActivity 종료
        finish()
    }
}
