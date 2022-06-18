package com.example.fffroject.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.fffroject.databinding.FragmentEnterBinding
class EnterFragment : Fragment() {
    private var _binding: FragmentEnterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentEnterBinding.inflate(inflater, container, false)
        val view = binding.root

        // 닉네임이 공백일 경우 버튼 비활성화
        binding.etNickname.addTextChangedListener { text ->
            binding.btnEnter.isEnabled = text.toString().replace(" ", "") != ""
        }

        binding.btnEnter.setOnClickListener {
            // 입력한 닉네임을 Bundle에 담아 ChatFragment로 보냄
            val nickname = binding.etNickname.text.toString()
            val bundle = Bundle()
            bundle.putString("nickname", nickname)
            // ChatFragment로 이동
            (activity as Main2Activity).replaceFragment(bundle)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}