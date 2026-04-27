/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnClickListener
 */
package com.lumiyaviewer.lumiya.ui.objects;

import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import com.lumiyaviewer.lumiya.ui.objects.ObjectDetailsFragment;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8$4
implements DialogInterface.OnClickListener {
    private final Object -$f0;
    private final Object -$f1;
    private final Object -$f2;

    private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int n) {
        ObjectDetailsFragment.lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_11291((SLAgentCircuit)this.-$f0, (SLObjectProfileData)this.-$f1, (String)this.-$f2, dialogInterface, n);
    }

    public /* synthetic */ _$Lambda$IbcMrpWxKnmu4WU7ZN8rETVfqs8$4(Object object, Object object2, Object object3) {
        this.-$f0 = object;
        this.-$f1 = object2;
        this.-$f2 = object3;
    }

    public final void onClick(DialogInterface dialogInterface, int n) {
        this.$m$0(dialogInterface, n);
    }
}
