package com.example.fffroject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView

class RegionSelectActivity : AppCompatActivity() {

    lateinit var searchView : android.widget.SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_region_select)

        searchView = findViewById(R.id.searchViewRegion)

//        searchView.setOnQueryTextListener{
//
//        }
    }
}