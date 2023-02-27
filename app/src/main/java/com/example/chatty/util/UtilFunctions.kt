package com.example.chatty.util

import android.content.Context.INPUT_METHOD_SERVICE
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone.getTimeZone

fun String.isPasswordValid() = length >= 6

fun View.hideKeyboard() {
    val inputMethodManager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

object StringUtils {
    const val EMPTY = ""
}

/*
private fun setTime(date: Int): String? {
    var time_text = ""
    val time = System.currentTimeMillis() / 1000
    val mills = time - date
    val hours: Int = safeLongToInt(mills / (60 * 60))
    val mins: Int = safeLongToInt((mills - hours * 3600 / 60) % 60)
    val diff = "$hours:$mins"
    if (hours < 24) {
        duration.setText("$diff hours ago")
    } else {
        val dateTemp = Date(date * 1000L)
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss ")
        sdf.timeZone = getTimeZone("GMT+2")
        time_text = sdf.format(dateTemp)
        return time_text
    }
    return time_text
}
*/