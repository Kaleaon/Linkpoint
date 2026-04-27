// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.mutelist;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.dao.MuteListCachedData;
import com.lumiyaviewer.lumiya.dao.MuteListCachedDataDao;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.MuteListRequest;
import com.lumiyaviewer.lumiya.slproto.messages.MuteListUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.RemoveMuteListEntry;
import com.lumiyaviewer.lumiya.slproto.messages.UpdateMuteListEntry;
import com.lumiyaviewer.lumiya.slproto.messages.UseCachedMuteList;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.xfer.ELLPath;
import com.lumiyaviewer.lumiya.slproto.modules.xfer.SLXferManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.QueryBuilder;
import java.util.Iterator;
import java.util.UUID;
import java.util.zip.CRC32;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.mutelist:
//            MuteListData, MuteListEntry, MuteType

public class SLMuteList extends SLModule
    implements com.lumiyaviewer.lumiya.slproto.modules.xfer.SLXfer.SLXferCompletionListener
{

    private Integer cachedCRC;
    private final MuteListCachedDataDao muteListCachedDataDao;
    private volatile MuteListData muteListData;
    private final RequestHandler muteListRequestHandler;
    private final ResultHandler muteListResultHandler;
    private final UserManager userManager;

    static ResultHandler _2D_get0(SLMuteList slmutelist)
    {
        return slmutelist.muteListResultHandler;
    }

    public SLMuteList(SLAgentCircuit slagentcircuit)
    {
        super(slagentcircuit);
        muteListData = new MuteListData();
        cachedCRC = null;
        muteListRequestHandler = new AsyncRequestHandler(agentCircuit, new SimpleRequestHandler() {

            final SLMuteList this$0;

            public void onRequest(SubscriptionSingleKey subscriptionsinglekey)
            {
                if (SLMuteList._2D_get0(SLMuteList.this) != null)
                {
                    SLMuteList._2D_get0(SLMuteList.this).onResultData(SubscriptionSingleKey.Value, getMuteList());
                }
            }

            public volatile void onRequest(Object obj)
            {
                onRequest((SubscriptionSingleKey)obj);
            }

            
            {
                this$0 = SLMuteList.this;
                super();
            }
        });
        userManager = UserManager.getUserManager(slagentcircuit.getAgentUUID());
        if (userManager != null)
        {
            muteListCachedDataDao = userManager.getDaoSession().getMuteListCachedDataDao();
            muteListResultHandler = userManager.muteListPool().attachRequestHandler(muteListRequestHandler);
            return;
        } else
        {
            muteListCachedDataDao = null;
            muteListResultHandler = null;
            return;
        }
    }

    private void RequestMuteList()
    {
        MuteListRequest mutelistrequest = new MuteListRequest();
        mutelistrequest.AgentData_Field.AgentID = circuitInfo.agentID;
        mutelistrequest.AgentData_Field.SessionID = circuitInfo.sessionID;
        com.lumiyaviewer.lumiya.slproto.messages.MuteListRequest.MuteData mutedata = mutelistrequest.MuteData_Field;
        int i;
        if (cachedCRC != null)
        {
            i = cachedCRC.intValue();
        } else
        {
            i = 0;
        }
        mutedata.MuteCRC = i;
        mutelistrequest.isReliable = true;
        SendMessage(mutelistrequest);
        Debug.Printf("MuteList: Requested mute list (CRC %08x)", new Object[] {
            Integer.valueOf(mutelistrequest.MuteData_Field.MuteCRC)
        });
    }

    public void Block(MuteListEntry mutelistentry)
    {
        muteListData = muteListData.Block(mutelistentry);
        Debug.Printf("MuteList: adding entry %s '%s'", new Object[] {
            mutelistentry.uuid.toString(), mutelistentry.name
        });
        UpdateMuteListEntry updatemutelistentry = new UpdateMuteListEntry();
        updatemutelistentry.AgentData_Field.AgentID = circuitInfo.agentID;
        updatemutelistentry.AgentData_Field.SessionID = circuitInfo.sessionID;
        updatemutelistentry.MuteData_Field.MuteID = mutelistentry.uuid;
        updatemutelistentry.MuteData_Field.MuteName = SLMessage.stringToVariableOEM(mutelistentry.name);
        updatemutelistentry.MuteData_Field.MuteType = mutelistentry.type.ordinal();
        updatemutelistentry.MuteData_Field.MuteFlags = mutelistentry.flags;
        updatemutelistentry.isReliable = true;
        SendMessage(updatemutelistentry);
        userManager.muteListPool().requestUpdate(SubscriptionSingleKey.Value);
    }

    public void HandleCircuitReady()
    {
        super.HandleCircuitReady();
        if (muteListCachedDataDao != null)
        {
            LazyList lazylist = muteListCachedDataDao.queryBuilder().listLazy();
            Object obj = lazylist.iterator();
            if (((Iterator) (obj)).hasNext())
            {
                obj = (MuteListCachedData)((Iterator) (obj)).next();
                muteListData = new MuteListData(((MuteListCachedData) (obj)).getData());
                cachedCRC = Integer.valueOf(((MuteListCachedData) (obj)).getCRC());
                userManager.muteListPool().requestUpdate(SubscriptionSingleKey.Value);
            }
            lazylist.close();
            RequestMuteList();
        }
    }

    public void HandleCloseCircuit()
    {
        if (userManager != null)
        {
            userManager.muteListPool().detachRequestHandler(muteListRequestHandler);
        }
        super.HandleCloseCircuit();
    }

    public void HandleMuteListUpdate(MuteListUpdate mutelistupdate)
    {
        mutelistupdate = SLMessage.stringFromVariableOEM(mutelistupdate.MuteData_Field.Filename);
        Debug.Printf("MuteList: fileName = '%s'", new Object[] {
            mutelistupdate
        });
        if (!mutelistupdate.equals(""))
        {
            agentCircuit.getModules().xferManager.RequestXfer(mutelistupdate, ELLPath.LL_PATH_CACHE, true, this, null);
        }
    }

    public void HandleUseCachedMuteList(UseCachedMuteList usecachedmutelist)
    {
        Debug.Printf("MuteList: Using cached mute list.", new Object[0]);
    }

    public void Unblock(MuteListEntry mutelistentry)
    {
        muteListData = muteListData.Unblock(mutelistentry);
        Debug.Printf("MuteList: removing entry %s '%s'", new Object[] {
            mutelistentry.uuid.toString(), mutelistentry.name
        });
        RemoveMuteListEntry removemutelistentry = new RemoveMuteListEntry();
        removemutelistentry.AgentData_Field.AgentID = circuitInfo.agentID;
        removemutelistentry.AgentData_Field.SessionID = circuitInfo.sessionID;
        removemutelistentry.MuteData_Field.MuteID = mutelistentry.uuid;
        removemutelistentry.MuteData_Field.MuteName = SLMessage.stringToVariableOEM(mutelistentry.name);
        removemutelistentry.isReliable = true;
        SendMessage(removemutelistentry);
        userManager.muteListPool().requestUpdate(SubscriptionSingleKey.Value);
    }

    public ImmutableList getMuteList()
    {
        return muteListData.getMuteList();
    }

    public boolean isMuted(UUID uuid, MuteType mutetype)
    {
        if (uuid != null)
        {
            return muteListData.isMuted(uuid, mutetype);
        } else
        {
            return false;
        }
    }

    public boolean isMutedByName(String s)
    {
        return muteListData.isMutedByName(s);
    }

    public void onXferComplete(Object obj, String s, byte abyte0[])
    {
        if (abyte0 != null)
        {
            muteListData = new MuteListData(abyte0);
            if (muteListCachedDataDao != null)
            {
                obj = new CRC32();
                ((CRC32) (obj)).update(abyte0);
                long l = ((CRC32) (obj)).getValue();
                muteListCachedDataDao.deleteAll();
                obj = new MuteListCachedData(null, (int)l, abyte0);
                muteListCachedDataDao.insert(obj);
            }
            userManager.muteListPool().requestUpdate(SubscriptionSingleKey.Value);
        }
    }
}
