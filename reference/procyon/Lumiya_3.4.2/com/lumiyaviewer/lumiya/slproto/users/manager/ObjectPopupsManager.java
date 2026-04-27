// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent;
import com.lumiyaviewer.lumiya.Debug;
import java.util.Map;
import java.util.Collections;
import java.util.WeakHashMap;
import javax.annotation.Nonnull;
import java.util.Set;
import java.util.concurrent.Executor;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;

public class ObjectPopupsManager
{
    private static final int MAX_POPUPS = 99;
    @Nullable
    private SLChatEvent displayedPopupEvent;
    private final AtomicInteger freshPopupsCount;
    @Nullable
    private SLChatEvent lastEvent;
    private final Object listenerLock;
    @Nullable
    private WeakReference<ObjectPopupListener> objectPopupListener;
    @Nullable
    private Executor objectPopupListenerExecutor;
    private final SubscribableList<SLChatEvent> objectPopups;
    private boolean popupAnimated;
    private final Set<Object> popupWatchers;
    private final AtomicInteger unreadPopupCount;
    @Nonnull
    private final UserManager userManager;
    
    ObjectPopupsManager(@Nonnull final UserManager userManager) {
        this.objectPopups = new SubscribableList<SLChatEvent>();
        this.listenerLock = new Object();
        this.objectPopupListener = null;
        this.objectPopupListenerExecutor = null;
        this.displayedPopupEvent = null;
        this.lastEvent = null;
        this.popupAnimated = false;
        this.freshPopupsCount = new AtomicInteger(0);
        this.unreadPopupCount = new AtomicInteger(0);
        this.popupWatchers = Collections.newSetFromMap(new WeakHashMap<Object, Boolean>());
        this.userManager = userManager;
    }
    
    private void addObjectPopupInternal(final SLChatEvent slChatEvent) {
        this.objectPopups.add(0, slChatEvent);
        while (this.objectPopups.size() > 99) {
            try {
                this.objectPopups.remove(this.objectPopups.size() - 1);
            }
            catch (final Exception ex) {
                Debug.Warning(ex);
            }
        }
    }
    
    private void notifyCountUpdated() {
        while (true) {
            ObjectPopupListener objectPopupListener = null;
            int size = 0;
            Label_0078: {
                synchronized (this.listenerLock) {
                    Executor objectPopupListenerExecutor;
                    if (this.objectPopupListener != null) {
                        objectPopupListener = this.objectPopupListener.get();
                        objectPopupListenerExecutor = this.objectPopupListenerExecutor;
                    }
                    else {
                        objectPopupListenerExecutor = null;
                    }
                    monitorexit(this.listenerLock);
                    if (objectPopupListener != null) {
                        size = this.objectPopups.size();
                        if (objectPopupListenerExecutor == null) {
                            break Label_0078;
                        }
                        objectPopupListenerExecutor.execute(new _$Lambda$gJtxV6TiuzFNXMR7_6og75a4tFE$4(size, objectPopupListener));
                    }
                    return;
                }
            }
            objectPopupListener.onObjectPopupCountChanged(size);
        }
    }
    
