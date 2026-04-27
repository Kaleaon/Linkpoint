/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnClickListener
 */
package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.chat.profiles.GroupMembersProfileTab;
import java.util.UUID;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY$4
implements DialogInterface.OnClickListener {
    private final Object -$f0;
    private final Object -$f1;
    private final Object -$f2;

    private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int n) {
        ((GroupMembersProfileTab)this.-$f0).lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMembersProfileTab_13581((UUID)this.-$f1, (ChatterID.ChatterIDUser)this.-$f2, dialogInterface, n);
    }

    public /* synthetic */ _$Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY$4(Object object, Object object2, Object object3) {
        this.-$f0 = object;
        this.-$f1 = object2;
        this.-$f2 = object3;
    }

    public final void onClick(DialogInterface dialogInterface, int n) {
        this.$m$0(dialogInterface, n);
    }
}
