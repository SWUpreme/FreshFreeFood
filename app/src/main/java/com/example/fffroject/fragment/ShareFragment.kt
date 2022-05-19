package com.example.fffroject.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.fffroject.AuthActivity
import com.example.fffroject.R
import com.example.fffroject.SharePostActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class ShareFragment : Fragment() {
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    lateinit var btn_addShare: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_share, container, false)

        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()

        btn_addShare = view.findViewById(R.id.btnShareAdd)

        // 나눔 게시글 추가(게시글 추가 액티비티로 이동)
        btn_addShare?.setOnClickListener {
            val intent = Intent(activity, SharePostActivity::class.java)
            startActivity(intent)
            //activity?.let { ContextCompat.startActivity(it, intent, null) }
        }


        return view
    }
}