// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres;

import com.lumiyaviewer.lumiya.render.RenderContext;

// Referenced classes of package com.lumiyaviewer.lumiya.render.glres:
//            GLLoadQueue

static interface er
{

    public abstract void GLCompleteLoad();

    public abstract int GLGetLoadSize();

    public abstract int GLLoad(RenderContext rendercontext, er er);
}
