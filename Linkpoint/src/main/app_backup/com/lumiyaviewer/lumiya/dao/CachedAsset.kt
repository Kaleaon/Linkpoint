package com.lumiyaviewer.lumiya.dao

data class CachedAsset(
    private var key: String? = null,
    private var status: Int = 0,
    private var data: ByteArray? = null,
    private var mustRevalidate: Boolean = false
) {
    fun getKey(): String? {
        return key
    }

    fun setKey(key: String?) {
        this.key = key
    }

    fun getStatus(): Int {
        return status
    }

    fun setStatus(status: Int) {
        this.status = status
    }

    fun getData(): ByteArray? {
        return data
    }

    fun setData(data: ByteArray?) {
        this.data = data
    }

    fun getMustRevalidate(): Boolean {
        return mustRevalidate
    }

    fun setMustRevalidate(mustRevalidate: Boolean) {
        this.mustRevalidate = mustRevalidate
    }

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
        var result = key?.hashCode() ?: 0
        result = 31 * result + status
        result = 31 * result + (data?.contentHashCode() ?: 0)
        result = 31 * result + mustRevalidate.hashCode()
        return result
    }
}
