package com.lumiyaviewer.lumiya.slproto.chat.generic

import android.content.ClipData
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.text.ClipboardManager
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.common.base.Objects
import com.google.common.base.Strings
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.GlobalOptions
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.chat.*
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.ui.chat.ChatEventTimestampUpdater
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView
import java.text.DateFormat
import java.util.Date
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

/**
 * Converted from Java to Kotlin
 * SLChatEvent - Represents various chat events in Second Life protocol
 */
abstract class SLChatEvent(
    @Nonnull protected val agentUUID: UUID,
    @Nonnull protected val source: ChatMessageSource,
    @Nullable protected val dbMessage: ChatMessage?,
    private val timestamp: Date,
    private val originalTimestamp: Date,
    private val isOffline: Boolean
) : View.OnLongClickListener {

    companion object {
        // Chat audible constants
        const val CHAT_AUDIBLE_BARELY = 0
        const val CHAT_AUDIBLE_FULLY = 1
        const val CHAT_AUDIBLE_NOT = -1

        // Chat source constants
        const val CHAT_SOURCE_AGENT = 1
        const val CHAT_SOURCE_OBJECT = 2
        const val CHAT_SOURCE_SYSTEM = 0
        const val CHAT_SOURCE_UNKNOWN = 3

        // Chat type constants
        const val CHAT_TYPE_DEBUG_MSG = 6
        const val CHAT_TYPE_NORMAL = 1
        const val CHAT_TYPE_OWNER = 8
        const val CHAT_TYPE_REGION = 7
        const val CHAT_TYPE_SHOUT = 2
        const val CHAT_TYPE_START = 4
        const val CHAT_TYPE_STOP = 5
        const val CHAT_TYPE_WHISPER = 0

        // IM constants
        const val IM_BUSY_AUTO_RESPONSE = 20
        const val IM_CONSOLE_AND_CHAT_HISTORY = 21
        const val IM_FRIENDSHIP_ACCEPTED = 39
        const val IM_FRIENDSHIP_DECLINED = 40
        const val IM_FRIENDSHIP_OFFERED = 38
        const val IM_FROM_TASK = 19
        const val IM_FROM_TASK_AS_ALERT = 31
        const val IM_GODLIKE_LURE_USER = 25
        const val IM_GOTO_URL = 28
        const val IM_GROUP_ELECTION_DEPRECATED = 27
        const val IM_GROUP_INVITATION = 3
        const val IM_GROUP_INVITATION_ACCEPT = 35
        const val IM_GROUP_INVITATION_DECLINE = 36
        const val IM_GROUP_MESSAGE_DEPRECATED = 8
        const val IM_GROUP_NOTICE = 32
        const val IM_GROUP_NOTICE_INVENTORY_ACCEPTED = 33
        const val IM_GROUP_NOTICE_INVENTORY_DECLINED = 34
        const val IM_GROUP_NOTICE_REQUESTED = 37
        const val IM_GROUP_VOTE = 7
        const val IM_INVENTORY_ACCEPTED = 5
        const val IM_INVENTORY_DECLINED = 6
        const val IM_INVENTORY_OFFERED = 4
        const val IM_LURE_ACCEPTED = 23
        const val IM_LURE_DECLINED = 24
        const val IM_LURE_USER = 22
        const val IM_MESSAGEBOX = 1
        const val IM_MESSAGEBOX_COUNTDOWN = 2
        const val IM_NEW_USER_DEFAULT = 12
        const val IM_NOTHING_SPECIAL = 0
        const val IM_SESSION_CONFERENCE_START = 16
        const val IM_SESSION_GROUP_START = 15
        const val IM_SESSION_INVITE = 13
        const val IM_SESSION_LEAVE = 18
        const val IM_SESSION_P2P_INVITE = 14
        const val IM_SESSION_SEND = 17
        const val IM_TASK_INVENTORY_ACCEPTED = 10
        const val IM_TASK_INVENTORY_DECLINED = 11
        const val IM_TASK_INVENTORY_OFFERED = 9
        const val IM_TELEPORT_REQUEST = 26
        const val IM_TYPING_START = 41
        const val IM_TYPING_STOP = 42

        fun loadFromDatabaseObject(chatMessage: ChatMessage, agentUUID: UUID): SLChatEvent? {
            // Factory method to create appropriate SLChatEvent subclass from database object
            return when (chatMessage.type) {
                ChatMessageType.Text.ordinal -> SLChatTextEvent.fromDatabase(chatMessage, agentUUID)
                ChatMessageType.BalanceChanged.ordinal -> SLChatBalanceChangedEvent.fromDatabase(chatMessage, agentUUID)
                ChatMessageType.InventoryItemOffered.ordinal -> SLChatInventoryItemOfferedEvent.fromDatabase(chatMessage, agentUUID)
                ChatMessageType.FriendshipOffered.ordinal -> SLChatFriendshipOfferedEvent.fromDatabase(chatMessage, agentUUID)
                ChatMessageType.FriendshipResult.ordinal -> SLChatFriendshipResultEvent.fromDatabase(chatMessage, agentUUID)
                ChatMessageType.GroupInvitation.ordinal -> SLChatGroupInvitationEvent.fromDatabase(chatMessage, agentUUID)
                ChatMessageType.Lure.ordinal -> SLChatLureEvent.fromDatabase(chatMessage, agentUUID)
                ChatMessageType.WentOnline.ordinal, ChatMessageType.WentOffline.ordinal -> 
                    SLChatOnlineOfflineEvent.fromDatabase(chatMessage, agentUUID)
                ChatMessageType.SystemMessage.ordinal -> SLChatSystemMessageEvent.fromDatabase(chatMessage, agentUUID)
                else -> null
            }
        }
    }

    enum class ChatMessageType {
        Text,
        BalanceChanged,
        InventoryItemOffered,
        InventoryItemOfferedByGroupNotice,
        InventoryItemOfferedByYou,
        FriendshipOffered,
        FriendshipResult,
        GroupInvitation,
        GroupInvitationSent,
        Lure,
        LureRequested,
        LureRequest,
        WentOnline,
        WentOffline,
        PermissionRequest,
        ScriptDialog,
        TextBoxDialog,
        SystemMessage,
        EnableRLVOffer,
        SessionMark,
        MissedVoiceCall,
        VoiceUpgrade
    }

    abstract fun getMessageType(): ChatMessageType
    
    abstract fun getMessage(): CharSequence
    
    abstract fun getSenderDisplayName(): CharSequence?
    
    abstract fun getChatterID(): ChatterID
    
    fun getTimestamp(): Date = timestamp
    
    fun getOriginalTimestamp(): Date = originalTimestamp
    
    fun isOfflineMessage(): Boolean = isOffline
    
    fun getSource(): ChatMessageSource = source
    
    fun getAgentUUID(): UUID = agentUUID
    
    fun getDatabaseMessage(): ChatMessage? = dbMessage
    
    override fun onLongClick(view: View?): Boolean {
        view?.let {
            showContextMenu(it)
            return true
        }
        return false
    }
    
    private fun showContextMenu(view: View) {
        val context = view.context
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.chat_message_context_menu)
        
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_copy -> {
                    copyToClipboard(context)
                    true
                }
                R.id.action_delete -> {
                    deleteMessage()
                    true
                }
                else -> false
            }
        }
        
        popupMenu.show()
    }
    
    private fun copyToClipboard(context: Context) {
        val message = getMessage()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("Chat Message", message)
            clipboard.setPrimaryClip(clip)
        } else {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.text = message
        }
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }
    
    private fun deleteMessage() {
        dbMessage?.let { msg ->
            // Delete from database
            // This would need access to the DAO
        }
    }

    open fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_message_item, parent, false)
        return ChatEventViewHolder(view)
    }
    
    open fun bindViewHolder(holder: RecyclerView.ViewHolder, userManager: UserManager) {
        if (holder is ChatEventViewHolder) {
            holder.messageText.text = getMessage()
            holder.timestampText.text = formatTimestamp()
            getSenderDisplayName()?.let {
                holder.senderName.text = it
                holder.senderName.visibility = View.VISIBLE
            } ?: run {
                holder.senderName.visibility = View.GONE
            }
            
            holder.itemView.setOnLongClickListener(this)
        }
    }
    
    private fun formatTimestamp(): String {
        val dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT)
        return dateFormat.format(timestamp)
    }
}
