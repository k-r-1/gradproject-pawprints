package com.swuproject.pawprints.ui.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.swuproject.pawprints.R
import com.swuproject.pawprints.common.Utils
import com.swuproject.pawprints.databinding.FragmentMypageBinding
import com.swuproject.pawprints.network.RetrofitClient
import com.swuproject.pawprints.network.RetrofitService
import com.swuproject.pawprints.ui.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MypageFragment : Fragment() {

    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!
    private lateinit var retrofitService: RetrofitService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        retrofitService = RetrofitClient.getRetrofitService()

        // Utils를 사용하여 SharedPreferences에서 사용자 이름 가져오기
        val userName = Utils.getUserName(requireContext())

        // userName이 null이 아닌 경우 text_name에 표시
        if (userName != null) {
            binding.textName.text = userName
        } else {
            binding.textName.text = "Unknown"
        }

        // 버튼 클릭 이벤트 리스너 설정
        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_editProfileFragment)
        }
        binding.btnEditPetInfo.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_editPetInfoFragment)
        }
        binding.btnMyMissingReport.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_MyLostReportFragment)
        }
        binding.btnMySightingReport.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_MySightReportFragment)
        }
        binding.btnFaq.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_faqFragment)
        }
        binding.btnContactUs.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_contactUsFragment)
        }

        binding.btnDeleteAccount.setOnClickListener {
            showDeleteAccountDialog()
        }

        binding.btnContactUs.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822" // 이메일 앱 필터링을 위한 MIME 타입
                putExtra(Intent.EXTRA_EMAIL, arrayOf("juwon_0715@swu.ac.kr")) // 수신인 이메일 주소
                putExtra(Intent.EXTRA_SUBJECT, "[발도장] 문의사항") // 기본 제목 설정
                putExtra(Intent.EXTRA_TEXT, "문의 내용을 작성해주세요.") // 기본 메시지 내용 설정
            }
            try {
                startActivity(Intent.createChooser(emailIntent, "이메일 앱을 선택하세요")) // 이메일 앱 선택 창 표시
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "이메일 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 로그아웃 버튼 클릭 이벤트 리스너 설정
        binding.btnLogout.setOnClickListener {
            val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear() // 모든 저장된 데이터 삭제
            editor.apply()

            // 로그인 액티비티로 이동
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)

            // 현재 액티비티 종료
            requireActivity().finish()
        }

        return root
    }
    private fun showDeleteAccountDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_delete_account, null)
        val editTextPassword = dialogView.findViewById<EditText>(R.id.editTextPassword)

        AlertDialog.Builder(requireContext())
            .setTitle("회원탈퇴")
            .setView(dialogView)
            .setPositiveButton("확인") { _, _ ->
                val password = editTextPassword.text.toString()
                if (password.isNotEmpty()) {
                    confirmPassword(password)
                } else {
                    Toast.makeText(requireContext(), "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun confirmPassword(password: String) {
        val userId = Utils.getUserId(requireContext()) ?: ""
        val requestBody = mapOf("userId" to userId, "userPw" to password)

        retrofitService.confirmPassword(requestBody).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // 비밀번호 확인 성공 시 탈퇴 여부 확인
                        AlertDialog.Builder(requireContext())
                            .setMessage("정말로 탈퇴하시겠습니까?")
                            .setPositiveButton("예") { _, _ ->
                                deleteUserAccount()
                            }
                            .setNegativeButton("아니오", null)
                            .show()
                    } else {
                        Toast.makeText(requireContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun deleteUserAccount() {
        val userId = Utils.getUserId(requireContext()) ?: return

        retrofitService.deleteUser(userId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "회원탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()

                    // 로그아웃 처리를 위한 SharedPreferences 초기화
                    val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().clear().apply()

                    // 로그인 화면으로 이동
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "회원탈퇴에 실패했습니다. 상태 코드: ${response.code()} 메시지: ${response.message()}",
                        Toast.LENGTH_LONG).show()
                    Log.e(
                        "회원탈퇴", // 로그의 태그
                        "회원탈퇴에 실패했습니다. 상태 코드: ${response.code()} 메시지: ${response.message()}"
                    )

                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}