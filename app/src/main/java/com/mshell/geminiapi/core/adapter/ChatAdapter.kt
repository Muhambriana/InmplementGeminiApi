package com.mshell.geminiapi.core.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.mshell.geminiapi.databinding.ItemListMessageInBinding
import com.mshell.geminiapi.databinding.ItemListMessageOutBinding
import com.mshell.geminiapi.core.models.LocalChat
import com.mshell.geminiapi.utils.config.ChatType
import com.mshell.geminiapi.utils.helper.Helper.getTextFromContent

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
                itemMessage.text = getTextFromContent(localChat.content)
            }
        }
    }

    inner class RightViewHolder(private val binding: ItemListMessageOutBinding) :
        ViewHolder(binding.root) {
        fun bind(localChat: LocalChat) {
            binding.apply {
                itemMessage.text = getTextFromContent(localChat.content)
            }
        }
    }

    companion object {
        const val LEFT_VIEW = 0
        const val RIGHT_VIEW = 1
    }
}