package com.example.chatty.util

import android.app.AlertDialog
import android.content.Context
import android.widget.Button
import com.example.chatty.R

fun String.isPasswordValid() = length >= 6

fun basicAlert(
    context: Context,
    title: String,
    message: String,
    setPositiveButton: () -> Unit,
): AlertDialog? {
    val builder = AlertDialog.Builder(context)
    return builder.apply {
        setTitle(title)
        setMessage(message)
        setPositiveButton(R.string.im_sure) { dialog, _ ->
            setPositiveButton()
            dialog.dismiss()
        }
        setNegativeButton(R.string.never_mind) { dialog, _ -> dialog.dismiss() }
    }.create()
}