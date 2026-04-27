package com.lumiyaviewer.lumiya.slproto.users;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.io.Serializable;
import java.util.UUID;

public class ParcelData implements Serializable {
    private final int area;
    private final String description;
    private final boolean isGroupOwned;
    private final String mediaURL;
    private final String name;
    private final UUID ownerID;
    private final boolean[] parcelBitmap = new boolean[4096];
    private final int parcelID;
    private final UUID snapshotUUID;

    public ParcelData(LLSDNode lLSDNode) throws LLSDException {
        UUID uuid = null;
        this.parcelID = lLSDNode.byKey("LocalID").asInt();
        this.name = lLSDNode.byKey("Name").asString();
        this.description = lLSDNode.byKey("Desc").asString();
        this.mediaURL = lLSDNode.byKey("MusicURL").asString();
        UUID asUUID = lLSDNode.byKey("SnapshotID").asUUID();
        if (asUUID != null && asUUID.equals(UUIDPool.ZeroUUID)) {
            asUUID = null;
        }
        this.snapshotUUID = asUUID;
        this.ownerID = lLSDNode.keyExists("OwnerID") ? lLSDNode.byKey("OwnerID").asUUID() : uuid;
        this.isGroupOwned = lLSDNode.keyExists("IsGroupOwned") ? lLSDNode.byKey("IsGroupOwned").asBoolean() : false;
        this.area = lLSDNode.keyExists("Area") ? lLSDNode.byKey("Area").asInt() : 0;
        byte[] asBinary = lLSDNode.byKey("Bitmap").asBinary();
        int i = 0;
        while (i < asBinary.length && i < 512) {
            byte b = asBinary[i];
            for (int i2 = 0; i2 < 8; i2++) {
                if ((b & 1) != 0) {
                    this.parcelBitmap[(i * 8) + i2] = true;
                }
                b = (byte) (b >> 1);
            }
            i++;
        }
    }

    public int getArea() {
        return this.area;
    }

    public String getDescription() {
        return this.description;
    }

    public String getMediaURL() {
        return this.mediaURL;
    }

    public String getName() {
        return this.name;
    }

    public UUID getOwnerID() {
        return this.ownerID;
    }

    public boolean[] getParcelBitmap() {
        return this.parcelBitmap;
    }

    public int getParcelID() {
        return this.parcelID;
    }

    public UUID getSnapshotUUID() {
        return this.snapshotUUID;
    }

    public boolean isGroupOwned() {
        return this.isGroupOwned;
    }
}
