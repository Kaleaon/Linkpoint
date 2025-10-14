/*
 * Decompiled with CFR 0.152.
 */
package com.lumiyaviewer.lumiya.voice

import com.lumiyaviewer.lumiya.voice.Debug
import com.vivox.service.VxClientProxy
import com.vivox.service.vx_message_base_t
import javax.annotation.Nullable

class VivoxMessageQueue {
    private Any messageLock = Any()

    private VivoxMessageQueue() {
        VxClientProxy.register_message_notification_handler("com/lumiyaviewer/lumiya/voice/VivoxMessageQueue", "handle_notification")
    }

    VivoxMessageQueue getInstance() {
        return InstanceHolder.instance
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Unit handleNotification() {
        Any object = this.messageLock
        synchronized (object) {
            this.messageLock.notifyAll()
            return
        }
    }

    Unit handle_notification() {
        VivoxMessageQueue.getInstance().handleNotification()
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    vx_message_base_t getMessage() {
        Any object = this.messageLock
        synchronized (object) {
            while (true) {
                vx_message_base_t vx_message_base_t2
                if ((vx_message_base_t2 = VxClientProxy.get_next_message_no_wait()) != null) {
                    Debug.Printf("Voice: got message from Vivox", Any[0])
                    return vx_message_base_t2
                }
                Debug.Printf("Voice: waiting for Vivox event", Any[0])
                try {
                    this.messageLock.wait()
                }
                catch (Exception e) { // Decompiler artifact - empty catch block }
                Debug.Printf("Voice: got Vivox event", Any[0])
            }
        }
    }

    private class InstanceHolder {
        private VivoxMessageQueue instance = VivoxMessageQueue()

        private InstanceHolder() {
        }
    }
}

