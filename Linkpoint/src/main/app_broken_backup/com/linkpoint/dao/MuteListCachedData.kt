package com.linkpoint.dao

data class MuteListCachedData(
    var id: Long? = null,
    var CRC: Int = 0,
    var data: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MuteListCachedData

        if (id != other.id) return false
        if (CRC != other.CRC) return false
        if (data != null) {
            if (other.data == null) return false
            if (!data.contentEquals(other.data)) return false
        } else if (other.data != null) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + CRC
        result = 31 * result + (data?.contentHashCode() ?: 0)
        return result
    }
}
