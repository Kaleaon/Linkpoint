package com.linkpoint.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.linkpoint.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Chat activity for Second Life local and IM chat
 */
class ChatActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var adapter: ChatAdapter
    
    private val messages = mutableListOf<ChatMessage>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        
        supportActionBar?.title = "Local Chat"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        initViews()
        setupChat()
    }
    
    private fun initViews() {
        recyclerView = findViewById(R.id.chatRecyclerView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        
        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        recyclerView.adapter = adapter
    }
    
    private fun setupChat() {
        sendButton.setOnClickListener {
            val text = messageInput.text.toString().trim()
            if (text.isNotEmpty()) {
                sendMessage(text)
                messageInput.text.clear()
            }
        }
        
        // Add welcome message
        addSystemMessage("Connected to Second Life. Type /me for emotes.")
    }
    
    private fun sendMessage(text: String) {
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = "You",
            content = text,
            timestamp = System.currentTimeMillis(),
            type = if (text.startsWith("/me ")) ChatMessageType.EMOTE else ChatMessageType.NORMAL
        )
        
        messages.add(message)
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
        
        // TODO: Send to SL server
    }
    
    private fun addSystemMessage(text: String) {
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = "System",
            content = text,
            timestamp = System.currentTimeMillis(),
            type = ChatMessageType.SYSTEM
        )
        
        messages.add(message)
        adapter.notifyItemInserted(messages.size - 1)
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

enum class ChatMessageType {
    NORMAL, WHISPER, SHOUT, EMOTE, SYSTEM, IM
}

data class ChatMessage(
    val id: String,
    val sender: String,
    val content: String,
    val timestamp: Long,
    val type: ChatMessageType
)

class ChatAdapter(private val messages: List<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    
    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val senderText: TextView = view.findViewById(R.id.senderText)
        val messageText: TextView = view.findViewById(R.id.messageText)
        val timeText: TextView = view.findViewById(R.id.timeText)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        
        holder.senderText.text = message.sender
        holder.messageText.text = message.content
        holder.timeText.text = dateFormat.format(Date(message.timestamp))
        
        // Style based on message type
        val textColor = when (message.type) {
            ChatMessageType.SYSTEM -> android.graphics.Color.parseColor("#2196F3")
            ChatMessageType.WHISPER -> android.graphics.Color.parseColor("#9C27B0")
            ChatMessageType.SHOUT -> android.graphics.Color.parseColor("#FF5722")
            ChatMessageType.EMOTE -> android.graphics.Color.parseColor("#4CAF50")
            ChatMessageType.IM -> android.graphics.Color.parseColor("#03A9F4")
            else -> android.graphics.Color.WHITE
        }
        
        holder.messageText.setTextColor(textColor)
    }
    
    override fun getItemCount() = messages.size
}
