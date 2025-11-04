// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.primitives.Booleans;
import com.google.common.base.Strings;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatterItemViewBuilder;
import android.content.Context;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;

public class ChatterDisplayData implements ChatterDisplayInfo, Comparable<ChatterDisplayData>
{
    public final ChatterID chatterID;
    public final String displayName;
    final float distanceToUser;
    final boolean isOnline;
    @Nullable
    private final SLChatEvent lastMessage;
    private final int unreadCount;
    private final boolean voiceActive;
    
    ChatterDisplayData(final ChatterID chatterID, final String displayName, final boolean isOnline, final int unreadCount, @Nullable final SLChatEvent lastMessage, final float distanceToUser, final boolean voiceActive) {
        this.chatterID = chatterID;
        this.displayName = displayName;
        this.isOnline = isOnline;
        this.unreadCount = unreadCount;
        this.lastMessage = lastMessage;
        this.distanceToUser = distanceToUser;
        this.voiceActive = voiceActive;
    }
    
    @Override
    public void buildView(final Context context, final ChatterItemViewBuilder chatterItemViewBuilder, final UserManager userManager) {
        chatterItemViewBuilder.setLabel(this.displayName);
        chatterItemViewBuilder.setThumbnailChatterID(this.chatterID, this.displayName);
        chatterItemViewBuilder.setOnlineStatusIcon(this.isOnline, this.isOnline);
        chatterItemViewBuilder.setUnreadCount(this.unreadCount);
        chatterItemViewBuilder.setVoiceActive(this.voiceActive);
        if (this.lastMessage != null) {
            chatterItemViewBuilder.setLastMessage(this.lastMessage.getPlainTextMessage(context, userManager, true).toString());
        }
        else {
            chatterItemViewBuilder.setLastMessage(null);
        }
        chatterItemViewBuilder.setDistance(this.distanceToUser);
    }
    
    @Override
    public int compareTo(@Nonnull final ChatterDisplayData chatterDisplayData) {
        final int compare = Booleans.compare(Strings.isNullOrEmpty(this.displayName), Strings.isNullOrEmpty(chatterDisplayData.displayName));
        if (compare != 0) {
            return compare;
        }
        String displayName;
        if (this.displayName != null) {
            displayName = this.displayName;
        }
        else {
            displayName = "";
        }
        String displayName2;
        if (chatterDisplayData.displayName != null) {
            displayName2 = chatterDisplayData.displayName;
        }
        else {
            displayName2 = "";
        }
        final int compareTo = displayName.compareTo(displayName2);
        if (compareTo != 0) {
            return compareTo;
        }
        return this.chatterID.compareTo(chatterDisplayData.chatterID);
    }
    
    @Override
    public ChatterID getChatterID(final UserManager userManager) {
        return this.chatterID;
    }
    
    @Nullable
    @Override
    public String getDisplayName() {
        return this.displayName;
    }
    
    @Nonnull
    ChatterDisplayData withDisplayName(final String s) {
        return new ChatterDisplayData(this.chatterID, s, this.isOnline, this.unreadCount, this.lastMessage, this.distanceToUser, this.voiceActive);
    }
    
    @Nonnull
    ChatterDisplayData withDistanceToUser(final float n) {
        return new ChatterDisplayData(this.chatterID, this.displayName, this.isOnline, this.unreadCount, this.lastMessage, n, this.voiceActive);
    }
    
    @Nonnull
    ChatterDisplayData withOnlineStatus(final boolean b) {
        return new ChatterDisplayData(this.chatterID, this.displayName, b, this.unreadCount, this.lastMessage, this.distanceToUser, this.voiceActive);
    }
    
    @Nonnull
    ChatterDisplayData withUnreadInfo(@Nonnull final UnreadMessageInfo unreadMessageInfo) {
        return new ChatterDisplayData(this.chatterID, this.displayName, this.isOnline, unreadMessageInfo.unreadCount(), unreadMessageInfo.lastMessage(), this.distanceToUser, this.voiceActive);
    }
    
    @Nonnull
    ChatterDisplayData withVoiceActive(final boolean b) {
        return new ChatterDisplayData(this.chatterID, this.displayName, this.isOnline, this.unreadCount, this.lastMessage, this.distanceToUser, b);
    }
}
