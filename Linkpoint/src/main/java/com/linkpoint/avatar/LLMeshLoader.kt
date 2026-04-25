package com.linkpoint.avatar

import android.content.Context
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Parser for the legacy "Linden Binary Mesh" file format used for the SL
 * system avatar (avatar_head.llm, avatar_upper_body.llm, avatar_lower_body.llm,
 * avatar_eye.llm, avatar_eyelashes.llm, avatar_hair.llm, avatar_skirt.llm).
 *
 * Verified against secondlife/viewer's `indra/llappearance/llpolymesh.cpp`,
 * function `LLPolyMeshSharedData::loadMesh`. Notable details that an
 * "obvious" reading would get wrong, in order:
 *
 *   1. After `hasWeights` and `hasDetailTexCoords` (each 1 byte), the header
 *      contains a `position` (3 floats), `rotationAngles` (3 floats),
 *      `rotationOrder` (1 byte) and `scale` (3 floats) — total 25 bytes —
 *      *before* numVertices.
 *   2. Counts are U16, NOT U32. This is true for numVertices, numFaces,
 *      and numSkinJoints. The C++ stores them as S32/U32 in memory but the
 *      on-disk widths are 16 bits.
 *   3. Per-vertex attributes are NOT interleaved. They're written as
 *      separate blocks: all coords, then all normals, then all binormals,
 *      then all UVs (always present), then optional detail UVs, then
 *      optional weights.
 *   4. After indices (and optional skin joint names), morphs are stored
 *      sentinel-terminated: read 64-byte names until one equals
 *      "End Morphs". Each morph payload uses S32 / U32 widths, not U16.
 *   5. After morphs there may optionally be a vertex remap table (S32
 *      count + pairs); EOF is also legal.
 *
 * We read the renderable subset (positions, normals, UVs, indices) and
 * capture skin weights + joint names for future GPU-skinning. Morphs and
 * remaps are skipped (we just stop reading).
 */
object LLMeshLoader {
    private const val TAG = "LLMeshLoader"
    private const val MAGIC = "Linden Binary Mesh 1.0"
    private const val MAGIC_RESERVED = 24 // bytes 0..23 padded with NULs

    data class LLMesh(
        val name: String,
        val positions: FloatArray,
        val normals: FloatArray,
        val uvs: FloatArray,
        val indices: ShortArray,
        /** Per-vertex bone weight (0..numJoints, frac = blend). Empty if no skin. */
        val weights: FloatArray = FloatArray(0),
        /** Joint names referenced by [weights]; index = floor(weight). */
        val jointNames: List<String> = emptyList(),
        /**
         * Morph targets keyed by name. Each morph is a sparse delta: only
         * the affected vertex indices get a non-zero coord/normal/uv delta.
         */
        val morphs: Map<String, MorphTarget> = emptyMap()
    ) {
        val vertexCount: Int get() = positions.size / 3
        val indexCount: Int get() = indices.size

        /**
         * Filament wants 4-bone-per-vertex weighted skinning: BONE_INDICES
         * is a UBYTE4 attribute and BONE_WEIGHTS is a FLOAT4. The legacy
         * `.llm` format stores a single float per vertex where
         *   floor(W) = primary joint index
         *   W - floor(W) = blend weight onto joint floor(W)+1
         *
         * Convert that to two non-zero bone weights per vertex (and pad to
         * 4 with zero-weight bone 0). Returns null if this mesh has no
         * skin data (unrigged mesh).
         */
        fun deriveBoneIndices(): ByteArray? {
            if (weights.isEmpty() || jointNames.isEmpty()) return null
            val maxJoint = jointNames.size - 1
            val out = ByteArray(vertexCount * 4)
            for (i in 0 until vertexCount) {
                val w = weights[i]
                val primary = kotlin.math.floor(w).toInt().coerceIn(0, maxJoint)
                val secondary = (primary + 1).coerceAtMost(maxJoint)
                out[i * 4] = primary.toByte()
                out[i * 4 + 1] = secondary.toByte()
                out[i * 4 + 2] = 0
                out[i * 4 + 3] = 0
            }
            return out
        }

        fun deriveBoneWeights(): FloatArray? {
            if (weights.isEmpty()) return null
            val out = FloatArray(vertexCount * 4)
            for (i in 0 until vertexCount) {
                val w = weights[i]
                val frac = (w - kotlin.math.floor(w)).coerceIn(0f, 1f)
                out[i * 4]     = 1f - frac
                out[i * 4 + 1] = frac
                out[i * 4 + 2] = 0f
                out[i * 4 + 3] = 0f
            }
            return out
        }
    }

