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

    var chatCount : Int  =0       // 채팅 개수

    //채팅 edit
    lateinit var Chatcontent: EditText
    lateinit var chatlist: ArrayList<MyChat>
    lateinit var chatroomid: String
    lateinit var chatid: String

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


        //toolbar 쪽지 보내기 눌렀을 때
        toolbChat.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.btnChatContent -> {
                    if (Chatcontent.text.toString() != null) {
                        if (user != null) {
                            db?.collection("user")?.document(user?.uid!!)?.collection("mychat")
                                ?.whereEqualTo("postid", postid)?.get()
                                ?.addOnCompleteListener { task ->

                                    chatid = UUID.randomUUID().toString()            // 채팅 아이디 생성

                                    // 기존에 채팅방이 존재하지 않는다면
                                    if (task.result?.size() == 0) {
                                        chatroomid = UUID.randomUUID().toString()

                                        // chatroom에 채팅방 생성
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
                                                Toast.makeText(this, "쪽지 전송이 완료됐습니다.", Toast.LENGTH_SHORT).show()
                                            }
                                            ?.addOnFailureListener {
                                                Toast.makeText(this, "쪽지 전송에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                            }

                                        // chatroom에 새로운 채팅 생성
                                        db?.collection("chatroom")?.document("$chatroomid")
                                            ?.collection("chat")?.document("$chatid")
                                            ?.set(
                                                hashMapOf(
                                                    "index" to chatid,
                                                    "context" to Chatcontent.text.toString(),
                                                    "from" to user?.uid,
                                                    "to" to to,
                                                    "sendedAt" to curTime,
                                                    "count" to chatCount
                                                )
                                            )
                                            ?.addOnSuccessListener {}
                                            ?.addOnFailureListener {}

                                        // 내 mychat에 새로운 채팅방 생성
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
                                            ?.addOnSuccessListener {}
                                            ?.addOnFailureListener {}

                                        // 상대방 mychat에 새로운 채팅방 생성
                                        db?.collection("user")?.document(to.toString())
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
                                            ?.addOnSuccessListener {}
                                            ?.addOnFailureListener {}

                                    // 기존에 상대방과의 채팅방이 존재한다면
                                    } else {
                                        // 해당하는 나의 채팅창을 찾아서(포스트 인덱스로) 채팅룸id 받아오기
                                        var doc = task.result.documents?.get(0)
                                        chatroomid = doc?.get("index").toString()

                                        //var chatCount = task.result!!.size()+1
                                        //var chatCount = task.result.documents.count()+1

                                        // 채탱 개수 가져오기
                                        db?.collection("chatroom")?.document("$chatroomid")
                                            ?.collection("chat")
                                            ?.addSnapshotListener() {value, error ->
                                                if (value != null){
                                                    for (snapshot in value.documents) {
                                                        chatCount += 1
                                                    }
                                                    Toast.makeText(this, chatCount, Toast.LENGTH_SHORT).show()
                                                    // 전체 채팅룸에 채팅 올리기
                                                    db?.collection("chatroom")?.document("$chatroomid")
                                                        ?.collection("chat")?.document("$chatid")
                                                        ?.set(
                                                            hashMapOf(
                                                                "index" to chatid,
                                                                "context" to Chatcontent.text.toString(),
                                                                "from" to user?.uid,
                                                                "to" to to,
                                                                "sendedAt" to curTime,
                                                                "count" to  chatCount
                                                            )
                                                        )
                                                        ?.addOnSuccessListener {
                                                            Toast.makeText(this, "쪽지 전송이 완료됐습니다.", Toast.LENGTH_SHORT).show()
                                                        }
                                                        ?.addOnFailureListener {
                                                            Toast.makeText(this, "쪽지 전송에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                                        }
                                                }

                                            }
//                                        // 채탱 개수 가져오기
//                                        db?.collection("chatroom")?.document("$chatroomid")?.collection("chat")
//                                            ?.addSnapshotListener () {value, error ->
//                                                if (value != null){
//                                                    chatCount = value.size()+1
//                                                    Toast.makeText(this, value.size(), Toast.LENGTH_SHORT).show()
//                                                }
//
//                                            }




                                        // 유저의 최신채팅 업데이트/채팅방의 최신채팅 업데이트 고민해볼점
                                        // 둘 중에 하나만 해도 정보 받아오기는 가능할 것 같으니 고민해서 하나만 업데이트하고 넣어주는걸로 하자
                                        // 채팅방에서 어느 것의 정보를 받아오느냐에 따라 다를 것
                                        // 편의성을 위해서는 그냥 도너/기버 두 명의 유저에게 업데이트 해 주는 것이 훨씬 나을 것이라고 생각함


                                        // 나의 최신 채팅/채팅시간 업데이트
                                        db?.collection("user")?.document(user?.uid!!)?.collection("mychat")?.document(chatroomid!!)
                                            ?.set(
                                                hashMapOf(
                                                    "context" to Chatcontent.text.toString(),
                                                    "sendedAt" to curTime,
                                                )
                                            )
                                            ?.addOnSuccessListener {
                                            }
                                            ?.addOnFailureListener {
                                            }

                                        // 상대의 최신 채팅/채팅시간 업데이트
                                        db?.collection("user")?.document(to.toString())?.collection("mychat")?.document(chatroomid!!)
                                            ?.set(
                                                hashMapOf(
                                                    "context" to Chatcontent.text.toString(),
                                                    "sendedAt" to curTime,
                                                )
                                            )
                                            ?.addOnSuccessListener {
                                            }
                                            ?.addOnFailureListener {
                                            }

                                        // 채팅룸의 최신 채팅/채팅시간 업데이트
                                        db?.collection("chatroom")?.document("$chatroomid")
                                            ?.set(
                                                hashMapOf(
                                                    "context" to Chatcontent.text.toString(),
                                                    "sendedAt" to curTime,
                                                )
                                            )
                                            ?.addOnSuccessListener {
                                            }
                                            ?.addOnFailureListener {
                                            }

                                    }

                                }
                        }
                        } else {
                            Toast.makeText(this@ChatActivity, "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
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
