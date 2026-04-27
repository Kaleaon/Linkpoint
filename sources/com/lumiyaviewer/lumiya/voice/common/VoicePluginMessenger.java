// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

// Referenced classes of package com.lumiyaviewer.lumiya.voice.common:
//            VoicePluginMessageType, VoicePluginMessage

public class VoicePluginMessenger
{

    public VoicePluginMessenger()
    {
    }

    public static boolean sendMessage(Messenger messenger, VoicePluginMessageType voicepluginmessagetype, VoicePluginMessage voicepluginmessage, Messenger messenger1)
    {
        if (messenger == null)
        {
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putString("messageType", voicepluginmessagetype.toString());
        bundle.putBundle("message", voicepluginmessage.toBundle());
        voicepluginmessagetype = Message.obtain(null, 200, bundle);
        voicepluginmessagetype.replyTo = messenger1;
        try
        {
            messenger.send(voicepluginmessagetype);
        }
        // Misplaced declaration of an exception variable
        catch (Messenger messenger)
        {
            return false;
        }
        return true;
    }
}
