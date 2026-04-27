// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.DrawableObject;
import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.render.MatrixStack;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.TouchHUDEvent;
import com.lumiyaviewer.lumiya.render.picking.IntersectInfo;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import com.lumiyaviewer.lumiya.render.spatial.DrawEntryList;
import com.lumiyaviewer.lumiya.render.spatial.DrawListPrimEntry;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.Vector3Array;
import com.lumiyaviewer.lumiya.utils.LinkedTreeNode;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;

// Referenced classes of package com.lumiyaviewer.lumiya.render.avatar:
//            DrawableAvatar

public class DrawableHUD
{

    private final DrawableAvatar attachedTo;
    private final SLAttachmentPoint attachmentPoint;
    private final DrawableStore drawableStore;
    private final Set hudObjects = Collections.newSetFromMap(new IdentityHashMap());
    private final LLVector3 maxPos = new LLVector3();
    private final LLVector3 minPos = new LLVector3();

    public DrawableHUD(SLAttachmentPoint slattachmentpoint, DrawEntryList drawentrylist, SLObjectInfo slobjectinfo, DrawableStore drawablestore, DrawableAvatar drawableavatar)
    {
        attachmentPoint = slattachmentpoint;
        drawableStore = drawablestore;
        attachedTo = drawableavatar;
        addObject(drawentrylist, slobjectinfo, new MatrixStack(), true);
    }

    private void addObject(DrawEntryList drawentrylist, SLObjectInfo slobjectinfo, MatrixStack matrixstack, boolean flag)
    {
        matrixstack.glPushMatrix();
        processObjectExtents(slobjectinfo, matrixstack, flag);
        com.lumiyaviewer.lumiya.render.spatial.DrawListObjectEntry drawlistobjectentry = slobjectinfo.getDrawListEntry();
        drawentrylist.addEntry(drawlistobjectentry);
        if (drawlistobjectentry instanceof DrawListPrimEntry)
        {
            hudObjects.add(((DrawListPrimEntry)drawlistobjectentry).getDrawableAttachment(drawableStore, attachedTo));
        }
        for (slobjectinfo = slobjectinfo.treeNode.getFirstChild(); slobjectinfo != null; slobjectinfo = slobjectinfo.getNextChild())
        {
            SLObjectInfo slobjectinfo1 = (SLObjectInfo)slobjectinfo.getDataObject();
            if (slobjectinfo1 != null)
            {
                addObject(drawentrylist, slobjectinfo1, matrixstack, false);
            }
        }

        matrixstack.glPopMatrix();
    }

    private void processObjectExtents(SLObjectInfo slobjectinfo, MatrixStack matrixstack, boolean flag)
    {
        float af[] = new float[8];
        Vector3Array vector3array = slobjectinfo.getObjectCoords();
        int i = vector3array.getElementOffset(0);
        int j = vector3array.getElementOffset(1);
        float af1[] = vector3array.getData();
        matrixstack.glTranslatef(af1[i + 0], af1[i + 1], af1[i + 2]);
        matrixstack.glMultMatrixf(slobjectinfo.getRotation().getInverseMatrix(), 0);
        af[0] = -af1[j + 0] / 2.0F;
        af[1] = -af1[j + 1] / 2.0F;
        af[2] = -af1[j + 2] / 2.0F;
        af[3] = 1.0F;
        Matrix.multiplyMV(af, 4, matrixstack.getMatrixData(), matrixstack.getMatrixDataOffset(), af, 0);
        if (flag)
        {
            minPos.x = af[4];
            minPos.y = af[5];
            minPos.z = af[6];
            maxPos.x = af[4];
            maxPos.y = af[5];
            maxPos.z = af[6];
        } else
        {
            minPos.x = Math.min(minPos.x, af[4]);
            minPos.y = Math.min(minPos.y, af[5]);
            minPos.z = Math.min(minPos.z, af[6]);
            maxPos.x = Math.max(maxPos.x, af[4]);
            maxPos.y = Math.max(maxPos.y, af[5]);
            maxPos.z = Math.max(maxPos.z, af[6]);
        }
        af[0] = af1[j + 0] / 2.0F;
        af[1] = af1[j + 1] / 2.0F;
        af[2] = af1[j + 2] / 2.0F;
        af[3] = 1.0F;
        Matrix.multiplyMV(af, 4, matrixstack.getMatrixData(), matrixstack.getMatrixDataOffset(), af, 0);
        minPos.x = Math.min(minPos.x, af[4]);
        minPos.y = Math.min(minPos.y, af[5]);
        minPos.z = Math.min(minPos.z, af[6]);
        maxPos.x = Math.max(maxPos.x, af[4]);
        maxPos.y = Math.max(maxPos.y, af[5]);
        maxPos.z = Math.max(maxPos.z, af[6]);
    }

