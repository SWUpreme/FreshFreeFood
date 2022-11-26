
package com.example.fffroject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fffroject.databinding.ActivityChatListBinding
import com.example.fffroject.databinding.ActivityMyshareBinding
import com.example.fffroject.fragment.ChatRoom
import com.example.fffroject.fragment.CustomDiverItemDecoration
import com.example.fffroject.fragment.MyChat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_chat_list.*
import kotlinx.android.synthetic.main.activity_myshare.*
import kotlinx.android.synthetic.main.activity_myshare.txtNoRegion
import kotlinx.android.synthetic.main.activity_sharedetail.*
import kotlinx.android.synthetic.main.fragment_share.*
import kotlinx.android.synthetic.main.item_chat_list.*

class ChatListActivity : AppCompatActivity() {
    // 바인딩 객체
    lateinit var binding: ActivityChatListBinding
    // 파이어스토어
    var auth : FirebaseAuth? = null
    var db : FirebaseFirestore? = null
    var user : FirebaseUser? = null
    // Data에 있는 ChatRoom
    lateinit var chatRoomList: ArrayList<ChatRoom>
    //리사이클러뷰
    lateinit var recyclerview: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 바인딩
        binding = ActivityChatListBinding.inflate(layoutInflater)        // 바인딩 객체 획득
        setContentView(binding.root)                                     // 액티비티 화면 출력
        // 채팅방 리스트 초기화
        chatRoomList = arrayListOf<ChatRoom>()                           // Data에 있는 ChatRoom

        // 파이어베이스
        auth = FirebaseAuth.getInstance()                                // 파이어베이스 인증 객체
        user = auth!!.currentUser
        db = FirebaseFirestore.getInstance()                             // 파이어베이스 인스턴스 초기화
        // 파이어베이스에서 데이터 불러오기
        loadChat()

        //리사이클러뷰
        recyclerview = binding.chatRecyclerview                     // 리사이클러뷰 바인딩
        recyclerview.adapter = this.ChatRecyclerViewAdapter()            // 리사이클러 뷰 어댑터 등록
        recyclerview.layoutManager = LinearLayoutManager(this) // 레이아웃 매니저 등록
        // 리사이클러 뷰 구분선_커스텀 diver
        val customDecoration = CustomDiverItemDecoration(4f, 10f, resources.getColor(R.color.diver_gray))
        recyclerview.addItemDecoration(customDecoration)
    }

    // 뷰 홀더
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // 뷰 어댑터
    inner class ChatRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        // 항목 개수를 판단
        override fun getItemCount(): Int {
            return chatRoomList.size
        }
        // 뷰 홀더 준비
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_chat_list, parent, false)
            return ViewHolder(view)
        }
        // 뷰 홀더의 뷰에 데이터 호출 (실제 데이터 출력)
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            // 바인딩
            var viewHolder = (holder as ViewHolder).itemView
            var chatName: TextView = viewHolder.findViewById(R.id.chat_tv_nickname)
            var chatContent: TextView = viewHolder.findViewById(R.id.chat_tv_contents)
            var chatTime: TextView = viewHolder.findViewById(R.id.chat_tv_time)

            // 뷰 데이터 출력 외 정보
            var chatroomIndex = chatRoomList!![position].index
            var from = chatRoomList!![position].from        // 나눔자
            var to = chatRoomList!![position].to            // 피나눔자
            var postid = chatRoomList!![position].postid  // 포스트 아이디
            var opponentId : String = ""                           // 상대방 유저 인덱스를 저장할 변수
            var oppoentNickname : String = ""                               // 상대방 닉네임


            // 상대방 유저 인덱스 찾기
            if (user != null) {
                if(user!!.uid.toString() != from){
                    // 내 아아디와 나눔자 아이디가 다르다면
                    opponentId = from.toString()    // 상대방은 나눔자
                }else{
                    // 내 아이디와 나눔자 아이디가 같다면
                    opponentId = to.toString()      // 상대방은 피나눔자
                }

                // 상대방이 현재 존재하는 계정이라면
                if(opponentId != ""){
                    // 쪽지 상대방 닉네임 가져오기
                    db?.collection("user")?.document(opponentId)?.get()
                        ?.addOnSuccessListener { value ->
                            oppoentNickname = value.data?.get("nickname") as String
                            // 뷰에 데이터 출력
                            chatName.text = oppoentNickname
                        }
                }else{
                    // 상대방이 탈퇴했다면
                    oppoentNickname = "(알수없음)"
                    // 뷰에 데이터 출력
                    chatName.text = oppoentNickname
                }
            }
            // 뷰에 데이터 출력
            chatContent.text = chatRoomList!![position].context
            chatTime.text = chatRoomList!![position].sendedAt

            // 객체 클릭 이벤트
            viewHolder.setOnClickListener {
                val intent = Intent(viewHolder.context, ChatDetailActivity::class.java)
                intent.putExtra("chatroomIndex", chatroomIndex.toString())
                intent.putExtra("postIndex", postid.toString())
                intent.putExtra("opponentId", opponentId)
                intent.putExtra("oppoentNickname", oppoentNickname)
                ContextCompat.startActivity(viewHolder.context, intent, null)
            }
        }
    }


    // 파이어베이스에서 쪽지방 데이터 불러오는 함수
    fun loadChat() {
        if (user != null) {
            db?.collection("user")?.document(user!!.uid)
                ?.collection("mychat")
                ?.orderBy("sendedAt", Query.Direction.DESCENDING)
                ?.addSnapshotListener { value, error ->
                    chatRoomList.clear()
                    if (value != null) {
                        // 쪽지 없음 텍스트 INVISIBLE
                        binding.txtNoChat.setVisibility(View.INVISIBLE)
                        // 채팅방 리스트에 추가하기
                        for (snapshot in value.documents) {
                            var item = snapshot.toObject(ChatRoom::class.java)
                            if (item != null) {
                                chatRoomList.add(item)
                            }
                        }
                    }else{
                        // 쪽지 없음 텍스트 VISIBLE
                        binding.txtNoChat.setVisibility(View.VISIBLE)
                    }
                    recyclerview.adapter?.notifyDataSetChanged()
                }
        }
    }
}