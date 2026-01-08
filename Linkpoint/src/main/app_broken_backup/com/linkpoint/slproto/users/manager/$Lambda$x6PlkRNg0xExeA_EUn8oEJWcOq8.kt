package com.linkpoint.slproto.users.manager

import com.linkpoint.react.Subscription
import com.linkpoint.voice.common.model.VoiceChatInfo

/* renamed from: com.linkpoint.slproto.users.manager.-$Lambda$x6PlkRNg0xExeA_EUn8oEJWcOq8  reason: invalid class name */
/* synthetic */ class $Lambda$x6PlkRNg0xExeA_EUn8oEJWcOq8 : Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private /* synthetic */ Any f222$f0

    private /* synthetic */ Unit $m$0(Any obj) {
        ((this as ChatterSubscription).f222$f0).m297com_lumiyaviewer_lumiya_slproto_users_manager_ChatterSubscriptionmthref0((VoiceChatInfo) obj)
    }

    /* synthetic */ $Lambda$x6PlkRNg0xExeA_EUn8oEJWcOq8(Any obj) {
        this.f222$f0 = obj
    }

    fun onData(Any obj): Unit {
        $m$0(obj)
    }
}
