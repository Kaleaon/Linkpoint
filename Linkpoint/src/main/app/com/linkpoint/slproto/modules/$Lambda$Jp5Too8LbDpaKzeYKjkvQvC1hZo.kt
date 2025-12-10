package com.linkpoint.slproto.modules
import java.util.*

import com.linkpoint.orm.InventoryEntryList
import com.linkpoint.react.Subscription

/* renamed from: com.linkpoint.slproto.modules.-$Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo  reason: invalid class name */
/* synthetic */ class $Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo : Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private /* synthetic */ Any f117$f0

    private /* synthetic */ Unit $m$0(Any obj) {
        ((this as SLAvatarAppearance).f117$f0).m200com_lumiyaviewer_lumiya_slproto_modules_SLAvatarAppearancemthref0((InventoryEntryList) obj)
    }

    /* synthetic */ $Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo(Any obj) {
        this.f117$f0 = obj
    }

    fun onData(Any obj): Unit {
        $m$0(obj)
    }
}
