package com.linkpoint.voice

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.nio.ByteBuffer
import java.nio.ByteOrder

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MumbleVoiceAdapterTest {

    @Test
    fun testUriParsingFullMumbleUri() {
        val voiceInfo = VoiceInfo(
            channelUri = "mumble://testuser:secretpass@murmur.grid.org:64738/mainchannel",
            channelCredentials = "",
            voiceAccountServerUri = null,
            regionName = "TestRegion"
        )
        val adapter = MumbleVoiceAdapter(voiceInfo = voiceInfo)

        assertEquals("testuser", adapter.username)
        assertEquals("secretpass", adapter.password)
        assertEquals("murmur.grid.org", adapter.serverHost)
        assertEquals(64738, adapter.serverPort)
        assertEquals("mainchannel", adapter.channelName)
    }

    @Test
    fun testUriParsingMinimalAndAccountInfoFallback() {
        val voiceInfo = VoiceInfo(
            channelUri = "mumble://murmur.grid.org",
            channelCredentials = "",
            voiceAccountServerUri = null,
            regionName = "TestRegion"
        )
        val accountInfo = VoiceAccountInfo(
            username = "acc_user",
            password = "acc_password",
            voiceServerUri = "murmur.grid.org"
        )
        val adapter = MumbleVoiceAdapter(voiceInfo = voiceInfo, accountInfo = accountInfo)

        assertEquals("acc_user", adapter.username)
        assertEquals("acc_password", adapter.password)
        assertEquals("murmur.grid.org", adapter.serverHost)
        assertEquals(MumbleVoiceAdapter.DEFAULT_MURMUR_PORT, adapter.serverPort)
    }

    @Test
    fun testBuildMumbleUdpPositionalAudioPacketHeaderAndPositionalFloats() {
        val voiceInfo = VoiceInfo(
            channelUri = "mumble://murmur.grid.org:64738",
            channelCredentials = "",
            voiceAccountServerUri = null,
            regionName = "TestRegion"
        )
        val adapter = MumbleVoiceAdapter(voiceInfo = voiceInfo)

        val seq = 42L
        val opusData = byteArrayOf(0x01, 0x02, 0x03, 0x04)
        val x = 128.5f
        val y = 64.25f
        val z = 21.0f

        val packet = adapter.buildMumbleUdpPositionalAudioPacket(seq, opusData, x, y, z)

        assertNotNull(packet)
        assertTrue(packet.size > 13)
        // Check header byte (0x80)
        assertEquals(0x80.toByte(), packet[0])

        // Verify the 12 trailing bytes encoded as Little Endian IEEE-754 floats
        val posStart = packet.size - 12
        val bb = ByteBuffer.wrap(packet, posStart, 12).order(ByteOrder.LITTLE_ENDIAN)
        assertEquals(x, bb.float, 0.001f)
        assertEquals(y, bb.float, 0.001f)
        assertEquals(z, bb.float, 0.001f)
    }

    private class TestSpatialAudioBridge : SpatialAudioBridge() {
        var lastSourceX = 0f
        var lastSourceY = 0f
        var lastSourceZ = 0f
        var updateSourceCalled = false

        override fun updateSource(x: Float, y: Float, z: Float) {
            lastSourceX = x
            lastSourceY = y
            lastSourceZ = z
            updateSourceCalled = true
        }
    }

    @Test
    fun testProcessIncomingMumbleAudioPacketUpdatesSpatialAudioBridge() {
        val bridge = TestSpatialAudioBridge()
        val voiceInfo = VoiceInfo(
            channelUri = "mumble://murmur.grid.org:64738",
            channelCredentials = "",
            voiceAccountServerUri = null,
            regionName = "TestRegion"
        )
        val adapter = MumbleVoiceAdapter(
            voiceInfo = voiceInfo,
            spatialAudioBridge = bridge
        )

        // Construct a mock incoming audio packet with 12 trailing position floats (10.0f, 20.0f, 30.0f)
        val payload = ByteArray(20)
        payload[0] = 0x80.toByte() // header
        val posBuf = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN)
        posBuf.putFloat(10.0f)
        posBuf.putFloat(20.0f)
        posBuf.putFloat(30.0f)
        System.arraycopy(posBuf.array(), 0, payload, 8, 12)

        adapter.processIncomingMumbleAudioPacket(payload, payload.size)

        assertTrue(bridge.updateSourceCalled)
        assertEquals(10.0f, bridge.lastSourceX, 0.001f)
        assertEquals(20.0f, bridge.lastSourceY, 0.001f)
        assertEquals(30.0f, bridge.lastSourceZ, 0.001f)
    }

    @Test
    fun testVoiceSessionInterfaceDefaults() {
        val voiceInfo = VoiceInfo(
            channelUri = "mumble://murmur.grid.org:64738",
            channelCredentials = "",
            voiceAccountServerUri = null,
            regionName = "TestRegion"
        )
        val adapter = MumbleVoiceAdapter(voiceInfo = voiceInfo)

        assertTrue(adapter.updateIceServers(emptyList()))
        adapter.setOutputGain(1.5f)
        adapter.sendJoin(primary = true)
        assertEquals("mumble://murmur.grid.org:64738", adapter.channelUri)
    }
}
