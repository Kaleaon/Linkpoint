package com.lumiyaviewer.lumiya.slproto.users.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.dao.ChatMessageDao;
import com.lumiyaviewer.lumiya.dao.Chatter;
import com.lumiyaviewer.lumiya.dao.ChatterDao;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.sync.CloudSyncServiceConnection;
import de.greenrobot.dao.query.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class SyncManager {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f226xb1d54699 = null;
    private static final int MAX_MESSAGES_PER_BATCH = 100;
    @Nonnull
    private final ChatMessageDao chatMessageDao;
    @Nonnull
    private final ChatterDao chatterDao;
    @Nonnull
    private final Context context;
    private final DateFormat dateFormat;
    @Nonnull
    private final Executor dbExecutor;
    @Nonnull
    private final String localChatName;
    private final Query<ChatMessage> messagesQuery;
    @Nonnull
    private final UserManager userManager;
    private final AtomicBoolean syncingEnabled = new AtomicBoolean(false);
    private final AtomicReference<CloudSyncServiceConnection> syncServiceConnection = new AtomicReference<>();
    private final AtomicBoolean syncMessageSent = new AtomicBoolean(false);
    private final AtomicBoolean needsStopSyncing = new AtomicBoolean(false);
    private final Map<ChatterID, ChatterNameRetriever> flushChatters = new ConcurrentHashMap();
    private final Set<String> flushChatterNames = Collections.newSetFromMap(new ConcurrentHashMap());
    private long lastConfirmedMessageID = 0;
    private ChatterNameRetriever myNameRetriever = null;
    private ChatterNameRetriever chatterNameRetriever = null;

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-users-ChatterID$ChatterTypeSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m366x2680ba3d() {
        if (f226xb1d54699 != null) {
            return f226xb1d54699;
        }
        int[] iArr = new int[ChatterID.ChatterType.valuesCustom().length];
        try {
            iArr[ChatterID.ChatterType.Group.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ChatterID.ChatterType.Local.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ChatterID.ChatterType.User.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        f226xb1d54699 = iArr;
        return iArr;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @SuppressLint({"SimpleDateFormat"})
    public SyncManager(@Nonnull UserManager userManager) {
        this.userManager = userManager;
        this.dbExecutor = userManager.getDatabaseExecutor();
        DaoSession daoSession = userManager.getDaoSession();
        this.chatMessageDao = daoSession.getChatMessageDao();
        this.chatterDao = daoSession.getChatterDao();
        this.context = LumiyaApp.getContext();
        this.localChatName = this.context.getString(R.string.local_chat_title);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.messagesQuery = this.chatMessageDao.queryBuilder().where(ChatMessageDao.Properties.Id.gt(null), ChatMessageDao.Properties.SyncedToGoogleDrive.eq(false)).orderAsc(ChatMessageDao.Properties.Id).limit(100).build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onChatterNameRetrieved */
    public void m372x9b8293ab(ChatterNameRetriever chatterNameRetriever) {
        this.dbExecutor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.3
            private final /* synthetic */ void $m$0() {
                ((SyncManager) this).m371x9b8293aa();
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onFlushChatterNameRetrieved */
    public void m373x9b8293ac(ChatterNameRetriever chatterNameRetriever) {
        String resolvedName = chatterNameRetriever.getResolvedName();
        this.flushChatters.remove(chatterNameRetriever.chatterID);
        chatterNameRetriever.dispose();
        if (Strings.isNullOrEmpty(resolvedName) || !this.flushChatterNames.add(resolvedName)) {
            return;
        }
        m371x9b8293aa();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onMyNameRetrieved */
    public void m368x9b8293a7(ChatterNameRetriever chatterNameRetriever) {
        this.dbExecutor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.4
            private final /* synthetic */ void $m$0() {
                ((SyncManager) this).m370x9b8293a9();
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: processMessagesFlushed */
    public void m377xcf5b7a09(ImmutableList<Long> immutableList) {
        for (Long l : immutableList) {
            ChatMessage load = this.chatMessageDao.load(l);
            if (load != null && !load.getSyncedToGoogleDrive()) {
                load.setSyncedToGoogleDrive(true);
                this.chatMessageDao.update(load);
            }
        }
    }

    @Nullable
    private String resolveChatterName(@Nonnull Chatter chatter) {
        if (chatter.getType() == ChatterID.ChatterType.User.ordinal() || chatter.getType() == ChatterID.ChatterType.Group.ordinal()) {
            ChatterID fromDatabaseObject = ChatterID.fromDatabaseObject(this.userManager.getUserID(), chatter);
            if (this.chatterNameRetriever == null || (!this.chatterNameRetriever.chatterID.equals(fromDatabaseObject))) {
                if (this.chatterNameRetriever != null) {
                    this.chatterNameRetriever.dispose();
                }
                this.chatterNameRetriever = new ChatterNameRetriever(fromDatabaseObject, new ChatterNameRetriever.OnChatterNameUpdated() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.1
                    private final /* synthetic */ void $m$0(ChatterNameRetriever chatterNameRetriever) {
                        ((SyncManager) this).m372x9b8293ab(chatterNameRetriever);
                    }

                    @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
                    public final void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
                        $m$0(chatterNameRetriever);
                    }
                }, this.dbExecutor, true);
            }
            return this.chatterNameRetriever.getResolvedName();
        }
        return this.localChatName;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:31:0x014f  */
    /* renamed from: syncMoreMessages */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void m371x9b8293aa() {
        if (!this.syncMessageSent.getAndSet(true)) {
            boolean keepSyncMessageSent = false;
            String myName = null;
            if (this.myNameRetriever == null) {
                this.myNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(this.userManager.getUserID(), this.userManager.getUserID()), new ChatterNameRetriever.OnChatterNameUpdated() {
                    @Override
                    public void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
                        SyncManager.this.m368x9b8293a7(chatterNameRetriever);
                    }
                }, this.dbExecutor, true);
            }
            myName = this.myNameRetriever.getResolvedName();
            if (myName != null) {
                Query<ChatMessage> query = this.messagesQuery.forCurrentThread();
                query.setParameter(0, Long.valueOf(this.lastConfirmedMessageID));
                de.greenrobot.dao.query.LazyList<ChatMessage> messages = query.listLazy();
                ImmutableList.Builder<com.lumiyaviewer.lumiya.cloud.common.LogChatMessage> builder = ImmutableList.builder();
                int count = 0;
                long lastMessageId = 0;
                for (ChatMessage chatMessage : messages) {
                    com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent chatEvent = com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.loadFromDatabaseObject(chatMessage, this.userManager.getUserID());
                    if (chatEvent != null) {
                        Chatter chatter = this.chatterDao.load(Long.valueOf(chatMessage.getChatterID()));
                        if (chatter != null) {
                            String chatterName = resolveChatterName(chatter);
                            if (chatterName == null) {
                                break;
                            }
                            CharSequence text = chatEvent.getPlainTextMessage(this.context, this.userManager, false);
                            com.lumiyaviewer.lumiya.cloud.common.LogChatMessage logChatMessage = new com.lumiyaviewer.lumiya.cloud.common.LogChatMessage(chatter.getType(), chatter.getUuid(), chatMessage.getId().longValue(), chatterName, "[" + this.dateFormat.format(chatMessage.getTimestamp()) + "] " + text);
                            builder.add(logChatMessage);
                            lastMessageId = logChatMessage.messageID;
                            count++;
                            if (count >= 100) {
                                break;
                            }
                        }
                    }
                }
                messages.close();
                if (count > 0) {
                    com.lumiyaviewer.lumiya.cloud.common.LogMessageBatch batch = new com.lumiyaviewer.lumiya.cloud.common.LogMessageBatch(this.userManager.getUserID(), myName, builder.build(), lastMessageId);
                    CloudSyncServiceConnection connection = this.syncServiceConnection.get();
                    if (connection != null) {
                        keepSyncMessageSent = connection.sendMessage(com.lumiyaviewer.lumiya.cloud.common.MessageType.LogMessageBatch, batch);
                    }
                }
                if (!this.flushChatterNames.isEmpty()) {
                    CloudSyncServiceConnection connection2 = this.syncServiceConnection.get();
                    if (connection2 != null) {
                        java.util.Iterator<String> iterator = this.flushChatterNames.iterator();
                        if (iterator.hasNext()) {
                            String chatterName2 = iterator.next();
                            iterator.remove();
                            connection2.sendMessage(com.lumiyaviewer.lumiya.cloud.common.MessageType.LogFlushMessages, new com.lumiyaviewer.lumiya.cloud.common.LogFlushMessages(this.userManager.getUserID(), myName, chatterName2));
                        }
                    }
                }
            }
            this.syncMessageSent.set(keepSyncMessageSent);
        }
        if (this.needsStopSyncing.getAndSet(false)) {
            this.syncingEnabled.set(false);
            CloudSyncServiceConnection connection3 = this.syncServiceConnection.getAndSet(null);
            this.syncMessageSent.set(false);
            if (connection3 != null) {
                connection3.sendMessage(com.lumiyaviewer.lumiya.cloud.common.MessageType.LogFlushMessages, new com.lumiyaviewer.lumiya.cloud.common.LogFlushMessages(this.userManager.getUserID(), null, null));
                connection3.disconnect();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void flushChatter(final ChatterID chatterID) {
        if (this.syncingEnabled.get()) {
            this.dbExecutor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.8
                private final /* synthetic */ void $m$0() {
                    ((SyncManager) this).m375x1b9f5c8c((ChatterID) chatterID);
                }

                @Override // java.lang.Runnable
                public final void run() {
                    $m$0();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_SyncManager_10038  reason: not valid java name */
    public /* synthetic */ void m374x1b9f54d0() {
        this.needsStopSyncing.set(true);
        m371x9b8293aa();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_SyncManager_10254  reason: not valid java name */
    public /* synthetic */ void m375x1b9f5c8c(ChatterID chatterID) {
        switch (m366x2680ba3d()[chatterID.getChatterType().ordinal()]) {
            case 1:
            case 3:
                if (this.flushChatters.containsKey(chatterID)) {
                    return;
                }
                new ChatterNameRetriever(chatterID, new ChatterNameRetriever.OnChatterNameUpdated() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8
                    private final /* synthetic */ void $m$0(ChatterNameRetriever chatterNameRetriever) {
                        ((SyncManager) this).m373x9b8293ac(chatterNameRetriever);
                    }

                    @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
                    public final void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
                        $m$0(chatterNameRetriever);
                    }
                }, this.dbExecutor, false).subscribe();
                return;
            case 2:
                if (this.flushChatterNames.add(this.localChatName)) {
                    m371x9b8293aa();
                    return;
                }
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_SyncManager_9602  reason: not valid java name */
    public /* synthetic */ void m376xcf5b71e5(long j) {
        this.lastConfirmedMessageID = j;
        this.syncMessageSent.set(false);
        m371x9b8293aa();
    }

    public void onMessagesFlushed(final ImmutableList<Long> immutableList) {
        this.dbExecutor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.9
            private final /* synthetic */ void $m$0() {
                ((SyncManager) this).m377xcf5b7a09((ImmutableList) immutableList);
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    public void onMessagesWritten(final long j) {
        this.dbExecutor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.10
            private final /* synthetic */ void $m$0() {
                ((SyncManager) this).m376xcf5b71e5(j);
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    public void startSyncing(CloudSyncServiceConnection cloudSyncServiceConnection) {
        this.syncServiceConnection.set(cloudSyncServiceConnection);
        this.syncingEnabled.set(true);
        this.needsStopSyncing.set(false);
        this.dbExecutor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.5
            private final /* synthetic */ void $m$0() {
                ((SyncManager) this).m367x9b8293a6();
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    public void stopSyncing() {
        Debug.Printf("SyncManager: requested to stop syncing", new Object[0]);
        this.dbExecutor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.6
            private final /* synthetic */ void $m$0() {
                ((SyncManager) this).m374x1b9f54d0();
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void syncNewMessages() {
        if (this.syncingEnabled.get()) {
            this.dbExecutor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$AZwop9CtlZWAAgrWZJSwnA0FdZ8.7
                private final /* synthetic */ void $m$0() {
                    ((SyncManager) this).m369x9b8293a8();
                }

                @Override // java.lang.Runnable
                public final void run() {
                    $m$0();
                }
            });
        }
    }
}
