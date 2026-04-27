// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users;

import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.UserName;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.react.Subscription;
import java.lang.ref.WeakReference;
import javax.annotation.Nullable;
import java.util.concurrent.Executor;

public class ChatterNameRetriever
{
    public final ChatterID chatterID;
    @Nullable
    private final Executor executor;
    private final WeakReference<OnChatterNameUpdated> listener;
    private volatile String resolvedName;
    private volatile String resolvedSecondaryName;
    private Subscription subscription;
    
    public ChatterNameRetriever(final ChatterID chatterID, final OnChatterNameUpdated referent, @Nullable final Executor executor) {
        this.subscription = null;
        this.chatterID = chatterID;
        this.listener = new WeakReference<OnChatterNameUpdated>(referent);
        this.executor = executor;
        this.subscribe();
    }
    
    public ChatterNameRetriever(final ChatterID chatterID, final OnChatterNameUpdated referent, @Nullable final Executor executor, final boolean b) {
        this.subscription = null;
        this.chatterID = chatterID;
        this.listener = new WeakReference<OnChatterNameUpdated>(referent);
        this.executor = executor;
        if (b) {
            this.subscribe();
        }
    }
    
    private void onCurrentLocation(final CurrentLocationInfo currentLocationInfo) {
        final String s = null;
        final ParcelData parcelData = currentLocationInfo.parcelData();
        String name = s;
        if (parcelData != null) {
            name = parcelData.getName();
        }
        if (!Objects.equal(this.resolvedName, name)) {
            this.resolvedName = name;
            final OnChatterNameUpdated onChatterNameUpdated = this.listener.get();
            if (onChatterNameUpdated != null) {
                onChatterNameUpdated.onChatterNameUpdated(this);
            }
        }
    }
    
    private void onGroupProfile(final GroupProfileReply groupProfileReply) {
        this.resolvedName = SLMessage.stringFromVariableOEM(groupProfileReply.GroupData_Field.Name);
        this.resolvedSecondaryName = SLMessage.stringFromVariableOEM(groupProfileReply.GroupData_Field.Name);
        final OnChatterNameUpdated onChatterNameUpdated = this.listener.get();
        if (onChatterNameUpdated != null) {
            onChatterNameUpdated.onChatterNameUpdated(this);
        }
    }
    
    private void onUserName(final UserName userName) {
        Debug.Printf("Resolved name for %s", userName.getUuid());
        if (GlobalOptions.getInstance().isLegacyUserNames()) {
            this.resolvedName = userName.getUserName();
            this.resolvedSecondaryName = userName.getDisplayName();
        }
        else {
            this.resolvedName = userName.getDisplayName();
            this.resolvedSecondaryName = userName.getUserName();
        }
        final OnChatterNameUpdated onChatterNameUpdated = this.listener.get();
        if (onChatterNameUpdated != null) {
            onChatterNameUpdated.onChatterNameUpdated(this);
        }
    }
    
    public void dispose() {
        if (this.subscription != null) {
            this.subscription.unsubscribe();
        }
    }
    
    public String getResolvedName() {
        return this.resolvedName;
    }
    
    public String getResolvedSecondaryName() {
        return this.resolvedSecondaryName;
    }
    
    public void subscribe() {
        final UserManager userManager = this.chatterID.getUserManager();
        if (userManager != null) {
            if (this.chatterID.getChatterType() == ChatterID.ChatterType.Local) {
                this.subscription = userManager.getCurrentLocationInfo().subscribe(SubscriptionSingleDataPool.getSingleDataKey(), this.executor, new _$Lambda$Tr7QBnh_GnHDqFtHSpMdsLw3Yfs(this));
            }
            else if (this.chatterID instanceof ChatterID.ChatterIDUser) {
                this.subscription = userManager.getUserNames().subscribe(((ChatterID.ChatterIDUser)this.chatterID).getChatterUUID(), this.executor, new _$Lambda$Tr7QBnh_GnHDqFtHSpMdsLw3Yfs$1(this));
            }
            else if (this.chatterID instanceof ChatterID.ChatterIDGroup) {
                this.subscription = userManager.getCachedGroupProfiles().getPool().subscribe(((ChatterID.ChatterIDGroup)this.chatterID).getChatterUUID(), this.executor, new _$Lambda$Tr7QBnh_GnHDqFtHSpMdsLw3Yfs$2(this));
            }
            else {
                this.subscription = null;
            }
        }
        else {
            this.subscription = null;
        }
    }
    
    public interface OnChatterNameUpdated
    {
        void onChatterNameUpdated(final ChatterNameRetriever p0);
    }
}
