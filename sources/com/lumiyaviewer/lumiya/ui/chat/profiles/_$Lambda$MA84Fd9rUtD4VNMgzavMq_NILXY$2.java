/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.ui.chat.profiles;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.ui.chat.profiles.GroupMembersProfileTab;
import java.util.UUID;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY$2
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((GroupMembersProfileTab)this.-$f0).-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMembersProfileTab-mthref-0((UUID)object);
    }

    public /* synthetic */ _$Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY$2(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
