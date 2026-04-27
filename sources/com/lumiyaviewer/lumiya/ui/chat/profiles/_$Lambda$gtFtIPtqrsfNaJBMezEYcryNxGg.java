/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.ui.chat.profiles;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarNotesReply;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserNotesEditFragment;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$gtFtIPtqrsfNaJBMezEYcryNxGg
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((UserNotesEditFragment)this.-$f0).-com_lumiyaviewer_lumiya_ui_chat_profiles_UserNotesEditFragment-mthref-0((AvatarNotesReply)object);
    }

    public /* synthetic */ _$Lambda$gtFtIPtqrsfNaJBMezEYcryNxGg(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
