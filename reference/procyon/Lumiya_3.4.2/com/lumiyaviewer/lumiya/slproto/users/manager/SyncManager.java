// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import de.greenrobot.dao.AbstractDao;
import com.lumiyaviewer.lumiya.Debug;
import de.greenrobot.dao.query.LazyList;
import com.lumiyaviewer.lumiya.cloud.common.LogFlushMessages;
import com.lumiyaviewer.lumiya.cloud.common.Bundleable;
import com.lumiyaviewer.lumiya.cloud.common.MessageType;
import java.util.List;
import com.lumiyaviewer.lumiya.cloud.common.LogMessageBatch;
import com.lumiyaviewer.lumiya.cloud.common.LogChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.dao.Chatter;
import java.util.Iterator;
import com.google.common.collect.ImmutableList;
import com.google.common.base.Strings;
import android.annotation.SuppressLint;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.WhereCondition;
import java.text.SimpleDateFormat;
import com.lumiyaviewer.lumiya.LumiyaApp;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import com.lumiyaviewer.lumiya.sync.CloudSyncServiceConnection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import de.greenrobot.dao.query.Query;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.text.DateFormat;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.dao.ChatterDao;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.dao.ChatMessageDao;

public class SyncManager
{
    private static final int MAX_MESSAGES_PER_BATCH = 100;
    @Nonnull
    private final ChatMessageDao chatMessageDao;
    @Nonnull
    private final ChatterDao chatterDao;
    private ChatterNameRetriever chatterNameRetriever;
    @Nonnull
    private final Context context;
    private final DateFormat dateFormat;
    @Nonnull
    private final Executor dbExecutor;
    private final Set<String> flushChatterNames;
    private final Map<ChatterID, ChatterNameRetriever> flushChatters;
    private long lastConfirmedMessageID;
    @Nonnull
    private final String localChatName;
    private final Query<ChatMessage> messagesQuery;
    private ChatterNameRetriever myNameRetriever;
    private final AtomicBoolean needsStopSyncing;
    private final AtomicBoolean syncMessageSent;
    private final AtomicReference<CloudSyncServiceConnection> syncServiceConnection;
    private final AtomicBoolean syncingEnabled;
    @Nonnull
    private final UserManager userManager;
    
    @SuppressLint({ "SimpleDateFormat" })
    SyncManager(@Nonnull final UserManager userManager) {
        this.syncingEnabled = new AtomicBoolean(false);
        this.syncServiceConnection = new AtomicReference<CloudSyncServiceConnection>();
        this.syncMessageSent = new AtomicBoolean(false);
        this.needsStopSyncing = new AtomicBoolean(false);
        this.flushChatters = new ConcurrentHashMap<ChatterID, ChatterNameRetriever>();
        this.flushChatterNames = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
        this.lastConfirmedMessageID = 0L;
        this.myNameRetriever = null;
        this.chatterNameRetriever = null;
        this.userManager = userManager;
        this.dbExecutor = userManager.getDatabaseExecutor();
        final DaoSession daoSession = userManager.getDaoSession();
        this.chatMessageDao = daoSession.getChatMessageDao();
        this.chatterDao = daoSession.getChatterDao();
        this.context = LumiyaApp.getContext();
        this.localChatName = this.context.getString(2131296658);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.messagesQuery = ((AbstractDao<ChatMessage, K>)this.chatMessageDao).queryBuilder().where(ChatMessageDao.Properties.Id.gt(null), ChatMessageDao.Properties.SyncedToGoogleDrive.eq(false)).orderAsc(ChatMessageDao.Properties.Id).limit(100).build();
    }
    
    private void onChatterNameRetrieved(final ChatterNameRetriever chatterNameRetriever) {
        this.dbExecutor.execute(new _$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8$3(this));
    }
    
    private void onFlushChatterNameRetrieved(final ChatterNameRetriever chatterNameRetriever) {
        final String resolvedName = chatterNameRetriever.getResolvedName();
        this.flushChatters.remove(chatterNameRetriever.chatterID);
        chatterNameRetriever.dispose();
        if (!Strings.isNullOrEmpty(resolvedName) && this.flushChatterNames.add(resolvedName)) {
            this.syncMoreMessages();
        }
    }
    
    private void onMyNameRetrieved(final ChatterNameRetriever chatterNameRetriever) {
        this.dbExecutor.execute(new _$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8$4(this));
    }
    
    private void processMessagesFlushed(final ImmutableList<Long> list) {
        final Iterator<Object> iterator = list.iterator();
        while (iterator.hasNext()) {
            final ChatMessage chatMessage = this.chatMessageDao.load(iterator.next());
            if (chatMessage != null && !chatMessage.getSyncedToGoogleDrive()) {
                chatMessage.setSyncedToGoogleDrive(true);
                ((AbstractDao<ChatMessage, K>)this.chatMessageDao).update(chatMessage);
            }
        }
    }
    
    @Nullable
    private String resolveChatterName(@Nonnull final Chatter chatter) {
        if (chatter.getType() == ChatterID.ChatterType.User.ordinal() || chatter.getType() == ChatterID.ChatterType.Group.ordinal()) {
            final ChatterID fromDatabaseObject = ChatterID.fromDatabaseObject(this.userManager.getUserID(), chatter);
            if (this.chatterNameRetriever == null || (this.chatterNameRetriever.chatterID.equals(fromDatabaseObject) ^ true)) {
                if (this.chatterNameRetriever != null) {
                    this.chatterNameRetriever.dispose();
                }
                this.chatterNameRetriever = new ChatterNameRetriever(fromDatabaseObject, (ChatterNameRetriever.OnChatterNameUpdated)new _$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8$1(this), this.dbExecutor, true);
            }
            return this.chatterNameRetriever.getResolvedName();
        }
        return this.localChatName;
    }
    
