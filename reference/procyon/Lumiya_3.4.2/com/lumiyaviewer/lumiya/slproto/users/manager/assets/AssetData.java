// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager.assets;

public class AssetData
{
    private final byte[] data;
    private final int status;
    
    public AssetData(final int status, final byte[] data) {
        this.status = status;
        this.data = data;
    }
    
    public byte[] getData() {
        return this.data;
    }
    
    public int getStatus() {
        return this.status;
    }
}
