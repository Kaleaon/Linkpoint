package com.linkpoint.network.grid

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linkpoint.core.GridInfo
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GridInfoResolverTest {

    @Test
    fun testSecondLifeUriDetection() {
        assertTrue(GridInfoResolver.isSecondLifeUri("https://login.agni.lindenlab.com/cgi-bin/login.cgi"))
        assertTrue(GridInfoResolver.isSecondLifeUri("https://login.aditi.lindenlab.com/cgi-bin/login.cgi"))
        assertTrue(GridInfoResolver.isSecondLifeUri("secondlife"))
        assertTrue(GridInfoResolver.isSecondLifeUri("secondlife_beta"))

        assertFalse(GridInfoResolver.isSecondLifeUri("http://login.osgrid.org:8002/"))
        assertFalse(GridInfoResolver.isSecondLifeUri("https://grid.kitely.com:8002/"))
    }

    @Test
    fun testUrlValidationGuardrail() {
        assertTrue(GridInfoResolver.isValidHttpUrl("http://login.osgrid.org:8002/"))
        assertTrue(GridInfoResolver.isValidHttpUrl("https://login.kitely.com/"))
        assertTrue(GridInfoResolver.isValidHttpUrl("http://map.osgrid.org/"))

        assertFalse(GridInfoResolver.isValidHttpUrl("javascript:alert(1)"))
        assertFalse(GridInfoResolver.isValidHttpUrl("ftp://files.grid.org/"))
        assertFalse(GridInfoResolver.isValidHttpUrl("invalid_url_format"))
        assertFalse(GridInfoResolver.isValidHttpUrl(null))
        assertFalse(GridInfoResolver.isValidHttpUrl(""))
    }

    @Test
    fun testParseXmlPayload() {
        val xmlPayload = """
            <?xml version="1.0" encoding="utf-8"?>
            <gridinfo>
                <gridname>OSGrid</gridname>
                <gridnick>osgrid</gridnick>
                <loginuri>http://login.osgrid.org:8002/</loginuri>
                <welcome>http://www.osgrid.org/welcome</welcome>
                <economy>http://economy.osgrid.org/</economy>
                <helperuri>http://helper.osgrid.org/</helperuri>
                <map>http://map.osgrid.org/</map>
            </gridinfo>
        """.trimIndent()

        val parsed = GridInfoResolver.parseGridInfoPayload(xmlPayload)

        assertEquals("OSGrid", parsed["gridname"])
        assertEquals("osgrid", parsed["gridnick"])
        assertEquals("http://login.osgrid.org:8002/", parsed["loginuri"])
        assertEquals("http://www.osgrid.org/welcome", parsed["welcome"])
        assertEquals("http://economy.osgrid.org/", parsed["economy"])
        assertEquals("http://helper.osgrid.org/", parsed["helperuri"])
        assertEquals("http://map.osgrid.org/", parsed["map"])
    }

    @Test
    fun testParseLlsdXmlPayload() {
        val llsdXmlPayload = """
            <map>
                <key>gridname</key><string>Kitely Grid</string>
                <key>gridnick</key><string>kitely</string>
                <key>loginuri</key><string>https://login.kitely.com/</string>
                <key>welcome</key><string>https://www.kitely.com/welcome</string>
                <key>economy</key><string>https://economy.kitely.com/</string>
                <key>helperuri</key><string>https://helper.kitely.com/</string>
                <key>map</key><string>https://map.kitely.com/</string>
            </map>
        """.trimIndent()

        val parsed = GridInfoResolver.parseGridInfoPayload(llsdXmlPayload)

        assertEquals("Kitely Grid", parsed["gridname"])
        assertEquals("kitely", parsed["gridnick"])
        assertEquals("https://login.kitely.com/", parsed["loginuri"])
        assertEquals("https://www.kitely.com/welcome", parsed["welcome"])
        assertEquals("https://economy.kitely.com/", parsed["economy"])
        assertEquals("https://helper.kitely.com/", parsed["helperuri"])
        assertEquals("https://map.kitely.com/", parsed["map"])
    }

    @Test
    fun testParseJsonPayload() {
        val jsonPayload = """
            {
                "gridname": "InWorldz",
                "gridnick": "inworldz",
                "loginuri": "http://login.inworldz.com:8002/",
                "welcome": "http://inworldz.com/welcome",
                "economy": "http://economy.inworldz.com/",
                "helperuri": "http://helper.inworldz.com/",
                "map": "http://map.inworldz.com/"
            }
        """.trimIndent()

        val parsed = GridInfoResolver.parseGridInfoPayload(jsonPayload)

        assertEquals("InWorldz", parsed["gridname"])
        assertEquals("inworldz", parsed["gridnick"])
        assertEquals("http://login.inworldz.com:8002/", parsed["loginuri"])
        assertEquals("http://inworldz.com/welcome", parsed["welcome"])
        assertEquals("http://economy.inworldz.com/", parsed["economy"])
        assertEquals("http://helper.inworldz.com/", parsed["helperuri"])
        assertEquals("http://map.inworldz.com/", parsed["map"])
    }

    @Test
    fun testBuildProbeUrls() {
        val urls = GridInfoResolver.buildProbeUrls("osgrid.org", null)
        assertTrue(urls.contains("http://osgrid.org/grid_info"))
        assertTrue(urls.contains("http://osgrid.org/grid_info.php"))

        val portUrls = GridInfoResolver.buildProbeUrls("grid.kitely.com:8002", null)
        assertTrue(portUrls.contains("http://grid.kitely.com:8002/grid_info"))
    }

    @Test
    fun testFallbackOnUnreachableHost() = runBlocking {
        val initial = GridInfo(
            id = "custom_test",
            name = "Test Grid",
            loginUri = "http://unreachable.test.invalid:8002/",
            gridNick = "test"
        )

        val startTime = System.currentTimeMillis()
        val result = GridInfoResolver.resolveGridInfo(
            inputAddress = "unreachable.test.invalid:8002",
            initialGrid = initial,
            timeoutMs = 1000L
        )
        val duration = System.currentTimeMillis() - startTime

        assertNotNull(result)
        assertEquals("http://unreachable.test.invalid:8002/", result.loginUri)
        assertFalse(result.isResolved)
        assertTrue("Duration ($duration ms) should respect timeout limit", duration < 2500L)
    }

    @Test
    fun testSecondLifeBypassFastPath() = runBlocking {
        val slGrid = GridInfo(
            id = "secondlife",
            name = "Second Life",
            loginUri = "https://login.agni.lindenlab.com/cgi-bin/login.cgi",
            gridNick = "agni"
        )

        val startTime = System.currentTimeMillis()
        val result = GridInfoResolver.resolveGridInfo(
            inputAddress = "https://login.agni.lindenlab.com/cgi-bin/login.cgi",
            initialGrid = slGrid,
            timeoutMs = 2500L
        )
        val duration = System.currentTimeMillis() - startTime

        assertEquals("https://login.agni.lindenlab.com/cgi-bin/login.cgi", result.loginUri)
        assertTrue("Second Life should bypass probe instantly without network delay", duration < 100L)
    }
}
