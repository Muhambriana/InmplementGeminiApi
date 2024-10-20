package com.example.geminiapi.ui.chat

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.geminiapi.core.ViewModelFactory
import com.example.geminiapi.core.adapter.ChatAdapter
import com.example.geminiapi.core.data.GeminiRepository
import com.example.geminiapi.core.data.Resource
import com.example.geminiapi.core.data.source.local.LocalDataSource
import com.example.geminiapi.core.data.source.remote.GeminiDataSource
import com.example.geminiapi.core.models.LocalChat
import com.example.geminiapi.databinding.ActivityRoomChatBinding
import com.example.geminiapi.utils.config.ChatType
import com.example.geminiapi.utils.config.Prompt
import com.example.geminiapi.utils.helper.DataMapper
import com.example.geminiapi.utils.helper.Helper.showShortToast
import com.google.ai.client.generativeai.Chat

class RoomChatActivity : AppCompatActivity() {

    private val binding: ActivityRoomChatBinding by lazy {
        ActivityRoomChatBinding.inflate(
            layoutInflater
        )
    }
    private val geminiDataSource: GeminiDataSource by lazy { GeminiDataSource() }
    private val localDataSource: LocalDataSource by lazy { LocalDataSource() }
    private var chat: Chat? = null
    private var listLocalChat = mutableListOf<LocalChat>()

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var roomChatViewModel: RoomChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initialization()
    }

    private fun initialization() {
        roomChatViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                GeminiRepository(
                    localDataSource,
                    geminiDataSource
                )
            )
        )[RoomChatViewModel::class.java]
        chatAdapter = ChatAdapter()
        startChat()
        showRecyclerList()

        viewInitialization()
    }

    private fun viewInitialization() {
        binding.btnSend.setOnClickListener {
            askGemini()
        }
    }

    private fun showRecyclerList() {
        binding.rvChat.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(
                this@RoomChatActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
        }
    }

    private fun startChat() {
        roomChatViewModel.getStartChat()
        roomChatViewModel.geminiChat.observe(this) { resources ->
            when (resources) {
                is Resource.Loading -> {}
                is Resource.Success -> { chat = resources.data }
                is Resource.Empty -> {}
                is Resource.Error -> {}
            }
        }
    }

    private fun askGemini() {
        // Remove existing observers to prevent duplicate calls. THIS IS TEMP SOLUTION
        roomChatViewModel.response.removeObservers(this)

        // Retrieve user input and create local chat items
        val question = binding.edPrompt.text.toString()
        val localChatUser = getLocalChat(ChatType.USER, question)
        val localChatModel = getLocalChat(ChatType.MODEL, "...")

        // Update local chat list and adapter
        listLocalChat.apply {
            add(localChatUser)
            add(localChatModel)
        }
        chatAdapter.apply {
            addItem(localChatUser)
            addItem(localChatModel)
        }

        // Send message and observe response
        roomChatViewModel.sendMessage(localChatModel, Prompt.TextPrompt(question))
        observeResponse()
    }

    private fun observeResponse() {
        roomChatViewModel.response.observe(this) { event ->
            event.getContentIfNotHandled()?.let { resources ->
                when (resources) {
                    is Resource.Loading -> {}
                    is Resource.Success -> handleSuccess(resources.data)
                    is Resource.Error -> showShortToast(resources.error.message.toString())
                    is Resource.Empty -> {}
                }
            }
        }
    }

    private fun handleSuccess(data: LocalChat?) {
        data?.let {
            chatAdapter.updateItem(it)
            binding.rvChat.smoothScrollToPosition(chatAdapter.itemCount - 1)
        }
    }


    private fun getLocalChat(chatType: ChatType, text: String): LocalChat {
        return DataMapper.mapToModel(
            chatAdapter.itemCount,
            chatType,
            text
        )
    }
}