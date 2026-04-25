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
 * Each `<param>` element in avatar_lad.xml describes a slider in the avatar
 * editor: a numeric `id`, a `name` (used to look up the matching morph
 * target in each `.llm` mesh), a `wearable` slot (shape / hair / shirt /
 * pants / etc.), and `value_min` / `value_max` defining the slider range.
 * For "morph" params (`<param_morph/>`), the child element is empty —
 * the morph data lives in the per-mesh `.llm` files keyed by [name].
 *
 * AvatarAppearance message payload: a sequence of bytes, one per param,
 * in the order that the viewer + sim agree on. Following the LL reference
 * viewer (`indra/llcharacter/llvisualparam.cpp`), that order is the
 * "param list" iteration order — params sorted by [id]. Each byte 0..255
 * is interpolated as `value_min + (byte/255) * (value_max - value_min)`.
 *
 * This loader skips skeleton params (`<param_skeleton>`) and color params
 * (`<param_color>`); only morph params drive mesh deformation, which is
 * what we need for body-shape morphing.
 */
object VisualParamLoader {
    private const val TAG = "VisualParamLoader"

    /**
     * One row of the visual-param table. Order in [allMorphParams] matches
     * the byte order in AvatarAppearance.visualParams (by [byteIndex]).
     */
    data class VisualParam(
        val id: Int,
        val byteIndex: Int,
        val name: String,
        val wearable: String,
        val valueMin: Float,
        val valueMax: Float,
        val valueDefault: Float,
        val isMorph: Boolean
    ) {
        /** Map a byte 0..255 from AvatarAppearance to a real param weight. */
        fun weightForByte(byte: Int): Float {
            val t = (byte and 0xFF) / 255f
            return valueMin + t * (valueMax - valueMin)
        }
    }

    /** All loaded params (morph and non-morph), sorted by id. */
    @Volatile
    private var allParamsCached: List<VisualParam> = emptyList()
    /** Subset of [allParamsCached] that drives morphs. */
    @Volatile
    private var morphParamsCached: List<VisualParam> = emptyList()

    /** Lookup table from morph name to morph param. */
    @Volatile
    private var byMorphNameCached: Map<String, VisualParam> = emptyMap()

    /** Has the loader been successfully populated from XML? */
    val isReady: Boolean get() = allParamsCached.isNotEmpty()

    fun allMorphParams(): List<VisualParam> = morphParamsCached
    fun byMorphName(name: String): VisualParam? = byMorphNameCached[name]

    /**
     * Parse `assets/avatar/avatar_lad.xml`. Idempotent — subsequent calls
     * are no-ops if the table is already populated. Returns the count of
     * params loaded (morph + non-morph) for logging.
     */
    fun load(context: Context): Int {
        if (isReady) return allParamsCached.size
        return try {
            val xml = context.assets.open("avatar/avatar_lad.xml").bufferedReader().use { it.readText() }
            val params = parse(xml)
            allParamsCached = params.sortedBy { it.id }
            // Morph param byte-indices follow the sorted-by-id order. We
            // re-emit each morph param with its position in that filtered
            // sequence so the AvatarAppearance handler can index directly.
            val morphs = mutableListOf<VisualParam>()
            allParamsCached.forEach { p ->
                if (p.isMorph) morphs += p.copy(byteIndex = morphs.size)
            }
            morphParamsCached = morphs
            byMorphNameCached = morphs.associateBy { it.name }
            Log.i(TAG, "Loaded ${allParamsCached.size} visual params (${morphs.size} morphs)")
            allParamsCached.size
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load avatar_lad.xml: ${e.message}")
            0
        }
    }

    private fun parse(xml: String): List<VisualParam> {
        val out = mutableListOf<VisualParam>()
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = false
        val parser = factory.newPullParser()
        parser.setInput(StringReader(xml))

        // Track the currently-open <param> element so we can finalise it
        // when we see the type-marker child (param_morph / param_color /
        // param_skeleton / param_driver).
        var currentId: Int? = null
        var currentName = ""
        var currentWearable = ""
        var currentMin = 0f
        var currentMax = 1f
        var currentDefault = 0f

        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG) {
                when (parser.name) {
                    "param" -> {
                        currentId = parser.getAttributeValue(null, "id")?.toIntOrNull()
                        currentName = parser.getAttributeValue(null, "name") ?: ""
                        currentWearable = parser.getAttributeValue(null, "wearable") ?: ""
                        currentMin = parser.getAttributeValue(null, "value_min")?.toFloatOrNull() ?: 0f
                        currentMax = parser.getAttributeValue(null, "value_max")?.toFloatOrNull() ?: 1f
                        currentDefault = parser.getAttributeValue(null, "value_default")?.toFloatOrNull()
                            ?: ((currentMin + currentMax) * 0.5f)
                    }
                    "param_morph" -> {
                        val id = currentId ?: run { event = parser.next(); continue }
                        out += VisualParam(
                            id = id, byteIndex = -1,
                            name = currentName,
                            wearable = currentWearable,
                            valueMin = currentMin, valueMax = currentMax,
                            valueDefault = currentDefault,
                            isMorph = true
                        )
                    }
                    "param_skeleton", "param_color", "param_driver" -> {
                        val id = currentId ?: run { event = parser.next(); continue }
                        out += VisualParam(
                            id = id, byteIndex = -1,
                            name = currentName,
                            wearable = currentWearable,
                            valueMin = currentMin, valueMax = currentMax,
                            valueDefault = currentDefault,
                            isMorph = false
                        )
                    }
                }
            }
            event = parser.next()
        }
        return out
    }
}
