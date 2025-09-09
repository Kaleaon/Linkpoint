package com.lumiyaviewer.lumiya.ui.chat;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;

/* renamed from: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$4T-RyU3GIOc1CH0v3ewFouMG3lk  reason: invalid class name */
final /* synthetic */ class $Lambda$4TRyU3GIOc1CH0v3ewFouMG3lk implements Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private final /* synthetic */ Object f240$f0;

    private final /* synthetic */ void $m$0(Object obj) {
        ((ChatterThumbnailData) this.f240$f0).m416com_lumiyaviewer_lumiya_ui_chat_ChatterThumbnailDatamthref0((CurrentLocationInfo) obj);
    }

    public /* synthetic */ $Lambda$4TRyU3GIOc1CH0v3ewFouMG3lk(Object obj) {
        this.f240$f0 = obj;
    }

    public final void onData(Object obj) {
        $m$0(obj);
    }
}
