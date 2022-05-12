package com.example.fffroject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.fffroject.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var bottom_navigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        bottom_navigation = findViewById(R.id.bottomNavigationView) //as BottomNavigationView

        //val refregeratorFragment = RefregeratorFragment()
        //supportFragmentManager.beginTransaction().add(R.id.main_content, RefregeratorFragment).commit()

        bottom_navigation.setOnItemSelectedListener { item->
            when(item.itemId){
                R.id.tab_divide -> {
                    val divideFragment = DivideFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, divideFragment).commit()
                    return@setOnItemSelectedListener true
                }
//                R.id.tab_refregerator -> {
//                    val mainFragment = RefregeratorFragment()
//                    supportFragmentManager.beginTransaction().replace(R.id.main_content, mainFragment).commit()
//                    return@setOnItemSelectedListener true
//                }
//                R.id.tab_mypage -> {
//                    val divideFragment = HomeFragment()
//                    supportFragmentManager.beginTransaction().replace(R.id.main_content, divideFragment).commit()
//                    return@setOnItemSelectedListener true
//                }
            }
            return@setOnItemSelectedListener false
        }

//        binding.bottomNavigationView.run { item ->
//            when(item.itemId){
//                R.id.tab_main -> {
//
//                }
//            }
//        }

    }

    fun setFragment(fragment: Fragment){
        val manager: FragmentManager = supportFragmentManager
        val fragm: FragmentTransaction = manager.beginTransaction()
    }
//
//    override fun onStart() {
//        super.onStart()
//        if(!FFFroject.checkAuth()){
//            binding.textLogin.visibility = View.GONE // 로그인 해 주세요 텍스트 보이게
//        } else {
//            binding.textLogin.visibility = View.VISIBLE    // 로그인 해 주세요 텍스트 안보이게
//        }
//    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_login, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        startActivity(Intent(this, AuthActivity::class.java))
//        return super.onOptionsItemSelected(item)
//    }

    private fun changeFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_content, fragment)
            .commit()
    }
}