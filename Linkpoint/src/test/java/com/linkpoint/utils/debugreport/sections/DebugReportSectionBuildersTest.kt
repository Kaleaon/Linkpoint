package com.linkpoint.utils.debugreport.sections

import android.test.mock.MockContext
import com.linkpoint.utils.debugreport.DebugReportContext
import org.junit.Assert.assertTrue
import org.junit.Test

class DebugReportSectionBuildersTest {
    private val context = DebugReportContext(MockContext(), null, System.currentTimeMillis())

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
