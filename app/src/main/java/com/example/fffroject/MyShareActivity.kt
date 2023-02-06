package com.example.fffroject

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fffroject.databinding.*
import com.example.fffroject.fragment.CustomDiverItemDecoration
import com.example.fffroject.fragment.PostAll
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_share.*
import java.util.ArrayList

// 나의 나눔
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
            var title: TextView = viewHolder.findViewById(R.id.listTitle)
            var region: TextView = viewHolder.findViewById(R.id.listRegion)
            var location: TextView = viewHolder.findViewById(R.id.listLocation)
            var foodName: TextView = viewHolder.findViewById(R.id.listName)
            var deadline: TextView = viewHolder.findViewById(R.id.listDeadline)
            var postedAt: TextView = viewHolder.findViewById(R.id.listCreatedAt)
            var btnmore: Button = viewHolder.findViewById(R.id.listBtnmore)

            // 뷰에 데이터 출력 (리사이클러 뷰 아이템 정보)
            region.text = postAllList!![position].region
            location.text = postAllList!![position].location
            foodName.text = postAllList!![position].foodName
            deadline.text = postAllList!![position].deadline
            postedAt.text = postAllList!![position].postedAt

            // 출력 외 게시글 요소
            var fridgeToss = postAllList!![position].fridgeToss
            var postId = postAllList!![position].postId
            var writer = postAllList!![position].writer
            var status = postAllList!![position].status

            // 냉장고에서 넘기기 여부 확인 후 색상 변경
            if(fridgeToss==true){
                region.setBackgroundResource(R.drawable.txt_background_round2_blue)
                location.setBackgroundResource(R.drawable.txt_background_round2_blue)
                region.setTextColor(ContextCompat.getColor(this@MyShareActivity!!, R.color.white))
                location.setTextColor(ContextCompat.getColor(this@MyShareActivity!!, R.color.white))
            }else{
                region.setBackgroundResource(R.drawable.txt_background_round2_white)
                location.setBackgroundResource(R.drawable.txt_background_round2_white)
                region.setTextColor(ContextCompat.getColor(this@MyShareActivity!!, R.color.blueblack))
                location.setTextColor(ContextCompat.getColor(this@MyShareActivity!!, R.color.blueblack))
            }

            // 거래 완료 타이틀 표시
           if(status.equals("shareDone")||status.equals("pointDone")){
               // 완료된 거래라면
               var originTitle= postAllList!![position].title
               title.text = "(거래완료) "+originTitle
               title.setTextColor(ContextCompat.getColor(this@MyShareActivity!!, R.color.deep_gray))
           }else{
               // 진행중인 거래라면
               title.text = postAllList!![position].title
           }

            // 객체 클릭 이벤트
            viewHolder.setOnClickListener{
                val intent = Intent(viewHolder.context, ShareDetailActivity::class.java)
                intent.putExtra("detailIndex", postId.toString())
                intent.putExtra("detailFlag", fridgeToss.toString())
                intent.putExtra("detailWriter", writer.toString())
                ContextCompat.startActivity(viewHolder.context, intent, null)
            }

            btnmore.setOnClickListener{
                if (postId != null) {
                    showDialogMoreOption(postId, viewHolder)
                }
            }

        }
    }

    // 파이어베이스에서 데이터 불러오는 함수
    private fun loadData() {
        if (user != null) {
            db?.collection("post")
                ?.whereEqualTo("writer", user?.uid.toString())
                ?.orderBy("updatedAt", Query.Direction.DESCENDING)
                ?.addSnapshotListener { value, error ->
                    postAllList.clear()
                    if (value != null) {
                        // 나눔 없음 텍스트 INVISIBLE
                        txtNoRegion.setVisibility(View.INVISIBLE)
                        // 전체 게시글 리스트에 추가하기
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

    // 게시글 아이템 더보기 다이얼로그
    private fun showDialogMoreOption(postId: String, viewHolder: View){
        //뷰 바인딩을 적용한 XML 파일 초기화
        val dialogBinding = DialogPostoptionBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this).run {
            setView(dialogBinding.root)
            show()
        }//.setCanceledOnTouchOutside(true)  //외부 터치시 닫기
        // Custom Dialog 위치 조절
        alertDialog.window?.setGravity(Gravity.BOTTOM)

        //배경 투명으로 지정(모서리 둥근 배경 보이게 하기)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 수정 버튼
        dialogBinding.btnPostUpdate.setOnClickListener(View.OnClickListener {
            alertDialog.dismiss()
            // 게시글 수정 액티비티 실행
            val intent = Intent(viewHolder.context, ShareUpdateActivity::class.java)
            intent.putExtra("postId", postId)
            ContextCompat.startActivity(viewHolder.context, intent, null)
        })

        // 삭제 버튼
        dialogBinding.btnPostDelete.setOnClickListener(View.OnClickListener {
            alertDialog.dismiss()
            showDialogDelete(postId)
        })

        // 취소 버튼
        dialogBinding.btnCancle.setOnClickListener(View.OnClickListener {
            alertDialog.dismiss()
        })
    }

    // 게시글 삭제 다이얼로그
    private fun showDialogDelete(postId: String){
        //뷰 바인딩을 적용한 XML 파일 초기화
        val dialogBinding = DialogDeletepostBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this).run {
            setView(dialogBinding.root)
            show()
        }

        //배경 투명으로 지정(모서리 둥근 배경 보이게 하기)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 게시글 타이틀 불러오기
        db?.collection("post")?.document(postId)?.get()
            ?.addOnSuccessListener {value->
                var postTitle = value.data?.get("title") as String
                dialogBinding.textPostDelete.text = "'"+postTitle+"' 게시글을 삭제하시겠습니까?"
            }
            ?.addOnFailureListener { }

        // 취소 버튼
        dialogBinding.btnPostDeleteCancle.setOnClickListener(View.OnClickListener {
            alertDialog.dismiss()
        })

        // 삭제 확인 버튼
        dialogBinding.btnPostDelete.setOnClickListener(View.OnClickListener {
            db?.collection("post")?.document(postId)
                ?.delete()
                ?.addOnSuccessListener {
                    Toast.makeText(this, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
                ?.addOnFailureListener { }
            alertDialog.dismiss()
        })
    }
}