// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.cloud.common;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

// Referenced classes of package com.lumiyaviewer.lumiya.cloud.common:
//            MessageType, Bundleable

public class CloudSyncMessenger
{

    public CloudSyncMessenger()
    {
    }

    public static boolean sendMessage(Messenger messenger, MessageType messagetype, Bundleable bundleable, Messenger messenger1)
    {
        if (messenger == null)
        {
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putString("messageType", messagetype.toString());
        bundle.putBundle("message", bundleable.toBundle());
        messagetype = Message.obtain(null, 100, bundle);
        messagetype.replyTo = messenger1;
        try
        {
            messenger.send(messagetype);
        }
        // Misplaced declaration of an exception variable
        catch (Messenger messenger)
        {
            return false;
        }
        return true;
    }
}
