package com.example.geminiapi.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminiapi.core.GeminiEvent
import com.example.geminiapi.core.data.GeminiRepository
import com.example.geminiapi.core.data.Resource
import com.example.geminiapi.core.models.LocalChat
import com.example.geminiapi.utils.config.Prompt
import com.example.geminiapi.utils.helper.Helper
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.type.Content
import kotlinx.coroutines.launch

class RoomChatViewModel(private val repository: GeminiRepository) : ViewModel() {

    private val _response = MutableLiveData<GeminiEvent<Resource<LocalChat>>>()
    private val _geminiChat = MutableLiveData<Resource<Chat>>()

    val geminiChat = _geminiChat
    val response: LiveData<GeminiEvent<Resource<LocalChat>>> = _response

    fun getStartChat(history: List<Content> = listOf()) {
        _geminiChat.postValue(Resource.Loading)
        viewModelScope.launch {
            val result = repository.getStartChat(history)
            _geminiChat.postValue(Resource.Success(result))
        }
    }

    fun sendMessage(localChat: LocalChat, prompt: Prompt) {
        _response.postValue(GeminiEvent(Resource.Loading))
        fetchMessageStream(localChat, prompt)
    }

    private fun fetchMessageStream(localChat: LocalChat, prompt: Prompt) {
        viewModelScope.launch {
            try {
                repository.getStreamMessage(prompt)
                    .onSuccess { streamFlow ->
                        streamFlow?.collect { response ->
                            updateLocalChatContent(localChat, response.text.toString())
                        }
                    }
                    .onFailure { error ->
                        _response.postValue(GeminiEvent(Resource.Error(error)))
                    }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun updateLocalChatContent(localChat: LocalChat, newText: String) {
        val updatedContent = Helper.appendTextToContent(localChat.content, newText)
        localChat.content = updatedContent
        _response.postValue(GeminiEvent(Resource.Success(localChat)))
    }

    private fun handleError(exception: Exception) {
        exception.printStackTrace()
        _response.postValue(GeminiEvent(Resource.Error(exception)))
    }

}