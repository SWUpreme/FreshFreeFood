package com.example.fffroject.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.fffroject.AuthActivity
import com.example.fffroject.FFFroject
import com.example.fffroject.R
import com.example.fffroject.databinding.FragmentFridgeBinding
import com.example.fffroject.databinding.FragmentMypageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class FridgeFragment : Fragment() {
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null

    lateinit var binding: FragmentFridgeBinding

    lateinit var recyclerviewFridge: RecyclerView

    lateinit var btn_addFridge: Button
    lateinit var edt_fridgename: EditText
    lateinit var spinner: Spinner
    lateinit var select_fridge: String
    lateinit var fridgeid: String
    lateinit var recyclerview_fridge : RecyclerView
//    fun newInstance() : FridgeFragment {
//        return FridgeFragment()
//    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_fridge, container, false)

        // 파이어베이스 인증 객체
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        // 파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()

        recyclerview_fridge = view.findViewById(R.id.recyclerviewFridge)

        binding = FragmentFridgeBinding.inflate(layoutInflater)
        btn_addFridge = view.findViewById(R.id.btnFridgeAdd)

        // 로그아웃 처리
        btn_addFridge?.setOnClickListener {
            Toast.makeText(context,"냉장고 추가 누름", Toast.LENGTH_SHORT).show()
            addFridge()
        }

        return view
//        return inflater.inflate(R.layout.fragment_fridge, container, false)
    }

    // 냉장고 추가
    fun addFridge() {
        val builder = AlertDialog.Builder(activity)
        val dialogView = layoutInflater.inflate(R.layout.dialog_addfridge, null)

        edt_fridgename = dialogView.findViewById(R.id.edtFridgeName)

        builder.setView(dialogView)
            .setPositiveButton("등록") { dialogInterFace, i ->
                if (edt_fridgename.text.toString() != null) {
                    if (user != null) {
                        fridgeid = UUID.randomUUID().toString()
                        firestore?.collection("fridge")?.document("$fridgeid")
                            ?.set(hashMapOf("fid" to fridgeid, "fridgename" to edt_fridgename.text.toString()))
                            ?.addOnSuccessListener { }
                            ?.addOnFailureListener { }
                        firestore?.collection("user")?.document(user!!.uid)?.collection("userfridge")
                            ?.document("$fridgeid")
                            ?.set(hashMapOf("fid" to fridgeid))
                            ?.addOnSuccessListener {  }
                            ?.addOnFailureListener {  }
                    }
                }
                else {
                    Toast.makeText(activity, "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

}