package com.example.geminiapi.utils.helper

import android.app.Activity
import android.widget.Toast

object Helper {
    fun Activity.showLongToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    fun Activity.showShortToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}