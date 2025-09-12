/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Messenger
 */
package com.lumiyaviewer.lumiya.cloud;

import android.os.Messenger;
import com.lumiyaviewer.lumiya.cloud.common.Bundleable;
import com.lumiyaviewer.lumiya.cloud.common.CloudSyncMessenger;
import com.lumiyaviewer.lumiya.cloud.common.MessageType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AgentSyncConnections {
    private final Map<UUID, Messenger> messengers = new HashMap<UUID, Messenger>();

    public void addSyncConnection(UUID uUID, Messenger messenger) {
        this.messengers.put(uUID, messenger);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean sendMessage(UUID uUID, MessageType messageType, Bundleable bundleable, Messenger messenger) {
        boolean bl = false;
        Messenger messenger2 = this.messengers.get(uUID);
        boolean bl2 = bl;
        if (messenger2 == null) return bl2;
        if (CloudSyncMessenger.sendMessage(messenger2, messageType, bundleable, messenger)) return true;
        this.messengers.remove(uUID);
        return bl;
    }
}

