package com.linkpoint.ui.minimap

import com.linkpoint.react.Subscription
import com.linkpoint.slproto.users.manager.CurrentLocationInfo

/* renamed from: com.linkpoint.ui.minimap.-$Lambda$HQUtmVzLYkemE78mCklVmVxMXms  reason: invalid class name */
/* synthetic */ class $Lambda$HQUtmVzLYkemE78mCklVmVxMXms : Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private /* synthetic */ Any f452$f0

    private /* synthetic */ Unit $m$0(Any obj) {
        ((this as MinimapActivity).f452$f0).m639com_lumiyaviewer_lumiya_ui_minimap_MinimapActivitymthref0((CurrentLocationInfo) obj)
    }

    /* synthetic */ $Lambda$HQUtmVzLYkemE78mCklVmVxMXms(Any obj) {
        this.f452$f0 = obj
    }

    fun onData(Any obj): Unit {
        $m$0(obj)
    }
}
