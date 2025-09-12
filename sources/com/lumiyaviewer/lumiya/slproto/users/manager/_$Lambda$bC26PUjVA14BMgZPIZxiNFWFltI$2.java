/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.chat.generic.OnChatEventListener;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI$2
implements Runnable {
    private final Object -$f0;
    private final Object -$f1;

    private final /* synthetic */ void $m$0() {
        ActiveChattersManager.lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_ActiveChattersManager_16177((OnChatEventListener)this.-$f0, (SLChatEvent)this.-$f1);
    }

    public /* synthetic */ _$Lambda$bC26PUjVA14BMgZPIZxiNFWFltI$2(Object object, Object object2) {
        this.-$f0 = object;
        this.-$f1 = object2;
    }

    @Override
    public final void run() {
        this.$m$0();
    }
}
