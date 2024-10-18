package com.example.geminiapi.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.geminiapi.BuildConfig
import com.example.geminiapi.R
import com.example.geminiapi.databinding.ActivityMainBinding
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firstInit()
    }

    private fun firstInit() {
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.AI_STUDIO_API_KEY
        )

        binding.btnSubmit.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
               multiConversationsStream(generativeModel)
            }
        }
    }

    // Generate text from text-only input
    private suspend fun generateTextFromTextInput(generativeModel: GenerativeModel): String? {
        val prompt = binding.edPrompt.text.toString()
        val response = generativeModel.generateContent(prompt)
        val resultBuilder = StringBuilder() // Use StringBuilder for efficient concatenation
        lifecycleScope.launch(Dispatchers.IO){ // Use coroutine for background processing
            generativeModel.generateContentStream(prompt).collect { chunk ->
                resultBuilder.append(chunk.text)
                withContext(Dispatchers.Main) { // Update UI on main thread less frequently
                    binding.tvMain.text = resultBuilder.toString()
                }
            }
        }

        return response.text
    }

    // Generate text from text-and-image input (multimodal)
    private suspend fun generateTextFromTextAndImageInput(generativeModel: GenerativeModel): String? {
        val text1 = "What's different between these pictures?"
        val text2 = "What the content of the image?"
        val image1: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.linux)
        val image2: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.linux)

        val inputContent = content {
            image(image1)
            image(image2)
            text(text1)
            text(text2)
        }

        val response = generativeModel.generateContent(inputContent)
        return response.text
    }

    // Multi-turn conversations (chat)
    private suspend fun multiConversations(generativeModel: GenerativeModel): String? {
        val userText = "Hello, I have 2 dogs in my house."
        val modelText = "Great to meet you. What would you like to know?"
        val chat = generativeModel.startChat(
            listOf(
                content(role = "user") { text(userText) },
                content(role = "model") { text(modelText) }
            )
        )

        val response = chat.sendMessage("How many paws are in my house?")
        return response.text
    }

    // Multi-turn conversations (chat)
    private suspend fun multiConversationsStream(generativeModel: GenerativeModel) {
        val userText = "Hello, I have 2 dogs in my house."
        val modelText = "Great to meet you. What would you like to know?"
        val chat = generativeModel.startChat(
            listOf(
                content(role = "user") { text(userText) },
                content(role = "model") { text(modelText) }
            )
        )

        chat.sendMessageStream("How many paws are in my house?, and give me long story telling about dog").collect {
            binding.tvMain.text = it.text
        }

    }
}