package com.linkpoint.world.topography

import com.linkpoint.render.lumiya.spatial.SpatialIndex
import kotlin.math.*

/**
 * Topography manifold types supported by the projection strategy interface.
 */
enum class TopographyType {
    PLANAR,
    RINGWORLD,
    SPHERICAL
}

/**
 * World topography projection strategy interface.
 * Abstracting topography calculations allows non-planar manifolds (such as ringworlds and spheres)
 * to coexist with standard OpenSim grid protocols and rendering engines.
 */
interface WorldTopographyProjection {
    val topographyType: TopographyType

    /**
     * Map local manifold position to standard Cartesian grid coordinates.
     */
    fun projectToCartesian(localX: Float, localY: Float, localZ: Float): FloatArray

    /**
     * Map standard Cartesian grid coordinates to local manifold position.
     */
    fun projectFromCartesian(cartesianX: Float, cartesianY: Float, cartesianZ: Float): FloatArray

    /**
     * Calculate surface unit normal vector pointing "up" relative to the local manifold floor.
     */
    fun getSurfaceNormal(localX: Float, localY: Float, localZ: Float): FloatArray

    /**
     * Calculate gravity acceleration vector along the local manifold normal.
     */
    fun getGravityVector(localX: Float, localY: Float, localZ: Float, gravityMagnitude: Float = 9.8f): FloatArray

    /**
     * Adapt bounding box volume bounds based on projected surface coordinates.
     */
    fun getProjectedBounds(
        minX: Float, minY: Float, minZ: Float,
        maxX: Float, maxY: Float, maxZ: Float
    ): SpatialIndex.EntryBounds

    /**
     * Query topography map to compute neighbor region handles across non-linear manifold boundaries.
     * Returns null if staying within the current region.
     */
    fun getNeighborRegionHandle(currentHandle: Long, localX: Float, localY: Float, regionSize: Int = 256): Long?
}

/**
 * Standard flat planar Cartesian projection (default fallback with zero overhead).
 */
class PlanarTopographyProjection : WorldTopographyProjection {
    override val topographyType: TopographyType = TopographyType.PLANAR

    override fun projectToCartesian(localX: Float, localY: Float, localZ: Float): FloatArray {
        return floatArrayOf(localX, localY, localZ)
    }

    override fun projectFromCartesian(cartesianX: Float, cartesianY: Float, cartesianZ: Float): FloatArray {
        return floatArrayOf(cartesianX, cartesianY, cartesianZ)
    }

    override fun getSurfaceNormal(localX: Float, localY: Float, localZ: Float): FloatArray {
        return floatArrayOf(0f, 0f, 1f)
    }

    override fun getGravityVector(localX: Float, localY: Float, localZ: Float, gravityMagnitude: Float): FloatArray {
        return floatArrayOf(0f, 0f, -gravityMagnitude)
    }

    override fun getProjectedBounds(
        minX: Float, minY: Float, minZ: Float,
        maxX: Float, maxY: Float, maxZ: Float
    ): SpatialIndex.EntryBounds {
        return SpatialIndex.EntryBounds(minX, minY, minZ, maxX, maxY, maxZ)
    }

    override fun getNeighborRegionHandle(currentHandle: Long, localX: Float, localY: Float, regionSize: Int): Long? {
        val currentRegionX = (currentHandle shr 40).toInt()
        val currentRegionY = ((currentHandle shr 8) and 0xFFFFFFFFL).toInt()

        val neighborX = when {
            localX < 0f -> currentRegionX - regionSize
            localX >= regionSize -> currentRegionX + regionSize
            else -> currentRegionX
        }
        val neighborY = when {
            localY < 0f -> currentRegionY - regionSize
            localY >= regionSize -> currentRegionY + regionSize
            else -> currentRegionY
        }

        if (neighborX == currentRegionX && neighborY == currentRegionY) {
            return null
        }

        return (neighborX.toLong() shl 40) or (neighborY.toLong() shl 8)
    }
}

/**
 * Ringworld cylindrical interior surface projection.
 * Local X is distance along ring circumference (wrapping smoothly).
 * Local Y is along cylinder width.
 * Local Z is altitude above inner surface.
 */
