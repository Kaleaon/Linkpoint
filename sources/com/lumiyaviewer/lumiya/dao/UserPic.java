package com.lumiyaviewer.lumiya.dao;

public class UserPic {
    private byte[] bitmap;
    private Long id;
    private String uuid;

    public UserPic() {
    }

    public UserPic(Long l) {
        this.id = l;
    }

    public UserPic(Long l, String str, byte[] bArr) {
        this.id = l;
        this.uuid = str;
        this.bitmap = bArr;
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

    public void setBitmap(byte[] bArr) {
        this.bitmap = bArr;
    }

    public void setId(Long l) {
        this.id = l;
    }

    public void setUuid(String str) {
        this.uuid = str;
    }
}
