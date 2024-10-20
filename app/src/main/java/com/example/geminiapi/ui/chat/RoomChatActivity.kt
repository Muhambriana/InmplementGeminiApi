package com.example.geminiapi.ui.chat

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import com.example.geminiapi.utils.helper.DataMapper
import com.example.geminiapi.utils.helper.Helper
import com.example.geminiapi.utils.helper.Helper.showShortToast
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RoomChatActivity : AppCompatActivity() {

    private val binding: ActivityRoomChatBinding by lazy {
        ActivityRoomChatBinding.inflate(
            layoutInflater
        )
    }
    private val geminiDataSource: GeminiDataSource by lazy { GeminiDataSource() }
    private val localDataSource: LocalDataSource by lazy { LocalDataSource() }
    private var chat: Chat? = null

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
            askGemini2()
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
        val userText = "help me calculation, just help me and give me the result"
        val modelText = "okay."
        val history = listOf(
            getLocalChat(ChatType.USER, userText),
            getLocalChat(ChatType.MODEL, modelText)
        )
        chatAdapter.setData(history)
        roomChatViewModel.getStartChat(
            listOf(
                history[0].content,
                history[1].content
            )
        )
        roomChatViewModel.geminiChat.observe(this) { resources ->
            when (resources) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    chat = resources.data
                    lifecycleScope.launch {
//                        askGemini()
                    }
                }
            }
        }
    }

    private suspend fun askGemini() {
        delay(2000)
        val question = "i have 3 new cat, and give me very short story telling about dog"
        val localChatUser = getLocalChat(ChatType.USER, question)
        chatAdapter.addItem(localChatUser)
        val localChatModel = getLocalChat(ChatType.MODEL, "...")
        chatAdapter.addItem(localChatModel)
        delay(2000)
        roomChatViewModel.getMessageStream(chat, question)
//        roomChatViewModel.response.observe(this) { resources ->
//            when (resources) {
//                is Resource.Loading -> {}
//                is Resource.Success -> {
//                    val new = localChatModel.apply {
//                        this.content = Helper.appendTextToContent(this.content, resources.data.toString())
//                    }
//                    Log.d("kocak2", "${new.id} || ${new.type} || ${new.type?.alias} || ${Helper.getTextFromContent(new.content)}")
//                    chatAdapter.updateItem(new)
//                    binding.rvChat.smoothScrollToPosition(chatAdapter.itemCount - 1)
//                }
//            }
//        }
    }

    private fun askGemini2() {
        //This is temp solution. Need to find best solution
        roomChatViewModel.response.removeObservers(this)
        val question = binding.edPrompt.text.toString()
        val localChatUser = getLocalChat(ChatType.USER, question)
        chatAdapter.addItem(localChatUser)
        val localChatModel = getLocalChat(ChatType.MODEL, "...")
        showShortToast("${Helper.getTextFromContent(localChatModel.content)} === ${localChatModel.id}")
        chatAdapter.addItem(localChatModel)
        roomChatViewModel.getMessageStream(chat, question)
        roomChatViewModel.response.observe(this) { event ->
            event.getContentIfNotHandled().let { resources ->
                when (resources) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        Log.d("kocak201",  resources.data.toString())
                        val new  = localChatModel.apply {
                            this.content = Helper.appendTextToContent(this.content, resources.data.toString())
                        }
                        Log.d("kocak2", "${new.id} || ${new.type} || ${new.type?.alias} || ${Helper.getTextFromContent(new.content)}")

                        chatAdapter.updateItem(new)
                        binding.rvChat.smoothScrollToPosition(chatAdapter.itemCount - 1)
                    }
                    null -> {}
                }

            }
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