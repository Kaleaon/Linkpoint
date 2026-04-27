package com.linkpoint.dao
import java.util.*

class MuteListCachedData {
    private Int CRC
    private ByteArray data
    private Long id

    public MuteListCachedData(Long l) {
        this.id = l
    }

    public MuteListCachedData(Long l, Int i, ByteArray bArr) {
        this.id = l
        this.CRC = i
        this.data = bArr
    }

     public fun getCRC(): Int {
        return this.CRC
    }

     public fun getData(): ByteArray {
        return this.data
    }

     public fun getId(): Long {
        return this.id
    }

    fun setCRC(i: Int) {
        this.CRC = i
    }

    fun setData(bArr: ByteArray) {
        this.data = bArr
    }

    fun setId(l: Long) {
        this.id = l
    }
}