    /**
     * Sparse vertex deltas for a named morph target. [vertexIndices] holds
     * the affected vertex indices; [coordDeltas] / [normalDeltas] / [uvDeltas]
     * are flat arrays parallel to [vertexIndices] (3, 3, 2 floats per entry).
     */
    data class MorphTarget(
        val name: String,
        val vertexIndices: IntArray,
        val coordDeltas: FloatArray,
        val normalDeltas: FloatArray,
        val uvDeltas: FloatArray
    )

    /**
     * Load and parse one of the bundled `assets/character/avatar_*.llm`
     * files. Returns null on missing asset or parse failure (callers
     * should fall back to the placeholder articulated body in that case).
     */
    fun load(context: Context, assetName: String): LLMesh? {
        return try {
            val bytes = context.assets.open("character/$assetName").use { it.readBytes() }
            parse(bytes, assetName)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load LLM asset $assetName: ${e.message}")
            null
        }
    }

    fun parse(bytes: ByteArray, name: String = "<unnamed>"): LLMesh? {
        if (bytes.size < MAGIC_RESERVED + 25 + 2) return null
        val magic = String(bytes, 0, MAGIC.length, Charsets.US_ASCII)
        if (magic != MAGIC) {
            Log.w(TAG, "$name: not an LLM file (magic='$magic')")
            return null
        }

        val bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        bb.position(MAGIC_RESERVED)
        try {
            val hasWeights = bb.get().toInt() != 0
            val hasDetailTex = bb.get().toInt() != 0

            // 25-byte block: position(3f) + rotationAngles(3f) + rotationOrder(1B) + scale(3f).
            // We don't apply rotation/scale here — the SL system avatar
            // uses identity rotation (zero Euler) and unit scale for all
            // shipped meshes, and applying them generically would also
            // require LL's mayaQ() angle-order conversion.
            val px = bb.float; val py = bb.float; val pz = bb.float
            // Skip rotationAngles + rotationOrder + scale (3+1+3 floats + 1 byte).
            bb.float; bb.float; bb.float        // rotationAngles
            bb.get()                            // rotationOrder
            bb.float; bb.float; bb.float        // scale (ignored for now)

            // U16 count, NOT U32.
            val numVertices = bb.short.toInt() and 0xFFFF
            if (numVertices <= 0 || numVertices > 200_000) {
                Log.w(TAG, "$name: implausible vertex count $numVertices")
                return null
            }

            // De-interleaved per-vertex attribute blocks. Order matches
            // LLPolyMeshSharedData::loadMesh: coords, normals, binormals,
            // texCoords, [detailTexCoords], [weights].
            val positions = FloatArray(numVertices * 3)
            for (i in 0 until numVertices) {
                positions[i * 3]     = bb.float + px
                positions[i * 3 + 1] = bb.float + py
                positions[i * 3 + 2] = bb.float + pz
            }
            val normals = FloatArray(numVertices * 3)
            for (i in 0 until numVertices) {
                normals[i * 3]     = bb.float
                normals[i * 3 + 1] = bb.float
                normals[i * 3 + 2] = bb.float
            }
            // Skip binormals — Filament rebuilds tangent frame from normals.
            for (i in 0 until numVertices) {
                bb.float; bb.float; bb.float
            }
            val uvs = FloatArray(numVertices * 2)
            for (i in 0 until numVertices) {
                uvs[i * 2]     = bb.float
                uvs[i * 2 + 1] = bb.float
            }
            if (hasDetailTex) {
                // Skip detail UVs (UV1) — not yet wired through the lit material.
                for (i in 0 until numVertices) {
                    bb.float; bb.float
                }
            }
            val weights = if (hasWeights) FloatArray(numVertices) else FloatArray(0)
            if (hasWeights) {
                for (i in 0 until numVertices) {
                    weights[i] = bb.float
                }
            }

            // U16 face count.
            val numFaces = bb.short.toInt() and 0xFFFF
            if (numFaces <= 0 || numFaces > 200_000) {
                Log.w(TAG, "$name: implausible face count $numFaces")
                return null
            }
            val indices = ShortArray(numFaces * 3)
            for (i in 0 until numFaces) {
                indices[i * 3]     = bb.short
                indices[i * 3 + 1] = bb.short
                indices[i * 3 + 2] = bb.short
            }

            // Skin joint names (U16 count, then 64-byte name buffers).
            val jointNames = mutableListOf<String>()
            if (hasWeights) {
                if (bb.remaining() < 2) {
                    return LLMesh(
                        name = name, positions = positions, normals = normals,
                        uvs = uvs, indices = indices, weights = weights,
                        jointNames = jointNames
                    )
                }
                val numSkinJoints = bb.short.toInt() and 0xFFFF
                if (numSkinJoints in 1..256 && bb.remaining() >= numSkinJoints * 64) {
                    val nameBuf = ByteArray(64)
                    for (j in 0 until numSkinJoints) {
                        bb.get(nameBuf)
                        val nul = nameBuf.indexOf(0)
                        val s = String(nameBuf, 0, if (nul >= 0) nul else 64, Charsets.US_ASCII)
                            .trimEnd(' ', ' ')
                        jointNames.add(s)
                    }
                }
            }

            // Sentinel-terminated morph list. Each morph is a 64-byte name
            // followed by S32 numVertices, then per-vertex U32 index +
            // 12-byte coord delta + 12-byte normal delta + 12-byte binormal
            // delta (skipped) + 8-byte UV delta.
            val morphs = parseMorphs(bb, name)

            // Trailing remap table is optional — silently ignore EOF.

            return LLMesh(
                name = name,
                positions = positions,
                normals = normals,
                uvs = uvs,
                indices = indices,
                weights = weights,
                jointNames = jointNames,
                morphs = morphs
            )
        } catch (e: Exception) {
            Log.w(TAG, "$name: LLM parse failed at byte ${bb.position()}: ${e.message}")
            return null
        }
    }

