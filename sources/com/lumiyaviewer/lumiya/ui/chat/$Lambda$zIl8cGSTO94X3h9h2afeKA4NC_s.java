package com.lumiyaviewer.lumiya.ui.chat;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;

/* renamed from: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$zIl8cGSTO94X3h9h2afeKA4NC_s  reason: invalid class name */
final /* synthetic */ class $Lambda$zIl8cGSTO94X3h9h2afeKA4NC_s implements Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private final /* synthetic */ Object f250$f0;

    private final /* synthetic */ void $m$0(Object obj) {
        ((ContactsFragment) this.f250$f0).m419com_lumiyaviewer_lumiya_ui_chat_ContactsFragmentmthref0((CurrentLocationInfo) obj);
    }

    public /* synthetic */ $Lambda$zIl8cGSTO94X3h9h2afeKA4NC_s(Object obj) {
        this.f250$f0 = obj;
    }

    public final void onData(Object obj) {
        $m$0(obj);
    }
}
