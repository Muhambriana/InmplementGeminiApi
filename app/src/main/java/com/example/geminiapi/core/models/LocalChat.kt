package com.example.geminiapi.core.models

import com.example.geminiapi.utils.config.ChatType
import com.google.ai.client.generativeai.type.Content

data class LocalChat(
    var id: Int? = null,
    var type: ChatType? = null,
    var content: Content,
)
