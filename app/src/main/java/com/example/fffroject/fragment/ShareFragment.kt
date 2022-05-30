package com.example.fffroject.fragment

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fffroject.AuthActivity
import com.example.fffroject.R
import com.example.fffroject.SharePostActivity
import com.example.fffroject.databinding.ActivitySharepostBinding
import com.example.fffroject.databinding.FragmentShareBinding
import com.example.fffroject.databinding.ItemSharelistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sharepost.*
import kotlinx.coroutines.NonDisposableHandle.parent

class ShareFragment : Fragment() {
    // 파이어스토어
    var auth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    // 바인딩 객체
    //lateinit var binding: FragmentShareBinding
    lateinit var btnShareAdd: ImageButton
    lateinit var recyclerviewShare: RecyclerView


    // Data에 있는 PostAll이랑 해줘야해
    lateinit var postAllList: ArrayList<PostAll>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_share, container, false)

        postAllList = arrayListOf<PostAll>()

        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어베이스 인스턴스 초기화
        db = FirebaseFirestore.getInstance()

        // 바인딩 객체 획득
        //binding = FragmentShareBinding.inflate(layoutInflater)

        // 바인딩
        btnShareAdd= view.findViewById(R.id.btnShareAdd)
        recyclerviewShare= view.findViewById(R.id.recyclerviewShare)



        /*11111
        // 파이어베이스에서 냉장고 값 불러오기
        loadData()

        binding.recyclerviewShare.layoutManager = LinearLayoutManager(activity)
        binding.recyclerviewShare.adapter=ShareViewAdapter()

        */

        // 나눔 게시글 추가(게시글 추가 액티비티로 이동)
        btnShareAdd.setOnClickListener {
            val intent = Intent(activity, SharePostActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    /*22222222
    // 뷰 홀더
    inner class ShareViewHolder(val binding: ItemSharelistBinding): RecyclerView.ViewHolder(binding.root)

    // 뷰 어댑터
    inner class ShareViewAdapter():
        RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        // 항목 개수를 판단
        override fun getItemCount(): Int {
            return postAllList.size
        }

        // 뷰 홀더 준비
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ShareViewHolder(ItemSharelistBinding.inflate(LayoutInflater.from(parent.context), parent,false))

        // 뷰 홀더의 뷰에 데이터 호출 (실제 데이터 출력)
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val binding = (holder as ShareViewHolder).binding
            //뷰에 데이터 출력
            binding.listTitle.text = postAllList!![position].title
            binding.listLocation.text = postAllList!![position].location
            binding.listName.text = postAllList!![position].name
            binding.listDeadline.text = postAllList!![position].deadline
            binding.listCreatedAt.text = postAllList!![position].createdAt
        }
    }

    // 파이어베이스에서 데이터 불러오는 함수
    fun loadData() {
        // 냉장고 리스트 불러오기
        if (user != null) {
            db?.collection("post")
                ?.addSnapshotListener { value, error ->
                    postAllList.clear()
                    if (value != null) {
                        for (snapshot in value.documents) {
                            var item = snapshot.toObject(PostAll::class.java)
                            if (item != null) {
                                postAllList.add(item)
                            }
                        }
                    }
                    binding.recyclerviewShare.adapter?.notifyDataSetChanged()
                }
        }
    }

    */

    /* 민영쓰
    // 뷰 어댑터
    inner class ShareAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate((R.layout.item_sharelist), parent, false)
            return ViewHolder(view)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        // view와 실제 데이터 연결
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        }

        override fun getItemCount(): Int {
            return postAllList.size
        }
    }
     */

}