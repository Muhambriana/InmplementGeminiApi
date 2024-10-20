package com.example.geminiapi.utils.helper

import android.app.Activity
import android.widget.Toast
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.TextPart

object Helper {
    fun Activity.showLongToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    fun Activity.showShortToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    fun getTextFromContent(content: Content): String {
        return content.parts.filterIsInstance<TextPart>().joinToString(separator = " ") { part ->
            part.text // Extract the text from each TextPart
        }
    }


    // New function to append new text to existing text in Content
    fun appendTextToContent(content: Content, newText: String): Content {
        val updatedParts = content.parts.toMutableList() // Create a mutable list from existing parts

        // Check if there's a TextPart to update
        if (updatedParts.isNotEmpty() && updatedParts.last() is TextPart) {
            val lastTextPart = updatedParts.last() as TextPart
            val updatedText = lastTextPart.text + newText // Concatenate old text with new text
            updatedParts[updatedParts.size - 1] = TextPart(updatedText) // Update the last TextPart
        } else {
            // If there's no TextPart, simply add the new text as a new TextPart
            updatedParts.add(TextPart(newText))
        }

        return Content(content.role, updatedParts) // Return a new Content object with updated parts
    }
}