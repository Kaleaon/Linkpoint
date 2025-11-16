package com.linkpoint.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.linkpoint.chat.ChatMessage
import com.linkpoint.databinding.ItemLinkpointMessageIncomingBinding
import com.linkpoint.databinding.ItemLinkpointMessageOutgoingBinding
import com.linkpoint.util.TimeFormatter

class ChatAdapter(
    private val timeFormatter: TimeFormatter = TimeFormatter()
) : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(DiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isLocal) VIEW_TYPE_OUTGOING else VIEW_TYPE_INCOMING
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_OUTGOING) {
            val binding = ItemLinkpointMessageOutgoingBinding.inflate(inflater, parent, false)
            OutgoingViewHolder(binding)
        } else {
            val binding = ItemLinkpointMessageIncomingBinding.inflate(inflater, parent, false)
            IncomingViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is OutgoingViewHolder -> holder.bind(message)
            is IncomingViewHolder -> holder.bind(message)
        }
    }

    inner class OutgoingViewHolder(
        private val binding: ItemLinkpointMessageOutgoingBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            binding.messageText.text = message.body
            binding.timestampText.text = timeFormatter.format(message.timestamp)
        }
    }

    inner class IncomingViewHolder(
        private val binding: ItemLinkpointMessageIncomingBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            binding.authorText.text = message.author
            binding.messageText.text = message.body
            binding.timestampText.text = timeFormatter.format(message.timestamp)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean =
            oldItem == newItem
    }

    companion object {
        private const val VIEW_TYPE_OUTGOING = 1
        private const val VIEW_TYPE_INCOMING = 2
    }
}
