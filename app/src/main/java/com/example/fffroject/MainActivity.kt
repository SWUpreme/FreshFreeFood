package com.example.fffroject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.fffroject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        if(!FFFroject.checkAuth()){
            binding.textLogin.visibility = View.GONE // 로그인 해 주세요 텍스트 보이게
        } else {
            binding.textLogin.visibility = View.VISIBLE    // 로그인 해 주세요 텍스트 안보이게
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_login, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        startActivity(Intent(this, AuthActivity::class.java))
//        return super.onOptionsItemSelected(item)
//    }
}