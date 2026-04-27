/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya;

import com.lumiyaviewer.lumiya.GridConnectionService;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$3DowF6pLKgVjVrTY9aZKQ2J3cf0$2
implements ChatterNameRetriever.OnChatterNameUpdated {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(ChatterNameRetriever chatterNameRetriever) {
        ((GridConnectionService)((Object)this.-$f0)).lambda$-com_lumiyaviewer_lumiya_GridConnectionService_20777(chatterNameRetriever);
    }

    public /* synthetic */ _$Lambda$3DowF6pLKgVjVrTY9aZKQ2J3cf0$2(Object object) {
        this.-$f0 = object;
    }

    @Override
    public final void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        this.$m$0(chatterNameRetriever);
    }
}
