package com.linkpoint.core

import androidx.test.core.app.ApplicationProvider
import com.linkpoint.protocol.messages.MessageParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

class RegionHandshakeSessionMappingTest {

    @Test
    fun `region handshake maps to canonical session and ui state without Unknown fallback`() {
        val payload = buildRegionHandshakePayload("New Region")
        val parsed = MessageParser.parseRegionHandshake(payload)
        assertNotNull(parsed)

        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val sessionManager = SessionManager(context)
        sessionManager.updateRegionInfo(regionHandle = 123L, x = 128, y = 128) // seeds "Unknown"
        sessionManager.updateFromRegionHandshake(
            simName = parsed!!.simName,
            regionId = parsed.regionId,
            regionFlags = parsed.regionFlags,
            regionFlagsExtended = parsed.regionFlagsExtended,
            simAccess = parsed.simAccess
        )

        val canonicalName = sessionManager.regionSessionState.value.simName
        val regionName = sessionManager.currentRegion.value?.name ?: "Unknown"
        val uiName = sessionManager.currentRegion.value?.name ?: "Unknown Region"

        assertFalse(canonicalName.isBlank())
        assertEquals(canonicalName, regionName)
        assertEquals(canonicalName, uiName)
        assertFalse(regionName == "Unknown")
        assertFalse(uiName == "Unknown Region")
    }

    private fun buildRegionHandshakePayload(simName: String): ByteArray {
        val simNameBytes = simName.toByteArray(Charsets.UTF_8)
        val buffer = ByteBuffer.allocate(1024).order(ByteOrder.LITTLE_ENDIAN)
        buffer.putInt(0xABCD) // RegionFlags
        buffer.put(13) // SimAccess
        buffer.put(simNameBytes.size.toByte())
        buffer.put(simNameBytes)
        buffer.put(UUID(0L, 1L).toBytes()) // SimOwner
        buffer.put(0) // IsEstateManager
        buffer.putFloat(20f)
        buffer.putFloat(1f)
        buffer.put(UUID(2L, 3L).toBytes()) // CacheID
        repeat(8) { buffer.put(UUID(it.toLong(), (it + 1).toLong()).toBytes()) }
        repeat(4) { buffer.putFloat(0f) }
        repeat(4) { buffer.putFloat(0f) }
        buffer.put(UUID(9L, 10L).toBytes()) // RegionID
        return buffer.array().copyOf(buffer.position())
    }

    private fun UUID.toBytes(): ByteArray {
        val bytes = ByteArray(16)
        val bb = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
        bb.putLong(this.mostSignificantBits)
        bb.putLong(this.leastSignificantBits)
        return bytes
    }
}
