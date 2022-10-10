/*package com.example.fffroject

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fffroject.databinding.ActivityChatBinding
import com.example.fffroject.fragment.user
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    var mBinding : ActivityChatBinding? = null
    val binding get() = mBinding!!
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    var userName = ""
    val chatList = mutableListOf<Chat>()
    lateinit var  friend : user
    lateinit var adapter: ChatAdapter

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val intent = intent
        friend = intent.getSerializableExtra("friend") as user // 상대방의 정보를 가져옴

        // 현재 사용자의 이름 가져오기
        db.collection("user")
            .whereEqualTo("uid", auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener {
                it.forEach {
                    userName = it["nickname"].toString()
                }
            }

        binding.friendName.text = friend.nickname // 채팅방 이름 설정

        binding.sendMessage.setOnClickListener { // 메세지 보내기 버튼 클릭 시
            // 데이터베이스 상에서
            // chat -> 현재유저 uid -> 상대유저 uid -> 필드저장(Chat)
            // chat -> 상대유저 uid -> 현재유저 uid -> 필드저장(Chat)
            // 둘다 업데이트 해줘야 함 (양방향 통신을 위해서)
            val content = binding.messageText.text.toString()

            if(content != "") {
                var chat = Chat(content, userName, 0)
                db.collection("chat")
                    .document(auth.currentUser!!.uid)
                    .collection(friend.uid.toString())
                    .document(com.google.firebase.Timestamp.now().toString())
                    .set(chat)
                chat = Chat(content, userName, 1)
                db.collection("chat")
                    .document(friend.uid.toString())
                    .collection(auth.currentUser!!.uid)
                    .document(com.google.firebase.Timestamp.now().toString())
                    .set(chat)

                binding.messageText.setText("")
            }
        }
        // 채팅 리스트 가져오기 + 리사이클러 뷰 어답터 적용
        getChatList()
        db.collection("chat")
            .document(auth.currentUser!!.uid)
            .collection(friend.uid.toString())
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("testt", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot!!.metadata.isFromCache) return@addSnapshotListener

                Log.d("testt", "done")
                getChatList()
            }


    }


    fun getChatList(){
        chatList.clear()
        db.collection("chat")
            .document(auth.currentUser!!.uid)
            .collection(friend.uid.toString())
            .get()
            .addOnSuccessListener {
                it.forEach {
                    val content = it["content"].toString()
                    val name = it["name"].toString()
                    val type = it["multiType"].toString().toInt()

                    val chat = Chat(content, name, type) // multiType:0 -> 내가 보낸 메세지

                    chatList.add(chat)
                }
                // 어답터 적용
                adapter = ChatAdapter(chatList, LayoutInflater.from(this), this)
                binding.chatRecyclerview.adapter = adapter
                binding.chatRecyclerview.scrollToPosition(chatList.size-1)
            }
    }


}

class Chat(
    val content: String,
    val name: String,
    val multiType: Int
)

class ChatAdapter(
    val chatList: MutableList<Chat>,
    val inflater: LayoutInflater,
    val context: Context
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun getItemViewType(position: Int): Int {
        return chatList[position].multiType

    }

    inner class myChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val myMsg : TextView
        init {
            myMsg = itemView.findViewById(R.id.chat_msg_me)
        }

    }

    inner class friendChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val friendMsg : TextView
        val name: TextView
        init {
            friendMsg = itemView.findViewById(R.id.chat_msg_friend)
            name = itemView.findViewById(R.id.chat_friend_name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            0->{ // 나의 메세지
                myChatViewHolder(inflater.inflate(R.layout.chat_content_me, parent, false))
            }
            else->{ // 친구의 메세지
                friendChatViewHolder(inflater.inflate(R.layout.chat_content_friend, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = chatList[position]
        when(chat.multiType){
            0->{ // 나의 메세지
                (holder as myChatViewHolder).myMsg.text = chat.content

            }
            else->{ // 친구의 메세지
                (holder as friendChatViewHolder).friendMsg.text = chat.content
                (holder as friendChatViewHolder).name.text = chat.name

            }
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}*/