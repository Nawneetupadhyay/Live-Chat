package com.example.livechat.data

data class UserData(
    val userId: String? = "",
    val name: String? = "",
    val number: String? = "",
    val imageUrl: String? = ""

)
{
    fun toMap() = mapOf( "userId" to userId,
        "name" to name,
        "number" to number,
        "imageUrl" to imageUrl)
}



data class Status(
    val user : ChatUser  = ChatUser(),
    val imageUrl: String?  = "",
    val timeStamp: String? = "",
)
data class ChatData(
    val chatId: String? = "",
    val user1 : ChatUser = ChatUser(),
    val user2 : ChatUser = ChatUser()
)

data class ChatUser(
    val userId: String? = "",
    val name: String? = "",
    val imageUrl: String? = "",
    val number: String? = ""
)


data class Message(
   var sendBy: String? = "",
    var message: String? = "",
    var timeStamp: String? = ""
)