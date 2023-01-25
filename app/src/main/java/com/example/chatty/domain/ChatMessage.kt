package com.example.chatty.domain

class ChatMessage(
    val id: String,
    val text: String,
    val fromId: String,
    val toId: String,
    val timeStamp: Long,
) {
    constructor(): this("", "", "", "", Long.MIN_VALUE)
}