/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnClickListener
 */
package com.lumiyaviewer.lumiya.ui.accounts;

import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.ui.accounts.AccountList;
import com.lumiyaviewer.lumiya.ui.accounts.ManageAccountsActivity;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$c901yk_brt0jPczBoAMr_Jn1w74$1
implements DialogInterface.OnClickListener {
    private final Object -$f0;
    private final Object -$f1;

    private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int n) {
        ((ManageAccountsActivity)this.-$f0).lambda$-com_lumiyaviewer_lumiya_ui_accounts_ManageAccountsActivity_6140((AccountList.AccountInfo)this.-$f1, dialogInterface, n);
    }

    public /* synthetic */ _$Lambda$c901yk_brt0jPczBoAMr_Jn1w74$1(Object object, Object object2) {
        this.-$f0 = object;
        this.-$f1 = object2;
    }

    public final void onClick(DialogInterface dialogInterface, int n) {
        this.$m$0(dialogInterface, n);
    }
}
