/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.DialogInterface
 *  android.view.View
 *  android.view.View$OnClickListener
 */
package com.lumiyaviewer.lumiya.ui.objects;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.objects.ObjectPayDialog;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$X9q_n5C700PWS1S1Fm8NW_TXuec$1
implements View.OnClickListener {
    private final Object -$f0;
    private final Object -$f1;
    private final Object -$f2;
    private final Object -$f3;

    private final /* synthetic */ void $m$0(View view) {
        ObjectPayDialog.lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectPayDialog_4340((AlertDialog)this.-$f0, (UserManager)this.-$f1, (SLObjectProfileData)this.-$f2, (DialogInterface)this.-$f3, view);
    }

    public /* synthetic */ _$Lambda$X9q_n5C700PWS1S1Fm8NW_TXuec$1(Object object, Object object2, Object object3, Object object4) {
        this.-$f0 = object;
        this.-$f1 = object2;
        this.-$f2 = object3;
        this.-$f3 = object4;
    }

    public final void onClick(View view) {
        this.$m$0(view);
    }
}
