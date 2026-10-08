package com.linkpoint.avatar

import android.content.Context
import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

/**
 * Loads the Linden Lab system avatar's VisualParam table from
 * `assets/avatar/avatar_lad.xml`.
 *
 * Each `<param>` element describes a slider in the avatar editor: numeric
 * `id`, a `name` (used to look up the matching morph target in each `.llm`
 * mesh, or the name of the skeletal/colour parameter), a `wearable` slot
 * (shape / hair / shirt / pants / skin / eyes / etc.), `value_min` /
 * `value_max` defining the slider range, and a single child element that
 * picks the parameter type:
 *
 *   <param_morph/>      drives a sparse vertex delta in the LL meshes
 *   <param_skeleton>    per-bone scale offsets blended by the param weight
 *   <param_color>       a colour palette the param weight indexes into
 *   <param_driver>      composite param that drives multiple sub-params
 *
 * AvatarAppearance message payload: a sequence of bytes, one per param,
 * in the order viewer + sim agree on (params sorted by id). Each byte
 * 0..255 maps to a param weight via `weightForByte(byte)`.
 *
 * This loader retains the data needed to apply each param type at render
 * time:
 *   - morph: just the name (mesh deltas live in the .llm files)
 *   - skeleton: the per-bone (name, scale) offsets; applied by adding
 *     `scale * weight` to each bone's scale on the AvatarSkeleton
 *   - color: the palette of (r, g, b, a) values; the param weight 0..1
 *     interpolates linearly across the palette to produce one colour
 *     (used for the lit material's baseColor tint on the affected
 *     wearable channel)
 */
object VisualParamLoader {
    private const val TAG = "VisualParamLoader"

    /** Param type marker. */
    enum class Kind { MORPH, SKELETON, COLOR, DRIVER }

    /** One row of the visual-param table. */
    data class VisualParam(
        val id: Int,
        val byteIndex: Int,
        val name: String,
        val wearable: String,
        val valueMin: Float,
        val valueMax: Float,
        val valueDefault: Float,
        val kind: Kind,
        /**
         * SL VisualParam group. 0 = TWEAKABLE (slider in the avatar
         * editor + transmitted), 1+ = derived (driven by other params),
         * 3 = TRANSMIT_NOT_TWEAKABLE. Group-0 and group-3 are
         * interleaved in `avatar_lad.xml` document order to form the
         * AvatarAppearance / AgentSetAppearance wire byte block —
         * cinderblocks/libremetaverse calls this `Group0ParamIds`.
         * Default `-1` for params that didn't carry an explicit group
         * attribute (treated as non-transmitted).
         */
        val group: Int = -1,
        /** Skeleton params: per-bone (name → 3-float scale offset). Empty otherwise. */
        val boneScales: Map<String, FloatArray> = emptyMap(),
        /** Color params: ordered list of RGBA tuples (each FloatArray size 4). Empty otherwise. */
        val colorPalette: List<FloatArray> = emptyList()
    ) {
        /** True for morph params (kept for backwards compat). */
        val isMorph: Boolean get() = kind == Kind.MORPH

        /** Map a byte 0..255 from AvatarAppearance to a real param weight. */
        fun weightForByte(byte: Int): Float {
            val t = (byte and 0xFF) / 255f
            return valueMin + t * (valueMax - valueMin)
        }

        /**
         * Sample the colour palette at the supplied weight. Returns null for
         * non-colour params. Weight is normalised against [valueMin,
         * valueMax] internally so callers can pass the raw byte's
         * interpolated weight from [weightForByte] directly.
         */
        fun sampleColor(weight: Float): FloatArray? {
            if (kind != Kind.COLOR || colorPalette.isEmpty()) return null
            if (colorPalette.size == 1) return colorPalette[0].copyOf()
            // Map weight from [valueMin, valueMax] back to [0, 1] across the palette.
            val span = (valueMax - valueMin).takeIf { kotlin.math.abs(it) > 1e-6f } ?: 1f
            val t = ((weight - valueMin) / span).coerceIn(0f, 1f)
            val pos = t * (colorPalette.size - 1)
            val i0 = pos.toInt().coerceIn(0, colorPalette.size - 1)
            val i1 = (i0 + 1).coerceAtMost(colorPalette.size - 1)
            val frac = pos - i0
            val a = colorPalette[i0]
            val b = colorPalette[i1]
            return floatArrayOf(
                a[0] + (b[0] - a[0]) * frac,
                a[1] + (b[1] - a[1]) * frac,
                a[2] + (b[2] - a[2]) * frac,
                a[3] + (b[3] - a[3]) * frac
            )
        }
    }

