package com.linkpoint.dao

class UserPic {
    private Byte[] bitmap
    private Long id
    private String uuid

    public UserPic(Long l) {
        this.id = l
    }

    public UserPic(Long l, String str, Byte[] bArr) {
        this.id = l
        this.uuid = str
        this.bitmap = bArr
    }

    public Byte[] getBitmap() {
        return this.bitmap
    }

    public Long getId() {
        return this.id
    }

    public String getUuid() {
        return this.uuid
    }

    fun setBitmap(Byte[] bArr) {
        this.bitmap = bArr
    }

    fun setId(Long l) {
        this.id = l
    }

    fun setUuid(String str) {
        this.uuid = str
    }
}
