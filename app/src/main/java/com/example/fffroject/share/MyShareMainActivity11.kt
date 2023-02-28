package com.example.fffroject.share

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.example.fffroject.R
import kotlinx.android.synthetic.main.activity_mysharemain.*
import kotlinx.android.synthetic.main.tab_myshare_button.view.*

class MyShareMainActivity11 : AppCompatActivity() {
    private lateinit var mContext : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mysharemain)
        mContext = applicationContext
    }

    private fun createView(tabName: String): View? {
        var tabView = LayoutInflater.from(mContext).inflate(R.layout.tab_myshare_button, null)

        tabView.tab_text.text = tabName
        when (tabName) {
            "나눔중" -> {
                return tabView
            }
            "나눔완료" -> {
                return tabView
            }
            else -> {
                return tabView
            }
        }
    }

    private fun initViewPager(){
        val ingFragment = MyshareFragmentTab()
        ingFragment.name = "나눔중"
        val doneFragment = MyshareFragmentTab()
        doneFragment.name = "나눔완료"

        val adapter = MySharePageAdapter(supportFragmentManager) // PageAdapter 생성
//        adapter.addItems(ingFragment)
//        adapter.addItems(doneFragment)

        viewPager.adapter = adapter // 뷰페이저에 adapter 장착
        tabLayout.setupWithViewPager(viewPager) // 탭레이아웃과 뷰페이저를 연동


        tabLayout.getTabAt(0)?.setCustomView(createView("나눔중"))
        tabLayout.getTabAt(1)?.setCustomView(createView("나눔완료"))
    }
}