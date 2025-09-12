/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnShowListener
 */
package com.lumiyaviewer.lumiya.ui.objects;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import com.lumiyaviewer.lumiya.slproto.objects.PayInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.objects.ObjectPayDialog;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$X9q_n5C700PWS1S1Fm8NW_TXuec$2
implements DialogInterface.OnShowListener {
    private final Object -$f0;
    private final Object -$f1;
    private final Object -$f2;
    private final Object -$f3;
    private final Object -$f4;

    private final /* synthetic */ void $m$0(DialogInterface dialogInterface) {
        ObjectPayDialog.lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectPayDialog_1356((AlertDialog)this.-$f0, (PayInfo)this.-$f1, (Context)this.-$f2, (UserManager)this.-$f3, (SLObjectProfileData)this.-$f4, dialogInterface);
    }

    public /* synthetic */ _$Lambda$X9q_n5C700PWS1S1Fm8NW_TXuec$2(Object object, Object object2, Object object3, Object object4, Object object5) {
        this.-$f0 = object;
        this.-$f1 = object2;
        this.-$f2 = object3;
        this.-$f3 = object4;
        this.-$f4 = object5;
    }

    public final void onShow(DialogInterface dialogInterface) {
        this.$m$0(dialogInterface);
    }
}
