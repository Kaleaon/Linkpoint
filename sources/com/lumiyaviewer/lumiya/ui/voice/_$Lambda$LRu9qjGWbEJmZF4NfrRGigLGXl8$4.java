/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.ui.voice;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8$4
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((VoiceStatusView)((Object)this.-$f0)).-com_lumiyaviewer_lumiya_ui_voice_VoiceStatusView-mthref-1((VoiceChatInfo)object);
    }

    public /* synthetic */ _$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8$4(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
