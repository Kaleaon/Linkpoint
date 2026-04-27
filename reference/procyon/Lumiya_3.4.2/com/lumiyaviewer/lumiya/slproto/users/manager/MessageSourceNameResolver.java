// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;
import java.util.Map;
import com.lumiyaviewer.lumiya.dao.UserName;
import com.lumiyaviewer.lumiya.react.Subscription;
import javax.annotation.Nonnull;
import java.util.concurrent.Executor;

public class MessageSourceNameResolver
{
    @Nonnull
    private final Executor dbExecutor;
    private final OnMessageSourcesResolvedListener listener;
    private final Object lock;
    private final Subscription.OnData<UserName> onUserName;
    private final Map<UUID, NameRequestEntry> requestEntryMap;
    @Nonnull
    private final UserManager userManager;
    
    public MessageSourceNameResolver(@Nonnull final UserManager userManager, final OnMessageSourcesResolvedListener listener) {
        this.lock = new Object();
        this.requestEntryMap = new ConcurrentHashMap<UUID, NameRequestEntry>();
        this.onUserName = new Subscription.OnData<UserName>() {
            public void onData(final UserName userName) {
                while (true) {
                    Set<Long> set = null;
                    while (true) {
                        Label_0133: {
                            Object o;
                            synchronized (MessageSourceNameResolver.this.lock) {
                                final NameRequestEntry nameRequestEntry = MessageSourceNameResolver.this.requestEntryMap.get(userName.getUuid());
                                if (nameRequestEntry == null) {
                                    break Label_0133;
                                }
                                o = new HashSet<Long>(nameRequestEntry.getMessageIDs());
                                if (userName.isComplete()) {
                                    MessageSourceNameResolver.this.requestEntryMap.remove(userName.getUuid());
                                    set = (Set<Long>)o;
                                    o = nameRequestEntry;
                                    monitorexit(MessageSourceNameResolver.this.lock);
                                    if (o != null) {
                                        ((NameRequestEntry)o).unsubscribe();
                                    }
                                    if (set != null) {
                                        MessageSourceNameResolver.this.listener.onMessageSourcesResolved(set, userName);
                                    }
                                    return;
                                }
                            }
                            final Set<Long> set2 = null;
                            set = (Set<Long>)o;
                            o = set2;
                            continue;
                        }
                        Object o = null;
                        continue;
                    }
                }
            }
        };
        this.userManager = userManager;
        this.listener = listener;
        this.dbExecutor = userManager.getDatabaseExecutor();
    }
    
    public void requestResolve(final UUID uuid, final Long n) {
        boolean b = false;
        synchronized (this.lock) {
            final NameRequestEntry nameRequestEntry = this.requestEntryMap.get(uuid);
            NameRequestEntry nameRequestEntry3;
            if (nameRequestEntry == null) {
                final NameRequestEntry nameRequestEntry2 = new NameRequestEntry(uuid, n);
                b = true;
                this.requestEntryMap.put(uuid, nameRequestEntry2);
                nameRequestEntry3 = nameRequestEntry2;
            }
            else {
                nameRequestEntry.addMessageID(n);
                nameRequestEntry3 = nameRequestEntry;
            }
            monitorexit(this.lock);
            if (b) {
                nameRequestEntry3.subscribe();
            }
        }
    }
    
    private class NameRequestEntry
    {
        private final Set<Long> messageDatabaseIDs;
        private Subscription<UUID, UserName> subscription;
        private final UUID userUUID;
        
        public NameRequestEntry(final UUID userUUID, final Long n) {
            this.userUUID = userUUID;
            (this.messageDatabaseIDs = new HashSet<Long>()).add(n);
        }
        
        public void addMessageID(final Long n) {
            this.messageDatabaseIDs.add(n);
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
    
    public interface OnMessageSourcesResolvedListener
    {
        void onMessageSourcesResolved(final Set<Long> p0, final UserName p1);
    }
}
