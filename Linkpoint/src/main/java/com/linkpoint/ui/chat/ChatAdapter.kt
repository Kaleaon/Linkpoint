package com.linkpoint.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linkpoint.R
import com.linkpoint.chat.ChatMessage
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying chat messages in a RecyclerView
 */
class ChatAdapter(
    private val onMessageClick: (ChatMessage) -> Unit = {}
) : ListAdapter<ChatMessage, ChatAdapter.ChatViewHolder>(DiffCallback()) {

    companion object {
        private const val VIEW_TYPE_INCOMING = 0
        private const val VIEW_TYPE_OUTGOING = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isOutgoing) {
            VIEW_TYPE_OUTGOING
        } else {
            VIEW_TYPE_INCOMING
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layoutRes = when (viewType) {
            VIEW_TYPE_INCOMING -> R.layout.item_chat_incoming
            VIEW_TYPE_OUTGOING -> R.layout.item_chat_outgoing
            else -> R.layout.item_chat_incoming
        }

        val view = LayoutInflater.from(parent.context)
            .inflate(layoutRes, parent, false)

        return ChatViewHolder(view, onMessageClick)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder for chat messages
     */
    class ChatViewHolder(
        itemView: View,
        private val onClick: (ChatMessage) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val senderName: TextView = itemView.findViewById(R.id.sender_name)
        private val messageText: TextView = itemView.findViewById(R.id.message_text)
        private val timestamp: TextView = itemView.findViewById(R.id.timestamp)

        fun bind(message: ChatMessage) {
            senderName.text = message.fromName
            messageText.text = message.message
            timestamp.text = formatTimestamp(message.timestamp)

            itemView.setOnClickListener { onClick(message) }
        }

        private fun formatTimestamp(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp

            return when {
                diff < 60000 -> "Just now"
                diff < 3600000 -> "${diff / 60000}m ago"
                diff < 86400000 -> "${diff / 3600000}h ago"
                diff < 604800000 -> SimpleDateFormat("EEE", Locale.getDefault()).format(Date(timestamp))
                else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
            }
        }
    }

    /**
     * DiffUtil callback for efficient updates
     */
    class DiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}