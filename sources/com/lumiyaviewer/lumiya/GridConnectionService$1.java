// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya;

import android.os.Handler;
import android.os.Message;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthParams;
import com.lumiyaviewer.lumiya.slproto.events.SLLoginResultEvent;

// Referenced classes of package com.lumiyaviewer.lumiya:
//            GridConnectionService, Debug

class this._cls0 extends Handler
{

    final GridConnectionService this$0;

    public void handleMessage(Message message)
    {
        message.what;
        JVM INSTR tableswitch 2131755033 2131755035: default 32
    //                   2131755033 33
    //                   2131755034 100
    //                   2131755035 69;
           goto _L1 _L2 _L3 _L4
_L1:
        return;
_L2:
        Debug.Printf("License: License check ok.", new Object[0]);
        if (message.obj instanceof SLAuthParams)
        {
            message = (SLAuthParams)message.obj;
            GridConnectionService._2D_wrap0(GridConnectionService.this, message);
            return;
        }
          goto _L1
_L4:
        Debug.Printf("License: License check failed.", new Object[0]);
        GridConnectionService._2D_get0(GridConnectionService.this).publish(new SLLoginResultEvent(false, "You don't have valid license to use this application.", null));
        return;
_L3:
        if (message.obj instanceof String)
        {
            message = (String)message.obj;
        } else
        {
            message = "Internal application error";
        }
        Debug.Printf("License: License check app error: %s", new Object[] {
            message
        });
        GridConnectionService._2D_get0(GridConnectionService.this).publish(new SLLoginResultEvent(false, (new StringBuilder()).append("License check failed: ").append(message).append(".").toString(), null));
        return;
    }

    sultEvent()
    {
        this$0 = GridConnectionService.this;
        super();
    }
}
