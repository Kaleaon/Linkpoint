package com.linkpoint.avatar

/**
 * Applies VisualParam-driven morph deltas to a base mesh's positions and
 * normals. Pure CPU function — caller is responsible for uploading the
 * result to GPU buffers.
 *
 * AvatarAppearance carries one byte per morph param. We map each byte 0..255
 * to a real param weight in `[value_min, value_max]` (provided by
 * [VisualParamLoader.VisualParam.weightForByte]), then for every morph in
 * the supplied [LLMeshLoader.LLMesh.morphs] map whose name matches a known
 * param name, accumulate `delta * weight` onto the base vertices.
 *
 * Notes:
 *  - Mesh `.llm` morphs are sparse: only the affected vertex indices have
 *    non-zero deltas, so a typical body-shape morph touches <10% of verts.
 *  - We zero out the result buffers each call instead of mutating in place
 *    to keep the call idempotent.
 *  - Normals are NOT renormalised here; the morph deltas are tiny enough
 *    that drift is negligible at typical slider ranges, and renormalising
 *    is a measurable per-vertex cost. If we see lighting artefacts we can
 *    add a normalise pass later.
 */
object MorphApplier {

    /**
     * Compute morphed positions + normals for a base mesh given visual
     * params. Output arrays must be the same size as the base arrays;
     * pre-allocate them (typically per-avatar) and reuse across frames.
     */
    fun apply(
        base: LLMeshLoader.LLMesh,
        visualParams: ByteArray,
        params: List<VisualParamLoader.VisualParam>,
        outPositions: FloatArray,
        outNormals: FloatArray
    ) {
        // Start from the base mesh.
        System.arraycopy(base.positions, 0, outPositions, 0, base.positions.size)
        System.arraycopy(base.normals,   0, outNormals,   0, base.normals.size)

        if (visualParams.isEmpty() || params.isEmpty() || base.morphs.isEmpty()) return

        // For every morph param the AvatarAppearance carries, look up the
        // corresponding morph target by name and accumulate scaled deltas.
        val maxIdx = minOf(visualParams.size, params.size)
        for (i in 0 until maxIdx) {
            val param = params[i]
            val weight = param.weightForByte(visualParams[i].toInt())
            // Skip morphs whose effective weight is the default (no visible
            // contribution) — both for performance and to avoid drift.
            if (kotlin.math.abs(weight - param.valueDefault) < 1e-4f) continue
            val morph = base.morphs[param.name] ?: continue
            applyMorph(morph, weight, outPositions, outNormals)
        }
    }

    private fun applyMorph(
        morph: LLMeshLoader.MorphTarget,
        weight: Float,
        positions: FloatArray,
        normals: FloatArray
    ) {
        val n = morph.vertexIndices.size
        for (i in 0 until n) {
            val v = morph.vertexIndices[i]
            // Defensive: malformed morph might reference a vertex outside
            // the base mesh range. Skip silently rather than crash.
            val pBase = v * 3
            if (pBase + 2 >= positions.size) continue
            positions[pBase]     += morph.coordDeltas[i * 3]     * weight
            positions[pBase + 1] += morph.coordDeltas[i * 3 + 1] * weight
            positions[pBase + 2] += morph.coordDeltas[i * 3 + 2] * weight
            normals[pBase]     += morph.normalDeltas[i * 3]     * weight
            normals[pBase + 1] += morph.normalDeltas[i * 3 + 1] * weight
            normals[pBase + 2] += morph.normalDeltas[i * 3 + 2] * weight
        }
    }
}
