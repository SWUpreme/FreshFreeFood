package com.example.fffroject.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fffroject.R
import com.example.fffroject.share.ShareDetailActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import java.text.SimpleDateFormat

class ChatActivity : AppCompatActivity() {
    // 파이어스토어
    var auth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    // sharedetail 액티비티에서 받아온 것
    var postId: String? = null
    var giver: String? = null

    // 바인딩
    lateinit var Chatcontent: EditText
    lateinit var chatroomId: String
    lateinit var chatId: String

    // 상대방 닉네임
    lateinit var oppoentNickname : String
    // 자신의 닉네임
    lateinit var myNickname : String
    // 상대방 fcm 토큰
    lateinit var opponentFcmToken : String
    // 자신의 fcm 토큰
    lateinit var myFcmToken: String

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Firestore 초기화
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        db = FirebaseFirestore.getInstance()
        Chatcontent = findViewById(R.id.ChatContent)

        //ShareDetail에서 받아 온 인덱스
        postId = intent.getStringExtra("detailIndex")!!     // 포스트 아이디
        giver = intent.getStringExtra("detailWriter")!!        // 나눔자 아이디

        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM/dd hh:mm")
        val simpleTime = dateFormat.format(time)

        val timeformatter = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")
        val fullTime = timeformatter.format(time)

