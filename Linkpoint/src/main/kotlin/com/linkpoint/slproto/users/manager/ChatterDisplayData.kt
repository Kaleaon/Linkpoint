package com.linkpoint.slproto.users.manager

import android.content.Context
import com.google.common.base.Strings
import com.google.common.primitives.Booleans
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.ui.chat.ChatterDisplayInfo
import com.linkpoint.ui.chat.contacts.ChatterItemViewBuilder
import javax.annotation.Nonnull
import javax.annotation.Nullable

class ChatterDisplayData : ChatterDisplayInfo, Comparable<ChatterDisplayData> {
    val ChatterID chatterID
    val String displayName
    final Float distanceToUser
    final Boolean isOnline
    private val SLChatEvent lastMessage
    private val Int unreadCount
    private val Boolean voiceActive

    ChatterDisplayData(ChatterID chatterID2, String str, Boolean z, Int i, SLChatEvent sLChatEvent, Float f, Boolean z2) {
        this.chatterID = chatterID2
        this.displayName = str
        this.isOnline = z
        this.unreadCount = i
        this.lastMessage = sLChatEvent
        this.distanceToUser = f
        this.voiceActive = z2
    }

    fun buildView(context: Context, chatterItemViewBuilder: ChatterItemViewBuilder, userManager: UserManager) {
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

     public fun compareTo(chatterDisplayData: ChatterDisplayData): Int {
        val compare: Int = Booleans.compare(Strings.isNullOrEmpty(this.displayName), Strings.isNullOrEmpty(chatterDisplayData.displayName))
        if (compare != 0) {
            return compare
        }
        val compareTo: Int = (this.displayName != null ? this.displayName : "").compareTo(chatterDisplayData.displayName != null ? chatterDisplayData.displayName : "")
        return compareTo != 0 ? compareTo : this.chatterID.compareTo(chatterDisplayData.chatterID)
    }

     public fun getChatterID(userManager: UserManager): ChatterID {
        return this.chatterID
    }

     public fun getDisplayName(): String {
        return this.displayName
    }

    /* access modifiers changed from: package-private */
     public fun withDisplayName(str: String): ChatterDisplayData {
        return ChatterDisplayData(this.chatterID, str, this.isOnline, this.unreadCount, this.lastMessage, this.distanceToUser, this.voiceActive)
    }

    /* access modifiers changed from: package-private */
     public fun withDistanceToUser(f: Float): ChatterDisplayData {
        return ChatterDisplayData(this.chatterID, this.displayName, this.isOnline, this.unreadCount, this.lastMessage, f, this.voiceActive)
    }

    /* access modifiers changed from: package-private */
     public fun withOnlineStatus(z: Boolean): ChatterDisplayData {
        return ChatterDisplayData(this.chatterID, this.displayName, z, this.unreadCount, this.lastMessage, this.distanceToUser, this.voiceActive)
    }

    /* access modifiers changed from: package-private */
     public fun withUnreadInfo(unreadMessageInfo: UnreadMessageInfo): ChatterDisplayData {
        return ChatterDisplayData(this.chatterID, this.displayName, this.isOnline, unreadMessageInfo.unreadCount(), unreadMessageInfo.lastMessage(), this.distanceToUser, this.voiceActive)
    }

    /* access modifiers changed from: package-private */
     public fun withVoiceActive(z: Boolean): ChatterDisplayData {
        return ChatterDisplayData(this.chatterID, this.displayName, this.isOnline, this.unreadCount, this.lastMessage, this.distanceToUser, z)
    }
}
