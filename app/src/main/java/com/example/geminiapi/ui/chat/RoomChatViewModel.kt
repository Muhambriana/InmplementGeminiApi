package com.example.geminiapi.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geminiapi.core.GeminiEvent
import com.example.geminiapi.core.data.GeminiRepository
import com.example.geminiapi.core.data.Resource
import com.example.geminiapi.utils.config.Prompt
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.type.Content
import kotlinx.coroutines.launch

class RoomChatViewModel(private val repository: GeminiRepository) : ViewModel() {

    private val _response = MutableLiveData<GeminiEvent<Resource<String>>>()
    private val _geminiChat = MutableLiveData<Resource<Chat>>()

    val geminiChat = _geminiChat
    val response: LiveData<GeminiEvent<Resource<String>>> = _response

    fun getStartChat(history: List<Content> = listOf()) {
        _geminiChat.postValue(Resource.Loading())
        viewModelScope.launch {
            val result = repository.getStartChat(history)
            _geminiChat.postValue(Resource.Success(result))
        }
    }

    fun getMessageStream(chat: Chat?, prompt: Prompt) {
        _response.postValue(GeminiEvent(Resource.Loading()))
        viewModelScope.launch {
            try {
                repository.getStreamMessage(chat, prompt).onSuccess {
                    it?.collect { res ->
                        _response.postValue(GeminiEvent(Resource.Success(res.text.toString())))
                    }
                }.onFailure {
                    _response.postValue(GeminiEvent(Resource.Error(it)))
                }
            } catch (e:Exception) {
                e.printStackTrace()
                _response.postValue(GeminiEvent(Resource.Error(e)))
            }
        }
    }
}