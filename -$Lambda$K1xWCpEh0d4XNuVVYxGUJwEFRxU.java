package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.dao.UserName;
import com.lumiyaviewer.lumiya.react.Subscription.OnData;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import java.util.UUID;

final /* synthetic */ class -$Lambda$K1xWCpEh0d4XNuVVYxGUJwEFRxU implements OnData {
    /* renamed from: -$f0 */
    private final /* synthetic */ Object f505-$f0;

    /* renamed from: com.lumiyaviewer.lumiya.slproto.-$Lambda$K1xWCpEh0d4XNuVVYxGUJwEFRxU$1 */
    final /* synthetic */ class AnonymousClass1 implements Runnable {
        /* renamed from: -$f0 */
        private final /* synthetic */ Object f506-$f0;
        /* renamed from: -$f1 */
        private final /* synthetic */ Object f507-$f1;
        /* renamed from: -$f2 */
        private final /* synthetic */ Object f508-$f2;

        private final /* synthetic */ void $m$0() {
            ((SLAgentCircuit) this.f506-$f0).m41lambda$-com_lumiyaviewer_lumiya_slproto_SLAgentCircuit_77024((SLInventoryEntry) this.f507-$f1, (UUID) this.f508-$f2);
        }

        public /* synthetic */ AnonymousClass1(Object obj, Object obj2, Object obj3) {
            this.f506-$f0 = obj;
            this.f507-$f1 = obj2;
            this.f508-$f2 = obj3;
        }

        public final void run() {
            $m$0();
        }
    }

    private final /* synthetic */ void $m$0(Object obj) {
        ((SLAgentCircuit) this.f505-$f0).m40lambda$-com_lumiyaviewer_lumiya_slproto_SLAgentCircuit_14593((UserName) obj);
    }

    public /* synthetic */ -$Lambda$K1xWCpEh0d4XNuVVYxGUJwEFRxU(Object obj) {
        this.f505-$f0 = obj;
    }

    public final void onData(Object obj) {
        $m$0(obj);
    }
}
