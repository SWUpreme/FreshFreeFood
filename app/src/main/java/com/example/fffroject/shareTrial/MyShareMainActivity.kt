package com.example.fffroject.shareTrial

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.example.fffroject.R
import com.example.fffroject.share.MySharePageAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_my_share_main.*
import kotlinx.android.synthetic.main.tab_myshare_button.view.*

class MyShareMainActivity : AppCompatActivity() {
    private lateinit var mContext : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mContext = applicationContext
        initViewPager() // 뷰페이저와 어댑터 장착
    }

    private fun createView(tabName: String): View {
        var tabView = LayoutInflater.from(mContext).inflate(R.layout.tab_myshare_button, null)

        tabView.tab_text.text = tabName
        when (tabName) {
            "찾기" -> {
                return tabView
            }
            "사진" -> {
                return tabView
            }
            "전화" -> {
                return tabView
            }
            else -> {
                return tabView
            }
        }
    }

    private fun initViewPager(){
        val searchFragment = MyShareFragmentTab()
        searchFragment.name = "찾기 창"
        val cameraFragment = MyShareFragmentTab()
        cameraFragment.name = "사진 창"
        val callFragment = MyShareFragmentTab()
        callFragment.name = "전화 창"


        val adapter = MySharePageAdapter(supportFragmentManager) // PageAdapter 생성
        adapter.addItems(searchFragment)
        adapter.addItems(cameraFragment)
        adapter.addItems(callFragment)


        main_viewPager.adapter = adapter // 뷰페이저에 adapter 장착
        main_tablayout.setupWithViewPager(main_viewPager) // 탭레이아웃과 뷰페이저를 연동


        main_tablayout.getTabAt(0)?.setCustomView(createView("찾기"))
        main_tablayout.getTabAt(1)?.setCustomView(createView("사진"))
        main_tablayout.getTabAt(2)?.setCustomView(createView("전화"))

//        main_tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
//            override fun onTabReselected(p0: TabLayout.Tab?) {}
//
//            override fun onTabUnselected(p0: TabLayout.Tab?) {}
//
//            override fun onTabSelected(p0: TabLayout.Tab?) {}
//        })

    }
}