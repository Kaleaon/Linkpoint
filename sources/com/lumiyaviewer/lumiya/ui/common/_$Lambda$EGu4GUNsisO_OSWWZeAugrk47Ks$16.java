/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnClickListener
 */
package com.lumiyaviewer.lumiya.ui.common;

import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.UserFunctionsFragment;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks$16
implements DialogInterface.OnClickListener {
    private final Object -$f0;
    private final Object -$f1;
    private final Object -$f2;

    private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int n) {
        UserFunctionsFragment.lambda$-com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_23327((UserManager)this.-$f0, (ChatterID)this.-$f1, (String)this.-$f2, dialogInterface, n);
    }

    public /* synthetic */ _$Lambda$EGu4GUNsisO_OSWWZeAugrk47Ks$16(Object object, Object object2, Object object3) {
        this.-$f0 = object;
        this.-$f1 = object2;
        this.-$f2 = object3;
    }

    public final void onClick(DialogInterface dialogInterface, int n) {
        this.$m$0(dialogInterface, n);
    }
}
