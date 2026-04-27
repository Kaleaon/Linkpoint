package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.react.DisposeHandler;
import de.greenrobot.dao.query.LazyList;

/* renamed from: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$u_XXTkSOKCgaVXhhU-plrxzPP28  reason: invalid class name */
final /* synthetic */ class $Lambda$u_XXTkSOKCgaVXhhUplrxzPP28 implements DisposeHandler {
    private final /* synthetic */ void $m$0(Object obj) {
        ((LazyList) obj).close();
    }

    public final void onDispose(Object obj) {
        $m$0(obj);
    }
}
