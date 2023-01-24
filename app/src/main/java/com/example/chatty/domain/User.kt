package com.example.chatty.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class User(val uid: String, val username: String, val imageUri: String) : Parcelable {
    constructor(): this("", "", "")
}
