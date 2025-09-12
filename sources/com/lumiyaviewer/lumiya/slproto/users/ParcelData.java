// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.io.Serializable;
import java.util.UUID;

public class ParcelData
    implements Serializable
{

    private final int area;
    private final String description;
    private final boolean isGroupOwned;
    private final String mediaURL;
    private final String name;
    private final UUID ownerID;
    private final boolean parcelBitmap[] = new boolean[4096];
    private final int parcelID;
    private final UUID snapshotUUID;

    public ParcelData(LLSDNode llsdnode)
        throws LLSDException
    {
        Object obj = null;
        super();
        parcelID = llsdnode.byKey("LocalID").asInt();
        name = llsdnode.byKey("Name").asString();
        description = llsdnode.byKey("Desc").asString();
        mediaURL = llsdnode.byKey("MusicURL").asString();
        UUID uuid1 = llsdnode.byKey("SnapshotID").asUUID();
        UUID uuid = uuid1;
        if (uuid1 != null)
        {
            uuid = uuid1;
            if (uuid1.equals(UUIDPool.ZeroUUID))
            {
                uuid = null;
            }
        }
        snapshotUUID = uuid;
        uuid = obj;
        if (llsdnode.keyExists("OwnerID"))
        {
            uuid = llsdnode.byKey("OwnerID").asUUID();
        }
        ownerID = uuid;
        int i;
        boolean flag;
        if (llsdnode.keyExists("IsGroupOwned"))
        {
            flag = llsdnode.byKey("IsGroupOwned").asBoolean();
        } else
        {
            flag = false;
        }
        isGroupOwned = flag;
        if (llsdnode.keyExists("Area"))
        {
            i = llsdnode.byKey("Area").asInt();
        } else
        {
            i = 0;
        }
        area = i;
        llsdnode = llsdnode.byKey("Bitmap").asBinary();
        for (i = 0; i < llsdnode.length && i < 512; i++)
        {
            byte byte0 = llsdnode[i];
            for (int j = 0; j < 8; j++)
            {
                if ((byte0 & 1) != 0)
                {
                    parcelBitmap[i * 8 + j] = true;
                }
                byte0 >>= 1;
            }

        }

    }

    public int getArea()
    {
        return area;
    }

    public String getDescription()
    {
        return description;
    }

    public String getMediaURL()
    {
        return mediaURL;
    }

    public String getName()
    {
        return name;
    }

    public UUID getOwnerID()
    {
        return ownerID;
    }

    public boolean[] getParcelBitmap()
    {
        return parcelBitmap;
    }

    public int getParcelID()
    {
        return parcelID;
    }

    public UUID getSnapshotUUID()
    {
        return snapshotUUID;
    }

    public boolean isGroupOwned()
    {
        return isGroupOwned;
    }
}
