package com.mshell.geminiapi.utils.config

import android.graphics.Bitmap
import com.google.ai.client.generativeai.type.Content

sealed class Prompt {
    data class TextPrompt(val text: String) : Prompt()
    data class BitmapPrompt(val bitmap: Bitmap) : Prompt()
    data class ContentPrompt(val content: Content) : Prompt()
}