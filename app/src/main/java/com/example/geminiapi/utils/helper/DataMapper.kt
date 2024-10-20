package com.example.geminiapi.utils.helper

import com.example.geminiapi.core.models.LocalChat
import com.example.geminiapi.utils.config.ChatType
import com.google.ai.client.generativeai.type.content

object DataMapper {
    fun mapToModel(lastChatId: Int, chatType: ChatType, text: String): LocalChat {
        val id = lastChatId + if (chatType == ChatType.USER) 1 else 2
        return LocalChat(
            id,
            chatType,
            content(chatType.toString()) { text(text) }
        )
    }
}