    @Volatile private var allParamsCached: List<VisualParam> = emptyList()
    @Volatile private var morphParamsCached: List<VisualParam> = emptyList()
    @Volatile private var skeletonParamsCached: List<VisualParam> = emptyList()
    @Volatile private var colorParamsCached: List<VisualParam> = emptyList()
    @Volatile private var byMorphNameCached: Map<String, VisualParam> = emptyMap()
    /**
     * Group-0 + group-3 params in `avatar_lad.xml` document order. This
     * is the canonical wire byte order for both the AgentSetAppearance
     * VisualParam block (encode side) and the AvatarAppearance packet
     * visual_param block (decode side). Mirrors libremetaverse's
     * `VisualParams.Group0ParamIds`. The `byteIndex` field on each
     * entry already encodes its position in this list.
     */
    @Volatile private var wireOrderCached: List<VisualParam> = emptyList()

    val isReady: Boolean get() = allParamsCached.isNotEmpty()

    fun allMorphParams(): List<VisualParam> = morphParamsCached
    fun allSkeletonParams(): List<VisualParam> = skeletonParamsCached
    fun allColorParams(): List<VisualParam> = colorParamsCached
    fun byMorphName(name: String): VisualParam? = byMorphNameCached[name]

    /**
     * Catalogue of every visual param sorted by numeric ID.
     */
    fun allParams(): List<VisualParam> = allParamsCached

    /**
     * Group-0 + group-3 params in `avatar_lad.xml` document order — the
     * canonical wire byte order for the AvatarAppearance + AgentSet-
     * Appearance VisualParam blocks. Bytes are emitted in this list's
     * order; bytes are decoded by indexing into this list. Mirrors
     * libremetaverse's `VisualParams.Group0ParamIds`.
     */
    fun wireOrderParams(): List<VisualParam> = wireOrderCached

    fun load(context: Context): Int {
        if (isReady) return allParamsCached.size
        return try {
            val xml = context.assets.open("avatar/avatar_lad.xml").bufferedReader().use { it.readText() }
            // Parse preserves avatar_lad.xml document order (the parser
            // appends to a list as it walks the XML). The wire byte
            // order depends on document order, so we keep it.
            val docOrder = parse(xml)
            // `allParams()` is the by-ID-sorted view (used elsewhere
            // for lookups by paramID).
            allParamsCached = docOrder.sortedBy { it.id }

            // Wire order: group ∈ {0, 3} in document order. Re-stamp
            // byteIndex to the position in this filtered list so each
            // param knows its own AvatarAppearance / AgentSetAppearance
            // byte slot.
            wireOrderCached = renumber(docOrder.filter { it.group == 0 || it.group == 3 })

            // Map paramID → wire byte index. Used to re-stamp byteIndex
            // on the kind-filtered render lists below so that
            // `bytes[p.byteIndex]` in the renderer fetches the correct
            // wire byte. Params NOT in the wire-order list get -1 and
            // the renderer should treat them as "use valueDefault" (or
            // skip).
            val wireIndexById = wireOrderCached
                .mapIndexed { i, p -> p.id to i }
                .toMap()

            // The kind-filtered lists are render conveniences (apply
            // morph/skeleton/color params separately). Their byteIndex
            // is now the wire byte slot, NOT the position within the
            // filtered list — that was the latent bug: render code did
            // `bytes[p.byteIndex]` and was reading the wrong byte for
            // every non-leading kind, since morph/skeleton/color
            // groups are interleaved in wire order.
            fun reindex(list: List<VisualParam>): List<VisualParam> =
                list.map { it.copy(byteIndex = wireIndexById[it.id] ?: -1) }
            morphParamsCached = reindex(allParamsCached.filter { it.kind == Kind.MORPH })
            skeletonParamsCached = reindex(allParamsCached.filter { it.kind == Kind.SKELETON })
            colorParamsCached = reindex(allParamsCached.filter { it.kind == Kind.COLOR })
            byMorphNameCached = morphParamsCached.associateBy { it.name }
            Log.i(TAG, "Loaded ${allParamsCached.size} visual params " +
                "(wire=${wireOrderCached.size} morph=${morphParamsCached.size} " +
                "skeleton=${skeletonParamsCached.size} color=${colorParamsCached.size})")
            allParamsCached.size
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load avatar_lad.xml: ${e.message}")
            0
        }
    }

