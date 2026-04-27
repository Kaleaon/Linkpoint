/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnClickListener
 */
package com.lumiyaviewer.lumiya.ui.inventory;

import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryFragmentHelper;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg$11
implements DialogInterface.OnClickListener {
    private final boolean -$f0;
    private final Object -$f1;
    private final Object -$f2;
    private final Object -$f3;

    private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int n) {
        InventoryFragmentHelper.lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryFragmentHelper_8206(this.-$f0, (SLInventory)this.-$f1, (SLInventoryEntry)this.-$f2, (Runnable)this.-$f3, dialogInterface, n);
    }

    public /* synthetic */ _$Lambda$6k4r7SqDbbDd94MFx7S4Wwav0Lg$11(boolean bl, Object object, Object object2, Object object3) {
        this.-$f0 = bl;
        this.-$f1 = object;
        this.-$f2 = object2;
        this.-$f3 = object3;
    }

    public final void onClick(DialogInterface dialogInterface, int n) {
        this.$m$0(dialogInterface, n);
    }
}
