package com.linkpoint.slproto.users.manager

import android.os.Parcel
import android.os.Parcelable
import java.util.UUID
import javax.annotation.Nonnull

class AvatarPickKey : Parcelable {
    const val Parcelable.Creator<AvatarPickKey> CREATOR = Parcelable.Creator<AvatarPickKey>() {
         public fun createFromParcel(parcel: Parcel): AvatarPickKey {
            return AvatarPickKey(parcel)
        }

        public Array<AvatarPickKey> newArray(Int i) {
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

     public fun describeContents(): Int {
        return 0
    }

     public fun equals(obj: Object): Boolean {
        if (this == obj) {
            return true
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false
        }
        val avatarPickKey: AvatarPickKey = (AvatarPickKey) obj
        if (!this.avatarID.equals(avatarPickKey.avatarID)) {
            return false
        }
        return this.pickID.equals(avatarPickKey.pickID)
    }

     public fun hashCode(): Int {
        return (this.avatarID.hashCode() * 31) + this.pickID.hashCode()
    }

     public fun toString(): String {
        return "AvatarPicksKey{avatarID=" + this.avatarID + ", pickID=" + this.pickID + '}'
    }

    fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(this.avatarID.toString())
        parcel.writeString(this.pickID.toString())
    }
}
