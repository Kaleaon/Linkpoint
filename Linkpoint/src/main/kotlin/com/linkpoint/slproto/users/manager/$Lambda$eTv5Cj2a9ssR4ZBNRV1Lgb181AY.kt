package com.linkpoint.slproto.users.manager

import com.linkpoint.react.Subscription
import com.linkpoint.slproto.messages.GroupProfileReply

/* renamed from: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$eTv5Cj2a9ssR4ZBNRV1Lgb181AY  reason: invalid class name */
final /* synthetic */ class $Lambda$eTv5Cj2a9ssR4ZBNRV1Lgb181AY : Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private val /* synthetic */ Object f198$f0

    private val /* synthetic */ Unit $m$0(Object obj) {
        ((ChatterGroupSubscription) this.f198$f0).m289com_lumiyaviewer_lumiya_slproto_users_manager_ChatterGroupSubscriptionmthref0((GroupProfileReply) obj)
    }

    public /* synthetic */ $Lambda$eTv5Cj2a9ssR4ZBNRV1Lgb181AY(Object obj) {
        this.f198$f0 = obj
    }

    val Unit onData(Object obj) {
        $m$0(obj)
    }
}
