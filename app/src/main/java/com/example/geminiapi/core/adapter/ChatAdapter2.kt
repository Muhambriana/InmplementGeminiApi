package com.example.geminiapi.core.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.geminiapi.core.models.Chat2
import com.example.geminiapi.databinding.ItemListMessageInBinding
import com.example.geminiapi.databinding.ItemListMessageOutBinding
import com.example.geminiapi.utils.config.ChatType
import com.google.ai.client.generativeai.type.TextPart

class ChatAdapter2 : RecyclerView.Adapter<ViewHolder>() {

    private val listOfChat = mutableListOf<Chat2>()

    fun addItem(Chat2: Chat2?) {
        if (Chat2 == null) return
        listOfChat.add(Chat2)
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
        val Chat2 = listOfChat[position]
        if (getItemViewType(position) == RIGHT_VIEW) {
            (holder as RightViewHolder).bind(Chat2)
        } else {
            (holder as LeftViewHolder).bind(Chat2)
        }
    }

    override fun getItemViewType(position: Int): Int {
        // Determine the view type based on the 'type' field
        return if (listOfChat[position].type == ChatType.USER) RIGHT_VIEW else LEFT_VIEW
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Chat2>?) {
        if (list == null) return
        listOfChat.clear() // Clear previous data to avoid duplications
        listOfChat.addAll(list)
        notifyDataSetChanged() // Notify adapter about the change
    }

    inner class LeftViewHolder(private val binding: ItemListMessageInBinding) :
        ViewHolder(binding.root) {
        fun bind(Chat2: Chat2) {
            binding.apply {
                itemUsername.text = Chat2.userName

                // Access the text from content
                Chat2.content?.let { content ->
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
        fun bind(Chat2: Chat2) {
            binding.apply {
                // Access the text from content
                Chat2.content?.let { content ->
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
