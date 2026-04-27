package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.dao.UserName;
import com.lumiyaviewer.lumiya.react.Subscription;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;

public class MessageSourceNameResolver {
    /* access modifiers changed from: private */
    @Nonnull
    public final Executor dbExecutor;
    /* access modifiers changed from: private */
    public final OnMessageSourcesResolvedListener listener;
    /* access modifiers changed from: private */
    public final Object lock = new Object();
    /* access modifiers changed from: private */
    public final Subscription.OnData<UserName> onUserName = new Subscription.OnData<UserName>() {
        public void onData(UserName userName) {
            NameRequestEntry nameRequestEntry;
            HashSet hashSet = null;
            synchronized (MessageSourceNameResolver.this.lock) {
                nameRequestEntry = (NameRequestEntry) MessageSourceNameResolver.this.requestEntryMap.get(userName.getUuid());
                if (nameRequestEntry != null) {
                    HashSet hashSet2 = new HashSet(nameRequestEntry.getMessageIDs());
                    if (userName.isComplete()) {
                        MessageSourceNameResolver.this.requestEntryMap.remove(userName.getUuid());
                        hashSet = hashSet2;
                    } else {
                        nameRequestEntry = null;
                        hashSet = hashSet2;
                    }
                } else {
                    nameRequestEntry = null;
                }
            }
            if (nameRequestEntry != null) {
                nameRequestEntry.unsubscribe();
            }
            if (hashSet != null) {
                MessageSourceNameResolver.this.listener.onMessageSourcesResolved(hashSet, userName);
            }
        }
    };
    /* access modifiers changed from: private */
    public final Map<UUID, NameRequestEntry> requestEntryMap = new ConcurrentHashMap();
    /* access modifiers changed from: private */
    @Nonnull
    public final UserManager userManager;

    private class NameRequestEntry {
        private final Set<Long> messageDatabaseIDs = new HashSet();
        private Subscription<UUID, UserName> subscription;
        private final UUID userUUID;

        public NameRequestEntry(UUID uuid, Long l) {
            this.userUUID = uuid;
            this.messageDatabaseIDs.add(l);
        }

        public void addMessageID(Long l) {
            this.messageDatabaseIDs.add(l);
        }

        public Set<Long> getMessageIDs() {
            return this.messageDatabaseIDs;
        }

        public void subscribe() {
            this.subscription = MessageSourceNameResolver.this.userManager.getUserNames().subscribe(this.userUUID, MessageSourceNameResolver.this.dbExecutor, MessageSourceNameResolver.this.onUserName);
        }

        public void unsubscribe() {
            this.subscription.unsubscribe();
            this.subscription = null;
        }
    }

    public interface OnMessageSourcesResolvedListener {
        void onMessageSourcesResolved(Set<Long> set, UserName userName);
    }

    public MessageSourceNameResolver(@Nonnull UserManager userManager2, OnMessageSourcesResolvedListener onMessageSourcesResolvedListener) {
        this.userManager = userManager2;
        this.listener = onMessageSourcesResolvedListener;
        this.dbExecutor = userManager2.getDatabaseExecutor();
    }

    public void requestResolve(UUID uuid, Long l) {
        NameRequestEntry nameRequestEntry;
        boolean z = false;
        synchronized (this.lock) {
            nameRequestEntry = this.requestEntryMap.get(uuid);
            if (nameRequestEntry == null) {
                nameRequestEntry = new NameRequestEntry(uuid, l);
                z = true;
                this.requestEntryMap.put(uuid, nameRequestEntry);
            } else {
                nameRequestEntry.addMessageID(l);
            }
        }
        if (z) {
            nameRequestEntry.subscribe();
        }
    }
}
