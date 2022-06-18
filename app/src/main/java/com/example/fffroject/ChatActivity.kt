package com.example.fffroject

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler
import android.util.Log;
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View;
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout;
import android.widget.TextView
import com.example.fffroject.fragment.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import java.util.*

/*
class ChatActivity : AppCompatActivity() {

    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    private var detailIndex : String? = null     //chatRoomUid
    private var detailWriter : String? = null  //destinationUid
    private var uid : String? = null
    private var recyclerView : RecyclerView? = null

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val imageView = findViewById<ImageView>(R.id.messageActivity_ImageView)
        val editText = findViewById<TextView>(R.id.messageActivity_editText)

        //메세지를 보낸 시간
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM월dd일 hh:mm")
        val curTime = dateFormat.format(Date(time)).toString()

        detailWriter = intent.getStringExtra("detailWriter")
        uid = Firebase.auth.currentUser?.uid.toString()
        recyclerView = findViewById(R.id.chatActivity_recyclerview)

        imageView.setOnClickListener {
            Log.d("클릭 시 dest", "$detailWriter")
            val chatModel = ChatModel()
            chatModel.user.put(uid.toString(), true)
            chatModel.user.put(detailWriter!!, true)

            val comment = ChatModel.Comment(uid, editText.text.toString(), curTime)
            if(detailIndex == null){
                imageView.isEnabled = false

                firestore!!.collection("chatModel").add(chatModel).addOnSuccessListener {
                    //채팅방 생성
                    checkChatRoom()
                    //메세지 보내기
                    Handler().postDelayed({
                        println(detailIndex)
                        firestore!!.collection("chatModel").document(detailIndex .toString()).collection("comments").add(comment)
                        messageActivity_editText.text = null
                    }, 1000L)
                    Log.d("chatUidNull dest", "$detailWriter")
                }
            }else{
                firestore!!.collection("chatModel").document(detailIndex .toString()).collection("comments").add(comment)
                messageActivity_editText.text = null
                Log.d("chatUidNotNull dest", "$detailWriter")
            }
        }
        checkChatRoom()
    }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
            val view : View = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)

            return ChatViewHolder(view)
        }
        @SuppressLint("RtlHardcoded")
        override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
            holder.chat_tv.textSize = 20F
            holder.chat_tv.text = comments[position].chat
            holder.time_tv.text = comments[position].time
            if(comments[position].uid.equals(uid)){ // 본인 채팅
                holder.chat_tv.setBackgroundResource(R.drawable.bg_send_message)
                holder.name_tv.visibility = View.INVISIBLE
                holder.layout_destination.visibility = View.INVISIBLE
                holder.layout_main.gravity = Gravity.RIGHT
            }else{ // 상대방 채팅
                holder.name_tv.text = user?.name
                holder.layout_destination.visibility = View.VISIBLE
                holder.name_tv.visibility = View.VISIBLE
                holder.chat_tv.setBackgroundResource(R.drawable.bg_receive_message)
                holder.layout_main.gravity = Gravity.LEFT
            }
        }

        inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val chat_tv: TextView = view.findViewById(R.id.chat_tv)
            val name_tv: TextView = view.findViewById(R.id.name_tv)
            val layout_destination: LinearLayout = view.findViewById(R.id.layout_destination)
            val layout_main: LinearLayout = view.findViewById(R.id.linearlayout_main)
            val time_tv : TextView = view.findViewById(R.id.time_tv)
        }

        override fun getItemCount(): Int {
            return comments.size
        }
    }
}*/
