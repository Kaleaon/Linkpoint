/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.ui.objects;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager;
import com.lumiyaviewer.lumiya.ui.objects.ObjectSelectorFragment;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$rXtKRyOts6GGB3GxWNYA5oEvU2Y
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((ObjectSelectorFragment)this.-$f0).lambda$-com_lumiyaviewer_lumiya_ui_objects_ObjectSelectorFragment_10971((ObjectsManager.ObjectDisplayList)object);
    }

    public /* synthetic */ _$Lambda$rXtKRyOts6GGB3GxWNYA5oEvU2Y(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
