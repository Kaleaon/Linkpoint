package com.linkpoint.linden.llmath

import kotlin.math.*

class Camera : CoordFrame {

    companion object {
        const val DEFAULT_FIELD_OF_VIEW: Float = 60f * DEG_TO_RAD
        const val DEFAULT_ASPECT_RATIO: Float  = 640f / 480f
        const val DEFAULT_NEAR_PLANE: Float    = 0.25f
        const val DEFAULT_FAR_PLANE: Float     = 64f

        const val MAX_ASPECT_RATIO: Float  = 50.0f
        const val MAX_NEAR_PLANE: Float    = 1023.9f
        const val MAX_FAR_PLANE: Float     = 100000.0f
        const val MAX_FAR_CLIP: Float      = 512.0f

        const val MIN_ASPECT_RATIO: Float  = 0.02f
        const val MIN_NEAR_PLANE: Float    = 0.1f
        const val MIN_FAR_PLANE: Float     = 0.2f

        const val MIN_FIELD_OF_VIEW: Float = 5.0f * DEG_TO_RAD
        const val MAX_FIELD_OF_VIEW: Float = 175.0f * DEG_TO_RAD

        const val PLANE_MASK_NONE: Int = 0xff

        const val AGENT_PLANE_NO_USER_CLIP_NUM: Int = 6
        const val AGENT_PLANE_USER_CLIP_NUM: Int    = 7
        const val PLANE_MASK_NUM: Int               = 8
        const val AGENT_FRUSTUM_NUM: Int            = 8

        const val AGENT_PLANE_LEFT: Int      = 0
        const val AGENT_PLANE_RIGHT: Int     = 1
        const val AGENT_PLANE_NEAR: Int      = 2
        const val AGENT_PLANE_BOTTOM: Int    = 3
        const val AGENT_PLANE_TOP: Int       = 4
        const val AGENT_PLANE_FAR: Int       = 5
        const val AGENT_PLANE_USER_CLIP: Int = 6

        const val PLANE_LEFT: Int   = 0
        const val PLANE_RIGHT: Int  = 1
        const val PLANE_BOTTOM: Int = 2
        const val PLANE_TOP: Int    = 3

        const val HORIZ_PLANE_LEFT: Int  = 0
        const val HORIZ_PLANE_RIGHT: Int = 1
        const val HORIZ_PLANE_NUM: Int   = 2
    }

    private val agentPlanes     = Array(AGENT_PLANE_USER_CLIP_NUM) { Plane() }
    private val regionPlanes    = Array(AGENT_PLANE_USER_CLIP_NUM) { Plane() }
    private val lastAgentPlanes = Array(AGENT_PLANE_USER_CLIP_NUM) { Plane() }
    // planeMask[i]: bitmask of which octant sign the plane normal points into (0..7),
    // or PLANE_MASK_NONE (0xff) when the plane is disabled.
    private val planeMask = IntArray(PLANE_MASK_NUM) { PLANE_MASK_NONE }

    var view: Float = DEFAULT_FIELD_OF_VIEW
        private set
    var aspect: Float = DEFAULT_ASPECT_RATIO
        private set
    var viewHeightInPixels: Int = -1
        private set
    var near: Float = DEFAULT_NEAR_PLANE
        private set
    var far: Float = DEFAULT_FAR_PLANE
        private set
    var fixedDistance: Float = -1f

    private var frustCenter     = Vector3()
    private var frustRadiusSq   = 0f
    private var planeCount      = AGENT_PLANE_NO_USER_CLIP_NUM

    val agentFrustum        = Array(AGENT_FRUSTUM_NUM) { Vector3() }
    var frustumCornerDist: Float = 0f

    constructor() : super() {
        calculateFrustumPlanes()
    }

    constructor(
        verticalFovRads: Float,
        aspectRatio: Float,
        viewHeightInPixels: Int,
        nearPlane: Float,
        farPlane: Float
    ) : super() {
        this.viewHeightInPixels = viewHeightInPixels
        aspect = llClamp(aspectRatio, MIN_ASPECT_RATIO, MAX_ASPECT_RATIO)
        near   = llClamp(nearPlane,   MIN_NEAR_PLANE,   MAX_NEAR_PLANE)
        val fp = if (farPlane < 0f) DEFAULT_FAR_PLANE else farPlane
        far    = llClamp(fp, MIN_FAR_PLANE, MAX_FAR_PLANE)
        setView(verticalFovRads)
    }

    fun isChanged(): Boolean {
        var changed = false
        for (i in 0 until planeCount) {
            if (planeMask[i] != PLANE_MASK_NONE && !changed) {
                changed = !agentPlanes[i].equal(lastAgentPlanes[i])
            }
            lastAgentPlanes[i].set(agentPlanes[i])
        }
        return changed
    }

