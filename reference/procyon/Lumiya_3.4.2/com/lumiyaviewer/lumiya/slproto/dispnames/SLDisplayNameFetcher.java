// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.dispnames;

import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.messages.UUIDNameReply;
import java.io.IOException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.Debug;
import java.util.HashSet;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.react.AsyncLimitsRequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.UUIDNameRequest;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.Set;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.concurrent.atomic.AtomicBoolean;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.dao.UserName;
import com.lumiyaviewer.lumiya.react.RequestQueue;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;

public class SLDisplayNameFetcher extends SLModule
{
    private static final int MAX_BATCH_SIZE = 4;
    private final String capsURL;
    private final Runnable httpThreadRunnable;
    private final RequestHandler<UUID> requestHandler;
    private final RequestQueue<UUID, UserName> requestQueue;
    private final ResultHandler<UUID, UserName> resultHandler;
    private final AtomicBoolean threadMustExit;
    private final boolean useDisplayNames;
    private final UserManager userManager;
    private final Thread workingThread;
    private final LLSDXMLRequest xmlReq;
    
    public SLDisplayNameFetcher(final SLAgentCircuit slAgentCircuit, final SLCaps slCaps) {
        final ResultHandler<UUID, UserName> resultHandler = null;
        final ResultHandler<UUID, UserName> resultHandler2 = null;
        super(slAgentCircuit);
        this.threadMustExit = new AtomicBoolean(false);
        this.requestHandler = new AsyncLimitsRequestHandler<UUID>(this.agentCircuit, new SimpleRequestHandler<UUID>() {
            @Override
            public void onRequest(@Nonnull final UUID uuid) {
                final UUIDNameRequest uuidNameRequest = new UUIDNameRequest();
                final UUIDNameRequest.UUIDNameBlock e = new UUIDNameRequest.UUIDNameBlock();
                e.ID = uuid;
                uuidNameRequest.UUIDNameBlock_Fields.add(e);
                while (uuidNameRequest.UUIDNameBlock_Fields.size() < 4 && SLDisplayNameFetcher.this.requestQueue != null && SLDisplayNameFetcher.this.requestQueue.getNextRequest() != null) {
                    final UUIDNameRequest.UUIDNameBlock e2 = new UUIDNameRequest.UUIDNameBlock();
                    e2.ID = uuid;
                    uuidNameRequest.UUIDNameBlock_Fields.add(e2);
                }
                uuidNameRequest.isReliable = true;
                SLDisplayNameFetcher.this.SendMessage(uuidNameRequest);
            }
        }, false, 3, 15000L);
        this.httpThreadRunnable = new Runnable() {
            @Override
            public void run() {
                final RequestQueue<UUID, UserName> userNameRequestQueue = SLDisplayNameFetcher.this.userManager.getUserNameRequestQueue();
                final HashSet set = new HashSet();
                while (!SLDisplayNameFetcher.this.threadMustExit.get()) {
                    set.clear();
                    Label_0128: {
                        try {
                            set.add(userNameRequestQueue.waitForRequest());
                            while (set.size() < 4) {
                                final UUID uuid = userNameRequestQueue.getNextRequest();
                                if (uuid == null) {
                                    break;
                                }
                                set.add(uuid);
                            }
                            break Label_0128;
                        }
                        catch (final InterruptedException ex) {
                            Debug.Warning(ex);
                        }
                        break;
                    }
                    SLDisplayNameFetcher.this.requestNamesHttp(set, userNameRequestQueue);
                    final Iterator iterator = set.iterator();
                    while (iterator.hasNext()) {
                        userNameRequestQueue.returnRequest((UUID)iterator.next());
                    }
                    set.clear();
                }
                final Iterator iterator2 = set.iterator();
                while (iterator2.hasNext()) {
                    userNameRequestQueue.returnRequest((UUID)iterator2.next());
                }
            }
        };
        this.userManager = UserManager.getUserManager(slAgentCircuit.circuitInfo.agentID);
        RequestQueue<UUID, UserName> userNameRequestQueue;
        if (this.userManager != null) {
            userNameRequestQueue = this.userManager.getUserNameRequestQueue();
        }
        else {
            userNameRequestQueue = null;
        }
        this.requestQueue = userNameRequestQueue;
        if (slCaps.getCapability(SLCaps.SLCapability.GetDisplayNames) != null) {
            this.capsURL = slCaps.getCapability(SLCaps.SLCapability.GetDisplayNames);
            this.useDisplayNames = true;
            ResultHandler<UUID, UserName> resultHandler3 = resultHandler2;
            if (this.requestQueue != null) {
                resultHandler3 = this.requestQueue.getResultHandler();
            }
            this.resultHandler = resultHandler3;
            this.xmlReq = new LLSDXMLRequest();
            (this.workingThread = new Thread(this.httpThreadRunnable, "DisplayNameFetcher")).start();
        }
        else {
            this.capsURL = null;
            this.workingThread = null;
            this.xmlReq = null;
            this.useDisplayNames = false;
            ResultHandler<UUID, UserName> attachRequestHandler = resultHandler;
            if (this.requestQueue != null) {
                attachRequestHandler = this.requestQueue.attachRequestHandler(this.requestHandler);
            }
            this.resultHandler = attachRequestHandler;
        }
    }
    
