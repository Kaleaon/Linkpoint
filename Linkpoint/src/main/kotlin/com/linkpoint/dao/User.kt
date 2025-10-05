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

    public Unit buildView(Context context, ChatterItemViewBuilder chatterItemViewBuilder, UserManager userManager) {
        chatterItemViewBuilder.setLabel(this.displayName)
        chatterItemViewBuilder.setThumbnailChatterID(getChatterID(userManager), this.displayName)
    }

    public Boolean getBadUUID() {
        return this.badUUID
    }

    public ChatterID getChatterID(UserManager userManager) {
        return ChatterID.getUserChatterID(userManager.getUserID(), this.uuid)
    }

    public String getDisplayName() {
        return this.displayName
    }

    public Long getId() {
        return this.id
    }

    public Boolean getIsFriend() {
        return this.isFriend
    }

    public Int getRightsGiven() {
        return this.rightsGiven
    }

    public Int getRightsHas() {
        return this.rightsHas
    }

    public String getUserName() {
        return this.userName
    }

    public UUID getUuid() {
        return this.uuid
    }

    public Boolean nameNeedsFetching() {
        return (this.userName == null || this.displayName == null) ? this.badUUID ^ 1 : false
    }

    public Unit setBadUUID(Boolean z) {
        this.badUUID = z
    }

    public Unit setDisplayName(String str) {
        this.displayName = str
    }

    public Unit setId(Long l) {
        this.id = l
    }

    public Unit setIsFriend(Boolean z) {
        this.isFriend = z
    }

    public Unit setRightsGiven(Int i) {
        this.rightsGiven = i
    }

    public Unit setRightsHas(Int i) {
        this.rightsHas = i
    }

    public Unit setUserName(String str) {
        this.userName = str
    }

    public Unit setUuid(UUID uuid) {
        this.uuid = uuid
    }
}
