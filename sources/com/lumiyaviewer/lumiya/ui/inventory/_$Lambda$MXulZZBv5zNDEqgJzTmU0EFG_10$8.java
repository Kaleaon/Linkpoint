/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.ui.inventory;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryFragment;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$MXulZZBv5zNDEqgJzTmU0EFG_10$8
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((InventoryFragment)this.-$f0).-com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment-mthref-2((SLAgentCircuit)object);
    }

    public /* synthetic */ _$Lambda$MXulZZBv5zNDEqgJzTmU0EFG_10$8(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
