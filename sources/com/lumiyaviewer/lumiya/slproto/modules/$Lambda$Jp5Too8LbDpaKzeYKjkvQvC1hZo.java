package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.orm.InventoryEntryList;
import com.lumiyaviewer.lumiya.react.Subscription;

/* renamed from: com.lumiyaviewer.lumiya.slproto.modules.-$Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo  reason: invalid class name */
final /* synthetic */ class $Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo implements Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private final /* synthetic */ Object f117$f0;

    private final /* synthetic */ void $m$0(Object obj) {
        ((SLAvatarAppearance) this.f117$f0).m200com_lumiyaviewer_lumiya_slproto_modules_SLAvatarAppearancemthref0((InventoryEntryList) obj);
    }

    public /* synthetic */ $Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo(Object obj) {
        this.f117$f0 = obj;
    }

    public final void onData(Object obj) {
        $m$0(obj);
    }
}
