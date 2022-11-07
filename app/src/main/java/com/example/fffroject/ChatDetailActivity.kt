

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
import com.example.fffroject.fragment.ChatRoom
import com.example.fffroject.fragment.CustomDiverItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatDetailActivity : AppCompatActivity() {

    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    // 채팅 리스트
    lateinit var ChatList: ArrayList<ChatRoom>
    lateinit var recyclerview_chatdetail: RecyclerView
    lateinit var toolbar_chatdetail: Toolbar
    lateinit var toolbar_friendname: TextView


    var Index: String? = null
    var to: String? = null
    var from: String? = null
    var context: String? = null
    var sendedAt: String? = null

    val TAG: String = "로그"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_detail)

        ChatList = arrayListOf<ChatRoom>()
        // var check = MutableLiveData<ArrayList<MessageDTO>>() // 최신 메시지 확인
        // Firestore 초기화
        auth = FirebaseAuth.getInstance()
        // user 불러오기
        user = auth!!.currentUser
        firestore = FirebaseFirestore.getInstance()


        Index = intent.getStringExtra("Index")
        to = intent.getStringExtra("to")
        context = intent.getStringExtra("context")
        sendedAt = intent.getStringExtra("sendedAt")

        toolbar_chatdetail = findViewById(R.id.toolbChatDetail)
        toolbar_friendname = findViewById(R.id.ChatName)
        toolbar_friendname.setText(to)


        toolbar_chatdetail.setOnMenuItemClickListener {
            when (it.itemId) {
                // 툴바 버튼 클릭 시
                R.id.btnChatDetail -> {
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.putExtra("Index", Index)
                    ContextCompat.startActivity(this, intent, null)

                    true
                }
                else -> false
            }
        }

        loadChatRoom()


        recyclerview_chatdetail = findViewById(R.id.chat_recyclerview)
        recyclerview_chatdetail.adapter = ChatAdapter()
        recyclerview_chatdetail.layoutManager = LinearLayoutManager(this)
        // 구분선 추가
        val customDecoration =
            CustomDiverItemDecoration(6f, 10f, resources.getColor(R.color.diver_gray))
        recyclerview_chatdetail.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            recyclerview_chatdetail.addItemDecoration(customDecoration)

        }


    }


    inner class ChatDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_list, parent, false)

            return ChatDetailViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as ChatDetailViewHolder).itemView
            var chat_name: TextView
            var chat_content: TextView
            var chat_time: TextView

            chat_name = viewHolder.findViewById(R.id.chat_tv_nickname)
            chat_content = viewHolder.findViewById(R.id.chat_tv_contents)
            chat_time = viewHolder.findViewById(R.id.chat_tv_time)


            chat_name.text = ChatList!![position].to
            chat_content.text = ChatList!![position].context
            chat_time.text = ChatList!![position].sendedAt


            if (ChatList[position].from!!.equals(user)) { // 본인 채팅
                chat_name.setTextColor(
                    ContextCompat.getColor(
                        viewHolder.context,
                        R.color.chat_2
                    )
                )
                chat_name.setText("보낸 쪽지")

            } else { // 상대방 채팅
                chat_name.setText("받은 쪽지")
                chat_name.setTextColor(
                    ContextCompat.getColor(
                        viewHolder.context,
                        R.color.chat_1
                    )
                )

            }

        }

        override fun getItemCount(): Int {
            return ChatList.size
        }

    }


    fun loadChatRoom() {
        // 채팅 불러오기
        firestore?.collection("user")?.document(user.toString())
            ?.collection("chat")
            ?.orderBy("sendedAt", Query.Direction.ASCENDING)
            ?.addSnapshotListener { value, error ->
                ChatList.clear()
                if (value != null) {
                    Toast.makeText(this, "수신은됨", Toast.LENGTH_SHORT).show()
                    // 문서 수신
                    for (snapshot in value.documents) {
                        var chatlist = snapshot.toObject(ChatRoom::class.java)

                        if (chatlist != null) {
                            ChatList.add(chatlist)
                        }
                    }

//                    val nickname = document?.data?.get("nickname").toString()
//                    val context = document?.data?.get("context").toString()
//                    val sendedAt = document?.data?.get("sendedAt") as Timestamp
//                    val email = document?.data?.get("email").toString()

//                    ChatList.add(item)
                }

//                ChatAdapter.notifyDataSetChanged()
            }


    }
}
