package com.linkpoint.slproto.users.manager
import java.util.*

import com.linkpoint.react.DisposeHandler
import de.greenrobot.dao.query.LazyList

/* renamed from: com.linkpoint.slproto.users.manager.-$Lambda$u_XXTkSOKCgaVXhhU-plrxzPP28  reason: invalid class name */
/* synthetic */ class $Lambda$u_XXTkSOKCgaVXhhUplrxzPP28 : DisposeHandler {
    private /* synthetic */ Unit $m$0(Any obj) {
        ((LazyList) obj).close()
    }

    fun onDispose(Any obj): Unit {
        $m$0(obj)
    }
}
