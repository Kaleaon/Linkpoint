package com.linkpoint.voice

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SpatialAudioBridgeTest {

    @Test
    fun testSpatialAudioBridgePcmProcessingFallback() {
        val bridge = SpatialAudioBridge(sampleRate = 48000, maxRingBufferFrames = 1440)
        assertNotNull(bridge)

        val inputPcm = ShortArray(480) { (it % 1000).toShort() }
        val outputPcm = ShortArray(960)

        val processed = bridge.processPcmChunk(inputPcm, inputPcm.size, 1, outputPcm)
        assertTrue(processed > 0)

        bridge.updateListener(10f, 20f, 128f)
        bridge.updateSource(15f, 25f, 128f)

        val latency = bridge.latencyMs
        assertTrue(latency >= 0.0f)

        bridge.release()
    }

    @Test
    fun testVivoxEngineShutdownDelegation() {
        VivoxVoiceEngine.shutdown()
        val supported = VivoxVoiceEngine.isVivoxSupported()
        assertEquals(false, supported)
    }
}
