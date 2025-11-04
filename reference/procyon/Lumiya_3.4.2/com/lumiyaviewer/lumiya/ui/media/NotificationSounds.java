// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.media;

import android.net.Uri;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import com.google.common.collect.ImmutableMap;

public enum NotificationSounds
{
    Group("Group", 2, 2131230720), 
    IM("IM", 1, 2131230722), 
    LocalChat("LocalChat", 0, 2131230721);
    
    public static final ImmutableMap<NotificationType, NotificationSounds> defaultSounds;
    private final int resourceId;
    
    static {
        defaultSounds = ImmutableMap.of(NotificationType.LocalChat, NotificationSounds.LocalChat, NotificationType.Private, NotificationSounds.IM, NotificationType.Group, NotificationSounds.Group);
    }
    
    private NotificationSounds(final String name, final int ordinal, final int resourceId) {
        this.resourceId = resourceId;
    }
    
    public static Uri getResourceUri(final int i) {
        return Uri.parse("android.resource://com.lumiyaviewer.lumiya/" + i);
    }
    
    public int getResourceId() {
        return this.resourceId;
    }
    
    public Uri getUri() {
        return getResourceUri(this.resourceId);
    }
}
