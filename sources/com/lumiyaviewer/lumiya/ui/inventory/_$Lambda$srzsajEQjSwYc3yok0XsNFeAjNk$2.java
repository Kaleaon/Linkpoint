/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.ui.inventory;

import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.ui.inventory.NotecardEditActivity;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk$2
implements SLInventory.OnNotecardUpdatedListener {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(SLInventoryEntry sLInventoryEntry, String string) {
        ((NotecardEditActivity)this.-$f0).lambda$-com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivity_19315(sLInventoryEntry, string);
    }

    public /* synthetic */ _$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk$2(Object object) {
        this.-$f0 = object;
    }

    @Override
    public final void onNotecardUpdated(SLInventoryEntry sLInventoryEntry, String string) {
        this.$m$0(sLInventoryEntry, string);
    }
}
