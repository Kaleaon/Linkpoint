package com.linkpoint.slproto.modules

import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.caps.SLCaps
import com.linkpoint.slproto.handler.SLMessageHandler
import com.linkpoint.slproto.https.LLSDXMLRequest
import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.llsd.LLSDXMLException
import com.linkpoint.slproto.messages.UUIDNameReply
import com.linkpoint.slproto.messages.UUIDNameRequest
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.utils.reqset.RequestListener
import com.linkpoint.utils.reqset.WeakPriorityRequestSet
import java.io.IOException
import java.util.ArrayList
import java.util.Iterator
import java.util.List
import java.util.UUID
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class SLUserNameFetcher : SLModule(), RequestListener {
    private const val MAX_BATCH_SIZE: Int = 4
    private const val REPLY_TIMEOUT: Long = 10000
    private val SLCaps caps
    /* access modifiers changed from: private */
    val Condition hasNamesToFetch = this.lock.newCondition()
    private Boolean isWaitingReply
    /* access modifiers changed from: private */
    val Lock lock = ReentrantLock()
    /* access modifiers changed from: private */
    public volatile Boolean threadMustExit
    private val Runnable threadRunnable = Runnable() {
        /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        fun run() {
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
    private val Object udpLock = Object()
    private val UserManager userManager
    private val WeakPriorityRequestSet<UUID> userNameRequests
    private Long waitingReplySince = 0
    private val Thread workingThread
    private val LLSDXMLRequest xmlReq

    public SLUserNameFetcher(SLAgentCircuit sLAgentCircuit, SLCaps sLCaps) {
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
    public Boolean FetchSomeNamesOverHTTP() {
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

    fun HandleCloseCircuit() {
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
    public synchronized Unit HandleUUIDNameReply(UUIDNameReply uUIDNameReply) {
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

    fun onNewRequest() {
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
