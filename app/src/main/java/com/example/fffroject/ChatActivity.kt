package com.example.fffroject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import java.text.SimpleDateFormat

class ChatActivity : AppCompatActivity() {

    var auth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    var detailIndex : String? = null
    var detailWriter : String? = null
    lateinit var Chatcontent : EditText
    //lateinit var toolbChat: Toolbar
    private var chatRoomUid : String? = null
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        /* setSupportActionBar(toolbChat)
         supportActionBar?.setDisplayHomeAsUpEnabled(true)
         supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_btn)
         supportActionBar?.setDisplayShowTitleEnabled(false)*/
        //toolbChat = findViewById(R.id.btnChatContent)

        // Firestore 초기화
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        db = FirebaseFirestore.getInstance()
        Chatcontent = findViewById(R.id.ChatContent)

        //ShareDetail에서 받아 온 인덱스
        detailIndex = intent.getStringExtra("detailIndex")!!
        detailWriter = intent.getStringExtra("detailWriter")!!
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM/dd hh:mm")
        val curTime = dateFormat.format(time)


        //toolbar 쪽지 보내기 눌렀을 때
        toolbChat.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.btnChatContent -> {
                    if(checkAllWritten()){
                        var content = Chatcontent.text.toString()
                        checkChatRoom(content, curTime)

                        val intent = Intent(applicationContext,ChatDetailActivity::class.java)
                        intent.putExtra("Index", detailIndex)
                        intent.putExtra("to", detailWriter)
                        intent.putExtra("context", content)
                        intent.putExtra("sendedAt", curTime)
                        intent.putExtra("from", user?.uid)

                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
                    }

                    true
                }
                else -> false
            }
        }


    }


    // 양식 작성 여부 확인
    private fun checkAllWritten(): Boolean{
        return (Chatcontent.length()>0)
    }


    private fun checkChatRoom(content: String, curTime: String){
        if (user != null){
            detailIndex = UUID.randomUUID().toString()



            db?.collection("user")?.document(user!!.uid)
                ?.collection("chat")?.document("$detailIndex")
                ?.set(
                    hashMapOf(
                        "index" to detailIndex,
                        "to" to detailWriter,
                        "from" to user?.uid,
                        "context" to content,
                        "sendedAt" to curTime
                    )
                )
                ?.addOnSuccessListener {
                    Toast.makeText(this, "쪽지 전송 완료.", Toast.LENGTH_SHORT).show()
                }
                ?.addOnFailureListener {
                    Toast.makeText(this, "쪽지 전송 실패.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    //item 버튼 클릭 했을 때
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                //뒤로가기 버튼 눌렀을 때
                Log.d("ToolBar_item: ", "뒤로가기 버튼 클릭")
                val intent = Intent(applicationContext,ShareDetailActivity::class.java)
                startActivity(intent)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

}