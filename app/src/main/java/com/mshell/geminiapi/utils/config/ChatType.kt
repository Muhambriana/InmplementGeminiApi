package com.mshell.geminiapi.utils.config

enum class ChatType(val type: String, val alias: String) {
    USER("User", "You"),
    MODEL("Model", "Gemini");

    override fun toString(): String {
        return type
    }
}
