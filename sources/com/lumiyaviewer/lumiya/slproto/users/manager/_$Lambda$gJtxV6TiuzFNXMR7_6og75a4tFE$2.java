/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$gJtxV6TiuzFNXMR7_6og75a4tFE$2
implements Runnable {
    private final Object -$f0;
    private final Object -$f1;

    private final /* synthetic */ void $m$0() {
        ObjectPopupsManager.lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_ObjectPopupsManager_3571((ObjectPopupsManager.ObjectPopupListener)this.-$f0, (SLChatEvent)this.-$f1);
    }

    public /* synthetic */ _$Lambda$gJtxV6TiuzFNXMR7_6og75a4tFE$2(Object object, Object object2) {
        this.-$f0 = object;
        this.-$f1 = object2;
    }

    @Override
    public final void run() {
        this.$m$0();
    }
}
