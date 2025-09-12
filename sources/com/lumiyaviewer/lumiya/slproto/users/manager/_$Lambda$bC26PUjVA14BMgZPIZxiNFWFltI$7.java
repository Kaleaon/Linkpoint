/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI$7
implements Runnable {
    private final boolean -$f0;
    private final Object -$f1;
    private final Object -$f2;

    private final /* synthetic */ void $m$0() {
        ((ActiveChattersManager)this.-$f1).lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_25047((ChatterID)this.-$f2, this.-$f0);
    }

    public /* synthetic */ _$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI$7(boolean bl, Object object, Object object2) {
        this.-$f0 = bl;
        this.-$f1 = object;
        this.-$f2 = object2;
    }

    @Override
    public final void run() {
        this.$m$0();
    }
}
