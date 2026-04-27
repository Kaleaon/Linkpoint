package com.linkpoint.slproto.users.manager.assets

class AssetData {
    private val ByteArray data
    private val Int status

    public AssetData(Int i, ByteArray bArr) {
        this.status = i
        this.data = bArr
    }

     public fun getData(): ByteArray {
        return this.data
    }

     public fun getStatus(): Int {
        return this.status
    }
}
