/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnClickListener
 */
package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.slproto.messages.PickInfoReply;
import com.lumiyaviewer.lumiya.slproto.users.manager.AvatarPickKey;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserPickFragment;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$pe_zD6dKvPMIxwvN5gLJ2hSMvgo$7
implements DialogInterface.OnClickListener {
    private final Object -$f0;
    private final Object -$f1;
    private final Object -$f2;
    private final Object -$f3;

    private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int n) {
        ((UserPickFragment)this.-$f0).lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_UserPickFragment_9741((UserManager)this.-$f1, (AvatarPickKey)this.-$f2, (PickInfoReply)this.-$f3, dialogInterface, n);
    }

    public /* synthetic */ _$Lambda$pe_zD6dKvPMIxwvN5gLJ2hSMvgo$7(Object object, Object object2, Object object3, Object object4) {
        this.-$f0 = object;
        this.-$f1 = object2;
        this.-$f2 = object3;
        this.-$f3 = object4;
    }

    public final void onClick(DialogInterface dialogInterface, int n) {
        this.$m$0(dialogInterface, n);
    }
}
