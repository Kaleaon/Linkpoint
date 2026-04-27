// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.UUID;

public class AvatarPickKey
    implements Parcelable
{

    public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public AvatarPickKey createFromParcel(Parcel parcel)
        {
            return new AvatarPickKey(parcel);
        }

        public volatile Object createFromParcel(Parcel parcel)
        {
            return createFromParcel(parcel);
        }

        public AvatarPickKey[] newArray(int i)
        {
            return new AvatarPickKey[i];
        }

        public volatile Object[] newArray(int i)
        {
            return newArray(i);
        }

    };
    public final UUID avatarID;
    public final UUID pickID;

    protected AvatarPickKey(Parcel parcel)
    {
        avatarID = UUID.fromString(parcel.readString());
        pickID = UUID.fromString(parcel.readString());
    }

    public AvatarPickKey(UUID uuid, UUID uuid1)
    {
        avatarID = uuid;
        pickID = uuid1;
    }

    public int describeContents()
    {
        return 0;
    }

    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        obj = (AvatarPickKey)obj;
        if (!avatarID.equals(((AvatarPickKey) (obj)).avatarID))
        {
            return false;
        } else
        {
            return pickID.equals(((AvatarPickKey) (obj)).pickID);
        }
    }

    public int hashCode()
    {
        return avatarID.hashCode() * 31 + pickID.hashCode();
    }

    public String toString()
    {
        return (new StringBuilder()).append("AvatarPicksKey{avatarID=").append(avatarID).append(", pickID=").append(pickID).append('}').toString();
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(avatarID.toString());
        parcel.writeString(pickID.toString());
    }

}
