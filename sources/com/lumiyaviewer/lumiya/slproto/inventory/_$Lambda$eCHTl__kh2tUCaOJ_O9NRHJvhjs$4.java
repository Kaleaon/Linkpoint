/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$eCHTl__kh2tUCaOJ_O9NRHJvhjs$4
implements SLInventory.OnInventoryCallbackListener {
    private final Object -$f0;
    private final Object -$f1;

    private final /* synthetic */ void $m$0(SLInventoryEntry sLInventoryEntry) {
        ((SLInventory)this.-$f0).lambda$-com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_42520((SLInventoryEntry)this.-$f1, sLInventoryEntry);
    }

    public /* synthetic */ _$Lambda$eCHTl__kh2tUCaOJ_O9NRHJvhjs$4(Object object, Object object2) {
        this.-$f0 = object;
        this.-$f1 = object2;
    }

    @Override
    public final void onInventoryCallback(SLInventoryEntry sLInventoryEntry) {
        this.$m$0(sLInventoryEntry);
    }
}
