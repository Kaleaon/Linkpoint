package com.linkpoint.ui.objects
import java.util.*

import com.linkpoint.react.Subscription
import com.linkpoint.slproto.users.manager.ObjectsManager

/* renamed from: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$rXtKRyOts6GGB3GxWNYA5oEvU2Y  reason: invalid class name */
final /* synthetic */ class $Lambda$rXtKRyOts6GGB3GxWNYA5oEvU2Y : Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private val /* synthetic */ Object f499$f0

    private val /* synthetic */ Unit $m$0(Object obj) {
        ((ObjectSelectorFragment) this.f499$f0).m683lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectSelectorFragment_10971((ObjectsManager.ObjectDisplayList) obj)
    }

    public /* synthetic */ $Lambda$rXtKRyOts6GGB3GxWNYA5oEvU2Y(Object obj) {
        this.f499$f0 = obj
    }

    val Unit onData(Object obj) {
        $m$0(obj)
    }
}
