package com.example.fffroject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fffroject.databinding.ActivityMyshareBinding
import com.example.fffroject.databinding.ActivitySharepostBinding
import com.example.fffroject.fragment.CustomDiverItemDecoration
import com.example.fffroject.fragment.PostAll
import com.example.fffroject.fragment.ShareFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_share.*
import java.util.ArrayList

class MyShareActivity: AppCompatActivity() {
    // 바인딩 객체
    lateinit var binding: ActivityMyshareBinding
    // Data에 있는 PostAll
    lateinit var postAllList: ArrayList<PostAll>
    // 파이어스토어
    var auth : FirebaseAuth? = null
    var db : FirebaseFirestore? = null
    var user : FirebaseUser? = null
    // 리사이클러뷰
    lateinit var recyclerviewMyShare: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyshareBinding.inflate(layoutInflater)        // 바인딩 객체 획득
        setContentView(binding.root)                    // 액티비티 화면 출력

        postAllList = arrayListOf<PostAll>()            // Data에 있는 PostAll
        auth = FirebaseAuth.getInstance()               // 파이어베이스 인증 객체
        user = auth!!.currentUser
        db = FirebaseFirestore.getInstance()            // 파이어베이스 인스턴스 초기화

        // 파이어베이스에서 게시글 불러오기
        loadData()

        // 리사이클러뷰 바인딩
        recyclerviewMyShare = binding.recyclerviewMyShare
        // 레이아웃 매니저 등록
        recyclerviewMyShare.layoutManager = LinearLayoutManager(this)
        // 리사이클러 뷰 어댑터 등록
        recyclerviewMyShare.adapter = this.MyShareViewAdapter()
        // 리사이클러 뷰 구분선_커스텀 diver
        val customDecoration = CustomDiverItemDecoration(4f, 10f, resources.getColor(R.color.diver_gray))
        recyclerviewMyShare.addItemDecoration(customDecoration)

    }

    // 뷰 홀더
    //inner class ShareViewHolder(val binding: ItemSharelistBinding): RecyclerView.ViewHolder(binding.root)
    inner class ShareViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // 뷰 어댑터
    inner class MyShareViewAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        // 항목 개수를 판단
        override fun getItemCount(): Int {
            return postAllList.size
        }

        // 뷰 홀더 준비
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_mysharelist, parent, false)
            return ShareViewHolder(view)
        }

        // 뷰 홀더의 뷰에 데이터 호출 (실제 데이터 출력)
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            // 바인딩
            val viewHolder = (holder as ShareViewHolder).itemView
            var listTitle: TextView = viewHolder.findViewById(R.id.listTitle)
            var listRegion: TextView = viewHolder.findViewById(R.id.listRegion)
            var listLocation: TextView = viewHolder.findViewById(R.id.listLocation)
            var listName: TextView = viewHolder.findViewById(R.id.listName)
            var listDeadline: TextView = viewHolder.findViewById(R.id.listDeadline)
            var listCreatedAt: TextView = viewHolder.findViewById(R.id.listCreatedAt)

            // 뷰에 데이터 출력 (리사이클러 뷰 아이템 정보)
            listTitle.text = postAllList!![position].title
            listRegion.text = postAllList!![position].region
            listLocation.text = postAllList!![position].location
            listName.text = postAllList!![position].name
            listDeadline.text = postAllList!![position].deadline
            listCreatedAt.text = postAllList!![position].createdAt

            // 출력 외 게시글 요소
            var listFlag = postAllList!![position].flag
            var listIndex = postAllList!![position].index
            var listWriter = postAllList!![position].writer

            // 냉장고에서 넘기기 여부 확인 후 색상 변경
            if(listFlag==true){
                listRegion.setBackgroundResource(R.drawable.txt_background_round2_blue)
                listLocation.setBackgroundResource(R.drawable.txt_background_round2_blue)
                listRegion.setTextColor(ContextCompat.getColor(this@MyShareActivity!!, R.color.white))
                listLocation.setTextColor(ContextCompat.getColor(this@MyShareActivity!!, R.color.white))
            }else{
                listRegion.setBackgroundResource(R.drawable.txt_background_round2_white)
                listLocation.setBackgroundResource(R.drawable.txt_background_round2_white)
                listRegion.setTextColor(ContextCompat.getColor(this@MyShareActivity!!, R.color.blueblack))
                listLocation.setTextColor(ContextCompat.getColor(this@MyShareActivity!!, R.color.blueblack))
            }

            // 객체 클릭 이벤트
            viewHolder.setOnClickListener{
                val intent = Intent(viewHolder.context, ShareDetailActivity::class.java)
                intent.putExtra("detailIndex", listIndex.toString())
                intent.putExtra("detailFlag", listFlag.toString())
                intent.putExtra("detailWriter", listWriter.toString())
                ContextCompat.startActivity(viewHolder.context, intent, null)
            }

        }
    }

    // 파이어베이스에서 데이터 불러오는 함수
    private fun loadData() {
        // 현재 지역 이름
        //presentRegion = txtRegionSelect.text as String

        if (user != null) {
            if (user != null) {
                db?.collection("post")
//                    ?.whereEqualTo("region", presentRegion)
                    ?.orderBy("dateTime", Query.Direction.DESCENDING)
                    ?.addSnapshotListener { value, error ->
                        postAllList.clear()
                        if (value != null) {
                            // 나눔 없음 텍스트 INVISIBLE
                            txtNoRegion.setVisibility(View.INVISIBLE)
                            for (snapshot in value.documents) {
                                var item = snapshot.toObject(PostAll::class.java)
                                if (item != null) {
                                    Log.d("region:", item.region.toString())
                                    postAllList.add(item)
                                }
                            }
                        }else{
                            // 나눔 없음 텍스트 VISIBLE
                            txtNoRegion.setVisibility(View.VISIBLE)
                        }
                        recyclerviewMyShare.adapter?.notifyDataSetChanged()
                    }
            }
        }

    }

}