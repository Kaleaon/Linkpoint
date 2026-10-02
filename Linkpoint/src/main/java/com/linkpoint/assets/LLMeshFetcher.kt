package com.linkpoint.assets

import android.util.Log
import com.linkpoint.protocol.llsd.LLSDInteger
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDReal
import com.linkpoint.protocol.types.LLVector3
import java.util.UUID

/**
 * Distance-gated Level of Detail (LOD) calculator and mesh request filter.
 *
 * Calculates projected screen pixel coverage for visible mesh objects based on object
 * bounding radius, camera field of view, viewport height, and camera-to-object distance.
 * Maps projected pixel footprint to appropriate Second Life mesh LOD levels (LOD0..LOD3)
 * or defers sub-pixel requests.
 */
class LLMeshFetcher {

    companion object {
        private const val TAG = "LLMeshFetcher"

        // Distance threshold beyond which avatars are gated to low-detail variants
        const val DISTANT_AVATAR_THRESHOLD_METERS = 50.0f

        // Projected pixel footprint threshold below which sub-pixel attachments are deferred
        const val SUBPIXEL_ATTACHMENT_THRESHOLD_PX = 8.0f

        // Projected pixel footprint threshold below which general scene objects are deferred
        const val SUBPIXEL_OBJECT_THRESHOLD_PX = 2.0f

        // Default screen pixel coverage thresholds for LOD selection
        const val DEFAULT_HIGH_LOD_THRESHOLD_PX = 200.0f   // LOD0 (HIGHEST)
        const val DEFAULT_MEDIUM_LOD_THRESHOLD_PX = 80.0f   // LOD1 (HIGH)
        const val DEFAULT_LOW_LOD_THRESHOLD_PX = 20.0f      // LOD2 (MEDIUM)
        const val DEFAULT_LOWEST_LOD_THRESHOLD_PX = 4.0f    // LOD3 (LOW)
    }

    /**
     * Camera parameters used for projection math.
     */
    data class CameraParams(
        val position: LLVector3 = LLVector3.zero(),
        val fovRad: Float = Math.toRadians(60.0).toFloat(), // Default 60 degree FOV
        val screenHeightPx: Int = 1080
    )

    /**
     * Thresholds for mapping screen pixel coverage to LOD levels.
     */
    data class LodThresholds(
        val highThreshold: Float = DEFAULT_HIGH_LOD_THRESHOLD_PX,
        val mediumThreshold: Float = DEFAULT_MEDIUM_LOD_THRESHOLD_PX,
        val lowThreshold: Float = DEFAULT_LOW_LOD_THRESHOLD_PX,
        val lowestThreshold: Float = DEFAULT_LOWEST_LOD_THRESHOLD_PX
    ) {
        companion object {
            /**
             * Extract LOD thresholds from LLSD mesh header if available.
             * Returns null if header data is missing or corrupted, triggering safe LOD0 fallback.
             */
            fun fromHeader(header: LLSDMap?): LodThresholds? {
                if (header == null) return null
                return try {
                    val anglesMap = header.getMap("lod_pixel_angles")
                        ?: header.getMap("lod_pixel_thresholds")

                    if (anglesMap != null) {
                        val high = readFloat(anglesMap, "high_lod")
                        val med = readFloat(anglesMap, "medium_lod")
                        val low = readFloat(anglesMap, "low_lod")
                        val lowest = readFloat(anglesMap, "lowest_lod")

                        if (high != null && med != null && low != null && lowest != null &&
                            high > 0f && med > 0f && low > 0f && lowest > 0f &&
                            high >= med && med >= low && low >= lowest
                        ) {
                            LodThresholds(
                                highThreshold = high,
                                mediumThreshold = med,
                                lowThreshold = low,
                                lowestThreshold = lowest
                            )
                        } else null
                    } else null
                } catch (e: Exception) {
                    try { Log.w(TAG, "Corrupted mesh header threshold data, using fallback: ${e.message}") } catch (_: Throwable) {}
                    null
                }
            }

            private fun readFloat(map: LLSDMap, key: String): Float? {
                val valObj = map.value[key]
                return when (valObj) {
                    is LLSDReal -> valObj.value.toFloat()
                    is LLSDInteger -> valObj.value.toFloat()
                    else -> null
                }
            }
        }
    }

    /**
     * Result of LOD calculation for a mesh request.
     */
    data class LodSelection(
        val targetLod: MeshLOD,
        val projectedPixelCoverage: Float,
        val distanceMeters: Float,
        val isDeferred: Boolean = false,
        val isDistantAvatarGated: Boolean = false,
        val reason: String = "Normal selection"
    )

