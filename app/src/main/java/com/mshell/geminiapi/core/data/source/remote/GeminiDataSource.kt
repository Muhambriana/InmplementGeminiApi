package com.mshell.geminiapi.core.data.source.remote

import com.mshell.geminiapi.BuildConfig
import com.mshell.geminiapi.utils.config.Prompt
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.flow.Flow


class GeminiDataSource {
    private val harassmentSafety = SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE)
    private val hateSpeechSafety = SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.LOW_AND_ABOVE)
    private val config = generationConfig {
        temperature = 0.9f
    }
    private val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.AI_STUDIO_API_KEY,
        config,
        safetySettings = listOf(harassmentSafety, hateSpeechSafety)
    )
    private val chat = generativeModel.startChat()

    fun getStartChat(history: List<Content>): Chat =
        generativeModel.startChat(history)

    fun getStreamMessage(prompt: Prompt): Result<Flow<GenerateContentResponse>?> =
        try {
            Result.success(
                when(prompt) {
                    is Prompt.TextPrompt -> {
                        chat.sendMessageStream(prompt.text)
                    }
                    is Prompt.BitmapPrompt -> {
                        chat.sendMessageStream(prompt.bitmap)
                    }
                    is Prompt.ContentPrompt -> {
                        chat.sendMessageStream(prompt.content)
                    }
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
}