// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.dispnames;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.UserName;
import com.lumiyaviewer.lumiya.react.AsyncLimitsRequestHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.RequestQueue;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.messages.UUIDNameReply;
import com.lumiyaviewer.lumiya.slproto.messages.UUIDNameRequest;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class SLDisplayNameFetcher extends SLModule
{

    private static final int MAX_BATCH_SIZE = 4;
    private final String capsURL = null;
    private final Runnable httpThreadRunnable = new Runnable() {

        final SLDisplayNameFetcher this$0;

        public void run()
        {
            RequestQueue requestqueue;
            Object obj2;
            requestqueue = SLDisplayNameFetcher._2D_get2(SLDisplayNameFetcher.this).getUserNameRequestQueue();
            obj2 = new HashSet();
_L3:
            if (SLDisplayNameFetcher._2D_get1(SLDisplayNameFetcher.this).get())
            {
                break MISSING_BLOCK_LABEL_94;
            }
            ((Set) (obj2)).clear();
            ((Set) (obj2)).add((UUID)requestqueue.waitForRequest());
_L1:
            UUID uuid;
            if (((Set) (obj2)).size() >= 4)
            {
                break MISSING_BLOCK_LABEL_128;
            }
            uuid = (UUID)requestqueue.getNextRequest();
            if (uuid == null)
            {
                break MISSING_BLOCK_LABEL_128;
            }
            ((Set) (obj2)).add(uuid);
              goto _L1
            InterruptedException interruptedexception;
            interruptedexception;
            Debug.Warning(interruptedexception);
            for (obj2 = ((Iterable) (obj2)).iterator(); ((Iterator) (obj2)).hasNext(); requestqueue.returnRequest((UUID)((Iterator) (obj2)).next())) { }
            break; /* Loop/switch isn't completed */
            SLDisplayNameFetcher._2D_wrap0(SLDisplayNameFetcher.this, ((Set) (obj2)), requestqueue);
            for (Iterator iterator = ((Iterable) (obj2)).iterator(); iterator.hasNext(); requestqueue.returnRequest((UUID)iterator.next())) { }
            ((Set) (obj2)).clear();
            if (true) goto _L3; else goto _L2
_L2:
        }

            
            {
                this$0 = SLDisplayNameFetcher.this;
                super();
            }
    };
    private final RequestHandler requestHandler;
    private final RequestQueue requestQueue;
    private final ResultHandler resultHandler;
    private final AtomicBoolean threadMustExit = new AtomicBoolean(false);
    private final boolean useDisplayNames = false;
    private final UserManager userManager;
    private final Thread workingThread = null;
    private final LLSDXMLRequest xmlReq = null;

    static RequestQueue _2D_get0(SLDisplayNameFetcher sldisplaynamefetcher)
    {
        return sldisplaynamefetcher.requestQueue;
    }

    static AtomicBoolean _2D_get1(SLDisplayNameFetcher sldisplaynamefetcher)
    {
        return sldisplaynamefetcher.threadMustExit;
    }

    static UserManager _2D_get2(SLDisplayNameFetcher sldisplaynamefetcher)
    {
        return sldisplaynamefetcher.userManager;
    }

    static void _2D_wrap0(SLDisplayNameFetcher sldisplaynamefetcher, Set set, RequestQueue requestqueue)
    {
        sldisplaynamefetcher.requestNamesHttp(set, requestqueue);
    }

    public SLDisplayNameFetcher(SLAgentCircuit slagentcircuit, SLCaps slcaps)
    {
        Object obj1 = null;
        Object obj = null;
        super(slagentcircuit);
        requestHandler = new AsyncLimitsRequestHandler(agentCircuit, new SimpleRequestHandler() {

            final SLDisplayNameFetcher this$0;

            public volatile void onRequest(Object obj2)
            {
                onRequest((UUID)obj2);
            }

            public void onRequest(UUID uuid)
            {
                UUIDNameRequest uuidnamerequest = new UUIDNameRequest();
                com.lumiyaviewer.lumiya.slproto.messages.UUIDNameRequest.UUIDNameBlock uuidnameblock = new com.lumiyaviewer.lumiya.slproto.messages.UUIDNameRequest.UUIDNameBlock();
                uuidnameblock.ID = uuid;
                uuidnamerequest.UUIDNameBlock_Fields.add(uuidnameblock);
                com.lumiyaviewer.lumiya.slproto.messages.UUIDNameRequest.UUIDNameBlock uuidnameblock1;
                for (; uuidnamerequest.UUIDNameBlock_Fields.size() < 4 && SLDisplayNameFetcher._2D_get0(SLDisplayNameFetcher.this) != null && (UUID)SLDisplayNameFetcher._2D_get0(SLDisplayNameFetcher.this).getNextRequest() != null; uuidnamerequest.UUIDNameBlock_Fields.add(uuidnameblock1))
                {
                    uuidnameblock1 = new com.lumiyaviewer.lumiya.slproto.messages.UUIDNameRequest.UUIDNameBlock();
                    uuidnameblock1.ID = uuid;
                }

                uuidnamerequest.isReliable = true;
                SendMessage(uuidnamerequest);
            }

            
            {
                this$0 = SLDisplayNameFetcher.this;
                super();
            }
        }, false, 3, 15000L);
        userManager = UserManager.getUserManager(slagentcircuit.circuitInfo.agentID);
        if (userManager != null)
        {
            slagentcircuit = userManager.getUserNameRequestQueue();
        } else
        {
            slagentcircuit = null;
        }
        requestQueue = slagentcircuit;
        if (slcaps.getCapability(com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.GetDisplayNames) != null)
        {
            capsURL = slcaps.getCapability(com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.GetDisplayNames);
            useDisplayNames = true;
            slagentcircuit = obj;
            if (requestQueue != null)
            {
                slagentcircuit = requestQueue.getResultHandler();
            }
            resultHandler = slagentcircuit;
            xmlReq = new LLSDXMLRequest();
            workingThread = new Thread(httpThreadRunnable, "DisplayNameFetcher");
            workingThread.start();
            return;
        }
        slagentcircuit = obj1;
        if (requestQueue != null)
        {
            slagentcircuit = requestQueue.attachRequestHandler(requestHandler);
        }
        resultHandler = slagentcircuit;
    }

    private void requestNamesHttp(Set set, RequestQueue requestqueue)
    {
        boolean flag1;
        flag1 = false;
        requestqueue = (new StringBuilder(capsURL)).append('/');
        Iterator iterator = set.iterator();
        boolean flag = true;
        while (iterator.hasNext()) 
        {
            UUID uuid1 = (UUID)iterator.next();
            Debug.Printf("UserName: Requesting name for %s over HTTP", new Object[] {
                uuid1
            });
            if (flag)
            {
                requestqueue.append('?');
            } else
            {
                requestqueue.append('&');
            }
            requestqueue.append("ids=").append(uuid1.toString());
            flag = false;
        }
        requestqueue = xmlReq.PerformRequest(requestqueue.toString(), null);
        if (requestqueue == null) goto _L2; else goto _L1
_L1:
        if (!requestqueue.keyExists("agents")) goto _L4; else goto _L3
_L3:
        LLSDNode llsdnode = requestqueue.byKey("agents");
        int i = 0;
_L5:
        if (i >= llsdnode.getCount())
        {
            break; /* Loop/switch isn't completed */
        }
        Object obj = llsdnode.byIndex(i);
        UUID uuid2 = ((LLSDNode) (obj)).byKey("id").asUUID();
        String s = ((LLSDNode) (obj)).byKey("display_name").asString();
        obj = new UserName(uuid2, ((LLSDNode) (obj)).byKey("username").asString(), s, false);
        if (resultHandler != null)
        {
            resultHandler.onResultData(uuid2, obj);
        }
        set.remove(uuid2);
        i++;
        if (true) goto _L5; else goto _L4
_L4:
        if (!requestqueue.keyExists("bad_ids")) goto _L2; else goto _L6
_L6:
        requestqueue = requestqueue.byKey("bad_ids");
        i = ((flag1) ? 1 : 0);
_L7:
        if (i >= requestqueue.getCount())
        {
            break; /* Loop/switch isn't completed */
        }
        UUID uuid = UUID.fromString(requestqueue.byIndex(i).asString());
        UserName username = new UserName(uuid, null, null, true);
        if (resultHandler != null)
        {
            resultHandler.onResultData(uuid, username);
        }
        set.remove(uuid);
        i++;
        if (true) goto _L7; else goto _L2
        set;
        set.printStackTrace();
_L2:
    }

    public void HandleCloseCircuit()
    {
        threadMustExit.set(true);
        if (xmlReq != null)
        {
            xmlReq.InterruptRequest();
        }
        if (workingThread != null)
        {
            workingThread.interrupt();
        }
        if (requestQueue != null)
        {
            requestQueue.detachRequestHandler(requestHandler);
        }
    }

    public void HandleUUIDNameReply(UUIDNameReply uuidnamereply)
    {
        uuidnamereply = uuidnamereply.UUIDNameBlock_Fields.iterator();
        do
        {
            if (!uuidnamereply.hasNext())
            {
                break;
            }
            Object obj = (com.lumiyaviewer.lumiya.slproto.messages.UUIDNameReply.UUIDNameBlock)uuidnamereply.next();
            UUID uuid = ((com.lumiyaviewer.lumiya.slproto.messages.UUIDNameReply.UUIDNameBlock) (obj)).ID;
            obj = (new StringBuilder()).append(SLMessage.stringFromVariableOEM(((com.lumiyaviewer.lumiya.slproto.messages.UUIDNameReply.UUIDNameBlock) (obj)).FirstName)).append(" ").append(SLMessage.stringFromVariableOEM(((com.lumiyaviewer.lumiya.slproto.messages.UUIDNameReply.UUIDNameBlock) (obj)).LastName)).toString();
            obj = new UserName(uuid, ((String) (obj)), ((String) (obj)), false);
            if (resultHandler != null)
            {
                resultHandler.onResultData(uuid, obj);
            }
        } while (true);
    }
}
