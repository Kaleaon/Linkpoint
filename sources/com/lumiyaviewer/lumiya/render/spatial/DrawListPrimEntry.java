// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.render.DrawableObject;
import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

// Referenced classes of package com.lumiyaviewer.lumiya.render.spatial:
//            DrawListObjectEntry, DrawList

public class DrawListPrimEntry extends DrawListObjectEntry
{

    private volatile WeakReference drawableObject;

    public DrawListPrimEntry(SLObjectInfo slobjectinfo)
    {
        super(slobjectinfo);
        drawableObject = null;
    }

    public void addToDrawList(DrawList drawlist)
    {
        Object obj = drawableObject;
        DrawableObject drawableobject;
        if (obj != null)
        {
            obj = (DrawableObject)((WeakReference) (obj)).get();
        } else
        {
            obj = null;
        }
        drawableobject = ((DrawableObject) (obj));
        if (obj == null)
        {
            drawableobject = new DrawableObject(drawlist.drawableStore, objectInfo, null);
            drawableObject = new WeakReference(drawableobject);
        }
        drawlist.objects.add(drawableobject);
    }

    public DrawableObject getDrawableAttachment(DrawableStore drawablestore, DrawableAvatar drawableavatar)
    {
        DrawableObject drawableobject = null;
        Object obj = drawableObject;
        if (obj != null)
        {
            drawableobject = (DrawableObject)((WeakReference) (obj)).get();
        }
        obj = drawableobject;
        if (drawableobject == null)
        {
            obj = new DrawableObject(drawablestore, objectInfo, drawableavatar);
            drawableObject = new WeakReference(obj);
        }
        return ((DrawableObject) (obj));
    }

    public DrawableObject getDrawableObject()
    {
        DrawableObject drawableobject = null;
        WeakReference weakreference = drawableObject;
        if (weakreference != null)
        {
            drawableobject = (DrawableObject)weakreference.get();
        }
        return drawableobject;
    }
}
