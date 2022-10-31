
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
import com.example.fffroject.fragment.MyChat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_chat_list.*

class ChatListActivity : AppCompatActivity() {

    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null
    var user : FirebaseUser? = null

    lateinit var chatlist: ArrayList<MyChat>

    lateinit var chatid: String
    lateinit var recyclerview_chat: RecyclerView

    lateinit var text_fridge_name: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        chatlist = arrayListOf<MyChat>()


        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        firestore = FirebaseFirestore.getInstance()


        loadChat()

        recyclerview_chat = findViewById(R.id.chat_recyclerview)
        recyclerview_chat.adapter = ChatRecyclerViewAdapter()
        recyclerview_chat.layoutManager = LinearLayoutManager(this)
    }


    // 리사이클러뷰 사용
    inner class ChatRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_chat_list, parent, false)
            return ViewHolder(view)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        // view와 실제 데이터 연결결
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as ViewHolder).itemView
            var chatname: TextView
            var chatcontent: TextView
            var chattime: TextView

            chatname = viewHolder.findViewById(R.id.chat_tv_nickname)
            chatcontent = viewHolder.findViewById(R.id.chat_tv_contents)
            chattime = viewHolder.findViewById(R.id.chat_tv_time)


            // 리사이클러뷰 아이템 정보
            chatname.text = chatlist!![position].from
            chatid = chatlist!![position].index!!
            chatcontent.text = chatlist!![position].context
            chattime.text = chatlist!![position].sendedAt




             viewHolder.setOnClickListener {
                 val intent = Intent(viewHolder.context, ChatDetailActivity::class.java)
                 ContextCompat.startActivity(viewHolder.context, intent, null)
             }

        }

        override fun getItemCount(): Int {
            return chatlist.size
        }

    }


    // 파이어베이스에서 mychat 불러오기
    fun loadChat() {
        if (user != null) {
            firestore?.collection("user")?.document(user!!.uid)
                ?.collection("mychat")
                ?.addSnapshotListener { value, error ->
                    chatlist.clear()
                    if (value != null) {
                        for (snapshot in value.documents) {
                            var item = snapshot.toObject(MyChat::class.java)
                            if (item != null) {
                                chatlist.add(item)
                            }
                        }
                    }
                    recyclerview_chat.adapter?.notifyDataSetChanged()
                }
        }
    }
}