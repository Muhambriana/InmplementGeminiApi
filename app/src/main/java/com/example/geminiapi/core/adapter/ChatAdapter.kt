package com.example.geminiapi.core.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.geminiapi.core.models.LocalChat
import com.example.geminiapi.databinding.ItemListMessageInBinding
import com.example.geminiapi.databinding.ItemListMessageOutBinding
import com.example.geminiapi.utils.config.ChatType
import com.google.ai.client.generativeai.type.TextPart

class ChatAdapter : RecyclerView.Adapter<ViewHolder>() {

    private val listOfLocalChat = mutableListOf<LocalChat>()

    fun updateItem(newLocalChat: LocalChat?) {
        // Notify adapter about the updated item
        notifyItemChanged(listOfLocalChat.indexOf(newLocalChat))

        // Scroll to the bottom of the RecyclerView after updating
    }

    fun addItem(localChat: LocalChat?) {
        if (localChat == null) return
        listOfLocalChat.add(localChat)
        notifyItemInserted(listOfLocalChat.size)
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

    override fun getItemCount(): Int = listOfLocalChat.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = listOfLocalChat[position]
        if (getItemViewType(position) == RIGHT_VIEW) {
            (holder as RightViewHolder).bind(chat)
        } else {
            (holder as LeftViewHolder).bind(chat)
        }
    }

    override fun getItemViewType(position: Int): Int {
        // Determine the view type based on the 'type' field
        return if (listOfLocalChat[position].type == ChatType.USER) RIGHT_VIEW else LEFT_VIEW
    }

    inner class LeftViewHolder(private val binding: ItemListMessageInBinding) :
        ViewHolder(binding.root) {
        fun bind(localChat: LocalChat) {
            binding.apply {
                itemUsername.text = localChat.type?.alias

                // Access the text from content
                localChat.content.let { content ->
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
        fun bind(localChat: LocalChat) {
            binding.apply {
                // Access the text from content
                localChat.content.let { content ->
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
