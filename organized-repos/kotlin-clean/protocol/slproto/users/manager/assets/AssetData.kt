package com.linkpoint.slproto.users.manager.assets

class AssetData {
    private val ByteArray data
    private val Int status

    public AssetData(Int i, ByteArray bArr) {
        this.status = i
        this.data = bArr
    }

    public ByteArray getData() {
        return this.data
    }

    public Int getStatus() {
        return this.status
    }
}
