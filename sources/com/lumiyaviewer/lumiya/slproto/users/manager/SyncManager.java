// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import android.content.Context;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.cloud.common.LogChatMessage;
import com.lumiyaviewer.lumiya.cloud.common.LogFlushMessages;
import com.lumiyaviewer.lumiya.cloud.common.LogMessageBatch;
import com.lumiyaviewer.lumiya.cloud.common.MessageType;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.dao.ChatMessageDao;
import com.lumiyaviewer.lumiya.dao.Chatter;
import com.lumiyaviewer.lumiya.dao.ChatterDao;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.sync.CloudSyncServiceConnection;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UserManager

public class SyncManager
{

    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues[];
    private static final int MAX_MESSAGES_PER_BATCH = 100;
    private final ChatMessageDao chatMessageDao;
    private final ChatterDao chatterDao;
    private ChatterNameRetriever chatterNameRetriever;
    private final Context context = LumiyaApp.getContext();
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final Executor dbExecutor;
    private final Set flushChatterNames = Collections.newSetFromMap(new ConcurrentHashMap());
    private final Map flushChatters = new ConcurrentHashMap();
    private long lastConfirmedMessageID;
    private final String localChatName;
    private final Query messagesQuery;
    private ChatterNameRetriever myNameRetriever;
    private final AtomicBoolean needsStopSyncing = new AtomicBoolean(false);
    private final AtomicBoolean syncMessageSent = new AtomicBoolean(false);
    private final AtomicReference syncServiceConnection = new AtomicReference();
    private final AtomicBoolean syncingEnabled = new AtomicBoolean(false);
    private final UserManager userManager;

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues;
        }
        int ai[] = new int[com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.values().length];
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Group.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Local.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.User.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues = ai;
        return ai;
    }

    SyncManager(UserManager usermanager)
    {
        lastConfirmedMessageID = 0L;
        myNameRetriever = null;
        chatterNameRetriever = null;
        userManager = usermanager;
        dbExecutor = usermanager.getDatabaseExecutor();
        usermanager = usermanager.getDaoSession();
        chatMessageDao = usermanager.getChatMessageDao();
        chatterDao = usermanager.getChatterDao();
        localChatName = context.getString(0x7f090192);
        messagesQuery = chatMessageDao.queryBuilder().where(com.lumiyaviewer.lumiya.dao.ChatMessageDao.Properties.Id.gt(null), new WhereCondition[] {
            com.lumiyaviewer.lumiya.dao.ChatMessageDao.Properties.SyncedToGoogleDrive.eq(Boolean.valueOf(false))
        }).orderAsc(new Property[] {
            com.lumiyaviewer.lumiya.dao.ChatMessageDao.Properties.Id
        }).limit(100).build();
    }

    private void onChatterNameRetrieved(ChatterNameRetriever chatternameretriever)
    {
        dbExecutor.execute(new _2D_.Lambda.AZwop9CtlZWAAgrWZJSwnA0FdZ8._cls3(this));
    }

    private void onFlushChatterNameRetrieved(ChatterNameRetriever chatternameretriever)
    {
        String s = chatternameretriever.getResolvedName();
        flushChatters.remove(chatternameretriever.chatterID);
        chatternameretriever.dispose();
        if (!Strings.isNullOrEmpty(s) && flushChatterNames.add(s))
        {
            syncMoreMessages();
        }
    }

    private void onMyNameRetrieved(ChatterNameRetriever chatternameretriever)
    {
        dbExecutor.execute(new _2D_.Lambda.AZwop9CtlZWAAgrWZJSwnA0FdZ8._cls4(this));
    }

    private void processMessagesFlushed(ImmutableList immutablelist)
    {
        immutablelist = immutablelist.iterator();
        do
        {
            if (!immutablelist.hasNext())
            {
                break;
            }
            Object obj = (Long)immutablelist.next();
            obj = (ChatMessage)chatMessageDao.load(obj);
            if (obj != null && !((ChatMessage) (obj)).getSyncedToGoogleDrive())
            {
                ((ChatMessage) (obj)).setSyncedToGoogleDrive(true);
                chatMessageDao.update(obj);
            }
        } while (true);
    }

    private String resolveChatterName(Chatter chatter)
    {
        if (chatter.getType() == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.User.ordinal() || chatter.getType() == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Group.ordinal())
        {
            chatter = ChatterID.fromDatabaseObject(userManager.getUserID(), chatter);
            if (chatterNameRetriever == null || chatterNameRetriever.chatterID.equals(chatter) ^ true)
            {
                if (chatterNameRetriever != null)
                {
                    chatterNameRetriever.dispose();
                }
                chatterNameRetriever = new ChatterNameRetriever(chatter, new _2D_.Lambda.AZwop9CtlZWAAgrWZJSwnA0FdZ8._cls1(this), dbExecutor, true);
            }
            return chatterNameRetriever.getResolvedName();
        } else
        {
            return localChatName;
        }
    }

    private void syncMoreMessages()
    {
        if (syncMessageSent.getAndSet(true)) goto _L2; else goto _L1
_L1:
        String s;
        Object obj;
        Object obj1;
        Iterator iterator1;
        int i;
        long l;
        if (myNameRetriever == null)
        {
            myNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(userManager.getUserID(), userManager.getUserID()), new _2D_.Lambda.AZwop9CtlZWAAgrWZJSwnA0FdZ8._cls2(this), dbExecutor, true);
        }
        s = myNameRetriever.getResolvedName();
        if (s == null)
        {
            break MISSING_BLOCK_LABEL_613;
        }
        obj = messagesQuery.forCurrentThread();
        ((Query) (obj)).setParameter(0, Long.valueOf(lastConfirmedMessageID));
        obj = ((Query) (obj)).listLazy();
        obj1 = ImmutableList.builder();
        iterator1 = ((Iterable) (obj)).iterator();
        l = 0L;
        i = 0;
