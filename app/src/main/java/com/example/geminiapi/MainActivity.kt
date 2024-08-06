package com.example.geminiapi

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.geminiapi.databinding.ActivityMainBinding
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firstInit()
    }

    private fun firstInit() {
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.aiStudioApiKey
        )

        CoroutineScope(Dispatchers.Main).launch {
            binding.tvMain.text = generateTextFromTextAndImageInput(generativeModel)
        }
    }

    // Generate text from text-only input
    private suspend fun generateTextFromTextInput(generativeModel: GenerativeModel): String? {
        val prompt = "Apakah kamu bisa berbahasa indonesia?"
        val response = generativeModel.generateContent(prompt)
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
}