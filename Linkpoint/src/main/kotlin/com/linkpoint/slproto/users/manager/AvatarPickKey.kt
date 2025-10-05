package com.linkpoint.slproto.users.manager

import android.os.Parcel
import android.os.Parcelable
import java.util.UUID
import javax.annotation.Nonnull

class AvatarPickKey : Parcelable {
    const val Parcelable.Creator<AvatarPickKey> CREATOR = Parcelable.Creator<AvatarPickKey>() {
        public AvatarPickKey createFromParcel(Parcel parcel) {
            return AvatarPickKey(parcel)
        }

        public AvatarPickKey[] newArray(Int i) {
            return AvatarPickKey[i]
        }
    }
    val UUID avatarID
    val UUID pickID

    protected AvatarPickKey(Parcel parcel) {
        this.avatarID = UUID.fromString(parcel.readString())
        this.pickID = UUID.fromString(parcel.readString())
    }

    public AvatarPickKey(UUID uuid, UUID uuid2) {
        this.avatarID = uuid
        this.pickID = uuid2
    }

    public Int describeContents() {
        return 0
    }

    public Boolean equals(Object obj) {
        if (this == obj) {
            return true
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false
        }
        AvatarPickKey avatarPickKey = (AvatarPickKey) obj
        if (!this.avatarID.equals(avatarPickKey.avatarID)) {
            return false
        }
        return this.pickID.equals(avatarPickKey.pickID)
    }

    public Int hashCode() {
        return (this.avatarID.hashCode() * 31) + this.pickID.hashCode()
    }

    public String toString() {
        return "AvatarPicksKey{avatarID=" + this.avatarID + ", pickID=" + this.pickID + '}'
    }

    public Unit writeToParcel(Parcel parcel, Int i) {
        parcel.writeString(this.avatarID.toString())
        parcel.writeString(this.pickID.toString())
    }
}
