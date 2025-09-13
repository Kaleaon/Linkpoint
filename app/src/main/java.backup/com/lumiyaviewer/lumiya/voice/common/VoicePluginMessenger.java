/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 *  android.os.Message
 *  android.os.Messenger
 *  android.os.RemoteException
 */
package com.lumiyaviewer.lumiya.voice.common;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessageType;

public class VoicePluginMessenger {
    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static boolean sendMessage(Messenger messenger, VoicePluginMessageType voicePluginMessageType, VoicePluginMessage voicePluginMessage, Messenger messenger2) {
        boolean bl;
        boolean bl2 = bl = false;
        if (messenger == null) return bl2;
        Bundle bundle = new Bundle();
        bundle.putString("messageType", voicePluginMessageType.toString());
        bundle.putBundle("message", voicePluginMessage.toBundle());
        voicePluginMessageType = Message.obtain(null, (int)200, (Object)bundle);
        ((Message)voicePluginMessageType).replyTo = messenger2;
        try {
            messenger.send((Message)voicePluginMessageType);
            return true;
        }
        catch (RemoteException remoteException) {
            return bl;
        }
    }
}

