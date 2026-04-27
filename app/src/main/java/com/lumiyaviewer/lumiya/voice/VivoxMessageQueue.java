/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.voice;

import com.lumiyaviewer.lumiya.voice.Debug;
import com.vivox.service.VxClientProxy;
import com.vivox.service.vx_message_base_t;
import javax.annotation.Nullable;

public class VivoxMessageQueue {
    private final Object messageLock = new Object();

    private VivoxMessageQueue() {
        VxClientProxy.register_message_notification_handler("com/lumiyaviewer/lumiya/voice/VivoxMessageQueue", "handle_notification");
    }

    public static VivoxMessageQueue getInstance() {
        return InstanceHolder.instance;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void handleNotification() {
        Object object = this.messageLock;
        synchronized (object) {
            this.messageLock.notifyAll();
            return;
        }
    }

    public static void handle_notification() {
        VivoxMessageQueue.getInstance().handleNotification();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    public vx_message_base_t getMessage() {
        Object object = this.messageLock;
        synchronized (object) {
            while (true) {
                vx_message_base_t vx_message_base_t2;
                if ((vx_message_base_t2 = VxClientProxy.get_next_message_no_wait()) != null) {
                    Debug.Printf("Voice: got message from Vivox", new Object[0]);
                    return vx_message_base_t2;
                }
                Debug.Printf("Voice: waiting for Vivox event", new Object[0]);
                try {
                    this.messageLock.wait();
                }
                catch (Exception e) { // Decompiler artifact - empty catch block }
                Debug.Printf("Voice: got Vivox event", new Object[0]);
            }
        }
    }

    private static class InstanceHolder {
        private static final VivoxMessageQueue instance = new VivoxMessageQueue();

        private InstanceHolder() {
        }
    }
}

