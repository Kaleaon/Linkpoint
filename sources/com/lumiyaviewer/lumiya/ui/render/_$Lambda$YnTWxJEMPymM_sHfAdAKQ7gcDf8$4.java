/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.ui.render;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.users.manager.MyAvatarState;
import com.lumiyaviewer.lumiya.ui.render.WorldViewActivity;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8$4
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((WorldViewActivity)this.-$f0).-com_lumiyaviewer_lumiya_ui_render_WorldViewActivity-mthref-1((MyAvatarState)object);
    }

    public /* synthetic */ _$Lambda$YnTWxJEMPymM_sHfAdAKQ7gcDf8$4(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
