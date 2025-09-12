/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import java.util.UUID;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$eCHTl__kh2tUCaOJ_O9NRHJvhjs$9
implements Runnable {
    private final boolean -$f0;
    private final Object -$f1;
    private final Object -$f2;
    private final Object -$f3;
    private final Object -$f4;
    private final Object -$f5;

    private final /* synthetic */ void $m$0() {
        ((SLInventory)this.-$f1).lambda$-com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_48053((SLInventoryEntry)this.-$f2, (byte[])this.-$f3, (UUID)this.-$f4, this.-$f0, (SLInventory.OnNotecardUpdatedListener)this.-$f5);
    }

    public /* synthetic */ _$Lambda$eCHTl__kh2tUCaOJ_O9NRHJvhjs$9(boolean bl, Object object, Object object2, Object object3, Object object4, Object object5) {
        this.-$f0 = bl;
        this.-$f1 = object;
        this.-$f2 = object2;
        this.-$f3 = object3;
        this.-$f4 = object4;
        this.-$f5 = object5;
    }

    @Override
    public final void run() {
        this.$m$0();
    }
}
