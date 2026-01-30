package com.linkpoint.world

import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.LLSDMap
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okio.Timeout
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException

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
        val mockCapabilityManager = CapabilityManager()

        val searchManager = SearchManager(mockCapabilityManager, mockCallFactory)
        val results = searchManager.searchPlaces("test")

        assertEquals(1, results.totalCount)
        assertEquals("Test Place", results.results[0].name)
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
