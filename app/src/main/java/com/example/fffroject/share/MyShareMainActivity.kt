package com.example.fffroject.share

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fffroject.R
import com.example.fffroject.databinding.ActivityMainBinding
import com.example.fffroject.databinding.ActivityMyShareMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_my_share_main.*

class MyShareMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyShareMainBinding
    private val tabTitleArray = arrayOf(
        "나눔중",
        "나눔완료"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyShareMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        viewPager.adapter = MyShareViewPagerAdapter(supportFragmentManager, lifecycle)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitleArray[position]
        }.attach()
    }
}