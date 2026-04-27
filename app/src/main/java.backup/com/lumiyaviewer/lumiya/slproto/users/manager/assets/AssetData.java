package com.lumiyaviewer.lumiya.slproto.users.manager.assets;

public class AssetData {
    private final byte[] data;
    private final int status;

    public AssetData(int i, byte[] bArr) {
        this.status = i;
        this.data = bArr;
    }

    public byte[] getData() {
        return this.data;
    }

    public int getStatus() {
        return this.status;
    }
}
