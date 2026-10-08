package com.linkpoint.linden.llprimitive

import com.linkpoint.linden.llcommon.LLUUID
import com.linkpoint.linden.llcommon.LLSD
import com.linkpoint.linden.llmath.Color4
import java.nio.ByteBuffer
import java.nio.ByteOrder

// Bit layout for the bump/shiny/fullbright packed byte:
// | SS FB BBBBB |  S=Shiny(2b), F=Fullbright(1b), B=Bumpmap(5b)
private const val BUMP_MASK: Int        = 0x1F
private const val FULLBRIGHT_SHIFT: Int = 5
private const val FULLBRIGHT_MASK: Int  = 0x01
private const val SHINY_SHIFT: Int      = 6
private const val SHINY_MASK: Int       = 0x03

// Media/texgen packed byte: | . . . T T M |  T=TexGen(2b), M=Media(1b)
private const val MEDIA_MASK: Int       = 0x01
private const val TEX_GEN_MASK: Int     = 0x06
private const val TEX_GEN_SHIFT: Int    = 1

enum class TexGen(val bits: UByte) {
    DEFAULT(0x00u),
    PLANAR(0x02u),
    SPHERICAL(0x04u),
    CYLINDRICAL(0x06u);

    companion object {
        fun fromBits(bits: UByte): TexGen =
            entries.firstOrNull { it.bits == (bits and TEX_GEN_MASK.toUByte()) } ?: DEFAULT
    }
}

