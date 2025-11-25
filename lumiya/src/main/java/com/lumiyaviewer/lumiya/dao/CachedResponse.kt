package com.lumiyaviewer.lumiya.dao

data class CachedResponse(
    private var key: String,
    private var data: ByteArray? = null,
    private var mustRevalidate: Boolean = false,
) {
    fun getKey(): String? {
        return key
    }

    fun setKey(key: String) {
        this.key = key
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
