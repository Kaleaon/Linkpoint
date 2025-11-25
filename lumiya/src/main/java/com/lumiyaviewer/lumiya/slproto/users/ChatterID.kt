package com.lumiyaviewer.lumiya.slproto.users

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import com.google.common.base.Objects
import com.lumiyaviewer.lumiya.dao.Chatter
import com.lumiyaviewer.lumiya.react.Subscription
import com.lumiyaviewer.lumiya.react.UIThreadExecutor
import com.lumiyaviewer.lumiya.slproto.GridConnectionManager
import com.lumiyaviewer.lumiya.slproto.SLGridConnection
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.ui.settings.NotificationType
import com.lumiyaviewer.lumiya.utils.UUIDPool
import java.util.UUID
import java.util.concurrent.Executor

abstract class ChatterID(val agentUUID: UUID) : Parcelable, Comparable<ChatterID> {

    enum class ChatterType(val notificationType: NotificationType) {
        Local(NotificationType.LocalChat),
        User(NotificationType.Private),
        Group(NotificationType.Group)
    }

    interface OnChatterPictureIDListener {
        fun onChatterPictureID(uuid: UUID)
    }

    abstract fun getChatterType(): ChatterType

    open fun getOptionalChatterUUID(): UUID? = null

    open fun getPictureID(
        userManager: UserManager,
        executor: Executor?,
        listener: OnChatterPictureIDListener
    ): Subscription? = null

    fun getConnection(): SLGridConnection? = GridConnectionManager.getConnection(agentUUID)

    fun getUserManager(): UserManager? = UserManager.getUserManager(agentUUID)

