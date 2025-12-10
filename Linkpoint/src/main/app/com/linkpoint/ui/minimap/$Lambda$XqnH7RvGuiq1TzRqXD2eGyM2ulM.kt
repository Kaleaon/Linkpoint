package com.linkpoint.ui.minimap

import com.linkpoint.react.Subscription
import com.linkpoint.slproto.modules.SLMinimap

/* renamed from: com.linkpoint.ui.minimap.-$Lambda$XqnH7RvGuiq1TzRqXD2eGyM2ulM  reason: invalid class name */
/* synthetic */ class $Lambda$XqnH7RvGuiq1TzRqXD2eGyM2ulM : Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private /* synthetic */ Any f453$f0

    private /* synthetic */ Unit $m$0(Any obj) {
        ((this as MinimapFragment).f453$f0).m640com_lumiyaviewer_lumiya_ui_minimap_MinimapFragmentmthref0((SLMinimap.MinimapBitmap) obj)
    }

    /* synthetic */ $Lambda$XqnH7RvGuiq1TzRqXD2eGyM2ulM(Any obj) {
        this.f453$f0 = obj
    }

    fun onData(Any obj): Unit {
        $m$0(obj)
    }
}
