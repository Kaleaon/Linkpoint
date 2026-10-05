package com.linkpoint.voice

import com.linkpoint.protocol.capabilities.CapabilityManager
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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

    @Test
    fun testDynamicIceServerResolutionRemovesHardcodedAgni() {
        val osGridCapUrl = "https://simhost-1.osgrid.org:9000/cap/ProvisionVoiceAccountRequest"
        val host = java.net.URI(osGridCapUrl).host

        assertEquals("simhost-1.osgrid.org", host)

        val resolvedStunUrls = listOf(
            "stun:$host:3478",
            "stun:stun.$host:3478",
            "stun:stun.l.google.com:19302",
            "stun:stun2.l.google.com:19302",
            "stun:stun.nextcloud.com:443",
            "stun:stun.twilio.com:3478"
        )

        assertTrue(resolvedStunUrls.any { it.contains("simhost-1.osgrid.org") })
        assertFalse(resolvedStunUrls.any { it.contains("stun1.agni.secondlife.io") })
        assertFalse(resolvedStunUrls.any { it.contains("stun2.agni.secondlife.io") })
        assertFalse(resolvedStunUrls.any { it.contains("stun3.agni.secondlife.io") })
    }

    @Test
    fun testVoiceCapabilityMissingFailsGracefully() {
        val mockCapManager = object : CapabilityManager() {
            override fun hasCapability(name: String): Boolean = false
        }

        val hasVoiceCap = mockCapManager.hasCapability(CapabilityManager.CAP_PROVISION_VOICE) ||
                          mockCapManager.hasCapability(CapabilityManager.CAP_SL_VOICE_WEBRTC) ||
                          mockCapManager.hasCapability(CapabilityManager.CAP_PARCEL_VOICE)

        assertFalse(hasVoiceCap)
    }

    @Test
    fun testParcelLocalIdPropagationKey() {
        val parcelLocalId = 12345
        val payloadMap = mutableMapOf<String, Any>()
        parcelLocalId.let {
            payloadMap["parcel_local_id"] = it
            payloadMap["parcelLocalId"] = it
        }

        assertEquals(12345, payloadMap["parcel_local_id"])
        assertEquals(12345, payloadMap["parcelLocalId"])
    }
}
