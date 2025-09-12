/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.ui.chat;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.ui.chat.ChatNewActivity;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$NRCeOQv_yeRY8P8t9O3BV_sPyX4
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((ChatNewActivity)this.-$f0).lambda$-com_lumiyaviewer_lumiya_ui_chat_ChatNewActivity_4384((CurrentLocationInfo)object);
    }

    public /* synthetic */ _$Lambda$NRCeOQv_yeRY8P8t9O3BV_sPyX4(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
