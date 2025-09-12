/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnClickListener
 *  android.graphics.Bitmap
 */
package com.lumiyaviewer.lumiya.ui.inventory;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryFragment;
import java.util.UUID;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$MXulZZBv5zNDEqgJzTmU0EFG_10$17
implements DialogInterface.OnClickListener {
    private final Object -$f0;
    private final Object -$f1;
    private final Object -$f2;
    private final Object -$f3;
    private final Object -$f4;

    private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int n) {
        ((InventoryFragment)this.-$f0).lambda$-com_lumiyaviewer_lumiya_ui_inventory_InventoryFragment_29313((String)this.-$f1, (Bitmap)this.-$f2, (UserManager)this.-$f3, (UUID)this.-$f4, dialogInterface, n);
    }

    public /* synthetic */ _$Lambda$MXulZZBv5zNDEqgJzTmU0EFG_10$17(Object object, Object object2, Object object3, Object object4, Object object5) {
        this.-$f0 = object;
        this.-$f1 = object2;
        this.-$f2 = object3;
        this.-$f3 = object4;
        this.-$f4 = object5;
    }

    public final void onClick(DialogInterface dialogInterface, int n) {
        this.$m$0(dialogInterface, n);
    }
}
