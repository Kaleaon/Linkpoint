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

public class SLCapEventQueue implements Runnable {
    private String capURL;
    private boolean done = false;
    private ICapsEventHandler eventHandler = null;
    private int lastEventID = 0;
    private List<CapsEvent> nextQueue = new LinkedList();
    private boolean threadMustExit = false;
    private AtomicBoolean willExitGracefully = new AtomicBoolean(false);
    private Thread workingThread;
    private LLSDXMLRequest xmlReq = new LLSDXMLRequest();

    public static class CapsEvent {
        public LLSDNode eventBody;
        public CapsEventType eventType;

        public CapsEvent(String str, LLSDNode lLSDNode) {
            try {
                this.eventType = CapsEventType.valueOf(str);
            } catch (IllegalArgumentException e) {
                this.eventType = CapsEventType.UnknownCapsEvent;
            }
            this.eventBody = lLSDNode;
        }
    }

    public enum CapsEventType {
        AgentGroupDataUpdate,
        AvatarGroupsReply,
        ChatterBoxInvitation,
        ChatterBoxSessionStartReply,
        ParcelProperties,
        TeleportFailed,
        TeleportFinish,
        BulkUpdateInventory,
        EstablishAgentCommunication,
        UnknownCapsEvent
    }

    public interface ICapsEventHandler {
        void OnCapsEvent(CapsEvent capsEvent);
    }

    public SLCapEventQueue(String str, ICapsEventHandler iCapsEventHandler) {
        this.capURL = str;
        this.eventHandler = iCapsEventHandler;
        this.workingThread = new Thread(this);
        this.workingThread.start();
    }

    public void run() {
        Debug.Log("CapEventQueue: working thread starting with capURL = " + this.capURL);
        boolean z2 = false;
        while (true) {
            if (this.threadMustExit) {
                break;
            }
            LLSDMap.LLSDMapEntry[] lLSDMapEntryArr = new LLSDMap.LLSDMapEntry[2];
            lLSDMapEntryArr[0] = new LLSDMap.LLSDMapEntry("ack", this.lastEventID != 0 ? new LLSDInt(this.lastEventID) : new LLSDUndefined());
            lLSDMapEntryArr[1] = new LLSDMap.LLSDMapEntry("done", new LLSDBoolean(this.done));
            try {
                LLSDNode PerformRequest = this.xmlReq.PerformRequest(this.capURL, new LLSDMap(lLSDMapEntryArr));
                if (this.done) {
                    Debug.Log("CapEventQueue: Done sent and confirmed, exiting gracefully.");
                    break;
                }
                try {
                    this.lastEventID = PerformRequest.byKey("id").asInt();
                    Debug.Log("CapEventQueue: new lastEventID = " + this.lastEventID);
                    int count = PerformRequest.byKey("events").getCount();
                    for (int i = 0; i < count; i++) {
                        LLSDNode byIndex = PerformRequest.byKey("events").byIndex(i);
                        String asString = byIndex.byKey("message").asString();
                        LLSDNode byKey = byIndex.byKey("body");
                        Debug.Log("CapEventQueue: event name = " + asString);
                        if (asString.equalsIgnoreCase("TeleportFinish")) {
                            this.done = true;
                            this.willExitGracefully.set(true);
                        }
                        this.nextQueue.add(new CapsEvent(asString, byKey));
                    }
                } catch (LLSDException e) {
                    Debug.Printf("CapEventQueue: failed to extract id. event was: %s" + PerformRequest.serializeToXML(), new Object[0]);
                    Debug.Warning(e);
                }
                if (!this.threadMustExit) {
                    while (true) {
                        z = z2;
                        if (this.nextQueue.size() <= 0) {
                            break;
                        }
                        CapsEvent remove = this.nextQueue.remove(0);
                        if (z || this.eventHandler == null) {
                            z2 = z;
                        } else {
                            if (remove.eventType == CapsEventType.TeleportFinish) {
                                z = true;
                            }
                            this.eventHandler.OnCapsEvent(remove);
                            z2 = z;
                        }
                    }
                    if (!z) {
                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e2) {
                            Debug.Log("Interrupted");
                            e2.printStackTrace();
                            z2 = z;
                        }
                    }
                    z2 = z;
                }
            } catch (FileNotFoundException e3) {
                Debug.Printf("CapEventQueue: Got file not found expection, cap queue closed?", new Object[0]);
            } catch (LLSDXMLException e4) {
                Debug.Warning(e4);
            } catch (IOException e5) {
                Debug.Warning(e5);
            } catch (NullPointerException e6) {
                Debug.Warning(e6);
            }
        }
        Debug.Log("CapEventQueue: event queue thread exiting");
    }

    public synchronized void stopQueue() {
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
