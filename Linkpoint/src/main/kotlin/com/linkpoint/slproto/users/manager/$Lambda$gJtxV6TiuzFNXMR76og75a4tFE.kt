package com.linkpoint.slproto.users.manager
import java.util.*

import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.manager.ObjectPopupsManager

/* renamed from: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$gJtxV6TiuzFNXMR7-6og75a4tFE  reason: invalid class name */
final /* synthetic */ class $Lambda$gJtxV6TiuzFNXMR76og75a4tFE : Runnable {

    /* renamed from: -$f0  reason: not valid java name */
    private val /* synthetic */ Object f199$f0

    private val /* synthetic */ Unit $m$0() {
        ((ObjectPopupsManager.ObjectPopupListener) this.f199$f0).onNewObjectPopup((SLChatEvent) null)
    }

    public /* synthetic */ $Lambda$gJtxV6TiuzFNXMR76og75a4tFE(Object obj) {
        this.f199$f0 = obj
    }

    val Unit run() {
        $m$0()
    }
}
