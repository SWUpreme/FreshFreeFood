package com.example.fffroject.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
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
    lateinit var text_envcontri: TextView
    // 환경 기여도 레벨
    var envpercent = 0
    var envlevel = 0

    // 이미지뷰 설정
    lateinit var img_tree1: ImageView
    lateinit var img_tree2: ImageView
    lateinit var img_tree3: ImageView
    lateinit var img_tree4: ImageView
    lateinit var img_tree5: ImageView
    lateinit var img_tree6: ImageView
    lateinit var img_tree7: ImageView
    lateinit var img_tree8: ImageView
    lateinit var img_tree9: ImageView
    lateinit var img_topbedge: ImageView


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

        // 프로그래스바 부분 연결
        progress_envlevel = view.findViewById(R.id.progEnvLev)
        text_envcontri = view.findViewById(R.id.textEnvContri)

        // 이미지뷰 부분(나무들) 연결
        img_tree1 = view.findViewById(R.id.imgTree1)
        img_tree2 = view.findViewById(R.id.imgTree2)
        img_tree3 = view.findViewById(R.id.imgTree3)
        img_tree4 = view.findViewById(R.id.imgTree4)
        img_tree5 = view.findViewById(R.id.imgTree5)
        img_tree6 = view.findViewById(R.id.imgTree6)
        img_tree7 = view.findViewById(R.id.imgTree7)
        img_tree8 = view.findViewById(R.id.imgTree8)
        img_tree9 = view.findViewById(R.id.imgTree9)
        img_topbedge = view.findViewById(R.id.imgTopBedge)

        // 기본 색 #9F9F9F로 설정 (GRAY)
        img_tree1.setColorFilter(Color.parseColor("#9F9F9F"))
        img_tree2.setColorFilter(Color.parseColor("#9F9F9F"))
        img_tree3.setColorFilter(Color.parseColor("#9F9F9F"))
        img_tree4.setColorFilter(Color.parseColor("#9F9F9F"))
        img_tree5.setColorFilter(Color.parseColor("#9F9F9F"))
        img_tree6.setColorFilter(Color.parseColor("#9F9F9F"))
        img_tree7.setColorFilter(Color.parseColor("#9F9F9F"))
        img_tree8.setColorFilter(Color.parseColor("#9F9F9F"))
        img_tree9.setColorFilter(Color.parseColor("#9F9F9F"))


        // 파이어베이스에서 환경 기여도 가져와서 설정
        loadEnvLev()

        // 환경 기여도에 따른 뱃지 보여주기
        setTreeImage()

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
                        text_envcontri.text = envpercent.toString() + "/50"
                    }
                }

        }
    }

    fun setTreeImage() {
        // 트리 이미지 세팅해주기 (먼저 envlevel부터 불러오기)
        if (user != null) {
            firestore?.collection("user")?.document(user!!.uid)
                ?.get()?.addOnSuccessListener { document ->
                    if (document != null) {
                        envlevel = document?.data?.get("envlevel").toString().toInt()
                        when(envlevel) {
                            1 -> {
                                img_tree1.setColorFilter(null)
                                img_topbedge.setImageResource(R.drawable.eco_tree1)}
                            2 -> {img_tree1.setColorFilter(null)
                                img_tree2.setColorFilter(null)
                                img_topbedge.setImageResource(R.drawable.eco_tree2)}
                            3 -> {img_tree1.setColorFilter(null)
                                img_tree2.setColorFilter(null)
                                img_tree3.setColorFilter(null)
                                img_topbedge.setImageResource(R.drawable.eco_tree3)}
                            4 -> {img_tree1.setColorFilter(null)
                                img_tree2.setColorFilter(null)
                                img_tree3.setColorFilter(null)
                                img_tree4.setColorFilter(null)
                                img_topbedge.setImageResource(R.drawable.eco_tree4)}
                            5 -> {img_tree1.setColorFilter(null)
                                img_tree2.setColorFilter(null)
                                img_tree3.setColorFilter(null)
                                img_tree4.setColorFilter(null)
                                img_tree5.setColorFilter(null)
                                img_topbedge.setImageResource(R.drawable.eco_tree5)}
                            6 -> {img_tree1.setColorFilter(null)
                                img_tree2.setColorFilter(null)
                                img_tree3.setColorFilter(null)
                                img_tree4.setColorFilter(null)
                                img_tree5.setColorFilter(null)
                                img_tree6.setColorFilter(null)
                                img_topbedge.setImageResource(R.drawable.eco_tree6)}
                            7 -> {img_tree1.setColorFilter(null)
                                img_tree2.setColorFilter(null)
                                img_tree3.setColorFilter(null)
                                img_tree4.setColorFilter(null)
                                img_tree5.setColorFilter(null)
                                img_tree6.setColorFilter(null)
                                img_tree7.setColorFilter(null)
                                img_topbedge.setImageResource(R.drawable.eco_tree7)}
                            8 -> {img_tree1.setColorFilter(null)
                                img_tree2.setColorFilter(null)
                                img_tree3.setColorFilter(null)
                                img_tree4.setColorFilter(null)
                                img_tree5.setColorFilter(null)
                                img_tree6.setColorFilter(null)
                                img_tree7.setColorFilter(null)
                                img_tree8.setColorFilter(null)
                                img_topbedge.setImageResource(R.drawable.eco_tree8)}
                            9 -> {img_tree1.setColorFilter(null)
                                img_tree2.setColorFilter(null)
                                img_tree3.setColorFilter(null)
                                img_tree4.setColorFilter(null)
                                img_tree5.setColorFilter(null)
                                img_tree6.setColorFilter(null)
                                img_tree7.setColorFilter(null)
                                img_tree8.setColorFilter(null)
                                img_tree9.setColorFilter(null)
                                img_topbedge.setImageResource(R.drawable.eco_tree9)}
                            else -> { }
                        }
                    }
                }
        }
    }
}