// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.ui.common.TextFieldEditFragment;

public abstract class ProfileTextFieldEditFragment extends TextFieldEditFragment
{
    private Subscription<UUID, AvatarPropertiesReply> avatarProperties;
    
    public ProfileTextFieldEditFragment() {
        this.avatarProperties = null;
    }
    
    protected abstract void onAvatarProperties(final AvatarPropertiesReply p0);
    
    @Override
    protected void onShowUser(@Nullable final ChatterID chatterID) {
        if (this.avatarProperties != null) {
            this.avatarProperties.unsubscribe();
            this.avatarProperties = null;
        }
        if (this.userManager != null && chatterID instanceof ChatterID.ChatterIDUser) {
            this.avatarProperties = this.userManager.getAvatarProperties().getPool().subscribe(((ChatterID.ChatterIDUser)chatterID).getChatterUUID(), UIThreadExecutor.getInstance(), new _$Lambda$6hJe_KPqqQcY7xiCxogddm78oYc(this));
        }
    }
}
