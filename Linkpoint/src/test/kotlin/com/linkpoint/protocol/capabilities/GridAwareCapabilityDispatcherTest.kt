package com.linkpoint.protocol.capabilities

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linkpoint.protocol.translation.LinkpointTranslationLayer
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GridAwareCapabilityDispatcherTest {

    @Test
    fun test_grid_aware_capability_dispatcher() {
        // 1. Verify strategy selection for Second Life Agni profile
        val agniLoginUrl = "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
        val agniStrategy = CapabilityStrategyDispatcher.selectStrategy(agniLoginUrl)
        assertTrue("Agni login URL must select LindenS3CapHandler", agniStrategy is LindenS3CapHandler)
        assertEquals("Agni grid type must be AGNI", LinkpointTranslationLayer.GridType.AGNI, agniStrategy.gridType)

        // 2. Verify strategy selection for Second Life Aditi profile
        val aditiLoginUrl = "https://login.aditi.lindenlab.com/cgi-bin/login.cgi"
        val aditiStrategy = CapabilityStrategyDispatcher.selectStrategy(aditiLoginUrl)
        assertTrue("Aditi login URL must select LindenS3CapHandler", aditiStrategy is LindenS3CapHandler)
        assertEquals("Aditi grid type must be ADITI", LinkpointTranslationLayer.GridType.ADITI, aditiStrategy.gridType)

        // 3. Verify strategy selection for OpenSim profile
        val openSimLoginUrl = "http://grid.osgrid.org:8002/"
        val openSimStrategy = CapabilityStrategyDispatcher.selectStrategy(openSimLoginUrl)
        assertTrue("OpenSim login URL must select OpenSimWebFetchCapHandler", openSimStrategy is OpenSimWebFetchCapHandler)
        assertEquals("OpenSim grid type must be OPENSIM", LinkpointTranslationLayer.GridType.OPENSIM, openSimStrategy.gridType)

        // 4. Verify explicit grid type override
        val explicitOpenSim = CapabilityStrategyDispatcher.selectStrategy(
            loginUrl = agniLoginUrl,
            explicitGridType = LinkpointTranslationLayer.GridType.OPENSIM
        )
        assertTrue("Explicit OPENSIM override must select OpenSimWebFetchCapHandler", explicitOpenSim is OpenSimWebFetchCapHandler)

        // 5. Verify capability URL resolution for Linden S3 strategy vs OpenSim WebFetch strategy
        val unreturnedSimCapUrl = "http://sim10045/cap/3b1263d9-9523-45c1-bd93-189f33877992"
        val repairedAgniUrl = agniStrategy.validateCapabilityUrl("GetTexture", unreturnedSimCapUrl, agniLoginUrl)
        assertEquals(
            "Agni sim URL must be repaired with .agni.lindenlab.com and https scheme",
            "https://sim10045.agni.lindenlab.com/cap/3b1263d9-9523-45c1-bd93-189f33877992",
            repairedAgniUrl
        )

        val openSimWebFetchUrl = "http://assets.osgrid.org:8003/CAP/webfetch/89f2c140-52a1-432a-bc91-23d919875e11"
        val resolvedOpenSimUrl = openSimStrategy.validateCapabilityUrl("GetTexture", openSimWebFetchUrl, openSimLoginUrl)
        assertEquals(
            "OpenSim WebFetch URL must be preserved without Agni domain corruption",
            openSimWebFetchUrl,
            resolvedOpenSimUrl
        )

        // 6. Verify header transformation
        val agniHeaders = agniStrategy.transformHeaders("GetTexture", emptyMap())
        assertTrue("Agni headers must request llsd XML/binary", agniHeaders["Accept"]?.contains("application/llsd+xml") == true)

        val openSimHeaders = openSimStrategy.transformHeaders("GetTexture", emptyMap())
        assertEquals("OpenSim headers must set X-OpenSim-Capability flag", "true", openSimHeaders["X-OpenSim-Capability"])
        assertTrue("OpenSim headers must accept image/x-j2c and wildcard", openSimHeaders["Accept"]?.contains("image/x-j2c") == true)
    }

    @Test
    fun `test OpenSimWebFetchCapHandler parses LLSD, JSON, and key-value formats`() {
        val handler = OpenSimWebFetchCapHandler()

        // Test LLSD XML
        val llsdXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <llsd>
                <map>
                    <key>GetTexture</key>
                    <string>http://assets.osgrid.org:8003/CAP/GetTexture/</string>
                    <key>FetchInventory2</key>
                    <string>http://inventory.osgrid.org:8003/CAP/FetchInventory/</string>
                </map>
            </llsd>
        """.trimIndent().toByteArray(Charsets.UTF_8)

        val llsdResult = handler.parseCapabilityResponse(llsdXml, "application/llsd+xml")
        assertNotNull("LLSD parse result should not be null", llsdResult)
        assertEquals("http://assets.osgrid.org:8003/CAP/GetTexture/", llsdResult!!["GetTexture"])
        assertEquals("http://inventory.osgrid.org:8003/CAP/FetchInventory/", llsdResult["FetchInventory2"])

        // Test JSON payload
        val jsonPayload = """
            {
                "GetTexture": "http://assets.osgrid.org:8003/CAP/GetTexture/",
                "WebFetch": "http://assets.osgrid.org:8003/CAP/WebFetch/"
            }
        """.trimIndent().toByteArray(Charsets.UTF_8)

        val jsonResult = handler.parseCapabilityResponse(jsonPayload, "application/json")
        assertNotNull("JSON parse result should not be null", jsonResult)
        assertEquals("http://assets.osgrid.org:8003/CAP/GetTexture/", jsonResult!!["GetTexture"])
        assertEquals("http://assets.osgrid.org:8003/CAP/WebFetch/", jsonResult["WebFetch"])

        // Test key-value text payload
        val kvPayload = """
            # OpenSim WebFetch Caps
            GetTexture=http://assets.osgrid.org:8003/CAP/GetTexture/
            WebFetch=http://assets.osgrid.org:8003/CAP/WebFetch/
        """.trimIndent().toByteArray(Charsets.UTF_8)

        val kvResult = handler.parseCapabilityResponse(kvPayload, "text/plain")
        assertNotNull("Key-value parse result should not be null", kvResult)
        assertEquals("http://assets.osgrid.org:8003/CAP/GetTexture/", kvResult!!["GetTexture"])
        assertEquals("http://assets.osgrid.org:8003/CAP/WebFetch/", kvResult["WebFetch"])
    }

    @Test
    fun `test HybridFallbackCapHandler handles unknown and mismatched grid profiles`() {
        val unknownUrl = "http://custom-private-grid.internal:9000/login"
        val strategy = CapabilityStrategyDispatcher.selectStrategy(unknownUrl)

        assertTrue("Unknown grid URL must select HybridFallbackCapHandler or OpenSim strategy",
            strategy is HybridFallbackCapHandler || strategy is OpenSimWebFetchCapHandler)

        val rawCapUrl = "http://custom-private-grid.internal:9000/CAP/123"
        val validatedUrl = strategy.validateCapabilityUrl("GetTexture", rawCapUrl, unknownUrl)
        assertEquals("Custom grid URL must be preserved", rawCapUrl, validatedUrl)

        val headers = strategy.transformHeaders("GetTexture", emptyMap())
        assertNotNull("Transformed headers must contain Accept header", headers["Accept"])
    }

    @Test
    fun `test CapabilityManager strategy integration`() {
        val manager = CapabilityManager()

        // 1. Verify default strategy is hybrid fallback
        assertEquals("Default strategy before init should be HybridFallbackCapHandler",
            "HybridFallbackCapHandler", manager.activeStrategy.javaClass.simpleName)

        // 2. Wire OpenSim strategy and verify manager state/diagnostics
        val openSimLoginUrl = "http://grid.osgrid.org:8002/"
        val openSimStrategy = CapabilityStrategyDispatcher.selectStrategy(openSimLoginUrl)
        manager.setStrategy(openSimStrategy)

        assertEquals("OpenSim login URL must wire OpenSimWebFetchCapHandler",
            "OpenSimWebFetchCapHandler", manager.activeStrategy.javaClass.simpleName)
        assertEquals("Grid type in diagnostics must be OPENSIM",
            "OPENSIM", manager.getDiagnostics().gridType)

        // 3. Wire Second Life Agni strategy and verify manager state/diagnostics
        val agniLoginUrl = "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
        val agniStrategy = CapabilityStrategyDispatcher.selectStrategy(agniLoginUrl)
        manager.setStrategy(agniStrategy)

        assertEquals("Agni login URL must wire LindenS3CapHandler",
            "LindenS3CapHandler", manager.activeStrategy.javaClass.simpleName)
        assertEquals("Grid type in diagnostics must be AGNI",
            "AGNI", manager.getDiagnostics().gridType)
    }
}
