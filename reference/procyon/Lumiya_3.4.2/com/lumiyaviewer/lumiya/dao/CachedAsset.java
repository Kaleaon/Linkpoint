// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

public class CachedAsset
{
    private byte[] data;
    private String key;
    private boolean mustRevalidate;
    private int status;
    
    public CachedAsset() {
    }
    
    public CachedAsset(final String key) {
        this.key = key;
    }
    
    public CachedAsset(final String key, final int status, final byte[] data, final boolean mustRevalidate) {
        this.key = key;
        this.status = status;
        this.data = data;
        this.mustRevalidate = mustRevalidate;
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
    
    public void setData(final byte[] data) {
        this.data = data;
    }
    
    public void setKey(final String key) {
        this.key = key;
    }
    
    public void setMustRevalidate(final boolean mustRevalidate) {
        this.mustRevalidate = mustRevalidate;
    }
    
    public void setStatus(final int status) {
        this.status = status;
    }
}
