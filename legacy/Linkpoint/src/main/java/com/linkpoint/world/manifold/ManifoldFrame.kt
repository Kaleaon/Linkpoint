package com.linkpoint.world.manifold

import com.linkpoint.protocol.llsd.*

typealias LLSD = LLSDValue

/**
 * Parametric Manifold Frame metadata representing grid topography and coordinate mapping rules.
 */
enum class TopologyType {
    FLAT_2D,
    RINGWORLD_CYLINDER,
    SPHERE_3D,
    TORUS_2D_PERIODIC,
    STACKED_MANIFOLD;

    companion object {
        fun fromString(value: String?): TopologyType {
            if (value == null) return FLAT_2D
            return try {
                valueOf(value.uppercase().trim())
            } catch (e: Exception) {
                FLAT_2D
            }
        }
    }
}

/**
 * Metadata defining a parametric surface or volume manifold frame.
 */
data class ManifoldFrame(
    val frameId: String = DEFAULT_FRAME_ID,
    val topologyType: TopologyType = TopologyType.FLAT_2D,
    val radius: Double = 0.0,
    val curvature: Double = 0.0,
    val originX: Double = 0.0,
    val originY: Double = 0.0,
    val originZ: Double = 0.0,
    val sizeX: Double = DEFAULT_SIZE,
    val sizeY: Double = DEFAULT_SIZE
) {
    /**
     * Convert ManifoldFrame to LLSDMap for capability negotiation.
     */
    fun toLLSD(): LLSDMap {
        return LLSDMap().apply {
            this["manifold_frame_id"] = LLSDString(frameId)
            this["topology_type"] = LLSDString(topologyType.name)
            this["radius"] = LLSDReal(radius)
            this["curvature"] = LLSDReal(curvature)
            this["origin"] = LLSDArray().apply {
                add(LLSDReal(originX))
                add(LLSDReal(originY))
                add(LLSDReal(originZ))
            }
            this["size_x"] = LLSDReal(sizeX)
            this["size_y"] = LLSDReal(sizeY)
        }
    }

    companion object {
        const val DEFAULT_FRAME_ID = "flat-2d"
        const val DEFAULT_SIZE = 262144.0 // Standard 1024x1024 region grid extent (256m * 1024)

        val IDENTITY = ManifoldFrame(
            frameId = DEFAULT_FRAME_ID,
            topologyType = TopologyType.FLAT_2D,
            radius = 0.0,
            curvature = 0.0,
            originX = 0.0,
            originY = 0.0,
            originZ = 0.0,
            sizeX = DEFAULT_SIZE,
            sizeY = DEFAULT_SIZE
        )

        private fun extractNumber(value: LLSDValue?): Double? {
            return when (value) {
                is LLSDReal -> value.value
                is LLSDInteger -> value.value.toDouble()
                else -> null
            }
        }

        /**
         * Safely parse ManifoldFrame from LLSD map/value.
         * Falls back to IDENTITY flat 2D frame if LLSD is missing or malformed.
         */
        fun fromLLSD(llsd: LLSDValue?): ManifoldFrame {
            if (llsd !is LLSDMap) return IDENTITY

            val frameId = llsd.getString("manifold_frame_id") ?: llsd.getString("frame_id") ?: DEFAULT_FRAME_ID
            val topologyType = TopologyType.fromString(llsd.getString("topology_type"))
            val radius = llsd.getReal("radius") ?: llsd.getLong("radius")?.toDouble() ?: 0.0
            val curvature = llsd.getReal("curvature") ?: 0.0

            var originX = 0.0
            var originY = 0.0
            var originZ = 0.0

            when (val originLlsd = llsd["origin"]) {
                is LLSDArray -> {
                    if (originLlsd.size >= 1) originX = extractNumber(originLlsd[0]) ?: 0.0
                    if (originLlsd.size >= 2) originY = extractNumber(originLlsd[1]) ?: 0.0
                    if (originLlsd.size >= 3) originZ = extractNumber(originLlsd[2]) ?: 0.0
                }
                is LLSDMap -> {
                    originX = llsd.getReal("x") ?: 0.0
                    originY = llsd.getReal("y") ?: 0.0
                    originZ = llsd.getReal("z") ?: 0.0
                }
                else -> {
                    originX = llsd.getReal("origin_x") ?: 0.0
                    originY = llsd.getReal("origin_y") ?: 0.0
                    originZ = llsd.getReal("origin_z") ?: 0.0
                }
            }

            val sizeX = llsd.getReal("size_x") ?: llsd.getReal("circumference") ?: DEFAULT_SIZE
            val sizeY = llsd.getReal("size_y") ?: DEFAULT_SIZE

            return ManifoldFrame(
                frameId = frameId,
                topologyType = topologyType,
                radius = radius,
                curvature = curvature,
                originX = originX,
                originY = originY,
                originZ = originZ,
                sizeX = sizeX,
                sizeY = sizeY
            )
        }
    }
}
