package com.lumiyaviewer.lumiya.dao;

import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatterItemViewBuilder;
import java.util.UUID;

public class User implements ChatterDisplayInfo {
    private boolean badUUID;
    private String displayName;
    private Long id;
    private boolean isFriend;
    private int rightsGiven;
    private int rightsHas;
    private String userName;
    private UUID uuid;

    public User(Long l) {
        this.id = l;
    }

    public User(Long l, UUID uuid, String str, String str2, boolean z, boolean z2, int i, int i2) {
        this.id = l;
        this.uuid = uuid;
        this.userName = str;
        this.displayName = str2;
        this.badUUID = z;
        this.isFriend = z2;
        this.rightsGiven = i;
        this.rightsHas = i2;
    }

    public void buildView(Context context, ChatterItemViewBuilder chatterItemViewBuilder, UserManager userManager) {
        chatterItemViewBuilder.setLabel(this.displayName);
        chatterItemViewBuilder.setThumbnailChatterID(getChatterID(userManager), this.displayName);
    }

    public boolean getBadUUID() {
        return this.badUUID;
    }

    public ChatterID getChatterID(UserManager userManager) {
        return ChatterID.getUserChatterID(userManager.getUserID(), this.uuid);
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Long getId() {
        return this.id;
    }

    public boolean getIsFriend() {
        return this.isFriend;
    }

    public int getRightsGiven() {
        return this.rightsGiven;
    }

    public int getRightsHas() {
        return this.rightsHas;
    }

    public String getUserName() {
        return this.userName;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public boolean nameNeedsFetching() {
        return (this.userName == null || this.displayName == null) ? this.badUUID ^ 1 : false;
    }

    public void setBadUUID(boolean z) {
        this.badUUID = z;
    }

    public void setDisplayName(String str) {
        this.displayName = str;
    }

    public void setId(Long l) {
        this.id = l;
    }

    public void setIsFriend(boolean z) {
        this.isFriend = z;
    }

    public void setRightsGiven(int i) {
        this.rightsGiven = i;
    }

    public void setRightsHas(int i) {
        this.rightsHas = i;
    }

    public void setUserName(String str) {
        this.userName = str;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
