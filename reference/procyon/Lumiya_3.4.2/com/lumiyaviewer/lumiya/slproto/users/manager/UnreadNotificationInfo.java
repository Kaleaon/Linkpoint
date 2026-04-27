// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.google.common.base.Strings;
import com.google.common.base.Optional;
import java.util.Collection;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import javax.annotation.Nullable;
import java.util.List;
import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class UnreadNotificationInfo
{
    public static UnreadNotificationInfo create(@Nonnull final UUID uuid, final int n, @Nullable final List<UnreadMessageSource> list, @Nullable final NotificationType notificationType, final int n2, @Nullable final NotificationType notificationType2, @Nullable final UnreadMessageSource unreadMessageSource, @Nonnull final ObjectPopupNotification objectPopupNotification) {
        ImmutableList<Object> list2;
        if (list != null) {
            list2 = (ImmutableList<Object>)ImmutableList.copyOf((Collection<? extends UnreadMessageSource>)list);
        }
        else {
            list2 = (ImmutableList<Object>)ImmutableList.of();
        }
        return new AutoValue_UnreadNotificationInfo(uuid, n, (ImmutableList<UnreadMessageSource>)list2, Optional.fromNullable(notificationType), n2, Optional.fromNullable(notificationType2), Optional.fromNullable(unreadMessageSource), objectPopupNotification);
    }
    
    public abstract UUID agentUUID();
    
    public abstract int freshMessagesCount();
    
    @Nonnull
    public abstract Optional<NotificationType> mostImportantFreshType();
    
    @Nonnull
    public abstract Optional<NotificationType> mostImportantType();
    
    @Nonnull
    public abstract ObjectPopupNotification objectPopupInfo();
    
    @Nonnull
    public abstract Optional<UnreadMessageSource> singleFreshSource();
    
    public abstract int totalUnreadCount();
    
    @Nonnull
    public abstract ImmutableList<UnreadMessageSource> unreadSources();
    
    public abstract static class ObjectPopupMessage
    {
        public static ObjectPopupMessage create(@Nullable final String s, @Nullable final String s2) {
            return (ObjectPopupMessage)new AutoValue_UnreadNotificationInfo_ObjectPopupMessage(Strings.nullToEmpty(s), Strings.nullToEmpty(s2));
        }
        
        public abstract String message();
        
        public abstract String objectName();
    }
    
    public abstract static class ObjectPopupNotification
    {
        private static ObjectPopupNotification empty;
        
        static {
            ObjectPopupNotification.empty = (ObjectPopupNotification)new AutoValue_UnreadNotificationInfo_ObjectPopupNotification(0, 0, Optional.absent());
        }
        
        public static ObjectPopupNotification create(final int n, final int n2, @Nullable final ObjectPopupMessage objectPopupMessage) {
            if (n == 0 && n2 == 0 && objectPopupMessage == null) {
                return ObjectPopupNotification.empty;
            }
            return (ObjectPopupNotification)new AutoValue_UnreadNotificationInfo_ObjectPopupNotification(n, n2, Optional.fromNullable(objectPopupMessage));
        }
        
        public abstract int freshObjectPopupsCount();
        
        public boolean isEmpty() {
            return this.equals(ObjectPopupNotification.empty);
        }
        
        @Nonnull
        public abstract Optional<ObjectPopupMessage> lastObjectPopup();
        
        public abstract int objectPopupsCount();
    }
    
    public abstract static class UnreadMessageSource
    {
        public static UnreadMessageSource create(@Nonnull final ChatterID chatterID, @Nullable final String s, @Nullable final List<SLChatEvent> list, final int n) {
            final Optional<String> fromNullable = Optional.fromNullable(s);
            ImmutableList<Object> list2;
            if (list != null) {
                list2 = (ImmutableList<Object>)ImmutableList.copyOf((Collection<? extends SLChatEvent>)list);
            }
            else {
                list2 = (ImmutableList<Object>)ImmutableList.of();
            }
            return (UnreadMessageSource)new AutoValue_UnreadNotificationInfo_UnreadMessageSource(chatterID, fromNullable, (ImmutableList<SLChatEvent>)list2, n);
        }
        
        @Nonnull
        public abstract ChatterID chatterID();
        
        public abstract Optional<String> chatterName();
        
        @Nonnull
        public abstract ImmutableList<SLChatEvent> unreadMessages();
        
        public abstract int unreadMessagesCount();
        
        public UnreadMessageSource withMessages(@Nullable final List<SLChatEvent> list) {
            final ChatterID chatterID = this.chatterID();
            final Optional<String> chatterName = this.chatterName();
            ImmutableList<Object> list2;
            if (list != null) {
                list2 = (ImmutableList<Object>)ImmutableList.copyOf((Collection<? extends SLChatEvent>)list);
            }
            else {
                list2 = (ImmutableList<Object>)ImmutableList.of();
            }
            return (UnreadMessageSource)new AutoValue_UnreadNotificationInfo_UnreadMessageSource(chatterID, chatterName, (ImmutableList<SLChatEvent>)list2, this.unreadMessagesCount());
        }
    }
}
