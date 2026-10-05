package com.linkpoint.chat

import com.linkpoint.protocol.messages.ChatData
import com.linkpoint.protocol.messages.ChatSourceType
import com.linkpoint.protocol.messages.ChatType
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.types.LLVector3
import com.linkpoint.ui.chat.formatTypingIndicator
import com.linkpoint.ui.people.NearbyPerson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class SpatialChatTypingIndicatorTest {

    @Test
    fun `test ChatManager handles typing start and stop simulator packets`() = runTest {
        val agentId = UUID.randomUUID()
        val udpConnection = UDPConnectionFixed()
        val chatManager = ChatManager(udpConnection, agentId)

        val typingAvatar1 = UUID.randomUUID()
        val typingAvatar2 = UUID.randomUUID()

        // Initially no typing avatars
        assertTrue(chatManager.typingAvatars.value.isEmpty())

        // Simulator sends START_TYPING for avatar 1
        chatManager.handleChatFromSimulator(
            ChatData(
                fromName = "Avatar One",
                sourceId = typingAvatar1,
                ownerId = typingAvatar1,
                sourceType = ChatSourceType.AGENT,
                chatType = ChatType.START_TYPING,
                audible = 1,
                position = LLVector3.zero(),
                message = ""
            )
        )

        waitUntil { chatManager.typingAvatars.value.contains(typingAvatar1) }

        // Simulator sends START_TYPING for avatar 2
        chatManager.handleChatFromSimulator(
            ChatData(
                fromName = "Avatar Two",
                sourceId = typingAvatar2,
                ownerId = typingAvatar2,
                sourceType = ChatSourceType.AGENT,
                chatType = ChatType.START_TYPING,
                audible = 1,
                position = LLVector3.zero(),
                message = ""
            )
        )

        waitUntil { chatManager.typingAvatars.value.contains(typingAvatar2) }
        assertTrue(chatManager.typingAvatars.value.contains(typingAvatar1))

        // Simulator sends STOP_TYPING for avatar 1
        chatManager.handleChatFromSimulator(
            ChatData(
                fromName = "Avatar One",
                sourceId = typingAvatar1,
                ownerId = typingAvatar1,
                sourceType = ChatSourceType.AGENT,
                chatType = ChatType.STOP_TYPING,
                audible = 1,
                position = LLVector3.zero(),
                message = ""
            )
        )

        waitUntil { !chatManager.typingAvatars.value.contains(typingAvatar1) }
        assertTrue(chatManager.typingAvatars.value.contains(typingAvatar2))

        // Simulator sends normal chat message from avatar 2 -> clears typing state for avatar 2
        chatManager.handleChatFromSimulator(
            ChatData(
                fromName = "Avatar Two",
                sourceId = typingAvatar2,
                ownerId = typingAvatar2,
                sourceType = ChatSourceType.AGENT,
                chatType = ChatType.NORMAL,
                audible = 1,
                position = LLVector3.zero(),
                message = "Hello world"
            )
        )

        waitUntil { chatManager.typingAvatars.value.isEmpty() }

        chatManager.shutdown()
    }

    private fun waitUntil(timeoutMs: Long = 3000, condition: () -> Boolean) {
        val start = System.currentTimeMillis()
        while (!condition()) {
            if (System.currentTimeMillis() - start > timeoutMs) {
                throw AssertionError("Condition not met within $timeoutMs ms")
            }
            Thread.sleep(20)
        }
    }

    @Test
    fun `test formatTypingIndicator output formatting`() {
        assertEquals("", formatTypingIndicator(emptyList()))
        assertEquals("Alice is typing…", formatTypingIndicator(listOf("Alice")))
        assertEquals("Alice and Bob are typing…", formatTypingIndicator(listOf("Alice", "Bob")))
        assertEquals("Alice and 2 others are typing…", formatTypingIndicator(listOf("Alice", "Bob", "Charlie")))
        assertEquals("Alice and 3 others are typing…", formatTypingIndicator(listOf("Alice", "Bob", "Charlie", "Dave")))
    }

    @Test
    fun `test NearbyPerson typing state model`() {
        val id = UUID.randomUUID()
        val personNotTyping = NearbyPerson(id = id, name = "Test Resident", distance = 12.0f, isFriend = true)
        assertFalse(personNotTyping.isTyping)

        val personTyping = personNotTyping.copy(isTyping = true)
        assertTrue(personTyping.isTyping)
        assertEquals(id, personTyping.id)
        assertEquals("Test Resident", personTyping.name)
        assertEquals(12.0f, personTyping.distance)
        assertTrue(personTyping.isFriend)
    }
}
