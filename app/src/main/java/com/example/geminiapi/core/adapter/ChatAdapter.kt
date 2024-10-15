package com.example.geminiapi.core.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.geminiapi.core.models.Chat
import com.example.geminiapi.databinding.ItemListMessageInBinding
import com.example.geminiapi.databinding.ItemListMessageOutBinding
import com.example.geminiapi.utils.config.ChatType

class ChatAdapter : RecyclerView.Adapter<ViewHolder>() {

    private val listOfChat = mutableListOf<Chat>()

    fun addItem(chat: Chat?) {
        if (chat == null) return
        listOfChat.add(chat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            LEFT_VIEW -> {
                val binding = ItemListMessageInBinding.inflate(inflater, parent, false)
                LeftViewHolder(binding)
            }
            RIGHT_VIEW -> {
                val binding = ItemListMessageOutBinding.inflate(inflater, parent, false)
                RightViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = listOfChat.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = listOfChat[position]
        if (getItemViewType(position) == RIGHT_VIEW) {
            (holder as RightViewHolder).bind(chat)
        } else {
            (holder as LeftViewHolder).bind(chat)
        }
    }

    override fun getItemViewType(position: Int): Int {
        // Determine the view type based on the 'type' field
        return if (listOfChat[position].type == ChatType.USER) RIGHT_VIEW else LEFT_VIEW
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Chat>?) {
        if (list == null) return
        listOfChat.clear() // Clear previous data to avoid duplications
        listOfChat.addAll(list)
        notifyDataSetChanged() // Notify adapter about the change
    }

    inner class LeftViewHolder(private val binding: ItemListMessageInBinding) :
        ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            binding.apply {
                itemUsername.text = chat.userName
                itemMessage.text = chat.message
            }
        }
    }

    inner class RightViewHolder(private val binding: ItemListMessageOutBinding) :
        ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            binding.apply {
                itemMessage.text = chat.message
            }
        }
    }

    companion object {
        const val LEFT_VIEW = 0
        const val RIGHT_VIEW = 1
    }
}
