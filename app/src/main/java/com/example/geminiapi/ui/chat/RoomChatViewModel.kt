package com.example.geminiapi.ui.chat

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminiapi.core.GeminiEvent
import com.example.geminiapi.core.data.GeminiRepository
import com.example.geminiapi.core.data.Resource
import com.example.geminiapi.core.models.LocalChat
import com.example.geminiapi.utils.config.ChatType
import com.example.geminiapi.utils.config.Prompt
import com.example.geminiapi.utils.helper.Helper
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.type.Content
import kotlinx.coroutines.launch

class RoomChatViewModel(private val repository: GeminiRepository) : ViewModel() {

    val geminiChat = MutableLiveData<Resource<Chat>>()
    private val _response = MutableLiveData<GeminiEvent<Resource<String>>>()
    val response: LiveData<GeminiEvent<Resource<String>>> = _response


    fun getStartChat(history: List<Content> = listOf()) {
        geminiChat.postValue(Resource.Loading())
        viewModelScope.launch {
            val result = repository.getStartChat(history)
            geminiChat.postValue(Resource.Success(result))
        }
    }

    fun getMessageStream(chat: Chat?, prompt: Prompt) {
        _response.postValue(GeminiEvent(Resource.Loading()))
        viewModelScope.launch {
            repository.getStreamMessage(chat, prompt)?.collect { res ->
                _response.postValue(GeminiEvent(Resource.Success(res.text.toString())))
            }
        }
    }
}