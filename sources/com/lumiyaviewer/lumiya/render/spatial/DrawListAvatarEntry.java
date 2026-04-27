// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.spatial;

import com.google.common.cache.LoadingCache;
import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

// Referenced classes of package com.lumiyaviewer.lumiya.render.spatial:
//            DrawListObjectEntry, DrawList

public class DrawListAvatarEntry extends DrawListObjectEntry
{

    private WeakReference drawableAvatar;
    private WeakReference drawableAvatarStub;
    private final SLObjectAvatarInfo objectAvatarInfo;

    public DrawListAvatarEntry(SLObjectAvatarInfo slobjectavatarinfo)
    {
        super(slobjectavatarinfo);
        drawableAvatar = null;
        drawableAvatarStub = null;
        objectAvatarInfo = slobjectavatarinfo;
    }

    public void addToDrawList(DrawList drawlist)
    {
        Object obj1 = null;
        Object obj = null;
        if (drawlist.avatars.size() < drawlist.avatarCountLimit || objectAvatarInfo.isMyAvatar())
        {
            obj1 = drawableAvatar;
            if (obj1 != null)
            {
                obj = (DrawableAvatar)((WeakReference) (obj1)).get();
            }
            obj1 = obj;
            if (obj == null)
            {
                obj1 = (DrawableAvatar)drawlist.drawableStore.drawableAvatarCache.getUnchecked(objectAvatarInfo);
                drawableAvatar = new WeakReference(obj1);
            }
            drawlist.avatars.add(obj1);
            if (objectAvatarInfo.isMyAvatar())
            {
                drawlist.myAvatar = ((DrawableAvatar) (obj1));
            }
            return;
        }
        WeakReference weakreference = drawableAvatarStub;
        obj = obj1;
        if (weakreference != null)
        {
            obj = (DrawableAvatarStub)weakreference.get();
        }
        obj1 = obj;
        if (obj == null)
        {
            obj1 = (DrawableAvatarStub)drawlist.drawableStore.drawableAvatarStubCache.getUnchecked(objectAvatarInfo);
            drawableAvatarStub = new WeakReference(obj1);
        }
        drawlist.avatarStubs.add(obj1);
    }

    public SLObjectAvatarInfo getObjectAvatarInfo()
    {
        return objectAvatarInfo;
    }
}
