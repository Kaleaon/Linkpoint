package com.linkpoint.dao
import java.util.*

class MuteListCachedData {
    private Int CRC
    private Byte[] data
    private Long id

    public MuteListCachedData(Long l) {
        this.id = l
    }

    public MuteListCachedData(Long l, Int i, Byte[] bArr) {
        this.id = l
        this.CRC = i
        this.data = bArr
    }

    public Int getCRC() {
        return this.CRC
    }

    public Byte[] getData() {
        return this.data
    }

    public Long getId() {
        return this.id
    }

    public Unit setCRC(Int i) {
        this.CRC = i
    }

    public Unit setData(Byte[] bArr) {
        this.data = bArr
    }

    public Unit setId(Long l) {
        this.id = l
    }
}
