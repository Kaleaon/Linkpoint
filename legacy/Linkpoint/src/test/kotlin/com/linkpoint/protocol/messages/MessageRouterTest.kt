package com.linkpoint.protocol.messages

import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class MessageRouterTest {

    @Test
    fun `handler counts update for register, unregister, and clear`() = runBlocking {
        val router = MessageRouter()
        val handler = object : MessageRouter.Handler {
            override fun handleMessage(messageId: Int, data: ByteArray): Boolean = true
        }

        router.registerHandler(1, handler)
        router.registerHandler(2, handler)

        assertEquals(2, router.getMessageCount())
        assertEquals(2, router.getHandlerCount())

        router.unregisterHandler(1, handler)

        assertEquals(1, router.getMessageCount())
        assertEquals(1, router.getHandlerCount())

        router.clearAll()

        assertEquals(0, router.getMessageCount())
        assertEquals(0, router.getHandlerCount())
    }
}
