/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$eCHTl__kh2tUCaOJ_O9NRHJvhjs$7
implements SLInventory.OnInventoryCallbackListener {
    private final boolean -$f0;
    private final Object -$f1;
    private final Object -$f2;
    private final Object -$f3;

    private final /* synthetic */ void $m$0(SLInventoryEntry sLInventoryEntry) {
        ((SLInventory)this.-$f1).lambda$-com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_49599(this.-$f0, (byte[])this.-$f2, (SLInventory.OnNotecardUpdatedListener)this.-$f3, sLInventoryEntry);
    }

    public /* synthetic */ _$Lambda$eCHTl__kh2tUCaOJ_O9NRHJvhjs$7(boolean bl, Object object, Object object2, Object object3) {
        this.-$f0 = bl;
        this.-$f1 = object;
        this.-$f2 = object2;
        this.-$f3 = object3;
    }

    @Override
    public final void onInventoryCallback(SLInventoryEntry sLInventoryEntry) {
        this.$m$0(sLInventoryEntry);
    }
}
