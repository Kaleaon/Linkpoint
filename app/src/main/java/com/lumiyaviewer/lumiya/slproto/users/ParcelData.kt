package com.lumiyaviewer.lumiya.slproto.users

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.lumiya.utils.UUIDPool
import java.io.Serializable
import java.util.UUID

class ParcelData : Serializable {
    private Int area
    private String description
    private Boolean isGroupOwned
    private String mediaURL
    private String name
    private UUID ownerID
    private BooleanArray parcelBitmap = BooleanArray(4096)
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

    Int getArea() {
        return this.area
    }

    String getDescription() {
        return this.description
    }

    String getMediaURL() {
        return this.mediaURL
    }

    String getName() {
        return this.name
    }

    UUID getOwnerID() {
        return this.ownerID
    }

    BooleanArray getParcelBitmap() {
        return this.parcelBitmap
    }

    Int getParcelID() {
        return this.parcelID
    }

    UUID getSnapshotUUID() {
        return this.snapshotUUID
    }

    Boolean isGroupOwned() {
        return this.isGroupOwned
    }
}
