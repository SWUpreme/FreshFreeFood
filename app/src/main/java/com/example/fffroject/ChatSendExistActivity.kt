package com.example.fffroject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.fffroject.databinding.ActivityChatSendExistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ChatSendExistActivity : AppCompatActivity() {
    // 바인딩 객체
    lateinit var binding: ActivityChatSendExistBinding

    // 파이어스토어
    var auth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    // 인텐트
    var chatroomId: String? = null
    var postId: String? = null
    var giverId: String? = null
    var takerId: String? = null
    var opponentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 바인딩
        binding = ActivityChatSendExistBinding.inflate(layoutInflater)        // 바인딩 객체 획득
        setContentView(binding.root)                                     // 액티비티 화면 출력
        // 인텐트
        chatroomId = intent.getStringExtra("chatroomId")            // 채팅방 아이디
        postId = intent.getStringExtra("postId")                    // 포스트 아이디
        opponentId = intent.getStringExtra("opponentId")            // 상대방 아이디
        giverId = intent.getStringExtra("giverId")                  // 나눔자 아이디
        takerId = intent.getStringExtra("takerId")                  // 피나눔자 아이디
        // 파이어베이스
        auth = FirebaseAuth.getInstance()                                // 파이어베이스 인증 객체
        user = auth!!.currentUser
        db = FirebaseFirestore.getInstance()                             // 파이어베이스 인스턴스 초기화
        // 현재 시간
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM/dd hh:mm")
        val simpleTime = dateFormat.format(time)

        val timeformatter = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")
        val fullTime = timeformatter.format(time)

        lateinit var chatid: String

        //toolbar 쪽지 보내기 눌렀을 때
        binding.toolbChat.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.btnChatContent -> {
                    if (binding.ChatContent.text.toString().length != 0) {
                        if (user != null) {
                            chatid = UUID.randomUUID().toString()            // 채팅 아이디 생성

                            // 전체 채팅룸에 채팅 올리기
                            db?.collection("chatroom")?.document("$chatroomId")
                                ?.collection("chat")?.document("$chatid")
                                ?.set(
                                    hashMapOf(
                                        "chatId" to chatid,
                                        "context" to binding.ChatContent.text.toString(),
                                        "taker" to takerId,
                                        "giver" to giverId,
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
                            db?.collection("user")?.document(user?.uid!!)?.collection("mychat")?.document("$chatroomId")
                                ?.update(
                                    "context", binding.ChatContent.text.toString(),
                                    "sendedAt", simpleTime,
                                    "updatedAt", fullTime
                                )
                                ?.addOnSuccessListener {}
                                ?.addOnFailureListener {}

                            // 상대의 최신 채팅/채팅시간 업데이트
                            db?.collection("user")?.document("$opponentId")?.collection("mychat")?.document("$chatroomId")
                                ?.update(
                                    "context", binding.ChatContent.text.toString(),
                                    "sendedAt", simpleTime,
                                    "updatedAt", fullTime
                                )
                                ?.addOnSuccessListener {}
                                ?.addOnFailureListener {}

                            // 채팅룸의 최신 채팅/채팅시간 업데이트
                            db?.collection("chatroom")?.document("$chatroomId")
                                ?.update(
                                    "context", binding.ChatContent.text.toString(),
                                    "sendedAt", simpleTime,
                                    "updatedAt", fullTime
                                )
                                ?.addOnSuccessListener {}
                                ?.addOnFailureListener {}
                        }
                        // 쪽지 전송 완료 후 세부 쪽지로 돌아가기
                        finish()
                    }else{
                        Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> false
            }
        }

    }
}