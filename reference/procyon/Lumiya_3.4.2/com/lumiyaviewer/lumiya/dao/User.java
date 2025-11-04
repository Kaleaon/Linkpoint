// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatterItemViewBuilder;
import android.content.Context;
import java.util.UUID;
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;

public class User implements ChatterDisplayInfo
{
    private boolean badUUID;
    private String displayName;
    private Long id;
    private boolean isFriend;
    private int rightsGiven;
    private int rightsHas;
    private String userName;
    private UUID uuid;
    
    public User() {
    }
    
    public User(final Long id) {
        this.id = id;
    }
    
    public User(final Long id, final UUID uuid, final String userName, final String displayName, final boolean badUUID, final boolean isFriend, final int rightsGiven, final int rightsHas) {
        this.id = id;
        this.uuid = uuid;
        this.userName = userName;
        this.displayName = displayName;
        this.badUUID = badUUID;
        this.isFriend = isFriend;
        this.rightsGiven = rightsGiven;
        this.rightsHas = rightsHas;
    }
    
    @Override
    public void buildView(final Context context, final ChatterItemViewBuilder chatterItemViewBuilder, final UserManager userManager) {
        chatterItemViewBuilder.setLabel(this.displayName);
        chatterItemViewBuilder.setThumbnailChatterID(this.getChatterID(userManager), this.displayName);
    }
    
    public boolean getBadUUID() {
        return this.badUUID;
    }
    
    @Override
    public ChatterID getChatterID(final UserManager userManager) {
        return ChatterID.getUserChatterID(userManager.getUserID(), this.uuid);
    }
    
    @Override
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
        return (this.userName == null || this.displayName == null) && (this.badUUID ^ true);
    }
    
    public void setBadUUID(final boolean badUUID) {
        this.badUUID = badUUID;
    }
    
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }
    
    public void setId(final Long id) {
        this.id = id;
    }
    
    public void setIsFriend(final boolean isFriend) {
        this.isFriend = isFriend;
    }
    
    public void setRightsGiven(final int rightsGiven) {
        this.rightsGiven = rightsGiven;
    }
    
    public void setRightsHas(final int rightsHas) {
        this.rightsHas = rightsHas;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public void setUuid(final UUID uuid) {
        this.uuid = uuid;
    }
}
