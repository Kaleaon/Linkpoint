package com.linkpoint.slproto.caps

import com.linkpoint.Debug
import com.linkpoint.slproto.https.LLSDXMLRequest
import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.llsd.LLSDXMLException
import com.linkpoint.slproto.llsd.types.LLSDBoolean
import com.linkpoint.slproto.llsd.types.LLSDInt
import com.linkpoint.slproto.llsd.types.LLSDMap
import com.linkpoint.slproto.llsd.types.LLSDUndefined
import java.io.FileNotFoundException
import java.io.IOException
import java.util.LinkedList
import java.util.List
import java.util.concurrent.atomic.AtomicBoolean

class SLCapEventQueue : Runnable {
    private String capURL
    private Boolean done = false
    private ICapsEventHandler eventHandler = null
    private Int lastEventID = 0
    private List<CapsEvent> nextQueue = LinkedList()
    private Boolean threadMustExit = false
    private AtomicBoolean willExitGracefully = AtomicBoolean(false)
    private Thread workingThread
    private LLSDXMLRequest xmlReq = LLSDXMLRequest()

    @JvmStatic
    class CapsEvent {
        public LLSDNode eventBody
        public CapsEventType eventType

        public CapsEvent(String str, LLSDNode lLSDNode) {
            try {
                this.eventType = CapsEventType.valueOf(str)
            } catch (IllegalArgumentException e) {
                this.eventType = CapsEventType.UnknownCapsEvent
            }
            this.eventBody = lLSDNode
        }
    }

    enum class CapsEventType {
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

    interface ICapsEventHandler {
        Unit OnCapsEvent(CapsEvent capsEvent)
    }

    public SLCapEventQueue(String str, ICapsEventHandler iCapsEventHandler) {
        this.capURL = str
        this.eventHandler = iCapsEventHandler
        this.workingThread = Thread(this)
        this.workingThread.start()
    }

    fun run() {
        Debug.Log("CapEventQueue: working thread starting with capURL = " + this.capURL)
        Boolean z2 = false
        while (true) {
            if (this.threadMustExit) {
                break
            }
            LLSDMap.LLSDMapEntry[] lLSDMapEntryArr = LLSDMap.LLSDMapEntry[2]
            lLSDMapEntryArr[0] = LLSDMap.LLSDMapEntry("ack", this.lastEventID != 0 ? LLSDInt(this.lastEventID) : LLSDUndefined())
            lLSDMapEntryArr[1] = LLSDMap.LLSDMapEntry("done", LLSDBoolean(this.done))
            try {
                LLSDNode PerformRequest = this.xmlReq.PerformRequest(this.capURL, LLSDMap(lLSDMapEntryArr))
                if (this.done) {
                    Debug.Log("CapEventQueue: Done sent and confirmed, exiting gracefully.")
                    break
                }
                try {
                    this.lastEventID = PerformRequest.byKey("id").asInt()
                    Debug.Log("CapEventQueue: lastEventID = " + this.lastEventID)
                    Int count = PerformRequest.byKey("events").getCount()
                    for (Int i = 0; i < count; i++) {
                        LLSDNode byIndex = PerformRequest.byKey("events").byIndex(i)
                        String asString = byIndex.byKey("message").asString()
                        LLSDNode byKey = byIndex.byKey("body")
                        Debug.Log("CapEventQueue: event name = " + asString)
                        if (asString.equalsIgnoreCase("TeleportFinish")) {
                            this.done = true
                            this.willExitGracefully.set(true)
                        }
                        this.nextQueue.add(CapsEvent(asString, byKey))
                    }
                } catch (LLSDException e) {
                    Debug.Printf("CapEventQueue: failed to extract id. event was: %s" + PerformRequest.serializeToXML(), Object[0])
                    Debug.Warning(e)
                }
                if (!this.threadMustExit) {
                    while (true) {
                        z = z2
                        if (this.nextQueue.size() <= 0) {
                            break
                        }
                        CapsEvent remove = this.nextQueue.remove(0)
                        if (z || this.eventHandler == null) {
                            z2 = z
                        } else {
                            if (remove.eventType == CapsEventType.TeleportFinish) {
                                z = true
                            }
                            this.eventHandler.OnCapsEvent(remove)
                            z2 = z
                        }
                    }
                    if (!z) {
                        try {
                            Thread.sleep(2500)
                        } catch (InterruptedException e2) {
                            Debug.Log("Interrupted")
                            e2.printStackTrace()
                            z2 = z
                        }
                    }
                    z2 = z
                }
            } catch (FileNotFoundException e3) {
                Debug.Printf("CapEventQueue: Got file not found expection, cap queue closed?", Object[0])
            } catch (LLSDXMLException e4) {
                Debug.Warning(e4)
            } catch (IOException e5) {
                Debug.Warning(e5)
            } catch (NullPointerException e6) {
                Debug.Warning(e6)
            }
        }
        Debug.Log("CapEventQueue: event queue thread exiting")
    }

    public synchronized Unit stopQueue() {
        if (!this.willExitGracefully.get()) {
            this.threadMustExit = true
            if (this.workingThread != null) {
                this.xmlReq.InterruptRequest()
                this.workingThread.interrupt()
                this.workingThread = null
            }
        }
    }
}
