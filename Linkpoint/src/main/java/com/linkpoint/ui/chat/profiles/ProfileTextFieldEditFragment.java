package com.linkpoint.ui.chat.profiles;

import com.linkpoint.react.Subscription;
import com.linkpoint.react.UIThreadExecutor;
import com.linkpoint.slproto.messages.AvatarPropertiesReply;
import com.linkpoint.slproto.users.ChatterID;
import com.linkpoint.ui.common.TextFieldEditFragment;
import java.util.UUID;
import javax.annotation.Nullable;

public abstract class ProfileTextFieldEditFragment extends TextFieldEditFragment {
    private Subscription<UUID, AvatarPropertiesReply> avatarProperties = null;

    /* access modifiers changed from: protected */
    /* renamed from: onAvatarProperties */
    public abstract void m507com_lumiyaviewer_lumiya_ui_chat_profiles_ProfileTextFieldEditFragmentmthref0(AvatarPropertiesReply avatarPropertiesReply);

    /* access modifiers changed from: protected */
    public void onShowUser(@Nullable ChatterID chatterID) {
        if (this.avatarProperties != null) {
            this.avatarProperties.unsubscribe();
            this.avatarProperties = null;
        }
        if (this.userManager != null && (chatterID instanceof ChatterID.ChatterIDUser)) {
            this.avatarProperties = this.userManager.getAvatarProperties().getPool().subscribe(((ChatterID.ChatterIDUser) chatterID).getChatterUUID(), UIThreadExecutor.getInstance(), new $Lambda$6hJeKPqqQcY7xiCxogddm78oYc(this));
        }
    }
}
