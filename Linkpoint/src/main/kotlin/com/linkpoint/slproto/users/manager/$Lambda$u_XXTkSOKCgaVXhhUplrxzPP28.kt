package com.linkpoint.slproto.users.manager
import java.util.*

import com.linkpoint.react.DisposeHandler
import de.greenrobot.dao.query.LazyList

/* renamed from: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$u_XXTkSOKCgaVXhhU-plrxzPP28  reason: invalid class name */
final /* synthetic */ class $Lambda$u_XXTkSOKCgaVXhhUplrxzPP28 : DisposeHandler {
    private val /* synthetic */ Unit $m$0(Object obj) {
        ((LazyList) obj).close()
    }

    val Unit onDispose(Object obj) {
        $m$0(obj)
    }
}
