/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$u_XXTkSOKCgaVXhhU_plrxzPP28$2
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((GroupManager)this.-$f0).-com_lumiyaviewer_lumiya_slproto_users_manager_GroupManager-mthref-0((AvatarGroupList)object);
    }

    public /* synthetic */ _$Lambda$u_XXTkSOKCgaVXhhU_plrxzPP28$2(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
