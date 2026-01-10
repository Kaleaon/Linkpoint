package com.linkpoint.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.network.ChatType
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Chat Activity - Local chat, IMs, and group chat
 * Based on Lumiya's ChatNewActivity
 */
class ChatActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "ChatActivity"
    }
    
    private lateinit var tabLayout: TabLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var adapter: ChatAdapter
    
    private val messages = mutableListOf<ChatMessage>()
    private var currentChannel = ChatChannel.LOCAL
    
    private val app by lazy { LinkpointApp.getInstance() }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Chat"
        }
        
        initViews()
        setupTabs()
        setupChat()
    }
    
    private fun initViews() {
        tabLayout = findViewById(R.id.tabLayout)
        recyclerView = findViewById(R.id.chatRecyclerView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        
        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        recyclerView.adapter = adapter
    }
    
    private fun setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Local"))
        tabLayout.addTab(tabLayout.newTab().setText("IMs"))
        tabLayout.addTab(tabLayout.newTab().setText("Groups"))
        tabLayout.addTab(tabLayout.newTab().setText("Nearby"))
        
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentChannel = when (tab.position) {
                    0 -> ChatChannel.LOCAL
                    1 -> ChatChannel.IM
                    2 -> ChatChannel.GROUP
                    3 -> ChatChannel.NEARBY
                    else -> ChatChannel.LOCAL
                }
                loadMessages()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
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
        addSystemMessage("Connected to local chat")
    }
    
    private fun loadMessages() {
        // Load messages for current channel
        messages.clear()
        adapter.notifyDataSetChanged()
        
        // Add channel info
        addSystemMessage("Switched to ${currentChannel.name.lowercase()} chat")
    }
    
    private fun sendMessage(text: String) {
        // Determine chat type from command
        val (chatType, displayText) = when {
            text.startsWith("/shout ") -> ChatType.SHOUT to text.removePrefix("/shout ")
            text.startsWith("/whisper ") -> ChatType.WHISPER to text.removePrefix("/whisper ")
            text.startsWith("/me ") -> ChatType.NORMAL to text // Emotes
            else -> ChatType.NORMAL to text
        }
        
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = app.sessionManager.getAvatarName().ifEmpty { "You" },
            content = displayText,
            timestamp = System.currentTimeMillis(),
            type = when (chatType) {
                ChatType.SHOUT -> MessageType.SHOUT
                ChatType.WHISPER -> MessageType.WHISPER
                else -> if (text.startsWith("/me ")) MessageType.EMOTE else MessageType.NORMAL
            },
            channel = currentChannel
        )
        
        messages.add(message)
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
        
        // Send to server
        lifecycleScope.launch {
            app.protocol.sendChat(displayText, 0, chatType)
        }
    }
    
    private fun addSystemMessage(text: String) {
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = "System",
            content = text,
            timestamp = System.currentTimeMillis(),
            type = MessageType.SYSTEM,
            channel = currentChannel
        )
        
        messages.add(message)
        adapter.notifyItemInserted(messages.size - 1)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

enum class ChatChannel {
    LOCAL, IM, GROUP, NEARBY
}

enum class MessageType {
    NORMAL, WHISPER, SHOUT, EMOTE, SYSTEM, OBJECT
}

data class ChatMessage(
    val id: String,
    val sender: String,
    val content: String,
    val timestamp: Long,
    val type: MessageType,
    val channel: ChatChannel
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
            MessageType.SYSTEM -> 0xFF888888.toInt()
            MessageType.WHISPER -> 0xFF9999FF.toInt()
            MessageType.SHOUT -> 0xFFFF6666.toInt()
            MessageType.EMOTE -> 0xFF66FF66.toInt()
            MessageType.OBJECT -> 0xFFFFAA00.toInt()
            else -> 0xFFFFFFFF.toInt()
        }
        holder.messageText.setTextColor(textColor)
    }
    
    override fun getItemCount() = messages.size
}
