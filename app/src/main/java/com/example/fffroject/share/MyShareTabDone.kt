package com.example.fffroject.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fffroject.databinding.FragmentMyShareTabDoneBinding

class MyShareTabDone : Fragment() {
    private lateinit var binding: FragmentMyShareTabDoneBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyShareTabDoneBinding.inflate(inflater, container, false)
        return binding.root
    }
}