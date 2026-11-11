package com.linkpoint.linden.llcommon

import java.security.MessageDigest
import java.util.UUID

data class LLUUID(val uuid: UUID) : Comparable<LLUUID> {

    constructor() : this(UUID(0L, 0L))

    constructor(s: String) : this(UUID.fromString(s))

    fun isNull(): Boolean = uuid.mostSignificantBits == 0L && uuid.leastSignificantBits == 0L

    fun notNull(): Boolean = !isNull()

    override fun toString(): String = uuid.toString()

    override fun compareTo(other: LLUUID): Int = uuid.compareTo(other.uuid)

    fun xor(other: LLUUID): LLUUID {
        val msnBits = uuid.mostSignificantBits xor other.uuid.mostSignificantBits
        val lsnBits = uuid.leastSignificantBits xor other.uuid.leastSignificantBits
        return LLUUID(UUID(msnBits, lsnBits))
    }

    fun combine(other: LLUUID): LLUUID {
        val md5 = MessageDigest.getInstance("MD5")
        md5.update(toBytes())
        md5.update(other.toBytes())
        val digest = md5.digest()
        var msb = 0L
        var lsb = 0L
        for (i in 0..7) msb = (msb shl 8) or (digest[i].toLong() and 0xffL)
        for (i in 8..15) lsb = (lsb shl 8) or (digest[i].toLong() and 0xffL)
        return LLUUID(UUID(msb, lsb))
    }

    internal fun toBytes(): ByteArray {
        val msb = uuid.mostSignificantBits
        val lsb = uuid.leastSignificantBits
        return ByteArray(16) { i ->
            if (i < 8) ((msb ushr ((7 - i) * 8)) and 0xffL).toByte()
            else ((lsb ushr ((15 - i) * 8)) and 0xffL).toByte()
        }
    }

    companion object {
        val NULL: LLUUID = LLUUID(UUID(0L, 0L))

        fun generate(): LLUUID = LLUUID(UUID.randomUUID())

        fun fromString(s: String): LLUUID? = runCatching { LLUUID(UUID.fromString(s)) }.getOrNull()

        fun combine(id1: LLUUID, id2: LLUUID): LLUUID = id1.combine(id2)

        fun fromBytes(bytes: ByteArray): LLUUID? {
            if (bytes.size < 16) return null
            var msb = 0L
            var lsb = 0L
            for (i in 0..7) msb = (msb shl 8) or (bytes[i].toLong() and 0xffL)
            for (i in 8..15) lsb = (lsb shl 8) or (bytes[i].toLong() and 0xffL)
            return LLUUID(java.util.UUID(msb, lsb))
        }
    }
}
