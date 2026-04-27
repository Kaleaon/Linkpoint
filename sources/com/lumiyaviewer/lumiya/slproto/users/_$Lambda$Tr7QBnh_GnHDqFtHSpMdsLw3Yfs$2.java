/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.slproto.users;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$Tr7QBnh_GnHDqFtHSpMdsLw3Yfs$2
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((ChatterNameRetriever)this.-$f0).-com_lumiyaviewer_lumiya_slproto_users_ChatterNameRetriever-mthref-2((GroupProfileReply)object);
    }

    public /* synthetic */ _$Lambda$Tr7QBnh_GnHDqFtHSpMdsLw3Yfs$2(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
