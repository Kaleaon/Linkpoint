package com.linkpoint.protocol.translation

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OpenSimTranslationTest {

    @Test
    fun `detectGridType identifies OpenSim URLs vs Agni URLs`() {
        val agniUrl = "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
        val openSimUrl = "http://hg.osgrid.org:8002/"

        assertEquals(LinkpointTranslationLayer.GridType.AGNI, LinkpointTranslationLayer.detectGridType(agniUrl))
        assertEquals(LinkpointTranslationLayer.GridType.OPENSIM, LinkpointTranslationLayer.detectGridType(openSimUrl))
    }

    @Test
    fun `configureForGrid sets OpenSim compatibility flags`() {
        LinkpointTranslationLayer.configureForGrid(LinkpointTranslationLayer.GridType.OPENSIM)
        assertFalse(LinkpointTranslationLayer.config.repairCapabilityUrls)
        assertTrue(LinkpointTranslationLayer.config.supportsVariableRegionSize)
        assertTrue(LinkpointTranslationLayer.config.supportsExtendedTerrain)

        val flags = LinkpointTranslationLayer.getCapabilityFlagsForGrid(LinkpointTranslationLayer.GridType.OPENSIM)
        assertEquals(true, flags["OpenSim"])
        assertEquals(true, flags["VariableRegionSize"])
        assertEquals(true, flags["ExtendedTerrain"])
    }
}
