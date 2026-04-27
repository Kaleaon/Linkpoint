/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.slproto.users;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$0dEDWURupJXcv_HDGgfxSQl02DE$1
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ChatterID.ChatterIDUser.lambda$-com_lumiyaviewer_lumiya_slproto_users_ChatterID$ChatterIDUser_6120((ChatterID.OnChatterPictureIDListener)this.-$f0, (AvatarPropertiesReply)object);
    }

    public /* synthetic */ _$Lambda$0dEDWURupJXcv_HDGgfxSQl02DE$1(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
