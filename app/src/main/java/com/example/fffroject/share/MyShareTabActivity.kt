package com.example.fffroject.share

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.fffroject.R
import com.google.android.material.tabs.TabLayout

class MyShareTabActivity : AppCompatActivity() {
    private lateinit var mContext : Context
    lateinit var tab_navigation: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mysharetab)
        mContext = applicationContext
    }

    private fun createView(tabName: String) {
//        var tabView = LayoutInflater.from(mContext).inflate(R.layout.custom_tab_button, null)

        tab_navigation = findViewById(R.id.tabLayout)

    }
}