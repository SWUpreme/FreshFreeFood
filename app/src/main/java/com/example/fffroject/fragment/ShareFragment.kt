package com.example.fffroject.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fffroject.AuthActivity
import com.example.fffroject.R
import com.example.fffroject.SharePostActivity
import com.example.fffroject.databinding.ActivitySharepostBinding
import com.example.fffroject.databinding.FragmentShareBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class ShareFragment : Fragment() {
    // 파이어스토어
    var auth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    // 바인딩 객체
    lateinit var binding: FragmentShareBinding

    // Data에 있는 PostAll이랑 해줘야해
    lateinit var postalllist: ArrayList<PostAll>

    lateinit var recyclerview_share: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_share, container, false)

        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        db = FirebaseFirestore.getInstance()

        postalllist = arrayListOf<PostAll>()


        recyclerview_share = view.findViewById(R.id.recyclerviewShare)
        recyclerview_share.adapter = RecyclerViewAdapter()
        recyclerview_share.layoutManager = LinearLayoutManager(activity)

        // 나눔 게시글 추가(게시글 추가 액티비티로 이동)
        binding.btnShareAdd.setOnClickListener {
            val intent = Intent(activity, SharePostActivity::class.java)
            startActivity(intent)
        }


        return view
    }

    // 리사이클러뷰 사용
    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate((R.layout.item_sharelist), parent, false)
            return ViewHolder(view)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        // view와 실제 데이터 연결
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        }

        override fun getItemCount(): Int {
            return postalllist.size
        }
    }
}