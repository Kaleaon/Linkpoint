// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAnimatedMeshData;
import com.lumiyaviewer.lumiya.slproto.avatar.SLPolyMesh;
import java.util.Arrays;

// Referenced classes of package com.lumiyaviewer.lumiya.render.avatar:
//            DrawableAvatarPart

class this._cls0
    implements Runnable
{

    final DrawableAvatarPart this$0;

    public void run()
    {
        Debug.Printf("Avatar: meshUpdate entered for part %s", new Object[] {
            DrawableAvatarPart._2D_get0(DrawableAvatarPart.this).toString()
        });
        Object obj = DrawableAvatarPart._2D_get5(DrawableAvatarPart.this);
        obj;
        JVM INSTR monitorenter ;
        Object obj1;
        float af[];
        obj1 = DrawableAvatarPart._2D_get3(DrawableAvatarPart.this);
        af = DrawableAvatarPart._2D_get2(DrawableAvatarPart.this);
        obj;
        JVM INSTR monitorexit ;
        if (af == null || obj1 == null) goto _L2; else goto _L1
_L1:
        Debug.Printf("Avatar: meshUpdate: part %s params %s", new Object[] {
            DrawableAvatarPart._2D_get0(DrawableAvatarPart.this).toString(), Arrays.toString(af)
        });
        obj = new SLAnimatedMeshData(DrawableAvatarPart._2D_get4(DrawableAvatarPart.this), DrawableAvatarPart._2D_get1(DrawableAvatarPart.this));
        DrawableAvatarPart._2D_get4(DrawableAvatarPart.this).applyMorphData(((com.lumiyaviewer.lumiya.slproto.avatar.SLMeshData) (obj)), af, ((com.lumiyaviewer.lumiya.render.GLTexture) (obj1)));
        obj1 = DrawableAvatarPart._2D_get5(DrawableAvatarPart.this);
        obj1;
        JVM INSTR monitorenter ;
        DrawableAvatarPart._2D_set0(DrawableAvatarPart.this, ((SLAnimatedMeshData) (obj)));
        DrawableAvatarPart._2D_set1(DrawableAvatarPart.this, true);
        obj1;
        JVM INSTR monitorexit ;
_L2:
        return;
        obj1;
        throw obj1;
        Exception exception;
        exception;
        throw exception;
    }

    ex()
    {
        this$0 = DrawableAvatarPart.this;
        super();
    }
}
