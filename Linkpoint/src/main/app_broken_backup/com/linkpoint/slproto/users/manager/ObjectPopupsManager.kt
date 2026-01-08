package com.linkpoint.slproto.users.manager

import com.linkpoint.Debug
import com.linkpoint.slproto.chat.SLChatTextEvent
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.manager.UnreadNotificationInfo
import java.lang.ref.WeakReference
import java.util.Collections
import java.util.WeakHashMap
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class ObjectPopupsManager(@NonNull private val userManager: UserManager) {
    private val MAX_POPUPS = 99
    
    @Nullable
    private var displayedPopupEvent: SLChatEvent? = null
    
    private val freshPopupsCount = AtomicInteger(0)
    
    @Nullable
    private var lastEvent: SLChatEvent? = null
    
    private val listenerLock = Any()
    
    @Nullable
    private var objectPopupListener: WeakReference<ObjectPopupListener>? = null
    
    @Nullable
    private var objectPopupListenerExecutor: Executor? = null
    
    private val objectPopups = SubscribableList<SLChatEvent>()
    private var popupAnimated = false
    
    // Collections.newSetFromMap(WeakHashMap()) creates a concurrent set backed by WeakHashMap
    private val popupWatchers = Collections.newSetFromMap(WeakHashMap<Any, Boolean>())
    
    private val unreadPopupCount = AtomicInteger(0)

    interface ObjectPopupListener {
        fun onNewObjectPopup(event: SLChatEvent?)
        fun onObjectPopupCountChanged(count: Int)
    }

    private fun addObjectPopupInternal(event: SLChatEvent) {
        objectPopups.add(0, event)
        while (objectPopups.size > MAX_POPUPS) {
            try {
                objectPopups.remove(objectPopups.size - 1)
            } catch (e: Exception) {
                Debug.Warning(e)
            }
        }
    }

    private fun notifyCountUpdated() {
        var listener: ObjectPopupListener? = null
        var executor: Executor? = null
        
        synchronized(listenerLock) {
            if (objectPopupListener != null) {
                listener = objectPopupListener?.get()
                executor = objectPopupListenerExecutor
            }
        }
        
        if (listener != null) {
            val count = objectPopups.size
            if (executor != null) {
                executor?.execute {
                    listener?.onObjectPopupCountChanged(count)
                }
            } else {
                listener?.onObjectPopupCountChanged(count)
            }
        }
    }

    fun addObjectPopup(event: SLChatEvent) {
        var showNow = false
        var listener: ObjectPopupListener? = null
        var executor: Executor? = null
        
        synchronized(listenerLock) {
            if (objectPopupListener != null) {
                listener = objectPopupListener?.get()
                executor = objectPopupListenerExecutor
                if (listener != null && displayedPopupEvent == null) {
                    displayedPopupEvent = event
                    popupAnimated = false
                    showNow = true
                }
            }
            
            if (popupWatchers.isNotEmpty()) {
                freshPopupsCount.incrementAndGet()
                unreadPopupCount.incrementAndGet()
                lastEvent = event
            }
        }
        
        if (showNow) {
            // Decompiled logic: if showNow, we don't add to internal list yet?
            // Wait, decompiled logic:
            // if (!z) { addObjectPopupInternal... } else { executor.execute(...) }
            // If z (showNow) is true, it calls onNewObjectPopup directly?
            // No, it calls executor.execute(Runnable(listener, event) { listener.onNewObjectPopup(event) })
            // It does NOT add to internal list if shown immediately?
            // Wait, `dismissDisplayedObjectPopup` adds it to internal list later.
            // So yes, if shown immediately, it's "displayed", not in "history" yet.
            
            if (listener != null) {
                if (executor != null) {
                    executor?.execute {
                        listener?.onNewObjectPopup(event)
                    }
                } else {
                    listener?.onNewObjectPopup(event)
                }
            }
        } else {
            addObjectPopupInternal(event)
            val count = objectPopups.size
            if (listener != null) {
                if (executor != null) {
                    executor?.execute {
                        listener?.onObjectPopupCountChanged(count)
                    }
                } else {
                    listener?.onObjectPopupCountChanged(count)
                }
            }
        }
        userManager.getUnreadNotificationManager().updateUnreadNotifications()
    }

    fun addPopupWatcher(watcher: Any) {
        synchronized(listenerLock) {
            popupWatchers.add(watcher)
            freshPopupsCount.set(0)
            unreadPopupCount.set(0)
        }
        userManager.getUnreadNotificationManager().updateUnreadNotifications()
    }

    fun cancelObjectPopup(event: SLChatEvent?) {
        if (event == null) return
        
        var listener: ObjectPopupListener? = null
        var executor: Executor? = null
        
        synchronized(listenerLock) {
            if (event == displayedPopupEvent) {
                displayedPopupEvent = null
                listener = objectPopupListener?.get()
                executor = objectPopupListenerExecutor
            }
            if (event == lastEvent) {
                lastEvent = null
            }
        }
        
        if (listener != null) {
            if (executor != null) {
                executor?.execute {
                    listener?.onNewObjectPopup(null)
                }
            } else {
                listener?.onNewObjectPopup(null)
            }
        }
        
        if (objectPopups.remove(event)) {
            notifyCountUpdated()
        }
        
        userManager.getUnreadNotificationManager().updateUnreadNotifications()
    }

    fun clearObjectPopups() {
        var listener: ObjectPopupListener? = null
        var executor: Executor? = null
        
        synchronized(listenerLock) {
            displayedPopupEvent = null
            listener = objectPopupListener?.get()
            executor = objectPopupListenerExecutor
            unreadPopupCount.set(0)
            freshPopupsCount.set(0)
            lastEvent = null
        }
        
        objectPopups.clear()
        
        if (listener != null) {
            val finalListener = listener
            val resetAction = {
                finalListener?.onObjectPopupCountChanged(0)
                finalListener?.onNewObjectPopup(null)
            }
            
            if (executor != null) {
                executor?.execute { resetAction() }
            } else {
                resetAction()
            }
        }
        
        userManager.getUnreadNotificationManager().updateUnreadNotifications()
    }

    fun dismissDisplayedObjectPopup(event: SLChatEvent?) {
        var eventToSave: SLChatEvent? = event
        
        synchronized(listenerLock) {
            if (eventToSave == displayedPopupEvent) {
                displayedPopupEvent = null
            } else if (eventToSave == null) {
                eventToSave = displayedPopupEvent
                displayedPopupEvent = null
            } else {
                eventToSave = null
            }
            freshPopupsCount.set(0)
            unreadPopupCount.set(0)
        }
        
        if (eventToSave != null) {
            addObjectPopupInternal(eventToSave!!)
            notifyCountUpdated()
        }
        
        userManager.getUnreadNotificationManager().updateUnreadNotifications()
    }

    fun getDisplayedObjectPopup(): SLChatEvent? {
        synchronized(listenerLock) {
            return displayedPopupEvent
        }
    }

    fun getNotification(clearFresh: Boolean): UnreadNotificationInfo.ObjectPopupNotification {
        synchronized(listenerLock) {
            val unread = unreadPopupCount.get()
            val fresh = if (clearFresh) freshPopupsCount.getAndSet(0) else 0
            
            var message: UnreadNotificationInfo.ObjectPopupMessage? = null
            val evt = lastEvent
            if (evt is SLChatTextEvent) {
                message = UnreadNotificationInfo.ObjectPopupMessage.create(evt.source.getSourceName(userManager), evt.rawText)
            }
            
            return UnreadNotificationInfo.ObjectPopupNotification.create(fresh, unread, message)
        }
    }

    fun getObjectPopupCount(): Int {
        return objectPopups.size
    }

    fun getObjectPopups(): SubscribableList<SLChatEvent> {
        return objectPopups
    }

    fun mustAnimatePopup(event: SLChatEvent): Boolean {
        synchronized(listenerLock) {
            if (event == displayedPopupEvent) {
                val result = !popupAnimated
                popupAnimated = true
                return result
            }
        }
        return false
    }

    fun removeObjectPopupListener(listener: ObjectPopupListener) {
        synchronized(listenerLock) {
            if (objectPopupListener != null && objectPopupListener?.get() === listener) {
                objectPopupListener = null
                objectPopupListenerExecutor = null
            }
        }
    }

    fun removePopupWatcher(watcher: Any) {
        synchronized(listenerLock) {
            popupWatchers.remove(watcher)
        }
    }

    fun setObjectPopupListener(listener: ObjectPopupListener, executor: Executor?) {
        synchronized(listenerLock) {
            objectPopupListener = WeakReference(listener)
            objectPopupListenerExecutor = executor
        }
    }
}