_L13:
        int j;
        long l1;
        l1 = l;
        j = i;
        if (!iterator1.hasNext()) goto _L4; else goto _L3
_L3:
        Object obj2;
        Object obj3;
        obj2 = (ChatMessage)iterator1.next();
        obj3 = SLChatEvent.loadFromDatabaseObject(((ChatMessage) (obj2)), userManager.getUserID());
        l1 = l;
        j = i;
        if (obj3 == null) goto _L6; else goto _L5
_L5:
        Chatter chatter;
        chatter = (Chatter)chatterDao.load(Long.valueOf(((ChatMessage) (obj2)).getChatterID()));
        l1 = l;
        j = i;
        if (chatter == null) goto _L6; else goto _L7
_L7:
        String s2;
        s2 = resolveChatterName(chatter);
        l1 = l;
        j = i;
        if (s2 == null) goto _L4; else goto _L8
_L8:
        obj3 = ((SLChatEvent) (obj3)).getPlainTextMessage(context, userManager, false);
        obj2 = new LogChatMessage(chatter.getType(), chatter.getUuid(), ((ChatMessage) (obj2)).getId().longValue(), s2, (new StringBuilder()).append("[").append(dateFormat.format(((ChatMessage) (obj2)).getTimestamp())).append("] ").append(((CharSequence) (obj3))).toString());
        ((com.google.common.collect.ImmutableList.Builder) (obj1)).add(obj2);
        l = ((LogChatMessage) (obj2)).messageID;
        i++;
        l1 = l;
        j = i;
        if (i < 100) goto _L6; else goto _L9
_L9:
        j = i;
        l1 = l;
_L4:
        ((LazyList) (obj)).close();
        if (j == 0) goto _L11; else goto _L10
_L10:
        obj = new LogMessageBatch(userManager.getUserID(), s, ((com.google.common.collect.ImmutableList.Builder) (obj1)).build(), l1);
        obj1 = (CloudSyncServiceConnection)syncServiceConnection.get();
        if (obj1 == null) goto _L11; else goto _L12
_L12:
        boolean flag = ((CloudSyncServiceConnection) (obj1)).sendMessage(MessageType.LogMessageBatch, ((com.lumiyaviewer.lumiya.cloud.common.Bundleable) (obj)));
_L14:
        boolean flag1;
        flag1 = flag;
        if (!flushChatterNames.isEmpty())
        {
            CloudSyncServiceConnection cloudsyncserviceconnection1 = (CloudSyncServiceConnection)syncServiceConnection.get();
            flag1 = flag;
            if (cloudsyncserviceconnection1 != null)
            {
                Iterator iterator = flushChatterNames.iterator();
                flag1 = flag;
                if (iterator.hasNext())
                {
                    String s1 = (String)iterator.next();
                    iterator.remove();
                    cloudsyncserviceconnection1.sendMessage(MessageType.LogFlushMessages, new LogFlushMessages(userManager.getUserID(), s, s1));
                    flag1 = flag;
                }
            }
        }
