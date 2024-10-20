package com.example.geminiapi.utils.helper

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.geminiapi.core.models.LocalChat
import com.example.geminiapi.utils.config.ChatType
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.TextPart
import com.google.ai.client.generativeai.type.content

object DataMapper {
    fun mapToModel(lastChatId: Int, chatType: ChatType, text: String): LocalChat {
        val id = lastChatId.plus(1)
        return LocalChat(
            id,
            chatType,
            content(chatType.toString()) {text(text)}
        )
    }
}