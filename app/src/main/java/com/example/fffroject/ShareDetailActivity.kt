package com.example.fffroject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.fffroject.databinding.ActivitySharedetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sharedetail.*

class ShareDetailActivity: AppCompatActivity()  {

    //파이어스토어
    var auth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    // 바인딩 객체
    lateinit var binding: ActivitySharedetailBinding
    // 툴바
    lateinit var toolbar_sharedetail: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 바인딩 객체 획득
        binding = ActivitySharedetailBinding.inflate(layoutInflater)
        // 액티비티 화면 출력
        setContentView(binding.root)

        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        db = FirebaseFirestore.getInstance()

        // 상단 툴바 사용
        toolbar_sharedetail = findViewById(R.id.toolbSharedetail)

        // 메세지 버튼
        toolbSharedetail.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.btnGotoMessage -> {
                    true
                }
                else -> false
            }
        }

    }
}