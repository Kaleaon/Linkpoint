package com.linkpoint.voice

import kotlinx.coroutines.suspendCancellableCoroutine
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Suspend-friendly wrappers around WebRTC's callback-based SDP API.
 *
 * The native [PeerConnection.createOffer], [createAnswer],
 * [setLocalDescription], and [setRemoteDescription] all take an
 * [SdpObserver] and signal completion asynchronously. Direct call sites
 * become unreadable quickly: the original [VoiceManager.createOffer]
 * acknowledged this with a `// In a real implementation, we'd use a
 * CompletableFuture or coroutine-based SDP observer` comment and just
 * returned an empty string. These wrappers make the same calls return
 * proper coroutine results, with cancellation support so a cancelled
 * scope won't leave a dangling SDP operation.
 */

/**
 * Create an SDP offer. Suspends until WebRTC's onCreateSuccess /
 * onCreateFailure callback fires. Resumes with [SessionDescription] on
 * success, throws [SdpException] on failure.
 */
suspend fun PeerConnection.createOfferSuspend(constraints: MediaConstraints): SessionDescription =
    suspendCancellableCoroutine { cont ->
        createOffer(object : SdpObserver {
            override fun onCreateSuccess(sdp: SessionDescription) { cont.resume(sdp) }
            override fun onCreateFailure(error: String) {
                cont.resumeWithException(SdpException("createOffer failed: $error"))
            }
            override fun onSetSuccess() {}
            override fun onSetFailure(error: String) {}
        }, constraints)
    }

/**
 * Create an SDP answer. Caller must have already invoked
 * [setRemoteDescriptionSuspend] with the remote offer.
 */
suspend fun PeerConnection.createAnswerSuspend(constraints: MediaConstraints): SessionDescription =
    suspendCancellableCoroutine { cont ->
        createAnswer(object : SdpObserver {
            override fun onCreateSuccess(sdp: SessionDescription) { cont.resume(sdp) }
            override fun onCreateFailure(error: String) {
                cont.resumeWithException(SdpException("createAnswer failed: $error"))
            }
            override fun onSetSuccess() {}
            override fun onSetFailure(error: String) {}
        }, constraints)
    }

/**
 * Apply the local SDP. Always called after [createOfferSuspend] or
 * [createAnswerSuspend].
 */
suspend fun PeerConnection.setLocalDescriptionSuspend(sdp: SessionDescription): Unit =
    suspendCancellableCoroutine { cont ->
        setLocalDescription(object : SdpObserver {
            override fun onCreateSuccess(sdp: SessionDescription) {}
            override fun onCreateFailure(error: String) {}
            override fun onSetSuccess() { cont.resume(Unit) }
            override fun onSetFailure(error: String) {
                cont.resumeWithException(SdpException("setLocalDescription failed: $error"))
            }
        }, sdp)
    }

/**
 * Apply the remote SDP. For an answerer this is the offer; for an
 * offerer this is the answer.
 */
suspend fun PeerConnection.setRemoteDescriptionSuspend(sdp: SessionDescription): Unit =
    suspendCancellableCoroutine { cont ->
        setRemoteDescription(object : SdpObserver {
            override fun onCreateSuccess(sdp: SessionDescription) {}
            override fun onCreateFailure(error: String) {}
            override fun onSetSuccess() { cont.resume(Unit) }
            override fun onSetFailure(error: String) {
                cont.resumeWithException(SdpException("setRemoteDescription failed: $error"))
            }
        }, sdp)
    }

/**
 * Bundle a remote ICE candidate from the signaling channel into the
 * peer connection. The wire format is the standard WebRTC trickle-ICE
 * triple `(sdpMid, sdpMLineIndex, candidate)`.
 */
fun PeerConnection.addRemoteIceCandidate(sdpMid: String, sdpMLineIndex: Int, candidate: String): Boolean =
    addIceCandidate(IceCandidate(sdpMid, sdpMLineIndex, candidate))

class SdpException(message: String) : RuntimeException(message)
