package com.lumiyaviewer.lumiya.dao

data class UserPic(
    var id: Long? = null,
    var uuid: String? = null,
    var bitmap: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserPic

        if (id != other.id) return false
        if (uuid != other.uuid) return false
        if (bitmap != null) {
            if (other.bitmap == null) return false
            if (!bitmap.contentEquals(other.bitmap)) return false
        } else if (other.bitmap != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (uuid?.hashCode() ?: 0)
        result = 31 * result + (bitmap?.contentHashCode() ?: 0)
        return result
    }
}