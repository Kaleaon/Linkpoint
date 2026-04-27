// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render;

import com.google.common.cache.CacheLoader;
import com.lumiyaviewer.lumiya.render.avatar.AvatarVisualState;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;

// Referenced classes of package com.lumiyaviewer.lumiya.render:
//            DrawableStore

class this._cls0 extends CacheLoader
{

    final DrawableStore this$0;

    public DrawableAvatarStub load(SLObjectAvatarInfo slobjectavatarinfo)
    {
        return slobjectavatarinfo.getAvatarVisualState().createDrawableAvatarStub(DrawableStore.this);
    }

    public volatile Object load(Object obj)
        throws Exception
    {
        return load((SLObjectAvatarInfo)obj);
    }

    vatarStub()
    {
        this$0 = DrawableStore.this;
        super();
    }
}
