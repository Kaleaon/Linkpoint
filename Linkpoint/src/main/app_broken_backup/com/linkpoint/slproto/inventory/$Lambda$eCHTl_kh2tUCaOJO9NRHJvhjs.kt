package com.linkpoint.slproto.inventory

import com.linkpoint.react.Subscription

/* renamed from: com.linkpoint.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs  reason: invalid class name */
/* synthetic */ class $Lambda$eCHTl_kh2tUCaOJO9NRHJvhjs : Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private /* synthetic */ Object f75$f0

    private /* synthetic */ Unit $m$0(Object obj) {
        ((this as SLInventory).f75$f0).m174com_lumiyaviewer_lumiya_slproto_inventory_SLInventorymthref0((SLInventoryEntry) obj)
    }

    /* synthetic */ $Lambda$eCHTl_kh2tUCaOJO9NRHJvhjs(Object obj) {
        this.f75$f0 = obj
    }

    fun onData(Object obj): Unit {
        $m$0(obj)
    }
}
