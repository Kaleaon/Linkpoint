package com.lumiyaviewer.lumiya.dao;
import java.util.*;

public class MuteListCachedData {
    private int CRC;
    private byte[] data;
    private Long id;

    public MuteListCachedData(Long l) {
        this.id = l;
    }

    public MuteListCachedData(Long l, int i, byte[] bArr) {
        this.id = l;
        this.CRC = i;
        this.data = bArr;
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

    public void setCRC(int i) {
        this.CRC = i;
    }

    public void setData(byte[] bArr) {
        this.data = bArr;
    }

    public void setId(Long l) {
        this.id = l;
    }
}