    private fun renumber(list: List<VisualParam>): List<VisualParam> =
        list.mapIndexed { i, p -> p.copy(byteIndex = i) }

    private fun parse(xml: String): List<VisualParam> {
        val out = mutableListOf<VisualParam>()
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = false
        val parser = factory.newPullParser()
        parser.setInput(StringReader(xml))

        var currentId: Int? = null
        var currentName = ""
        var currentWearable = ""
        var currentMin = 0f
        var currentMax = 1f
        var currentDefault = 0f
        var currentGroup = -1
        var currentKind: Kind? = null
        var pendingBones = mutableMapOf<String, FloatArray>()
        var pendingColors = mutableListOf<FloatArray>()

        fun finalise() {
            val id = currentId ?: return
            val kind = currentKind ?: return
            out += VisualParam(
                id = id, byteIndex = -1,
                name = currentName, wearable = currentWearable,
                valueMin = currentMin, valueMax = currentMax,
                valueDefault = currentDefault,
                kind = kind,
                group = currentGroup,
                boneScales = if (kind == Kind.SKELETON) pendingBones.toMap() else emptyMap(),
                colorPalette = if (kind == Kind.COLOR) pendingColors.toList() else emptyList()
            )
            currentKind = null
            currentGroup = -1
            pendingBones = mutableMapOf()
            pendingColors = mutableListOf()
        }

        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> when (parser.name) {
                    "param" -> {
                        currentId = parser.getAttributeValue(null, "id")?.toIntOrNull()
                        currentName = parser.getAttributeValue(null, "name") ?: ""
                        currentWearable = parser.getAttributeValue(null, "wearable") ?: ""
                        currentMin = parser.getAttributeValue(null, "value_min")?.toFloatOrNull() ?: 0f
                        currentMax = parser.getAttributeValue(null, "value_max")?.toFloatOrNull() ?: 1f
                        currentDefault = parser.getAttributeValue(null, "value_default")?.toFloatOrNull()
                            ?: ((currentMin + currentMax) * 0.5f)
                        currentGroup = parser.getAttributeValue(null, "group")?.toIntOrNull() ?: -1
                    }
                    "param_morph" -> currentKind = Kind.MORPH
                    "param_skeleton" -> currentKind = Kind.SKELETON
                    "param_color" -> currentKind = Kind.COLOR
                    "param_driver" -> currentKind = Kind.DRIVER
                    "bone" -> {
                        if (currentKind == Kind.SKELETON) {
                            val name = parser.getAttributeValue(null, "name") ?: ""
                            val scaleStr = parser.getAttributeValue(null, "scale")
                            val scale = parseFloat3(scaleStr)
                            if (name.isNotEmpty() && scale != null) pendingBones[name] = scale
                        }
                    }
                    "value" -> {
                        if (currentKind == Kind.COLOR) {
                            val colorStr = parser.getAttributeValue(null, "color")
                            val rgba = parseColor(colorStr)
                            if (rgba != null) pendingColors += rgba
                        }
                    }
                }
                XmlPullParser.END_TAG -> if (parser.name == "param") finalise()
            }
            event = parser.next()
        }
        return out
    }

    private fun parseFloat3(s: String?): FloatArray? {
        if (s.isNullOrBlank()) return null
        val parts = s.trim().split(Regex("\\s+"))
        if (parts.size < 3) return null
        return try {
            floatArrayOf(parts[0].toFloat(), parts[1].toFloat(), parts[2].toFloat())
        } catch (e: NumberFormatException) {
            null
        }
    }

    /** Parse a "r, g, b, a" 0..255 colour string into 0..1 floats. */
    private fun parseColor(s: String?): FloatArray? {
        if (s.isNullOrBlank()) return null
        val parts = s.split(",").map { it.trim() }
        if (parts.size < 3) return null
        return try {
            val r = parts[0].toInt() / 255f
            val g = parts[1].toInt() / 255f
            val b = parts[2].toInt() / 255f
            val a = if (parts.size >= 4) parts[3].toInt() / 255f else 1f
            floatArrayOf(r, g, b, a)
        } catch (e: NumberFormatException) {
            null
        }
    }
}
