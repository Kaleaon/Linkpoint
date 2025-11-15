package com.lumiyaviewer.lumiya.slproto.users.manager

import com.lumiyaviewer.lumiya.react.Subscription
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo

/* renamed from: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$x6PlkRNg0xExeA_EUn8oEJWcOq8  reason: invalid class name */
/* synthetic */ class $Lambda$x6PlkRNg0xExeA_EUn8oEJWcOq8 : Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private /* synthetic */ Any f222$f0

    private /* synthetic */ Unit $m$0(Any obj) {
        ((ChatterSubscription) this.f222$f0).m297com_lumiyaviewer_lumiya_slproto_users_manager_ChatterSubscriptionmthref0((VoiceChatInfo) obj)
    }

    /* synthetic */ $Lambda$x6PlkRNg0xExeA_EUn8oEJWcOq8(Any obj) {
        this.f222$f0 = obj
    }

    Unit onData(Any obj) {
        $m$0(obj)
    }
}
