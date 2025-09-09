package com.lumiyaviewer.lumiya.dao;

public class CachedAsset {
    private byte[] data;
    private String key;
    private boolean mustRevalidate;
    private int status;

    public CachedAsset() {
    }

    public CachedAsset(String str) {
        this.key = str;
    }

    public CachedAsset(String str, int i, byte[] bArr, boolean z) {
        this.key = str;
        this.status = i;
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

    public int getStatus() {
        return this.status;
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

    public void setStatus(int i) {
        this.status = i;
    }
}
