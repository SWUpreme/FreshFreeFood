

package com.example.fffroject

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fffroject.databinding.ActivityChatDetailBinding
import com.example.fffroject.databinding.DialogSharedoneBinding
import com.example.fffroject.fragment.ChatDetail
import com.example.fffroject.fragment.CustomDiverItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatDetailActivity : AppCompatActivity() {
    // 바인딩 객체
    lateinit var binding: ActivityChatDetailBinding
    // 파이어스토어
    var auth: FirebaseAuth? = null
    var db: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    // Data에 있는 ChatDetail
    lateinit var chatDetailList: ArrayList<ChatDetail>
    // 리사이클러뷰
    lateinit var recyclerview: RecyclerView
    // 인텐트
    var chatroomId: String? = null
    var postId: String? = null
    var giverId: String? = null
    var takerId: String? = null
    var opponentId: String? = null
    var oppoentNickname: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 바인딩
        binding = ActivityChatDetailBinding.inflate(layoutInflater)        // 바인딩 객체 획득
        setContentView(binding.root)                                     // 액티비티 화면 출력
        // 채팅방 리스트 초기화
        chatDetailList = arrayListOf<ChatDetail>()                           // Data에 있는 ChatDetail
        // 파이어베이스
        auth = FirebaseAuth.getInstance()                                // 파이어베이스 인증 객체
        user = auth!!.currentUser
        db = FirebaseFirestore.getInstance()                             // 파이어베이스 인스턴스 초기화
        // 인텐트
        chatroomId = intent.getStringExtra("chatroomIndex")      // 채팅방 아이디
        postId = intent.getStringExtra("postIndex")              // 포스트 아이디
        opponentId = intent.getStringExtra("opponentId")            // 상대방 아이디
        giverId = intent.getStringExtra("giverId")                  // 나눔자 아이디
        takerId = intent.getStringExtra("takerId")                  // 피나눔자 아이디
        oppoentNickname = intent.getStringExtra("oppoentNickname")  // 상대방 닉네임
        // 파이어베이스에서 데이터 불러오기
        loadChatDetail()    // 쪽지 불러오기
        loadPostName()      // 게시글 이름 불러오기
        checkBtn()          // 버튼 visble 처리

        //리사이클러뷰
        recyclerview = binding.chatDetailRecyclerView                     // 리사이클러뷰 바인딩
        recyclerview.adapter = this.ChatDetailRecyclerViewAdapter()            // 리사이클러 뷰 어댑터 등록
        recyclerview.layoutManager = LinearLayoutManager(this) // 레이아웃 매니저 등록
        // 리사이클러 뷰 구분선_커스텀 diver
        val customDecoration = CustomDiverItemDecoration(4f, 10f, resources.getColor(R.color.diver_gray))
        recyclerview.addItemDecoration(customDecoration)

        // 툴바 쪽지 전송 버튼
        binding.toolbChatDetail.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.btnChatContent -> {
                    val intent = Intent(this, ChatSendExistActivity::class.java)
                    intent.putExtra("chatroomId", chatroomId)
                    intent.putExtra("postId", postId)
                    intent.putExtra("opponentId", opponentId)
                    intent.putExtra("giverId", giverId)
                    intent.putExtra("takerId", takerId)
                    ContextCompat.startActivity(this, intent, null)
                    true
                }
                else -> false
            }
        }

        // 거래완료 버튼
        binding.btnShareComplete.setOnClickListener{
            // 거래완료 다이얼로그 생성
            showDialogDelete(postId.toString())
        }

        // 별점 보내기 버튼
        binding.btnSendStar.setOnClickListener{
            // 별점 보내기 완료로 변경
            db?.collection("post")?.document(postId.toString())
                ?.update(
                    "pointDone", true
                )
                ?.addOnSuccessListener {}
                ?.addOnFailureListener {}
            val intent = Intent(this, SharePointActivity::class.java)
            intent.putExtra("opponentId", opponentId)                   // 상대방 아이디
            intent.putExtra("oppoentNickname", oppoentNickname)         // 상대방 닉네임
            intent.putExtra("postIndex", postId)                     // 포스트 인덱스
            ContextCompat.startActivity(this, intent, null)
        }
    }

    // 뷰 홀더
    inner class ChatDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    // 뷰 어댑터
    inner class ChatDetailRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        // 항목 개수를 판단
        override fun getItemCount(): Int {
            return chatDetailList.size
        }
        // 뷰 홀더 준비
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_detail_list, parent, false)
            return ChatDetailViewHolder(view)
        }
        // 뷰 홀더의 뷰에 데이터 호출 (실제 데이터 출력)
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            // 바인딩
            var viewHolder = (holder as ChatDetailViewHolder).itemView
            var tfFromTo: TextView
            var tfContext: TextView
            var tfTime: TextView

            tfFromTo = viewHolder.findViewById(R.id.chatDetailFromTo)
            tfContext = viewHolder.findViewById(R.id.chatDetailContext)
            tfTime = viewHolder.findViewById(R.id.chatDetailTime)

            var wroteId = chatDetailList!![position].writer
            tfContext.text = chatDetailList!![position].context
            tfTime.text = chatDetailList!![position].sendedAt

            // 보낸쪽지=파랑, 받은쪽지=빨강으로 색상 변경하기
            if (wroteId!!.equals(user?.uid)) { // 본인 채팅
                tfFromTo.setText("보낸 쪽지")
                tfFromTo.setTextColor(
                    ContextCompat.getColor(viewHolder.context, R.color.chat_2)
                )
            } else { // 상대방 채팅
                tfFromTo.setText("받은 쪽지")
                tfFromTo.setTextColor(
                    ContextCompat.getColor(viewHolder.context, R.color.chat_1)
                )
            }
        }
    }

    // 파이어베이스에서 채팅 데이터 불러오기
    fun loadChatDetail() {
        if (user != null) {
            // 채팅 불러오기
            db?.collection("chatroom")?.document(chatroomId.toString())
                ?.collection("chat")
                ?.orderBy("updatedAt", Query.Direction.DESCENDING)
                ?.addSnapshotListener { value, error ->
                    chatDetailList.clear()
                    if (value != null) {
                        // 쪽지 리스트에 추가하기
                        for (snapshot in value.documents) {
                            var item = snapshot.toObject(ChatDetail::class.java)
                            if (item != null) {
                                chatDetailList.add(item)
                            }
                        }
                    }
                    recyclerview.adapter?.notifyDataSetChanged()
                }
        }
    }

    // 파이어베이스에서 포스트 이름 불러오기+툴바 닉네임 변경
    @SuppressLint("SetTextI18n")
    fun loadPostName(){
        if (user != null) {
            db?.collection("post")?.document(postId.toString())?.get()
                ?.addOnSuccessListener { value ->
                    var title = value.data?.get("title") as String
                    binding.chatDetailContext.text = "\"$title\" 글을 통해 시작된 쪽지입니다."
                    binding.ChatName.text = oppoentNickname
                }
        }
    }

    // 나눔자=거래완료, 피나눔자=별점보내기(나눔이 완료됐다면) 버튼 보이기
    fun checkBtn(){
        if (user != null) {
            if(giverId==user!!.uid){
                // 유저가 나눔자라면
                if (user != null) {
                    db?.collection("post")?.document(postId.toString())?.get()
                        ?.addOnSuccessListener { value ->
                            var status :String = value.data?.get("status") as String
                            if(status.equals("shareDone") || status.equals("pointDone")){
                                // 나눔자가 나눔 완료했다면
                                binding.btnShareComplete.visibility = View.GONE
                                binding.btnSendStar.visibility = View.GONE
                            }else{
                                // 나눔 완료되지 않았다면
                                binding.btnShareComplete.visibility = View.VISIBLE
                                binding.btnSendStar.visibility = View.GONE
                            }
                        }
                }
            }else{
                // 유저가 피나눔자라면
                if (user != null) {
                    db?.collection("post")?.document(postId.toString())?.get()
                        ?.addOnSuccessListener { value ->
                            var status :String = value.data?.get("status") as String
//                            if(status.equals("shareDone") || status.equals("pointDone")){
//                                // 나눔자가 나눔 완료했다면
//                                if(pointDone==true){
//                                    // 피나눔자가 포인트를 줬다면
//                                    binding.btnShareComplete.visibility = View.GONE
//                                    binding.btnSendStar.visibility = View.GONE
//                                }else{
//                                    // 피나눔자가 포인트를 안 줬다면
//                                    binding.btnShareComplete.visibility = View.GONE
//                                    binding.btnSendStar.visibility = View.VISIBLE
//                                }
//                            }else{
//                                // 나눔 완료되지 않았다면
//                                binding.btnShareComplete.visibility = View.GONE
//                                binding.btnSendStar.visibility = View.GONE
//                            }

                            if(status.equals("shareDone")){
                                // 나눔 완료 & 포인트 미지급
                                binding.btnShareComplete.visibility = View.GONE
                                binding.btnSendStar.visibility = View.VISIBLE
                            }else if(status.equals("pointDone")){
                                // 나눔 완료 & 포인트 지급
                                binding.btnShareComplete.visibility = View.GONE
                                binding.btnSendStar.visibility = View.GONE
                            }else{
                                // 나눔 미완료 & 포인트 미지급
                                binding.btnShareComplete.visibility = View.GONE
                                binding.btnSendStar.visibility = View.GONE
                            }
                        }
                }
            }
        }
    }

    // 무료나눔 완료 버튼
    fun showDialogDelete(postIndex: String){
        //뷰 바인딩을 적용한 XML 파일 초기화
        val dialogBinding = DialogSharedoneBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(this).run {
            setView(dialogBinding.root)
            show()
        }
        //배경 투명으로 지정(모서리 둥근 배경 보이게 하기)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 아니오 버튼 (거래완료하지않기)
        dialogBinding.btnNo.setOnClickListener(View.OnClickListener {
            alertDialog.dismiss()
        })

        // 예 버튼 (거래완료하기)
        dialogBinding.btnYes.setOnClickListener(View.OnClickListener {
            db?.collection("post")?.document(postIndex.toString())
                ?.update(
                    "status", "shareDone"
                )
                ?.addOnSuccessListener {
                    Toast.makeText(this, "나눔이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                }
                ?.addOnFailureListener {}
            alertDialog.dismiss()
            // 거래 완료 후 전체 쪽지방 화면으로 돌아가기
            finish()
        })
    }
}
