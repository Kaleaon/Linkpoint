package com.lumiyaviewer.lumiya.slproto

import android.content.Intent
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils

class SLURL : Parcelable {
    private var locationName: String? = null
    private var locationX: Int = 128
    private var locationY: Int = 128
    private var locationZ: Int = 0

    @Throws(Exception::class)
    constructor(intent: Intent) {
        val data: Uri? = intent.data
        if (data != null && data.scheme != null && data.host != null) {
            if (data.scheme.equals("http", ignoreCase = true)) {
                val segments = data.pathSegments
                if (data.host.equals("maps.secondlife.com", ignoreCase = true) &&
                    segments != null && segments.size >= 2 &&
                    segments[0].equals("secondlife", ignoreCase = true)
                ) {
                    locationName = segments[1]
                    if (locationName == "") {
                        locationName = null
                    } else {
                        if (segments.size >= 3) {
                            locationX = segments[2].toInt()
                        }
                        if (segments.size >= 4) {
                            locationY = segments[3].toInt()
                        }
                        if (segments.size >= 5) {
                            locationZ = segments[4].toInt()
                        }
                    }
                }
            } else if (data.scheme.equals("secondlife", ignoreCase = true)) {
                locationName = data.host
                val segments = data.pathSegments
                if (segments != null) {
                    if (segments.size >= 1) {
                        locationX = segments[0].toInt()
                    }
                    if (segments.size >= 2) {
                        locationY = segments[1].toInt()
                    }
                    if (segments.size >= 3) {
                        locationZ = segments[2].toInt()
                    }
                }
            }
        }
        if (locationName == null) {
            throw Exception("No SLURL data in the given intent")
        }
    }

    private constructor(parcel: Parcel) {
        locationName = parcel.readString()
        locationX = parcel.readInt()
        locationY = parcel.readInt()
        locationZ = parcel.readInt()
    }

    override fun describeContents(): Int = 0

    fun getLocationName(): String? = locationName

    fun getLocationX(): Int = locationX

    fun getLocationY(): Int = locationY

    fun getLocationZ(): Int = locationZ

    fun getLoginStartLocation(): String {
        val name = TextUtils.htmlEncode(locationName)
        return "uri:$name&amp;${locationX}&amp;${locationY}&amp;${locationZ}"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(locationName)
        parcel.writeInt(locationX)
        parcel.writeInt(locationY)
        parcel.writeInt(locationZ)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SLURL> = object : Parcelable.Creator<SLURL> {
            override fun createFromParcel(parcel: Parcel): SLURL = SLURL(parcel)
            override fun newArray(size: Int): Array<SLURL?> = arrayOfNulls(size)
        }
    }
}
