// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import android.os.Parcel;
import javax.annotation.Nonnull;
import java.util.UUID;
import android.os.Parcelable$Creator;
import android.os.Parcelable;

public class AvatarPickKey implements Parcelable
{
    public static final Parcelable$Creator<AvatarPickKey> CREATOR;
    @Nonnull
    public final UUID avatarID;
    @Nonnull
    public final UUID pickID;
    
    static {
        CREATOR = (Parcelable$Creator)new Parcelable$Creator<AvatarPickKey>() {
            public AvatarPickKey createFromParcel(final Parcel parcel) {
                return new AvatarPickKey(parcel);
            }
            
            public AvatarPickKey[] newArray(final int n) {
                return new AvatarPickKey[n];
            }
        };
    }
    
    protected AvatarPickKey(final Parcel parcel) {
        this.avatarID = UUID.fromString(parcel.readString());
        this.pickID = UUID.fromString(parcel.readString());
    }
    
    public AvatarPickKey(@Nonnull final UUID avatarID, @Nonnull final UUID pickID) {
        this.avatarID = avatarID;
        this.pickID = pickID;
    }
    
    public int describeContents() {
        return 0;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final AvatarPickKey avatarPickKey = (AvatarPickKey)o;
        return this.avatarID.equals(avatarPickKey.avatarID) && this.pickID.equals(avatarPickKey.pickID);
    }
    
    @Override
    public int hashCode() {
        return this.avatarID.hashCode() * 31 + this.pickID.hashCode();
    }
    
    @Override
    public String toString() {
        return "AvatarPicksKey{avatarID=" + this.avatarID + ", pickID=" + this.pickID + '}';
    }
    
    public void writeToParcel(final Parcel parcel, final int n) {
        parcel.writeString(this.avatarID.toString());
        parcel.writeString(this.pickID.toString());
    }
}
