package com.linkpoint.chat.queue

import com.linkpoint.ui.chat.ChatChannel
import com.linkpoint.ui.chat.ChatMessage as UiChatMessage
import com.linkpoint.ui.chat.MessageType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class FrameAlignedMessageQueueTest {

    @Test
    fun `test ChatRingBuffer thread-safe ingestion and FIFO sequence preservation`() = runBlocking {
        val ringBuffer = ChatRingBuffer(capacity = 2000, backpressureThreshold = 1500)
        val threadCount = 10
        val itemsPerThread = 100

        // Concurrently enqueue from 10 coroutines/threads
        val jobs = (0 until threadCount).map { threadIdx ->
            async(Dispatchers.Default) {
                for (i in 0 until itemsPerThread) {
                    val index = threadIdx * itemsPerThread + i
                    val msg = UiChatMessage(
                        id = "msg-$index",
                        sender = "User-$threadIdx",
                        content = "Payload $index",
                        timestamp = 1000L + index,
                        type = MessageType.NORMAL,
                        channel = ChatChannel.LOCAL,
                    )
                    ringBuffer.enqueue(QueuedChatMessage.Ui(msg))
                }
            }
        }
        jobs.awaitAll()

        assertEquals("Ring buffer size should equal total enqueued items", 1000, ringBuffer.size)

        val drained = ringBuffer.drainAll()
        assertEquals("Drained size should equal 1000", 1000, drained.size)
        assertEquals("Ring buffer size after drain should be 0", 0, ringBuffer.size)
    }

    @Test
    fun `test ChatRingBuffer backpressure trimming under packet floods`() {
        val ringBuffer = ChatRingBuffer(capacity = 1000, backpressureThreshold = 500)

        // Enqueue 600 items into buffer with threshold 500
        for (i in 0 until 600) {
            val msg = UiChatMessage(
                id = "flood-$i",
                sender = "FloodBot",
                content = "Flood message $i",
                timestamp = 1000L + i,
                type = MessageType.NORMAL,
                channel = ChatChannel.LOCAL,
            )
            ringBuffer.enqueue(QueuedChatMessage.Ui(msg))
        }

        val drained = ringBuffer.drainAll()

        // Backpressure drops oldest excess (600 - 250 = 350 dropped), prepends 1 notice, leaves 250 newer messages -> 251 total
        assertTrue("Drained size must be bounded under threshold", drained.size <= 500)
        val notice = drained.firstOrNull { it is QueuedChatMessage.SystemNotice }
        assertNotNull("Backpressure system notice must be injected", notice)

        val systemNotice = notice as QueuedChatMessage.SystemNotice
        assertTrue(
            "Notice should mention merged count",
            systemNotice.noticeText.contains("older messages merged"),
        )
    }

    @Test
    fun `test FrameAlignedMessageQueueService frame-aligned flushing`() {
        val service = FrameAlignedMessageQueueService(
            frameIntervalMs = 1000L, // manual flush in test
            ringBufferCapacity = 500,
            backpressureThreshold = 300,
        )
        service.stopFrameTicker() // Control ticks manually in test

        // Initially state is empty
        assertEquals(0, service.chatViewState.value.messages.size)

        // Ingest 5 messages off UI thread
        for (i in 0 until 5) {
            val uiMsg = UiChatMessage(
                id = "id-$i",
                sender = "Sender",
                content = "Message $i",
                timestamp = System.currentTimeMillis(),
                type = MessageType.NORMAL,
                channel = ChatChannel.LOCAL,
            )
            service.enqueueMessage(QueuedChatMessage.Ui(uiMsg))
        }

        // State snapshot has NOT updated yet prior to frame tick
        assertEquals("State snapshot should remain un-flushed prior to frame tick", 0, service.chatViewState.value.messages.size)

        // Perform frame tick flush
        val snapshot = service.flushFrame(100000000L)

        assertEquals("Snapshot must contain 5 messages after frame flush", 5, snapshot.messages.size)
        assertEquals(5L, snapshot.totalProcessedCount)
        assertEquals(100000000L, snapshot.frameTimestampNanos)
        assertEquals(5, service.chatViewState.value.messages.size)

        service.shutdown()
    }

    @Test
    fun `test extreme packet flood over 200 msgs per sec maintains frame alignment`() = runBlocking {
        val service = FrameAlignedMessageQueueService(
            frameIntervalMs = 1000L, // manual flushing for simulation
            ringBufferCapacity = 500,
            backpressureThreshold = 200,
        )
        service.stopFrameTicker()

        // Simulate network dispatcher receiving 300 messages per second off-thread
        val floodCount = 300
        val floodProducer = async(Dispatchers.Default) {
            for (i in 0 until floodCount) {
                val uiMsg = UiChatMessage(
                    id = "flood-packet-$i",
                    sender = "SpamBot",
                    content = "Spam content $i",
                    timestamp = System.currentTimeMillis(),
                    type = MessageType.NORMAL,
                    channel = ChatChannel.GROUP,
                )
                service.enqueueMessage(QueuedChatMessage.Ui(uiMsg))
            }
        }
        floodProducer.await()

        // Ring buffer absorbed all packets off UI thread
        // Execute 1st frame tick flush
        val frame1Snapshot = service.flushFrame(frameNanos = 16_666_666L)

        // Verify state snapshot was updated in a single batch pass
        assertFalse("Messages list should not be empty after frame tick", frame1Snapshot.messages.isEmpty())
        assertTrue("Backpressure should trigger notice under 300 msg flood", service.chatViewState.value.backpressureEventCount > 0)
        assertEquals(16_666_666L, frame1Snapshot.frameTimestampNanos)

        // Group channel pre-filtering check
        val groupMsgs = frame1Snapshot.messagesForChannel(ChatChannel.GROUP)
        assertFalse("Pre-filtered group messages should be populated", groupMsgs.isEmpty())

        service.shutdown()
    }

    @Test
    fun `test ChatViewState immutable channel filtering`() {
        val msgLocal = UiChatMessage("1", "User1", "Hi local", 100L, MessageType.NORMAL, ChatChannel.LOCAL)
        val msgIm = UiChatMessage("2", "User2", "Hi IM", 101L, MessageType.NORMAL, ChatChannel.IM)
        val msgGroup = UiChatMessage("3", "User3", "Hi Group", 102L, MessageType.NORMAL, ChatChannel.GROUP)

        val channelMap = mapOf(
            ChatChannel.LOCAL to listOf(msgLocal),
            ChatChannel.IM to listOf(msgIm),
            ChatChannel.GROUP to listOf(msgGroup),
        )

        val state = ChatViewState(
            messages = listOf(msgLocal, msgIm, msgGroup),
            filteredMessagesByChannel = channelMap,
        )

        assertEquals(1, state.messagesForChannel(ChatChannel.LOCAL).size)
        assertEquals("Hi local", state.messagesForChannel(ChatChannel.LOCAL)[0].content)
        assertEquals(1, state.messagesForChannel(ChatChannel.IM).size)
        assertEquals("Hi IM", state.messagesForChannel(ChatChannel.IM)[0].content)
        assertEquals(1, state.messagesForChannel(ChatChannel.GROUP).size)
        assertEquals("Hi Group", state.messagesForChannel(ChatChannel.GROUP)[0].content)
    }
}
