// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.caps;

import java.io.FileNotFoundException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException;
import java.io.IOException;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt;
import com.lumiyaviewer.lumiya.Debug;
import java.util.LinkedList;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.List;

public class SLCapEventQueue implements Runnable
{
    private String capURL;
    private boolean done;
    private ICapsEventHandler eventHandler;
    private int lastEventID;
    private List<CapsEvent> nextQueue;
    private boolean threadMustExit;
    private AtomicBoolean willExitGracefully;
    private Thread workingThread;
    private LLSDXMLRequest xmlReq;
    
    public SLCapEventQueue(final String capURL, final ICapsEventHandler eventHandler) {
        this.eventHandler = null;
        this.lastEventID = 0;
        this.threadMustExit = false;
        this.willExitGracefully = new AtomicBoolean(false);
        this.xmlReq = new LLSDXMLRequest();
        this.nextQueue = new LinkedList<CapsEvent>();
        this.done = false;
        this.capURL = capURL;
        this.eventHandler = eventHandler;
        (this.workingThread = new Thread(this)).start();
    }
    
    @Override
    public void run() {
        Debug.Log("CapEventQueue: working thread starting with capURL = " + this.capURL);
        int n = 0;
    Label_0027:
        while (true) {
            Label_0126: {
                if (this.threadMustExit) {
                    break Label_0126;
                }
                Label_0132: {
                    if (this.lastEventID == 0) {
                        break Label_0132;
                    }
                    LLSDNode llsdNode = new LLSDInt(this.lastEventID);
                    LLSDMap llsdMap;
                    LLSDNode performRequest;
                    LLSDNode byIndex;
                    String string;
                    LLSDNode byKey;
                    CapsEvent capsEvent;
                    Label_0360_Outer:Label_0367_Outer:
                    while (true) {
                        llsdMap = new LLSDMap(new LLSDMap.LLSDMapEntry[] { new LLSDMap.LLSDMapEntry("ack", llsdNode), new LLSDMap.LLSDMapEntry("done", new LLSDBoolean(this.done)) });
                        while (true) {
                            Label_0492: {
                                while (true) {
                                    try {
                                        performRequest = this.xmlReq.PerformRequest(this.capURL, llsdMap);
                                        if (this.done) {
                                            Debug.Log("CapEventQueue: Done sent and confirmed, exiting gracefully.");
                                            Debug.Log("CapEventQueue: event queue thread exiting");
                                            return;
                                        }
                                        try {
                                            this.lastEventID = performRequest.byKey("id").asInt();
                                            Debug.Log("CapEventQueue: new lastEventID = " + this.lastEventID);
                                            for (int count = performRequest.byKey("events").getCount(), i = 0; i < count; ++i) {
                                                byIndex = performRequest.byKey("events").byIndex(i);
                                                string = byIndex.byKey("message").asString();
                                                byKey = byIndex.byKey("body");
                                                Debug.Log("CapEventQueue: event name = " + string);
                                                if (string.equalsIgnoreCase("TeleportFinish")) {
                                                    this.done = true;
                                                    this.willExitGracefully.set(true);
                                                }
                                                this.nextQueue.add(new CapsEvent(string, byKey));
                                            }
                                        }
                                        catch (final LLSDException ex) {
                                            Debug.Printf("CapEventQueue: failed to extract id. event was: %s" + performRequest.serializeToXML(), new Object[0]);
                                            Debug.Warning(ex);
                                        }
                                        if (!this.threadMustExit) {
                                            while (this.nextQueue.size() > 0) {
                                                capsEvent = this.nextQueue.remove(0);
                                                if (n != 0 || this.eventHandler == null) {
                                                    break Label_0492;
                                                }
                                                if (capsEvent.eventType == CapsEventType.TeleportFinish) {
                                                    n = 1;
                                                }
                                                this.eventHandler.OnCapsEvent(capsEvent);
                                            }
                                            break;
                                        }
                                        continue Label_0027;
                                        llsdNode = new LLSDUndefined();
                                        continue Label_0360_Outer;
                                    }
                                    catch (final NullPointerException ex2) {
                                        Debug.Warning(ex2);
                                        continue Label_0367_Outer;
                                    }
                                    catch (final IOException ex3) {
                                        Debug.Warning(ex3);
                                        continue Label_0367_Outer;
                                    }
                                    catch (final LLSDXMLException ex4) {
                                        Debug.Warning(ex4);
                                        continue Label_0367_Outer;
                                    }
                                    catch (final FileNotFoundException ex5) {
                                        Debug.Printf("CapEventQueue: Got file not found expection, cap queue closed?", new Object[0]);
                                        continue Label_0367_Outer;
                                    }
                                    break;
                                }
                                break;
                            }
                            continue;
                        }
                    }
                }
            }
            Label_0476: {
                if (n != 0) {
                    break Label_0476;
                }
                try {
                    Thread.sleep(2500L);
                }
                catch (final InterruptedException ex6) {
                    Debug.Log("Interrupted");
                    ex6.printStackTrace();
                }
            }
        }
    }
    
    public void stopQueue() {
        synchronized (this) {
            if (!this.willExitGracefully.get()) {
                this.threadMustExit = true;
                if (this.workingThread != null) {
                    this.xmlReq.InterruptRequest();
                    this.workingThread.interrupt();
                    this.workingThread = null;
                }
            }
        }
    }
    
    public static class CapsEvent
    {
        public LLSDNode eventBody;
        public CapsEventType eventType;
        
        public CapsEvent(final String s, final LLSDNode eventBody) {
            while (true) {
                try {
                    this.eventType = CapsEventType.valueOf(s);
                    this.eventBody = eventBody;
                }
                catch (final IllegalArgumentException ex) {
                    this.eventType = CapsEventType.UnknownCapsEvent;
                    continue;
                }
                break;
            }
        }
    }
    
    public enum CapsEventType
    {
        AgentGroupDataUpdate("AgentGroupDataUpdate", 0), 
        AvatarGroupsReply("AvatarGroupsReply", 1), 
        BulkUpdateInventory("BulkUpdateInventory", 7), 
        ChatterBoxInvitation("ChatterBoxInvitation", 2), 
        ChatterBoxSessionStartReply("ChatterBoxSessionStartReply", 3), 
        EstablishAgentCommunication("EstablishAgentCommunication", 8), 
        ParcelProperties("ParcelProperties", 4), 
        TeleportFailed("TeleportFailed", 5), 
        TeleportFinish("TeleportFinish", 6), 
        UnknownCapsEvent("UnknownCapsEvent", 9);
        
        private CapsEventType(final String name, final int ordinal) {
        }
    }
    
    public interface ICapsEventHandler
    {
        void OnCapsEvent(final CapsEvent p0);
    }
}
