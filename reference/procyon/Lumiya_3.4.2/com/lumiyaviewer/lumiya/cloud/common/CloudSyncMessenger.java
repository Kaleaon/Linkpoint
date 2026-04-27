// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.cloud.common;

import android.os.RemoteException;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.os.Messenger;

public class CloudSyncMessenger
{
    public static boolean sendMessage(final Messenger messenger, final MessageType messageType, final Bundleable bundleable, final Messenger replyTo) {
        if (messenger == null) {
            return false;
        }
        final Bundle bundle = new Bundle();
        bundle.putString("messageType", messageType.toString());
        bundle.putBundle("message", bundleable.toBundle());
        final Message obtain = Message.obtain((Handler)null, 100, (Object)bundle);
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