    /**
     * Compute screen pixel footprint (coverage diameter) of an object's bounding sphere.
     *
     * Formula:
     * projectedPixels = (boundingRadius * screenHeightPx) / (distance * tan(fov / 2))
     */
    fun calculateProjectedPixelCoverage(
        boundingRadius: Float,
        distanceMeters: Float,
        fovRad: Float,
        screenHeightPx: Int
    ): Float {
        val safeRadius = if (boundingRadius <= 0f) 0.5f else boundingRadius
        val safeDistance = if (distanceMeters <= 0.001f) 0.001f else distanceMeters
        val safeFov = fovRad.coerceIn(0.01f, Math.PI.toFloat() - 0.01f)

        val tanHalfFov = kotlin.math.tan((safeFov / 2.0f).toDouble()).toFloat()
        val safeTan = if (tanHalfFov <= 0.0001f) 0.57735f else tanHalfFov

        return (safeRadius * screenHeightPx) / (safeDistance * safeTan)
    }

    /**
     * Calculate target LOD level and deferral status for a mesh object.
     *
     * @param meshId Asset UUID of the mesh
     * @param objectPos World position of object
     * @param boundingRadius Bounding sphere radius in meters
     * @param camera Active camera parameters
     * @param isAvatar True if object is an avatar or avatar attachment parent
     * @param isAttachment True if object is a sub-pixel attachment
     * @param header Optional LLSD mesh header for custom thresholds
     */
    fun selectLod(
        meshId: UUID,
        objectPos: LLVector3,
        boundingRadius: Float,
        camera: CameraParams,
        isAvatar: Boolean = false,
        isAttachment: Boolean = false,
        header: LLSDMap? = null
    ): LodSelection {
        val distance = camera.position.distance(objectPos)
        val projectedPixels = calculateProjectedPixelCoverage(
            boundingRadius = boundingRadius,
            distanceMeters = distance,
            fovRad = camera.fovRad,
            screenHeightPx = camera.screenHeightPx
        )

        // Sub-pixel attachment deferral check
        if (isAttachment && projectedPixels < SUBPIXEL_ATTACHMENT_THRESHOLD_PX) {
            return LodSelection(
                targetLod = MeshLOD.LOW,
                projectedPixelCoverage = projectedPixels,
                distanceMeters = distance,
                isDeferred = true,
                reason = "Deferred sub-pixel attachment (${String.format("%.1f", projectedPixels)}px < ${SUBPIXEL_ATTACHMENT_THRESHOLD_PX}px)"
            )
        }

        // Sub-pixel general object deferral check
        if (!isAvatar && projectedPixels < SUBPIXEL_OBJECT_THRESHOLD_PX) {
            return LodSelection(
                targetLod = MeshLOD.LOW,
                projectedPixelCoverage = projectedPixels,
                distanceMeters = distance,
                isDeferred = true,
                reason = "Deferred sub-pixel object (${String.format("%.1f", projectedPixels)}px < ${SUBPIXEL_OBJECT_THRESHOLD_PX}px)"
            )
        }

        // Try extracting custom thresholds from mesh header.
        // If header is missing, corrupted, or invalid, fall back to default thresholds.
        val thresholds = LodThresholds.fromHeader(header) ?: LodThresholds()

        var selectedLod = when {
            projectedPixels >= thresholds.highThreshold -> MeshLOD.HIGHEST
            projectedPixels >= thresholds.mediumThreshold -> MeshLOD.HIGH
            projectedPixels >= thresholds.lowThreshold -> MeshLOD.MEDIUM
            else -> MeshLOD.LOW
        }

        var isDistantAvatarGated = false
        var reason = "Screen pixel coverage selection (${String.format("%.1f", projectedPixels)}px)"

        // Distance gating for avatars beyond 50m: force low-detail variants
        if (isAvatar && distance > DISTANT_AVATAR_THRESHOLD_METERS) {
            if (selectedLod == MeshLOD.HIGHEST || selectedLod == MeshLOD.HIGH) {
                selectedLod = MeshLOD.MEDIUM
                isDistantAvatarGated = true
                reason = "Distant avatar gated beyond ${DISTANT_AVATAR_THRESHOLD_METERS}m (dist=${String.format("%.1f", distance)}m)"
            }
        }

        return LodSelection(
            targetLod = selectedLod,
            projectedPixelCoverage = projectedPixels,
            distanceMeters = distance,
            isDeferred = false,
            isDistantAvatarGated = isDistantAvatarGated,
            reason = reason
        )
    }

    /**
     * Check if camera movement warrants re-evaluating LOD selection for an active mesh object.
     */
    fun shouldReevaluate(
        previousDistance: Float,
        currentDistance: Float,
        distanceThresholdRatio: Float = 0.15f
    ): Boolean {
        if (previousDistance <= 0f) return true
        val delta = kotlin.math.abs(currentDistance - previousDistance)
        return (delta / previousDistance) >= distanceThresholdRatio
    }
}