    private void syncMoreMessages() {
        if (!this.syncMessageSent.getAndSet(true)) {
            if (this.myNameRetriever == null) {
                this.myNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(this.userManager.getUserID(), this.userManager.getUserID()), (ChatterNameRetriever.OnChatterNameUpdated)new _$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8$2(this), this.dbExecutor, true);
            }
            final String resolvedName = this.myNameRetriever.getResolvedName();
            boolean newValue = false;
            Label_0515: {
                if (resolvedName != null) {
                    final Query<ChatMessage> forCurrentThread = this.messagesQuery.forCurrentThread();
                    forCurrentThread.setParameter(0, this.lastConfirmedMessageID);
                    final LazyList listLazy = forCurrentThread.listLazy();
                    final ImmutableList.Builder<LogChatMessage> builder = ImmutableList.builder();
                    final Iterator iterator = listLazy.iterator();
                    long n = 0L;
                    int n2 = 0;
                    long n3;
                    int n4;
                    while (true) {
                        n3 = n;
                        n4 = n2;
                        if (!iterator.hasNext()) {
                            break;
                        }
                        final ChatMessage chatMessage = (ChatMessage)iterator.next();
                        final SLChatEvent loadFromDatabaseObject = SLChatEvent.loadFromDatabaseObject(chatMessage, this.userManager.getUserID());
                        long n5 = n;
                        int n6 = n2;
                        if (loadFromDatabaseObject != null) {
                            final Chatter chatter = this.chatterDao.load(chatMessage.getChatterID());
                            n5 = n;
                            n6 = n2;
                            if (chatter != null) {
                                final String resolveChatterName = this.resolveChatterName(chatter);
                                n3 = n;
                                n4 = n2;
                                if (resolveChatterName == null) {
                                    break;
                                }
                                final LogChatMessage logChatMessage = new LogChatMessage(chatter.getType(), chatter.getUuid(), chatMessage.getId(), resolveChatterName, "[" + this.dateFormat.format(chatMessage.getTimestamp()) + "] " + loadFromDatabaseObject.getPlainTextMessage(this.context, this.userManager, false));
                                builder.add(logChatMessage);
                                final long messageID = logChatMessage.messageID;
                                ++n2;
                                n5 = messageID;
                                if ((n6 = n2) >= 100) {
                                    n4 = n2;
                                    n3 = messageID;
                                    break;
                                }
                            }
                        }
                        n = n5;
                        n2 = n6;
                    }
                    listLazy.close();
                    while (true) {
                        Label_0607: {
                            if (n4 == 0) {
                                break Label_0607;
                            }
                            final LogMessageBatch logMessageBatch = new LogMessageBatch(this.userManager.getUserID(), resolvedName, builder.build(), n3);
                            final CloudSyncServiceConnection cloudSyncServiceConnection = this.syncServiceConnection.get();
                            if (cloudSyncServiceConnection == null) {
                                break Label_0607;
                            }
                            final boolean sendMessage = cloudSyncServiceConnection.sendMessage(MessageType.LogMessageBatch, logMessageBatch);
                            newValue = sendMessage;
                            if (this.flushChatterNames.isEmpty()) {
                                break Label_0515;
                            }
                            final CloudSyncServiceConnection cloudSyncServiceConnection2 = this.syncServiceConnection.get();
                            newValue = sendMessage;
                            if (cloudSyncServiceConnection2 == null) {
                                break Label_0515;
                            }
                            final Iterator<String> iterator2 = this.flushChatterNames.iterator();
                            newValue = sendMessage;
                            if (iterator2.hasNext()) {
                                final String s = iterator2.next();
                                iterator2.remove();
                                cloudSyncServiceConnection2.sendMessage(MessageType.LogFlushMessages, new LogFlushMessages(this.userManager.getUserID(), resolvedName, s));
                                newValue = sendMessage;
                            }
                            break Label_0515;
                        }
                        final boolean sendMessage = false;
                        continue;
                    }
                }
                newValue = false;
            }
            this.syncMessageSent.set(newValue);
        }
        if (this.needsStopSyncing.getAndSet(false)) {
            this.syncingEnabled.set(false);
            final CloudSyncServiceConnection cloudSyncServiceConnection3 = this.syncServiceConnection.getAndSet(null);
            this.syncMessageSent.set(false);
            if (cloudSyncServiceConnection3 != null) {
                cloudSyncServiceConnection3.sendMessage(MessageType.LogFlushMessages, new LogFlushMessages(this.userManager.getUserID(), null, null));
                cloudSyncServiceConnection3.disconnect();
            }
        }
    }
    
    void flushChatter(final ChatterID chatterID) {
        if (this.syncingEnabled.get()) {
            this.dbExecutor.execute(new _$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8$8(this, chatterID));
        }
    }
    
    public void onMessagesFlushed(final ImmutableList<Long> list) {
        this.dbExecutor.execute(new _$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8$9(this, list));
    }
    
    public void onMessagesWritten(final long n) {
        this.dbExecutor.execute(new _$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8$10(n, this));
    }
    
    public void startSyncing(final CloudSyncServiceConnection newValue) {
        this.syncServiceConnection.set(newValue);
        this.syncingEnabled.set(true);
        this.needsStopSyncing.set(false);
        this.dbExecutor.execute(new _$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8$5(this));
    }
    
    public void stopSyncing() {
        Debug.Printf("SyncManager: requested to stop syncing", new Object[0]);
        this.dbExecutor.execute(new _$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8$6(this));
    }
    
    void syncNewMessages() {
        if (this.syncingEnabled.get()) {
            this.dbExecutor.execute(new _$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8$7(this));
        }
    }
}
