package com.example.fffroject

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.fffroject.fragment.FoodList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class WriteActivity : AppCompatActivity() {

    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    lateinit var food: ArrayList<food>

    lateinit var name: EditText
    lateinit var deadline_year:EditText
    lateinit var deadline_month: EditText
    lateinit var deadline_day: EditText
    lateinit var purchasedAt_year: EditText
    lateinit var purchasedAt_month: EditText
    lateinit var purchasedAt_day: EditText
    lateinit var count: EditText
    lateinit var upload_btn: Button

    lateinit var foodlist: ArrayList<FoodList>
    lateinit var foodindex: String

    var fridgeindex : String? = null
    var done = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        foodlist = arrayListOf<FoodList>()

        //food = arrayListOf<food>()
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        firestore = FirebaseFirestore.getInstance()


        name = findViewById(R.id.name)
        deadline_year = findViewById(R.id.fdeadlineYear)
        deadline_month = findViewById(R.id.fdeadlineMonth)
        deadline_day = findViewById(R.id.fdeadlineDate)
        purchasedAt_year = findViewById(R.id.fpurchasedAtYear)
        purchasedAt_month = findViewById(R.id.fpurchasedAtMonth)
        purchasedAt_day = findViewById(R.id.fpurchasedAtDate)
        count = findViewById(R.id.count)
        upload_btn = findViewById(R.id.scan_btn)

        fridgeindex = intent.getStringExtra("index")  // ????????? id

        // ????????? ??????
        upload_btn.setOnClickListener {

//            val food = hashMapOf(
//                "name" to name.text.toString(),
//                "deadline" to deadline.text.toString(),
//                "purchasedAt" to purchasedAt.text.toString(),
//                "count" to count.text.toString(),
//            )

            // ?????? ?????? ?????????????????? ??????
            if (user != null) {
                var food_deadline = deadline_year.text.toString()+"."+deadline_month.text.toString()+"."+deadline_day.text.toString()
                var purchasedAt = purchasedAt_year.text.toString()+"."+ purchasedAt_month.text.toString()+"."+ purchasedAt_day.text.toString()
                foodindex = UUID.randomUUID().toString()
                firestore?.collection("fridge")?.document("$fridgeindex")
                    ?.collection("food")?.document("$foodindex")
                    ?.set(
                        hashMapOf(
                            "index" to foodindex,
                            "name" to name.text.toString(),
                            "deadline" to food_deadline,
                            "purchaseAt" to purchasedAt,
                            "count" to count.text.toString().toInt(),
                            "done" to done
                        )
                    )
                    ?.addOnSuccessListener { finish() }
                    ?.addOnFailureListener {  }
            }


//            firestore!!.collection("food")
//                .add(food)
//                .addOnSuccessListener { documentReference ->
//                    Log.d(
//                        "DatabaseTest",
//                        documentReference.id
//                    )
//                }
//                .addOnFailureListener { exception -> Log.d("DatabaseTest", exception.message!!) }
//
//
            Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show()
        }
    }

}