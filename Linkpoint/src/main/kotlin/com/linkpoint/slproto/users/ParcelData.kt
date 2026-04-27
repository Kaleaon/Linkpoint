package com.linkpoint.slproto.users

import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.utils.UUIDPool
import java.io.Serializable
import java.util.UUID

class ParcelData : Serializable {
    private val Int area
    private val String description
    private val Boolean isGroupOwned
    private val String mediaURL
    private val String name
    private val UUID ownerID
    private val BooleanArray parcelBitmap = Boolean[4096]
    private val Int parcelID
    private val UUID snapshotUUID

    public ParcelData(LLSDNode lLSDNode) throws LLSDException {
        val uuid: UUID = null
        this.parcelID = lLSDNode.byKey("LocalID").asInt()
        this.name = lLSDNode.byKey("Name").asString()
        this.description = lLSDNode.byKey("Desc").asString()
        this.mediaURL = lLSDNode.byKey("MusicURL").asString()
        val asUUID: UUID = lLSDNode.byKey("SnapshotID").asUUID()
        if (asUUID != null && asUUID.equals(UUIDPool.ZeroUUID)) {
            asUUID = null
        }
        this.snapshotUUID = asUUID
        this.ownerID = lLSDNode.keyExists("OwnerID") ? lLSDNode.byKey("OwnerID").asUUID() : uuid
        this.isGroupOwned = lLSDNode.keyExists("IsGroupOwned") ? lLSDNode.byKey("IsGroupOwned").asBoolean() : false
        this.area = lLSDNode.keyExists("Area") ? lLSDNode.byKey("Area").asInt() : 0
        val asBinary: ByteArray = lLSDNode.byKey("Bitmap").asBinary()
        val i: Int = 0
        while (i < asBinary.length && i < 512) {
            val b: Byte = asBinary[i]
            for (Int i2 = 0; i2 < 8; i2++) {
                if ((b & 1) != 0) {
                    this.parcelBitmap[(i * 8) + i2] = true
                }
                b = (Byte) (b >> 1)
            }
            i++
        }
    }

     public fun getArea(): Int {
        return this.area
    }

     public fun getDescription(): String {
        return this.description
    }

     public fun getMediaURL(): String {
        return this.mediaURL
    }

     public fun getName(): String {
        return this.name
    }

     public fun getOwnerID(): UUID {
        return this.ownerID
    }

     public fun getParcelBitmap(): BooleanArray {
        return this.parcelBitmap
    }

     public fun getParcelID(): Int {
        return this.parcelID
    }

     public fun getSnapshotUUID(): UUID {
        return this.snapshotUUID
    }

     public fun isGroupOwned(): Boolean {
        return this.isGroupOwned
    }
}
