/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnClickListener
 */
package com.lumiyaviewer.lumiya.ui.common;

import android.app.Activity;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.TeleportHomeDialog;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$sKhJxooMqZpn4u0mFmtSpF7hGx8$1
implements DialogInterface.OnClickListener {
    private final Object -$f0;
    private final Object -$f1;
    private final Object -$f2;

    private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int n) {
        TeleportHomeDialog.lambda$-com_lumiyaviewer_lumiya_ui_common_TeleportHomeDialog_956((Activity)this.-$f0, (UserManager)this.-$f1, (SLAgentCircuit)this.-$f2, dialogInterface, n);
    }

    public /* synthetic */ _$Lambda$sKhJxooMqZpn4u0mFmtSpF7hGx8$1(Object object, Object object2, Object object3) {
        this.-$f0 = object;
        this.-$f1 = object2;
        this.-$f2 = object3;
    }

    public final void onClick(DialogInterface dialogInterface, int n) {
        this.$m$0(dialogInterface, n);
    }
}
