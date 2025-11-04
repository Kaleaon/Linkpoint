// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

public class MuteListCachedData
{
    private int CRC;
    private byte[] data;
    private Long id;
    
    public MuteListCachedData() {
    }
    
    public MuteListCachedData(final Long id) {
        this.id = id;
    }
    
    public MuteListCachedData(final Long id, final int crc, final byte[] data) {
        this.id = id;
        this.CRC = crc;
        this.data = data;
    }
    
    public int getCRC() {
        return this.CRC;
    }
    
    public byte[] getData() {
        return this.data;
    }
    
    public Long getId() {
        return this.id;
    }
    
    public void setCRC(final int crc) {
        this.CRC = crc;
    }
    
    public void setData(final byte[] data) {
        this.data = data;
    }
    
    public void setId(final Long id) {
        this.id = id;
    }
}
