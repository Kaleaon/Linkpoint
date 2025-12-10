package com.linkpoint.slproto.users.manager

import android.os.Parcel
import android.os.Parcelable
import java.util.UUID
import androidx.annotation.NonNull

class AvatarPickKey : Parcelable {
    Parcelable.Creator<AvatarPickKey> CREATOR = Parcelable.Creator<AvatarPickKey>() {
        fun createFromParcel(Parcel parcel): AvatarPickKey {
            return AvatarPickKey(parcel)
        }

        AvatarPickKey[] newArray(Int i) {
            return AvatarPickKey[i]
        }
    }
    @NonNull
    UUID avatarID
    @NonNull
    UUID pickID

    protected AvatarPickKey(Parcel parcel) {
        this.avatarID = UUID.fromString(parcel.readString())
        this.pickID = UUID.fromString(parcel.readString())
    }

    AvatarPickKey(@NonNull UUID uuid, @NonNull UUID uuid2) {
        this.avatarID = uuid
        this.pickID = uuid2
    }

    fun describeContents(): Int {
        return 0
    }

    fun equals(Any obj): Boolean {
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

    fun hashCode(): Int {
        return (this.avatarID.hashCode() * 31) + this.pickID.hashCode()
    }

    fun toString(): String {
        return "AvatarPicksKey{avatarID=" + this.avatarID + ", pickID=" + this.pickID + '}'
    }

    fun writeToParcel(Parcel parcel, Int i): Unit {
        parcel.writeString(this.avatarID.toString())
        parcel.writeString(this.pickID.toString())
    }
}
