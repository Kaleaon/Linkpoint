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
    private val BooleanArray parcelBitmap = BooleanArray(4096)
    private val Int parcelID
    private val UUID snapshotUUID

    public ParcelData(LLSDNode lLSDNode) throws LLSDException {
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
        Int i = 0
        while (i < asBinary.length && i < 512) {
            Byte b = asBinary[i]
            for (Int i2 = 0; i2 < 8; i2++) {
                if ((b & 1) != 0) {
                    this.parcelBitmap[(i * 8) + i2] = true
                }
                b = (Byte) (b >> 1)
            }
            i++
        }
    }

    public Int getArea() {
        return this.area
    }

    public String getDescription() {
        return this.description
    }

    public String getMediaURL() {
        return this.mediaURL
    }

    public String getName() {
        return this.name
    }

    public UUID getOwnerID() {
        return this.ownerID
    }

    public BooleanArray getParcelBitmap() {
        return this.parcelBitmap
    }

    public Int getParcelID() {
        return this.parcelID
    }

    public UUID getSnapshotUUID() {
        return this.snapshotUUID
    }

    public Boolean isGroupOwned() {
        return this.isGroupOwned
    }
}
