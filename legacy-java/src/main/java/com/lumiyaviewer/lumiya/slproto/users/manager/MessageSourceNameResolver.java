package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.dao.UserName;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public class MessageSourceNameResolver {

    @Nonnull
    private final Executor dbExecutor;
    private final OnMessageSourcesResolvedListener listener;

    @Nonnull
    private final UserManager userManager;
    private final Object lock = new Object();
    private final Map<UUID, NameRequestEntry> requestEntryMap = new ConcurrentHashMap();
    private final Subscription.OnData<UserName> onUserName = new Subscription.OnData<UserName>() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.MessageSourceNameResolver.1
        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
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

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class NameRequestEntry {
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
            this.subscription = MessageSourceNameResolver.this.userManager.getUserNames().subscribe((Subscribable<UUID, UserName>) this.userUUID, MessageSourceNameResolver.this.dbExecutor, MessageSourceNameResolver.this.onUserName);
        }

        public void unsubscribe() {
            this.subscription.unsubscribe();
            this.subscription = null;
        }
    }

    /* loaded from: classes.dex */
    public interface OnMessageSourcesResolvedListener {
        void onMessageSourcesResolved(Set<Long> set, UserName userName);
    }

    public MessageSourceNameResolver(@Nonnull UserManager userManager, OnMessageSourcesResolvedListener onMessageSourcesResolvedListener) {
        this.userManager = userManager;
        this.listener = onMessageSourcesResolvedListener;
        this.dbExecutor = userManager.getDatabaseExecutor();
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