_L15:
        syncMessageSent.set(flag1);
_L2:
        if (needsStopSyncing.getAndSet(false))
        {
            syncingEnabled.set(false);
            CloudSyncServiceConnection cloudsyncserviceconnection = (CloudSyncServiceConnection)syncServiceConnection.getAndSet(null);
            syncMessageSent.set(false);
            if (cloudsyncserviceconnection != null)
            {
                cloudsyncserviceconnection.sendMessage(MessageType.LogFlushMessages, new LogFlushMessages(userManager.getUserID(), null, null));
                cloudsyncserviceconnection.disconnect();
            }
        }
        return;
_L6:
        l = l1;
        i = j;
          goto _L13
_L11:
        flag = false;
          goto _L14
        flag1 = false;
          goto _L15
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_users_manager_SyncManager_2D_mthref_2D_0()
    {
        syncMoreMessages();
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_users_manager_SyncManager_2D_mthref_2D_1(ChatterNameRetriever chatternameretriever)
    {
        onMyNameRetrieved(chatternameretriever);
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_users_manager_SyncManager_2D_mthref_2D_2()
    {
        syncMoreMessages();
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_users_manager_SyncManager_2D_mthref_2D_3()
    {
        syncMoreMessages();
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_users_manager_SyncManager_2D_mthref_2D_4()
    {
        syncMoreMessages();
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_users_manager_SyncManager_2D_mthref_2D_5(ChatterNameRetriever chatternameretriever)
    {
        onChatterNameRetrieved(chatternameretriever);
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_users_manager_SyncManager_2D_mthref_2D_6(ChatterNameRetriever chatternameretriever)
    {
        onFlushChatterNameRetrieved(chatternameretriever);
    }

    void flushChatter(ChatterID chatterid)
    {
        if (syncingEnabled.get())
        {
            dbExecutor.execute(new _2D_.Lambda.AZwop9CtlZWAAgrWZJSwnA0FdZ8._cls8(this, chatterid));
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_SyncManager_10038()
    {
        needsStopSyncing.set(true);
        syncMoreMessages();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_SyncManager_10254(ChatterID chatterid)
    {
        _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues()[chatterid.getChatterType().ordinal()];
        JVM INSTR tableswitch 1 3: default 36
    //                   1 58
    //                   2 37
    //                   3 58;
           goto _L1 _L2 _L3 _L2
_L1:
        return;
_L3:
        if (flushChatterNames.add(localChatName))
        {
            syncMoreMessages();
            return;
        }
        continue; /* Loop/switch isn't completed */
_L2:
        if (!flushChatters.containsKey(chatterid))
        {
            (new ChatterNameRetriever(chatterid, new _2D_.Lambda.AZwop9CtlZWAAgrWZJSwnA0FdZ8(this), dbExecutor, false)).subscribe();
            return;
        }
        if (true) goto _L1; else goto _L4
_L4:
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_SyncManager_9602(long l)
    {
        lastConfirmedMessageID = l;
        syncMessageSent.set(false);
        syncMoreMessages();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_SyncManager_9859(ImmutableList immutablelist)
    {
        processMessagesFlushed(immutablelist);
    }

    public void onMessagesFlushed(ImmutableList immutablelist)
    {
        dbExecutor.execute(new _2D_.Lambda.AZwop9CtlZWAAgrWZJSwnA0FdZ8._cls9(this, immutablelist));
    }

    public void onMessagesWritten(long l)
    {
        dbExecutor.execute(new _2D_.Lambda.AZwop9CtlZWAAgrWZJSwnA0FdZ8._cls10(l, this));
    }

    public void startSyncing(CloudSyncServiceConnection cloudsyncserviceconnection)
    {
        syncServiceConnection.set(cloudsyncserviceconnection);
        syncingEnabled.set(true);
        needsStopSyncing.set(false);
        dbExecutor.execute(new _2D_.Lambda.AZwop9CtlZWAAgrWZJSwnA0FdZ8._cls5(this));
    }

    public void stopSyncing()
    {
        Debug.Printf("SyncManager: requested to stop syncing", new Object[0]);
        dbExecutor.execute(new _2D_.Lambda.AZwop9CtlZWAAgrWZJSwnA0FdZ8._cls6(this));
    }

    void syncNewMessages()
    {
        if (syncingEnabled.get())
        {
            dbExecutor.execute(new _2D_.Lambda.AZwop9CtlZWAAgrWZJSwnA0FdZ8._cls7(this));
        }
    }
}
