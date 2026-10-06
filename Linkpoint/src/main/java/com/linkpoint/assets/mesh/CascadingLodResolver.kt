package com.linkpoint.assets.mesh

import com.linkpoint.assets.MeshLOD
import com.linkpoint.protocol.llsd.LLSDMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * Result of evaluating available header maps against requested LOD levels.
 *
 * @property lodMap Parsed LLSD map for the resolved LOD.
 * @property lodKey Header string key corresponding to [resolvedLod] (e.g. "medium_lod").
 * @property resolvedLod Actual MeshLOD selected.
 * @property cascadeDepth Step distance from requested MeshLOD (0 = exact requested tier available).
 */
data class LodResolutionResult(
    val lodMap: LLSDMap,
    val lodKey: String,
    val resolvedLod: MeshLOD,
    val cascadeDepth: Int
)

/**
 * Snapshot of LOD resolution cascade metrics.
 */
data class LodResolverTelemetry(
    val totalResolutions: Long,
    val fallbackCount: Long,
    val maxCascadeDepth: Int,
    val totalCascadeDepth: Long,
    val averageCascadeDepth: Double
)

/**
 * Encapsulates LOD fallback policies and cascading evaluation strategies.
 */
class CascadingLodResolver {

    private val totalResolutions = AtomicLong(0)
    private val fallbackCount = AtomicLong(0)
    private val maxCascadeDepth = AtomicInteger(0)
    private val totalCascadeDepth = AtomicLong(0)

    /**
     * Resolves the optimal available LOD map for [requestedLod] from [headerMap].
     * Evaluates missing-tier combinations with a cascading fallback sequence.
     *
     * @param headerMap Parsed LLSD mesh header.
     * @param requestedLod Desired LOD tier.
     * @return [LodResolutionResult] or null if no valid LOD map exists in the header.
     */
    fun resolveLodMap(headerMap: LLSDMap, requestedLod: MeshLOD): LodResolutionResult? {
        val candidateSequence = getLodCandidates(requestedLod)
        for ((depth, candidateLod) in candidateSequence.withIndex()) {
            val key = lodKeyFor(candidateLod)
            val lodMap = headerMap.getMap(key)
            if (lodMap != null && isValidLodMap(lodMap)) {
                recordTelemetry(depth)
                return LodResolutionResult(
                    lodMap = lodMap,
                    lodKey = key,
                    resolvedLod = candidateLod,
                    cascadeDepth = depth
                )
            }
        }
        return null
    }

    /**
     * Returns true if [map] contains non-negative offset and positive size integers.
     */
    private fun isValidLodMap(map: LLSDMap): Boolean {
        val offset = map.getInt("offset")
        val size = map.getInt("size")
        return offset != null && size != null && offset >= 0 && size > 0
    }

    /**
     * Maps [lod] to its LLSD header key name.
     */
    fun lodKeyFor(lod: MeshLOD): String = when (lod) {
        MeshLOD.HIGHEST -> "high_lod"
        MeshLOD.HIGH -> "medium_lod"
        MeshLOD.MEDIUM -> "low_lod"
        MeshLOD.LOW -> "lowest_lod"
    }

    companion object {
        private val HIGHEST_CANDIDATES = listOf(MeshLOD.HIGHEST, MeshLOD.HIGH, MeshLOD.MEDIUM, MeshLOD.LOW)
        private val HIGH_CANDIDATES = listOf(MeshLOD.HIGH, MeshLOD.HIGHEST, MeshLOD.MEDIUM, MeshLOD.LOW)
        private val MEDIUM_CANDIDATES = listOf(MeshLOD.MEDIUM, MeshLOD.HIGH, MeshLOD.LOW, MeshLOD.HIGHEST)
        private val LOW_CANDIDATES = listOf(MeshLOD.LOW, MeshLOD.MEDIUM, MeshLOD.HIGH, MeshLOD.HIGHEST)
    }

    /**
     * Preferred fallback search sequence for a given requested LOD tier.
     */
    private fun getLodCandidates(requested: MeshLOD): List<MeshLOD> = when (requested) {
        MeshLOD.HIGHEST -> HIGHEST_CANDIDATES
        MeshLOD.HIGH -> HIGH_CANDIDATES
        MeshLOD.MEDIUM -> MEDIUM_CANDIDATES
        MeshLOD.LOW -> LOW_CANDIDATES
    }

    private fun recordTelemetry(depth: Int) {
        totalResolutions.incrementAndGet()
        totalCascadeDepth.addAndGet(depth.toLong())
        if (depth > 0) {
            fallbackCount.incrementAndGet()
        }
        while (true) {
            val currentMax = maxCascadeDepth.get()
            if (depth <= currentMax || maxCascadeDepth.compareAndSet(currentMax, depth)) {
                break
            }
        }
    }

    /**
     * Returns a snapshot of LOD resolution cascade metrics.
     */
    fun getTelemetry(): LodResolverTelemetry {
        val total = totalResolutions.get()
        val totalDepth = totalCascadeDepth.get()
        val avgDepth = if (total > 0) totalDepth.toDouble() / total.toDouble() else 0.0
        return LodResolverTelemetry(
            totalResolutions = total,
            fallbackCount = fallbackCount.get(),
            maxCascadeDepth = maxCascadeDepth.get(),
            totalCascadeDepth = totalDepth,
            averageCascadeDepth = avgDepth
        )
    }
}
