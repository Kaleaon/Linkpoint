/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.ui.chat.profiles;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.messages.PickInfoReply;
import com.lumiyaviewer.lumiya.ui.chat.profiles.PickDescriptionEditFragment;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$Y7Ne2VWglUcvjFUgJydWWKVgIXM
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((PickDescriptionEditFragment)this.-$f0).-com_lumiyaviewer_lumiya_ui_chat_profiles_PickDescriptionEditFragment-mthref-0((PickInfoReply)object);
    }

    public /* synthetic */ _$Lambda$Y7Ne2VWglUcvjFUgJydWWKVgIXM(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
