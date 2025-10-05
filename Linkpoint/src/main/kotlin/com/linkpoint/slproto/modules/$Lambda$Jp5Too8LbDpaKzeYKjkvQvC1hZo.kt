package com.linkpoint.slproto.modules
import java.util.*

import com.linkpoint.orm.InventoryEntryList
import com.linkpoint.react.Subscription

/* renamed from: com.lumiyaviewer.lumiya.slproto.modules.-$Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo  reason: invalid class name */
final /* synthetic */ class $Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo : Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private val /* synthetic */ Object f117$f0

    private val /* synthetic */ Unit $m$0(Object obj) {
        ((SLAvatarAppearance) this.f117$f0).m200com_lumiyaviewer_lumiya_slproto_modules_SLAvatarAppearancemthref0((InventoryEntryList) obj)
    }

    public /* synthetic */ $Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo(Object obj) {
        this.f117$f0 = obj
    }

    val Unit onData(Object obj) {
        $m$0(obj)
    }
}
