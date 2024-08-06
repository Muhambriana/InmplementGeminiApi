package com.example.geminiapi

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.geminiapi.databinding.ActivityMainBinding
import com.google.ai.client.generativeai.GenerativeModel
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
            generateTextFromTextInput(generativeModel)
        }
    }

    // Generate text from text-only input
    private suspend fun generateTextFromTextInput(generativeModel: GenerativeModel) {
        val prompt = "Who is the ceo of google in 2020."
        val response = generativeModel.generateContent(prompt)
        binding.tvMain.text = response.text
    }
}