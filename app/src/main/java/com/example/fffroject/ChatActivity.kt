package com.example.fffroject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.fffroject.fragment.MyChat
import com.example.fffroject.fragment.MyFridge
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

    //sharedetail에서 받아온 것
    var postid: String? = null
    var to: String? = null

    //채팅 edit
    lateinit var Chatcontent: EditText

    lateinit var chatlist: ArrayList<MyChat>

    lateinit var chatroomid: String
    lateinit var chatid: String

    // lateinit var toolbar_chat: Toolbar

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatlist = arrayListOf<MyChat>()

        // Firestore 초기화
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        db = FirebaseFirestore.getInstance()
        Chatcontent = findViewById(R.id.ChatContent)

        //ShareDetail에서 받아 온 인덱스
        postid = intent.getStringExtra("detailIndex")!!
        to = intent.getStringExtra("detailWriter")!!

        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM/dd hh:mm")
        val curTime = dateFormat.format(time)
        //toolbar_chat = findViewById(R.id.toolbChat)
//        toolbar_chat = findViewById(R.id.toolbChat)


        //toolbar 쪽지 보내기 눌렀을 때
        toolbChat.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.btnChatContent -> {
                    if (Chatcontent.text.toString() != null) {
                        if (user != null) {
                            db?.collection("user")?.document(user?.uid!!)?.collection("mychat")
                                ?.whereEqualTo("postid", postid)?.get()
                                ?.addOnCompleteListener { task ->
                                    chatid = UUID.randomUUID().toString()

                                    if (task.result?.size() == 0) {
                                        chatroomid = UUID.randomUUID().toString()


                                        db?.collection("chatroom")?.document("$chatroomid")
                                            ?.set(
                                                hashMapOf(
                                                    "index" to chatroomid,
                                                    "postid" to postid,
                                                    "context" to Chatcontent.text.toString(),
                                                    "from" to user?.uid,
                                                    "to" to to,
                                                    "sendedAt" to curTime
                                                )
                                            )
                                            ?.addOnSuccessListener {
                                                Toast.makeText(this, "쪽지 전송 완료.", Toast.LENGTH_SHORT).show()
                                            }
                                            ?.addOnFailureListener {
                                                Toast.makeText(this, "쪽지 전송 실패.", Toast.LENGTH_SHORT).show()
                                            }

                                        db?.collection("user")?.document(user!!.uid)
                                            ?.collection("mychat")
                                            ?.document("$chatroomid")
                                            ?.set(
                                                hashMapOf(
                                                    "index" to chatroomid,
                                                    "postid" to postid,
                                                    "context" to Chatcontent.text.toString(),
                                                    "from" to user?.uid,
                                                    "to" to to,
                                                    "sendedAt" to curTime
                                                )
                                            )
                                            ?.addOnSuccessListener {
                                                Toast.makeText(this, "쪽지 전송 완료.", Toast.LENGTH_SHORT).show()
                                            }
                                            ?.addOnFailureListener {
                                                Toast.makeText(this, "쪽지 전송 실패.", Toast.LENGTH_SHORT).show()
                                            }

                                        db?.collection("chatroom")?.document("$chatroomid")
                                            ?.collection("chat")?.document("$chatid")
                                            ?.set(
                                                hashMapOf(
                                                    "index" to chatid,
                                                    "context" to Chatcontent.text.toString(),
                                                    "from" to user?.uid,
                                                    "to" to to,
                                                    "sendedAt" to curTime
                                                )
                                            )
                                            ?.addOnSuccessListener {
                                                Toast.makeText(this, "쪽지 전송 완료.", Toast.LENGTH_SHORT).show()
                                            }
                                            ?.addOnFailureListener {
                                                Toast.makeText(this, "쪽지 전송 실패.", Toast.LENGTH_SHORT).show()
                                            }

                                    } else {
                                        db?.collection("user")?.document(user?.uid!!)?.collection("mychat")?.document(chatroomid!!)
                                            ?.update("context", Chatcontent.text.toString())
                                            ?.addOnSuccessListener {
                                                Toast.makeText(this, "쪽지 전송 완료.", Toast.LENGTH_SHORT).show()
                                            }
                                            ?.addOnFailureListener {
                                                Toast.makeText(this, "쪽지 전송 실패.", Toast.LENGTH_SHORT).show()
                                            }

                                        db?.collection("user")?.document(user?.uid!!)?.collection("mychat")?.document(chatroomid!!)
                                            ?.update("sendedAt", curTime)
                                            ?.addOnSuccessListener {
                                                Toast.makeText(this, "쪽지 전송 완료.", Toast.LENGTH_SHORT).show()
                                            }
                                            ?.addOnFailureListener {
                                                Toast.makeText(this, "쪽지 전송 실패.", Toast.LENGTH_SHORT).show()
                                            }

                                        db?.collection("chatroom")?.document("$chatroomid")
                                            ?.collection("chat")?.document("$chatid")
                                            ?.update("context", Chatcontent.text.toString())
                                            ?.addOnSuccessListener {
                                                Toast.makeText(this, "쪽지 전송 완료.", Toast.LENGTH_SHORT).show()
                                            }
                                            ?.addOnFailureListener {
                                                Toast.makeText(this, "쪽지 전송 실패.", Toast.LENGTH_SHORT).show()
                                            }
                                        db?.collection("chatroom")?.document("$chatroomid")
                                            ?.collection("chat")?.document("$chatid")
                                            ?.update("sendedAt", curTime)
                                            ?.addOnSuccessListener {
                                                Toast.makeText(this, "쪽지 전송 완료.", Toast.LENGTH_SHORT).show()
                                            }
                                            ?.addOnFailureListener {
                                                Toast.makeText(this, "쪽지 전송 실패.", Toast.LENGTH_SHORT).show()
                                            }


                                        db?.collection("chatroom")?.document("$chatroomid")
                                            ?.update("context", Chatcontent.text.toString())
                                            ?.addOnSuccessListener {
                                                Toast.makeText(this, "쪽지 전송 완료.", Toast.LENGTH_SHORT).show()
                                            }
                                            ?.addOnFailureListener {
                                                Toast.makeText(this, "쪽지 전송 실패.", Toast.LENGTH_SHORT).show()
                                            }
                                        db?.collection("chatroom")?.document("$chatroomid")
                                            ?.update("sendedAt", curTime)
                                            ?.addOnSuccessListener {
                                                Toast.makeText(this, "쪽지 전송 완료.", Toast.LENGTH_SHORT).show()
                                            }
                                            ?.addOnFailureListener {
                                                Toast.makeText(this, "쪽지 전송 실패.", Toast.LENGTH_SHORT).show()
                                            }
                                    }

                                }
                        }
                        } else {
                            Toast.makeText(this@ChatActivity, "내용을 입력하세요.", Toast.LENGTH_SHORT)
                                .show()
                        }



                        true
                    }
                    else -> false

                }
            }




    }

        //item 버튼 클릭 했을 때
        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item?.itemId) {
                android.R.id.home -> {
                    //뒤로가기 버튼 눌렀을 때
                    Log.d("ToolBar_item: ", "뒤로가기 버튼 클릭")
                    val intent = Intent(applicationContext, ShareDetailActivity::class.java)
                    startActivity(intent)
                    return true
                }

                else -> return super.onOptionsItemSelected(item)
            }
        }

    }
