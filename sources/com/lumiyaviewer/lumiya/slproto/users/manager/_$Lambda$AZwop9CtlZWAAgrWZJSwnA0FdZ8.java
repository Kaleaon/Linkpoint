/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.manager.SyncManager;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8
implements ChatterNameRetriever.OnChatterNameUpdated {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(ChatterNameRetriever chatterNameRetriever) {
        ((SyncManager)this.-$f0).-com_lumiyaviewer_lumiya_slproto_users_manager_SyncManager-mthref-6(chatterNameRetriever);
    }

    public /* synthetic */ _$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8(Object object) {
        this.-$f0 = object;
    }

    @Override
    public final void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        this.$m$0(chatterNameRetriever);
    }
}