class RingworldTopographyProjection(
    val radius: Float = 1000f,
    val width: Float = 256f
) : WorldTopographyProjection {

    override val topographyType: TopographyType = TopographyType.RINGWORLD

    private val circumference: Float = (2.0 * PI * radius).toFloat()

    override fun projectToCartesian(localX: Float, localY: Float, localZ: Float): FloatArray {
        val theta = localX / radius
        val r = radius - localZ
        val x = r * sin(theta)
        val y = localY
        val z = radius - r * cos(theta)
        return floatArrayOf(x, y, z)
    }

    override fun projectFromCartesian(cartesianX: Float, cartesianY: Float, cartesianZ: Float): FloatArray {
        val localY = cartesianY
        val dx = cartesianX
        val dz = radius - cartesianZ
        val r = sqrt(dx * dx + dz * dz)
        val localZ = radius - r
        var theta = atan2(dx, dz)
        if (theta < 0f) theta += (2.0 * PI).toFloat()
        val localX = theta * radius
        return floatArrayOf(localX, localY, localZ)
    }

    override fun getSurfaceNormal(localX: Float, localY: Float, localZ: Float): FloatArray {
        val theta = localX / radius
        // Pointing inward toward center / upward from inner surface
        val nx = -sin(theta)
        val ny = 0f
        val nz = cos(theta)
        return floatArrayOf(nx, ny, nz)
    }

    override fun getGravityVector(localX: Float, localY: Float, localZ: Float, gravityMagnitude: Float): FloatArray {
        val theta = localX / radius
        // Downward centrifugal force pointing outward toward floor
        val gx = sin(theta) * gravityMagnitude
        val gy = 0f
        val gz = -cos(theta) * gravityMagnitude
        return floatArrayOf(gx, gy, gz)
    }

    override fun getProjectedBounds(
        minX: Float, minY: Float, minZ: Float,
        maxX: Float, maxY: Float, maxZ: Float
    ): SpatialIndex.EntryBounds {
        val corners = arrayOf(
            projectToCartesian(minX, minY, minZ),
            projectToCartesian(minX, minY, maxZ),
            projectToCartesian(minX, maxY, minZ),
            projectToCartesian(minX, maxY, maxZ),
            projectToCartesian(maxX, minY, minZ),
            projectToCartesian(maxX, minY, maxZ),
            projectToCartesian(maxX, maxY, minZ),
            projectToCartesian(maxX, maxY, maxZ)
        )

        var pMinX = Float.MAX_VALUE
        var pMinY = Float.MAX_VALUE
        var pMinZ = Float.MAX_VALUE
        var pMaxX = -Float.MAX_VALUE
        var pMaxY = -Float.MAX_VALUE
        var pMaxZ = -Float.MAX_VALUE

        for (pt in corners) {
            pMinX = minOf(pMinX, pt[0])
            pMinY = minOf(pMinY, pt[1])
            pMinZ = minOf(pMinZ, pt[2])
            pMaxX = maxOf(pMaxX, pt[0])
            pMaxY = maxOf(pMaxY, pt[1])
            pMaxZ = maxOf(pMaxZ, pt[2])
        }

        return SpatialIndex.EntryBounds(pMinX, pMinY, pMinZ, pMaxX, pMaxY, pMaxZ)
    }

    override fun getNeighborRegionHandle(currentHandle: Long, localX: Float, localY: Float, regionSize: Int): Long? {
        val totalRegionsX = maxOf(1, (circumference / regionSize).roundToInt())
        val worldWidthX = totalRegionsX * regionSize

        val currentRegionX = (currentHandle shr 40).toInt()
        val currentRegionY = ((currentHandle shr 8) and 0xFFFFFFFFL).toInt()

        val offsetX = when {
            localX < 0f -> -regionSize
            localX >= regionSize -> regionSize
            else -> 0
        }
        val offsetY = when {
            localY < 0f -> -regionSize
            localY >= regionSize -> regionSize
            else -> 0
        }

        if (offsetX == 0 && offsetY == 0) return null

        var targetRegionX = currentRegionX + offsetX
        // Wrap X around ringworld circumference manifold
        targetRegionX = ((targetRegionX % worldWidthX) + worldWidthX) % worldWidthX
        val targetRegionY = currentRegionY + offsetY

        return (targetRegionX.toLong() shl 40) or (targetRegionY.toLong() shl 8)
    }
}

/**
 * Spherical planet manifold projection.
 * Local X is longitude arc distance along equator.
 * Local Y is latitude arc distance.
 * Local Z is altitude above surface.
 */