    /**
     * Read the sentinel-terminated morph list: each morph starts with a
     * 64-byte name; the literal "End Morphs" marks the end. Each morph
     * payload uses S32 / U32 widths (NOT U16 like the base mesh).
     *
     * Returns an empty map if there's no morph section, the section is
     * malformed, or we hit EOF mid-morph (we keep what we got).
     */
    private fun parseMorphs(bb: ByteBuffer, meshName: String): Map<String, MorphTarget> {
        val out = HashMap<String, MorphTarget>()
        val nameBuf = ByteArray(64)
        try {
            while (bb.remaining() >= 64) {
                bb.get(nameBuf)
                val nul = nameBuf.indexOf(0)
                val morphName = String(nameBuf, 0, if (nul >= 0) nul else 64, Charsets.US_ASCII)
                if (morphName == "End Morphs") break
                if (bb.remaining() < 4) break
                val numVertices = bb.int
                if (numVertices <= 0 || numVertices > 200_000) {
                    // Malformed — bail rather than read garbage.
                    break
                }
                val needBytes = numVertices.toLong() * (4 + 12 + 12 + 12 + 8)
                if (bb.remaining() < needBytes) break

                val vertexIndices = IntArray(numVertices)
                val coordDeltas = FloatArray(numVertices * 3)
                val normalDeltas = FloatArray(numVertices * 3)
                val uvDeltas = FloatArray(numVertices * 2)
                for (i in 0 until numVertices) {
                    vertexIndices[i] = bb.int
                    coordDeltas[i * 3]     = bb.float
                    coordDeltas[i * 3 + 1] = bb.float
                    coordDeltas[i * 3 + 2] = bb.float
                    normalDeltas[i * 3]     = bb.float
                    normalDeltas[i * 3 + 1] = bb.float
                    normalDeltas[i * 3 + 2] = bb.float
                    // Skip binormal delta (12 bytes).
                    bb.float; bb.float; bb.float
                    uvDeltas[i * 2]     = bb.float
                    uvDeltas[i * 2 + 1] = bb.float
                }
                if (morphName.isNotEmpty()) {
                    out[morphName] = MorphTarget(
                        name = morphName,
                        vertexIndices = vertexIndices,
                        coordDeltas = coordDeltas,
                        normalDeltas = normalDeltas,
                        uvDeltas = uvDeltas
                    )
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "$meshName: morph list parse aborted: ${e.message}")
        }
        return out
    }
}
