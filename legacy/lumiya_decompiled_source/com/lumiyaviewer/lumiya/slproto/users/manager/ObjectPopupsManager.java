package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class ObjectPopupsManager {
    private static final int MAX_POPUPS = 99;
    @Nonnull
    private final UserManager userManager;
    private final SubscribableList<SLChatEvent> objectPopups = new SubscribableList<>();
    private final Object listenerLock = new Object();
    @Nullable
    private WeakReference<ObjectPopupListener> objectPopupListener = null;
    @Nullable
    private Executor objectPopupListenerExecutor = null;
    @Nullable
    private SLChatEvent displayedPopupEvent = null;
    @Nullable
    private SLChatEvent lastEvent = null;
    private boolean popupAnimated = false;
    private final AtomicInteger freshPopupsCount = new AtomicInteger(0);
    private final AtomicInteger unreadPopupCount = new AtomicInteger(0);
    private final Set<Object> popupWatchers = Collections.newSetFromMap(new WeakHashMap());

    /* loaded from: classes.dex */
    public interface ObjectPopupListener {
        void onNewObjectPopup(SLChatEvent sLChatEvent);

        void onObjectPopupCountChanged(int i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ObjectPopupsManager(@Nonnull UserManager userManager) {
        this.userManager = userManager;
    }

    private void addObjectPopupInternal(SLChatEvent sLChatEvent) {
        this.objectPopups.add(0, sLChatEvent);
        while (this.objectPopups.size() > 99) {
            try {
                this.objectPopups.remove(this.objectPopups.size() - 1);
            } catch (Exception e) {
                Debug.Warning(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_ObjectPopupsManager_8558  reason: not valid java name */
    public static /* synthetic */ void m348xe63a07b1(ObjectPopupListener objectPopupListener) {
        objectPopupListener.onObjectPopupCountChanged(0);
        objectPopupListener.onNewObjectPopup(null);
    }

    private void notifyCountUpdated() {
        Executor executor;
        final ObjectPopupListener objectPopupListener = null;
        synchronized (this.listenerLock) {
            if (this.objectPopupListener != null) {
                objectPopupListener = this.objectPopupListener.get();
                executor = this.objectPopupListenerExecutor;
            } else {
                executor = null;
            }
        }
        if (objectPopupListener != null) {
            final int size = this.objectPopups.size();
            if (executor != null) {
                executor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$gJtxV6TiuzFNXMR7-6og75a4tFE.4
                    private final /* synthetic */ void $m$0() {
                        ((ObjectPopupsManager.ObjectPopupListener) objectPopupListener).onObjectPopupCountChanged(size);
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        $m$0();
                    }
                });
            } else {
                objectPopupListener.onObjectPopupCountChanged(size);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addObjectPopup(final SLChatEvent sLChatEvent) {
        Executor executor;
        boolean z = false;
        final ObjectPopupListener objectPopupListener = null;
        synchronized (this.listenerLock) {
            if (this.objectPopupListener != null) {
                objectPopupListener = this.objectPopupListener.get();
                executor = this.objectPopupListenerExecutor;
                if (objectPopupListener != null && this.displayedPopupEvent == null) {
                    this.displayedPopupEvent = sLChatEvent;
                    this.popupAnimated = false;
                    z = true;
                }
            } else {
                executor = null;
            }
            if (!(!this.popupWatchers.isEmpty())) {
                this.freshPopupsCount.incrementAndGet();
                this.unreadPopupCount.incrementAndGet();
                this.lastEvent = sLChatEvent;
            }
        }
        if (!z) {
            addObjectPopupInternal(sLChatEvent);
            final int size = this.objectPopups.size();
            if (objectPopupListener != null) {
                if (executor != null) {
                    executor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$gJtxV6TiuzFNXMR7-6og75a4tFE.3
                        private final /* synthetic */ void $m$0() {
                            ((ObjectPopupsManager.ObjectPopupListener) objectPopupListener).onObjectPopupCountChanged(size);
                        }

                        @Override // java.lang.Runnable
                        public final void run() {
                            $m$0();
                        }
                    });
                } else {
                    objectPopupListener.onObjectPopupCountChanged(size);
                }
            }
        } else if (executor != null) {
            executor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$gJtxV6TiuzFNXMR7-6og75a4tFE.2
                private final /* synthetic */ void $m$0() {
                    ((ObjectPopupsManager.ObjectPopupListener) objectPopupListener).onNewObjectPopup((SLChatEvent) sLChatEvent);
                }

                @Override // java.lang.Runnable
                public final void run() {
                    $m$0();
                }
            });
        } else {
            objectPopupListener.onNewObjectPopup(sLChatEvent);
        }
        this.userManager.getUnreadNotificationManager().updateUnreadNotifications();
    }

    public void addPopupWatcher(Object obj) {
        synchronized (this.listenerLock) {
            this.popupWatchers.add(obj);
            this.freshPopupsCount.set(0);
            this.unreadPopupCount.set(0);
        }
        this.userManager.getUnreadNotificationManager().updateUnreadNotifications();
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x001f A[Catch: all -> 0x0049, TRY_LEAVE, TryCatch #0 {, blocks: (B:5:0x0006, B:7:0x000a, B:9:0x0011, B:10:0x0019, B:11:0x001b, B:13:0x001f), top: B:29:0x0006 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void cancelObjectPopup(com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent r6) {
        /*
            r5 = this;
            r1 = 0
            java.lang.Object r3 = r5.listenerLock
            monitor-enter(r3)
            if (r6 == 0) goto L46
            com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent r0 = r5.displayedPopupEvent     // Catch: java.lang.Throwable -> L49
            if (r6 != r0) goto L46
            r0 = 0
            r5.displayedPopupEvent = r0     // Catch: java.lang.Throwable -> L49
            java.lang.ref.WeakReference<com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager$ObjectPopupListener> r0 = r5.objectPopupListener     // Catch: java.lang.Throwable -> L49
            if (r0 == 0) goto L44
            java.lang.ref.WeakReference<com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager$ObjectPopupListener> r0 = r5.objectPopupListener     // Catch: java.lang.Throwable -> L49
            java.lang.Object r0 = r0.get()     // Catch: java.lang.Throwable -> L49
            com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager$ObjectPopupListener r0 = (com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager.ObjectPopupListener) r0     // Catch: java.lang.Throwable -> L49
        L19:
            java.util.concurrent.Executor r2 = r5.objectPopupListenerExecutor     // Catch: java.lang.Throwable -> L49
        L1b:
            com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent r4 = r5.lastEvent     // Catch: java.lang.Throwable -> L49
            if (r6 != r4) goto L22
            r4 = 0
            r5.lastEvent = r4     // Catch: java.lang.Throwable -> L49
        L22:
            monitor-exit(r3)
            if (r0 == 0) goto L2f
            if (r2 == 0) goto L4c
            com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$gJtxV6TiuzFNXMR7-6og75a4tFE r1 = new com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$gJtxV6TiuzFNXMR7-6og75a4tFE
            r1.<init>()
            r2.execute(r1)
        L2f:
            com.lumiyaviewer.lumiya.slproto.users.manager.SubscribableList<com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent> r0 = r5.objectPopups
            boolean r0 = r0.remove(r6)
            if (r0 == 0) goto L3a
            r5.notifyCountUpdated()
        L3a:
            com.lumiyaviewer.lumiya.slproto.users.manager.UserManager r0 = r5.userManager
            com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationManager r0 = r0.getUnreadNotificationManager()
            r0.updateUnreadNotifications()
            return
        L44:
            r0 = r1
            goto L19
        L46:
            r0 = r1
            r2 = r1
            goto L1b
        L49:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        L4c:
            r0.onNewObjectPopup(r1)
            goto L2f
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager.cancelObjectPopup(com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearObjectPopups() {
        final ObjectPopupListener objectPopupListener;
        Executor executor;
        synchronized (this.listenerLock) {
            this.displayedPopupEvent = null;
            objectPopupListener = this.objectPopupListener != null ? this.objectPopupListener.get() : null;
            executor = this.objectPopupListenerExecutor;
            this.unreadPopupCount.set(0);
            this.freshPopupsCount.set(0);
            this.lastEvent = null;
        }
        this.objectPopups.clear();
        if (objectPopupListener != null) {
            if (executor != null) {
                executor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$gJtxV6TiuzFNXMR7-6og75a4tFE.1
                    private final /* synthetic */ void $m$0() {
                        ObjectPopupsManager.m348xe63a07b1((ObjectPopupsManager.ObjectPopupListener) objectPopupListener);
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        $m$0();
                    }
                });
            } else {
                objectPopupListener.onObjectPopupCountChanged(0);
                objectPopupListener.onNewObjectPopup(null);
            }
        }
        this.userManager.getUnreadNotificationManager().updateUnreadNotifications();
    }

    public void dismissDisplayedObjectPopup(SLChatEvent sLChatEvent) {
        synchronized (this.listenerLock) {
            if (sLChatEvent == this.displayedPopupEvent) {
                this.displayedPopupEvent = null;
            } else if (sLChatEvent == null) {
                sLChatEvent = this.displayedPopupEvent;
                this.displayedPopupEvent = null;
            } else {
                sLChatEvent = null;
            }
            this.freshPopupsCount.set(0);
            this.unreadPopupCount.set(0);
        }
        if (sLChatEvent != null) {
            addObjectPopupInternal(sLChatEvent);
            notifyCountUpdated();
        }
        this.userManager.getUnreadNotificationManager().updateUnreadNotifications();
    }

    public SLChatEvent getDisplayedObjectPopup() {
        SLChatEvent sLChatEvent;
        synchronized (this.listenerLock) {
            sLChatEvent = this.displayedPopupEvent;
        }
        return sLChatEvent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nonnull
    public UnreadNotificationInfo.ObjectPopupNotification getNotification(boolean z) {
        UnreadNotificationInfo.ObjectPopupNotification create;
        synchronized (this.listenerLock) {
            create = UnreadNotificationInfo.ObjectPopupNotification.create(z ? this.freshPopupsCount.getAndSet(0) : 0, this.unreadPopupCount.get(), this.lastEvent instanceof SLChatTextEvent ? UnreadNotificationInfo.ObjectPopupMessage.create(this.lastEvent.getSource().getSourceName(this.userManager), ((SLChatTextEvent) this.lastEvent).getRawText()) : null);
        }
        return create;
    }

    public int getObjectPopupCount() {
        return this.objectPopups.size();
    }

    public SubscribableList<SLChatEvent> getObjectPopups() {
        return this.objectPopups;
    }

    public boolean mustAnimatePopup(SLChatEvent sLChatEvent) {
        boolean z = false;
        synchronized (this.listenerLock) {
            if (sLChatEvent == this.displayedPopupEvent) {
                z = !this.popupAnimated;
                this.popupAnimated = true;
            }
        }
        return z;
    }

    public void removeObjectPopupListener(ObjectPopupListener objectPopupListener) {
        synchronized (this.listenerLock) {
            if (this.objectPopupListener != null && this.objectPopupListener.get() == objectPopupListener) {
                this.objectPopupListener = null;
                this.objectPopupListenerExecutor = null;
            }
        }
    }

    public void removePopupWatcher(Object obj) {
        synchronized (this.listenerLock) {
            this.popupWatchers.remove(obj);
        }
    }

    public void setObjectPopupListener(ObjectPopupListener objectPopupListener, @Nullable Executor executor) {
        synchronized (this.listenerLock) {
            this.objectPopupListener = new WeakReference<>(objectPopupListener);
            this.objectPopupListenerExecutor = executor;
        }
    }
}
