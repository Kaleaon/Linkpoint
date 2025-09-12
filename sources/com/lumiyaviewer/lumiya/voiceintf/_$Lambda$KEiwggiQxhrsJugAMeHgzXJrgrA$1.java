/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.voiceintf;

import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceRinging;
import com.lumiyaviewer.lumiya.voiceintf.VoicePluginServiceConnection;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$KEiwggiQxhrsJugAMeHgzXJrgrA$1
implements ChatterNameRetriever.OnChatterNameUpdated {
    private final Object -$f0;
    private final Object -$f1;

    private final /* synthetic */ void $m$0(ChatterNameRetriever chatterNameRetriever) {
        ((VoicePluginServiceConnection)this.-$f0).lambda$-com_lumiyaviewer_lumiya_voiceintf_VoicePluginServiceConnection_13701((VoiceRinging)this.-$f1, chatterNameRetriever);
    }

    public /* synthetic */ _$Lambda$KEiwggiQxhrsJugAMeHgzXJrgrA$1(Object object, Object object2) {
        this.-$f0 = object;
        this.-$f1 = object2;
    }

    @Override
    public final void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        this.$m$0(chatterNameRetriever);
    }
}