class SphericalTopographyProjection(
    val radius: Float = 1000f
) : WorldTopographyProjection {

    override val topographyType: TopographyType = TopographyType.SPHERICAL

    private val circumference: Float = (2.0 * PI * radius).toFloat()

    override fun projectToCartesian(localX: Float, localY: Float, localZ: Float): FloatArray {
        val lambda = localX / radius
        val phi = localY / radius
        val r = radius + localZ

        val x = r * cos(phi) * sin(lambda)
        val y = r * sin(phi)
        val z = r * cos(phi) * cos(lambda) - radius

        return floatArrayOf(x, y, z)
    }

    override fun projectFromCartesian(cartesianX: Float, cartesianY: Float, cartesianZ: Float): FloatArray {
        val dz = cartesianZ + radius
        val r = sqrt(cartesianX * cartesianX + cartesianY * cartesianY + dz * dz)
        val localZ = r - radius
        val phi = asin((cartesianY / r).coerceIn(-1f, 1f))
        val lambda = atan2(cartesianX, dz)

        val localX = lambda * radius
        val localY = phi * radius

        return floatArrayOf(localX, localY, localZ)
    }

    override fun getSurfaceNormal(localX: Float, localY: Float, localZ: Float): FloatArray {
        val lambda = localX / radius
        val phi = localY / radius

        val nx = cos(phi) * sin(lambda)
        val ny = sin(phi)
        val nz = cos(phi) * cos(lambda)

        return floatArrayOf(nx, ny, nz)
    }

    override fun getGravityVector(localX: Float, localY: Float, localZ: Float, gravityMagnitude: Float): FloatArray {
        val normal = getSurfaceNormal(localX, localY, localZ)
        return floatArrayOf(
            -normal[0] * gravityMagnitude,
            -normal[1] * gravityMagnitude,
            -normal[2] * gravityMagnitude
        )
    }

    override fun getProjectedBounds(
        minX: Float, minY: Float, minZ: Float,
        maxX: Float, maxY: Float, maxZ: Float
    ): SpatialIndex.EntryBounds {
        val corners = arrayOf(
            projectToCartesian(minX, minY, minZ),
            projectToCartesian(minX, minY, maxZ),
            projectToCartesian(minX, maxY, minZ),
            projectToCartesian(minX, maxY, maxZ),
            projectToCartesian(maxX, minY, minZ),
            projectToCartesian(maxX, minY, maxZ),
            projectToCartesian(maxX, maxY, minZ),
            projectToCartesian(maxX, maxY, maxZ)
        )

        var pMinX = Float.MAX_VALUE
        var pMinY = Float.MAX_VALUE
        var pMinZ = Float.MAX_VALUE
        var pMaxX = -Float.MAX_VALUE
        var pMaxY = -Float.MAX_VALUE
        var pMaxZ = -Float.MAX_VALUE

        for (pt in corners) {
            pMinX = minOf(pMinX, pt[0])
            pMinY = minOf(pMinY, pt[1])
            pMinZ = minOf(pMinZ, pt[2])
            pMaxX = maxOf(pMaxX, pt[0])
            pMaxY = maxOf(pMaxY, pt[1])
            pMaxZ = maxOf(pMaxZ, pt[2])
        }

        return SpatialIndex.EntryBounds(pMinX, pMinY, pMinZ, pMaxX, pMaxY, pMaxZ)
    }

    override fun getNeighborRegionHandle(currentHandle: Long, localX: Float, localY: Float, regionSize: Int): Long? {
        val totalRegionsX = maxOf(1, (circumference / regionSize).roundToInt())
        val worldWidthX = totalRegionsX * regionSize

        val currentRegionX = (currentHandle shr 40).toInt()
        val currentRegionY = ((currentHandle shr 8) and 0xFFFFFFFFL).toInt()

        val offsetX = when {
            localX < 0f -> -regionSize
            localX >= regionSize -> regionSize
            else -> 0
        }
        val offsetY = when {
            localY < 0f -> -regionSize
            localY >= regionSize -> regionSize
            else -> 0
        }

        if (offsetX == 0 && offsetY == 0) return null

        var targetX = currentRegionX + offsetX
        targetX = ((targetX % worldWidthX) + worldWidthX) % worldWidthX

        var targetY = currentRegionY + offsetY
        targetY = targetY.coerceIn(-worldWidthX / 2, worldWidthX / 2)

        return (targetX.toLong() shl 40) or (targetY.toLong() shl 8)
    }
}

/**
 * Helper for network boundary serialization. Converts local manifold coordinates to standard Cartesian
 * grid protocol packets and vice versa to maintain full OpenSim network protocol compatibility.
 */
object TopographyNetworkSerializer {

    /**
     * Converts local manifold position to Cartesian protocol packet array.
     */
    fun toCartesianProtocolPacket(
        localX: Float, localY: Float, localZ: Float,
        topography: WorldTopographyProjection
    ): FloatArray {
        return topography.projectToCartesian(localX, localY, localZ)
    }

    /**
     * Converts Cartesian protocol packet array to local manifold position.
     */
    fun fromCartesianProtocolPacket(
        cartesianX: Float, cartesianY: Float, cartesianZ: Float,
        topography: WorldTopographyProjection
    ): FloatArray {
        return topography.projectFromCartesian(cartesianX, cartesianY, cartesianZ)
    }
}
