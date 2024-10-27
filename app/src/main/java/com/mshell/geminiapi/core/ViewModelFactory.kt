package com.mshell.geminiapi.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mshell.geminiapi.core.data.GeminiRepository
import com.mshell.geminiapi.ui.chat.RoomChatViewModel


class ViewModelFactory(private val discalRepository: GeminiRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RoomChatViewModel::class.java) -> {
                RoomChatViewModel(this.discalRepository) as T
            }
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}