    void addObjectPopup(final SLChatEvent slChatEvent) {
        while (true) {
            final int n = 0;
            ObjectPopupListener objectPopupListener = null;
            while (true) {
                Executor executor = null;
                Label_0210: {
                    int size = 0;
                    Label_0199: {
                        synchronized (this.listenerLock) {
                            if (this.objectPopupListener != null) {
                                final ObjectPopupListener objectPopupListener2 = this.objectPopupListener.get();
                                final Executor objectPopupListenerExecutor = this.objectPopupListenerExecutor;
                                objectPopupListener = objectPopupListener2;
                                size = n;
                                executor = objectPopupListenerExecutor;
                                if (objectPopupListener2 != null) {
                                    objectPopupListener = objectPopupListener2;
                                    size = n;
                                    executor = objectPopupListenerExecutor;
                                    if (this.displayedPopupEvent == null) {
                                        this.displayedPopupEvent = slChatEvent;
                                        this.popupAnimated = false;
                                        size = 1;
                                        executor = objectPopupListenerExecutor;
                                        objectPopupListener = objectPopupListener2;
                                    }
                                }
                            }
                            else {
                                executor = null;
                                size = n;
                            }
                            if (!(this.popupWatchers.isEmpty() ^ true)) {
                                this.freshPopupsCount.incrementAndGet();
                                this.unreadPopupCount.incrementAndGet();
                                this.lastEvent = slChatEvent;
                            }
                            monitorexit(this.listenerLock);
                            if (size == 0) {
                                this.addObjectPopupInternal(slChatEvent);
                                size = this.objectPopups.size();
                                if (objectPopupListener != null) {
                                    if (executor == null) {
                                        break Label_0199;
                                    }
                                    executor.execute(new _$Lambda$gJtxV6TiuzFNXMR7_6og75a4tFE$3(size, objectPopupListener));
                                }
                                this.userManager.getUnreadNotificationManager().updateUnreadNotifications();
                                return;
                            }
                            break Label_0210;
                        }
                    }
                    objectPopupListener.onObjectPopupCountChanged(size);
                    continue;
                }
                final SLChatEvent slChatEvent2;
                if (executor != null) {
                    executor.execute(new _$Lambda$gJtxV6TiuzFNXMR7_6og75a4tFE$2(objectPopupListener, slChatEvent2));
                    continue;
                }
                objectPopupListener.onNewObjectPopup(slChatEvent2);
                continue;
            }
        }
    }
    
    public void addPopupWatcher(final Object o) {
        synchronized (this.listenerLock) {
            this.popupWatchers.add(o);
            this.freshPopupsCount.set(0);
            this.unreadPopupCount.set(0);
            monitorexit(this.listenerLock);
            this.userManager.getUnreadNotificationManager().updateUnreadNotifications();
        }
    }
    
    public void cancelObjectPopup(final SLChatEvent o) {
        final Object listenerLock = this.listenerLock;
        monitorenter(listenerLock);
        Label_0118: {
            if (o == null) {
                break Label_0118;
            }
            while (true) {
                while (true) {
                    ObjectPopupListener objectPopupListener = null;
                    Label_0131: {
                        try {
                            Executor objectPopupListenerExecutor;
                            if (o == this.displayedPopupEvent) {
                                this.displayedPopupEvent = null;
                                if (this.objectPopupListener != null) {
                                    objectPopupListener = this.objectPopupListener.get();
                                }
                                else {
                                    objectPopupListener = null;
                                }
                                objectPopupListenerExecutor = this.objectPopupListenerExecutor;
                            }
                            else {
                                objectPopupListener = null;
                                objectPopupListenerExecutor = null;
                            }
                            if (o == this.lastEvent) {
                                this.lastEvent = null;
                            }
                            monitorexit(listenerLock);
                            if (objectPopupListener != null) {
                                if (objectPopupListenerExecutor == null) {
                                    break Label_0131;
                                }
                                objectPopupListenerExecutor.execute(new _$Lambda$gJtxV6TiuzFNXMR7_6og75a4tFE(objectPopupListener));
                            }
                            if (this.objectPopups.remove(o)) {
                                this.notifyCountUpdated();
                            }
                            this.userManager.getUnreadNotificationManager().updateUnreadNotifications();
                            return;
                        }
                        finally {
                            monitorexit(listenerLock);
                        }
                    }
                    objectPopupListener.onNewObjectPopup(null);
                    continue;
                }
            }
        }
    }
    
    void clearObjectPopups() {
        while (true) {
            while (true) {
                Label_0108: {
                    synchronized (this.listenerLock) {
                        this.displayedPopupEvent = null;
                        ObjectPopupListener objectPopupListener;
                        if (this.objectPopupListener != null) {
                            objectPopupListener = this.objectPopupListener.get();
                        }
                        else {
                            objectPopupListener = null;
                        }
                        final Executor objectPopupListenerExecutor = this.objectPopupListenerExecutor;
                        this.unreadPopupCount.set(0);
                        this.freshPopupsCount.set(0);
                        this.lastEvent = null;
                        monitorexit(this.listenerLock);
                        this.objectPopups.clear();
                        if (objectPopupListener != null) {
                            if (objectPopupListenerExecutor == null) {
                                break Label_0108;
                            }
                            objectPopupListenerExecutor.execute(new _$Lambda$gJtxV6TiuzFNXMR7_6og75a4tFE$1(objectPopupListener));
                        }
                        this.userManager.getUnreadNotificationManager().updateUnreadNotifications();
                        return;
                    }
                }
                final ObjectPopupListener objectPopupListener2;
                objectPopupListener2.onObjectPopupCountChanged(0);
                objectPopupListener2.onNewObjectPopup(null);
                continue;
            }
        }
    }
    
