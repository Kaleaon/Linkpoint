package com.lumiyaviewer.lumiya.slproto.users.manager.assets

data class AssetData(
    val status: Int,
    val data: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AssetData

        if (status != other.status) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status
        result = 31 * result + data.contentHashCode()
        return result
    }
}