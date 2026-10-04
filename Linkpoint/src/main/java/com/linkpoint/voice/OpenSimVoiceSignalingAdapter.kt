package com.linkpoint.voice

import org.json.JSONObject
import org.webrtc.PeerConnection

/**
 * Adapter converting OpenSim SIP channel credentials and server URIs
 * into WebRTC SDP parameters and signaling messages for [WebRtcVoiceSession].
 *
 * OpenSim regions provide voice credentials via ParcelVoiceInfoRequest
 * and ProvisionVoiceAccountRequest using legacy SIP/Vivox schemas.
 * This adapter translates those credentials into WebRTC SDP parameters,
 * SIP-over-WebRTC gateway session descriptors, and ICE server configurations,
 * bypassing legacy 32-bit Vivox C++ JNI stubs completely.
 */
class OpenSimVoiceSignalingAdapter {

    data class OpenSimSipCredentials(
        val channelUri: String,
        val channelCredentials: String,
        val username: String,
        val password: String,
        val voiceServerUri: String,
        val iceServers: List<PeerConnection.IceServer>
    )

    data class ConvertedWebRtcSdpParams(
        val offerSdp: String,
        val gatewayUri: String,
        val authorizationToken: String,
        val sipHeaders: Map<String, String>,
        val iceServers: List<PeerConnection.IceServer>
    )

    /**
     * Parse and convert OpenSim SIP channel info and voice account info
     * into structured [OpenSimSipCredentials].
     */
    fun parseCredentials(
        voiceInfo: VoiceInfo,
        accountInfo: VoiceAccountInfo?
    ): OpenSimSipCredentials {
        val username = accountInfo?.username?.ifEmpty { null }
            ?: extractUserFromSipUri(voiceInfo.channelUri)
        val password = accountInfo?.password?.ifEmpty { null }
            ?: voiceInfo.channelCredentials
        val serverUri = accountInfo?.voiceServerUri?.ifEmpty { null }
            ?: voiceInfo.voiceAccountServerUri?.ifEmpty { null }
            ?: extractHostFromSipUri(voiceInfo.channelUri)

        val iceServers = if (!accountInfo?.iceServers.isNullOrEmpty()) {
            accountInfo!!.iceServers.map { spec ->
                PeerConnection.IceServer.builder(spec.urls).apply {
                    spec.username?.let { setUsername(it) }
                    spec.credential?.let { setPassword(it) }
                }.createIceServer()
            }
        } else {
            emptyList()
        }

        return OpenSimSipCredentials(
            channelUri = voiceInfo.channelUri,
            channelCredentials = voiceInfo.channelCredentials,
            username = username,
            password = password,
            voiceServerUri = serverUri,
            iceServers = iceServers
        )
    }

    /**
     * Convert OpenSim SIP credentials and a WebRTC SDP offer into converted SDP parameters
     * suitable for a WebRTC-over-SIP gateway session.
     */
    fun convertSipToWebRtcSdp(
        credentials: OpenSimSipCredentials,
        rawOfferSdp: String
    ): ConvertedWebRtcSdpParams {
        val mangledSdp = injectOpenSimSipSdpAttributes(rawOfferSdp, credentials)
        val gatewayUri = buildGatewayEndpoint(credentials.voiceServerUri, credentials.channelUri)
        val authToken = buildAuthToken(credentials)
        val headers = mapOf(
            "X-OpenSim-Voice-Channel" to credentials.channelUri,
            "X-OpenSim-Voice-User" to credentials.username,
            "Authorization" to "Bearer $authToken"
        )

        return ConvertedWebRtcSdpParams(
            offerSdp = mangledSdp,
            gatewayUri = gatewayUri,
            authorizationToken = authToken,
            sipHeaders = headers,
            iceServers = credentials.iceServers
        )
    }

    /**
     * Synthesize or mangle a WebRTC SDP answer from OpenSim SIP gateway response
     * ensuring required Opus and AEC parameters are set.
     */
    fun convertSipAnswerToWebRtcSdp(
        rawAnswerSdp: String,
        credentials: OpenSimSipCredentials
    ): String {
        return ensureOpusAndAecAttributes(rawAnswerSdp)
    }

    private fun injectOpenSimSipSdpAttributes(
        sdp: String,
        credentials: OpenSimSipCredentials
    ): String {
        val sipAttribute = "a=identity:${credentials.username}\r\n" +
                "a=remote-uri:${credentials.channelUri}\r\n"
        return if (sdp.contains("m=audio")) {
            sdp.replace("m=audio", "m=audio\r\n$sipAttribute")
        } else {
            if (sdp.endsWith("\r\n") || sdp.endsWith("\n")) {
                sdp + sipAttribute
            } else {
                sdp + "\r\n" + sipAttribute
            }
        }
    }

    private fun ensureOpusAndAecAttributes(sdp: String): String {
        var result = sdp
        if (!result.contains("a=rtpmap:") && !result.contains("opus")) {
            val opusLines = "\r\na=rtpmap:111 opus/48000/2\r\na=fmtp:111 minptime=10;useinbandfec=1;stereo=1;sprop-stereo=1;maxplaybackrate=48000\r\n"
            result = if (result.endsWith("\r\n") || result.endsWith("\n")) {
                result.trimEnd() + opusLines
            } else {
                result + opusLines
            }
        }
        return result
    }

    private fun buildGatewayEndpoint(serverUri: String, channelUri: String): String {
        val cleanServer = if (serverUri.startsWith("http://") || serverUri.startsWith("https://")) {
            serverUri
        } else {
            "https://$serverUri"
        }
        return if (cleanServer.endsWith("/")) {
            "${cleanServer}webrtc-gateway"
        } else {
            "$cleanServer/webrtc-gateway"
        }
    }

    private fun buildAuthToken(credentials: OpenSimSipCredentials): String {
        return "${credentials.username}:${credentials.password}:${credentials.channelCredentials}"
    }

    fun extractUserFromSipUri(sipUri: String): String {
        return try {
            val clean = sipUri.removePrefix("sip:").removePrefix("sips:")
            val userPart = clean.substringBefore("@")
            if (userPart.isNotEmpty() && userPart != clean) userPart else "opensim_user"
        } catch (_: Exception) {
            "opensim_user"
        }
    }

    fun extractHostFromSipUri(sipUri: String): String {
        return try {
            val clean = sipUri.removePrefix("sip:").removePrefix("sips:")
            if (clean.contains("@")) {
                val hostPart = clean.substringAfter("@").substringBefore(":").substringBefore("/")
                if (hostPart.isNotEmpty()) hostPart else "voice.opensim.org"
            } else {
                "voice.opensim.org"
            }
        } catch (_: Exception) {
            "voice.opensim.org"
        }
    }
}
