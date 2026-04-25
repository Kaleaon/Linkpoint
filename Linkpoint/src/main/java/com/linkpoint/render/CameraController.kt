package com.linkpoint.render

import com.linkpoint.protocol.types.LLVector3
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * Manages the in-world camera: position, look-at, and mode (follow vs
 * mouselook). Lives between the user-input layer (gestures, joysticks)
 * and the Filament Camera entity owned by RenderManager. Stateless about
 * Filament — the controller computes the desired eye/target/up triple
 * each frame and the renderer feeds it into camera.lookAt().
 *
 * Modeled loosely after Lumiya's CameraParams:
 * - Quaternion-style yaw/pitch rotation around the avatar in FOLLOW.
 * - Pitch clamped to ±85° to avoid gimbal flip at the poles.
 * - Pinch-to-zoom adjusts follow distance.
 * - MOUSELOOK locks the camera to the avatar head and rotates the eye
 *   forward; movement input drives the avatar instead.
 */
class CameraController {

    enum class Mode { FOLLOW, MOUSELOOK }

    companion object {
        private const val MIN_PITCH_DEG = -85f
        private const val MAX_PITCH_DEG = 85f
        // Eye height offset from the avatar root for mouselook (head height).
        private const val MOUSELOOK_EYE_HEIGHT = 1.7f
        // Default vertical offset of the focus point above the avatar root in
        // follow mode (looking at the chest/face rather than the feet).
        private const val FOLLOW_TARGET_HEIGHT = 1.2f
        // Filament uses Z-up for SL coordinates (configured in
        // setupDefaultCamera/setCameraTransform), so the orbit plane is XY.
        private const val DEG_TO_RAD = PI.toFloat() / 180f
    }

    @Volatile
    var mode: Mode = Mode.FOLLOW
        private set

    // Follow-mode orbit state.
    @Volatile var yawDeg: Float = 0f
    @Volatile var pitchDeg: Float = -15f
    @Volatile var followDistance: Float = 5f

    // Limits exposed for the preferences screen.
    @Volatile var minFollowDistance: Float = 1.0f
    @Volatile var maxFollowDistance: Float = 30f

    // Sensitivity (deg per pixel) and inversion. Both adjustable from prefs.
    @Volatile var orbitSensitivity: Float = 0.25f
    @Volatile var invertY: Boolean = false

    // Latest agent state pushed by the protocol layer. Defaults place the
    // camera at the standard SL spawn so something sensible renders before
    // the first AgentMovementComplete arrives.
    @Volatile private var agentPosition: LLVector3 = LLVector3(128f, 128f, 22f)
    @Volatile private var agentYawDeg: Float = 0f

    fun setMode(newMode: Mode) {
        mode = newMode
    }

    fun toggleMode() {
        mode = if (mode == Mode.FOLLOW) Mode.MOUSELOOK else Mode.FOLLOW
    }

    /** Update the agent's position so the camera tracks the avatar. */
    fun setAgentPosition(position: LLVector3, yawDegrees: Float = agentYawDeg) {
        agentPosition = position
        agentYawDeg = yawDegrees
    }

    /**
     * Apply a drag gesture. dx is horizontal (yaw), dy is vertical (pitch),
     * both in pixels. The renderer converts pixels to degrees using
     * [orbitSensitivity]; pitch is clamped to ±85° to avoid pole flip.
     */
    fun applyOrbit(dxPixels: Float, dyPixels: Float) {
        yawDeg = (yawDeg - dxPixels * orbitSensitivity) % 360f
        val dy = (if (invertY) -dyPixels else dyPixels) * orbitSensitivity
        pitchDeg = (pitchDeg + dy).coerceIn(MIN_PITCH_DEG, MAX_PITCH_DEG)
    }

    /**
     * Apply a pinch zoom factor. ScaleGestureDetector reports a multiplicative
     * factor; >1 means pinch-out (zoom in / shorter follow distance), <1 means
     * pinch-in (zoom out / longer follow distance).
     */
    fun applyZoom(scaleFactor: Float) {
        if (scaleFactor <= 0f) return
        followDistance = (followDistance / scaleFactor)
            .coerceIn(minFollowDistance, maxFollowDistance)
    }

    /**
     * Compute the eye/target positions for the current frame. Eye is where
     * the camera sits, target is the look-at point. Both in SL world
     * coordinates (Z up, meters). Caller passes a 6-element FloatArray that
     * receives [eyeX, eyeY, eyeZ, targetX, targetY, targetZ].
     */
    fun computeView(out: FloatArray) {
        require(out.size >= 6) { "CameraController.computeView needs 6 floats" }
        val px = agentPosition.x
        val py = agentPosition.y
        val pz = agentPosition.z

        when (mode) {
            Mode.FOLLOW -> {
                val yawRad = yawDeg * DEG_TO_RAD
                val pitchRad = pitchDeg * DEG_TO_RAD
                val cp = cos(pitchRad)
                val sp = sin(pitchRad)
                // Spherical offset around the avatar; positive distance places
                // the camera behind the avatar facing forward.
                val ox = sin(yawRad) * cp * followDistance
                val oy = -cos(yawRad) * cp * followDistance
                val oz = sp * followDistance
                out[0] = px + ox
                out[1] = py + oy
                out[2] = pz + FOLLOW_TARGET_HEIGHT + oz
                out[3] = px
                out[4] = py
                out[5] = pz + FOLLOW_TARGET_HEIGHT
            }
            Mode.MOUSELOOK -> {
                val yawRad = (agentYawDeg + yawDeg) * DEG_TO_RAD
                val pitchRad = pitchDeg * DEG_TO_RAD
                val cp = cos(pitchRad)
                val sp = sin(pitchRad)
                val fx = sin(yawRad) * cp
                val fy = -cos(yawRad) * cp
                val fz = sp
                out[0] = px
                out[1] = py
                out[2] = pz + MOUSELOOK_EYE_HEIGHT
                out[3] = px + fx
                out[4] = py + fy
                out[5] = pz + MOUSELOOK_EYE_HEIGHT + fz
            }
        }
    }

    /** Reset orbit angles to the default (used when toggling modes). */
    fun reset() {
        yawDeg = 0f
        pitchDeg = -15f
        followDistance = max(minFollowDistance, min(maxFollowDistance, 5f))
    }
}
