package com.example.geminiapi.core.models

import com.example.geminiapi.utils.config.ChatType

data class Chat(
    var type: ChatType? = null,
    var userName: String? = null,
    var message: String? = null,
)
