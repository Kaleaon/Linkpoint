package com.lumiyaviewer.lumiya.ui.chat;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;

/* renamed from: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$NRCeOQv-yeRY8P8t9O3BV_sPyX4  reason: invalid class name */
final /* synthetic */ class $Lambda$NRCeOQvyeRY8P8t9O3BV_sPyX4 implements Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private final /* synthetic */ Object f243$f0;

    private final /* synthetic */ void $m$0(Object obj) {
        ((ChatNewActivity) this.f243$f0).m414lambda$com_lumiyaviewer_lumiya_ui_chat_ChatNewActivity_4384((CurrentLocationInfo) obj);
    }

    public /* synthetic */ $Lambda$NRCeOQvyeRY8P8t9O3BV_sPyX4(Object obj) {
        this.f243$f0 = obj;
    }

    public final void onData(Object obj) {
        $m$0(obj);
    }
}
