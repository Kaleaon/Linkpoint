// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import com.lumiyaviewer.lumiya.slproto.messages.UUIDNameReply;
import com.lumiyaviewer.lumiya.slproto.messages.UUIDNameRequest;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.reqset.RequestListener;
import com.lumiyaviewer.lumiya.utils.reqset.WeakPriorityRequestSet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules:
//            SLModule

public class SLUserNameFetcher extends SLModule
    implements RequestListener
{

    private static final int MAX_BATCH_SIZE = 4;
    private static final long REPLY_TIMEOUT = 10000L;
    private final SLCaps caps;
    private final Condition hasNamesToFetch;
    private boolean isWaitingReply;
    private final Lock lock = new ReentrantLock();
    private volatile boolean threadMustExit;
    private final Runnable threadRunnable = new Runnable() {

        final SLUserNameFetcher this$0;

        public void run()
        {
_L2:
            if (SLUserNameFetcher._2D_get2(SLUserNameFetcher.this))
            {
                break; /* Loop/switch isn't completed */
            }
            while (SLUserNameFetcher._2D_wrap0(SLUserNameFetcher.this)) ;
            SLUserNameFetcher._2D_get1(SLUserNameFetcher.this).lock();
            SLUserNameFetcher._2D_get0(SLUserNameFetcher.this).await();
            SLUserNameFetcher._2D_get1(SLUserNameFetcher.this).unlock();
            if (true) goto _L2; else goto _L1
            Exception exception;
            exception;
            try
            {
                SLUserNameFetcher._2D_get1(SLUserNameFetcher.this).unlock();
                throw exception;
            }
            catch (InterruptedException interruptedexception) { }
_L1:
        }

            
            {
                this$0 = SLUserNameFetcher.this;
                super();
            }
    };
    private final Object udpLock = new Object();
    private final UserManager userManager;
    private final WeakPriorityRequestSet userNameRequests;
    private long waitingReplySince;
    private final Thread workingThread;
    private final LLSDXMLRequest xmlReq;

    static Condition _2D_get0(SLUserNameFetcher slusernamefetcher)
    {
        return slusernamefetcher.hasNamesToFetch;
    }

    static Lock _2D_get1(SLUserNameFetcher slusernamefetcher)
    {
        return slusernamefetcher.lock;
    }

    static boolean _2D_get2(SLUserNameFetcher slusernamefetcher)
    {
        return slusernamefetcher.threadMustExit;
    }

    static boolean _2D_wrap0(SLUserNameFetcher slusernamefetcher)
    {
        return slusernamefetcher.FetchSomeNamesOverHTTP();
    }

    public SLUserNameFetcher(SLAgentCircuit slagentcircuit, SLCaps slcaps)
    {
        super(slagentcircuit);
        hasNamesToFetch = lock.newCondition();
        waitingReplySince = 0L;
        userManager = UserManager.getUserManager(slagentcircuit.circuitInfo.agentID);
        caps = slcaps;
        threadMustExit = false;
        if (slcaps.getCapability(com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.GetDisplayNames) != null)
        {
            xmlReq = new LLSDXMLRequest();
            workingThread = new Thread(threadRunnable, "DisplayNameFetcher");
            workingThread.start();
        } else
        {
            workingThread = null;
            xmlReq = null;
        }
        if (userManager != null)
        {
            userNameRequests = userManager.getUserNameRequests();
            userNameRequests.addListener(this);
            return;
        } else
        {
            userNameRequests = null;
            return;
        }
    }

    private boolean FetchSomeNamesOverHTTP()
    {
        Object obj;
        boolean flag1;
        flag1 = false;
        Object obj1 = getUUIDsToFetch(4);
        if (((List) (obj1)).isEmpty())
        {
            return false;
        }
        obj = (new StringBuilder()).append(caps.getCapability(com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.GetDisplayNames)).append("/").toString();
        obj1 = ((Iterable) (obj1)).iterator();
        boolean flag = true;
        while (((Iterator) (obj1)).hasNext()) 
        {
            UUID uuid1 = (UUID)((Iterator) (obj1)).next();
            if (flag)
            {
                obj = (new StringBuilder()).append(((String) (obj))).append("?").toString();
            } else
            {
                obj = (new StringBuilder()).append(((String) (obj))).append("&").toString();
            }
            obj = (new StringBuilder()).append(((String) (obj))).append("ids=").append(uuid1.toString()).toString();
            flag = false;
        }
        UUID uuid2;
        String s;
        Object obj2;
        int i;
        try
        {
            obj = xmlReq.PerformRequest(((String) (obj)), null);
        }
        // Misplaced declaration of an exception variable
        catch (Object obj)
        {
            ((LLSDXMLException) (obj)).printStackTrace();
            obj = null;
        }
        // Misplaced declaration of an exception variable
        catch (Object obj)
        {
            ((IOException) (obj)).printStackTrace();
            obj = null;
        }
        if (obj == null) goto _L2; else goto _L1
_L1:
        if (!((LLSDNode) (obj)).keyExists("agents")) goto _L4; else goto _L3
_L3:
        obj1 = ((LLSDNode) (obj)).byKey("agents");
        i = 0;
_L5:
        if (i >= ((LLSDNode) (obj1)).getCount())
        {
            break; /* Loop/switch isn't completed */
        }
        obj2 = ((LLSDNode) (obj1)).byIndex(i);
        uuid2 = ((LLSDNode) (obj2)).byKey("id").asUUID();
        s = ((LLSDNode) (obj2)).byKey("display_name").asString();
        obj2 = ((LLSDNode) (obj2)).byKey("username").asString();
        if (userManager != null)
        {
            userManager.updateUserNames(uuid2, ((String) (obj2)), s);
            userNameRequests.completeRequest(uuid2);
        }
        i++;
        if (true) goto _L5; else goto _L4
_L4:
        if (!((LLSDNode) (obj)).keyExists("bad_ids")) goto _L2; else goto _L6
_L6:
        obj = ((LLSDNode) (obj)).byKey("bad_ids");
        int j = ((flag1) ? 1 : 0);
_L7:
        if (j >= ((LLSDNode) (obj)).getCount())
        {
            break; /* Loop/switch isn't completed */
        }
        UUID uuid = UUID.fromString(((LLSDNode) (obj)).byIndex(j).asString());
        if (userManager != null)
        {
            userManager.setUserBadUUID(uuid);
            userNameRequests.completeRequest(uuid);
        }
        j++;
        if (true) goto _L7; else goto _L2
        LLSDException llsdexception;
        llsdexception;
        llsdexception.printStackTrace();
_L2:
        return true;
    }

    private void FetchSomeNamesOverUDP()
    {
        Object obj = getUUIDsToFetch(4);
        if (((List) (obj)).isEmpty())
        {
            isWaitingReply = false;
            return;
        }
        UUIDNameRequest uuidnamerequest = new UUIDNameRequest();
        com.lumiyaviewer.lumiya.slproto.messages.UUIDNameRequest.UUIDNameBlock uuidnameblock;
        for (obj = ((Iterable) (obj)).iterator(); ((Iterator) (obj)).hasNext(); uuidnamerequest.UUIDNameBlock_Fields.add(uuidnameblock))
        {
            UUID uuid = (UUID)((Iterator) (obj)).next();
            uuidnameblock = new com.lumiyaviewer.lumiya.slproto.messages.UUIDNameRequest.UUIDNameBlock();
            uuidnameblock.ID = uuid;
        }

        isWaitingReply = true;
        waitingReplySince = System.currentTimeMillis();
        uuidnamerequest.isReliable = true;
        SendMessage(uuidnamerequest);
    }

    private List getUUIDsToFetch(int i)
    {
        ArrayList arraylist = new ArrayList(i);
        if (userNameRequests != null)
        {
            do
            {
                if (arraylist.size() >= i)
                {
                    break;
                }
                UUID uuid = (UUID)userNameRequests.getRequest();
                if (uuid == null)
                {
                    break;
                }
                arraylist.add(uuid);
            } while (true);
        }
        return arraylist;
    }

    public void HandleCloseCircuit()
    {
        threadMustExit = true;
        if (xmlReq != null)
        {
            xmlReq.InterruptRequest();
        }
        if (workingThread != null)
        {
            workingThread.interrupt();
        }
        if (userNameRequests != null)
        {
            userNameRequests.removeListener(this);
        }
    }

    public void HandleUUIDNameReply(UUIDNameReply uuidnamereply)
    {
        this;
        JVM INSTR monitorenter ;
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
            if (userManager != null)
            {
                userManager.updateUserNames(uuid, ((String) (obj)), ((String) (obj)));
                userNameRequests.completeRequest(uuid);
            }
        } while (true);
        break MISSING_BLOCK_LABEL_106;
        uuidnamereply;
        throw uuidnamereply;
        uuidnamereply = ((UUIDNameReply) (udpLock));
        uuidnamereply;
        JVM INSTR monitorenter ;
        isWaitingReply = false;
        FetchSomeNamesOverUDP();
        uuidnamereply;
        JVM INSTR monitorexit ;
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        uuidnamereply;
        JVM INSTR monitorexit ;
        throw exception;
    }

    public void onNewRequest()
    {
        if (workingThread == null)
        {
            break MISSING_BLOCK_LABEL_47;
        }
        lock.lock();
        hasNamesToFetch.signal();
        lock.unlock();
        return;
        Exception exception;
        exception;
        lock.unlock();
        throw exception;
        Object obj = udpLock;
        obj;
        JVM INSTR monitorenter ;
        if (!isWaitingReply || System.currentTimeMillis() > waitingReplySince + 10000L)
        {
            FetchSomeNamesOverUDP();
        }
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception1;
        exception1;
        throw exception1;
    }
}
