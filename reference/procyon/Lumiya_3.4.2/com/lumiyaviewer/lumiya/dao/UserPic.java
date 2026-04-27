// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

public class UserPic
{
    private byte[] bitmap;
    private Long id;
    private String uuid;
    
    public UserPic() {
    }
    
    public UserPic(final Long id) {
        this.id = id;
    }
    
    public UserPic(final Long id, final String uuid, final byte[] bitmap) {
        this.id = id;
        this.uuid = uuid;
        this.bitmap = bitmap;
    }
    
    public byte[] getBitmap() {
        return this.bitmap;
    }
    
    public Long getId() {
        return this.id;
    }
    
    public String getUuid() {
        return this.uuid;
    }
    
    public void setBitmap(final byte[] bitmap) {
        this.bitmap = bitmap;
    }
    
    public void setId(final Long id) {
        this.id = id;
    }
    
    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }
}
