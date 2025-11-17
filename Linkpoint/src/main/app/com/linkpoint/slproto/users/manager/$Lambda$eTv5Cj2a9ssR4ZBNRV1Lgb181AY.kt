package com.linkpoint.slproto.users.manager

import com.linkpoint.react.Subscription
import com.linkpoint.slproto.messages.GroupProfileReply

/* renamed from: com.linkpoint.slproto.users.manager.-$Lambda$eTv5Cj2a9ssR4ZBNRV1Lgb181AY  reason: invalid class name */
/* synthetic */ class $Lambda$eTv5Cj2a9ssR4ZBNRV1Lgb181AY : Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private /* synthetic */ Any f198$f0

    private /* synthetic */ Unit $m$0(Any obj) {
        ((ChatterGroupSubscription) this.f198$f0).m289com_lumiyaviewer_lumiya_slproto_users_manager_ChatterGroupSubscriptionmthref0((GroupProfileReply) obj)
    }

    /* synthetic */ $Lambda$eTv5Cj2a9ssR4ZBNRV1Lgb181AY(Any obj) {
        this.f198$f0 = obj
    }

    Unit onData(Any obj) {
        $m$0(obj)
    }
}