        //toolbar 쪽지 보내기 눌렀을 때
        toolbChat.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.btnChatContent -> {
                    // 글을 작성했다면
                    if (Chatcontent.text.toString().length != 0) {
                        if (user != null) {
                            // 유저의 채팅에 같은 게시글에서 작성된 쪽지의 존재 여부 확인
                            db?.collection("user")?.document(user?.uid!!)?.collection("mychat")
                                ?.whereEqualTo("postId", postId)?.get()
                                ?.addOnCompleteListener { task ->

                                    chatId = UUID.randomUUID().toString()            // 채팅 아이디 생성

                                    // 기존에 채팅방이 존재하지 않는다면
                                    if (task.result?.size() == 0) {
                                        chatroomId = UUID.randomUUID().toString()

                                        // 해당유저의 token 받아오기
                                        db?.collection("user")?.document(user?.uid!!)?.get()?.addOnSuccessListener { value ->
                                            myFcmToken = value.data?.get("fcmToken") as String
                                            myNickname = value.data?.get("nickname") as String

                                            // 상대방의 token 받아오기
                                            db?.collection("user")?.document(giver!!)?.get()?.addOnSuccessListener { value ->
                                                opponentFcmToken = value.data?.get("fcmToken") as String
                                                oppoentNickname = value.data?.get("nickname") as String

                                                // chatroom에 채팅방 생성
                                                db?.collection("chatroom")?.document("$chatroomId")
                                                    ?.set(
                                                        hashMapOf(
                                                            "chatroomId" to chatroomId,
                                                            "postId" to postId,
                                                            "contextTxt" to Chatcontent.text.toString(),
                                                            "taker" to user?.uid,
                                                            "giver" to giver,
                                                            "sendedAt" to simpleTime,
                                                            "createdAt" to fullTime,
                                                            "updatedAt" to fullTime,
                                                            "status" to "active"
                                                        )
                                                    )
                                                    ?.addOnSuccessListener {
                                                        Toast.makeText(this, "쪽지 전송이 완료됐습니다.", Toast.LENGTH_SHORT).show()
                                                    }
                                                    ?.addOnFailureListener {
                                                        Toast.makeText(this, "쪽지 전송에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                                    }

                                                // chatroom에 새로운 채팅 생성
                                                db?.collection("chatroom")?.document("$chatroomId")
                                                    ?.collection("chat")?.document("$chatId")
                                                    ?.set(
                                                        hashMapOf(
                                                            "chatId" to chatId,
                                                            "contextTxt" to Chatcontent.text.toString(),
                                                            "taker" to user?.uid,
                                                            "giver" to giver,
                                                            "writer" to user?.uid,
                                                            "sendedAt" to simpleTime,
                                                            "createdAt" to fullTime,
                                                            "updatedAt" to fullTime,
                                                            "status" to "active"
                                                        )
                                                    )
                                                    ?.addOnSuccessListener {}
                                                    ?.addOnFailureListener {}

                                                // 내 mychat에 새로운 채팅방 생성
                                                db?.collection("user")?.document(user!!.uid)
                                                    ?.collection("mychat")
                                                    ?.document("$chatroomId")
                                                    ?.set(
                                                        hashMapOf(
                                                            "chatroomId" to chatroomId,
                                                            "postId" to postId,
                                                            "contextTxt" to Chatcontent.text.toString(),
                                                            "taker" to user?.uid,
                                                            "giver" to giver,
                                                            "opponentId" to giver,
                                                            "opponentFcm" to opponentFcmToken,
                                                            "oppoentNickname" to oppoentNickname,
                                                            "sendedAt" to simpleTime,
                                                            "createdAt" to fullTime,
                                                            "updatedAt" to fullTime,
                                                            "status" to "active"
                                                        )
                                                    )
                                                    ?.addOnSuccessListener {}
                                                    ?.addOnFailureListener {}

                                                // 상대방 mychat에 새로운 채팅방 생성
                                                db?.collection("user")?.document(giver.toString())
                                                    ?.collection("mychat")
                                                    ?.document("$chatroomId")
                                                    ?.set(
                                                        hashMapOf(
                                                            "chatroomId" to chatroomId,
                                                            "postId" to postId,
                                                            "contextTxt" to Chatcontent.text.toString(),
                                                            "taker" to user?.uid,
                                                            "giver" to giver,
                                                            "opponentId" to user!!.uid,
                                                            "opponentFcm" to myFcmToken,
                                                            "oppoentNickname" to myNickname,
                                                            "sendedAt" to simpleTime,
                                                            "createdAt" to fullTime,
                                                            "updatedAt" to fullTime,
                                                            "status" to "active"
                                                        )
                                                    )
                                                    ?.addOnSuccessListener {}
                                                    ?.addOnFailureListener {}
                                            }
                                        }


                                    // 기존에 상대방과의 채팅방이 존재한다면
                                    } else {
                                        // 해당하는 나의 채팅창을 찾아서(포스트 인덱스로) 채팅룸id 받아오기
                                        var doc = task.result.documents?.get(0)
                                        chatroomId = doc?.get("chatroomId").toString()

                                        // 전체 채팅룸에 채팅 올리기
                                        db?.collection("chatroom")?.document("$chatroomId")
                                            ?.collection("chat")?.document("$chatId")
                                            ?.set(
                                                hashMapOf(
                                                    "chatId" to chatId,
                                                    "contextTxt" to Chatcontent.text.toString(),
                                                    "taker" to user?.uid,
                                                    "giver" to giver,
                                                    "writer" to user?.uid,
                                                    "sendedAt" to simpleTime,
                                                    "createdAt" to fullTime,
                                                    "updatedAt" to fullTime,
                                                    "status" to "active"
                                                )
                                            )
                                            ?.addOnSuccessListener {
                                                Toast.makeText(this, "쪽지 전송이 완료됐습니다.", Toast.LENGTH_SHORT).show()
                                            }
                                            ?.addOnFailureListener {
                                                Toast.makeText(this, "쪽지 전송에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                            }

                                        // 나의 최신 채팅/채팅시간 업데이트
                                        db?.collection("user")?.document(user?.uid!!)?.collection("mychat")?.document(chatroomId!!)
                                            ?.update(
                                                "contextTxt", Chatcontent.text.toString(),
                                                "sendedAt", simpleTime,
                                                "updatedAt", fullTime
                                            )
                                            ?.addOnSuccessListener {
                                            }
                                            ?.addOnFailureListener {
                                            }

                                        // 상대의 최신 채팅/채팅시간 업데이트
                                        db?.collection("user")?.document(giver.toString())?.collection("mychat")?.document(chatroomId!!)
                                            ?.update(
                                                "contextTxt", Chatcontent.text.toString(),
                                                "sendedAt", simpleTime,
                                                "updatedAt", fullTime
                                            )
                                            ?.addOnSuccessListener {
                                            }
                                            ?.addOnFailureListener {
                                            }

                                        // 채팅룸의 최신 채팅/채팅시간 업데이트
                                        db?.collection("chatroom")?.document("$chatroomId")
                                            ?.update(
                                                "contextTxt", Chatcontent.text.toString(),
                                                "sendedAt", simpleTime,
                                                "updatedAt", fullTime
                                            )
                                            ?.addOnSuccessListener {
                                            }
                                            ?.addOnFailureListener {
                                            }

                                    }

                                }
                        }
                        // 쪽지 전송 완료 후 게시글로 돌아가기
                        finish()
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
