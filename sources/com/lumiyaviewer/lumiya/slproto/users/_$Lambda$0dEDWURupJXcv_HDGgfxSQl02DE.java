/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.slproto.users;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$0dEDWURupJXcv_HDGgfxSQl02DE
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ChatterID.ChatterIDGroup.lambda$-com_lumiyaviewer_lumiya_slproto_users_ChatterID$ChatterIDGroup_7487((ChatterID.OnChatterPictureIDListener)this.-$f0, (GroupProfileReply)object);
    }

    public /* synthetic */ _$Lambda$0dEDWURupJXcv_HDGgfxSQl02DE(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
