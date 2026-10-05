package com.linkpoint.voice

import androidx.test.core.app.ApplicationProvider
import com.linkpoint.protocol.capabilities.CapabilityManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OpenSimVoiceAdapterTest {

    private val adapter = OpenSimVoiceSignalingAdapter()

    @Test
    fun testExtractUserFromSipUri() {
        assertEquals("conf-12345", adapter.extractUserFromSipUri("sip:conf-12345@voice.grid.org:5060"))
        assertEquals("user99", adapter.extractUserFromSipUri("sips:user99@opensim.example.com"))
        assertEquals("opensim_user", adapter.extractUserFromSipUri("invalid-uri"))
    }

    @Test
    fun testExtractHostFromSipUri() {
        assertEquals("voice.grid.org", adapter.extractHostFromSipUri("sip:conf-12345@voice.grid.org:5060"))
        assertEquals("opensim.example.com", adapter.extractHostFromSipUri("sips:user99@opensim.example.com"))
        assertEquals("voice.opensim.org", adapter.extractHostFromSipUri("invalid-uri"))
    }

    @Test
    fun testParseCredentialsWithFullAccount() {
        val voiceInfo = VoiceInfo(
            channelUri = "sip:conf-999@voice.opensim.org:5060",
            channelCredentials = "channel_secret_token",
            voiceAccountServerUri = "https://voice.opensim.org/api/",
            regionName = "OpenSim Sandbox"
        )
        val accountInfo = VoiceAccountInfo(
            username = "x_avatar_123",
            password = "account_secret_password",
            voiceServerUri = "https://voice.opensim.org/api/",
            iceServers = listOf(
                IceServerSpec(urls = listOf("stun:stun.opensim.org:3478"))
            )
        )

        val creds = adapter.parseCredentials(voiceInfo, accountInfo)

        assertEquals("sip:conf-999@voice.opensim.org:5060", creds.channelUri)
        assertEquals("channel_secret_token", creds.channelCredentials)
        assertEquals("x_avatar_123", creds.username)
        assertEquals("account_secret_password", creds.password)
        assertEquals("https://voice.opensim.org/api/", creds.voiceServerUri)
        assertEquals(1, creds.iceServers.size)
    }

    @Test
    fun testParseCredentialsFallbackWhenAccountInfoNull() {
        val voiceInfo = VoiceInfo(
            channelUri = "sip:channel_456@grid.opensim.org:5060",
            channelCredentials = "token_fallback",
            voiceAccountServerUri = null,
            regionName = "OpenSim Grid"
        )

        val creds = adapter.parseCredentials(voiceInfo, null)

        assertEquals("channel_456", creds.username)
        assertEquals("token_fallback", creds.password)
        assertEquals("grid.opensim.org", creds.voiceServerUri)
        assertTrue(creds.iceServers.isEmpty())
    }

    @Test
    fun testConvertSipToWebRtcSdp() {
        val voiceInfo = VoiceInfo(
            channelUri = "sip:conf-777@voice.opensim.org:5060",
            channelCredentials = "tok",
            voiceAccountServerUri = "voice.opensim.org",
            regionName = "Region A"
        )
        val creds = adapter.parseCredentials(voiceInfo, null)

        val rawOfferSdp = "v=0\r\no=- 12345 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\nm=audio 9 UDP/TLS/RTP/SAVPF 111\r\n"
        val converted = adapter.convertSipToWebRtcSdp(creds, rawOfferSdp)

        assertTrue(converted.offerSdp.contains("a=identity:conf-777"))
        assertTrue(converted.offerSdp.contains("a=remote-uri:sip:conf-777@voice.opensim.org:5060"))
        assertEquals("https://voice.opensim.org/webrtc-gateway", converted.gatewayUri)
        assertEquals("conf-777:tok:tok", converted.authorizationToken)
        assertEquals("Bearer conf-777:tok:tok", converted.sipHeaders["Authorization"])
        assertEquals("sip:conf-777@voice.opensim.org:5060", converted.sipHeaders["X-OpenSim-Voice-Channel"])
    }

    @Test
    fun testConvertSipAnswerToWebRtcSdp() {
        val voiceInfo = VoiceInfo(
            channelUri = "sip:conf-777@voice.opensim.org:5060",
            channelCredentials = "tok",
            voiceAccountServerUri = "voice.opensim.org",
            regionName = "Region A"
        )
        val creds = adapter.parseCredentials(voiceInfo, null)
        val rawAnswer = "v=0\r\no=- 54321 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\n"

        val convertedAnswer = adapter.convertSipAnswerToWebRtcSdp(rawAnswer, creds)

        assertTrue(convertedAnswer.contains("a=rtpmap:111 opus/48000/2"))
        assertTrue(convertedAnswer.contains("useinbandfec=1"))
    }

    @Test
    fun testVoiceCapabilitiesInCapabilityManager() {
        assertEquals("ParcelVoiceInfoRequest", CapabilityManager.CAP_PARCEL_VOICE)
        assertEquals("ProvisionVoiceAccountRequest", CapabilityManager.CAP_PROVISION_VOICE)
    }

    @Test
    fun testVoiceManagerJoinSpatialVoiceOpenSimRouting() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val capManager = CapabilityManager()
        val voiceManager = VoiceManager(context, capManager)

        // On an OpenSim region lacking SL "webrtc" voiceServerType, joinSpatialVoice routes to OpenSim voice adapter
        val result = voiceManager.joinSpatialVoice()
        // No parcel voice capability registered -> returns false safely
        assertFalse(result)
        // Verify native Vivox JNI stubs are bypassed and disabled
        assertFalse(VivoxVoiceEngine.isVivoxSupported())
    }
}
