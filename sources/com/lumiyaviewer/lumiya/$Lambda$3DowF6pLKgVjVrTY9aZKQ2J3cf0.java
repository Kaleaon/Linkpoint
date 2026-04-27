package com.lumiyaviewer.lumiya;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;

/* renamed from: com.lumiyaviewer.lumiya.-$Lambda$3DowF6pLKgVjVrTY9aZKQ2J3cf0  reason: invalid class name */
final /* synthetic */ class $Lambda$3DowF6pLKgVjVrTY9aZKQ2J3cf0 implements Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private final /* synthetic */ Object f0$f0;

    private final /* synthetic */ void $m$0(Object obj) {
        ((GridConnectionService) this.f0$f0).m7com_lumiyaviewer_lumiya_GridConnectionServicemthref0((CurrentLocationInfo) obj);
    }

    public /* synthetic */ $Lambda$3DowF6pLKgVjVrTY9aZKQ2J3cf0(Object obj) {
        this.f0$f0 = obj;
    }

    public final void onData(Object obj) {
        $m$0(obj);
    }
}
