package com.linkpoint.dao

class CachedResponse {
    private Byte[] data
    private String key
    private Boolean mustRevalidate

    public CachedResponse(String str) {
        this.key = str
    }

    public CachedResponse(String str, Byte[] bArr, Boolean z) {
        this.key = str
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

    fun setData(Byte[] bArr) {
        this.data = bArr
    }

    fun setKey(String str) {
        this.key = str
    }

    fun setMustRevalidate(Boolean z) {
        this.mustRevalidate = z
    }
}
