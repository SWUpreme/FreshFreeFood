package com.example.fffroject

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.fffroject.fragment.MypageFragment

class SplashActivity : AppCompatActivity() {

    private var viewPager: ViewPager? = null
    private var myViewPagerAdapter: MyViewPagerAdapter? = null
    private var dotsLayout : LinearLayout? = null
    private lateinit var dots :Array<TextView?>
    private lateinit var layouts:IntArray
    private var backBtn: ImageButton? = null
    private var btnNext: Button? = null
    //private var prefManager : PrefManger? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //setContentView() 호출 전 처음 시작인지 확인
        //prefManager = PrefManger(this)
//        if (!prefManager!!.isFirstTimeLaunch){
//
//            // 스플래시 화면을 봤으면, 다시 스플래시 화면이 출력x
//            launchHomeScreen()
//            finish()
//        }

        if (Build.VERSION.SDK_INT >= 21){
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE  or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        }
        setContentView(R.layout.activity_splash)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        viewPager = findViewById<ViewPager>(R.id.view_pager)
        dotsLayout = findViewById(R.id.layout_dots)
        backBtn = findViewById<ImageButton>(R.id.btnAdviceClose)
//        btnNext = findViewById(R.id.btn_next)

        //layout 화면(추가 가능)
        layouts = intArrayOf(
            R.layout.item_splash1,
            R.layout.item_splash2,
            R.layout.item_splash3,
            R.layout.item_splash4,
            R.layout.item_splash5
        )
        // 하단의 점 추가
        addBottomDots(0)

        // 알림 표시줄 투명화
        changeStatusBarColor()
        // launchHomeScreen()
        myViewPagerAdapter = MyViewPagerAdapter(this,layouts)
        viewPager!!.adapter = myViewPagerAdapter

        viewPager!!.addOnPageChangeListener(viewPagerChangeListener)

//        btnSkip!!.setOnClickListener { launchHomeScreen() }
//        btnNext!!.setOnClickListener {
//            // 마지막 페이지 확인
//            val current = getItem(+1)
//
//            if (current<layouts.size){
//                // 다음 화면으로 이동
//                viewPager!!.currentItem = current
//            }
//            else{
//                launchHomeScreen()
//            }
//        }

        //x버튼 누르면 main 화면으로 이동
        backBtn!!.setOnClickListener {
            finish()
        }

    }



    private fun getItem(i: Int): Int {
        return viewPager!!.currentItem +i
    }
    val viewPagerChangeListener: ViewPager.OnPageChangeListener =
        object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                addBottomDots(position)

//                if (position == layouts.size -1 ){
//
//                    btnNext!!.text = "start"
//                    btnSkip!!.visibility = View.GONE
//                }
//                else{
//                    btnNext!!.text = "next"
//                    btnSkip!!.visibility = View.VISIBLE
//                }
            }

            override fun onPageSelected(position: Int) {}

            override fun onPageScrollStateChanged(state: Int) {}

        }

    private fun changeStatusBarColor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT

        }

    }



    private fun addBottomDots(currentPage: Int) {
        dots = arrayOfNulls(layouts.size)
        val colorActive = resources.getIntArray(R.array.array_dot_active)
        val colorInActive = resources.getIntArray(R.array.array_dot_inactive)
        dotsLayout!!.removeAllViews()
        for (i in dots.indices){
            dots[i] = TextView(this)
            dots[i]!!.text = Html.fromHtml("&#8226")
            dots[i]!!.textSize = 70f
            dots[i]!!.setTextColor(colorInActive[currentPage])
            dotsLayout!!.addView(dots[i])
        }
        if (dots.size> 0 ){
            dots[currentPage]!!.setTextColor(colorActive[currentPage])
        }
    }


//    private fun launchHomeScreen() {
//        prefManager!!.isFirstTimeLaunch = false
//        startActivity(Intent(this, LoginActivity::class.java))
//        finish()
//    }

}