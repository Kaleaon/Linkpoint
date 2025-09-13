/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Handler
 *  android.os.Message
 */
package com.lumiyaviewer.lumiya.voice;

import android.os.Handler;
import android.os.Message;
import com.lumiyaviewer.lumiya.voice.Debug;
import com.lumiyaviewer.lumiya.voice.VivoxMessageQueue;
import com.vivox.service.VxClientProxy;
import com.vivox.service.vx_evt_base_t;
import com.vivox.service.vx_message_base_t;
import com.vivox.service.vx_message_type;
import com.vivox.service.vx_req_base_t;
import com.vivox.service.vx_resp_base_t;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

public class VivoxMessageController {
    private final Handler handler;
    private final AtomicBoolean listenForMessages;
    private final OnVivoxMessageListener listener;
    private final Map<String, PendingRequest> pendingRequests;
    private final Runnable receiveMessages;
    private final Thread receiverThread;
    private final AtomicInteger requestId = new AtomicInteger(1);

    public VivoxMessageController(OnVivoxMessageListener onVivoxMessageListener) {
        this.listenForMessages = new AtomicBoolean(true);
        this.pendingRequests = Collections.synchronizedMap(new HashMap());
        this.handler = new Handler(this){
            final VivoxMessageController this$0;
            {
                this.this$0 = vivoxMessageController;
            }

            /*
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            public void handleMessage(Message object) {
                block4: {
                    block3: {
                        if (!(((Message)object).obj instanceof vx_message_base_t)) break block3;
                        object = (vx_message_base_t)((Message)object).obj;
                        if (((vx_message_base_t)object).getType() != vx_message_type.msg_event) break block4;
                        object = new vx_evt_base_t(vx_message_base_t.getCPtr((vx_message_base_t)object), false);
                        this.this$0.listener.onVivoxEvent((vx_evt_base_t)object);
                    }
                    return;
                }
                this.this$0.listener.onVivoxMessage((vx_message_base_t)object);
            }
        };
        this.receiveMessages = new Runnable(this){
            final VivoxMessageController this$0;
            {
                this.this$0 = vivoxMessageController;
            }

            @Override
            public void run() {
                VivoxMessageQueue vivoxMessageQueue = VivoxMessageQueue.getInstance();
                while (this.this$0.listenForMessages.get()) {
                    boolean bl;
                    vx_message_base_t vx_message_base_t2 = vivoxMessageQueue.getMessage();
                    if (vx_message_base_t2 == null) continue;
                    Object object = vx_message_base_t2.getType();
                    Debug.Printf("Voice: got vxMessage (%s)", object);
                    boolean bl2 = bl = false;
                    if (object == vx_message_type.msg_response) {
                        object = new vx_resp_base_t(vx_message_base_t.getCPtr(vx_message_base_t2), false);
                        Object object2 = ((vx_resp_base_t)object).getRequest();
                        bl2 = bl;
                        if (object2 != null) {
                            object2 = ((vx_req_base_t)object2).getCookie();
                            Debug.Printf("Voice: response, requestCookie '%s'", object2);
                            object2 = (PendingRequest)this.this$0.pendingRequests.remove(object2);
                            bl2 = bl;
                            if (object2 != null) {
                                ((PendingRequest)object2).signalRequestCompleted((vx_resp_base_t)object);
                                bl2 = true;
                            }
                        }
                    }
                    if (bl2) continue;
                    this.this$0.handler.sendMessage(this.this$0.handler.obtainMessage(1, (Object)vx_message_base_t2));
                }
            }
        };
        this.listener = onVivoxMessageListener;
        this.receiverThread = new Thread(this.receiveMessages, "VivoxReceiveThread");
        this.receiverThread.start();
    }

    private String getRequestID() {
        return Integer.toString(this.requestId.getAndIncrement());
    }

    public void sendRequest(vx_req_base_t vx_req_base_t2) {
        String string2 = this.getRequestID();
        Debug.Printf("Voice: sending request with cookie '%s'", string2);
        VxClientProxy.set_request_cookie(vx_req_base_t2, string2);
        VxClientProxy.vx_issue_request(vx_req_base_t2);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Nullable
    public vx_resp_base_t sendRequestAndWait(vx_req_base_t object) {
        Object var3_3 = null;
        String string2 = this.getRequestID();
        Debug.Printf("Voice: sending request with cookie '%s'", string2);
        VxClientProxy.set_request_cookie((vx_req_base_t)object, string2);
        PendingRequest pendingRequest = new PendingRequest();
        this.pendingRequests.put(string2, pendingRequest);
        int n = VxClientProxy.vx_issue_request((vx_req_base_t)object);
        if (n != 0) {
            Debug.Printf("Voice: vx_issue_request returned %d", n);
            return var3_3;
        }
        try {
            return pendingRequest.waitResult();
        }
        catch (InterruptedException interruptedException) {
            return var3_3;
        }
    }

    public void stop() {
        this.listenForMessages.set(false);
        this.receiverThread.interrupt();
    }

    public static interface OnVivoxMessageListener {
        public void onVivoxEvent(vx_evt_base_t var1);

        public void onVivoxMessage(vx_message_base_t var1);
    }

    private static class PendingRequest {
        private final Object monitor = new Object();
        private vx_resp_base_t result = null;

        private PendingRequest() {
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public void signalRequestCompleted(vx_resp_base_t vx_resp_base_t2) {
            Object object = this.monitor;
            synchronized (object) {
                this.result = vx_resp_base_t2;
                this.monitor.notifyAll();
                return;
            }
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public vx_resp_base_t waitResult() throws InterruptedException {
            Object object = this.monitor;
            synchronized (object) {
                while (this.result == null) {
                    this.monitor.wait();
                }
                return this.result;
            }
        }
    }
}

