package com.lumiyaviewer.lumiya.ui.chat.profiles

import com.lumiyaviewer.lumiya.react.Subscription
import com.lumiyaviewer.lumiya.react.UIThreadExecutor
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.ui.common.TextFieldEditFragment
import java.util.UUID
import androidx.annotation.Nullable

abstract class ProfileTextFieldEditFragment : TextFieldEditFragment {
    private Subscription<UUID, AvatarPropertiesReply> avatarProperties = null

    /* access modifiers changed from: protected */
    /* renamed from: onAvatarProperties */
    abstract Unit m507com_lumiyaviewer_lumiya_ui_chat_profiles_ProfileTextFieldEditFragmentmthref0(AvatarPropertiesReply avatarPropertiesReply)

    /* access modifiers changed from: protected */
    Unit onShowUser(@Nullable ChatterID chatterID) {
        if (this.avatarProperties != null) {
            this.avatarProperties.unsubscribe()
            this.avatarProperties = null
        }
        if (this.userManager != null && (chatterID instanceof ChatterID.ChatterIDUser)) {
            this.avatarProperties = this.userManager.getAvatarProperties().getPool().subscribe(((ChatterID.ChatterIDUser) chatterID).getChatterUUID(), UIThreadExecutor.getInstance(), $Lambda$6hJeKPqqQcY7xiCxogddm78oYc(this))
        }
    }
}
