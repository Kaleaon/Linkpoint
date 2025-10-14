package com.lumiyaviewer.lumiya.slproto.dispnames

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.dao.UserName
import com.lumiyaviewer.lumiya.react.AsyncLimitsRequestHandler
import com.lumiyaviewer.lumiya.react.RequestHandler
import com.lumiyaviewer.lumiya.react.RequestQueue
import com.lumiyaviewer.lumiya.react.ResultHandler
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.lumiya.slproto.messages.UUIDNameReply
import com.lumiyaviewer.lumiya.slproto.messages.UUIDNameRequest
import com.lumiyaviewer.lumiya.slproto.modules.SLModule
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.io.IOException
import java.util.HashSet
import java.util.Set
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import javax.annotation.Nonnull

class SLDisplayNameFetcher : SLModule {
    private Int MAX_BATCH_SIZE = 4
    private String capsURL
    private Runnable httpThreadRunnable = Runnable() {
        Unit run() {
            UUID nextRequest
            RequestQueue<UUID, UserName> userNameRequestQueue = SLDisplayNameFetcher.this.userManager.getUserNameRequestQueue()
            HashSet<UUID> hashSet = HashSet<>()
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
    private RequestHandler<UUID> requestHandler = AsyncLimitsRequestHandler(this.agentCircuit, SimpleRequestHandler<UUID>() {
        Unit onRequest(@Nonnull UUID uuid) {
            UUIDNameRequest uUIDNameRequest = UUIDNameRequest()
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
    RequestQueue<UUID, UserName> requestQueue
    private ResultHandler<UUID, UserName> resultHandler
    /* access modifiers changed from: private */
    AtomicBoolean threadMustExit = AtomicBoolean(false)
    private Boolean useDisplayNames
    /* access modifiers changed from: private */
    UserManager userManager
    private Thread workingThread
    private LLSDXMLRequest xmlReq

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    SLDisplayNameFetcher(SLAgentCircuit sLAgentCircuit, SLCaps sLCaps) {
        super(sLAgentCircuit)
        ResultHandler<UUID, UserName> resultHandler2 = null
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
    Unit requestNamesHttp(Set<UUID> set, RequestQueue<UUID, UserName> requestQueue2) {
        StringBuilder append = StringBuilder(this.capsURL).append('/')
        Boolean z = true
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
            LLSDNode PerformRequest = this.xmlReq.PerformRequest(append.toString(), (LLSDNode) null)
            if (PerformRequest != null) {
                if (PerformRequest.keyExists("agents")) {
                    LLSDNode byKey = PerformRequest.byKey("agents")
                    for (Int i = 0; i < byKey.getCount(); i++) {
                        LLSDNode byIndex = byKey.byIndex(i)
                        UUID asUUID = byIndex.byKey("id").asUUID()
                        UserName userName = UserName(asUUID, byIndex.byKey("username").asString(), byIndex.byKey("display_name").asString(), false)
                        if (this.resultHandler != null) {
                            this.resultHandler.onResultData(asUUID, userName)
                        }
                        set.remove(asUUID)
                    }
                }
                if (PerformRequest.keyExists("bad_ids")) {
                    LLSDNode byKey2 = PerformRequest.byKey("bad_ids")
                    for (Int i2 = 0; i2 < byKey2.getCount(); i2++) {
                        UUID fromString = UUID.fromString(byKey2.byIndex(i2).asString())
                        UserName userName2 = UserName(fromString, (String) null, (String) null, true)
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

    Unit HandleCloseCircuit() {
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
    Unit HandleUUIDNameReply(UUIDNameReply uUIDNameReply) {
        for (UUIDNameReply.UUIDNameBlock uUIDNameBlock : uUIDNameReply.UUIDNameBlock_Fields) {
            UUID uuid = uUIDNameBlock.ID
            String str = SLMessage.stringFromVariableOEM(uUIDNameBlock.FirstName) + " " + SLMessage.stringFromVariableOEM(uUIDNameBlock.LastName)
            UserName userName = UserName(uuid, str, str, false)
            if (this.resultHandler != null) {
                this.resultHandler.onResultData(uuid, userName)
            }
        }
    }
}
