package com.example.chatty.util

import android.app.AlertDialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import com.example.chatty.R

fun String.isPasswordValid() = length >= 6

fun View.hideKeyboard() {
    val inputMethodManager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

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