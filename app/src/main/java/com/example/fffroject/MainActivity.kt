package com.example.fffroject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toolbar
import com.example.fffroject.fragment.FridgeFragment
import com.example.fffroject.fragment.MypageFragment
import com.example.fffroject.fragment.ShareFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    //lateinit var binding: ActivityMainBinding
    lateinit var bottom_navigation: BottomNavigationView
    lateinit var toolbar : Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        bottom_navigation = findViewById(R.id.bottomNavigationView) //as BottomNavigationView

        //val refregeratorFragment = RefregeratorFragment()
        //supportFragmentManager.beginTransaction().add(R.id.main_content, RefregeratorFragment).commit()

//        val fridgeFragment = FridgeFragment()
//        supportFragmentManager.beginTransaction().add(R.id.tab_fridge, fridgeFragment).commit()

        // bottomNavigationView 설정
        bottom_navigation.setOnItemSelectedListener { item->
            when(item.itemId){
                R.id.tab_share -> {                    val shareFragment = ShareFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, shareFragment).commit()
                    return@setOnItemSelectedListener true
                }
                R.id.tab_fridge -> {
                    val fridgeFragment = FridgeFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, fridgeFragment).commit()
                    return@setOnItemSelectedListener true
                }
                R.id.tab_mypage -> {
                    val mypageFragment = MypageFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, mypageFragment).commit()
                    return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener false
        }
        val fridgeFragment = FridgeFragment()
        supportFragmentManager.beginTransaction().add(R.id.main_content, fridgeFragment).commit()
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        var fragment = supportFragmentManager.getFragments()
//        R.id.btnPlus -> {
//            if (fragment == [0])
//        }
//        return super.onOptionsItemSelected(item)
//    }

//    fun setFragment(fragment: Fragment){
//        val manager: FragmentManager = supportFragmentManager
//        val fragm: FragmentTransaction = manager.beginTransaction()
//    }
//
//    override fun onStart() {
//        super.onStart()
//        val fridgeFragment = FridgeFragment()
//        supportFragmentManager.beginTransaction().add(R.id.tab_fridge, fridgeFragment).commit()
//
////        if(!FFFroject.checkAuth()){
////            binding.textLogin.visibility = View.GONE // 로그인 해 주세요 텍스트 보이게
////        } else {
////            binding.textLogin.visibility = View.VISIBLE    // 로그인 해 주세요 텍스트 안보이게
////        }
//    }


//    private fun changeFragment(fragment: Fragment){
//        supportFragmentManager
//            .beginTransaction()
//            .replace(R.id.main_content, fragment)
//            .commit()
//    }
}