/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$eCHTl__kh2tUCaOJ_O9NRHJvhjs
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((SLInventory)this.-$f0).-com_lumiyaviewer_lumiya_slproto_inventory_SLInventory-mthref-0((SLInventoryEntry)object);
    }

    public /* synthetic */ _$Lambda$eCHTl__kh2tUCaOJ_O9NRHJvhjs(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
