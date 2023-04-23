package com.example.fffroject.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fffroject.*
import com.example.fffroject.share.ShareDetailActivity
import com.example.fffroject.share.SharePostActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.firestore.Query
import java.util.*

// 무료나눔 전체 리사이클러뷰
class ShareFragment : Fragment() {
    // 파이어스토어
    var auth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    // 바인딩
    lateinit var btnSelectRegion: ImageButton
    lateinit var txtRegionSelect: TextView
    lateinit var webView: WebView
    lateinit var recyclerviewShare: RecyclerView
    lateinit var toolbar_sharepost: Toolbar
    lateinit var txtNoRegion: TextView

    // 현재 지역
    lateinit var presentRegion : String

    // Data에 있는 PostAll
    lateinit var postAllList: ArrayList<PostAll>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_share, container, false)

        // 전체 게시글 리스트
        postAllList = arrayListOf<PostAll>()

        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어베이스 인스턴스 초기화
        db = FirebaseFirestore.getInstance()

        // 바인딩
        btnSelectRegion= view.findViewById(R.id.btnSelectRegion)
        txtRegionSelect = view.findViewById(R.id.txtRegionSelect)
        recyclerviewShare= view.findViewById(R.id.recyclerviewShare)
        toolbar_sharepost = view.findViewById(R.id.toolbShare)
        txtNoRegion = view.findViewById(R.id.txtNoRegion)

        // 파이어베이스에서 게시글 불러오기
        loadData()

        // 레이아웃 매니저 등록
        recyclerviewShare.layoutManager = LinearLayoutManager(activity)
        // 리사이클러 뷰 어댑터 등록
        recyclerviewShare.adapter = ShareViewAdapter()
        // 리사이클러 뷰 구분선_커스텀 diver
        //recyclerviewShare.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        val customDecoration = CustomDiverItemDecoration(4f, 10f, resources.getColor(R.color.diver_gray))
        recyclerviewShare.addItemDecoration(customDecoration)

        // 나눔 게시글 추가(게시글 추가 액티비티로 이동)
        toolbar_sharepost.setOnMenuItemClickListener{
            when(it.itemId) {
                R.id.btnPlus -> {
                    if(txtRegionSelect.text != "나눔 지역을 선택해주세요."){
                        // 지역 선택이 되어 있을 시
                        val intent = Intent(activity, SharePostActivity::class.java)
                        intent.putExtra("region", txtRegionSelect.text)
                        startActivity(intent)
                    }else{
                        // 지역 선택이 안 되어 있을 시
                        Toast.makeText(activity, "나눔 지역을 선택해주세요.", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> false
            }
        }

        // 주소 검색 웹뷰 화면으로 이동
        btnSelectRegion.setOnClickListener{
            val intent = Intent(activity, RegionSelectActivity::class.java)
            startForResult.launch(intent)
        }
        return view
    }

    // 웹뷰 화면의 콜백 받는 부분
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        // RegionSelectActivity로부터 결과값을 이곳으로 전달
        if (it.resultCode == Activity.RESULT_OK) {
            if(it.data != null){
                var regionData : String? = it.data!!.getStringExtra("data")
                txtRegionSelect.text = regionData
                // 받아온 지역을 db에 저장
                db?.collection("user")?.document(user?.uid.toString())
                    ?.update("nowRegion", regionData)
                    ?.addOnSuccessListener {}
                loadData()      // 리사이클러뷰 재로딩
            }
        }
    }


    // 뷰 홀더
    inner class ShareViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // 뷰 어댑터
    inner class ShareViewAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        // 항목 개수를 판단
        override fun getItemCount(): Int {
            return postAllList.size
        }

        // 뷰 홀더 준비
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_sharelist, parent, false)
            return ShareViewHolder(view)
        }

        // 뷰 홀더의 뷰에 데이터 호출 (실제 데이터 출력)
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            // 바인딩
            val viewHolder = (holder as ShareViewHolder).itemView
            var title: TextView = viewHolder.findViewById(R.id.listTitle)
            var region: TextView = viewHolder.findViewById(R.id.listRegion)
            var location: TextView = viewHolder.findViewById(R.id.listLocation)
            var foodName: TextView = viewHolder.findViewById(R.id.listName)
            var deadline: TextView = viewHolder.findViewById(R.id.listDeadline)
            var postedAt: TextView = viewHolder.findViewById(R.id.listCreatedAt)

            // 뷰에 데이터 출력 (리사이클러 뷰 아이템 정보)
            title.text = postAllList!![position].title
            region.text = postAllList!![position].region
            location.text = postAllList!![position].location
            foodName.text = postAllList!![position].foodName
            deadline.text = postAllList!![position].deadline
            postedAt.text = postAllList!![position].postedAt

            // 출력 외 게시글 요소
            var fridgeToss = postAllList!![position].fridgeToss
            var postId = postAllList!![position].postId
            var writer = postAllList!![position].writer

            // 냉장고에서 넘기기 여부 확인 후 색상 변경
            if(fridgeToss==true){
                // 냉장고 페이지에서 작성된 게시글이라면
                region.setBackgroundResource(R.drawable.txt_background_round2_blue)
                location.setBackgroundResource(R.drawable.txt_background_round2_blue)
                region.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                location.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            }else{
                // 무료 나눔 페이지에서 작성된 게시글이라면
                region.setBackgroundResource(R.drawable.txt_background_round2_white)
                location.setBackgroundResource(R.drawable.txt_background_round2_white)
                region.setTextColor(ContextCompat.getColor(context!!, R.color.blueblack))
                location.setTextColor(ContextCompat.getColor(context!!, R.color.blueblack))
            }

            // 객체 클릭 이벤트
            viewHolder.setOnClickListener{
                val intent = Intent(viewHolder.context, ShareDetailActivity::class.java)
                intent.putExtra("detailIndex", postId.toString())
                intent.putExtra("detailFlag", fridgeToss.toString())
                intent.putExtra("detailWriter", writer.toString())
                ContextCompat.startActivity(viewHolder.context, intent, null)
            }

        }
    }

    // 파이어베이스에서 데이터 불러오는 함수
    private fun loadData() {
        if(user != null){
            // 현재 지역 설정 조회
            db?.collection("user")?.document(user?.uid.toString())?.get()?.addOnSuccessListener { value ->
                presentRegion = value.data?.get("nowRegion") as String
                // 현재 지역 설정이 되어있다면
                if (presentRegion != "n"){
                    // 지역 없음 텍스트 unvisible
                    txtNoRegion.setVisibility(View.INVISIBLE)
                    // 검색창 텍스트를 설정된 지역으로 변경
                    txtRegionSelect.text = presentRegion
                    // 해당 지역 게시글 리스트 불러오기
                    if (user != null) {
                        db?.collection("post")
                            ?.whereEqualTo("region", presentRegion)
                            ?.whereEqualTo("status", "active")
                            ?.orderBy("updatedAt", Query.Direction.DESCENDING)
                            ?.addSnapshotListener { value, error ->
                                postAllList.clear()
                                if (value != null && !value.isEmpty) {
                                    txtNoRegion.setVisibility(View.INVISIBLE)
                                    for (snapshot in value.documents) {
                                        var item = snapshot.toObject(PostAll::class.java)
                                        if (item != null) {
                                            Log.d("region:", item.region.toString())
                                            postAllList.add(item)
                                        }
                                    }
                                }
                                // 현재 설정된 지역의 나눔이 없다면
                                else{
                                    // 지역 없음 텍스트 visible
                                    txtNoRegion.setVisibility(View.VISIBLE)
                                    // 안내 텍스트를 변경
                                    txtNoRegion.text = "나눔을 시작해주세요."
                                }
                                recyclerviewShare.adapter?.notifyDataSetChanged()
                            }
                    }
                }
                // 현재 지역 설정이 안 되어 있다면
                else{
                    // 지역 없음 텍스트 visible
                    txtNoRegion.setVisibility(View.VISIBLE)
                    // 검색창 텍스트와 안내 텍스트를 변경
                    txtNoRegion.text = "나눔 할 지역을 검색해 주세요."
                    txtRegionSelect.text = "나눔 지역을 선택해주세요."
                }
            }
        }
    }
}

// 커스텀 divider 추가
class CustomDiverItemDecoration(
    private val height: Float,
    private val padding: Float,
    @ColorInt
    private val color: Int
) : RecyclerView.ItemDecoration() {

    private val paint = Paint()

    init {
        paint.color = color
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingStart + padding
        val right = parent.width - parent.paddingEnd - padding

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = (child.bottom + params.bottomMargin).toFloat()
            val bottom = top + height

            c.drawRect(left, top, right, bottom, paint)
        }
    }
}
