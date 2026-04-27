/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 */
package com.lumiyaviewer.lumiya.ui.common;

import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.TeleportProgressDialog;
import java.util.UUID;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$8gWLFwrhoxKapEC8iWggaUhFR1c$2
implements Runnable {
    private final Object -$f0;
    private final Object -$f1;
    private final Object -$f2;
    private final Object -$f3;

    private final /* synthetic */ void $m$0() {
        TeleportProgressDialog.lambda$-com_lumiyaviewer_lumiya_ui_common_TeleportProgressDialog_1322((SLAgentCircuit)this.-$f0, (UUID)this.-$f1, (Context)this.-$f2, (UserManager)this.-$f3);
    }

    public /* synthetic */ _$Lambda$8gWLFwrhoxKapEC8iWggaUhFR1c$2(Object object, Object object2, Object object3, Object object4) {
        this.-$f0 = object;
        this.-$f1 = object2;
        this.-$f2 = object3;
        this.-$f3 = object4;
    }

    @Override
    public final void run() {
        this.$m$0();
    }
}
