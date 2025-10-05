package com.linkpoint.slproto.users.manager.assets

class AssetData {
    private val Byte[] data
    private val Int status

    public AssetData(Int i, Byte[] bArr) {
        this.status = i
        this.data = bArr
    }

    public Byte[] getData() {
        return this.data
    }

    public Int getStatus() {
        return this.status
    }
}
