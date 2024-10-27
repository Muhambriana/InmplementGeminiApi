package com.mshell.geminiapi.utils.helper

import com.mshell.geminiapi.core.models.LocalChat
import com.mshell.geminiapi.utils.config.ChatType
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