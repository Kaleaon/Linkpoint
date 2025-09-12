/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnClickListener
 */
package com.lumiyaviewer.lumiya.ui.myava;

import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListEntry;
import com.lumiyaviewer.lumiya.ui.myava.MuteListFragment;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$dntbaqhB2OOLQW5t89NMwUjCLX4$3
implements DialogInterface.OnClickListener {
    private final Object -$f0;
    private final Object -$f1;

    private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int n) {
        ((MuteListFragment)this.-$f0).lambda$-com_lumiyaviewer_lumiya_ui_myava_MuteListFragment_5870((MuteListEntry)this.-$f1, dialogInterface, n);
    }

    public /* synthetic */ _$Lambda$dntbaqhB2OOLQW5t89NMwUjCLX4$3(Object object, Object object2) {
        this.-$f0 = object;
        this.-$f1 = object2;
    }

    public final void onClick(DialogInterface dialogInterface, int n) {
        this.$m$0(dialogInterface, n);
    }
}
