package com.lumiyaviewer.lumiya.cloud.common;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class CloudSyncMessenger {
    public static boolean sendMessage(Messenger messenger, MessageType messageType, Bundleable bundleable, Messenger messenger2) {
        if (messenger == null) {
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putString("messageType", messageType.toString());
        bundle.putBundle("message", bundleable.toBundle());
        Message obtain = Message.obtain(null, 100, bundle);
        obtain.replyTo = messenger2;
        try {
            messenger.send(obtain);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }
}
