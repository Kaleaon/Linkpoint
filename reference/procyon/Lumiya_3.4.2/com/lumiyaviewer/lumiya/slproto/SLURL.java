// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto;

import android.text.TextUtils;
import java.util.List;
import android.net.Uri;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable$Creator;
import android.os.Parcelable;

public class SLURL implements Parcelable
{
    public static final Parcelable$Creator<SLURL> CREATOR;
    private String locationName;
    private int locationX;
    private int locationY;
    private int locationZ;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<SLURL>() {
            public SLURL createFromParcel(final Parcel parcel) {
                return new SLURL(parcel, null);
            }
            
            public SLURL[] newArray(final int n) {
                return new SLURL[n];
            }
        };
    }
    
    public SLURL(final Intent intent) throws Exception {
        this.locationX = 128;
        this.locationY = 128;
        this.locationZ = 0;
        final Uri data = intent.getData();
        if (data != null && data.getScheme() != null && data.getHost() != null) {
            if (data.getScheme().equalsIgnoreCase("http")) {
                if (data.getHost().equalsIgnoreCase("maps.secondlife.com")) {
                    final List pathSegments = data.getPathSegments();
                    if (pathSegments != null && pathSegments.size() >= 2 && ((String)pathSegments.get(0)).equalsIgnoreCase("secondlife")) {
                        this.locationName = (String)pathSegments.get(1);
                        if (!this.locationName.equals("")) {
                            if (pathSegments.size() >= 3) {
                                this.locationX = Integer.parseInt((String)pathSegments.get(2));
                            }
                            if (pathSegments.size() >= 4) {
                                this.locationY = Integer.parseInt((String)pathSegments.get(3));
                            }
                            if (pathSegments.size() >= 5) {
                                this.locationZ = Integer.parseInt((String)pathSegments.get(4));
                            }
                        }
                        else {
                            this.locationName = null;
                        }
                    }
                }
            }
            else if (data.getScheme().equalsIgnoreCase("secondlife")) {
                this.locationName = data.getHost();
                final List pathSegments2 = data.getPathSegments();
                if (pathSegments2 != null) {
                    if (pathSegments2.size() >= 1) {
                        this.locationX = Integer.parseInt((String)pathSegments2.get(0));
                    }
                    if (pathSegments2.size() >= 2) {
                        this.locationY = Integer.parseInt((String)pathSegments2.get(1));
                    }
                    if (pathSegments2.size() >= 3) {
                        this.locationZ = Integer.parseInt((String)pathSegments2.get(2));
                    }
                }
            }
        }
        if (this.locationName == null) {
            throw new Exception("No SLURL data in the given intent");
        }
    }
    
    private SLURL(final Parcel parcel) {
        this.locationX = 128;
        this.locationY = 128;
        this.locationZ = 0;
        this.locationName = parcel.readString();
        this.locationX = parcel.readInt();
        this.locationY = parcel.readInt();
        this.locationZ = parcel.readInt();
    }
    
    public int describeContents() {
        return 0;
    }
    
    public String getLocationName() {
        return this.locationName;
    }
    
    public int getLocationX() {
        return this.locationX;
    }
    
    public int getLocationY() {
        return this.locationY;
    }
    
    public int getLocationZ() {
        return this.locationZ;
    }
    
    public String getLoginStartLocation() {
        return "uri:" + TextUtils.htmlEncode(this.locationName) + "&amp;" + Integer.toString(this.locationX) + "&amp;" + Integer.toString(this.locationY) + "&amp;" + Integer.toString(this.locationZ);
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        parcel.writeString(this.locationName);
        parcel.writeInt(this.locationX);
        parcel.writeInt(this.locationY);
        parcel.writeInt(this.locationZ);
    }
}
