package com.swuproject.pawprints.ui.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.swuproject.pawprints.R
import com.swuproject.pawprints.common.Utils
import com.swuproject.pawprints.databinding.FragmentMypageBinding
import com.swuproject.pawprints.ui.LoginActivity

class MypageFragment : Fragment() {

    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        val root: View = binding.root

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
            findNavController().navigate(R.id.action_mypageFragment_to_MyMissingReportFragment)
        }
        binding.btnMySightingReport.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_MySightingReportFragment)
        }
        binding.btnFaq.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_faqFragment)
        }
        binding.btnContactUs.setOnClickListener {
            findNavController().navigate(R.id.action_mypageFragment_to_contactUsFragment)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}