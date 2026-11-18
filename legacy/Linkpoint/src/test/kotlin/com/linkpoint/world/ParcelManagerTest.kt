package com.linkpoint.world

import com.linkpoint.protocol.messages.AdditionalMessageParsers.ParcelInfoReplyData
import com.linkpoint.protocol.messages.UDPConnectionFixed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class ParcelManagerTest {

    private lateinit var udpConnection: UDPConnectionFixed
    private lateinit var parcelManager: ParcelManager

    @Before
    fun setUp() {
        udpConnection = UDPConnectionFixed()
        parcelManager = ParcelManager(udpConnection)
    }

    @Test
    fun `handleParcelInfoReply updates latestParcelInfo flow and cache`() {
        assertNull(parcelManager.latestParcelInfo.value)

        val parcelId = UUID.randomUUID()
        val ownerId = UUID.randomUUID()
        val reply = ParcelInfoReplyData(
            parcelID = parcelId,
            ownerID = ownerId,
            name = "Stitch Sanctuary",
            description = "A peaceful test estate",
            actualArea = 1024,
            billableArea = 1024,
            flags = ParcelManager.FLAG_ALLOW_FLY or ParcelManager.FLAG_ALLOW_SCRIPTS,
            globalX = 256128f,
            globalY = 256128f,
            globalZ = 25f,
            simName = "Sandbox Simulator",
            snapshotID = UUID.randomUUID(),
            dwell = 42f,
            salePrice = 500,
            auctionID = 0
        )

        parcelManager.handleParcelInfoReply(reply)

        assertEquals(reply, parcelManager.latestParcelInfo.value)
        val cached = parcelManager.getParcelInfo(parcelId)
        assertNotNull(cached)
        assertEquals("Stitch Sanctuary", cached?.name)
        assertEquals(1024, cached?.actualArea)
        assertEquals(500, cached?.salePrice)
    }

    @Test
    fun `requestParcelInfo handles string variants safely`() {
        // Blank string should fallback without throwing
        parcelManager.requestParcelInfo("")
        parcelManager.requestParcelInfo("   ")

        // Invalid UUID string should fallback to vector request without throwing
        parcelManager.requestParcelInfo("invalid-uuid-string")

        // Valid UUID string should call UUID request overload
        val validUuid = UUID.randomUUID().toString()
        parcelManager.requestParcelInfo(validUuid)
    }
}
