// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

public class CachedResponse
{
    private byte[] data;
    private String key;
    private boolean mustRevalidate;
    
    public CachedResponse() {
    }
    
    public CachedResponse(final String key) {
        this.key = key;
    }
    
    public CachedResponse(final String key, final byte[] data, final boolean mustRevalidate) {
        this.key = key;
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
    
    public void setData(final byte[] data) {
        this.data = data;
    }
    
    public void setKey(final String key) {
        this.key = key;
    }
    
    public void setMustRevalidate(final boolean mustRevalidate) {
        this.mustRevalidate = mustRevalidate;
    }
}
