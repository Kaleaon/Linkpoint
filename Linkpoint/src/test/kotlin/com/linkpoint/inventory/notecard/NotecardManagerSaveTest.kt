package com.linkpoint.inventory.notecard

import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDString
import com.linkpoint.protocol.llsd.LLSDUUID
import com.linkpoint.protocol.llsd.LLSDXmlUtils
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.transfer.TransferManager
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.InetSocketAddress
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference

class NotecardManagerSaveTest {

    @Test
    fun `saveNotecard uses agent capability and succeeds`() = runTest {
        val requestInfo = AtomicReference<Pair<String, LLSDMap>>()

        withTestServer(responseXml = llsdState("complete")) { serverUrl ->
            val manager = buildManager { capName, body ->
                requestInfo.set(capName to body)
                LLSDMap(mutableMapOf("uploader" to LLSDString(serverUrl)))
            }

            val success = manager.saveNotecard(UUID.randomUUID(), "hello agent")
            assertTrue(success)
        }

        val (capName, body) = requestInfo.get()
        assertEquals(CapabilityManager.CAP_UPDATE_NOTECARD_AGENT, capName)
        assertTrue(body["item_id"] is LLSDUUID)
        assertEquals(null, body["task_id"])
        assertEquals(null, body["object_id"])
    }

    @Test
    fun `saveNotecard agent path fails when capability request returns no payload`() = runTest {
        val manager = buildManager { _, _ -> null }
        val success = manager.saveNotecard(UUID.randomUUID(), "agent failure")
        assertFalse(success)
    }

    @Test
    fun `saveNotecard uses task capability and includes task and object identifiers`() = runTest {
        val requestInfo = AtomicReference<Pair<String, LLSDMap>>()
        val taskId = UUID.randomUUID()
        val objectId = UUID.randomUUID()

        withTestServer(responseXml = llsdState("complete")) { serverUrl ->
            val manager = buildManager { capName, body ->
                requestInfo.set(capName to body)
                LLSDMap(mutableMapOf("uploader" to LLSDString(serverUrl)))
            }

            val success = manager.saveNotecard(
                itemId = UUID.randomUUID(),
                newText = "hello task",
                taskId = taskId,
                objectId = objectId
            )
            assertTrue(success)
        }

        val (capName, body) = requestInfo.get()
        assertEquals(CapabilityManager.CAP_UPDATE_NOTECARD_TASK, capName)
        assertEquals(taskId, (body["task_id"] as LLSDUUID).value)
        assertEquals(objectId, (body["object_id"] as LLSDUUID).value)
    }

    @Test
    fun `saveNotecard task path fails when capability request returns no payload`() = runTest {
        val manager = buildManager { _, _ -> null }
        val success = manager.saveNotecard(
            itemId = UUID.randomUUID(),
            newText = "task failure",
            taskId = UUID.randomUUID(),
            objectId = UUID.randomUUID()
        )
        assertFalse(success)
    }

    @Test
    fun `saveNotecard fails when capability response omits uploader`() = runTest {
        val manager = buildManager { _, _ -> LLSDMap() }
        val success = manager.saveNotecard(UUID.randomUUID(), "no uploader")
        assertFalse(success)
    }

    @Test
    fun `saveNotecard fails when uploader response state is not complete`() = runTest {
        withTestServer(responseXml = llsdState("queued")) { serverUrl ->
            val manager = buildManager { _, _ ->
                LLSDMap(mutableMapOf("uploader" to LLSDString(serverUrl)))
            }

            val success = manager.saveNotecard(UUID.randomUUID(), "queued state")
            assertFalse(success)
        }
    }

    private fun buildManager(
        capRequest: suspend (String, LLSDMap) -> com.linkpoint.protocol.llsd.LLSDValue?
    ): NotecardManager {
        val transferManager = TransferManager(UDPConnectionFixed(), UUID(0, 0), UUID(0, 0))
        return NotecardManager(
            transferManager = transferManager,
            capabilityManager = CapabilityManager(),
            httpClient = OkHttpClient(),
            capabilityRequest = capRequest
        )
    }

    private fun llsdState(state: String): String {
        return LLSDXmlUtils.wrap(LLSDMap(mutableMapOf("state" to LLSDString(state))))
    }

    private fun withTestServer(responseXml: String, block: (String) -> Unit) {
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/") { exchange ->
            val bytes = responseXml.toByteArray(Charsets.UTF_8)
            exchange.responseHeaders.add("Content-Type", "application/llsd+xml")
            exchange.sendResponseHeaders(200, bytes.size.toLong())
            exchange.responseBody.use { it.write(bytes) }
        }
        server.start()
        try {
            block("http://127.0.0.1:${server.address.port}/")
        } finally {
            server.stop(0)
        }
    }
}
