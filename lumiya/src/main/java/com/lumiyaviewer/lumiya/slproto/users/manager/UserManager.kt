package com.lumiyaviewer.lumiya.slproto.users.manager

import com.lumiyaviewer.lumiya.slproto.SLParcelInfo
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectDisplayInfo
import com.lumiyaviewer.lumiya.slproto.users.ParcelData
import java.util.UUID
import java.io.Serializable

class UserManager {
    companion object {
        @JvmStatic
        fun getUserManager(uuid: UUID?): UserManager? {
            return null
        }
    }

    fun getChatterList(): ChatterList? = null
    fun getObjectsManager(): ObjectsManager? = null
    fun getCurrentLocationInfoSnapshot(): LocationInfo? = null
    fun getUserID(): UUID = UUID(0,0)

    class ChatterList {
        fun getFriendManager(): FriendManager? = null
    }

    class FriendManager {
        fun updateFriendList(friends: List<Any>) {}
    }
    
    class LocationInfo {
        val parcelData: ParcelData? = null
    }
}

class ObjectsManager {
    class ObjectDisplayList(val list: List<SLObjectDisplayInfo>, val hasMore: Boolean)

    fun clearParcelInfo(info: SLParcelInfo) {}
    fun setParcelInfo(info: SLParcelInfo) {}
}
