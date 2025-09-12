// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            WorldSurfaceView, WorldViewActivity

class this._cls0 extends Handler
{

    final WorldSurfaceView this$0;

    public void handleMessage(Message message)
    {
        message.what;
        JVM INSTR tableswitch 1 5: default 40
    //                   1 41
    //                   2 154
    //                   3 191
    //                   4 202
    //                   5 213;
           goto _L1 _L2 _L3 _L4 _L5 _L6
_L1:
        return;
_L2:
        if (message.obj != null && (message.obj instanceof ObjectIntersectInfo))
        {
            message = (ObjectIntersectInfo)message.obj;
            Debug.Log((new StringBuilder()).append("UI!!! PICKED OBJECT isAvatar ").append(((ObjectIntersectInfo) (message)).objInfo.isAvatar()).append(" local ID ").append(Integer.toString(((ObjectIntersectInfo) (message)).objInfo.localID)).toString());
            boolean flag;
            if (((ObjectIntersectInfo) (message)).objInfo instanceof SLObjectAvatarInfo)
            {
                flag = ((SLObjectAvatarInfo)((ObjectIntersectInfo) (message)).objInfo).isMyAvatar();
            } else
            {
                flag = false;
            }
            if (!flag)
            {
                WorldSurfaceView._2D_get0(WorldSurfaceView.this).handlePickedObject(message);
                return;
            }
        }
        continue; /* Loop/switch isn't completed */
_L3:
        if (message.obj != null && (message.obj instanceof SLObjectInfo))
        {
            message = (SLObjectInfo)message.obj;
            WorldSurfaceView._2D_get0(WorldSurfaceView.this).setTouchedObject(message);
            return;
        }
        if (true) goto _L1; else goto _L4
_L4:
        WorldSurfaceView._2D_get0(WorldSurfaceView.this).rendererSurfaceCreated();
        return;
_L5:
        WorldSurfaceView._2D_get0(WorldSurfaceView.this).rendererShaderCompileError();
        return;
_L6:
        if (message.obj instanceof Bitmap)
        {
            WorldSurfaceView._2D_get0(WorldSurfaceView.this).processScreenshot((Bitmap)message.obj);
            return;
        }
        if (true) goto _L1; else goto _L7
_L7:
    }

    rInfo()
    {
        this$0 = WorldSurfaceView.this;
        super();
    }
}