    override fun compareTo(other: ChatterID): Int {
        val cmp = agentUUID.compareTo(other.agentUUID)
        return if (cmp != 0) cmp else getChatterType().compareTo(other.getChatterType())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChatterID) return false
        return agentUUID == other.agentUUID && getChatterType() == other.getChatterType()
    }

    override fun hashCode(): Int {
        return agentUUID.hashCode() + 1 + getChatterType().ordinal
    }

    open fun isValidUUID(): Boolean = false

    open fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putInt("chatterType", getChatterType().ordinal)
        bundle.putString("chatterAgentUUID", agentUUID.toString())
        return bundle
    }

    fun toDatabaseObject(chatter: Chatter) {
        chatter.type = getChatterType().ordinal
        chatter.uuid = getOptionalChatterUUID()
    }

    override fun toString(): String {
        return "Chatter:${getChatterType()}"
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(agentUUID.mostSignificantBits)
        dest.writeLong(agentUUID.leastSignificantBits)
    }

    override fun describeContents(): Int = 0

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<ChatterID> {
            override fun createFromParcel(source: Parcel): ChatterID? {
                // Abstract class, cannot instantiate directly. 
                // Need to read type first or use specific creators?
                // Based on decompiled code, it seems to read generic fields. 
                // But typical Parcelable implementation requires specific class.
                // Since this is abstract, usually subclasses implement CREATOR.
                // The decompiled code had abstract class implementing Parcelable but no CREATOR in it, 
                // subclasses had CREATOR.
                return null 
            }

            override fun newArray(size: Int): Array<ChatterID?> {
                return arrayOfNulls(size)
            }
        }

        fun fromBundle(bundle: Bundle): ChatterID? {
            val agentUUID = UUID.fromString(bundle.getString("chatterAgentUUID"))
            val typeIdx = bundle.getInt("chatterType", 0)
            val chatterUUIDStr = bundle.getString("chatterUUID")
            val chatterUUID = if (chatterUUIDStr != null) UUID.fromString(chatterUUIDStr) else null

            return when (ChatterType.values()[typeIdx]) {
                ChatterType.Group -> if (chatterUUID != null) getGroupChatterID(agentUUID, chatterUUID) else null
                ChatterType.Local -> getLocalChatterID(agentUUID)
                ChatterType.User -> if (chatterUUID != null) getUserChatterID(agentUUID, chatterUUID) else null
            }
        }

        fun fromDatabaseObject(agentUUID: UUID, chatter: Chatter): ChatterID? {
            return when (ChatterType.values()[chatter.type]) {
                ChatterType.Group -> {
                    val uuid = chatter.uuid ?: return null
                    getGroupChatterID(agentUUID, uuid)
                }
                ChatterType.Local -> getLocalChatterID(agentUUID)
                ChatterType.User -> {
                    val uuid = chatter.uuid ?: return null
                    getUserChatterID(agentUUID, uuid)
                }
            }
        }

        @JvmStatic
        fun getGroupChatterID(agentUUID: UUID, groupUUID: UUID): ChatterID = ChatterIDGroup(agentUUID, groupUUID)

        @JvmStatic
        fun getLocalChatterID(agentUUID: UUID): ChatterID = ChatterIDLocal(agentUUID)

        @JvmStatic
        fun getUserChatterID(agentUUID: UUID, userUUID: UUID): ChatterID = ChatterIDUser(agentUUID, userUUID)
    }

    private abstract class ChatterIDWithUUID(agentUUID: UUID, val uuid: UUID) : ChatterID(agentUUID) {
        override fun getChatterUUID(): UUID = uuid
        override fun getOptionalChatterUUID(): UUID? = uuid

        override fun compareTo(other: ChatterID): Int {
            val cmp = super.compareTo(other)
            if (cmp != 0) return cmp
            return if (other is ChatterIDWithUUID) uuid.compareTo(other.uuid) else 0
        }

        override fun equals(other: Any?): Boolean {
            if (!super.equals(other)) return false
            if (other !is ChatterIDWithUUID) return false
            return uuid == other.uuid
        }

        override fun hashCode(): Int = uuid.hashCode() + super.hashCode()

        override fun isValidUUID(): Boolean = uuid != UUIDPool.ZeroUUID

        override fun toBundle(): Bundle {
            val bundle = super.toBundle()
            bundle.putString("chatterUUID", uuid.toString())
            return bundle
        }

        override fun toString(): String {
            return "${super.toString()}:$uuid"
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeLong(uuid.mostSignificantBits)
            dest.writeLong(uuid.leastSignificantBits)
        }
        
        abstract fun getChatterUUID(): UUID
    }

    class ChatterIDGroup(agentUUID: UUID, groupUUID: UUID) : ChatterIDWithUUID(agentUUID, groupUUID) {
        constructor(parcel: Parcel) : this(
            UUIDPool.getUUID(parcel.readLong(), parcel.readLong()),
            UUIDPool.getUUID(parcel.readLong(), parcel.readLong())
        )

        override fun getChatterType(): ChatterType = ChatterType.Group

        override fun getPictureID(
            userManager: UserManager,
            executor: Executor?,
            listener: OnChatterPictureIDListener
        ): Subscription {
            return userManager.getCachedGroupProfiles().pool.subscribe(
                uuid,
                UIThreadExecutor.getInstance()
            ) { data ->
                // Assuming data has picture ID logic
                // listener.onChatterPictureID(...)
            }
        }

        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<ChatterIDGroup> {
                override fun createFromParcel(source: Parcel): ChatterIDGroup = ChatterIDGroup(source)
                override fun newArray(size: Int): Array<ChatterIDGroup?> = arrayOfNulls(size)
            }
        }
    }

    class ChatterIDLocal(agentUUID: UUID) : ChatterID(agentUUID) {
        constructor(parcel: Parcel) : this(UUIDPool.getUUID(parcel.readLong(), parcel.readLong()))

        override fun getChatterType(): ChatterType = ChatterType.Local

        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<ChatterIDLocal> {
                override fun createFromParcel(source: Parcel): ChatterIDLocal = ChatterIDLocal(source)
                override fun newArray(size: Int): Array<ChatterIDLocal?> = arrayOfNulls(size)
            }
        }
    }

    class ChatterIDUser(agentUUID: UUID, userUUID: UUID) : ChatterIDWithUUID(agentUUID, userUUID) {
        constructor(parcel: Parcel) : this(
            UUIDPool.getUUID(parcel.readLong(), parcel.readLong()),
            UUIDPool.getUUID(parcel.readLong(), parcel.readLong())
        )

        override fun getChatterType(): ChatterType = ChatterType.User

        override fun getPictureID(
            userManager: UserManager,
            executor: Executor?,
            listener: OnChatterPictureIDListener
        ): Subscription {
            return userManager.getAvatarProperties().pool.subscribe(
                uuid,
                executor
            ) { data ->
                // listener.onChatterPictureID(...)
            }
        }

        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<ChatterIDUser> {
                override fun createFromParcel(source: Parcel): ChatterIDUser = ChatterIDUser(source)
                override fun newArray(size: Int): Array<ChatterIDUser?> = arrayOfNulls(size)
            }
        }
    }
}
