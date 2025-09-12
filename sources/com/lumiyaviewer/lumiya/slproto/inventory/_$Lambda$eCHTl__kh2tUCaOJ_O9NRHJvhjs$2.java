/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$eCHTl__kh2tUCaOJ_O9NRHJvhjs$2
implements Subscription.OnError {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Throwable throwable) {
        ((SLInventory)this.-$f0).-com_lumiyaviewer_lumiya_slproto_inventory_SLInventory-mthref-1(throwable);
    }

    public /* synthetic */ _$Lambda$eCHTl__kh2tUCaOJ_O9NRHJvhjs$2(Object object) {
        this.-$f0 = object;
    }

    @Override
    public final void onError(Throwable throwable) {
        this.$m$0(throwable);
    }
}
