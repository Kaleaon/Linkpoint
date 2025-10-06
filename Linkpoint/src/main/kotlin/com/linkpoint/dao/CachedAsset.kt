package com.linkpoint.dao

class CachedAsset {
    private Byte[] data
    private String key
    private Boolean mustRevalidate
    private Int status

    public CachedAsset(String str) {
        this.key = str
    }

    public CachedAsset(String str, Int i, Byte[] bArr, Boolean z) {
        this.key = str
        this.status = i
        this.data = bArr
        this.mustRevalidate = z
    }

    public Byte[] getData() {
        return this.data
    }

    public String getKey() {
        return this.key
    }

    public Boolean getMustRevalidate() {
        return this.mustRevalidate
    }

    public Int getStatus() {
        return this.status
    }

    fun setData(Byte[] bArr) {
        this.data = bArr
    }

    fun setKey(String str) {
        this.key = str
    }

    fun setMustRevalidate(Boolean z) {
        this.mustRevalidate = z
    }

    fun setStatus(Int i) {
        this.status = i
    }
}
