package com.example.geminiapi.core.data.source.remote

import android.graphics.Bitmap
import android.util.Log
import com.example.geminiapi.BuildConfig
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

    suspend fun getStreamMessage(chat: Chat?, prompt: String): Flow<GenerateContentResponse>? =
        chat?.sendMessageStream(prompt)

    suspend fun getStreamMessage(chat: Chat?, prompt: Content): Flow<GenerateContentResponse>? =
        chat?.sendMessageStream(prompt)

    suspend fun getStreamMessage(chat: Chat?, prompt: Bitmap): Flow<GenerateContentResponse>? =
        chat?.sendMessageStream(prompt)
}