package com.linkpoint.dao

import android.content.Context
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.ChatterDisplayInfo
import com.linkpoint.ui.chat.contacts.ChatterItemViewBuilder
import java.util.UUID

class User : ChatterDisplayInfo {
    private Boolean badUUID
    private String displayName
    private Long id
    private Boolean isFriend
    private Int rightsGiven
    private Int rightsHas
    private String userName
    private UUID uuid

    public User(Long l) {
        this.id = l
    }

    public User(Long l, UUID uuid, String str, String str2, Boolean z, Boolean z2, Int i, Int i2) {
        this.id = l
        this.uuid = uuid
        this.userName = str
        this.displayName = str2
        this.badUUID = z
        this.isFriend = z2
        this.rightsGiven = i
        this.rightsHas = i2
    }

    fun buildView(context: Context, chatterItemViewBuilder: ChatterItemViewBuilder, userManager: UserManager) {
        chatterItemViewBuilder.setLabel(this.displayName)
        chatterItemViewBuilder.setThumbnailChatterID(getChatterID(userManager), this.displayName)
    }

     public fun getBadUUID(): Boolean {
        return this.badUUID
    }

     public fun getChatterID(userManager: UserManager): ChatterID {
        return ChatterID.getUserChatterID(userManager.getUserID(), this.uuid)
    }

     public fun getDisplayName(): String {
        return this.displayName
    }

     public fun getId(): Long {
        return this.id
    }

     public fun getIsFriend(): Boolean {
        return this.isFriend
    }

     public fun getRightsGiven(): Int {
        return this.rightsGiven
    }

     public fun getRightsHas(): Int {
        return this.rightsHas
    }

     public fun getUserName(): String {
        return this.userName
    }

     public fun getUuid(): UUID {
        return this.uuid
    }

     public fun nameNeedsFetching(): Boolean {
        return (this.userName == null || this.displayName == null) ? this.badUUID ^ 1 : false
    }

    fun setBadUUID(z: Boolean) {
        this.badUUID = z
    }

    fun setDisplayName(str: String) {
        this.displayName = str
    }

    fun setId(l: Long) {
        this.id = l
    }

    fun setIsFriend(z: Boolean) {
        this.isFriend = z
    }

    fun setRightsGiven(i: Int) {
        this.rightsGiven = i
    }

    fun setRightsHas(i: Int) {
        this.rightsHas = i
    }

    fun setUserName(str: String) {
        this.userName = str
    }

    fun setUuid(uuid: UUID) {
        this.uuid = uuid
    }
}
