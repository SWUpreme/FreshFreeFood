package com.example.fffroject

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class SharePointActivity : AppCompatActivity(){

    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null
    var user : FirebaseUser? = null

    // 별점 연동
    lateinit var btn_star1: Button
    lateinit var btn_star2: Button
    lateinit var btn_star3: Button
    lateinit var btn_star4: Button
    lateinit var btn_star5: Button

    //별점포인트
    var point = 1

    // 후기 보내기 버튼
    lateinit var btn_review_send: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sharepoint)

        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()

        // 별점 버튼 연결
        btn_star1 = findViewById(R.id.btnStar1)
        btn_star2 = findViewById(R.id.btnStar2)
        btn_star3 = findViewById(R.id.btnStar3)
        btn_star4 = findViewById(R.id.btnStar4)
        btn_star5 = findViewById(R.id.btnStar5)

        // 후기 보내기 버튼 연결
        btn_review_send = findViewById(R.id.btnReviewSend)

        // 기본 별점 1점으로 설정
        btn_star1.setBackgroundResource(R.drawable.ic_btn_star)
        btn_star2.setBackgroundResource(R.drawable.btn_star_gray)
        btn_star3.setBackgroundResource(R.drawable.btn_star_gray)
        btn_star4.setBackgroundResource(R.drawable.btn_star_gray)
        btn_star5.setBackgroundResource(R.drawable.btn_star_gray)

        btn_star1.setOnClickListener {
            btn_star1.setBackgroundResource(R.drawable.ic_btn_star)
            btn_star2.setBackgroundResource(R.drawable.btn_star_gray)
            btn_star3.setBackgroundResource(R.drawable.btn_star_gray)
            btn_star4.setBackgroundResource(R.drawable.btn_star_gray)
            btn_star5.setBackgroundResource(R.drawable.btn_star_gray)
            Toast.makeText(this, "고마운 나눔자에게 별은 한 개 이상 주세요!", Toast.LENGTH_SHORT).show()
            point = 1
        }

        btn_star2.setOnClickListener {
            btn_star1.setBackgroundResource(R.drawable.ic_btn_star)
            btn_star2.setBackgroundResource(R.drawable.ic_btn_star)
            btn_star3.setBackgroundResource(R.drawable.btn_star_gray)
            btn_star4.setBackgroundResource(R.drawable.btn_star_gray)
            btn_star5.setBackgroundResource(R.drawable.btn_star_gray)
            point = 2
        }

        btn_star3.setOnClickListener {
            btn_star1.setBackgroundResource(R.drawable.ic_btn_star)
            btn_star2.setBackgroundResource(R.drawable.ic_btn_star)
            btn_star3.setBackgroundResource(R.drawable.ic_btn_star)
            btn_star4.setBackgroundResource(R.drawable.btn_star_gray)
            btn_star5.setBackgroundResource(R.drawable.btn_star_gray)
            point = 3
        }

        btn_star4.setOnClickListener {
            btn_star1.setBackgroundResource(R.drawable.ic_btn_star)
            btn_star2.setBackgroundResource(R.drawable.ic_btn_star)
            btn_star3.setBackgroundResource(R.drawable.ic_btn_star)
            btn_star4.setBackgroundResource(R.drawable.ic_btn_star)
            btn_star5.setBackgroundResource(R.drawable.btn_star_gray)
            point = 4
        }

        btn_star5.setOnClickListener {
            btn_star1.setBackgroundResource(R.drawable.ic_btn_star)
            btn_star2.setBackgroundResource(R.drawable.ic_btn_star)
            btn_star3.setBackgroundResource(R.drawable.ic_btn_star)
            btn_star4.setBackgroundResource(R.drawable.ic_btn_star)
            btn_star5.setBackgroundResource(R.drawable.ic_btn_star)
            point = 5
        }

        btn_review_send.setOnClickListener {
            Toast.makeText(this, point.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}