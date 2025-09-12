package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;

/* renamed from: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$x6PlkRNg0xExeA_EUn8oEJWcOq8  reason: invalid class name */
final /* synthetic */ class $Lambda$x6PlkRNg0xExeA_EUn8oEJWcOq8 implements Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private final /* synthetic */ Object f222$f0;

    private final /* synthetic */ void $m$0(Object obj) {
        ((ChatterSubscription) this.f222$f0).m297com_lumiyaviewer_lumiya_slproto_users_manager_ChatterSubscriptionmthref0((VoiceChatInfo) obj);
    }

    public /* synthetic */ $Lambda$x6PlkRNg0xExeA_EUn8oEJWcOq8(Object obj) {
        this.f222$f0 = obj;
    }

    public final void onData(Object obj) {
        $m$0(obj);
    }
}
