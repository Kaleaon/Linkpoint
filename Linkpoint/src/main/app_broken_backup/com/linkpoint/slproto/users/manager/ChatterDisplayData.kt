package com.linkpoint.slproto.users.manager

import android.content.Context
import com.google.common.base.Strings
import com.google.common.primitives.Booleans
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.ui.chat.ChatterDisplayInfo
import com.linkpoint.ui.chat.contacts.ChatterItemViewBuilder
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class ChatterDisplayData : ChatterDisplayInfo, Comparable<ChatterDisplayData> {
    ChatterID chatterID
    String displayName
    Float distanceToUser
    Boolean isOnline
    @Nullable
    private SLChatEvent lastMessage
    private Int unreadCount
    private Boolean voiceActive

    ChatterDisplayData(ChatterID chatterID2, String str, Boolean z, Int i, @Nullable SLChatEvent sLChatEvent, Float f, Boolean z2) {
        this.chatterID = chatterID2
        this.displayName = str
        this.isOnline = z
        this.unreadCount = i
        this.lastMessage = sLChatEvent
        this.distanceToUser = f
        this.voiceActive = z2
    }

    fun buildView(Context context, ChatterItemViewBuilder chatterItemViewBuilder, UserManager userManager)  {
        chatterItemViewBuilder.setLabel(this.displayName)
        chatterItemViewBuilder.setThumbnailChatterID(this.chatterID, this.displayName)
        chatterItemViewBuilder.setOnlineStatusIcon(this.isOnline, this.isOnline)
        chatterItemViewBuilder.setUnreadCount(this.unreadCount)
        chatterItemViewBuilder.setVoiceActive(this.voiceActive)
        if (this.lastMessage != null) {
            chatterItemViewBuilder.setLastMessage(this.lastMessage.getPlainTextMessage(context, userManager, true).toString())
        } else {
            chatterItemViewBuilder.setLastMessage((String) null)
        }
        chatterItemViewBuilder.setDistance(this.distanceToUser)
    }

    fun compareTo(@NonNull ChatterDisplayData chatterDisplayData): Int {
        var compare: Int = Booleans.compare(Strings.isNullOrEmpty(this.displayName), Strings.isNullOrEmpty(chatterDisplayData.displayName))
        if (compare != 0) {
            return compare
        }
        var compareTo: Int = (this.displayName != null ? this.displayName : "").compareTo(chatterDisplayData.displayName != null ? chatterDisplayData.displayName : "")
        return compareTo != 0 ? compareTo : this.chatterID.compareTo(chatterDisplayData.chatterID)
    }

    fun getChatterID(UserManager userManager): ChatterID {
        return this.chatterID
    }

    @Nullable
    fun getDisplayName(): String {
        return this.displayName
    }

    /* access modifiers changed from: package-private */
    @NonNull
    fun withDisplayName(String str): ChatterDisplayData {
        return ChatterDisplayData(this.chatterID, str, this.isOnline, this.unreadCount, this.lastMessage, this.distanceToUser, this.voiceActive)
    }

    /* access modifiers changed from: package-private */
    @NonNull
    fun withDistanceToUser(Float f): ChatterDisplayData {
        return ChatterDisplayData(this.chatterID, this.displayName, this.isOnline, this.unreadCount, this.lastMessage, f, this.voiceActive)
    }

    /* access modifiers changed from: package-private */
    @NonNull
    fun withOnlineStatus(Boolean z): ChatterDisplayData {
        return ChatterDisplayData(this.chatterID, this.displayName, z, this.unreadCount, this.lastMessage, this.distanceToUser, this.voiceActive)
    }

    /* access modifiers changed from: package-private */
    @NonNull
    fun withUnreadInfo(@NonNull UnreadMessageInfo unreadMessageInfo): ChatterDisplayData {
        return ChatterDisplayData(this.chatterID, this.displayName, this.isOnline, unreadMessageInfo.unreadCount(), unreadMessageInfo.lastMessage(), this.distanceToUser, this.voiceActive)
    }

    /* access modifiers changed from: package-private */
    @NonNull
    fun withVoiceActive(Boolean z): ChatterDisplayData {
        return ChatterDisplayData(this.chatterID, this.displayName, this.isOnline, this.unreadCount, this.lastMessage, this.distanceToUser, z)
    }
}
