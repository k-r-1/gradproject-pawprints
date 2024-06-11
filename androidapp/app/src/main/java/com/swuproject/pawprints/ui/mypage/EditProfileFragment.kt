package com.swuproject.pawprints.ui.mypage

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.swuproject.pawprints.R
import com.swuproject.pawprints.databinding.FragmentEditProfileBinding
import com.swuproject.pawprints.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private var isEmailChecked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.navigation_mypage)
            }
        })

        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        val userId = sharedPreferences.getString("user_id", "")
        val userName = sharedPreferences.getString("user_name", "")
        val userEmail = sharedPreferences.getString("user_email", "")
        val userNickname = sharedPreferences.getString("user_nickname", "")
        val userPhone = sharedPreferences.getString("user_phone", "")

        binding.editId.setText(userId)
        binding.editName.setText(userName)
        binding.editEmail.setText(userEmail)
        binding.editNickname.setText(userNickname)
        binding.editPhone.setText(userPhone)

        binding.editEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isEmailChecked = false
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnCheckEmail.setOnClickListener {
            val email = binding.editEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                RetrofitClient.getRetrofitService().checkUserEmail(email).enqueue(object : Callback<Map<String, String>> {
                    override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                        if (response.isSuccessful && response.body()?.get("message") == "사용 가능한 이메일입니다.") {
                            isEmailChecked = true
                            Toast.makeText(requireContext(), "사용 가능한 이메일입니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            isEmailChecked = false
                            Toast.makeText(requireContext(), "이미 사용 중인 이메일입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                        isEmailChecked = false
                        Toast.makeText(requireContext(), "이메일 중복 확인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        binding.saveButton.setOnClickListener {
            val updatedName = binding.editName.text.toString().trim()
            val updatedEmail = binding.editEmail.text.toString().trim()
            val updatedNickname = binding.editNickname.text.toString().trim()
            val updatedPhone = binding.editPhone.text.toString().trim()

            if (updatedName.isEmpty() || updatedEmail.isEmpty() || updatedNickname.isEmpty() || updatedPhone.isEmpty()) {
                Toast.makeText(requireContext(), "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isEmailChecked && updatedEmail != userEmail) {
                Toast.makeText(requireContext(), "이메일 중복 확인을 해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 이메일 중복 확인 다시 한번 수행
            if (updatedEmail != userEmail) {
                RetrofitClient.getRetrofitService().checkUserEmail(updatedEmail).enqueue(object : Callback<Map<String, String>> {
                    override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                        if (response.isSuccessful && response.body()?.get("message") == "사용 가능한 이메일입니다.") {
                            saveUserProfile(userId, updatedName, updatedEmail, updatedNickname, updatedPhone)
                        } else {
                            Toast.makeText(requireContext(), "이미 사용 중인 이메일입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                        Toast.makeText(requireContext(), "이메일 중복 확인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                saveUserProfile(userId, updatedName, updatedEmail, updatedNickname, updatedPhone)
            }
        }
    }

    private fun saveUserProfile(userId: String?, updatedName: String, updatedEmail: String, updatedNickname: String, updatedPhone: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        val userUpdates = mapOf(
            "userId" to userId.orEmpty(),
            "userName" to updatedName,
            "userEmail" to updatedEmail,
            "userNickname" to updatedNickname,
            "userPhone" to updatedPhone
        )

        RetrofitClient.getRetrofitService().updateUser(userUpdates).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    with(sharedPreferences.edit()) {
                        putString("user_name", updatedName)
                        putString("user_email", updatedEmail)
                        putString("user_nickname", updatedNickname)
                        putString("user_phone", updatedPhone)
                        apply()
                    }
                    Toast.makeText(requireContext(), "정보가 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "정보 수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "정보 수정 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}