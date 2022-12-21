package com.example.fffroject

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
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

    // intent값 받아온 것
    var giver : String? = null
    var givername :  String? = null
    var postindex : String? = null

    // 후기 보내기 버튼
    lateinit var btn_review_send: Button
    // 나눔자 이름
    lateinit var text_giver : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sharepoint)

        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()

        // intent와 연결(FridgeFragment에서 넘겨 준 것들)
        giver = intent.getStringExtra("opponentId")    // 공유해준사람의 uid
        givername = intent.getStringExtra("oppoentNickname")    // 공유해준사람의 닉네임
        postindex = intent.getStringExtra("postIndex")    // 포스트 인덱스

        // 공유해준사람 이름 연결
        text_giver = findViewById(R.id.textShareName)
        text_giver.setText(givername)

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
            Toast.makeText(this, "후기가 전송되었습니다.", Toast.LENGTH_SHORT).show()
            // 별점 보내기 완료로 변경
            firestore?.collection("post")?.document(postindex.toString())
                ?.update(
                    "pointDone", true
                )
                ?.addOnSuccessListener {}
                ?.addOnFailureListener {}
            pointUp(giver.toString(), point)
            val intent = Intent(this, ChatListActivity::class.java)
            ContextCompat.startActivity(this, intent, null)
            finish()
        }
    }

    fun pointUp(giver : String, point : Int) {
        firestore?.collection("user")?.document(giver)
            ?.get()?.addOnSuccessListener { document ->
                var sharepoint = 0
                if (document != null) {
                    sharepoint = document?.data?.get("sharepoint").toString().toInt()
                    sharepoint += point
                    // sharepoint가 30이 넘는 경우
                    var rest = 0
                    if (sharepoint > 29) {
                        rest = sharepoint % 30
                        firestore?.collection("user")?.document(giver)
                            ?.update("sharepoint", rest)
                            ?.addOnSuccessListener { }
                            ?.addOnFailureListener { }
                        firestore?.collection("user")?.document(giver)
                            ?.update("envlevel", FieldValue.increment(1))
                            ?.addOnSuccessListener { }
                            ?.addOnFailureListener { }
                    }
                    else {
                        firestore?.collection("user")?.document(giver)
                            ?.update("sharepoint", sharepoint)
                            ?.addOnSuccessListener { }
                            ?.addOnFailureListener { }
                    }

                }
            }
    }
}