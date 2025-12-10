package com.linkpoint.ui.objects
import java.util.*

import com.linkpoint.react.Subscription
import com.linkpoint.slproto.users.manager.ObjectsManager

/* renamed from: com.linkpoint.ui.objects.-$Lambda$rXtKRyOts6GGB3GxWNYA5oEvU2Y  reason: invalid class name */
/* synthetic */ class $Lambda$rXtKRyOts6GGB3GxWNYA5oEvU2Y : Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private /* synthetic */ Any f499$f0

    private /* synthetic */ Unit $m$0(Any obj) {
        ((this as ObjectSelectorFragment).f499$f0).m683lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectSelectorFragment_10971((ObjectsManager.ObjectDisplayList) obj)
    }

    /* synthetic */ $Lambda$rXtKRyOts6GGB3GxWNYA5oEvU2Y(Any obj) {
        this.f499$f0 = obj
    }

    fun onData(Any obj): Unit {
        $m$0(obj)
    }
}
