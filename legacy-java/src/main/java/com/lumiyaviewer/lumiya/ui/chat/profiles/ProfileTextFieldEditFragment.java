package com.lumiyaviewer.lumiya.ui.chat.profiles;

import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.common.TextFieldEditFragment;
import java.util.UUID;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public abstract class ProfileTextFieldEditFragment extends TextFieldEditFragment {
    private Subscription<UUID, AvatarPropertiesReply> avatarProperties = null;

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: onAvatarProperties, reason: merged with bridge method [inline-methods] */
    public abstract void m517x7aa22308(AvatarPropertiesReply avatarPropertiesReply);

    @Override // com.lumiyaviewer.lumiya.ui.common.ChatterFragment
    protected void onShowUser(@Nullable ChatterID chatterID) {
        if (this.avatarProperties != null) {
            this.avatarProperties.unsubscribe();
            this.avatarProperties = null;
        }
        if (this.userManager == null || !(chatterID instanceof ChatterID.ChatterIDUser)) {
            return;
        }
        this.avatarProperties = this.userManager.getAvatarProperties().getPool().subscribe((Subscribable) ((ChatterID.ChatterIDUser) chatterID).getChatterUUID(), UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$6hJe-KPqqQcY7xiCxogddm78oYc
            private final /* synthetic */ void $m$0(Object obj) {
                ((ProfileTextFieldEditFragment) this).m517x7aa22308((AvatarPropertiesReply) obj);
            }

            @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
            public final void onData(Object obj) {
                $m$0(obj);
            }
        });
    }
}
