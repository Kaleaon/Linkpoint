/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.orm.InventoryEntryList;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo$1
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((SLAvatarAppearance)this.-$f0).-com_lumiyaviewer_lumiya_slproto_modules_SLAvatarAppearance-mthref-1((InventoryEntryList)object);
    }

    public /* synthetic */ _$Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo$1(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