    public ObjectIntersectInfo Draw(RenderContext rendercontext, float f, float f1, float f2, TouchHUDEvent touchhudevent, boolean flag)
    {
        DrawableObject drawableobject;
        Iterator iterator;
        rendercontext.glModelPushMatrix();
        float f3 = (minPos.y + maxPos.y) / 2.0F;
        float f4 = (minPos.z + maxPos.z) / 2.0F;
        float f5 = Math.max(maxPos.y - minPos.y, maxPos.z - minPos.z);
        if (f5 > 0.001F)
        {
            f = (1.0F / f5) * f;
            rendercontext.glModelScalef(1.0F, f, f);
        }
        rendercontext.glModelTranslatef(-minPos.x, -f3 + f1, -f4 + f2);
        iterator = hudObjects.iterator();
        drawableobject = null;
_L2:
        Object obj;
        if (!iterator.hasNext())
        {
            break MISSING_BLOCK_LABEL_253;
        }
        obj = (DrawableObject)iterator.next();
        if (!flag)
        {
            break; /* Loop/switch isn't completed */
        }
        ((DrawableObject) (obj)).DrawHoverText(rendercontext, true);
        obj = drawableobject;
_L7:
        drawableobject = ((DrawableObject) (obj));
        if (true) goto _L2; else goto _L1
_L1:
        ((DrawableObject) (obj)).Draw(rendercontext, 3);
        if (touchhudevent == null) goto _L4; else goto _L3
_L3:
        ObjectIntersectInfo objectintersectinfo = ((DrawableObject) (obj)).PickObject(rendercontext, touchhudevent.x, touchhudevent.y, (-1.0F / 0.0F));
        if (objectintersectinfo == null) goto _L4; else goto _L5
_L5:
        obj = objectintersectinfo;
        if (drawableobject == null) goto _L7; else goto _L6
_L6:
        obj = objectintersectinfo;
        if (objectintersectinfo.pickDepth < ((ObjectIntersectInfo) (drawableobject)).pickDepth) goto _L7; else goto _L4
_L4:
        obj = drawableobject;
          goto _L7
        rendercontext.glModelPopMatrix();
        if (touchhudevent != null && drawableobject != null)
        {
            Debug.Printf("TouchHUD event: pickDepth %f objID %d", new Object[] {
                Float.valueOf(((ObjectIntersectInfo) (drawableobject)).pickDepth), Integer.valueOf(((ObjectIntersectInfo) (drawableobject)).objInfo.localID)
            });
            if (((ObjectIntersectInfo) (drawableobject)).intersectInfo != null)
            {
                Debug.Printf("TouchHUD event: intersect face %d uv (%f, %f) st (%f, %f)", new Object[] {
                    Integer.valueOf(((ObjectIntersectInfo) (drawableobject)).intersectInfo.faceID), Float.valueOf(((ObjectIntersectInfo) (drawableobject)).intersectInfo.u), Float.valueOf(((ObjectIntersectInfo) (drawableobject)).intersectInfo.v), Float.valueOf(((ObjectIntersectInfo) (drawableobject)).intersectInfo.s), Float.valueOf(((ObjectIntersectInfo) (drawableobject)).intersectInfo.t)
                });
            }
        }
        return drawableobject;
    }

    public SLAttachmentPoint getAttachmentPoint()
    {
        return attachmentPoint;
    }
}
