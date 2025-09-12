/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.ui.minimap;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.modules.SLMinimap;
import com.lumiyaviewer.lumiya.ui.minimap.MinimapFragment;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$XqnH7RvGuiq1TzRqXD2eGyM2ulM$1
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((MinimapFragment)this.-$f0).-com_lumiyaviewer_lumiya_ui_minimap_MinimapFragment-mthref-1((SLMinimap.UserLocations)object);
    }

    public /* synthetic */ _$Lambda$XqnH7RvGuiq1TzRqXD2eGyM2ulM$1(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
