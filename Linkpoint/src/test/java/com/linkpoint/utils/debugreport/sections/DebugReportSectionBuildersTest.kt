package com.linkpoint.utils.debugreport.sections

import android.content.Context
import com.linkpoint.utils.debugreport.DebugReportContext
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock

class DebugReportSectionBuildersTest {
    private val context = DebugReportContext(mock(Context::class.java), null, System.currentTimeMillis())

    @Test
    fun connectionBuilder_handlesUnavailableApp() {
        val section = ConnectionSectionBuilder().build(context)
        assertTrue(section.contains("App instance not available"))
    }

    @Test
    fun networkBuilder_handlesUnavailableStateGracefully() {
        val section = NetworkSectionBuilder().build(context)
        assertTrue(section.contains("NETWORK ACTIVITY & PACKET STATUS"))
    }

    @Test
    fun cacheBuilder_handlesUnavailableStateGracefully() {
        val section = CacheSectionBuilder().build(context)
        assertTrue(section.contains("CACHE STATISTICS"))
    }
}
