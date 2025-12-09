package com.linkpoint.slproto.users.manager
import java.util.*

import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.manager.ObjectPopupsManager

/* renamed from: com.linkpoint.slproto.users.manager.-$Lambda$gJtxV6TiuzFNXMR7-6og75a4tFE  reason: invalid class name */
/* synthetic */ class $Lambda$gJtxV6TiuzFNXMR76og75a4tFE : Runnable {

    /* renamed from: -$f0  reason: not valid java name */
    private /* synthetic */ Any f199$f0

    private /* synthetic */ Unit $m$0() {
        ((ObjectPopupsManager.ObjectPopupListener) this.f199$f0).onNewObjectPopup((SLChatEvent) null)
    }

    /* synthetic */ $Lambda$gJtxV6TiuzFNXMR76og75a4tFE(Any obj) {
        this.f199$f0 = obj
    }

    fun run(): Unit {
        $m$0()
    }
}
