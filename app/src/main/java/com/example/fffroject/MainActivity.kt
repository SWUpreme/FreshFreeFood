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

}