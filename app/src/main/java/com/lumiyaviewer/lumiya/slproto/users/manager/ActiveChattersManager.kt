package com.lumiyaviewer.lumiya.slproto.users.manager

import com.google.common.eventbus.EventBus
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.dao.*
import com.lumiyaviewer.lumiya.react.RequestFinalProcessor
import com.lumiyaviewer.lumiya.react.SubscriptionPool
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.chat.SLChatSessionMarkEvent
import com.lumiyaviewer.lumiya.slproto.chat.generic.OnChatEventListener
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown
import com.lumiyaviewer.lumiya.utils.wlist.ChunkedListLoader
import de.greenrobot.dao.query.LazyList
import de.greenrobot.dao.query.Query
import de.greenrobot.dao.query.WhereCondition
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.Executor
import androidx.annotation.NonNull
import androidx.annotation.Nullable

/**
 * Converted from Java to Kotlin
 * ActiveChattersManager - Manages active chat sessions and message history
 */
class ActiveChattersManager(
    private val userManager: UserManager,
    daoSession: DaoSession,
    private val chatterList: ChatterList
) : MessageSourceNameResolver.OnMessageSourcesResolvedListener {

    companion object {
        private const val CHAT_LOG_CHUNK_SIZE = 100
    }

    data class ChatMessageEvent(
        val chatMessage: ChatMessage,
        val isNewMessage: Boolean,
        val isPrivate: Boolean
    )

    private val chatEventBus = EventBus()
    private val chatEventLock = Any()
    private val chatMessageDao: ChatMessageDao = daoSession.chatMessageDao
    private val chatterDao: ChatterDao = daoSession.chatterDao
    private val displayedChatters: MutableSet<ChatterID> = Collections.synchronizedSet(HashSet())
    private val findChatterQuery: Query<Chatter>
    private val findChatterQueryNullUUID: Query<Chatter>
    private val localChatterID: ChatterID
    private val messageLoaders: MutableMap<ChatterID, MutableList<WeakReference<ChatMessageLoader>>> = HashMap()
    private val messageLoadersLock = Any()
    private val messageSourceNameResolver: MessageSourceNameResolver
    private val objectMessageListeners: MutableMap<OnChatEventListener, Executor> = WeakHashMap()
    private val objectMessageListenersLock = Any()
    
    private val onListUpdated = object : OnListUpdated {
        override fun onListUpdated() {
            chatterList.notifyListUpdated(ChatterListType.Active)
        }
    }
    
    private val unreadCountsPool = SubscriptionPool<ChatterID, UnreadMessageInfo>()

    init {
        findChatterQuery = chatterDao.queryBuilder()
            .where(ChatterDao.Properties.Type.eq(null), ChatterDao.Properties.Uuid.eq(""))
            .build()
        
        findChatterQueryNullUUID = chatterDao.queryBuilder()
            .where(ChatterDao.Properties.Type.eq(null), ChatterDao.Properties.Uuid.isNull)
            .build()
        
        localChatterID = ChatterID.getLocalChatterID(userManager.userID)
        messageSourceNameResolver = MessageSourceNameResolver(userManager, this)
        
        object : RequestFinalProcessor<ChatterID, UnreadMessageInfo>(
            unreadCountsPool, 
            userManager.databaseExecutor
        ) {
            override fun processRequest(request: ChatterID): UnreadMessageInfo {
                val chatter = getChatter(request) ?: return UnreadMessageInfo.create(0, null)
                
                val lastMessageID = chatter.lastMessageID ?: return UnreadMessageInfo.create(chatter.unreadCount, null)
                
                val chatMessage = chatMessageDao.load(lastMessageID) ?: return UnreadMessageInfo.create(chatter.unreadCount, null)
                
                val chatEvent = SLChatEvent.loadFromDatabaseObject(chatMessage, userManager.userID)
                return UnreadMessageInfo.create(chatter.unreadCount, chatEvent)
            }
        }
    }

    private fun clearChatHistoryInternal(chatterID: ChatterID) {
        synchronized(chatEventLock) {
            val chatter = getChatter(chatterID) ?: return
            
            val query = chatMessageDao.queryBuilder()
                .where(ChatMessageDao.Properties.ChatterDbID.eq(chatter.id))
                .build()
            
            query.list().forEach { message ->
                chatMessageDao.delete(message)
            }
            
            chatter.lastMessageID = null
            chatter.lastMessageTime = null
            chatter.unreadCount = 0
            chatterDao.update(chatter)
            
            onChatterUpdated(chatter)
        }
    }

    fun addDisplayedChatter(chatterID: ChatterID) {
        displayedChatters.add(chatterID)
    }

    fun addObjectMessageListener(listener: OnChatEventListener, executor: Executor) {
        synchronized(objectMessageListenersLock) {
            objectMessageListeners[listener] = executor
        }
    }

    fun clearChatHistory(chatterID: ChatterID) {
        userManager.databaseExecutor.execute {
            clearChatHistoryInternal(chatterID)
        }
    }

    fun deleteChatter(chatterID: ChatterID) {
        userManager.databaseExecutor.execute {
            synchronized(chatEventLock) {
                val chatter = getChatter(chatterID) ?: return@execute
                
                val query = chatMessageDao.queryBuilder()
                    .where(ChatMessageDao.Properties.ChatterDbID.eq(chatter.id))
                    .build()
                
                query.list().forEach { message ->
                    chatMessageDao.delete(message)
                }
                
                chatterDao.delete(chatter)
                chatterList.onChatterRemoved(chatter, ChatterListType.Active)
            }
        }
    }

    fun getChatter(chatterID: ChatterID): Chatter? {
        return when {
            chatterID.uuid != null -> {
                val query = chatterDao.queryBuilder()
                    .where(
                        ChatterDao.Properties.Type.eq(chatterID.type.ordinal),
                        ChatterDao.Properties.Uuid.eq(chatterID.uuid.toString())
                    )
                    .build()
                query.unique()
            }
            else -> null
        }
    }

    fun getChatMessageLoader(chatterID: ChatterID): ChatMessageLoader {
        synchronized(messageLoadersLock) {
            var loadersList = messageLoaders[chatterID]
            if (loadersList == null) {
                loadersList = mutableListOf()
                messageLoaders[chatterID] = loadersList
            }
            
            val iterator = loadersList.iterator()
            while (iterator.hasNext()) {
                val ref = iterator.next()
                val loader = ref.get()
                if (loader == null) {
                    iterator.remove()
                } else {
                    return loader
                }
            }
            
            val newLoader = ChatMessageLoader(chatterID)
            loadersList.add(WeakReference(newLoader))
            return newLoader
        }
    }

    fun muteChatter(chatterID: ChatterID) {
        userManager.databaseExecutor.execute {
            val chatter = getChatter(chatterID) ?: return@execute
            chatter.isMuted = true
            chatterDao.update(chatter)
            onChatterUpdated(chatter)
        }
    }

    override fun onMessageSourcesResolved(messageIds: Set<Long>, userName: UserName) {
        messageIds.forEach { messageId ->
            val chatMessage = chatMessageDao.load(messageId) ?: return@forEach
            chatMessage.senderName = userName.displayName
            chatMessage.senderLegacyName = userName.userName
            chatMessageDao.update(chatMessage)
            onMessageUpdated(chatMessage)
        }
    }

    fun releaseMessageLoader(chatterID: ChatterID, loader: ChatMessageLoader) {
        synchronized(messageLoadersLock) {
            val loadersList = messageLoaders[chatterID] ?: return
            val iterator = loadersList.iterator()
            while (iterator.hasNext()) {
                val ref = iterator.next()
                val currentLoader = ref.get()
                if (currentLoader == null || currentLoader == loader) {
                    iterator.remove()
                }
            }
        }
    }

    fun removeDisplayedChatter(chatterID: ChatterID) {
        displayedChatters.remove(chatterID)
    }

    fun removeObjectMessageListener(listener: OnChatEventListener) {
        synchronized(objectMessageListenersLock) {
            objectMessageListeners.remove(listener)
        }
    }

    fun unmuteChatter(chatterID: ChatterID) {
        userManager.databaseExecutor.execute {
            val chatter = getChatter(chatterID) ?: return@execute
            chatter.isMuted = false
            chatterDao.update(chatter)
            onChatterUpdated(chatter)
        }
    }

    private fun onChatterUpdated(chatter: Chatter) {
        chatterList.onChatterUpdated(chatter, ChatterListType.Active)
    }

    private fun onMessageUpdated(message: ChatMessage) {
        val event = ChatMessageEvent(message, false, false)
        chatEventBus.post(event)
    }

    inner class ChatMessageLoader(private val chatterID: ChatterID) : ChunkedListLoader<ChatMessage> {
        private var lazyList: LazyList<ChatMessage>? = null
        
        override fun loadChunk(offset: Int, limit: Int): List<ChatMessage> {
            val chatter = getChatter(chatterID) ?: return emptyList()
            
            if (lazyList == null) {
                val query = chatMessageDao.queryBuilder()
                    .where(ChatMessageDao.Properties.ChatterDbID.eq(chatter.id))
                    .orderDesc(ChatMessageDao.Properties.Timestamp)
                    .build()
                lazyList = query.listLazy()
            }
            
            val list = lazyList ?: return emptyList()
            val result = mutableListOf<ChatMessage>()
            val endIndex = minOf(offset + limit, list.size)
            
            for (i in offset until endIndex) {
                result.add(list[i])
            }
            
            return result
        }
        
        override fun getCount(): Int {
            val chatter = getChatter(chatterID) ?: return 0
            return chatMessageDao.queryBuilder()
                .where(ChatMessageDao.Properties.ChatterDbID.eq(chatter.id))
                .count()
                .toInt()
        }
        
        fun close() {
            lazyList?.close()
            lazyList = null
        }
    }

    interface OnListUpdated {
        fun onListUpdated()
    }
}
