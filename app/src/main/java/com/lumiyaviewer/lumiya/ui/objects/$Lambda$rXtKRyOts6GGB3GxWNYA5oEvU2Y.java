package com.lumiyaviewer.lumiya.ui.objects;
import java.util.*;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager;

/* renamed from: com.lumiyaviewer.lumiya.ui.objects.-$Lambda$rXtKRyOts6GGB3GxWNYA5oEvU2Y  reason: invalid class name */
final /* synthetic */ class $Lambda$rXtKRyOts6GGB3GxWNYA5oEvU2Y implements Subscription.OnData {

    /* renamed from: -$f0  reason: not valid java name */
    private final /* synthetic */ Object f499$f0;

    private final /* synthetic */ void $m$0(Object obj) {
        ((ObjectSelectorFragment) this.f499$f0).m683lambda$com_lumiyaviewer_lumiya_ui_objects_ObjectSelectorFragment_10971((ObjectsManager.ObjectDisplayList) obj);
    }

    public /* synthetic */ $Lambda$rXtKRyOts6GGB3GxWNYA5oEvU2Y(Object obj) {
        this.f499$f0 = obj;
    }

    public final void onData(Object obj) {
        $m$0(obj);
    }
}
