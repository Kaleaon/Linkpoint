package com.linkpoint.dao

class CachedAsset {
    private ByteArray data
    private String key
    private Boolean mustRevalidate
    private Int status

    public CachedAsset(String str) {
        this.key = str
    }

    public CachedAsset(String str, Int i, ByteArray bArr, Boolean z) {
        this.key = str
        this.status = i
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

     public fun getStatus(): Int {
        return this.status
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

    fun setStatus(i: Int) {
        this.status = i
    }
}
