package com.linkpoint.slproto.dispnames

import com.linkpoint.Debug
import com.linkpoint.dao.UserName
import com.linkpoint.react.AsyncLimitsRequestHandler
import com.linkpoint.react.RequestHandler
import com.linkpoint.react.RequestQueue
import com.linkpoint.react.ResultHandler
import com.linkpoint.react.SimpleRequestHandler
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.caps.SLCaps
import com.linkpoint.slproto.handler.SLMessageHandler
import com.linkpoint.slproto.https.LLSDXMLRequest
import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.messages.UUIDNameReply
import com.linkpoint.slproto.messages.UUIDNameRequest
import com.linkpoint.slproto.modules.SLModule
import com.linkpoint.slproto.users.manager.UserManager
import java.io.IOException
import java.util.HashSet
import java.util.Set
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import javax.annotation.Nonnull

class SLDisplayNameFetcher : SLModule() {
    private const val MAX_BATCH_SIZE: Int = 4
    private val String capsURL
    private val Runnable httpThreadRunnable = Runnable() {
        override fun run() {
            UUID nextRequest
            val userNameRequestQueue: RequestQueue<UUID, UserName> = SLDisplayNameFetcher.this.userManager.getUserNameRequestQueue()
            val hashSet: HashSet<UUID> = HashSet<>()
            while (!SLDisplayNameFetcher.this.threadMustExit.get()) {
                hashSet.clear()
                try {
                    hashSet.add(userNameRequestQueue.waitForRequest())
                    while (hashSet.size() < 4 && (nextRequest = userNameRequestQueue.getNextRequest()) != null) {
                        hashSet.add(nextRequest)
                    }
                    SLDisplayNameFetcher.this.requestNamesHttp(hashSet, userNameRequestQueue)
                    for (UUID returnRequest : hashSet) {
                        userNameRequestQueue.returnRequest(returnRequest)
                    }
                    hashSet.clear()
                } catch (InterruptedException e) {
                    Debug.Warning(e)
                }
            }
            for (UUID returnRequest2 : hashSet) {
                userNameRequestQueue.returnRequest(returnRequest2)
            }
        }
    }
    private val RequestHandler<UUID> requestHandler = AsyncLimitsRequestHandler(this.agentCircuit, SimpleRequestHandler<UUID>() {
        fun onRequest(uuid: UUID) {
            val uUIDNameRequest: UUIDNameRequest = UUIDNameRequest()
            UUIDNameRequest.UUIDNameBlock uUIDNameBlock = UUIDNameRequest.UUIDNameBlock()
            uUIDNameBlock.ID = uuid
            uUIDNameRequest.UUIDNameBlock_Fields.add(uUIDNameBlock)
            while (uUIDNameRequest.UUIDNameBlock_Fields.size() < 4 && SLDisplayNameFetcher.this.requestQueue != null && ((UUID) SLDisplayNameFetcher.this.requestQueue.getNextRequest()) != null) {
                UUIDNameRequest.UUIDNameBlock uUIDNameBlock2 = UUIDNameRequest.UUIDNameBlock()
                uUIDNameBlock2.ID = uuid
                uUIDNameRequest.UUIDNameBlock_Fields.add(uUIDNameBlock2)
            }
            uUIDNameRequest.isReliable = true
            SLDisplayNameFetcher.this.SendMessage(uUIDNameRequest)
        }
    }, false, 3, 15000)
    /* access modifiers changed from: private */
    val RequestQueue<UUID, UserName> requestQueue
    private val ResultHandler<UUID, UserName> resultHandler
    /* access modifiers changed from: private */
    val AtomicBoolean threadMustExit = AtomicBoolean(false)
    private val Boolean useDisplayNames
    /* access modifiers changed from: private */
    val UserManager userManager
    private val Thread workingThread
    private val LLSDXMLRequest xmlReq

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SLDisplayNameFetcher(SLAgentCircuit sLAgentCircuit, SLCaps sLCaps) {
        super(sLAgentCircuit)
        val resultHandler2: ResultHandler<UUID, UserName> = null
        this.userManager = UserManager.getUserManager(sLAgentCircuit.circuitInfo.agentID)
        this.requestQueue = this.userManager != null ? this.userManager.getUserNameRequestQueue() : null
        if (sLCaps.getCapability(SLCaps.SLCapability.GetDisplayNames) != null) {
            this.capsURL = sLCaps.getCapability(SLCaps.SLCapability.GetDisplayNames)
            this.useDisplayNames = true
            this.resultHandler = this.requestQueue != null ? this.requestQueue.getResultHandler() : resultHandler2
            this.xmlReq = LLSDXMLRequest()
            this.workingThread = Thread(this.httpThreadRunnable, "DisplayNameFetcher")
            this.workingThread.start()
            return
        }
        this.capsURL = null
        this.workingThread = null
        this.xmlReq = null
        this.useDisplayNames = false
        this.resultHandler = this.requestQueue != null ? this.requestQueue.attachRequestHandler(this.requestHandler) : resultHandler2
    }

