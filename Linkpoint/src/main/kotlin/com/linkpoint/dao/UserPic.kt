package com.linkpoint.dao

class UserPic {
    private ByteArray bitmap
    private Long id
    private String uuid

    public UserPic(Long l) {
        this.id = l
    }

    public UserPic(Long l, String str, ByteArray bArr) {
        this.id = l
        this.uuid = str
        this.bitmap = bArr
    }

     public fun getBitmap(): ByteArray {
        return this.bitmap
    }

     public fun getId(): Long {
        return this.id
    }

     public fun getUuid(): String {
        return this.uuid
    }

    fun setBitmap(bArr: ByteArray) {
        this.bitmap = bArr
    }

    fun setId(l: Long) {
        this.id = l
    }

    fun setUuid(str: String) {
        this.uuid = str
    }
}
