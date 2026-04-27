/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.ui.chat.profiles;

import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import com.lumiyaviewer.lumiya.ui.chat.profiles.ProfileTextFieldEditFragment;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
final class _$Lambda$6hJe_KPqqQcY7xiCxogddm78oYc
implements Subscription.OnData {
    private final Object -$f0;

    private final /* synthetic */ void $m$0(Object object) {
        ((ProfileTextFieldEditFragment)this.-$f0).-com_lumiyaviewer_lumiya_ui_chat_profiles_ProfileTextFieldEditFragment-mthref-0((AvatarPropertiesReply)object);
    }

    public /* synthetic */ _$Lambda$6hJe_KPqqQcY7xiCxogddm78oYc(Object object) {
        this.-$f0 = object;
    }

    public final void onData(Object object) {
        this.$m$0(object);
    }
}
