package com.example.chatty.domain

class User(val uid: String, val username: String, val imageUri: String) {
    constructor(): this("", "", "")
}
