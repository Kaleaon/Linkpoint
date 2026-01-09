package com.linkpoint.slproto.users

import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.utils.UUIDPool
import java.io.Serializable
import java.util.UUID

class ParcelData : Serializable {
    private Int area
    private String description
    private Boolean isGroupOwned
    private String mediaURL
    private String name
    private UUID ownerID
    private val parcelBitmap: BooleanArray = BooleanArray(4096)
    private Int parcelID
    private UUID snapshotUUID

    ParcelData(LLSDNode lLSDNode) throws LLSDException {
        UUID uuid = null
        this.parcelID = lLSDNode.byKey("LocalID").asInt()
        this.name = lLSDNode.byKey("Name").asString()
        this.description = lLSDNode.byKey("Desc").asString()
        this.mediaURL = lLSDNode.byKey("MusicURL").asString()
        UUID asUUID = lLSDNode.byKey("SnapshotID").asUUID()
        if (asUUID != null && asUUID.equals(UUIDPool.ZeroUUID)) {
            asUUID = null
        }
        this.snapshotUUID = asUUID
        this.ownerID = lLSDNode.keyExists("OwnerID") ? lLSDNode.byKey("OwnerID").asUUID() : uuid
        this.isGroupOwned = lLSDNode.keyExists("IsGroupOwned") ? lLSDNode.byKey("IsGroupOwned").asBoolean() : false
        this.area = lLSDNode.keyExists("Area") ? lLSDNode.byKey("Area").asInt() : 0
        ByteArray asBinary = lLSDNode.byKey("Bitmap").asBinary()
        var i: Int = 0
        while (i < asBinary.size && i < 512) {
            Byte b = asBinary[i]
            for (i2 in 0 until 8) {
                if ((b & 1) != 0) {
                    this.parcelBitmap[(i * 8) + i2] = true
                }
                b = (Byte) (b >> 1)
            }
            i++
        }
    }

    fun getArea(): Int {
        return this.area
    }

    fun getDescription(): String {
        return this.description
    }

    fun getMediaURL(): String {
        return this.mediaURL
    }

    fun getName(): String {
        return this.name
    }

    fun getOwnerID(): UUID {
        return this.ownerID
    }

    fun getParcelBitmap(): BooleanArray {
        return this.parcelBitmap
    }

    fun getParcelID(): Int {
        return this.parcelID
    }

    fun getSnapshotUUID(): UUID {
        return this.snapshotUUID
    }

    fun isGroupOwned(): Boolean {
        return this.isGroupOwned
    }
}
