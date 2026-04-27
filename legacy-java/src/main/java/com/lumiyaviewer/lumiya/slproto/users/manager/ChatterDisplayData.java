package com.lumiyaviewer.lumiya.slproto.users.manager;

import android.content.Context;
import com.google.common.base.Strings;
import com.google.common.primitives.Booleans;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatterItemViewBuilder;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class ChatterDisplayData implements ChatterDisplayInfo, Comparable<ChatterDisplayData> {
    public final ChatterID chatterID;
    public final String displayName;
    final float distanceToUser;
    final boolean isOnline;

    @Nullable
    private final SLChatEvent lastMessage;
    private final int unreadCount;
    private final boolean voiceActive;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChatterDisplayData(ChatterID chatterID, String str, boolean z, int i, @Nullable SLChatEvent sLChatEvent, float f, boolean z2) {
        this.chatterID = chatterID;
        this.displayName = str;
        this.isOnline = z;
        this.unreadCount = i;
        this.lastMessage = sLChatEvent;
        this.distanceToUser = f;
        this.voiceActive = z2;
    }

    @Override // com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo
    public void buildView(Context context, ChatterItemViewBuilder chatterItemViewBuilder, UserManager userManager) {
        chatterItemViewBuilder.setLabel(this.displayName);
        chatterItemViewBuilder.setThumbnailChatterID(this.chatterID, this.displayName);
        chatterItemViewBuilder.setOnlineStatusIcon(this.isOnline, this.isOnline);
        chatterItemViewBuilder.setUnreadCount(this.unreadCount);
        chatterItemViewBuilder.setVoiceActive(this.voiceActive);
        if (this.lastMessage != null) {
            chatterItemViewBuilder.setLastMessage(this.lastMessage.getPlainTextMessage(context, userManager, true).toString());
        } else {
            chatterItemViewBuilder.setLastMessage(null);
        }
        chatterItemViewBuilder.setDistance(this.distanceToUser);
    }

    @Override // java.lang.Comparable
    public int compareTo(@Nonnull ChatterDisplayData chatterDisplayData) {
        int compare = Booleans.compare(Strings.isNullOrEmpty(this.displayName), Strings.isNullOrEmpty(chatterDisplayData.displayName));
        if (compare != 0) {
            return compare;
        }
        int compareTo = (this.displayName != null ? this.displayName : "").compareTo(chatterDisplayData.displayName != null ? chatterDisplayData.displayName : "");
        return compareTo != 0 ? compareTo : this.chatterID.compareTo(chatterDisplayData.chatterID);
    }

    @Override // com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo
    public ChatterID getChatterID(UserManager userManager) {
        return this.chatterID;
    }

    @Override // com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo
    @Nullable
    public String getDisplayName() {
        return this.displayName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nonnull
    public ChatterDisplayData withDisplayName(String str) {
        return new ChatterDisplayData(this.chatterID, str, this.isOnline, this.unreadCount, this.lastMessage, this.distanceToUser, this.voiceActive);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nonnull
    public ChatterDisplayData withDistanceToUser(float f) {
        return new ChatterDisplayData(this.chatterID, this.displayName, this.isOnline, this.unreadCount, this.lastMessage, f, this.voiceActive);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nonnull
    public ChatterDisplayData withOnlineStatus(boolean z) {
        return new ChatterDisplayData(this.chatterID, this.displayName, z, this.unreadCount, this.lastMessage, this.distanceToUser, this.voiceActive);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nonnull
    public ChatterDisplayData withUnreadInfo(@Nonnull UnreadMessageInfo unreadMessageInfo) {
        return new ChatterDisplayData(this.chatterID, this.displayName, this.isOnline, unreadMessageInfo.unreadCount(), unreadMessageInfo.lastMessage(), this.distanceToUser, this.voiceActive);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nonnull
    public ChatterDisplayData withVoiceActive(boolean z) {
        return new ChatterDisplayData(this.chatterID, this.displayName, this.isOnline, this.unreadCount, this.lastMessage, this.distanceToUser, z);
    }
}
