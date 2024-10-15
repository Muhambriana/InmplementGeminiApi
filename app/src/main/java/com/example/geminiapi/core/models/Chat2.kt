package com.example.geminiapi.core.models

import com.example.geminiapi.utils.config.ChatType
import com.google.ai.client.generativeai.type.Content

data class Chat2(
    var type: ChatType? = null,
    var userName: String? = null,
    var content: Content? = null,
)
