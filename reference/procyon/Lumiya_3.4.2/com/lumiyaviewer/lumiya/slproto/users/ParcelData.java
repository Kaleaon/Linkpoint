// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.util.UUID;
import java.io.Serializable;

public class ParcelData implements Serializable
{
    private final int area;
    private final String description;
    private final boolean isGroupOwned;
    private final String mediaURL;
    private final String name;
    private final UUID ownerID;
    private final boolean[] parcelBitmap;
    private final int parcelID;
    private final UUID snapshotUUID;
    
    public ParcelData(final LLSDNode llsdNode) throws LLSDException {
        final UUID uuid = null;
        this.parcelBitmap = new boolean[4096];
        this.parcelID = llsdNode.byKey("LocalID").asInt();
        this.name = llsdNode.byKey("Name").asString();
        this.description = llsdNode.byKey("Desc").asString();
        this.mediaURL = llsdNode.byKey("MusicURL").asString();
        UUID uuid3;
        final UUID uuid2 = uuid3 = llsdNode.byKey("SnapshotID").asUUID();
        if (uuid2 != null) {
            uuid3 = uuid2;
            if (uuid2.equals(UUIDPool.ZeroUUID)) {
                uuid3 = null;
            }
        }
        this.snapshotUUID = uuid3;
        UUID uuid4 = uuid;
        if (llsdNode.keyExists("OwnerID")) {
            uuid4 = llsdNode.byKey("OwnerID").asUUID();
        }
        this.ownerID = uuid4;
        this.isGroupOwned = (llsdNode.keyExists("IsGroupOwned") && llsdNode.byKey("IsGroupOwned").asBoolean());
        int int1;
        if (llsdNode.keyExists("Area")) {
            int1 = llsdNode.byKey("Area").asInt();
        }
        else {
            int1 = 0;
        }
        this.area = int1;
        final byte[] binary = llsdNode.byKey("Bitmap").asBinary();
        for (int n = 0; n < binary.length && n < 512; ++n) {
            byte b = binary[n];
            for (int i = 0; i < 8; ++i) {
                if ((b & 0x1) != 0x0) {
                    this.parcelBitmap[n * 8 + i] = true;
                }
                b >>= 1;
            }
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
