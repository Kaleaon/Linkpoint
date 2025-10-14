package com.lumiyaviewer.lumiya.slproto.modules

import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDXMLException
import com.lumiyaviewer.lumiya.slproto.messages.UUIDNameReply
import com.lumiyaviewer.lumiya.slproto.messages.UUIDNameRequest
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.utils.reqset.RequestListener
import com.lumiyaviewer.lumiya.utils.reqset.WeakPriorityRequestSet
import java.io.IOException
import java.util.ArrayList
import java.util.Iterator
import java.util.List
import java.util.UUID
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class SLUserNameFetcher : SLModule : RequestListener {
    private Int MAX_BATCH_SIZE = 4
    private Long REPLY_TIMEOUT = 10000
    private SLCaps caps
    /* access modifiers changed from: private */
    Condition hasNamesToFetch = this.lock.newCondition()
    private Boolean isWaitingReply
    /* access modifiers changed from: private */
    Lock lock = ReentrantLock()
    /* access modifiers changed from: private */
    volatile Boolean threadMustExit
    private Runnable threadRunnable = Runnable() {
        /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        Unit run() {
            /*
                r2 = this
            L_0x0000:
                com.lumiyaviewer.lumiya.slproto.modules.SLUserNameFetcher r0 = com.lumiyaviewer.lumiya.slproto.modules.SLUserNameFetcher.this
                Boolean r0 = r0.threadMustExit
                if (r0 != 0) goto L_0x002d
            L_0x0008:
                com.lumiyaviewer.lumiya.slproto.modules.SLUserNameFetcher r0 = com.lumiyaviewer.lumiya.slproto.modules.SLUserNameFetcher.this     // Catch:{ InterruptedException -> 0x002c }
                Boolean r0 = r0.FetchSomeNamesOverHTTP()     // Catch:{ InterruptedException -> 0x002c }
                if (r0 != 0) goto L_0x0008
                com.lumiyaviewer.lumiya.slproto.modules.SLUserNameFetcher r0 = com.lumiyaviewer.lumiya.slproto.modules.SLUserNameFetcher.this     // Catch:{ InterruptedException -> 0x002c }
                java.util.concurrent.locks.Lock r0 = r0.lock     // Catch:{ InterruptedException -> 0x002c }
                r0.lock()     // Catch:{ InterruptedException -> 0x002c }
                com.lumiyaviewer.lumiya.slproto.modules.SLUserNameFetcher r0 = com.lumiyaviewer.lumiya.slproto.modules.SLUserNameFetcher.this     // Catch:{ all -> 0x002e }
                java.util.concurrent.locks.Condition r0 = r0.hasNamesToFetch     // Catch:{ all -> 0x002e }
                r0.await()     // Catch:{ all -> 0x002e }
                com.lumiyaviewer.lumiya.slproto.modules.SLUserNameFetcher r0 = com.lumiyaviewer.lumiya.slproto.modules.SLUserNameFetcher.this     // Catch:{ InterruptedException -> 0x002c }
                java.util.concurrent.locks.Lock r0 = r0.lock     // Catch:{ InterruptedException -> 0x002c }
                r0.unlock()     // Catch:{ InterruptedException -> 0x002c }
                goto L_0x0000
            L_0x002c:
                r0 = move-exception
            L_0x002d:
                return
            L_0x002e:
                r0 = move-exception
                com.lumiyaviewer.lumiya.slproto.modules.SLUserNameFetcher r1 = com.lumiyaviewer.lumiya.slproto.modules.SLUserNameFetcher.this     // Catch:{ InterruptedException -> 0x002c }
                java.util.concurrent.locks.Lock r1 = r1.lock     // Catch:{ InterruptedException -> 0x002c }
                r1.unlock()     // Catch:{ InterruptedException -> 0x002c }
                throw r0     // Catch:{ InterruptedException -> 0x002c }
            */
            throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.modules.SLUserNameFetcher.AnonymousClass1.run():Unit")
        }
    }
    private Any udpLock = Any()
    private UserManager userManager
    private WeakPriorityRequestSet<UUID> userNameRequests
    private Long waitingReplySince = 0
    private Thread workingThread
    private LLSDXMLRequest xmlReq

    SLUserNameFetcher(SLAgentCircuit sLAgentCircuit, SLCaps sLCaps) {
        super(sLAgentCircuit)
        this.userManager = UserManager.getUserManager(sLAgentCircuit.circuitInfo.agentID)
        this.caps = sLCaps
        this.threadMustExit = false
        if (sLCaps.getCapability(SLCaps.SLCapability.GetDisplayNames) != null) {
            this.xmlReq = LLSDXMLRequest()
            this.workingThread = Thread(this.threadRunnable, "DisplayNameFetcher")
            this.workingThread.start()
        } else {
            this.workingThread = null
            this.xmlReq = null
        }
        if (this.userManager != null) {
            this.userNameRequests = this.userManager.getUserNameRequests()
            this.userNameRequests.addListener(this)
            return
        }
        this.userNameRequests = null
    }

    /* access modifiers changed from: private */
    Boolean FetchSomeNamesOverHTTP() {
        String str
        LLSDNode lLSDNode
        List<UUID> uUIDsToFetch = getUUIDsToFetch(4)
        if (uUIDsToFetch.isEmpty()) {
            return false
        }
        String str2 = this.caps.getCapability(SLCaps.SLCapability.GetDisplayNames) + "/"
        Iterator<T> it = uUIDsToFetch.iterator()
        Boolean z = true
        while (true) {
            str = str2
            if (it.hasNext()) {
                str2 = (z ? str + "?" : str + "&") + "ids=" + ((UUID) it.next()).toString()
                z = false
            } else {
                try {
                    break
                } catch (LLSDXMLException e) {
                    e.printStackTrace()
                    lLSDNode = null
                } catch (IOException e2) {
                    e2.printStackTrace()
                    lLSDNode = null
                }
            }
        }
        lLSDNode = this.xmlReq.PerformRequest(str, (LLSDNode) null)
        if (lLSDNode != null) {
            try {
                if (lLSDNode.keyExists("agents")) {
                    LLSDNode byKey = lLSDNode.byKey("agents")
                    for (Int i = 0; i < byKey.getCount(); i++) {
                        LLSDNode byIndex = byKey.byIndex(i)
                        UUID asUUID = byIndex.byKey("id").asUUID()
                        String asString = byIndex.byKey("display_name").asString()
                        String asString2 = byIndex.byKey("username").asString()
                        if (this.userManager != null) {
                            this.userManager.updateUserNames(asUUID, asString2, asString)
                            this.userNameRequests.completeRequest(asUUID)
                        }
                    }
                }
                if (lLSDNode.keyExists("bad_ids")) {
                    LLSDNode byKey2 = lLSDNode.byKey("bad_ids")
                    for (Int i2 = 0; i2 < byKey2.getCount(); i2++) {
                        UUID fromString = UUID.fromString(byKey2.byIndex(i2).asString())
                        if (this.userManager != null) {
                            this.userManager.setUserBadUUID(fromString)
                            this.userNameRequests.completeRequest(fromString)
                        }
                    }
                }
            } catch (LLSDException e3) {
                e3.printStackTrace()
            }
        }
        return true
    }

    private Unit FetchSomeNamesOverUDP() {
        List<UUID> uUIDsToFetch = getUUIDsToFetch(4)
        if (uUIDsToFetch.isEmpty()) {
            this.isWaitingReply = false
            return
        }
        UUIDNameRequest uUIDNameRequest = UUIDNameRequest()
        for (UUID uuid : uUIDsToFetch) {
            UUIDNameRequest.UUIDNameBlock uUIDNameBlock = UUIDNameRequest.UUIDNameBlock()
            uUIDNameBlock.ID = uuid
            uUIDNameRequest.UUIDNameBlock_Fields.add(uUIDNameBlock)
        }
        this.isWaitingReply = true
        this.waitingReplySince = System.currentTimeMillis()
        uUIDNameRequest.isReliable = true
        SendMessage(uUIDNameRequest)
    }

    private List<UUID> getUUIDsToFetch(Int i) {
        UUID request
        ArrayList arrayList = ArrayList(i)
        if (this.userNameRequests != null) {
            while (arrayList.size() < i && (request = this.userNameRequests.getRequest()) != null) {
                arrayList.add(request)
            }
        }
        return arrayList
    }

    Unit HandleCloseCircuit() {
        this.threadMustExit = true
        if (this.xmlReq != null) {
            this.xmlReq.InterruptRequest()
        }
        if (this.workingThread != null) {
            this.workingThread.interrupt()
        }
        if (this.userNameRequests != null) {
            this.userNameRequests.removeListener(this)
        }
    }

    @SLMessageHandler
    synchronized Unit HandleUUIDNameReply(UUIDNameReply uUIDNameReply) {
        for (UUIDNameReply.UUIDNameBlock uUIDNameBlock : uUIDNameReply.UUIDNameBlock_Fields) {
            UUID uuid = uUIDNameBlock.ID
            String str = SLMessage.stringFromVariableOEM(uUIDNameBlock.FirstName) + " " + SLMessage.stringFromVariableOEM(uUIDNameBlock.LastName)
            if (this.userManager != null) {
                this.userManager.updateUserNames(uuid, str, str)
                this.userNameRequests.completeRequest(uuid)
            }
        }
        synchronized (this.udpLock) {
            this.isWaitingReply = false
            FetchSomeNamesOverUDP()
        }
    }

    Unit onNewRequest() {
        if (this.workingThread != null) {
            this.lock.lock()
            try {
                this.hasNamesToFetch.signal()
            } finally {
                this.lock.unlock()
            }
        } else {
            synchronized (this.udpLock) {
                if (!this.isWaitingReply || System.currentTimeMillis() > this.waitingReplySince + REPLY_TIMEOUT) {
                    FetchSomeNamesOverUDP()
                }
            }
        }
    }
}
