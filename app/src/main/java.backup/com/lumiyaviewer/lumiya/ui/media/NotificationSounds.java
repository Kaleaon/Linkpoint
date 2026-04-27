package com.lumiyaviewer.lumiya.ui.media;

import android.net.Uri;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;

public enum NotificationSounds {
    LocalChat(R.raw.lumiya_local_chat_message),
    IM(R.raw.lumiya_private_message),
    Group(R.raw.lumiya_group_message);
    
    public static final ImmutableMap<NotificationType, NotificationSounds> defaultSounds = null;
    private final int resourceId;

    static {
        defaultSounds = ImmutableMap.of(NotificationType.LocalChat, LocalChat, NotificationType.Private, IM, NotificationType.Group, Group);
    }

    private NotificationSounds(int i) {
        this.resourceId = i;
    }

    public static Uri getResourceUri(int i) {
        return Uri.parse("android.resource://com.lumiyaviewer.lumiya/" + i);
    }

    public int getResourceId() {
        return this.resourceId;
    }

    public Uri getUri() {
        return getResourceUri(this.resourceId);
    }
}
