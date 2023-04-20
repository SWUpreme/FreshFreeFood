package com.example.fffroject

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class UserDeleteActivity : AppCompatActivity(){

    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user : FirebaseUser? = null

    lateinit var btn_check : Button
    lateinit var text_check : TextView
    lateinit var btn_checkok : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userdelete)

        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()

        btn_check = findViewById(R.id.btnCheck)
        btn_checkok = findViewById(R.id.btnDeleteOk)
        text_check = findViewById(R.id.textCheck)
        var check = false

        btn_check.setOnClickListener {
            if (check == false){
                btn_check.setBackgroundResource(R.drawable.checkbox_blue)
                btn_checkok.setBackgroundResource(R.drawable.btn_checkok_blue)
                text_check.setTextColor(Color.parseColor("#30353D"))
                check = true
            }
            else {
                btn_check.setBackgroundResource(R.drawable.checkbox_grey)
                btn_checkok.setBackgroundResource(R.drawable.btn_checkok_grey)
                text_check.setTextColor(Color.parseColor("#95979B"))
                check = false
            }
        }

        btn_checkok.setOnClickListener {
            // 체크박스 확인했을경우
            if (check == true) {
                Toast.makeText(this, "click", Toast.LENGTH_SHORT).show()
            }
        }

    }
}