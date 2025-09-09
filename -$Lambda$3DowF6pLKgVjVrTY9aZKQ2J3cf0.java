package com.lumiyaviewer.lumiya;

import com.lumiyaviewer.lumiya.react.Subscription.OnData;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotifications;

final /* synthetic */ class -$Lambda$3DowF6pLKgVjVrTY9aZKQ2J3cf0 implements OnData {
    /* renamed from: -$f0 */
    private final /* synthetic */ Object f0-$f0;

    /* renamed from: com.lumiyaviewer.lumiya.-$Lambda$3DowF6pLKgVjVrTY9aZKQ2J3cf0$1 */
    final /* synthetic */ class AnonymousClass1 implements OnData {
        /* renamed from: -$f0 */
        private final /* synthetic */ Object f1-$f0;

        private final /* synthetic */ void $m$0(Object obj) {
            ((GridConnectionService) this.f1-$f0).m11-com_lumiyaviewer_lumiya_GridConnectionService-mthref-1((UnreadNotifications) obj);
        }

        public /* synthetic */ AnonymousClass1(Object obj) {
            this.f1-$f0 = obj;
        }

        public final void onData(Object obj) {
            $m$0(obj);
        }
    }

    /* renamed from: com.lumiyaviewer.lumiya.-$Lambda$3DowF6pLKgVjVrTY9aZKQ2J3cf0$2 */
    final /* synthetic */ class AnonymousClass2 implements OnChatterNameUpdated {
        /* renamed from: -$f0 */
        private final /* synthetic */ Object f2-$f0;

        private final /* synthetic */ void $m$0(ChatterNameRetriever chatterNameRetriever) {
            ((GridConnectionService) this.f2-$f0).m12lambda$-com_lumiyaviewer_lumiya_GridConnectionService_20777(chatterNameRetriever);
        }

        public /* synthetic */ AnonymousClass2(Object obj) {
            this.f2-$f0 = obj;
        }

        public final void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
            $m$0(chatterNameRetriever);
        }
    }

    private final /* synthetic */ void $m$0(Object obj) {
        ((GridConnectionService) this.f0-$f0).m10-com_lumiyaviewer_lumiya_GridConnectionService-mthref-0((CurrentLocationInfo) obj);
    }

    public /* synthetic */ -$Lambda$3DowF6pLKgVjVrTY9aZKQ2J3cf0(Object obj) {
        this.f0-$f0 = obj;
    }

    public final void onData(Object obj) {
        $m$0(obj);
    }
}
