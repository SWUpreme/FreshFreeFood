package com.example.fffroject.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fffroject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class EnvlevelFragment: Fragment() {
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    lateinit var progress_envlevel: ProgressBar
    // 환경 기여도 레벨
    var envpercent = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view =
            LayoutInflater.from(activity).inflate(R.layout.fragment_envlevel, container, false)

        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()

        progress_envlevel = view.findViewById(R.id.progEnvLev)


        // 파이어베이스에서 환경 기여도 가져와서 설정
        loadEnvLev()


        return view
    }

    fun loadEnvLev() {
        // 환경 기여도 불러오기
        // 컬럼->다큐먼트->필드 에서 해당하는 필드의 값을 불러오는 방법
        if (user != null) {
            firestore?.collection("user")?.document(user!!.uid)
                ?.get()?.addOnSuccessListener { document ->
                    if (document != null) {
                        envpercent = document?.data?.get("contribution").toString().toInt()
                        //Toast.makeText(context, envlevel.toString(), Toast.LENGTH_SHORT).show()
                        // 해당 위치(if문 내부)를 벗어나면 값이 초기화되므로 내부에서 해결해준다.
                        progress_envlevel.progress = envpercent
                    }
                }

        }
    }
}