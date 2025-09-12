/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnClickListener
 */
package com.lumiyaviewer.lumiya.ui.inventory;

import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.ui.inventory.NotecardEditActivity;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk$3
implements DialogInterface.OnClickListener {
    private final Object -$f0;
    private final Object -$f1;

    private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int n) {
        ((NotecardEditActivity)this.-$f0).lambda$-com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivity_14261((SLInventoryEntry)this.-$f1, dialogInterface, n);
    }

    public /* synthetic */ _$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk$3(Object object, Object object2) {
        this.-$f0 = object;
        this.-$f1 = object2;
    }

    public final void onClick(DialogInterface dialogInterface, int n) {
        this.$m$0(dialogInterface, n);
    }
}
