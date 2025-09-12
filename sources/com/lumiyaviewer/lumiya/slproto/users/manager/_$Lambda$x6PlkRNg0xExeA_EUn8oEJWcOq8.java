/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterSubscription;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$x6PlkRNg0xExeA_EUn8oEJWcOq8
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((ChatterSubscription)this.-$f0).-com_lumiyaviewer_lumiya_slproto_users_manager_ChatterSubscription-mthref-0((VoiceChatInfo)object);
    }

    public /* synthetic */ _$Lambda$x6PlkRNg0xExeA_EUn8oEJWcOq8(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
