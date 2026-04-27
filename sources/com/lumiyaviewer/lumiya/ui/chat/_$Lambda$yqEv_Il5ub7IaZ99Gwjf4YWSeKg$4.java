/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.ui.chat;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg$4
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((ChatFragment)this.-$f0).-com_lumiyaviewer_lumiya_ui_chat_ChatFragment-mthref-2((VoiceChatInfo)object);
    }

    public /* synthetic */ _$Lambda$yqEv_Il5ub7IaZ99Gwjf4YWSeKg$4(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
