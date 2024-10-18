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
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.TextPart

class ChatAdapter : RecyclerView.Adapter<ViewHolder>() {

    private val listOfChat = mutableListOf<Chat>()

    fun updateItem(newChat: Chat?) {
        newChat ?: return  // Return early if chat is null

        // Find the existing chat by id
        val existingChat = listOfChat.find { it.id == newChat.id } ?: return

        // Extract the new TextPart, if present
        val newTextPart = newChat.content?.parts?.firstOrNull() as? TextPart ?: return

        // Get or initialize parts from existing chat
        val updatedParts = existingChat.content?.parts?.toMutableList() ?: mutableListOf()

        // Append new text to the existing text part, or add the new part
        val existingTextPart = updatedParts.firstOrNull() as? TextPart
        if (existingTextPart != null) {
            // Append to existing text
            updatedParts[0] = TextPart(existingTextPart.text + "\n" + newTextPart.text)
        } else {
            // Add new part if no existing text part
            updatedParts.add(newTextPart)
        }

        // Update content with the modified parts
        existingChat.content = Content(existingChat.content?.role, updatedParts)

        // Notify adapter about the updated item
        notifyItemChanged(listOfChat.indexOf(existingChat))

        // Scroll to the bottom of the RecyclerView after updating
    }


    fun addItem(chat: Chat?) {
        if (chat == null) return
        listOfChat.add(chat)
        notifyItemInserted(listOfChat.size)
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

                // Access the text from content
                chat.content?.let { content ->
                    // Assuming you're interested in the first part
                    if (content.parts.isNotEmpty()) {
                        val firstPart = content.parts[0]
                        if (firstPart is TextPart) {
                            itemMessage.text = firstPart.text // Set the text in your TextView
                        }
                    }
                }
            }
        }
    }

    inner class RightViewHolder(private val binding: ItemListMessageOutBinding) :
        ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            binding.apply {
                // Access the text from content
                chat.content?.let { content ->
                    // Assuming you're interested in the first part
                    if (content.parts.isNotEmpty()) {
                        val firstPart = content.parts[0]
                        if (firstPart is TextPart) {
                            itemMessage.text = firstPart.text // Set the text in your TextView
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val LEFT_VIEW = 0
        const val RIGHT_VIEW = 1
    }
}
