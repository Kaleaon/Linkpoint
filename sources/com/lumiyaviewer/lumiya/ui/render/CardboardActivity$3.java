// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.os.Handler;
import android.os.Message;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            CardboardActivity

class this._cls0 extends Handler
{

    final CardboardActivity this$0;

    public void handleMessage(Message message)
    {
        boolean flag = false;
        message.what;
        JVM INSTR tableswitch 1 2: default 28
    //                   1 29
    //                   2 123;
           goto _L1 _L2 _L3
_L1:
        return;
_L2:
        if (message.obj != null && (message.obj instanceof ObjectIntersectInfo))
        {
            message = (ObjectIntersectInfo)message.obj;
            Debug.Printf("Cardboard: PICKED OBJECT isAvatar %b localID %d", new Object[] {
                Boolean.valueOf(((ObjectIntersectInfo) (message)).objInfo.isAvatar()), Integer.valueOf(((ObjectIntersectInfo) (message)).objInfo.localID)
            });
            if (((ObjectIntersectInfo) (message)).objInfo instanceof SLObjectAvatarInfo)
            {
                flag = ((SLObjectAvatarInfo)((ObjectIntersectInfo) (message)).objInfo).isMyAvatar();
            }
            if (!flag)
            {
                CardboardActivity._2D_wrap2(CardboardActivity.this, message);
                return;
            }
        }
        continue; /* Loop/switch isn't completed */
_L3:
        if (message.obj != null && (message.obj instanceof SLObjectInfo))
        {
            message = (SLObjectInfo)message.obj;
            Debug.Printf("Cardboard: touched object isAvatar %b localID %d", new Object[] {
                Boolean.valueOf(message.isAvatar()), Integer.valueOf(((SLObjectInfo) (message)).localID)
            });
            return;
        }
        if (true) goto _L1; else goto _L4
_L4:
    }

    Info()
    {
        this$0 = CardboardActivity.this;
        super();
    }
}