data class TextureEntry(
    var textureId: LLUUID = LLUUID.NULL,
    var color: Color4 = Color4(1f, 1f, 1f, 1f),
    var scaleS: Float = 1f,
    var scaleT: Float = 1f,
    var offsetS: Float = 0f,
    var offsetT: Float = 0f,
    var rotation: Float = 0f,
    var glow: Float = 0f,
    // packed: bits[4:0]=bumpmap, bit[5]=fullbright, bits[7:6]=shiny
    var bumpPacked: UByte = 0u,
    // packed: bit[0]=media, bits[2:1]=texgen
    var mediaFlags: UByte = 0u,
) {
    var bumpmap: UByte
        get() = (bumpPacked.toInt() and BUMP_MASK).toUByte()
        set(v) { bumpPacked = ((bumpPacked.toInt() and BUMP_MASK.inv()) or (v.toInt() and BUMP_MASK)).toUByte() }

    var fullbright: Boolean
        get() = ((bumpPacked.toInt() ushr FULLBRIGHT_SHIFT) and FULLBRIGHT_MASK) != 0
        set(v) {
            bumpPacked = if (v)
                (bumpPacked.toInt() or (FULLBRIGHT_MASK shl FULLBRIGHT_SHIFT)).toUByte()
            else
                (bumpPacked.toInt() and (FULLBRIGHT_MASK shl FULLBRIGHT_SHIFT).inv()).toUByte()
        }

    var shinyness: UByte
        get() = ((bumpPacked.toInt() ushr SHINY_SHIFT) and SHINY_MASK).toUByte()
        set(v) {
            bumpPacked = ((bumpPacked.toInt() and (SHINY_MASK shl SHINY_SHIFT).inv()) or
                ((v.toInt() and SHINY_MASK) shl SHINY_SHIFT)).toUByte()
        }

    var hasMedia: Boolean
        get() = (mediaFlags.toInt() and MEDIA_MASK) != 0
        set(v) {
            mediaFlags = if (v)
                (mediaFlags.toInt() or MEDIA_MASK).toUByte()
            else
                (mediaFlags.toInt() and MEDIA_MASK.inv()).toUByte()
        }

    var texGen: TexGen
        get() = TexGen.fromBits(mediaFlags)
        set(v) {
            mediaFlags = ((mediaFlags.toInt() and TEX_GEN_MASK.inv()) or v.bits.toInt()).toUByte()
        }

    fun toSD(): LLSD = LLSD.map(
        "imageid"  to LLSD.uuid(textureId),
        "colors"   to LLSD.array(
            LLSD.real(color.r.toDouble()),
            LLSD.real(color.g.toDouble()),
            LLSD.real(color.b.toDouble()),
            LLSD.real(color.a.toDouble()),
        ),
        "scales"   to LLSD.real(scaleS.toDouble()),
        "scalet"   to LLSD.real(scaleT.toDouble()),
        "offsets"  to LLSD.real(offsetS.toDouble()),
        "offsett"  to LLSD.real(offsetT.toDouble()),
        "imagerot" to LLSD.real(rotation.toDouble()),
        "bump"     to LLSD.integer(bumpPacked.toInt()),
        "texgen"   to LLSD.integer(mediaFlags.toInt()),
        "glow"     to LLSD.real(glow.toDouble()),
    )

    companion object {
        fun fromSD(sd: LLSD): TextureEntry = TextureEntry(
            textureId  = sd["imageid"].asUUID(),
            color      = sd["colors"].let { c ->
                Color4(c[0].asFloat(), c[1].asFloat(), c[2].asFloat(), c[3].asFloat())
            },
            scaleS     = sd["scales"].asFloat(),
            scaleT     = sd["scalet"].asFloat(),
            offsetS    = sd["offsets"].asFloat(),
            offsetT    = sd["offsett"].asFloat(),
            rotation   = sd["imagerot"].asFloat(),
            glow       = sd["glow"].asFloat(),
            bumpPacked = sd["bump"].asInt().toUByte(),
            mediaFlags = sd["texgen"].asInt().toUByte(),
        )

        // Deserialise from a raw packed binary representation (little-endian).
        fun unpack(buf: ByteBuffer): TextureEntry {
            buf.order(ByteOrder.LITTLE_ENDIAN)
            val id = LLUUID.fromBytes(ByteArray(16).also { buf.get(it) }) ?: LLUUID.NULL
            val r = buf.get().toInt() and 0xFF
            val g = buf.get().toInt() and 0xFF
            val b = buf.get().toInt() and 0xFF
            val a = buf.get().toInt() and 0xFF
            val color = Color4(r / 255f, g / 255f, b / 255f, a / 255f)
            val scaleS = buf.float
            val scaleT = buf.float
            val offsetS = buf.float
            val offsetT = buf.float
            val rotation = buf.float
            val bump = buf.get().toUByte()
            val media = buf.get().toUByte()
            val glow = buf.get().toInt() and 0xFF
            return TextureEntry(
                textureId  = id,
                color      = color,
                scaleS     = scaleS,
                scaleT     = scaleT,
                offsetS    = offsetS,
                offsetT    = offsetT,
                rotation   = rotation,
                glow       = glow / 255f,
                bumpPacked = bump,
                mediaFlags = media,
            )
        }
    }

    fun pack(buf: ByteBuffer) {
        buf.order(ByteOrder.LITTLE_ENDIAN)
        buf.put(textureId.toBytes())
        buf.put((color.r * 255f + 0.5f).toInt().coerceIn(0, 255).toByte())
        buf.put((color.g * 255f + 0.5f).toInt().coerceIn(0, 255).toByte())
        buf.put((color.b * 255f + 0.5f).toInt().coerceIn(0, 255).toByte())
        buf.put((color.a * 255f + 0.5f).toInt().coerceIn(0, 255).toByte())
        buf.putFloat(scaleS)
        buf.putFloat(scaleT)
        buf.putFloat(offsetS)
        buf.putFloat(offsetT)
        buf.putFloat(rotation)
        buf.put(bumpPacked.toByte())
        buf.put(mediaFlags.toByte())
        buf.put((glow * 255f + 0.5f).toInt().coerceIn(0, 255).toByte())
    }
}

class TextureList(capacity: Int = 0) {
    private val entries: MutableList<TextureEntry> = ArrayList(capacity)

    val size: Int get() = entries.size

    operator fun get(index: Int): TextureEntry = entries[index]
    operator fun set(index: Int, te: TextureEntry) { entries[index] = te }

    fun setSize(n: Int) {
        while (entries.size < n) entries.add(TextureEntry())
        while (entries.size > n) entries.removeAt(entries.size - 1)
    }

    fun setAllIds(id: LLUUID) = entries.forEach { it.textureId = id }

    fun copy(other: TextureList) {
        entries.clear()
        other.entries.mapTo(entries) { it.copy() }
    }

    fun toList(): List<TextureEntry> = entries.toList()
}
