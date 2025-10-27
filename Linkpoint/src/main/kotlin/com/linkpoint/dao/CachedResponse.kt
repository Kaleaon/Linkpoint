package com.linkpoint.dao

class CachedResponse {
    private ByteArray data
    private String key
    private Boolean mustRevalidate

    public CachedResponse(String str) {
        this.key = str
    }

    public CachedResponse(String str, ByteArray bArr, Boolean z) {
        this.key = str
        this.data = bArr
        this.mustRevalidate = z
    }

     public fun getData(): ByteArray {
        return this.data
    }

     public fun getKey(): String {
        return this.key
    }

     public fun getMustRevalidate(): Boolean {
        return this.mustRevalidate
    }

    fun setData(bArr: ByteArray) {
        this.data = bArr
    }

    fun setKey(str: String) {
        this.key = str
    }

    fun setMustRevalidate(z: Boolean) {
        this.mustRevalidate = z
    }
}
