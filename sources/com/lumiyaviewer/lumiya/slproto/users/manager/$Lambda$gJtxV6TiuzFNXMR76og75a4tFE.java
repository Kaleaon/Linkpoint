package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager;

/* renamed from: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$gJtxV6TiuzFNXMR7-6og75a4tFE  reason: invalid class name */
final /* synthetic */ class $Lambda$gJtxV6TiuzFNXMR76og75a4tFE implements Runnable {

    /* renamed from: -$f0  reason: not valid java name */
    private final /* synthetic */ Object f199$f0;

    private final /* synthetic */ void $m$0() {
        ((ObjectPopupsManager.ObjectPopupListener) this.f199$f0).onNewObjectPopup((SLChatEvent) null);
    }

    public /* synthetic */ $Lambda$gJtxV6TiuzFNXMR76og75a4tFE(Object obj) {
        this.f199$f0 = obj;
    }

    public final void run() {
        $m$0();
    }
}
