package com.linkpoint.render.shadow

import kotlin.math.max
import kotlin.math.tan

/**
 * Evaluates Level-of-Detail (LOD) screen coverage rules to exclude small objects
 * from dynamic shadow passes.
 *
 * Requirement 4: Objects with screen bounding area below 2% are excluded from shadow passes.
 */
object ShadowLodCuller {

    /** Default screen bounding area percentage threshold below which objects are excluded from shadow passes (2%). */
    const val DEFAULT_MIN_SCREEN_AREA_PERCENT: Float = 2.0f

    /**
     * Calculates the estimated screen bounding area percentage (0.0% - 100.0%) of an object given its
     * 3D extents, camera distance, field of view, and viewport dimensions.
     */
    fun calculateScreenBoundingAreaPercent(
        extentsX: Float,
        extentsY: Float,
        extentsZ: Float,
        distanceToCamera: Float,
        fovDegrees: Float,
        viewportWidth: Int,
        viewportHeight: Int
    ): Float {
        if (distanceToCamera <= 0.01f || viewportWidth <= 0 || viewportHeight <= 0) {
            return 100.0f
        }

        // Bounding radius / max dimension of the object
        val maxExtent = max(extentsX, max(extentsY, extentsZ))
        val fovRad = Math.toRadians(fovDegrees.toDouble()).toFloat()
        val tanHalfFov = tan(fovRad / 2.0f)

        if (tanHalfFov <= 0.0001f) {
            return 100.0f
        }

        // Project height in pixels onto screen viewport
        val projectedHeightPx = (maxExtent / (2.0f * distanceToCamera * tanHalfFov)) * viewportHeight
        val aspect = viewportWidth.toFloat() / viewportHeight.toFloat()
        val projectedWidthPx = projectedHeightPx / max(0.1f, aspect)

        val totalViewportArea = (viewportWidth.toFloat() * viewportHeight.toFloat())
        val objectScreenArea = (projectedWidthPx * projectedHeightPx)

        return (objectScreenArea / totalViewportArea) * 100.0f
    }

    /**
     * Determines whether an object should cast shadows based on screen area coverage percentage.
     * Objects with screen bounding area below [minScreenAreaPercent] (2%) are excluded from shadow passes.
     */
    fun shouldCastShadow(
        extentsX: Float,
        extentsY: Float,
        extentsZ: Float,
        distanceToCamera: Float,
        fovDegrees: Float,
        viewportWidth: Int,
        viewportHeight: Int,
        minScreenAreaPercent: Float = DEFAULT_MIN_SCREEN_AREA_PERCENT
    ): Boolean {
        val screenAreaPercent = calculateScreenBoundingAreaPercent(
            extentsX, extentsY, extentsZ,
            distanceToCamera, fovDegrees,
            viewportWidth, viewportHeight
        )
        return screenAreaPercent >= minScreenAreaPercent
    }
}
