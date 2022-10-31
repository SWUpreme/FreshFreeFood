package com.example.fffroject.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.fffroject.*
import com.example.fffroject.databinding.FragmentMypageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MypageFragment : Fragment() {
    //    fun newInstance() : MypageFragment {
//        return MypageFragment()
//    }
    lateinit var binding: FragmentMypageBinding

    var auth: FirebaseAuth? = null
    var user : FirebaseUser? = null

    lateinit var btn_logout: Button
    lateinit var btn_mypage_share: Button
    lateinit var btn_mypage_message: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_mypage, container, false)

        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        binding = FragmentMypageBinding.inflate(layoutInflater)
        btn_logout = view.findViewById(R.id.btnLogout)

        // 버튼 연동
       btn_mypage_share = view.findViewById(R.id.btnMypageShare)
       btn_mypage_message = view.findViewById(R.id.btnMypageMessage)

        // 로그아웃 처리
        btn_logout.setOnClickListener {
            Toast.makeText(context,"로그아웃누름", Toast.LENGTH_SHORT).show()
            auth?.signOut()
            // FFFroject.email = null
            val intent = Intent(activity, AuthActivity::class.java)
            activity?.let { ContextCompat.startActivity(it, intent, null) }
            // startActivity(Intent(activity, AuthActivity::class.java))
        }

        // 나의 나눔 버튼 눌렀을 경우
        btn_mypage_share.setOnClickListener {
            val intent = Intent(view.context, MyShareActivity::class.java)
            ContextCompat.startActivity(view.context, intent, null)
        }

        // 메시지 버튼을 눌렀을 경우
        btn_mypage_message.setOnClickListener {
            val intent = Intent(view.context, ChatListActivity::class.java)
            ContextCompat.startActivity(view.context, intent, null)
        }

        // 로그아웃 처리
//        binding.btnLogoutGoogle.setOnClickListener {
            // 구글 계정 로그아웃
//            Toast.makeText(
//                baseContext, "로그아웃 누름",
//                Toast.LENGTH_SHORT
//            ).show()
//            Toast.makeText(context,"로그아웃누름", Toast.LENGTH_SHORT).show()
//            FFFroject.auth.signOut()
//            FFFroject.email = null
//            //changeVisibility("logout")
//            //val intent = Intent(activity, AuthActivity::class.java)
//            //activity?.let { ContextCompat.startActivity(it, intent, null) }
//            startActivity(Intent(activity, AuthActivity::class.java))
        //}
        return view
    }
}