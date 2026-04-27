package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;

/* renamed from: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$eTv5Cj2a9ssR4ZBNRV1Lgb181AY  reason: invalid class name */
final /* synthetic */ class $Lambda$eTv5Cj2a9ssR4ZBNRV1Lgb181AY implements Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private final /* synthetic */ Object f198$f0;

    private final /* synthetic */ void $m$0(Object obj) {
        ((ChatterGroupSubscription) this.f198$f0).m289com_lumiyaviewer_lumiya_slproto_users_manager_ChatterGroupSubscriptionmthref0((GroupProfileReply) obj);
    }

    public /* synthetic */ $Lambda$eTv5Cj2a9ssR4ZBNRV1Lgb181AY(Object obj) {
        this.f198$f0 = obj;
    }

    public final void onData(Object obj) {
        $m$0(obj);
    }
}
