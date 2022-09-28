package com.example.fffroject

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.custom_dialog.*

class MyCustomDialog(context: Context,
                     myCustomDialogInterface: MyCustomDialogInterface)
                    : Dialog(context), View.OnClickListener
{

    val TAG: String = "로그"

    //
    private var myCustomDialogInterface: MyCustomDialogInterface? = null

    // 인터페이스 연결
    init {
        this.myCustomDialogInterface = myCustomDialogInterface
    }

    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.custom_dialog)

        Log.d(TAG, "MyCustomDialog - onCreate() called")
        // 배경 투명
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        barcode_btn.setOnClickListener(this)
        write_btn.setOnClickListener(this)
        close_btn.setOnClickListener {
            dismiss()
        }


    }

    override fun onClick(view: View?) {
        when(view){

            // 바코드 버튼이 클릭 되었을때
            barcode_btn -> {
                Log.d(TAG, "MyCustomDialog -  버튼 클릭!")

                this.myCustomDialogInterface?.onBarcodeBtnClicked()
            }

            // 직접 입력 버튼이 클릭 되었을때
            write_btn -> {
                Log.d(TAG, "MyCustomDialog - 버튼 클릭!")

                this.myCustomDialogInterface?.onWriteBtnClicked()
            }


        }
    }


}
