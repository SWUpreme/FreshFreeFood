package com.example.fffroject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FoodListActivity : AppCompatActivity(), MyCustomDialogInterface {

    val TAG: String = "로그"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foodlist)
        Log.d(TAG, "FoodListActivity - onCreate() called")
    }



    fun onDialogBtnClicked(view: View){
        Log.d(TAG, "FoodListActivity - onDialogBtnClicked() called")

        val myCustomDialog = MyCustomDialog(this, this)

        myCustomDialog.show()

    }


    // 바코드 버튼 클릭
    override fun onBarcodeBtnClicked() {
        Log.d(TAG, "FoodListActivity - onBarcodeBtnClicked() called")
        val intent = Intent(applicationContext, BarCodeActivity::class.java)
        startActivity(intent)
    }

    // 직접 입력 버튼 클릭
    override fun onWriteBtnClicked() {
        Log.d(TAG, "FoodListActivity - onWriteBtnClicked() called")
        val intent = Intent(applicationContext, WriteActivity::class.java)
        startActivity(intent)
    }
}
