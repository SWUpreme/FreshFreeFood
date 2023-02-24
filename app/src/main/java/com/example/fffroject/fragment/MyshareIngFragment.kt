package com.example.fffroject.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fffroject.R
import kotlinx.android.synthetic.main.fragment_myshare_ing.view.*


class MyshareIngFragment : Fragment() {
    var name = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =inflater.inflate(R.layout.fragment_myshare_ing, container, false)
        view.textView.text = name

        return view
    }
}