package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import javax.annotation.Nonnull;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ChatterSubscription {
    @Nonnull
    private final SortedChatterList chatterList;
    @Nonnull
    ChatterDisplayData displayData;
    @Nonnull
    private final Subscription<ChatterID, UnreadMessageInfo> unreadCountSubscription;
    @Nonnull
    private final Subscription<ChatterID, VoiceChatInfo> voiceChatInfoSubscription;
    private final Subscription.OnData<VoiceChatInfo> onVoiceStatusChanged = new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$x6PlkRNg0xExeA_EUn8oEJWcOq8
        private final /* synthetic */ void $m$0(Object obj) {
            ((ChatterSubscription) this).m308x45811332((VoiceChatInfo) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    };
    private final Subscription.OnData<UnreadMessageInfo> onUnreadCount = new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$x6PlkRNg0xExeA_EUn8oEJWcOq8.1
        private final /* synthetic */ void $m$0(Object obj) {
            ((ChatterSubscription) this).m309x45811333((UnreadMessageInfo) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    };
    boolean isValid = true;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChatterSubscription(@Nonnull SortedChatterList sortedChatterList, @Nonnull ChatterID chatterID, @Nonnull UserManager userManager) {
        this.chatterList = sortedChatterList;
        this.displayData = new ChatterDisplayData(chatterID, null, false, 0, null, Float.NaN, false);
        this.unreadCountSubscription = userManager.getChatterList().getActiveChattersManager().getUnreadCounts().subscribe(chatterID, this.onUnreadCount);
        this.voiceChatInfoSubscription = userManager.getVoiceChatInfo().subscribe(chatterID, this.onVoiceStatusChanged);
        sortedChatterList.addChatter(this.displayData);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onUnreadCountChanged */
    public void m309x45811333(UnreadMessageInfo unreadMessageInfo) {
        if (unreadMessageInfo != null) {
            setChatterDisplayData(this.displayData.withUnreadInfo(unreadMessageInfo));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onVoiceChatInfoChanged */
    public void m308x45811332(VoiceChatInfo voiceChatInfo) {
        boolean z = false;
        ChatterDisplayData chatterDisplayData = this.displayData;
        if (voiceChatInfo != null && voiceChatInfo.state != VoiceChatInfo.VoiceChatState.None) {
            z = true;
        }
        setChatterDisplayData(chatterDisplayData.withVoiceActive(z));
    }

    public void dispose() {
        unsubscribe();
        this.chatterList.removeChatter(this.displayData);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setChatterDisplayData(@Nonnull ChatterDisplayData chatterDisplayData) {
        ChatterDisplayData chatterDisplayData2 = this.displayData;
        this.displayData = chatterDisplayData;
        this.chatterList.replaceChatter(chatterDisplayData2, this.displayData);
    }

    public void unsubscribe() {
        this.unreadCountSubscription.unsubscribe();
        this.voiceChatInfoSubscription.unsubscribe();
    }
}
