// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;

// Referenced classes of package com.lumiyaviewer.lumiya.render.avatar:
//            AnimationData

private static class position extends position
{

    private final LLVector3 position;

    protected LLVector3 getTransform()
    {
        return position;
    }

    protected volatile Object getTransform()
    {
        return getTransform();
    }

    public void setInterpolated(LLVector3 llvector3, float f, getTransform gettransform, float f1)
    {
        llvector3.setLerp(position, f, (LLVector3)gettransform.Transform(), f1);
    }

    public volatile void setInterpolated(Object obj, float f, Transform transform, float f1)
    {
        setInterpolated((LLVector3)obj, f, transform, f1);
    }

    public void setTransform(LLVector3 llvector3)
    {
        llvector3.set(position);
    }

    public volatile void setTransform(Object obj)
    {
        setTransform((LLVector3)obj);
    }

    public String toString()
    {
        return position.toString();
    }

    (float f, LLVector3 llvector3)
    {
        super(f, null);
        position = llvector3;
    }
}
