package com.example.fffroject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userdelete)

        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()

        btn_check = findViewById(R.id.btnCheck)
        var check = false

        btn_check.setOnClickListener {
            if (check == false){
                btn_check.setBackgroundResource(R.drawable.checkbox_blue)
                check = true
            }
            else {
                btn_check.setBackgroundResource(R.drawable.checkbox_grey)
                check = false
            }
        }

    }
}