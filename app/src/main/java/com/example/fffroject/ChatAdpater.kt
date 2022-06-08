package com.example.fffroject

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fffroject.fragment.Chat

// 리사이클러 뷰 어댑터 클래스
class ChatAdapter(val currentUser: String, val itemList: ArrayList<Chat>): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ChatAdapter.ViewHolder, position: Int) {
        // 현재 닉네임과 글쓴이의 닉네임이 같을 경우 배경을 노란색으로 변경
        if (currentUser == itemList[position].nickname) {
            holder.card.setCardBackgroundColor(Color.parseColor("#FFF176"))
        }
        holder.nickname.text = itemList[position].nickname
        holder.contents.text = itemList[position].contents

    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView.findViewById(R.id.chat_card_view)
        val nickname: TextView = itemView.findViewById(R.id.chat_tv_nickname)
        val contents: TextView = itemView.findViewById(R.id.chat_tv_contents)

    }
}