    public void dismissDisplayedObjectPopup(SLChatEvent displayedPopupEvent) {
        while (true) {
            while (true) {
                Label_0084: {
                    synchronized (this.listenerLock) {
                        if (displayedPopupEvent == this.displayedPopupEvent) {
                            this.displayedPopupEvent = null;
                        }
                        else {
                            if (displayedPopupEvent != null) {
                                break Label_0084;
                            }
                            displayedPopupEvent = this.displayedPopupEvent;
                            this.displayedPopupEvent = null;
                        }
                        this.freshPopupsCount.set(0);
                        this.unreadPopupCount.set(0);
                        monitorexit(this.listenerLock);
                        if (displayedPopupEvent != null) {
                            this.addObjectPopupInternal(displayedPopupEvent);
                            this.notifyCountUpdated();
                        }
                        this.userManager.getUnreadNotificationManager().updateUnreadNotifications();
                        return;
                    }
                }
                displayedPopupEvent = null;
                continue;
            }
        }
    }
    
    public SLChatEvent getDisplayedObjectPopup() {
        synchronized (this.listenerLock) {
            return this.displayedPopupEvent;
        }
    }
    
    @Nonnull
    UnreadNotificationInfo.ObjectPopupNotification getNotification(final boolean b) {
        synchronized (this.listenerLock) {
            final int value = this.unreadPopupCount.get();
            int andSet;
            if (b) {
                andSet = this.freshPopupsCount.getAndSet(0);
            }
            else {
                andSet = 0;
            }
            Object create = null;
            if (this.lastEvent instanceof SLChatTextEvent) {
                create = UnreadNotificationInfo.ObjectPopupMessage.create(this.lastEvent.getSource().getSourceName(this.userManager), ((SLChatTextEvent)this.lastEvent).getRawText());
            }
            return UnreadNotificationInfo.ObjectPopupNotification.create(andSet, value, (UnreadNotificationInfo.ObjectPopupMessage)create);
        }
    }
    
    public int getObjectPopupCount() {
        return this.objectPopups.size();
    }
    
    public SubscribableList<SLChatEvent> getObjectPopups() {
        return this.objectPopups;
    }
    
    public boolean mustAnimatePopup(final SLChatEvent slChatEvent) {
        boolean b = false;
        synchronized (this.listenerLock) {
            if (slChatEvent == this.displayedPopupEvent) {
                b = (this.popupAnimated ^ true);
                this.popupAnimated = true;
            }
            return b;
        }
    }
    
    public void removeObjectPopupListener(final ObjectPopupListener objectPopupListener) {
        synchronized (this.listenerLock) {
            if (this.objectPopupListener != null && this.objectPopupListener.get() == objectPopupListener) {
                this.objectPopupListener = null;
                this.objectPopupListenerExecutor = null;
            }
        }
    }
    
    public void removePopupWatcher(final Object o) {
        synchronized (this.listenerLock) {
            this.popupWatchers.remove(o);
        }
    }
    
    public void setObjectPopupListener(final ObjectPopupListener referent, @Nullable final Executor objectPopupListenerExecutor) {
        synchronized (this.listenerLock) {
            this.objectPopupListener = new WeakReference<ObjectPopupListener>(referent);
            this.objectPopupListenerExecutor = objectPopupListenerExecutor;
        }
    }
    
    public interface ObjectPopupListener
    {
        void onNewObjectPopup(final SLChatEvent p0);
        
        void onObjectPopupCountChanged(final int p0);
    }
}
