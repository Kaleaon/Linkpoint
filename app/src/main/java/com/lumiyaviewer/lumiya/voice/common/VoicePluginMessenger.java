package com.lumiyaviewer.lumiya.voice.common;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class VoicePluginMessenger {
    public static boolean sendMessage(Messenger messenger, VoicePluginMessageType voicePluginMessageType, VoicePluginMessage voicePluginMessage, Messenger messenger2) {
        if (messenger == null) {
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putString("messageType", voicePluginMessageType.toString());
        bundle.putBundle("message", voicePluginMessage.toBundle());
        Message obtain = Message.obtain((Handler) null, 200, bundle);
        obtain.replyTo = messenger2;
        try {
            messenger.send(obtain);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }
}