    fun getAgentPlane(idx: Int): Plane    = agentPlanes[idx]
    fun getUserClipPlane(): Plane          = agentPlanes[AGENT_PLANE_USER_CLIP]

    fun setUserClipPlane(plane: Plane) {
        planeCount = AGENT_PLANE_USER_CLIP_NUM
        agentPlanes[AGENT_PLANE_USER_CLIP].set(plane)
        planeMask[AGENT_PLANE_USER_CLIP] = calcPlaneMask(plane)
    }

    fun disableUserClipPlane() {
        planeCount = AGENT_PLANE_NO_USER_CLIP_NUM
    }

    fun setView(verticalFovRads: Float) {
        view = llClamp(verticalFovRads, MIN_FIELD_OF_VIEW, MAX_FIELD_OF_VIEW)
        calculateFrustumPlanes()
    }

    fun setViewHeightInPixels(height: Int) {
        viewHeightInPixels = height
        calculateFrustumPlanes()
    }

    fun setAspect(aspectRatio: Float) {
        aspect = llClamp(aspectRatio, MIN_ASPECT_RATIO, MAX_ASPECT_RATIO)
        calculateFrustumPlanes()
    }

    fun setNear(nearPlane: Float) {
        near = llClamp(nearPlane, MIN_NEAR_PLANE, MAX_NEAR_PLANE)
        calculateFrustumPlanes()
    }

    fun setFar(farPlane: Float) {
        far = llClamp(farPlane, MIN_FAR_PLANE, MAX_FAR_PLANE)
        calculateFrustumPlanes()
    }

    fun getMinView(): Float =
        if (aspect > 1f) MIN_FIELD_OF_VIEW else MIN_FIELD_OF_VIEW * (1f / aspect)

    fun getMaxView(): Float =
        if (aspect > 1f) MAX_FIELD_OF_VIEW / aspect else MAX_FIELD_OF_VIEW

    fun getYaw(): Float = atan2(xAxis.y, xAxis.x)
    fun getPitch(): Float {
        val xyLen = sqrt(xAxis.x * xAxis.x + xAxis.y * xAxis.y)
        return atan2(xAxis.z, xyLen)
    }

    fun sphereInFrustum(center: Vector3, radius: Float): Int {
        var res = false
        for (i in 0 until 6) {
            if (planeMask[i] != PLANE_MASK_NONE) {
                val d = agentPlanes[i].dist(center)
                if (d > radius) return 0
                res = res || (d > -radius)
            }
        }
        return if (res) 1 else 2
    }

    fun pointInFrustum(point: Vector3): Int = sphereInFrustum(point, 0f)

    fun sphereInFrustumFull(center: Vector3, radius: Float): Int = sphereInFrustum(center, radius)

    fun sphereInFrustumQuick(center: Vector3, radius: Float): Int {
        val dx = center.x - frustCenter.x
        val dy = center.y - frustCenter.y
        val dz = center.z - frustCenter.z
        val dsq = dx * dx + dy * dy + dz * dz
        val r = far * 0.5f + radius
        return if (dsq < r * r) 1 else 0
    }

    // Returns 0 if outside, 1 if intersecting, 2 if fully inside.
    // center and halfExtents are in the same space as the frustum planes.
    fun AABBInFrustum(center: Vector3, halfExtents: Vector3, planes: Array<Plane>? = null): Int {
        val p = planes ?: agentPlanes
        val maxPlanes = minOf(planeCount, AGENT_PLANE_USER_CLIP_NUM)
        var result = false
        for (i in 0 until maxPlanes) {
            val mask = planeMask[i]
            if (mask < PLANE_MASK_NUM) {
                val n = p[i].normal()
                val r = abs(n.x) * halfExtents.x + abs(n.y) * halfExtents.y + abs(n.z) * halfExtents.z
                val d = p[i].dist(center)
                if (d > r) return 0
                if (!result) result = (d > -r)
            }
        }
        return if (result) 1 else 2
    }

    fun AABBInRegionFrustum(center: Vector3, halfExtents: Vector3): Int =
        AABBInFrustum(center, halfExtents, regionPlanes)

    fun AABBInFrustumNoFarClip(center: Vector3, halfExtents: Vector3, planes: Array<Plane>? = null): Int {
        val p = planes ?: agentPlanes
        val maxPlanes = minOf(planeCount, AGENT_PLANE_USER_CLIP_NUM)
        var result = false
        for (i in 0 until maxPlanes) {
            if (i == AGENT_PLANE_FAR) continue
            val mask = planeMask[i]
            if (mask < PLANE_MASK_NUM) {
                val n = p[i].normal()
                val r = abs(n.x) * halfExtents.x + abs(n.y) * halfExtents.y + abs(n.z) * halfExtents.z
                val d = p[i].dist(center)
                if (d > r) return 0
                if (!result) result = (d > -r)
            }
        }
        return if (result) 1 else 2
    }

