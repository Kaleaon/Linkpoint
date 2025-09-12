/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.slproto.modules.voice;

import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLAsyncRequest;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.modules.voice.SLVoice;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY$7
implements LLSDXMLAsyncRequest.LLSDXMLResultListener {
    private final int -$f0;
    private final Object -$f1;

    private final /* synthetic */ void $m$0(LLSDNode lLSDNode) {
        ((SLVoice)this.-$f1).lambda$-com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoice_12525(this.-$f0, lLSDNode);
    }

    public /* synthetic */ _$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY$7(int n, Object object) {
        this.-$f0 = n;
        this.-$f1 = object;
    }

    @Override
    public final void onLLSDXMLResult(LLSDNode lLSDNode) {
        this.$m$0(lLSDNode);
    }
}
