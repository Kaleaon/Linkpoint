/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.ui.voice;

import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.ui.voice.VoiceStatusView;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8$10
implements ChatterNameRetriever.OnChatterNameUpdated {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(ChatterNameRetriever chatterNameRetriever) {
        ((VoiceStatusView)((Object)this.-$f0)).lambda$-com_lumiyaviewer_lumiya_ui_voice_VoiceStatusView_24065(chatterNameRetriever);
    }

    public /* synthetic */ _$Lambda$LRu9qjGWbEJmZF4NfrRGigLGXl8$10(Object object) {
        this.-$f0 = object;
    }

    @Override
    public final void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        this.$m$0(chatterNameRetriever);
    }
}
