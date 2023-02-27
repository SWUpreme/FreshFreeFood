package com.example.fffroject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fffroject.databinding.ActivityKeywordBinding
import com.example.fffroject.fragment.KeyWord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class KeywordActivity : AppCompatActivity() {
    var auth: FirebaseAuth? = null
    var user : FirebaseUser? = null
    // 전역 변수로 바인딩 객체 선언
    private var mBinding: ActivityKeywordBinding? = null

    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mBinding!!
    var firestore: FirebaseFirestore? = null

    // Data에 있는 keyword와 연결
    val keywordList = arrayListOf<KeyWord>()
    val keyadapter = KeyWordAdapter(keywordList)
    var mDocuments: List<DocumentSnapshot>? = null
    lateinit var keyid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityKeywordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadData()
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()

        //키워드 추가버튼 클릭 시
        mBinding?.addkeywordBtn?.setOnClickListener {
            val input = binding.keywordInput
            keyid = UUID.randomUUID().toString()
            val nowTime = System.currentTimeMillis()
            val timeformatter = SimpleDateFormat("yyyy.MM.dd.hh.mm.ss")
            val dateTime = timeformatter.format(nowTime)
            firestore?.collection("user")?.document(user!!.uid)?.collection("mykeyword")
                ?.document("$keyid")
                ?.set(
                    hashMapOf(
                        "keyId" to keyid,
                        "keyword" to input.text.toString(),
                        "createdAt" to dateTime.toString(),
                        "updatedAt" to dateTime.toString(),
                        "status" to "active"
                    )
                )
                ?.addOnSuccessListener {
                    Toast.makeText(this, "키워드가 추가되었습니다", Toast.LENGTH_SHORT).show()
                    firestore?.collection("user")?.document(user!!.uid)?.collection("mykeyword")!!
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .addSnapshotListener { snapshot, exception ->
                            if (exception != null) {
                            } else {
                                if (snapshot != null) {
                                    if (!snapshot.isEmpty) {
                                        keywordList.clear()
                                        mDocuments = snapshot.documents
                                        val documents = snapshot.documents
                                        for (document in documents) {
                                            val item = KeyWord(document["keyword"] as String)
                                            keywordList.add(item)
                                        }
                                        keyadapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                }

                ?.addOnFailureListener { exception ->
                }
        }

        mBinding?.recyclerviewKeyword?.layoutManager = LinearLayoutManager(this)
        mBinding?.recyclerviewKeyword?.adapter = keyadapter

        //키워드 삭제
        (mBinding?.recyclerviewKeyword?.adapter as KeyWordAdapter)
        keyadapter.itemClick = object : KeyWordAdapter.ItemClick {
            override fun onClick(view: View, pos: Int) {
                when (view.id) {
                    R.id.delete_btn -> itemDelete(mDocuments!!.get(pos))
                }
            }
        }


    }

    //
    fun loadData() {

            firestore?.collection("user")?.document(user!!.uid)?.collection("mykeyword")
                ?.orderBy("createdAt", Query.Direction.DESCENDING)
                ?.addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                    } else {
                        if (snapshot != null) {
                            if (!snapshot.isEmpty) {
                                keywordList.clear()
                                mDocuments = snapshot.documents
                                val documents = snapshot.documents
                                for (document in documents) {
                                    val item = KeyWord(document["keyword"] as String)
                                    keywordList.add(item)
                                }
                                keyadapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
    }


    fun itemDelete(doc: DocumentSnapshot) {
        firestore?.collection("user")?.document(user!!.uid)?.collection("mykeyword")?.document(doc.id)
            ?.delete()

    }

    //종료
    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}