    private void requestNamesHttp(final Set<UUID> set, final RequestQueue<UUID, UserName> requestQueue) {
        final int n = 0;
        final StringBuilder append = new StringBuilder(this.capsURL).append('/');
        final Iterator iterator = set.iterator();
        int n2 = 1;
        while (iterator.hasNext()) {
            final UUID uuid = (UUID)iterator.next();
            Debug.Printf("UserName: Requesting name for %s over HTTP", uuid);
            if (n2 != 0) {
                append.append('?');
            }
            else {
                append.append('&');
            }
            append.append("ids=").append(uuid.toString());
            n2 = 0;
        }
        try {
            final LLSDNode performRequest = this.xmlReq.PerformRequest(append.toString(), null);
            if (performRequest != null) {
                if (performRequest.keyExists("agents")) {
                    final LLSDNode byKey = performRequest.byKey("agents");
                    for (int i = 0; i < byKey.getCount(); ++i) {
                        final LLSDNode byIndex = byKey.byIndex(i);
                        final UUID uuid2 = byIndex.byKey("id").asUUID();
                        final UserName userName = new UserName(uuid2, byIndex.byKey("username").asString(), byIndex.byKey("display_name").asString(), false);
                        if (this.resultHandler != null) {
                            this.resultHandler.onResultData(uuid2, userName);
                        }
                        set.remove(uuid2);
                    }
                }
                if (performRequest.keyExists("bad_ids")) {
                    final LLSDNode byKey2 = performRequest.byKey("bad_ids");
                    for (int j = n; j < byKey2.getCount(); ++j) {
                        final UUID fromString = UUID.fromString(byKey2.byIndex(j).asString());
                        final UserName userName2 = new UserName(fromString, null, null, true);
                        if (this.resultHandler != null) {
                            this.resultHandler.onResultData(fromString, userName2);
                        }
                        set.remove(fromString);
                    }
                }
            }
        }
        catch (final LLSDException | IOException ex) {
            ((Throwable)ex).printStackTrace();
        }
    }
    
    @Override
    public void HandleCloseCircuit() {
        this.threadMustExit.set(true);
        if (this.xmlReq != null) {
            this.xmlReq.InterruptRequest();
        }
        if (this.workingThread != null) {
            this.workingThread.interrupt();
        }
        if (this.requestQueue != null) {
            this.requestQueue.detachRequestHandler(this.requestHandler);
        }
    }
    
    @SLMessageHandler
    public void HandleUUIDNameReply(final UUIDNameReply uuidNameReply) {
        for (final UUIDNameReply.UUIDNameBlock uuidNameBlock : uuidNameReply.UUIDNameBlock_Fields) {
            final UUID id = uuidNameBlock.ID;
            final String string = SLMessage.stringFromVariableOEM(uuidNameBlock.FirstName) + " " + SLMessage.stringFromVariableOEM(uuidNameBlock.LastName);
            final UserName userName = new UserName(id, string, string, false);
            if (this.resultHandler != null) {
                this.resultHandler.onResultData(id, userName);
            }
        }
    }
}
