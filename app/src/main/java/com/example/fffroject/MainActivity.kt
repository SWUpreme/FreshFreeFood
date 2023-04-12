package com.example.fffroject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toolbar
import com.example.fffroject.fragment.EnvlevelFragment
import com.example.fffroject.fragment.FridgeFragment
import com.example.fffroject.fragment.MypageFragment
import com.example.fffroject.fragment.ShareFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage

class MainActivity : AppCompatActivity() {
    // 파이어스토어
    var auth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    var storage: FirebaseStorage? = null

    lateinit var bottom_navigation: BottomNavigationView
    val TAG = "chatToken"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // [minjeong] fcm 토큰 가져오고 저장하기
        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        db = FirebaseFirestore.getInstance()
        // 파이어스토리지 인스턴스 초기화
        storage = FirebaseStorage.getInstance()
        getFCMToken()

        bottom_navigation = findViewById(R.id.bottomNavigationView) //as BottomNavigationView

        // bottomNavigationView 설정
        bottom_navigation.setOnItemSelectedListener { item->
            when(item.itemId){
                R.id.tab_share -> {                    val shareFragment = ShareFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, shareFragment).commit()
                    return@setOnItemSelectedListener true
                }
                R.id.tab_fridge -> {
                    val fridgeFragment = FridgeFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, fridgeFragment).commit()
                    return@setOnItemSelectedListener true
                }
                R.id.tab_mypage -> {
                    val mypageFragment = MypageFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, mypageFragment).commit()
                    return@setOnItemSelectedListener true
                }
                R.id.tab_envlevel -> {
                    val envlevelFragment = EnvlevelFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, envlevelFragment).commit()
                    return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener false
        }
        val fridgeFragment = FridgeFragment()
        supportFragmentManager.beginTransaction().add(R.id.main_content, fridgeFragment).commit()
    }

    // [minjeong] fcm 토큰 가져오기
    private fun getFCMToken(): String?{
        var token: String? = null
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            token = task.result

            // Log and toast
            Log.d(TAG, "FCM Token is ${token}")

            db?.collection("user")?.document(user?.uid.toString())
                ?.update("fcmToken", token)
                ?.addOnSuccessListener {
                    Log.d(TAG, "Success FCM Token is ${token}")
                }
        })


        return token
    }

    // [minjeong] fcm 토큰 db에 저장하기
    private fun setFCMToken(token: String){
        Log.d(TAG, "tokeeknoeno Token is ${token}")
        // 받아온 토큰을 db에 저장
        db?.collection("user")?.document(user?.uid.toString())
            ?.set("fcmToken" to token)
            ?.addOnSuccessListener {}
    }

}