    fun AABBInRegionFrustumNoFarClip(center: Vector3, halfExtents: Vector3): Int =
        AABBInFrustumNoFarClip(center, halfExtents, regionPlanes)

    fun heightInPixels(center: Vector3, radius: Float): Float {
        if (radius == 0f) return 0f
        if (viewHeightInPixels <= -1) return -1f
        val dx = center.x - origin.x
        val dy = center.y - origin.y
        val dz = center.z - origin.z
        val dist = sqrt(dx * dx + dy * dy + dz * dz)
        val angle = 2f * atan2(radius, dist)
        return (angle / view) * viewHeightInPixels
    }

    fun ignoreAgentFrustumPlane(idx: Int) {
        if (idx < 0 || idx > planeCount) return
        planeMask[idx] = PLANE_MASK_NONE
        agentPlanes[idx].clear()
    }

    fun calcAgentFrustumPlanes(frust: Array<Vector3>) {
        for (i in 0 until AGENT_FRUSTUM_NUM) agentFrustum[i].set(frust[i])
        val diff = frust[5] - origin
        frustumCornerDist = diff.length()

        agentPlanes[AGENT_PLANE_NEAR]   = planeFromPoints(frust[0], frust[1], frust[2])
        agentPlanes[AGENT_PLANE_FAR]    = planeFromPoints(frust[5], frust[4], frust[6])
        agentPlanes[AGENT_PLANE_LEFT]   = planeFromPoints(frust[4], frust[0], frust[7])
        agentPlanes[AGENT_PLANE_RIGHT]  = planeFromPoints(frust[1], frust[5], frust[6])
        agentPlanes[AGENT_PLANE_TOP]    = planeFromPoints(frust[3], frust[2], frust[6])
        agentPlanes[AGENT_PLANE_BOTTOM] = planeFromPoints(frust[1], frust[0], frust[4])

        for (i in 0 until planeCount) {
            planeMask[i] = calcPlaneMask(agentPlanes[i])
        }
    }

    fun calcRegionFrustumPlanes(shift: Vector3, farClipDistance: Float) {
        val fn = agentPlanes[AGENT_PLANE_FAR].normal()
        val dd = fn * origin
        val farW = if (dd + agentPlanes[AGENT_PLANE_FAR][3] < 0f) {
            -farClipDistance - dd + fn * shift
        } else {
            farClipDistance - dd + fn * shift
        }

        for (i in 0 until 7) {
            if (planeMask[i] == PLANE_MASK_NONE) continue
            val n = agentPlanes[i].normal()
            val d = if (i != AGENT_PLANE_FAR) agentPlanes[i][3] + n * shift else farW
            regionPlanes[i] = Plane(n, d)
        }
    }

    protected fun calculateFrustumPlanes() {
        val top    = far * tan(0.5f * view)
        val bottom = -top
        val left   = top * aspect
        val right  = -left
        calculateFrustumPlanes(left, right, top, bottom)
    }

    protected fun calculateFrustumPlanes(left: Float, right: Float, top: Float, bottom: Float) {
        val halfFar = transformToAbsolute(Vector3(far * 0.5f, 0f, 0f))
        frustCenter = halfFar
        val r = far * 0.5f
        frustRadiusSq = r * r * 1.05f
    }

    protected fun calculateFrustumPlanesFromWindow(x1: Float, y1: Float, x2: Float, y2: Float) {
        val viewHeight = tan(0.5f * view) * far
        val viewWidth  = viewHeight * aspect
        calculateFrustumPlanes(
            x1 * -2f * viewWidth,
            x2 * -2f * viewWidth,
            y2 *  2f * viewHeight,
            y1 *  2f * viewHeight
        )
    }

    private fun planeFromPoints(p1: Vector3, p2: Vector3, p3: Vector3): Plane {
        val n = ((p2 - p1) % (p3 - p1)).also { it.normalize() }
        return Plane(p1, n)
    }

    // Compute a mask (0..7) encoding which octant the plane normal points into.
    // This is used as an index into the frustum scaler table for AABB tests.
    private fun calcPlaneMask(plane: Plane): Int {
        val n = plane.normal()
        var mask = 0
        if (n.x < 0f) mask = mask or 1
        if (n.y < 0f) mask = mask or 2
        if (n.z < 0f) mask = mask or 4
        return mask
    }

    override fun toString(): String = buildString {
        appendLine("{")
        appendLine("  Center = $origin")
        appendLine("  AtAxis = $xAxis")
        appendLine("  LeftAxis = $yAxis")
        appendLine("  UpAxis = $zAxis")
        appendLine("  View = $view")
        appendLine("  Aspect = $aspect")
        appendLine("  NearPlane = $near")
        appendLine("  FarPlane = $far")
        append("}")
    }
}
