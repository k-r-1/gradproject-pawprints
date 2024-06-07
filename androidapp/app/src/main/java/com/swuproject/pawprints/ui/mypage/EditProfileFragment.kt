package com.swuproject.pawprints.ui.mypage

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.swuproject.pawprints.R
import com.swuproject.pawprints.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
