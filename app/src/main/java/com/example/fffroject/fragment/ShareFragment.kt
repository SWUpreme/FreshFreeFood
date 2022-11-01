package com.example.fffroject.fragment

import android.app.Activity
import android.app.DownloadManager
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
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fffroject.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sharepost.*

import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_share.*
import java.util.*

class ShareFragment : Fragment() {
    // 파이어스토어
    var auth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    // 바인딩
    lateinit var btnShareAdd: ImageButton
    lateinit var btnSelectRegion: ImageButton
    lateinit var txtRegionSelect: TextView
    lateinit var webView: WebView
    lateinit var recyclerviewShare: RecyclerView
    lateinit var toolbar_sharepost: Toolbar
    lateinit var txtNoRegion: TextView

    lateinit var presentRegion : String

    // Data에 있는 PostAll
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

        // 바인딩
        btnShareAdd= view.findViewById(R.id.btnShareAdd)
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
                        val intent = Intent(activity, SharePostActivity::class.java)
                        intent.putExtra("region", txtRegionSelect.text)
                        startActivity(intent)
                    }else{
                        //양식 작성 안되어 있을 시
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
            //startActivity(intent)
            startForResult.launch(intent)
        }

        return view
    }

    // 콜백 받는 부분
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        // RegionSelectActivity로부터 결과값을 이곳으로 전달
        if (it.resultCode == Activity.RESULT_OK) {
            if(it.data != null){
                var regionData : String? = it.data!!.getStringExtra("data")
                txtRegionSelect.text = regionData
                db?.collection("user")?.document(user?.uid.toString())
                    ?.update("nowRegion", regionData)
                    ?.addOnSuccessListener {}
                loadData()      // 리사이클러뷰 재로딩
            }
        }
    }


    // 뷰 홀더
    //inner class ShareViewHolder(val binding: ItemSharelistBinding): RecyclerView.ViewHolder(binding.root)
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
                listRegion.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                listLocation.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            }else{
                listRegion.setBackgroundResource(R.drawable.txt_background_round2_white)
                listLocation.setBackgroundResource(R.drawable.txt_background_round2_white)
                listRegion.setTextColor(ContextCompat.getColor(context!!, R.color.blueblack))
                listLocation.setTextColor(ContextCompat.getColor(context!!, R.color.blueblack))
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
                            ?.orderBy("dateTime", Query.Direction.DESCENDING)
                            ?.addSnapshotListener { value, error ->
                                postAllList.clear()
                                if (value != null) {
                                    for (snapshot in value.documents) {
                                        var item = snapshot.toObject(PostAll::class.java)
                                        if (item != null) {
                                            Log.d("region:", item.region.toString())
                                            postAllList.add(item)
                                        }
                                    }
                                }
                                recyclerviewShare.adapter?.notifyDataSetChanged()
                            }
                    }
                }
                // 현재 지역 설정이 안 되어 있다면
                else{
                    // 지역 없음 텍스트 visible
                    txtNoRegion.setVisibility(View.VISIBLE)
                    // 검색창 텍스트를 없음으로 변경
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
