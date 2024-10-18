package com.example.geminiapi.ui.chat

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.geminiapi.BuildConfig
import com.example.geminiapi.core.adapter.ChatAdapter
import com.example.geminiapi.core.models.Chat
import com.example.geminiapi.databinding.ActivityRoomChatBinding
import com.example.geminiapi.utils.config.ChatType
import com.example.geminiapi.utils.helper.Helper.showShortToast
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RoomChatActivity : AppCompatActivity() {

    private val binding: ActivityRoomChatBinding by lazy {
        ActivityRoomChatBinding.inflate(layoutInflater)
    }

    private val chatAdapter: ChatAdapter by lazy {
        ChatAdapter()
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
            apiKey = BuildConfig.AI_STUDIO_API_KEY
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
                Chat(1, ChatType.USER, "You", history[0]),
                Chat(2, ChatType.MODEL, "Gemini", history[1])
            )
        )


        val question = "i have 3 new cat, and give me very short story telling about dog"
        val chatQuestion = Chat(3, ChatType.USER, "You", content(role = "user") {text(question)})
        chatAdapter.addItem(chatQuestion)
        chatAdapter.addItem( Chat(4, ChatType.MODEL, "Gemini", content(role = "model") { text("...") }))
        delay(2000)
        chat.sendMessageStream(question).collect { res ->
            chatAdapter.updateItem(Chat(4, ChatType.MODEL, "Gemini", content(role = "model") { text(res.text.toString()) }))
            binding.rvChat.smoothScrollToPosition(chatAdapter.itemCount - 1)
        }


        delay(5000)
        val question2 = "How many cat are in my house?, and total cat and dogs that i have?"
        val chatQuestion2 = Chat(5, ChatType.USER, "You", content(role = "user") {text(question2)})
        chatAdapter.addItem(chatQuestion2)
        chatAdapter.addItem( Chat(6, ChatType.MODEL, "Gemini", content(role = "model") { text("...") }))
        delay(2000)
        chat.sendMessageStream(question2).collect { res ->
            chatAdapter.updateItem(Chat(6, ChatType.MODEL, "Gemini", content(role = "model") { text(res.text.toString()) }))
            binding.rvChat.smoothScrollToPosition(chatAdapter.itemCount - 1)
        }
        var count = 6
        binding.btnSend.setOnClickListener {
            count += 1
            var currentId = count
            val question3 = binding.edPrompt.text.toString()
            val chatQuestion3 = Chat(currentId, ChatType.USER, "You", content(role = "user") {text(question3)})
            currentId+=1
            val geminiId = currentId
            showShortToast("$count === $currentId === $geminiId")
            chatAdapter.addItem(chatQuestion3)
            chatAdapter.addItem(Chat(geminiId, ChatType.MODEL, "Gemini", content(role = "model") { text("...") }))
            CoroutineScope(Dispatchers.Main).launch {
                delay(2000)
                chat.sendMessageStream(question3).collect { res ->
                    Log.d("kocak", "$question3 === $geminiId")
                    chatAdapter.updateItem(Chat(geminiId, ChatType.MODEL, "Gemini", content(role = "model") { text(res.text.toString()) }))
                    binding.rvChat.smoothScrollToPosition(chatAdapter.itemCount - 1)
                }
            }
        }


    }
}