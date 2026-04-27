/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnClickListener
 */
package com.lumiyaviewer.lumiya.ui.objects;

import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.objects.ObjectDetailsFragment;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8$6
implements DialogInterface.OnClickListener {
    private final int -$f0;
    private final Object -$f1;
    private final Object -$f2;

    private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int n) {
        ObjectDetailsFragment.lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_25106((UserManager)this.-$f1, (SLObjectProfileData)this.-$f2, this.-$f0, dialogInterface, n);
    }

    public /* synthetic */ _$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8$6(int n, Object object, Object object2) {
        this.-$f0 = n;
        this.-$f1 = object;
        this.-$f2 = object2;
    }

    public final void onClick(DialogInterface dialogInterface, int n) {
        this.$m$0(dialogInterface, n);
    }
}
