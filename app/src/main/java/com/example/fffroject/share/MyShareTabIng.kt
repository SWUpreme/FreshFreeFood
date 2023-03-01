package com.example.fffroject.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fffroject.databinding.FragmentMyShareTabIngBinding

class MyShareTabIng : Fragment() {
    private lateinit var binding: FragmentMyShareTabIngBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyShareTabIngBinding.inflate(inflater, container, false)
        return binding.root
    }
}