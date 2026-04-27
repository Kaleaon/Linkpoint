// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.util.List;

public class SLURL
    implements Parcelable
{

    public static final android.os.Parcelable.Creator CREATOR = new android.os.Parcelable.Creator() {

        public SLURL createFromParcel(Parcel parcel)
        {
            return new SLURL(parcel, null);
        }

        public volatile Object createFromParcel(Parcel parcel)
        {
            return createFromParcel(parcel);
        }

        public SLURL[] newArray(int i)
        {
            return new SLURL[i];
        }

        public volatile Object[] newArray(int i)
        {
            return newArray(i);
        }

    };
    private String locationName;
    private int locationX;
    private int locationY;
    private int locationZ;

    public SLURL(Intent intent)
        throws Exception
    {
        locationX = 128;
        locationY = 128;
        locationZ = 0;
        intent = intent.getData();
        if (intent != null && intent.getScheme() != null && intent.getHost() != null)
        {
            if (intent.getScheme().equalsIgnoreCase("http"))
            {
                if (intent.getHost().equalsIgnoreCase("maps.secondlife.com"))
                {
                    intent = intent.getPathSegments();
                    if (intent != null && intent.size() >= 2 && ((String)intent.get(0)).equalsIgnoreCase("secondlife"))
                    {
                        locationName = (String)intent.get(1);
                        if (!locationName.equals(""))
                        {
                            if (intent.size() >= 3)
                            {
                                locationX = Integer.parseInt((String)intent.get(2));
                            }
                            if (intent.size() >= 4)
                            {
                                locationY = Integer.parseInt((String)intent.get(3));
                            }
                            if (intent.size() >= 5)
                            {
                                locationZ = Integer.parseInt((String)intent.get(4));
                            }
                        } else
                        {
                            locationName = null;
                        }
                    }
                }
            } else
            if (intent.getScheme().equalsIgnoreCase("secondlife"))
            {
                locationName = intent.getHost();
                intent = intent.getPathSegments();
                if (intent != null)
                {
                    if (intent.size() >= 1)
                    {
                        locationX = Integer.parseInt((String)intent.get(0));
                    }
                    if (intent.size() >= 2)
                    {
                        locationY = Integer.parseInt((String)intent.get(1));
                    }
                    if (intent.size() >= 3)
                    {
                        locationZ = Integer.parseInt((String)intent.get(2));
                    }
                }
            }
        }
        if (locationName == null)
        {
            throw new Exception("No SLURL data in the given intent");
        } else
        {
            return;
        }
    }

    private SLURL(Parcel parcel)
    {
        locationX = 128;
        locationY = 128;
        locationZ = 0;
        locationName = parcel.readString();
        locationX = parcel.readInt();
        locationY = parcel.readInt();
        locationZ = parcel.readInt();
    }

    SLURL(Parcel parcel, SLURL slurl)
    {
        this(parcel);
    }

    public int describeContents()
    {
        return 0;
    }

    public String getLocationName()
    {
        return locationName;
    }

    public int getLocationX()
    {
        return locationX;
    }

    public int getLocationY()
    {
        return locationY;
    }

    public int getLocationZ()
    {
        return locationZ;
    }

    public String getLoginStartLocation()
    {
        return (new StringBuilder()).append("uri:").append(TextUtils.htmlEncode(locationName)).append("&amp;").append(Integer.toString(locationX)).append("&amp;").append(Integer.toString(locationY)).append("&amp;").append(Integer.toString(locationZ)).toString();
    }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(locationName);
        parcel.writeInt(locationX);
        parcel.writeInt(locationY);
        parcel.writeInt(locationZ);
    }

}
