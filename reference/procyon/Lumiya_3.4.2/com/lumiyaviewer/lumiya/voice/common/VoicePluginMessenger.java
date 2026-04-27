// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.voice.common;

import android.os.RemoteException;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.os.Messenger;

public class VoicePluginMessenger
{
    public static boolean sendMessage(final Messenger messenger, final VoicePluginMessageType voicePluginMessageType, final VoicePluginMessage voicePluginMessage, final Messenger replyTo) {
        if (messenger == null) {
            return false;
        }
        final Bundle bundle = new Bundle();
        bundle.putString("messageType", voicePluginMessageType.toString());
        bundle.putBundle("message", voicePluginMessage.toBundle());
        final Message obtain = Message.obtain((Handler)null, 200, (Object)bundle);
        obtain.replyTo = replyTo;
        try {
            messenger.send(obtain);
            return true;
        }
        catch (final RemoteException ex) {
            return false;
        }
    }
}
