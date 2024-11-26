package com.blissvine.xchat.models

data class ChatData(
    val chatId: String? = "",
    var user1: ChatUser = ChatUser(),
    var user2: ChatUser = ChatUser(),

    )

data class ChatUser(
    val userId: String? = "",
    val name: String? = "",
    val imageUrl: String? = "",
    val number: String? = "",
)

data class Message(
    val sentBy: String? = "",
    val message: String? = "",
    val timeStamp: String? = ""
)


data class Status(
    val user: ChatUser = ChatUser(),
    val imageUrl: String? = "",
    val timeStamp: Long? = null
)

data class TypingStatus(
    val userId: String? = "",
    val isTyping: Boolean = false
)
