package com.example.fffroject.share

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.fffroject.fragment.MyshareIngFragment

class MySharePageAdapter(fm : FragmentManager) : FragmentStatePagerAdapter(fm){
    private var fragments : ArrayList<Fragment> = ArrayList()

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

    fun addItems(fragment : MyshareIngFragment){
        fragments.add(fragment)
    }
}