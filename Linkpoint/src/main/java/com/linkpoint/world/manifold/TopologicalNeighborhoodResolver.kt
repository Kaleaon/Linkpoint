package com.linkpoint.world.manifold

import android.util.Log
import kotlin.math.floor

/**
 * Resolves neighbor region handles across flat and non-planar topographies
 * (ringworlds, cylinders, spheres, tori, stacked manifolds).
 */
object TopologicalNeighborhoodResolver {
    private const val TAG = "TopologicalResolver"
    const val REGION_SIZE = 256

    /**
     * Compute neighbor region coordinates and handle given current region handle,
     * local crossing position, and active manifold frame.
     *
     * @param currentHandle Active region handle
     * @param localX Region-local X position in meters (0 to REGION_SIZE)
     * @param localY Region-local Y position in meters (0 to REGION_SIZE)
     * @param frame Active parametric manifold frame
     * @return Resolved neighbor handle, or null if avatar remains inside current region boundaries.
     */
    fun getNeighborRegionHandle(
        currentHandle: Long,
        localX: Float,
        localY: Float,
        frame: ManifoldFrame = ManifoldFrame.IDENTITY,
        regionSizeX: Int = REGION_SIZE,
        regionSizeY: Int = REGION_SIZE
    ): Long? {
        val startTime = System.nanoTime()

        // Extract current global region coordinates (in meters)
        val currentRegionX = (currentHandle shr 40).toInt()
        val currentRegionY = ((currentHandle shr 8) and 0xFFFFFFFF).toInt()

        // Determine step displacement
        val dx = when {
            localX < 0f -> -regionSizeX
            localX >= regionSizeX -> regionSizeX
            else -> 0
        }

        val dy = when {
            localY < 0f -> -regionSizeY
            localY >= regionSizeY -> regionSizeY
            else -> 0
        }

        // Staying in current region
        if (dx == 0 && dy == 0) return null

        val baseTargetX = currentRegionX + dx
        val baseTargetY = currentRegionY + dy

        val (neighborX, neighborY) = resolveCoordinates(baseTargetX, baseTargetY, frame)

        val neighborHandle = (neighborX.toLong() shl 40) or (neighborY.toLong() shl 8)

        val elapsedUs = (System.nanoTime() - startTime) / 1000
        if (elapsedUs > 1000) {
            Log.w(TAG, "Neighborhood resolution exceeded 1ms threshold: ${elapsedUs}us")
        }

        return neighborHandle
    }

    /**
     * Resolve boundary wrap-around / surface mapping coordinates based on manifold frame topology.
     */
    fun resolveCoordinates(baseX: Int, baseY: Int, frame: ManifoldFrame): Pair<Int, Int> {
        if (frame.topologyType == TopologyType.FLAT_2D) {
            return Pair(baseX, baseY)
        }

        val gridWidth = maxOf(1, (frame.sizeX / REGION_SIZE).toInt())
        val gridHeight = maxOf(1, (frame.sizeY / REGION_SIZE).toInt())

        val gridX = baseX / REGION_SIZE
        val gridY = baseY / REGION_SIZE

        return when (frame.topologyType) {
            TopologyType.FLAT_2D -> Pair(baseX, baseY)

            TopologyType.RINGWORLD_CYLINDER -> {
                // X-axis periodic wrap-around (ringworld circumference)
                val wrappedGridX = positiveMod(gridX, gridWidth)
                Pair(wrappedGridX * REGION_SIZE, baseY)
            }

            TopologyType.TORUS_2D_PERIODIC -> {
                // Both X and Y periodic wrap-around
                val wrappedGridX = positiveMod(gridX, gridWidth)
                val wrappedGridY = positiveMod(gridY, gridHeight)
                Pair(wrappedGridX * REGION_SIZE, wrappedGridY * REGION_SIZE)
            }

            TopologyType.SPHERE_3D -> {
                // Spherical surface topology:
                // Latitude (Y) wrap across poles reflects longitude (X) by half-circumference
                var targetGridY = gridY
                var targetGridX = gridX

                if (targetGridY < 0) {
                    // Crossed South Pole
                    targetGridY = -targetGridY - 1
                    targetGridX += gridWidth / 2
                } else if (targetGridY >= gridHeight) {
                    // Crossed North Pole
                    targetGridY = 2 * gridHeight - targetGridY - 1
                    targetGridX += gridWidth / 2
                }

                targetGridX = positiveMod(targetGridX, gridWidth)
                Pair(targetGridX * REGION_SIZE, targetGridY * REGION_SIZE)
            }

            TopologyType.STACKED_MANIFOLD -> {
                // Stacked manifold bounds checking with origin offset
                val originGridX = (frame.originX / REGION_SIZE).toInt()
                val originGridY = (frame.originY / REGION_SIZE).toInt()

                val relX = gridX - originGridX
                val relY = gridY - originGridY

                val wrappedRelX = positiveMod(relX, gridWidth)
                val wrappedRelY = positiveMod(relY, gridHeight)

                Pair((originGridX + wrappedRelX) * REGION_SIZE, (originGridY + wrappedRelY) * REGION_SIZE)
            }
        }
    }

    private fun positiveMod(value: Int, mod: Int): Int {
        if (mod <= 0) return value
        val result = value % mod
        return if (result < 0) result + mod else result
    }
}
