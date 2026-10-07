package com.linkpoint.world

import com.linkpoint.model.search.*
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.LLSDMap
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okio.Timeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import java.io.IOException
import java.util.UUID

@RunWith(JUnit4::class)
class SearchManagerTest {

    @Test
    fun testSearchPlaces() = runBlocking {
        val jsonResponse = """
            {
                "places": [
                    {
                        "parcel_id": "00000000-0000-0000-0000-000000000001",
                        "name": "Test Place",
                        "description": "A nice place",
                        "region": "Test Region",
                        "category": "General",
                        "traffic": 100,
                        "area": 512,
                        "location": "128,128,0"
                    }
                ],
                "total": 1
            }
        """.trimIndent()

        val mockCallFactory = MockCallFactory(jsonResponse)
        val mockCapabilityManager = mock(CapabilityManager::class.java)

        val searchManager = SearchManager(mockCapabilityManager, mockCallFactory)
        val results = searchManager.searchPlaces("test")

        assertEquals(1, results.totalCount)
        val firstResult: SearchResult = results.results[0]
        assertEquals("Test Place", firstResult.name)
        assertEquals("A nice place", firstResult.description)
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), firstResult.id)
        assertTrue(firstResult is PlaceResult)
    }

    @Test
    fun testSearchResultInterfaceContract() {
        val personId = UUID.randomUUID()
        val person: SearchResult = PersonResult(
            agentId = personId,
            displayName = "Alice Resident",
            userName = "alice.resident",
            isOnline = true
        )
        assertEquals(personId, person.id)
        assertEquals("Alice Resident", person.name)
        assertEquals("alice.resident", person.description)

        val groupId = UUID.randomUUID()
        val group: SearchResult = GroupResult(
            groupId = groupId,
            name = "Developers Group",
            charter = "A group for developers",
            memberCount = 42,
            isOpen = true
        )
        assertEquals(groupId, group.id)
        assertEquals("Developers Group", group.name)
        assertEquals("A group for developers", group.description)

        val event: SearchResult = EventResult(
            eventId = 12345,
            name = "Live Music",
            description = "Concert at noon",
            region = "Main Stage"
        )
        assertEquals(UUID(0L, 12345L), event.id)
        assertEquals("Live Music", event.name)
        assertEquals("Concert at noon", event.description)
    }

    @Test
    fun testGetEffectiveSearchBaseUrl() {
        val mockCapabilityManager = mock(CapabilityManager::class.java)
        val searchManager = SearchManager(mockCapabilityManager)
        val baseUrl = searchManager.getEffectiveSearchBaseUrl()
        // Default fallback returns Second Life search endpoint
        assertEquals("https://search.secondlife.com/client_search", baseUrl)
    }

    class MockCallFactory(private val responseBody: String) : Call.Factory {
        override fun newCall(request: Request): Call {
            return MockCall(request, responseBody)
        }
    }

    class MockCall(private val request: Request, private val responseBody: String) : Call {
        override fun execute(): Response {
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create("application/json".toMediaType(), responseBody))
                .build()
        }

        override fun enqueue(responseCallback: Callback) {
            responseCallback.onResponse(this, execute())
        }

        override fun cancel() {}
        override fun clone(): Call = this
        override fun isCanceled(): Boolean = false
        override fun isExecuted(): Boolean = true
        override fun request(): Request = request
        override fun timeout(): Timeout = Timeout.NONE
    }
}
