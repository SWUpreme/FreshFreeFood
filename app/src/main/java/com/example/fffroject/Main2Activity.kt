package com.example.fffroject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fffroject.databinding.ActivityMain2Binding
import com.example.fffroject.fragment.ChatFragment
import com.example.fffroject.fragment.EnterFragment

class Main2Activity : AppCompatActivity() {
    private lateinit var binding : ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 앱 구동시 EnterFragment 표시
        supportFragmentManager.beginTransaction()
            .replace(R.id.layout_frame, EnterFragment())
            .commit()
    }

    // ChatFragment로 프래그먼트 교체 (EnterFragment에서 호출할 예정)
    fun replaceFragment(bundle: Bundle) {
        val destination = ChatFragment()
        destination.arguments = bundle      // 닉네임을 받아옴
        supportFragmentManager.beginTransaction()
            .replace(R.id.layout_frame, destination)
            .commit()
    }
}