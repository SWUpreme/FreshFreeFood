package com.example.fffroject.fragment
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap


// 나의 냉장고 리스트
data class MyFridge(
    var fridgeId : String? = null,
    var fridgeName : String? = null,
    var current : String? = null,
    //var status : Boolean? = null,
    var member : Int? = null,
    var createdAt : String? = null,
    //var updatedAt : String? = null,
    var status : String? = null
)

// 나눔 전체 게시글 리스트
data class PostAll(
    var postId :  String? =  null,
    var writer : String? = null,
    var title : String? = null,
    var region : String? = null,
    var location : String? = null,
    var foodName : String? = null,
    var deadline : String? = null,
    var postedAt : String? = null,
    var fridgeToss : Boolean? = null,
    var status : String? = null
)

// 나눔 세부 게시글 리스트
data class PostDetail(
    var postId :  String? =  null,
    var title : String? = null,
    var region : String? = null,
    var location : String? = null,
    var foodName : String? = null,
    var deadline : String? = null,
    var postedAt : String? = null,
    var fridgeToss : Boolean? = null,
    var writer : String? = null,
    var purchasedAt : String? = null,
    var content : String? = null,
    var done : Boolean? = null
)

// 식품 리스트 데이터
data class FoodList(
    var foodId : String? = null,
    var foodName : String? = null,
    var deadline : String? = null,
    var purchaseAt : String? = null,
    var count : Int = 0,
    var status :  String? = null,
    var createdAt : String? = null,
    var updatedAt : String? = null
)

// 채팅방
data class ChatRoom(
    var chatroomId : String? = null,
    var contextTxt :  String? =  null,
    var postId :  String? =  null,
    var taker : String? = null,
    var giver : String? = null,
    var sendedAt : String? = null
)
// 상세 채팅 리스트
data class ChatDetail(
    var chatId : String? = null,
    var writer :  String? =  null,
    var contextTxt :  String? =  null,
    var taker : String? = null,
    var giver : String? = null,
    var sendedAt : String? = null
)

data class MyChat(
    var context :  String? =  null,
    var from : String? = null,
    var index : String? = null,
    var to : String? = null,
    var sendedAt : String? = null,
)

data class Chat(
    var context :  String? =  null,
    var from : String? = null,
    var index : String? = null,
    var to : String? = null,
    var sendedAt : String? = null,
)

data class KeyWord(
    var keyword: String = "",
    var keyId : String? = null,
    var status :  String? = null,
    var createdAt : String? = null,
    var updatedAt : String? = null,

)