/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.ui.chat.profiles;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.messages.PickInfoReply;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserPickFragment;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$pe_zD6dKvPMIxwvN5gLJ2hSMvgo$3
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((UserPickFragment)this.-$f0).-com_lumiyaviewer_lumiya_ui_chat_profiles_UserPickFragment-mthref-0((PickInfoReply)object);
    }

    public /* synthetic */ _$Lambda$pe_zD6dKvPMIxwvN5gLJ2hSMvgo$3(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
