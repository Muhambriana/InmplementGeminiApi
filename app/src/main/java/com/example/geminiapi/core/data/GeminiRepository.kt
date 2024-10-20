package com.example.geminiapi.core.data

import android.graphics.Bitmap
import com.example.geminiapi.core.data.source.local.LocalDataSource
import com.example.geminiapi.core.data.source.remote.GeminiDataSource
import com.example.geminiapi.core.models.LocalChat
import com.example.geminiapi.utils.config.ChatType
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import kotlinx.coroutines.flow.Flow

class GeminiRepository(
    private val localDataSource: LocalDataSource,
    private val geminiDataSource: GeminiDataSource
) {

    suspend fun getStartChat(history: List<Content>): Chat =
        geminiDataSource.getStartChat(history)

    suspend fun getStreamMessage(chat: Chat?, prompt: String): Flow<GenerateContentResponse>? =
        geminiDataSource.getStreamMessage(chat, prompt)

    suspend fun getStreamMessage(chat: Chat?, prompt: Content): Flow<GenerateContentResponse>? =
        geminiDataSource.getStreamMessage(chat, prompt)

    suspend fun getStreamMessage(chat: Chat?, prompt: Bitmap): Flow<GenerateContentResponse>? =
        geminiDataSource.getStreamMessage(chat, prompt)

}