    /* access modifiers changed from: private */
    fun requestNamesHttp(set: Set<UUID>, requestQueue2: RequestQueue<UUID, UserName>) {
        val append: StringBuilder = StringBuilder(this.capsURL).append('/')
        val z: Boolean = true
        for (UUID uuid : set) {
            Debug.Printf("UserName: Requesting name for %s over HTTP", uuid)
            if (z) {
                append.append('?')
            } else {
                append.append('&')
            }
            append.append("ids=").append(uuid.toString())
            z = false
        }
        try {
            val PerformRequest: LLSDNode = this.xmlReq.PerformRequest(append.toString(), (LLSDNode) null)
            if (PerformRequest != null) {
                if (PerformRequest.keyExists("agents")) {
                    val byKey: LLSDNode = PerformRequest.byKey("agents")
                    for (Int i = 0; i < byKey.getCount(); i++) {
                        val byIndex: LLSDNode = byKey.byIndex(i)
                        val asUUID: UUID = byIndex.byKey("id").asUUID()
                        val userName: UserName = UserName(asUUID, byIndex.byKey("username").asString(), byIndex.byKey("display_name").asString(), false)
                        if (this.resultHandler != null) {
                            this.resultHandler.onResultData(asUUID, userName)
                        }
                        set.remove(asUUID)
                    }
                }
                if (PerformRequest.keyExists("bad_ids")) {
                    val byKey2: LLSDNode = PerformRequest.byKey("bad_ids")
                    for (Int i2 = 0; i2 < byKey2.getCount(); i2++) {
                        val fromString: UUID = UUID.fromString(byKey2.byIndex(i2).asString())
                        val userName2: UserName = UserName(fromString, (String) null, (String) null, true)
                        if (this.resultHandler != null) {
                            this.resultHandler.onResultData(fromString, userName2)
                        }
                        set.remove(fromString)
                    }
                }
            }
        } catch (LLSDException | IOException e) {
            e.printStackTrace()
        }
    }

    fun HandleCloseCircuit() {
        this.threadMustExit.set(true)
        if (this.xmlReq != null) {
            this.xmlReq.InterruptRequest()
        }
        if (this.workingThread != null) {
            this.workingThread.interrupt()
        }
        if (this.requestQueue != null) {
            this.requestQueue.detachRequestHandler(this.requestHandler)
        }
    }

    @SLMessageHandler
    fun HandleUUIDNameReply(uUIDNameReply: UUIDNameReply) {
        for (UUIDNameReply.UUIDNameBlock uUIDNameBlock : uUIDNameReply.UUIDNameBlock_Fields) {
            val uuid: UUID = uUIDNameBlock.ID
            val str: String = SLMessage.stringFromVariableOEM(uUIDNameBlock.FirstName) + " " + SLMessage.stringFromVariableOEM(uUIDNameBlock.LastName)
            val userName: UserName = UserName(uuid, str, str, false)
            if (this.resultHandler != null) {
                this.resultHandler.onResultData(uuid, userName)
            }
        }
    }
}
