package com.example.geminiapi.core.data.source.remote

import com.example.geminiapi.BuildConfig
import com.example.geminiapi.utils.config.Prompt
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import kotlinx.coroutines.flow.Flow


class GeminiDataSource {
    private val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.AI_STUDIO_API_KEY
    )

    suspend fun getStartChat(history: List<Content>): Chat =
        generativeModel.startChat(history)

    suspend fun getStreamMessage(chat: Chat?, prompt: Prompt): Flow<GenerateContentResponse>? =
        when(prompt) {
            is Prompt.TextPrompt -> {
                chat?.sendMessageStream(prompt.text)
            }
            is Prompt.BitmapPrompt -> {
                chat?.sendMessageStream(prompt.bitmap)
            }
            is Prompt.ContentPrompt -> {
                chat?.sendMessageStream(prompt.content)
            }
        }
}