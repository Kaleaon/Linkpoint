/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.ui.render;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.ui.render.CardboardActivity;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$15
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((CardboardActivity)this.-$f0).-com_lumiyaviewer_lumiya_ui_render_CardboardActivity-mthref-3((CurrentLocationInfo)object);
    }

    public /* synthetic */ _$Lambda$yhBpPTpVtOAhPHTLXL5B0hI4gXA$15(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
