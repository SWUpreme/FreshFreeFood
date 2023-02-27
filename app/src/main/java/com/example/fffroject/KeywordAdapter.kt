package com.example.fffroject
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.fffroject.fragment.KeyWord
import com.google.firebase.firestore.FirebaseFirestore

class KeyWordAdapter(val keywordList:ArrayList<KeyWord>): RecyclerView.Adapter<KeyWordAdapter.ViewHolder>() {

    //클릭 인터페이스 정의
    interface ItemClick{
        fun onClick(view: View, pos:Int)
    }
    //클릭 리스너 선언
    var itemClick:ItemClick?=null



    // xml파일을 inflate하여 ViewHolder를 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyWordAdapter.ViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_keyword, parent, false)
        return ViewHolder(view)
    }


    // onCreateViewHolder에서 만든 view와 실제 데이터를 연결
    override fun onBindViewHolder(holder: KeyWordAdapter.ViewHolder, position: Int) {
        holder.keyword.text = keywordList[position].keyword
        // 아이템 클릭 호출
        if (itemClick != null) {
            holder?.itemView?.setOnClickListener { v ->
                itemClick?.onClick(v, position)
            }
        }
        //키워드 삭제
        holder.delbtn.setOnClickListener {v:View->
            keywordList.removeAt(position)
            notifyItemRemoved(position)
            itemClick?.onClick(v,position)
        }
    }

    class ViewHolder(view:View): RecyclerView.ViewHolder(view){
        val keyword: TextView =view.findViewById(R.id.txtkeyword)
        val delbtn:ImageView=view.findViewById(R.id.delete_btn)
    }

    // 리사이클러뷰의 아이템 총 개수 반환
    override fun getItemCount(): Int {
        return keywordList.size
    }

}