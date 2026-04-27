package com.linkpoint.voice

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.webrtc.IceCandidate
import java.util.concurrent.TimeUnit

internal data class SignalingIceCandidate(
    val sdpMid: String,
    val sdpMLineIndex: Int,
    val candidate: String
)

internal data class OfferResponsePayload(
    val answerSdp: String,
    val remoteCandidates: List<SignalingIceCandidate>,
    val pollUri: String?
)

internal interface VoiceSignalingTransport {
    suspend fun post(uri: String, credentials: String, body: String): String
    suspend fun poll(uri: String, credentials: String): String
}

internal class HttpVoiceSignalingTransport(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : VoiceSignalingTransport {
    override suspend fun post(uri: String, credentials: String, body: String): String = withContext(ioDispatcher) {
        val req = Request.Builder()
            .url(uri)
            .apply {
                if (credentials.isNotEmpty()) {
                    header("Authorization", "Bearer $credentials")
                }
            }
            .post(body.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()
        client.newCall(req).execute().use { resp ->
            val responseBody = resp.body?.string().orEmpty()
            if (!resp.isSuccessful) {
                throw IllegalStateException("HTTP ${resp.code}: ${responseBody.take(256)}")
            }
            responseBody
        }
    }

    override suspend fun poll(uri: String, credentials: String): String = withContext(ioDispatcher) {
        val req = Request.Builder()
            .url(uri)
            .apply {
                if (credentials.isNotEmpty()) {
                    header("Authorization", "Bearer $credentials")
                }
            }
            .get()
            .build()
        client.newCall(req).execute().use { resp ->
            val responseBody = resp.body?.string().orEmpty()
            if (!resp.isSuccessful) {
                throw IllegalStateException("HTTP ${resp.code}: ${responseBody.take(256)}")
            }
            responseBody
        }
    }
}

internal interface VoiceSignalingPayloadCodec {
    fun encodeOffer(offerSdp: String): String
    fun encodeLocalIce(candidate: IceCandidate): String
    fun decodeOfferResponse(responseBody: String): OfferResponsePayload
    fun decodeRemoteIce(responseBody: String): List<SignalingIceCandidate>
}

internal class JsonVoiceSignalingPayloadCodec : VoiceSignalingPayloadCodec {
    override fun encodeOffer(offerSdp: String): String = JSONObject().apply {
        put("jsep", JSONObject().apply {
            put("type", "offer")
            put("sdp", offerSdp)
        })
    }.toString()

    override fun encodeLocalIce(candidate: IceCandidate): String = JSONObject().apply {
        put("ice_candidate", JSONObject().apply {
            put("sdpMid", candidate.sdpMid ?: "")
            put("sdpMLineIndex", candidate.sdpMLineIndex)
            put("candidate", candidate.sdp)
        })
    }.toString()

    override fun decodeOfferResponse(responseBody: String): OfferResponsePayload {
        val json = JSONObject(responseBody)
        val answerSdp = json.optJSONObject("jsep")?.optString("sdp", "").orEmpty()
        if (answerSdp.isEmpty()) {
            throw IllegalArgumentException("Voice signaling response missing jsep.sdp")
        }
        return OfferResponsePayload(
            answerSdp = answerSdp,
            remoteCandidates = decodeCandidates(json),
            pollUri = json.optString("poll_uri").takeIf { !it.isNullOrBlank() }
        )
    }

    override fun decodeRemoteIce(responseBody: String): List<SignalingIceCandidate> {
        val json = JSONObject(responseBody)
        return decodeCandidates(json)
    }

    private fun decodeCandidates(json: JSONObject): List<SignalingIceCandidate> {
        val candidates = mutableListOf<SignalingIceCandidate>()
        val array = json.optJSONArray("ice_candidates") ?: return emptyList()
        for (i in 0 until array.length()) {
            val candidateObj = array.optJSONObject(i) ?: continue
            val sdpMid = candidateObj.optString("sdpMid")
            val candidate = candidateObj.optString("candidate")
            if (sdpMid.isNotEmpty() && candidate.isNotEmpty()) {
                candidates.add(
                    SignalingIceCandidate(
                        sdpMid = sdpMid,
                        sdpMLineIndex = candidateObj.optInt("sdpMLineIndex", 0),
                        candidate = candidate
                    )
                )
            }
        }
        return candidates
    }
}

internal class VoiceSignalingOrchestrator(
    private val transport: VoiceSignalingTransport,
    private val codec: VoiceSignalingPayloadCodec,
    private val pollIntervalMs: Long = 1_000L
) {
    suspend fun connect(
        session: VoiceSession,
        channelUri: String,
        credentials: String,
        localIceCandidates: SharedFlow<IceCandidate>
    ) {
        val offerSdp = session.createOffer()
        if (offerSdp.isEmpty()) {
            throw IllegalStateException("SDP offer was empty")
        }

        val offerResponse = transport.post(
            uri = channelUri,
            credentials = credentials,
            body = codec.encodeOffer(offerSdp)
        )
        val offerPayload = codec.decodeOfferResponse(offerResponse)
        if (!session.handleAnswer(offerPayload.answerSdp)) {
            throw IllegalStateException("setRemoteDescription failed for SDP answer")
        }
        ingestRemoteCandidates(session, offerPayload.remoteCandidates)

        coroutineScope {
            launchTrickle(this, channelUri, credentials, localIceCandidates)
            launchPolling(this, session, offerPayload.pollUri, credentials)
        }
    }

    private fun ingestRemoteCandidates(session: VoiceSession, candidates: List<SignalingIceCandidate>) {
        for (candidate in candidates) {
            session.addRemoteIceCandidate(candidate.sdpMid, candidate.sdpMLineIndex, candidate.candidate)
        }
    }

    private fun launchTrickle(
        scope: kotlinx.coroutines.CoroutineScope,
        channelUri: String,
        credentials: String,
        localIceCandidates: SharedFlow<IceCandidate>
    ) {
        scope.launch {
            localIceCandidates.collect { candidate ->
                try {
                    transport.post(
                        uri = channelUri,
                        credentials = credentials,
                        body = codec.encodeLocalIce(candidate)
                    )
                } catch (e: Exception) {
                    android.util.Log.w("VoiceSession", "ICE trickle POST failed: ${e.message}")
                }
            }
        }
    }

    private fun launchPolling(
        scope: kotlinx.coroutines.CoroutineScope,
        session: VoiceSession,
        pollUri: String?,
        credentials: String
    ) {
        if (pollUri.isNullOrEmpty()) return
        scope.launch {
            while (isActive) {
                try {
                    val pollResponse = transport.poll(pollUri, credentials)
                    ingestRemoteCandidates(session, codec.decodeRemoteIce(pollResponse))
                } catch (e: Exception) {
                    android.util.Log.w("VoiceSession", "Remote ICE poll failed: ${e.message}")
                }
                delay(pollIntervalMs)
            }
        }
    }
}
