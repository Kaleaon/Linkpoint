/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya;

import com.lumiyaviewer.lumiya.GridConnectionService;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$3DowF6pLKgVjVrTY9aZKQ2J3cf0
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((GridConnectionService)((Object)this.-$f0)).-com_lumiyaviewer_lumiya_GridConnectionService-mthref-0((CurrentLocationInfo)object);
    }

    public /* synthetic */ _$Lambda$3DowF6pLKgVjVrTY9aZKQ2J3cf0(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
