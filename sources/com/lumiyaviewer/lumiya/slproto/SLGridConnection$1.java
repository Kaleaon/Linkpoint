// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto:
//            SLGridConnection

class val.location
    implements Runnable
{

    final SLGridConnection this$0;
    final String val$location;
    final boolean val$pauseBeforeConnecting;

    public void run()
    {
        if (val$pauseBeforeConnecting)
        {
            try
            {
                Thread.sleep(3000L);
            }
            catch (InterruptedException interruptedexception)
            {
                interruptedexception.printStackTrace();
            }
        }
        SLGridConnection._2D_wrap0(SLGridConnection.this, SLGridConnection._2D_get0(SLGridConnection.this), val$location);
        SLGridConnection._2D_set0(SLGridConnection.this, null);
    }

    ()
    {
        this$0 = final_slgridconnection;
        val$pauseBeforeConnecting = flag;
        val$location = String.this;
        super();
    }
}
