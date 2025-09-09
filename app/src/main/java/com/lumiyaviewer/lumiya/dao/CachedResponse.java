package com.lumiyaviewer.lumiya.dao;

public class CachedResponse {
    private byte[] data;
    private String key;
    private boolean mustRevalidate;

    public CachedResponse(String str) {
        this.key = str;
    }

    public CachedResponse(String str, byte[] bArr, boolean z) {
        this.key = str;
        this.data = bArr;
        this.mustRevalidate = z;
    }

    public byte[] getData() {
        return this.data;
    }

    public String getKey() {
        return this.key;
    }

    public boolean getMustRevalidate() {
        return this.mustRevalidate;
    }

    public void setData(byte[] bArr) {
        this.data = bArr;
    }

    public void setKey(String str) {
        this.key = str;
    }

    public void setMustRevalidate(boolean z) {
        this.mustRevalidate = z;
    }
}
