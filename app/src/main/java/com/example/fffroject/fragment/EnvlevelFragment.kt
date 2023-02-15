package com.example.fffroject.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.example.fffroject.R
import com.example.fffroject.databinding.DialogFixnicknameBinding
import com.example.fffroject.databinding.DialogTreegradeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_envlevel.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EnvlevelFragment: Fragment() {
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    lateinit var progress_envlevel: ProgressBar
    lateinit var text_envcontri: TextView
    lateinit var text_nowmonth : TextView
    lateinit var text_fridgeenv: TextView

    // 현재 월 불러오기
    var logindate = ""

    // 환경 기여도 레벨
    var envpercent = 0
    var envlevel = 0
    var sharepoint = 0

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
    lateinit var img_gradebox: ImageView
    lateinit var env_back: ConstraintLayout
    lateinit var recycler: LinearLayout
    lateinit var text_grade: TextView
    lateinit var text_grade2: TextView
    lateinit var text_grade3: TextView

    // 나무 등급 보기 버튼
    lateinit var btn_show_grade: Button


    @RequiresApi(Build.VERSION_CODES.O)
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

        // 말풍선 월 연결
        text_nowmonth = view.findViewById(R.id.textNowMonth)
        // 개인 환경기여도 연결
        text_fridgeenv = view.findViewById(R.id.textFridgeEnv)

        // 프로그래스바 부분 연결
        progress_envlevel = view.findViewById(R.id.progEnvLev)
        text_envcontri = view.findViewById(R.id.textEnvContri)

        // 등급 보여주는 버튼 연결
        btn_show_grade = view.findViewById(R.id.btnGrade)

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

        // 나무 등급 보여주는 이미지, 텍스트 연결
        img_gradebox = view.findViewById(R.id.imgTreeGrade)
        text_grade = view.findViewById(R.id.textGrade)
        text_grade2 = view.findViewById(R.id.textGrade2)
        text_grade3 = view.findViewById(R.id.textGrade3)
        // 다른 부분 클릭시 등급표 안보이게 해주기 위해 영역 연결
        env_back = view.findViewById(R.id.envBack)
        recycler = view.findViewById(R.id.envLinear)
        // 처음엔 등급표 안보이게
        img_gradebox.visibility = View.INVISIBLE
        text_grade.visibility = View.INVISIBLE
        text_grade2.visibility = View.INVISIBLE
        text_grade3.visibility = View.INVISIBLE

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

        // 환경 기여도에 따른 뱃지 보여주기
        setTreeImage()

        // 달이 갱신되면 냉장고 털기 0으로 설정해주기
        setContri()

        // 파이어베이스에서 환경 기여도 가져와서 설정
        loadEnvLev()

        // 등급 보여주는 버튼을 눌렀을 때
        btn_show_grade.setOnClickListener {
            img_gradebox.visibility = View.VISIBLE
            text_grade.visibility = View.VISIBLE
            text_grade2.visibility = View.VISIBLE
            text_grade3.visibility = View.VISIBLE

            // 다른 배경 눌렀을 때 등급표 안보이게
            env_back.setOnClickListener {
                img_gradebox.visibility = View.INVISIBLE
                text_grade.visibility = View.INVISIBLE
                text_grade2.visibility = View.INVISIBLE
                text_grade3.visibility = View.INVISIBLE
            }
            recycler.setOnClickListener {
                img_gradebox.visibility = View.INVISIBLE
                text_grade.visibility = View.INVISIBLE
                text_grade2.visibility = View.INVISIBLE
                text_grade3.visibility = View.INVISIBLE
            }
        }

        return view
    }

    fun loadEnvLev() {
        // 환경 기여도 불러오기
        // 컬럼->다큐먼트->필드 에서 해당하는 필드의 값을 불러오는 방법
        if (user != null) {
            firestore?.collection("user")?.document(user!!.uid)
                ?.get()?.addOnSuccessListener { document ->
                    if (document != null) {
                        envpercent = document?.data?.get("eatCount").toString().toInt()
                        sharepoint = document?.data?.get("sharepoint").toString().toInt()

                        // 해당 위치(if문 내부)를 벗어나면 값이 초기화되므로 내부에서 해결해준다.
                        progress_envlevel.progress = sharepoint
                        text_envcontri.text = sharepoint.toString() + "/30"
                        text_fridgeenv.text = envpercent.toString() + "회"
                    }
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setContri() {
        // 마지막 로그인 년.월 불러오기
        // 컬럼->다큐먼트->필드 에서 해당하는 필드의 값을 불러오는 방법
        if (user != null) {
            firestore?.collection("user")?.document(user!!.uid)
                ?.get()?.addOnSuccessListener { document ->
                    if (document != null) {
                        // 해당 위치(if문 내부)를 벗어나면 값이 초기화되므로 내부에서 해결해준다.
                        // 현재 년.월 받아오기
                        var nowdate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                        val nowTime = System.currentTimeMillis()
                        val timeformatter = SimpleDateFormat("yyyy.MM.dd.hh.mm.ss")
                        val dateTime = timeformatter.format(nowTime)
                        //var nowyear = nowdate.split(".").get(0)
                        var nowmonth = nowdate.split(".")?.get(1)
                        text_nowmonth.setText(nowmonth + "월!")
                        logindate = document?.data?.get("loginDate").toString()
                        logindate = logindate.split(".")?.get(0) + "." + logindate.split(".")?.get(1)
                        if ((nowdate.split(".").get(0) + "." + nowdate.split(".").get(1))!=logindate){
                            firestore?.collection("user")?.document(user!!.uid)
                                ?.update("eatCount", 0)
                                ?.addOnSuccessListener { loadEnvLev() }
                                ?.addOnFailureListener { }
                            firestore?.collection("user")?.document(user!!.uid)
                                ?.update("loginDate", dateTime)
                                ?.addOnSuccessListener { }
                                ?.addOnFailureListener { }
                            firestore?.collection("user")?.document(user!!.uid)
                                ?.update("updatedAt", dateTime)
                                ?.addOnSuccessListener { }
                                ?.addOnFailureListener { }
                        }
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
                                img_topbedge.setImageResource(R.drawable.eco_img_bedge1)}
                            2 -> {img_tree1.setColorFilter(null)
                                img_tree2.setColorFilter(null)
                                img_topbedge.setImageResource(R.drawable.eco_img_bedge2)}
                            3 -> {img_tree1.setColorFilter(null)
                                img_tree2.setColorFilter(null)
                                img_tree3.setColorFilter(null)
                                img_topbedge.setImageResource(R.drawable.eco_img_bedge4)}
                            4 -> {img_tree1.setColorFilter(null)
                                img_tree2.setColorFilter(null)
                                img_tree3.setColorFilter(null)
                                img_tree4.setColorFilter(null)
                                img_topbedge.setImageResource(R.drawable.eco_img_bedge3)}
                            5 -> {img_tree1.setColorFilter(null)
                                img_tree2.setColorFilter(null)
                                img_tree3.setColorFilter(null)
                                img_tree4.setColorFilter(null)
                                img_tree5.setColorFilter(null)
                                img_topbedge.setImageResource(R.drawable.eco_img_bedge5)}
                            6 -> {img_tree1.setColorFilter(null)
                                img_tree2.setColorFilter(null)
                                img_tree3.setColorFilter(null)
                                img_tree4.setColorFilter(null)
                                img_tree5.setColorFilter(null)
                                img_tree6.setColorFilter(null)
                                img_topbedge.setImageResource(R.drawable.eco_img_bedge6)}
                            7 -> {img_tree1.setColorFilter(null)
                                img_tree2.setColorFilter(null)
                                img_tree3.setColorFilter(null)
                                img_tree4.setColorFilter(null)
                                img_tree5.setColorFilter(null)
                                img_tree6.setColorFilter(null)
                                img_tree7.setColorFilter(null)
                                img_topbedge.setImageResource(R.drawable.eco_img_bedge7)}
                            8 -> {img_tree1.setColorFilter(null)
                                img_tree2.setColorFilter(null)
                                img_tree3.setColorFilter(null)
                                img_tree4.setColorFilter(null)
                                img_tree5.setColorFilter(null)
                                img_tree6.setColorFilter(null)
                                img_tree7.setColorFilter(null)
                                img_tree8.setColorFilter(null)
                                img_topbedge.setImageResource(R.drawable.eco_img_bedge8)}
                            9 -> {img_tree1.setColorFilter(null)
                                img_tree2.setColorFilter(null)
                                img_tree3.setColorFilter(null)
                                img_tree4.setColorFilter(null)
                                img_tree5.setColorFilter(null)
                                img_tree6.setColorFilter(null)
                                img_tree7.setColorFilter(null)
                                img_tree8.setColorFilter(null)
                                img_tree9.setColorFilter(null)
                                img_topbedge.setImageResource(R.drawable.eco_img_bedge9)}
                            else -> {  img_tree1.setColorFilter(null)
                                img_topbedge.setImageResource(R.drawable.eco_img_bedge1)}
                        }
                    }
                }
        }
    }
}