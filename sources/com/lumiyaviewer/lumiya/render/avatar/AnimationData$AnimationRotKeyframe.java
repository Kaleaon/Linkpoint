// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;

// Referenced classes of package com.lumiyaviewer.lumiya.render.avatar:
//            AnimationData

private static class quaternion extends quaternion
{

    private final LLQuaternion quaternion;

    protected LLQuaternion getTransform()
    {
        return quaternion;
    }

    protected volatile Object getTransform()
    {
        return getTransform();
    }

    public void setInterpolated(LLQuaternion llquaternion, float f, getTransform gettransform, float f1)
    {
        llquaternion.setLerp(quaternion, f, (LLQuaternion)gettransform.Transform(), f1);
    }

    public volatile void setInterpolated(Object obj, float f, Transform transform, float f1)
    {
        setInterpolated((LLQuaternion)obj, f, transform, f1);
    }

    public void setTransform(LLQuaternion llquaternion)
    {
        llquaternion.set(quaternion);
    }

    public volatile void setTransform(Object obj)
    {
        setTransform((LLQuaternion)obj);
    }

    public String toString()
    {
        return quaternion.toString();
    }

    (float f, LLQuaternion llquaternion)
    {
        super(f, null);
        quaternion = llquaternion;
    }
}
