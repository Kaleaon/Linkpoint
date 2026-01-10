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

class SLUserNameFetcher : SLModule : RequestListener {
    private val MAX_BATCH_SIZE: Int = 4
    private val REPLY_TIMEOUT: Long = 10000
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
        fun run()  {
            /*
                r2 = this
            L_0x0000:
                com.linkpoint.slproto.modules.SLUserNameFetcher r0 = com.linkpoint.slproto.modules.SLUserNameFetcher.this
                var r0: Boolean = r0.threadMustExit
                if (r0 != 0) goto L_0x002d
            L_0x0008:
                com.linkpoint.slproto.modules.SLUserNameFetcher r0 = com.linkpoint.slproto.modules.SLUserNameFetcher.this     // Catch:{ InterruptedException -> 0x002c }
                var r0: Boolean = r0.FetchSomeNamesOverHTTP()     // Catch:{ InterruptedException -> 0x002c }
                if (r0 != 0) goto L_0x0008
                com.linkpoint.slproto.modules.SLUserNameFetcher r0 = com.linkpoint.slproto.modules.SLUserNameFetcher.this     // Catch:{ InterruptedException -> 0x002c }
                java.util.concurrent.locks.Lock r0 = r0.lock     // Catch:{ InterruptedException -> 0x002c }
                r0.lock()     // Catch:{ InterruptedException -> 0x002c }
                com.linkpoint.slproto.modules.SLUserNameFetcher r0 = com.linkpoint.slproto.modules.SLUserNameFetcher.this     // Catch:{ all -> 0x002e }
                java.util.concurrent.locks.Condition r0 = r0.hasNamesToFetch     // Catch:{ all -> 0x002e }
                r0.await()     // Catch:{ all -> 0x002e }
                com.linkpoint.slproto.modules.SLUserNameFetcher r0 = com.linkpoint.slproto.modules.SLUserNameFetcher.this     // Catch:{ InterruptedException -> 0x002c }
                java.util.concurrent.locks.Lock r0 = r0.lock     // Catch:{ InterruptedException -> 0x002c }
                r0.unlock()     // Catch:{ InterruptedException -> 0x002c }
                goto L_0x0000
            L_0x002c:
                r0 = move-exception
            L_0x002d:
                return
            L_0x002e:
                r0 = move-exception
                com.linkpoint.slproto.modules.SLUserNameFetcher r1 = com.linkpoint.slproto.modules.SLUserNameFetcher.this     // Catch:{ InterruptedException -> 0x002c }
                java.util.concurrent.locks.Lock r1 = r1.lock     // Catch:{ InterruptedException -> 0x002c }
                r1.unlock()     // Catch:{ InterruptedException -> 0x002c }
                throw r0     // Catch:{ InterruptedException -> 0x002c }
            */
            throw UnsupportedOperationException("Method not decompiled: com.linkpoint.slproto.modules.SLUserNameFetcher.AnonymousClass1.run():Unit")
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
    fun FetchSomeNamesOverHTTP(): Boolean {
        String str
        LLSDNode lLSDNode
        List<UUID> uUIDsToFetch = getUUIDsToFetch(4)
        if (uUIDsToFetch.isEmpty()) {
            return false
        }
        var str2: String = this.caps.getCapability(SLCaps.SLCapability.GetDisplayNames) + "/"
        Iterator<T> it = uUIDsToFetch.iterator()
        var z: Boolean = true
        while (true) {
            str = str2
            if (it.hasNext()) {
                str2 = (z ? str + "?" : str + "&") + "ids=" + ((it as UUID).next()).toString()
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
                    for (i in 0 until byKey.getCount()) {
                        LLSDNode byIndex = byKey.byIndex(i)
                        UUID asUUID = byIndex.byKey("id").asUUID()
                        var asString: String = byIndex.byKey("display_name").asString()
                        var asString2: String = byIndex.byKey("username").asString()
                        if (this.userManager != null) {
                            this.userManager.updateUserNames(asUUID, asString2, asString)
                            this.userNameRequests.completeRequest(asUUID)
                        }
                    }
                }
                if (lLSDNode.keyExists("bad_ids")) {
                    LLSDNode byKey2 = lLSDNode.byKey("bad_ids")
                    for (i2 in 0 until byKey2.getCount()) {
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

    fun HandleCloseCircuit()  {
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
            var str: String = SLMessage.stringFromVariableOEM(uUIDNameBlock.FirstName) + " " + SLMessage.stringFromVariableOEM(uUIDNameBlock.LastName)
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

    fun onNewRequest()  {
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
