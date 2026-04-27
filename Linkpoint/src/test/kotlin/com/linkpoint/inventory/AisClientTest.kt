package com.linkpoint.inventory

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class AisClientTest {

    @Test
    fun `retries on 5xx with bounded exponential backoff`() = runBlocking {
        val responses = ArrayDeque(
            listOf(
                AisHttpResponse(503, ""),
                AisHttpResponse(502, ""),
                AisHttpResponse(
                    200,
                    """
                    {"folder":{"folder_id":"00000000-0000-0000-0000-000000000001","parent_id":"00000000-0000-0000-0000-000000000000","name":"Root","version":4,"type":9},"categories":[],"items":[]}
                    """.trimIndent()
                )
            )
        )
        val recordedDelays = mutableListOf<Long>()

        val client = AisClient(
            endpointProvider = object : InventoryApiEndpointProvider {
                override fun getInventoryApiBaseUrl(): String = "https://example.test/ais"
            },
            transport = object : AisTransport {
                override suspend fun execute(request: AisHttpRequest): AisHttpResponse = responses.removeFirst()
            },
            baseDelayMs = 100,
            maxDelayMs = 150,
            maxServerRetries = 4,
            sleeper = { recordedDelays += it }
        )

        val subtree = client.getSubtree(UUID.fromString("00000000-0000-0000-0000-000000000001"))

        assertEquals(4, subtree.folder.version)
        assertEquals(listOf(100L, 150L), recordedDelays)
    }

    @Test
    fun `404 raises capability unavailable`() = runBlocking {
        val client = AisClient(
            endpointProvider = object : InventoryApiEndpointProvider {
                override fun getInventoryApiBaseUrl(): String = "https://example.test/ais"
            },
            transport = object : AisTransport {
                override suspend fun execute(request: AisHttpRequest): AisHttpResponse = AisHttpResponse(404, "")
            }
        )

        var raised = false
        try {
            client.getSubtree(UUID.randomUUID())
        } catch (e: AisCapabilityUnavailableException) {
            raised = true
        }

        assertTrue(raised)
    }
}
