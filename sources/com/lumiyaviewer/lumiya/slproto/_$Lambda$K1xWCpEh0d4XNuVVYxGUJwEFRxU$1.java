/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import java.util.UUID;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$K1xWCpEh0d4XNuVVYxGUJwEFRxU$1
implements Runnable {
    private final Object -$f0;
    private final Object -$f1;
    private final Object -$f2;

    private final /* synthetic */ void $m$0() {
        ((SLAgentCircuit)this.-$f0).lambda$-com_lumiyaviewer_lumiya_slproto_SLAgentCircuit_77024((SLInventoryEntry)this.-$f1, (UUID)this.-$f2);
    }

    public /* synthetic */ _$Lambda$K1xWCpEh0d4XNuVVYxGUJwEFRxU$1(Object object, Object object2, Object object3) {
        this.-$f0 = object;
        this.-$f1 = object2;
        this.-$f2 = object3;
    }

    @Override
    public final void run() {
        this.$m$0();
    }
}
