

package com.example.fffroject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fffroject.databinding.ActivityChatDetailBinding
import com.example.fffroject.databinding.ActivityChatListBinding
import com.example.fffroject.fragment.ChatDetail
import com.example.fffroject.fragment.ChatRoom
import com.example.fffroject.fragment.CustomDiverItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatDetailActivity : AppCompatActivity() {
    // 바인딩 객체
    lateinit var binding: ActivityChatDetailBinding
    // 파이어스토어
    var auth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    // Data에 있는 ChatDetail
    lateinit var chatDetailList: ArrayList<ChatDetail>
    //리사이클러뷰
    lateinit var recyclerview: RecyclerView

    var intentChatroomIndex: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 바인딩
        binding = ActivityChatDetailBinding.inflate(layoutInflater)        // 바인딩 객체 획득
        setContentView(binding.root)                                     // 액티비티 화면 출력
        // 채팅방 리스트 초기화
        chatDetailList = arrayListOf<ChatDetail>()                           // Data에 있는 ChatDetail
        // 파이어베이스
        auth = FirebaseAuth.getInstance()                                // 파이어베이스 인증 객체
        user = auth!!.currentUser
        db = FirebaseFirestore.getInstance()                             // 파이어베이스 인스턴스 초기화
        // 인텐트-채팅방 인덱스
        intentChatroomIndex = intent.getStringExtra("chatroomIndex")
        // 파이어베이스에서 데이터 불러오기
        loadChatDetail()

        binding.toolbChatDetail.setOnMenuItemClickListener {
            when (it.itemId) {
                // 툴바 버튼 클릭 시
                R.id.btnChatDetail -> {
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra("Index", intentChatroomIndex)
                    ContextCompat.startActivity(this, intent, null)

                    true
                }
                else -> false
            }
        }

        //리사이클러뷰
        recyclerview = binding.chatDetailRecyclerView                     // 리사이클러뷰 바인딩
        recyclerview.adapter = this.ChatDetailRecyclerViewAdapter()            // 리사이클러 뷰 어댑터 등록
        recyclerview.layoutManager = LinearLayoutManager(this) // 레이아웃 매니저 등록
        // 리사이클러 뷰 구분선_커스텀 diver
        val customDecoration = CustomDiverItemDecoration(4f, 10f, resources.getColor(R.color.diver_gray))
        recyclerview.addItemDecoration(customDecoration)
    }

    // 뷰 홀더
    inner class ChatDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    // 뷰 어댑터
    inner class ChatDetailRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        // 항목 개수를 판단
        override fun getItemCount(): Int {
            return chatDetailList.size
        }
        // 뷰 홀더 준비
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_detail_list, parent, false)
            return ChatDetailViewHolder(view)
        }
        // 뷰 홀더의 뷰에 데이터 호출 (실제 데이터 출력)
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            // 바인딩
            var viewHolder = (holder as ChatDetailViewHolder).itemView
            var tfFromTo: TextView
            var tfContext: TextView
            var tfTime: TextView

            tfFromTo = viewHolder.findViewById(R.id.chatDetailFromTo)
            tfContext = viewHolder.findViewById(R.id.chatDetailContext)
            tfTime = viewHolder.findViewById(R.id.chatDetailTime)


            //tfFromTo.text = chatDetailList!![position].wroteId
            var wroteId = chatDetailList!![position].wroteId
            tfContext.text = chatDetailList!![position].context
            tfTime.text = chatDetailList!![position].sendedAt

            // 보낸쪽지=파랑, 받은쪽지=빨강으로 색상 변경하기
            if (wroteId!!.equals(user?.uid)) { // 본인 채팅
                tfFromTo.setText("보낸 쪽지")
                tfFromTo.setTextColor(
                    ContextCompat.getColor(
                        viewHolder.context,
                        R.color.chat_2
                    )
                )
            } else { // 상대방 채팅
                tfFromTo.setText("받은 쪽지")
                tfFromTo.setTextColor(
                    ContextCompat.getColor(
                        viewHolder.context,
                        R.color.chat_1
                    )
                )
            }
        }
    }


    fun loadChatDetail() {
        if (user != null) {
            // 채팅 불러오기
            db?.collection("chatroom")?.document(intentChatroomIndex.toString())
                ?.collection("chat")
                ?.orderBy("sendedAt", Query.Direction.DESCENDING)
                ?.addSnapshotListener { value, error ->
                    chatDetailList.clear()
                    if (value != null) {
                        // 쪽지 리스트에 추가하기
                        for (snapshot in value.documents) {
                            var item = snapshot.toObject(ChatDetail::class.java)
                            if (item != null) {
                                chatDetailList.add(item)
                            }
                        }
                    }
                    recyclerview.adapter?.notifyDataSetChanged()
                }
        }
    }
}
