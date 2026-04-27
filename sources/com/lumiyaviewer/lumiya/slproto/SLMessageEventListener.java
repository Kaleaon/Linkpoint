// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto:
//            SLMessage

public interface SLMessageEventListener
{
    public static abstract class SLMessageBaseEventListener
        implements SLMessageEventListener
    {

        public void onMessageAcknowledged(SLMessage slmessage)
        {
        }

        public void onMessageTimeout(SLMessage slmessage)
        {
        }

        public SLMessageBaseEventListener()
        {
        }
    }


    public abstract void onMessageAcknowledged(SLMessage slmessage);

    public abstract void onMessageTimeout(SLMessage slmessage);
}
