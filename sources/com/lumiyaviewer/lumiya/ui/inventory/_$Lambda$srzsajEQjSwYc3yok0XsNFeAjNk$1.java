/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.ui.inventory;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.ui.inventory.NotecardEditActivity;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk$1
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((NotecardEditActivity)this.-$f0).-com_lumiyaviewer_lumiya_ui_inventory_NotecardEditActivity-mthref-1((SLAgentCircuit)object);
    }

    public /* synthetic */ _$Lambda$srzsajEQjSwYc3yok0XsNFeAjNk$1(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
