/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterGroupSubscription;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$eTv5Cj2a9ssR4ZBNRV1Lgb181AY
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((ChatterGroupSubscription)this.-$f0).-com_lumiyaviewer_lumiya_slproto_users_manager_ChatterGroupSubscription-mthref-0((GroupProfileReply)object);
    }

    public /* synthetic */ _$Lambda$eTv5Cj2a9ssR4ZBNRV1Lgb181AY(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
