package com.example.fffroject.fragment
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap


// 나의 냉장고 리스트
data class MyFridge(
    var index : String? = null,
    var name : String? = null,
    var current : String? = null
)

// 나눔 전체 게시글 리스트
data class PostAll(
    var index :  String? =  null,
    var writer : String? = null,
    var title : String? = null,
    var region : String? = null,
    var location : String? = null,
    var name : String? = null,
    var deadline : String? = null,
    var createdAt : String? = null,
    var flag : Boolean? = null,
    var done : Boolean? = null
)

// 나눔 세부 게시글 리스트
data class PostDetail(
    var index :  String? =  null,
    var title : String? = null,
    var region : String? = null,
    var location : String? = null,
    var name : String? = null,
    var deadline : String? = null,
    var createdAt : String? = null,
    var flag : Boolean? = null,
    var writer : String? = null,
    var purchasedAt : String? = null,
    var content : String? = null,
    var done : Boolean? = null
)

// 식품 리스트 데이터
data class FoodList(
    var index : String? = null,
    var name : String? = null,
    var deadline : String? = null,
    var purchaseAt : String? = null,
    var count : Int = 0,
    var done :  Boolean = false
)

//입력
data class food(
    var name :String? = null,
    var deadline : String? = null,
    var purchasedAt : String? = null,
    var count : String? = null

)

//채팅
data class ChatDTO(
    var context :  String? =  null,
    var from : String? = null,
    var index : String? = null,
    var to : String? = null,
    var sendedAt : String? = null,

    )


data class MyChat(
    var index : String? = null,
    var from : String? = null,
    var context : String? = null,
    var sendedAt : String? = null,
)