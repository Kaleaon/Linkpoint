// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users;

import android.os.Bundle;
import android.os.Parcel;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users:
//            ChatterID

private static abstract class <init> extends ChatterID
{

    protected final UUID uuid;

    public int compareTo(ChatterID chatterid)
    {
        int i = super.compareTo(chatterid);
        if (i != 0)
        {
            return i;
        }
        if (chatterid instanceof <init>)
        {
            return uuid.compareTo(((uuid)chatterid).uuid);
        } else
        {
            return 0;
        }
    }

    public boolean equals(Object obj)
    {
        if (super.equals(obj) && (obj instanceof uuid))
        {
            return Objects.equal(uuid, ((uuid)obj).uuid);
        } else
        {
            return false;
        }
    }

    public UUID getChatterUUID()
    {
        return uuid;
    }

    public UUID getOptionalChatterUUID()
    {
        return uuid;
    }

    public int hashCode()
    {
        int j = super.hashCode();
        int i;
        if (uuid != null)
        {
            i = uuid.hashCode();
        } else
        {
            i = 0;
        }
        return i + j;
    }

    public boolean isValidUUID()
    {
        if (uuid != null)
        {
            return UUIDPool.ZeroUUID.equals(uuid) ^ true;
        } else
        {
            return false;
        }
    }

    public Bundle toBundle()
    {
        Bundle bundle = super.toBundle();
        String s;
        if (uuid != null)
        {
            s = uuid.toString();
        } else
        {
            s = UUIDPool.ZeroUUID.toString();
        }
        bundle.putString("chatterUUID", s);
        return bundle;
    }

    public String toString()
    {
        StringBuilder stringbuilder = (new StringBuilder()).append(super.toString()).append(":");
        String s;
        if (uuid != null)
        {
            s = uuid.toString();
        } else
        {
            s = "null";
        }
        return stringbuilder.append(s).toString();
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        super.writeToParcel(parcel, i);
        if (uuid != null)
        {
            parcel.writeLong(uuid.getMostSignificantBits());
            parcel.writeLong(uuid.getLeastSignificantBits());
            return;
        } else
        {
            parcel.writeLong(0L);
            parcel.writeLong(0L);
            return;
        }
    }

    private (Parcel parcel)
    {
        super(parcel, null);
        uuid = UUIDPool.getUUID(parcel.readLong(), parcel.readLong());
    }

    uuid(Parcel parcel, uuid uuid1)
    {
        this(parcel);
    }

    private <init>(UUID uuid1, UUID uuid2)
    {
        super(uuid1, null);
        if (uuid2 == null)
        {
            uuid2 = UUIDPool.ZeroUUID;
        }
        uuid = uuid2;
    }

    uuid(UUID uuid1, UUID uuid2, uuid uuid3)
    {
        this(uuid1, uuid2);
    }
}
