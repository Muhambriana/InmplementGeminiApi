package com.example.geminiapi.ui.chat

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.geminiapi.BuildConfig
import com.example.geminiapi.R
import com.example.geminiapi.core.adapter.ChatAdapter
import com.example.geminiapi.core.adapter.ChatAdapter2
import com.example.geminiapi.core.models.Chat
import com.example.geminiapi.core.models.Chat2
import com.example.geminiapi.databinding.ActivityRoomChatBinding
import com.example.geminiapi.utils.config.ChatType
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RoomChatActivity : AppCompatActivity() {

    private val binding: ActivityRoomChatBinding by lazy {
        ActivityRoomChatBinding.inflate(layoutInflater)
    }

    private val chatAdapter: ChatAdapter2 by lazy {
        ChatAdapter2()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            apiKey = BuildConfig.aiStudioApiKey
        )
        binding.rvChat.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(
                this@RoomChatActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
        }
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            multiConversationsStream(generativeModel)
        }
    }


    private fun getDataDummy(): MutableList<Chat> {
        return mutableListOf(
            Chat(ChatType.USER, "You", "How are you today?"),
            Chat(ChatType.MODEL, "Gemini", "Good, how about you?"),
            Chat(ChatType.USER, "You", "I'm doing well, thanks for asking!"),
            Chat(ChatType.MODEL, "Gemini", "That's great to hear! What are your plans for today?"),
            Chat(ChatType.USER, "You", "I plan to work on some coding projects."),
            Chat(ChatType.MODEL, "Gemini", "Sounds interesting! Which project are you working on?"),
            Chat(ChatType.USER, "You", "I'm building a chat application."),
            Chat(ChatType.MODEL, "Gemini", "That sounds fun! Do you need any help with it?"),
            Chat(ChatType.USER, "You", "I think I'm good for now, but I'll let you know if I need assistance."),
            Chat(ChatType.MODEL, "Gemini", "Sure! Just reach out whenever you need help.")
        )
    }

    // Multi-turn conversations (chat)
    private suspend fun multiConversationsStream(generativeModel: GenerativeModel) {
        val userText = "Hello, I have 2 dogs in my house."
        val modelText = "Great to meet you. What would you like to know?"
        val history = listOf(
            content(role = "user") { text(userText) },
            content(role = "model") { text(modelText) }
        )

        val chat = generativeModel.startChat(
            history
        )
        chatAdapter.setData(
            listOf(
                Chat2(ChatType.USER, "You", history[0]),
                Chat2(ChatType.MODEL, "Gemini", history[1])
            )
        )


        val question = "How many paws are in my house?, and give me long story telling about dog"
        val chatQuestion = Chat2(ChatType.USER, "You", content(role = "user") {text(question)})
        chatAdapter.addItem(chatQuestion)
        delay(2000)
        chat.sendMessageStream(question).collect { res ->
            chatAdapter.addItem(Chat2(ChatType.MODEL, "Gemini", content(role = "user") { text(res.text.toString()) }))
        }

    }
}