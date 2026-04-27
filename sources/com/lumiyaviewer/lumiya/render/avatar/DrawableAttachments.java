// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.avatar;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.DrawableObject;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.lumiya.render.shaders.RiggedMeshProgram30;
import com.lumiyaviewer.lumiya.slproto.avatar.SLSkeletonBoneID;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import java.util.Iterator;

// Referenced classes of package com.lumiyaviewer.lumiya.render.avatar:
//            AvatarSkeleton

class DrawableAttachments
{

    private GLLoadableBuffer glAnimationDataBuffer;
    private final ImmutableMultimap nonRigged;
    private final ImmutableList rigged;

    DrawableAttachments()
    {
        glAnimationDataBuffer = null;
        nonRigged = ImmutableMultimap.of();
        rigged = ImmutableList.of();
    }

    DrawableAttachments(Multimap multimap)
    {
        glAnimationDataBuffer = null;
        com.google.common.collect.ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
        com.google.common.collect.ImmutableList.Builder builder1 = ImmutableList.builder();
        if (multimap != null)
        {
            for (Iterator iterator = multimap.keySet().iterator(); iterator.hasNext();)
            {
                Integer integer = (Integer)iterator.next();
                Iterator iterator1 = multimap.get(integer).iterator();
                while (iterator1.hasNext()) 
                {
                    DrawableObject drawableobject = (DrawableObject)iterator1.next();
                    if (drawableobject.isRiggedMesh())
                    {
                        builder1.add(drawableobject);
                    } else
                    {
                        builder.put(integer, drawableobject);
                    }
                }
            }

        }
        nonRigged = builder.build();
        rigged = builder1.build();
        Debug.Printf("Created drawableAttachments: %d rigged, %d non-rigged", new Object[] {
            Integer.valueOf(rigged.size()), Integer.valueOf(nonRigged.size())
        });
    }

    DrawableAttachments(DrawableAttachments drawableattachments)
    {
        glAnimationDataBuffer = null;
        com.google.common.collect.ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
        com.google.common.collect.ImmutableList.Builder builder1 = ImmutableList.builder();
        builder1.addAll(drawableattachments.rigged);
        for (Iterator iterator = drawableattachments.nonRigged.keySet().iterator(); iterator.hasNext();)
        {
            Integer integer = (Integer)iterator.next();
            Iterator iterator1 = drawableattachments.nonRigged.get(integer).iterator();
            while (iterator1.hasNext()) 
            {
                DrawableObject drawableobject = (DrawableObject)iterator1.next();
                if (drawableobject.isRiggedMesh())
                {
                    builder1.add(drawableobject);
                } else
                {
                    builder.put(integer, drawableobject);
                }
            }
        }

        nonRigged = builder.build();
        rigged = builder1.build();
        glAnimationDataBuffer = drawableattachments.glAnimationDataBuffer;
        Debug.Printf("Updated drawableAttachments: %d rigged, %d non-rigged", new Object[] {
            Integer.valueOf(rigged.size()), Integer.valueOf(nonRigged.size())
        });
    }

    boolean Draw(RenderContext rendercontext, AvatarSkeleton avatarskeleton, boolean flag)
    {
        if (!rigged.isEmpty())
        {
            if (rendercontext.hasGL30)
            {
                rendercontext.setupRiggedMeshProgram(true);
                Object obj;
                Object obj1;
                float af[];
                DrawableObject drawableobject;
                int i;
                boolean flag1;
                if (glAnimationDataBuffer == null)
                {
                    glAnimationDataBuffer = new GLLoadableBuffer(new DirectByteBuffer(rendercontext.currentRiggedMeshProgram.uAnimationDataBlockSize));
                    flag1 = true;
                } else
                {
                    flag1 = false;
                }
                if (flag || flag1)
                {
                    glAnimationDataBuffer.getRawBuffer().loadFromFloatArray(0, avatarskeleton.jointWorldMatrix, 0, (SLSkeletonBoneID.VALUES.length + 47) * 16);
                }
                obj = glAnimationDataBuffer;
                if (flag)
                {
                    flag1 = true;
                }
                ((GLLoadableBuffer) (obj)).BindUniformDynamic(rendercontext, 1, flag1);
                obj = rigged.iterator();
                for (i = 0; ((Iterator) (obj)).hasNext(); i = ((DrawableObject)((Iterator) (obj)).next()).DrawRigged30(rendercontext, 1) | i) { }
                if ((i & 2) != 0)
                {
                    rendercontext.setupRiggedMeshProgram(false);
                    for (obj = rigged.iterator(); ((Iterator) (obj)).hasNext(); ((DrawableObject)((Iterator) (obj)).next()).DrawRigged30(rendercontext, 2)) { }
                }
                rendercontext.clearRiggedMeshProgram();
            } else
            {
                obj = rigged.iterator();
                while (((Iterator) (obj)).hasNext()) 
                {
                    ((DrawableObject)((Iterator) (obj)).next()).DrawRigged(rendercontext, avatarskeleton, 3);
                }
            }
        }
        obj = nonRigged.keySet().iterator();
        flag = false;
        do
        {
            if (!((Iterator) (obj)).hasNext())
            {
                break;
            }
            obj1 = (Integer)((Iterator) (obj)).next();
            af = avatarskeleton.getAttachmentMatrix(((Integer) (obj1)).intValue());
            if (af != null)
            {
                rendercontext.glObjWorldPushAndMultMatrixf(af, 0);
                for (obj1 = nonRigged.get(obj1).iterator(); ((Iterator) (obj1)).hasNext();)
                {
                    drawableobject = (DrawableObject)((Iterator) (obj1)).next();
                    if (drawableobject.isRiggedMesh())
                    {
                        flag = true;
                    } else
                    {
                        drawableobject.Draw(rendercontext, 3);
                    }
                }

                rendercontext.glObjWorldPopMatrix();
            }
        } while (true);
        return flag;
    }
}
