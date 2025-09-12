/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.dao.Chatter;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI$6
implements Runnable {
    private final Object -$f0;
    private final Object -$f1;
    private final Object -$f2;

    private final /* synthetic */ void $m$0() {
        ((ActiveChattersManager)this.-$f0).lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_10179((ChatterID)this.-$f1, (Chatter)this.-$f2);
    }

    public /* synthetic */ _$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI$6(Object object, Object object2, Object object3) {
        this.-$f0 = object;
        this.-$f1 = object2;
        this.-$f2 = object3;
    }

    @Override
    public final void run() {
        this.$m$0();
    }
}
