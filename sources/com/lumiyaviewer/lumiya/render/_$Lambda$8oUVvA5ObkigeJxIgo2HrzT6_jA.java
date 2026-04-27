/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.render;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.render.WorldViewRenderer;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$8oUVvA5ObkigeJxIgo2HrzT6_jA
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((WorldViewRenderer)this.-$f0).-com_lumiyaviewer_lumiya_render_WorldViewRenderer-mthref-0((SLAgentCircuit)object);
    }

    public /* synthetic */ _$Lambda$8oUVvA5ObkigeJxIgo2HrzT6_jA(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
