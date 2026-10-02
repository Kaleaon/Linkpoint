package com.linkpoint.voice

import com.linkpoint.protocol.capabilities.CapabilityManager
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WebRtcVoiceCapabilityTest {

    @Test
    fun testSLVoiceWebRTCCapabilityConstantDefined() {
        assertEquals("SLVoiceWebRTC", CapabilityManager.CAP_SL_VOICE_WEBRTC)
        assertEquals("ProvisionVoiceAccountRequest", CapabilityManager.CAP_PROVISION_VOICE)
        assertEquals("VoiceSignalingRequest", CapabilityManager.CAP_VOICE_SIGNALING_REQUEST)
    }

    @Test
    fun testCapabilityNameAudit() {
        assertTrue(CapabilityManager.CAP_SL_VOICE_WEBRTC.isNotEmpty())
    }
}
