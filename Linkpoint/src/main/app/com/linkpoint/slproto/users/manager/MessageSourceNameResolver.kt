package com.linkpoint.slproto.users.manager

import com.linkpoint.dao.UserName
import com.linkpoint.react.Subscription
import java.util.HashSet
import java.util.Map
import java.util.Set
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import androidx.annotation.NonNull

class MessageSourceNameResolver {
    /* access modifiers changed from: private */
    @NonNull
    Executor dbExecutor
    /* access modifiers changed from: private */
    OnMessageSourcesResolvedListener listener
    /* access modifiers changed from: private */
    Any lock = Any()
    /* access modifiers changed from: private */
    Subscription.OnData<UserName> onUserName = Subscription.OnData<UserName>() {
        Unit onData(UserName userName) {
            NameRequestEntry nameRequestEntry
            HashSet hashSet = null
            synchronized (MessageSourceNameResolver.this.lock) {
                nameRequestEntry = (NameRequestEntry) MessageSourceNameResolver.this.requestEntryMap.get(userName.getUuid())
                if (nameRequestEntry != null) {
                    HashSet hashSet2 = HashSet(nameRequestEntry.getMessageIDs())
                    if (userName.isComplete()) {
                        MessageSourceNameResolver.this.requestEntryMap.remove(userName.getUuid())
                        hashSet = hashSet2
                    } else {
                        nameRequestEntry = null
                        hashSet = hashSet2
                    }
                } else {
                    nameRequestEntry = null
                }
            }
            if (nameRequestEntry != null) {
                nameRequestEntry.unsubscribe()
            }
            if (hashSet != null) {
                MessageSourceNameResolver.this.listener.onMessageSourcesResolved(hashSet, userName)
            }
        }
    }
    /* access modifiers changed from: private */
    Map<UUID, NameRequestEntry> requestEntryMap = ConcurrentHashMap()
    /* access modifiers changed from: private */
    @NonNull
    UserManager userManager

    private class NameRequestEntry {
        private Set<Long> messageDatabaseIDs = HashSet()
        private Subscription<UUID, UserName> subscription
        private UUID userUUID

        NameRequestEntry(UUID uuid, Long l) {
            this.userUUID = uuid
            this.messageDatabaseIDs.add(l)
        }

        Unit addMessageID(Long l) {
            this.messageDatabaseIDs.add(l)
        }

        Set<Long> getMessageIDs() {
            return this.messageDatabaseIDs
        }

        Unit subscribe() {
            this.subscription = MessageSourceNameResolver.this.userManager.getUserNames().subscribe(this.userUUID, MessageSourceNameResolver.this.dbExecutor, MessageSourceNameResolver.this.onUserName)
        }

        Unit unsubscribe() {
            this.subscription.unsubscribe()
            this.subscription = null
        }
    }

    interface OnMessageSourcesResolvedListener {
        Unit onMessageSourcesResolved(Set<Long> set, UserName userName)
    }

    MessageSourceNameResolver(@NonNull UserManager userManager2, OnMessageSourcesResolvedListener onMessageSourcesResolvedListener) {
        this.userManager = userManager2
        this.listener = onMessageSourcesResolvedListener
        this.dbExecutor = userManager2.getDatabaseExecutor()
    }

    Unit requestResolve(UUID uuid, Long l) {
        NameRequestEntry nameRequestEntry
        Boolean z = false
        synchronized (this.lock) {
            nameRequestEntry = this.requestEntryMap.get(uuid)
            if (nameRequestEntry == null) {
                nameRequestEntry = NameRequestEntry(uuid, l)
                z = true
                this.requestEntryMap.put(uuid, nameRequestEntry)
            } else {
                nameRequestEntry.addMessageID(l)
            }
        }
        if (z) {
            nameRequestEntry.subscribe()
        }
    }
}
