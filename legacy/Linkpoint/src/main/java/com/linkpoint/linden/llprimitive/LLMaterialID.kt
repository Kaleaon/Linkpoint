package com.linkpoint.linden.llprimitive

import com.linkpoint.linden.llcommon.LLSD

class LLMaterialID private constructor(private val bytes: ByteArray) {
    init { require(bytes.size == 16) { "MaterialID must be exactly 16 bytes" } }

    fun isNull(): Boolean = bytes.all { it == 0.toByte() }

    fun toLLSD(): LLSD = LLSD.of(bytes)
    fun toByteArray(): ByteArray = bytes.copyOf()

    fun toHexString(): String = bytes.joinToString("") { "%02x".format(it) }

    override fun equals(other: Any?): Boolean =
        other is LLMaterialID && bytes.contentEquals(other.bytes)

    override fun hashCode(): Int = bytes.contentHashCode()

    override fun toString(): String = toHexString()

    companion object {
        val NULL: LLMaterialID = LLMaterialID(ByteArray(16))

        fun fromBytes(data: ByteArray): LLMaterialID {
            require(data.size == 16) { "Expected 16 bytes" }
            return LLMaterialID(data.copyOf())
        }

        fun fromLLSD(sd: LLSD): LLMaterialID {
            val b = sd.asBinary()
            return if (b.size == 16) LLMaterialID(b.copyOf()) else NULL
        }

        fun fromHexString(hex: String): LLMaterialID {
            if (hex.length != 32) return NULL
            return runCatching {
                LLMaterialID(ByteArray(16) { hex.substring(it * 2, it * 2 + 2).toInt(16).toByte() })
            }.getOrDefault(NULL)
        }

        fun generate(): LLMaterialID =
            LLMaterialID(java.util.UUID.randomUUID().let { uuid ->
                val bb = java.nio.ByteBuffer.allocate(16)
                bb.putLong(uuid.mostSignificantBits)
                bb.putLong(uuid.leastSignificantBits)
                bb.array()
            })
    }
}
