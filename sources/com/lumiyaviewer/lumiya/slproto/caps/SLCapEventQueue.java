// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.caps;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SLCapEventQueue
    implements Runnable
{
    public static class CapsEvent
    {

        public LLSDNode eventBody;
        public CapsEventType eventType;

        public CapsEvent(String s, LLSDNode llsdnode)
        {
            try
            {
                eventType = CapsEventType.valueOf(s);
            }
            // Misplaced declaration of an exception variable
            catch (String s)
            {
                eventType = CapsEventType.UnknownCapsEvent;
            }
            eventBody = llsdnode;
        }
    }

    public static final class CapsEventType extends Enum
    {

        private static final CapsEventType $VALUES[];
        public static final CapsEventType AgentGroupDataUpdate;
        public static final CapsEventType AvatarGroupsReply;
        public static final CapsEventType BulkUpdateInventory;
        public static final CapsEventType ChatterBoxInvitation;
        public static final CapsEventType ChatterBoxSessionStartReply;
        public static final CapsEventType EstablishAgentCommunication;
        public static final CapsEventType ParcelProperties;
        public static final CapsEventType TeleportFailed;
        public static final CapsEventType TeleportFinish;
        public static final CapsEventType UnknownCapsEvent;

        public static CapsEventType valueOf(String s)
        {
            return (CapsEventType)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/caps/SLCapEventQueue$CapsEventType, s);
        }

        public static CapsEventType[] values()
        {
            return $VALUES;
        }

        static 
        {
            AgentGroupDataUpdate = new CapsEventType("AgentGroupDataUpdate", 0);
            AvatarGroupsReply = new CapsEventType("AvatarGroupsReply", 1);
            ChatterBoxInvitation = new CapsEventType("ChatterBoxInvitation", 2);
            ChatterBoxSessionStartReply = new CapsEventType("ChatterBoxSessionStartReply", 3);
            ParcelProperties = new CapsEventType("ParcelProperties", 4);
            TeleportFailed = new CapsEventType("TeleportFailed", 5);
            TeleportFinish = new CapsEventType("TeleportFinish", 6);
            BulkUpdateInventory = new CapsEventType("BulkUpdateInventory", 7);
            EstablishAgentCommunication = new CapsEventType("EstablishAgentCommunication", 8);
            UnknownCapsEvent = new CapsEventType("UnknownCapsEvent", 9);
            $VALUES = (new CapsEventType[] {
                AgentGroupDataUpdate, AvatarGroupsReply, ChatterBoxInvitation, ChatterBoxSessionStartReply, ParcelProperties, TeleportFailed, TeleportFinish, BulkUpdateInventory, EstablishAgentCommunication, UnknownCapsEvent
            });
        }

        private CapsEventType(String s, int i)
        {
            super(s, i);
        }
    }

    public static interface ICapsEventHandler
    {

        public abstract void OnCapsEvent(CapsEvent capsevent);
    }


    private String capURL;
    private boolean done;
    private ICapsEventHandler eventHandler;
    private int lastEventID;
    private List nextQueue;
    private boolean threadMustExit;
    private AtomicBoolean willExitGracefully;
    private Thread workingThread;
    private LLSDXMLRequest xmlReq;

    public SLCapEventQueue(String s, ICapsEventHandler icapseventhandler)
    {
        eventHandler = null;
        lastEventID = 0;
        threadMustExit = false;
        willExitGracefully = new AtomicBoolean(false);
        xmlReq = new LLSDXMLRequest();
        nextQueue = new LinkedList();
        done = false;
        capURL = s;
        eventHandler = icapseventhandler;
        workingThread = new Thread(this);
        workingThread.start();
    }

    public void run()
    {
        boolean flag;
        Debug.Log((new StringBuilder()).append("CapEventQueue: working thread starting with capURL = ").append(capURL).toString());
        flag = false;
_L4:
        Object obj;
        if (threadMustExit)
        {
            break MISSING_BLOCK_LABEL_127;
        }
        if (lastEventID != 0)
        {
            obj = new LLSDInt(lastEventID);
        } else
        {
            obj = new LLSDUndefined();
        }
        obj = new LLSDMap(new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry[] {
            new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("ack", ((LLSDNode) (obj))), new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("done", new LLSDBoolean(done))
        });
        obj = xmlReq.PerformRequest(capURL, ((LLSDNode) (obj)));
        if (!done)
        {
            break MISSING_BLOCK_LABEL_144;
        }
        Debug.Log("CapEventQueue: Done sent and confirmed, exiting gracefully.");
        Debug.Log("CapEventQueue: event queue thread exiting");
        return;
        int j;
        lastEventID = ((LLSDNode) (obj)).byKey("id").asInt();
        Debug.Log((new StringBuilder()).append("CapEventQueue: new lastEventID = ").append(lastEventID).toString());
        j = ((LLSDNode) (obj)).byKey("events").getCount();
        int i = 0;
_L2:
        if (i >= j)
        {
            break MISSING_BLOCK_LABEL_338;
        }
        LLSDNode llsdnode = ((LLSDNode) (obj)).byKey("events").byIndex(i);
        String s = llsdnode.byKey("message").asString();
        llsdnode = llsdnode.byKey("body");
        Debug.Log((new StringBuilder()).append("CapEventQueue: event name = ").append(s).toString());
        if (s.equalsIgnoreCase("TeleportFinish"))
        {
            done = true;
            willExitGracefully.set(true);
        }
        nextQueue.add(new CapsEvent(s, llsdnode));
        i++;
        if (true) goto _L2; else goto _L1
_L1:
        LLSDException llsdexception;
        llsdexception;
        try
        {
            Debug.Printf((new StringBuilder()).append("CapEventQueue: failed to extract id. event was: %s").append(((LLSDNode) (obj)).serializeToXML()).toString(), new Object[0]);
            Debug.Warning(llsdexception);
        }
        catch (FileNotFoundException filenotfoundexception)
        {
            Debug.Printf("CapEventQueue: Got file not found expection, cap queue closed?", new Object[0]);
        }
        catch (LLSDXMLException llsdxmlexception)
        {
            Debug.Warning(llsdxmlexception);
        }
        catch (IOException ioexception)
        {
            Debug.Warning(ioexception);
        }
        catch (NullPointerException nullpointerexception)
        {
            Debug.Warning(nullpointerexception);
        }
        if (!threadMustExit)
        {
            do
            {
                if (nextQueue.size() <= 0)
                {
                    break;
                }
                CapsEvent capsevent = (CapsEvent)nextQueue.remove(0);
                if (!flag && eventHandler != null)
                {
                    if (capsevent.eventType == CapsEventType.TeleportFinish)
                    {
                        flag = true;
                    }
                    eventHandler.OnCapsEvent(capsevent);
                }
            } while (true);
            if (!flag)
            {
                try
                {
                    Thread.sleep(2500L);
                }
                catch (InterruptedException interruptedexception)
                {
                    Debug.Log("Interrupted");
                    interruptedexception.printStackTrace();
                }
            }
        }
        if (true) goto _L4; else goto _L3
_L3:
    }

    public void stopQueue()
    {
        this;
        JVM INSTR monitorenter ;
        if (!willExitGracefully.get())
        {
            threadMustExit = true;
            if (workingThread != null)
            {
                xmlReq.InterruptRequest();
                workingThread.interrupt();
                workingThread = null;
            }
        }
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }
}
