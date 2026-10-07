package com.linkpoint.chat.queue

import com.linkpoint.chat.ChatManager
import com.linkpoint.chat.IMManager
import com.linkpoint.messaging.MessagingDispatcher
import com.linkpoint.ui.chat.ChatChannel
import com.linkpoint.ui.chat.ChatMessage as UiChatMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Dedicated Frame-Aligned Message Queue and Ring Buffer Service.
 *
 * Ingests inbound chat/IM packets off the UI thread into a thread-safe ring buffer,
 * applying backpressure under extreme flood activity. A frame manager flushes queued
 * items into immutable [ChatViewState] snapshots strictly at display refresh intervals,
 * decoupling network packet rates from UI render passes to maintain 60 FPS compliance.
 */
class FrameAlignedMessageQueueService(
    private val chatManager: ChatManager? = null,
    private val imManager: IMManager? = null,
    private val myAgentIdProvider: () -> UUID = { UUID(0L, 0L) },
    private val frameIntervalMs: Long = 16L, // ~60 FPS display refresh cycle
    private val maxUiHistorySize: Int = 500,
    ringBufferCapacity: Int = ChatRingBuffer.DEFAULT_CAPACITY,
    backpressureThreshold: Int = ChatRingBuffer.DEFAULT_BACKPRESSURE_THRESHOLD,
) {
    companion object {
        const val DEFAULT_FRAME_INTERVAL_MS = 16L
    }

    private val ringBuffer = ChatRingBuffer(ringBufferCapacity, backpressureThreshold)
    private val scope = CoroutineScope(MessagingDispatcher.dispatcher + SupervisorJob())

    private val activeUiMessages = mutableListOf<UiChatMessage>()
    private val _chatViewState = MutableStateFlow(ChatViewState())
    val chatViewState: StateFlow<ChatViewState> = _chatViewState.asStateFlow()

    private var frameTickerJob: Job? = null
    private var totalProcessedCount = 0L
    private var backpressureEventCount = 0L

    var activeImSessionId: UUID? = null
    var activeGroupSessionId: UUID? = null

    init {
        startIngestion()
        startFrameTicker()
    }

    /**
     * Start background coroutine flow collectors to ingest packets off the UI thread.
     */
    fun startIngestion() {
        chatManager?.let { cm ->
            scope.launch {
                cm.chatFlow.collect { chat ->
                    val myId = myAgentIdProvider()
                    enqueueMessage(QueuedChatMessage.Core(chat, myId))
                }
            }
        }

        imManager?.let { im ->
            scope.launch {
                im.messageFlow.collect { message ->
                    val myId = myAgentIdProvider()
                    val channel = when (message.sessionId) {
                        activeImSessionId -> ChatChannel.IM
                        activeGroupSessionId -> ChatChannel.GROUP
                        else -> ChatChannel.IM
                    }
                    enqueueMessage(QueuedChatMessage.Instant(message, myId, channel))
                }
            }
        }
    }

    /**
     * Thread-safe ingestion method for incoming message packets off the UI thread.
     */
    fun enqueueMessage(item: QueuedChatMessage) {
        ringBuffer.enqueue(item)
    }

    /**
     * Thread-safe bulk ingestion method.
     */
    fun enqueueMessages(items: List<QueuedChatMessage>) {
        ringBuffer.enqueueAll(items)
    }

    /**
     * Seed initial history into active UI messages and trigger an initial frame snapshot.
     */
    fun seedHistory(history: List<UiChatMessage>) {
        synchronized(activeUiMessages) {
            activeUiMessages.clear()
            activeUiMessages.addAll(history.takeLast(maxUiHistorySize))
        }
        flushFrame(System.nanoTime())
    }

    /**
     * Start display refresh frame ticker (~16.6ms / 60 FPS).
     */
    fun startFrameTicker() {
        if (frameTickerJob?.isActive == true) return
        frameTickerJob = scope.launch(Dispatchers.Default) {
            while (isActive) {
                delay(frameIntervalMs)
                flushFrame(System.nanoTime())
            }
        }
    }

    /**
     * Stop the frame ticker loop.
     */
    fun stopFrameTicker() {
        frameTickerJob?.cancel()
        frameTickerJob = null
    }

    /**
     * Flush queued ring buffer items into an immutable UI state snapshot.
     * Can be invoked synchronously by tests or frame tickers.
     */
    fun flushFrame(frameNanos: Long = System.nanoTime()): ChatViewState {
        val drained = ringBuffer.drainAll()
        val pendingCount = ringBuffer.size

        if (drained.isEmpty()) {
            return _chatViewState.value
        }

        val newUiMessages = ArrayList<UiChatMessage>(drained.size)
        for (item in drained) {
            if (item is QueuedChatMessage.SystemNotice) {
                backpressureEventCount++
            }
            val uiMsg = when (item) {
                is QueuedChatMessage.Core -> item.toUiMessage()
                is QueuedChatMessage.Instant -> item.toUiMessage()
                is QueuedChatMessage.Ui -> item.uiMessage
                is QueuedChatMessage.SystemNotice -> item.toUiMessage()
            }
            newUiMessages.add(uiMsg)
        }

        totalProcessedCount += newUiMessages.size

        val updatedMessages: List<UiChatMessage> = synchronized(activeUiMessages) {
            activeUiMessages.addAll(newUiMessages)
            while (activeUiMessages.size > maxUiHistorySize) {
                activeUiMessages.removeAt(0)
            }
            activeUiMessages.toList()
        }

        val channelMap = updatedMessages.groupBy { it.channel }

        val snapshot = ChatViewState(
            messages = updatedMessages,
            filteredMessagesByChannel = channelMap,
            frameTimestampNanos = frameNanos,
            pendingQueueSize = pendingCount,
            totalProcessedCount = totalProcessedCount,
            backpressureEventCount = backpressureEventCount,
        )

        _chatViewState.value = snapshot
        return snapshot
    }

    /**
     * Clear all active state and pending ring buffer queue items.
     */
    fun clear() {
        ringBuffer.clear()
        synchronized(activeUiMessages) {
            activeUiMessages.clear()
        }
        _chatViewState.value = ChatViewState()
    }

    /**
     * Cancel coroutine jobs and shut down the service.
     */
    fun shutdown() {
        stopFrameTicker()
        scope.cancel()
    }
}
