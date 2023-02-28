package com.example.fffroject.share

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

import com.example.fffroject.shareTrial.MyShareFragmentTab
import java.util.concurrent.CountDownLatch

class MySharePageAdapter(fm : FragmentManager) : FragmentStatePagerAdapter(fm){
    private var fragments : ArrayList<MyShareFragmentTab> = ArrayList()

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

    fun addItems(fragment : MyShareFragmentTab){
        fragments.add(fragment)
    }
}