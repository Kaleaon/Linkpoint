/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnClickListener
 */
package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserPicksProfileTab;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$0JruYUVxhc8cYQ6nJZD1LVnQE5A$3
implements DialogInterface.OnClickListener {
    private final int -$f0;
    private final Object -$f1;
    private final Object -$f2;
    private final Object -$f3;
    private final Object -$f4;

    private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int n) {
        ((UserPicksProfileTab)this.-$f1).lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_UserPicksProfileTab_4543((SLAgentCircuit)this.-$f2, (String)this.-$f3, (ParcelData)this.-$f4, this.-$f0, dialogInterface, n);
    }

    public /* synthetic */ _$Lambda$0JruYUVxhc8cYQ6nJZD1LVnQE5A$3(int n, Object object, Object object2, Object object3, Object object4) {
        this.-$f0 = n;
        this.-$f1 = object;
        this.-$f2 = object2;
        this.-$f3 = object3;
        this.-$f4 = object4;
    }

    public final void onClick(DialogInterface dialogInterface, int n) {
        this.$m$0(dialogInterface, n);
    }
}
