/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.ui.minimap;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.ui.minimap.MinimapActivity;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$HQUtmVzLYkemE78mCklVmVxMXms
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((MinimapActivity)this.-$f0).-com_lumiyaviewer_lumiya_ui_minimap_MinimapActivity-mthref-0((CurrentLocationInfo)object);
    }

    public /* synthetic */ _$Lambda$HQUtmVzLYkemE78mCklVmVxMXms(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
