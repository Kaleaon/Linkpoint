package com.lumiyaviewer.lumiya.slproto;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.util.List;

public class SLURL implements Parcelable {
    public static final Parcelable.Creator<SLURL> CREATOR = new Parcelable.Creator<SLURL>() {
        public SLURL createFromParcel(Parcel parcel) {
            return new SLURL(parcel, (SLURL) null);
        }

        public SLURL[] newArray(int i) {
            return new SLURL[i];
        }
    };
    private String locationName;
    private int locationX;
    private int locationY;
    private int locationZ;

    public SLURL(Intent intent) throws Exception {
        List<String> pathSegments;
        this.locationX = 128;
        this.locationY = 128;
        this.locationZ = 0;
        Uri data = intent.getData();
        if (!(data == null || data.getScheme() == null || data.getHost() == null)) {
            if (data.getScheme().equalsIgnoreCase("http")) {
                if (data.getHost().equalsIgnoreCase("maps.secondlife.com") && (pathSegments = data.getPathSegments()) != null && pathSegments.size() >= 2 && pathSegments.get(0).equalsIgnoreCase("secondlife")) {
                    this.locationName = pathSegments.get(1);
                    if (!this.locationName.equals("")) {
                        if (pathSegments.size() >= 3) {
                            this.locationX = Integer.parseInt(pathSegments.get(2));
                        }
                        if (pathSegments.size() >= 4) {
                            this.locationY = Integer.parseInt(pathSegments.get(3));
                        }
                        if (pathSegments.size() >= 5) {
                            this.locationZ = Integer.parseInt(pathSegments.get(4));
                        }
                    } else {
                        this.locationName = null;
                    }
                }
            } else if (data.getScheme().equalsIgnoreCase("secondlife")) {
                this.locationName = data.getHost();
                List<String> pathSegments2 = data.getPathSegments();
                if (pathSegments2 != null) {
                    if (pathSegments2.size() >= 1) {
                        this.locationX = Integer.parseInt(pathSegments2.get(0));
                    }
                    if (pathSegments2.size() >= 2) {
                        this.locationY = Integer.parseInt(pathSegments2.get(1));
                    }
                    if (pathSegments2.size() >= 3) {
                        this.locationZ = Integer.parseInt(pathSegments2.get(2));
                    }
                }
            }
        }
        if (this.locationName == null) {
            throw new Exception("No SLURL data in the given intent");
        }
    }

    private SLURL(Parcel parcel) {
        this.locationX = 128;
        this.locationY = 128;
        this.locationZ = 0;
        this.locationName = parcel.readString();
        this.locationX = parcel.readInt();
        this.locationY = parcel.readInt();
        this.locationZ = parcel.readInt();
    }

    /* synthetic */ SLURL(Parcel parcel, SLURL slurl) {
        this(parcel);
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

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.locationName);
        parcel.writeInt(this.locationX);
        parcel.writeInt(this.locationY);
        parcel.writeInt(this.locationZ);
    }
}
