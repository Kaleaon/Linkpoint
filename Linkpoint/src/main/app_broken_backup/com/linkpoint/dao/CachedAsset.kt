package com.linkpoint.dao

data class CachedAsset(
    var key: String,
    var status: Int = 0,
    var data: ByteArray? = null,
    var mustRevalidate: Boolean = false,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CachedAsset

        if (key != other.key) return false
        if (status != other.status) return false
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
        result = 31 * result + status
        result = 31 * result + (data?.contentHashCode() ?: 0)
        result = 31 * result + mustRevalidate.hashCode()
        return result
    }
}
