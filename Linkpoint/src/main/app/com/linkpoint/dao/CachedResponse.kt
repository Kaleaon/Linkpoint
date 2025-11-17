package com.linkpoint.dao

data class CachedResponse(
    var key: String,
    var data: ByteArray? = null,
    var mustRevalidate: Boolean = false,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CachedResponse

        if (key != other.key) return false
        if (data != null) {
            if (other.data == null) return false
            if (!data.contentEquals(other.data)) return false
        } else if (other.data != null) {
            return false
        }
        if (mustRevalidate != other.mustRevalidate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + (data?.contentHashCode() ?: 0)
        result = 31 * result + mustRevalidate.hashCode()
        return result
    }
}
