package com.example.geminiapi.core.data

import com.example.geminiapi.core.data.source.local.LocalDataSource
import com.example.geminiapi.core.data.source.remote.GeminiDataSource
import com.example.geminiapi.utils.config.Prompt
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

    suspend fun getStreamMessage(chat: Chat?, prompt: Prompt): Flow<GenerateContentResponse>? =
        geminiDataSource.getStreamMessage(chat, prompt)

}