// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import com.lumiyaviewer.lumiya.react.Subscription;
import javax.annotation.Nonnull;

class ChatterSubscription
{
    @Nonnull
    private final SortedChatterList chatterList;
    @Nonnull
    ChatterDisplayData displayData;
    boolean isValid;
    private final Subscription.OnData<UnreadMessageInfo> onUnreadCount;
    private final Subscription.OnData<VoiceChatInfo> onVoiceStatusChanged;
    @Nonnull
    private final Subscription<ChatterID, UnreadMessageInfo> unreadCountSubscription;
    @Nonnull
    private final Subscription<ChatterID, VoiceChatInfo> voiceChatInfoSubscription;
    
    ChatterSubscription(@Nonnull final SortedChatterList chatterList, @Nonnull final ChatterID chatterID, @Nonnull final UserManager userManager) {
        this.onVoiceStatusChanged = new _$Lambda$x6PlkRNg0xExeA_EUn8oEJWcOq8(this);
        this.onUnreadCount = new _$Lambda$x6PlkRNg0xExeA_EUn8oEJWcOq8$1(this);
        this.chatterList = chatterList;
        this.displayData = new ChatterDisplayData(chatterID, null, false, 0, null, Float.NaN, false);
        this.unreadCountSubscription = userManager.getChatterList().getActiveChattersManager().getUnreadCounts().subscribe(chatterID, this.onUnreadCount);
        this.voiceChatInfoSubscription = userManager.getVoiceChatInfo().subscribe(chatterID, this.onVoiceStatusChanged);
        this.isValid = true;
        chatterList.addChatter(this.displayData);
    }
    
    private void onUnreadCountChanged(final UnreadMessageInfo unreadMessageInfo) {
        if (unreadMessageInfo != null) {
            this.setChatterDisplayData(this.displayData.withUnreadInfo(unreadMessageInfo));
        }
    }
    
    private void onVoiceChatInfoChanged(final VoiceChatInfo voiceChatInfo) {
        final boolean b = false;
        final ChatterDisplayData displayData = this.displayData;
        boolean b2 = b;
        if (voiceChatInfo != null) {
            b2 = b;
            if (voiceChatInfo.state != VoiceChatInfo.VoiceChatState.None) {
                b2 = true;
            }
        }
        this.setChatterDisplayData(displayData.withVoiceActive(b2));
    }
    
    public void dispose() {
        this.unsubscribe();
        this.chatterList.removeChatter(this.displayData);
    }
    
    void setChatterDisplayData(@Nonnull final ChatterDisplayData displayData) {
        final ChatterDisplayData displayData2 = this.displayData;
        this.displayData = displayData;
        this.chatterList.replaceChatter(displayData2, this.displayData);
    }
    
    public void unsubscribe() {
        this.unreadCountSubscription.unsubscribe();
        this.voiceChatInfoSubscription.unsubscribe();
    }
}
