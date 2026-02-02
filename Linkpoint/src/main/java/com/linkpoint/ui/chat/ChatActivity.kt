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
import com.linkpoint.chat.SessionType
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
        const val EXTRA_IM_SESSION_ID = "extra_im_session_id"
    }
    
    private lateinit var tabLayout: TabLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var adapter: ActivityChatAdapter
    
    private val messages = mutableListOf<ActivityChatMessage>()
    private var currentChannel = ActivityChatChannel.LOCAL
    private var activeImSessionId: UUID? = null
    private var activeGroupSessionId: UUID? = null
    
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
        handleIntent()
        observeImMessages()
        observeSessionUpdates()
    }
    
    private fun initViews() {
        tabLayout = findViewById(R.id.tabLayout)
        recyclerView = findViewById(R.id.chatRecyclerView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        
        adapter = ActivityChatAdapter(messages)
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
                    0 -> ActivityChatChannel.LOCAL
                    1 -> ActivityChatChannel.IM
                    2 -> ActivityChatChannel.GROUP
                    3 -> ActivityChatChannel.NEARBY
                    else -> ActivityChatChannel.LOCAL
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

    private fun handleIntent() {
        val sessionId = intent.getStringExtra(EXTRA_IM_SESSION_ID)
            ?.let { runCatching { UUID.fromString(it) }.getOrNull() }
        if (sessionId != null) {
            val session = app.imManager.activeSessions.value.firstOrNull { it.sessionId == sessionId }
            when (session?.type) {
                SessionType.GROUP, SessionType.CONFERENCE -> {
                    activeGroupSessionId = sessionId
                    tabLayout.getTabAt(ActivityChatChannel.GROUP.ordinal)?.select()
                }
                else -> {
                    activeImSessionId = sessionId
                    tabLayout.getTabAt(ActivityChatChannel.IM.ordinal)?.select()
                }
            }
        }
    }

    private fun observeImMessages() {
        lifecycleScope.launch {
            app.imManager.messageFlow.collect { imMessage ->
                val sessionId = when (currentChannel) {
                    ActivityChatChannel.IM -> activeImSessionId
                    ActivityChatChannel.GROUP -> activeGroupSessionId
                    else -> null
                } ?: return@collect
                if (imMessage.sessionId != sessionId) {
                    return@collect
                }
                if (currentChannel != ActivityChatChannel.IM && currentChannel != ActivityChatChannel.GROUP) {
                    return@collect
                }

                val activityMessage = imMessage.toActivityMessage(currentChannel)
                messages.add(activityMessage)
                adapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)
            }
        }
    }

    private fun observeSessionUpdates() {
        lifecycleScope.launch {
            app.imManager.activeSessions.collect {
                if (currentChannel == ActivityChatChannel.IM || currentChannel == ActivityChatChannel.GROUP) {
                    loadMessages()
                }
            }
        }
    }
    
    private fun loadMessages() {
        // Load messages for current channel
        messages.clear()
        adapter.notifyDataSetChanged()
        
        if (currentChannel == ActivityChatChannel.IM || currentChannel == ActivityChatChannel.GROUP) {
            val sessionId = resolveSessionIdForChannel(currentChannel)
            if (sessionId == null) {
                addSystemMessage("No ${currentChannel.name.lowercase()} session available")
                return
            }

            when (currentChannel) {
                ActivityChatChannel.IM -> activeImSessionId = sessionId
                ActivityChatChannel.GROUP -> activeGroupSessionId = sessionId
                else -> {}
            }
            app.imManager.markAsRead(sessionId)

            val imMessages = app.imManager.getSessionMessages(sessionId)
            messages.addAll(imMessages.map { it.toActivityMessage(currentChannel) })
            adapter.notifyDataSetChanged()
            if (messages.isNotEmpty()) {
                recyclerView.scrollToPosition(messages.size - 1)
            }
        } else {
            // Add channel info
            addSystemMessage("Switched to ${currentChannel.name.lowercase()} chat")
        }
    }
    
    private fun sendMessage(text: String) {
        if (currentChannel == ActivityChatChannel.IM || currentChannel == ActivityChatChannel.GROUP) {
            val sessionId = resolveSessionIdForChannel(currentChannel)
            if (sessionId == null) {
                addSystemMessage("No ${currentChannel.name.lowercase()} session available")
                return
            }
            app.imManager.sendIM(sessionId, text)
            return
        }

        // Determine chat type from command
        val (chatType, displayText) = when {
            text.startsWith("/shout ") -> ChatType.SHOUT to text.removePrefix("/shout ")
            text.startsWith("/whisper ") -> ChatType.WHISPER to text.removePrefix("/whisper ")
            text.startsWith("/me ") -> ChatType.NORMAL to text // Emotes
            else -> ChatType.NORMAL to text
        }
        
        val message = ActivityChatMessage(
            id = UUID.randomUUID().toString(),
            sender = app.sessionManager.getAvatarName().ifEmpty { "You" },
            content = displayText,
            timestamp = System.currentTimeMillis(),
            type = when (chatType) {
                ChatType.SHOUT -> ActivityMessageType.SHOUT
                ChatType.WHISPER -> ActivityMessageType.WHISPER
                else -> if (text.startsWith("/me ")) ActivityMessageType.EMOTE else ActivityMessageType.NORMAL
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
        val message = ActivityChatMessage(
            id = UUID.randomUUID().toString(),
            sender = "System",
            content = text,
            timestamp = System.currentTimeMillis(),
            type = ActivityMessageType.SYSTEM,
            channel = currentChannel
        )
        
        messages.add(message)
        adapter.notifyItemInserted(messages.size - 1)
    }

    private fun com.linkpoint.chat.IMMessage.toActivityMessage(channel: ActivityChatChannel): ActivityChatMessage {
        return ActivityChatMessage(
            id = id.toString(),
            sender = fromName,
            content = message,
            timestamp = timestamp,
            type = ActivityMessageType.NORMAL,
            channel = channel
        )
    }

    private fun resolveSessionIdForChannel(channel: ActivityChatChannel): UUID? {
        return when (channel) {
            ActivityChatChannel.IM -> {
                activeImSessionId ?: app.imManager.activeSessions.value
                    .firstOrNull { it.type == SessionType.P2P }
                    ?.sessionId
            }
            ActivityChatChannel.GROUP -> {
                activeGroupSessionId ?: app.imManager.activeSessions.value
                    .firstOrNull { it.type == SessionType.GROUP || it.type == SessionType.CONFERENCE }
                    ?.sessionId
            }
            else -> null
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

// Local classes to avoid conflicts with chat module classes
enum class ActivityChatChannel {
    LOCAL, IM, GROUP, NEARBY
}

enum class ActivityMessageType {
    NORMAL, WHISPER, SHOUT, EMOTE, SYSTEM, OBJECT
}

data class ActivityChatMessage(
    val id: String,
    val sender: String,
    val content: String,
    val timestamp: Long,
    val type: ActivityMessageType,
    val channel: ActivityChatChannel
)

class ActivityChatAdapter(private val messages: List<ActivityChatMessage>) : RecyclerView.Adapter<ActivityChatAdapter.ViewHolder>() {
    
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
            ActivityMessageType.SYSTEM -> 0xFF888888.toInt()
            ActivityMessageType.WHISPER -> 0xFF9999FF.toInt()
            ActivityMessageType.SHOUT -> 0xFFFF6666.toInt()
            ActivityMessageType.EMOTE -> 0xFF66FF66.toInt()
            ActivityMessageType.OBJECT -> 0xFFFFAA00.toInt()
            else -> 0xFFFFFFFF.toInt()
        }
        holder.messageText.setTextColor(textColor)
    }
    
    override fun getItemCount() = messages.size
}
