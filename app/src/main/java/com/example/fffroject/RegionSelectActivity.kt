package com.example.fffroject

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import com.example.fffroject.fragment.ShareFragment

class RegionSelectActivity : AppCompatActivity() {
    companion object{
        var regionData : String =""
    }

    lateinit var webView: WebView

    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_region_select)

        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true       // 자바스크립트 허용
        webView.addJavascriptInterface(BridgeInterface(this), "Android")
        /* 웹뷰에서 새 창이 뜨지 않도록 방지하는 구문. 필수 */
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()
        /* 웹뷰에서 새 창이 뜨지 않도록 방지하는 구문. 필수 */

        // 최초 웹뷰 로드
        webView.loadUrl("https://fffroject-f3104.web.app")

//        searchView.setOnQueryTextListener{
//
//        }
    }

    inner class WebViewClient : android.webkit.WebViewClient(){
        override fun onPageFinished(view: WebView?, url: String?) {
            // Android -> Javascript 함수 호출
            webView.loadUrl("javascript:sample2_execDaumPostcode();")   // 링크 주소를 load함
        }
    }

    // 우편 번호 찾는 자바스크립트와 연결하는 통로
    class BridgeInterface(private val mContext: RegionSelectActivity) {
        @JavascriptInterface
        fun processDATA(data: String){
            // 다음(카카오) 주소 검색 API 결과 값이 브릿지 통로를 통해 전달 받는다. (from JAVASCRIPT)
            val shareFragment = ShareFragment()
            var bundle = Bundle()
            bundle.putString("data",data)
            shareFragment.arguments = bundle
            mContext.supportFragmentManager.beginTransaction().replace(R.id.main_content, shareFragment).commit()
            // getActivity().setResult(Activity.RESULT_OK, data)

            // 액티비티용
//            val intent = Intent(mContext, ShareFragment::class.java)
//            intent.putExtra("data", data)
//            ContextCompat.startActivity(mContext, intent, null)
//            setResult(RESULT_OK, intent)
//            finish
        }
    }
}