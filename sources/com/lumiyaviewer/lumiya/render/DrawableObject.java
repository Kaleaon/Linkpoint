// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render;

import android.opengl.GLES10;
import android.opengl.Matrix;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.render.avatar.DrawableHoverText;
import com.lumiyaviewer.lumiya.render.drawable.DrawablePrim;
import com.lumiyaviewer.lumiya.render.glres.GLQuery;
import com.lumiyaviewer.lumiya.render.picking.CollisionBox;
import com.lumiyaviewer.lumiya.render.picking.GLRayTrace;
import com.lumiyaviewer.lumiya.render.picking.IntersectInfo;
import com.lumiyaviewer.lumiya.render.picking.IntersectPickable;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.geometry.PrimCache;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations;
import com.lumiyaviewer.lumiya.slproto.objects.HoverText;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams;
import com.lumiyaviewer.lumiya.slproto.prims.PrimFlexibleInfo;
import com.lumiyaviewer.lumiya.slproto.prims.PrimVolumeParams;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.Vector3Array;

// Referenced classes of package com.lumiyaviewer.lumiya.render:
//            RenderContext, MatrixStack, BoundingBox, DrawableStore

public class DrawableObject
    implements IntersectPickable, ResourceConsumer
{

    private static final int INVISIBLE_FRAMES_APPEAR = 10;
    private static final int INVISIBLE_FRAMES_DISAPPEAR = 10;
    private final DrawableAvatar attachedTo;
    private volatile DrawableHoverText drawableHoverText;
    private volatile DrawablePrim drawablePrim;
    private final DrawableStore drawableStore;
    private volatile PrimFlexibleInfo flexibleInfo;
    private volatile HoverText hoverText;
    private int invisibleCount;
    private int invisibleFrames;
    private boolean isInvisible;
    private final float objCoordsData[];
    private final int objCoordsScale;
    private final SLObjectInfo objInfo;
    private GLQuery occlusionQuery;

    public DrawableObject(DrawableStore drawablestore, SLObjectInfo slobjectinfo, DrawableAvatar drawableavatar)
    {
        occlusionQuery = null;
        isInvisible = false;
        invisibleCount = 0;
        invisibleFrames = 0;
        drawableStore = drawablestore;
        objInfo = slobjectinfo;
        attachedTo = drawableavatar;
        objCoordsData = slobjectinfo.getObjectCoords().getData();
        objCoordsScale = slobjectinfo.getObjectCoords().getElementOffset(1);
        setPrimDrawParams(slobjectinfo.getPrimDrawParams());
        setHoverText(slobjectinfo.getHoverText());
    }

    public final void ApplyJointTranslations(MeshJointTranslations meshjointtranslations)
    {
        DrawablePrim drawableprim = drawablePrim;
        if (drawableprim != null)
        {
            drawableprim.ApplyJointTranslations(meshjointtranslations);
        }
    }

    public final int Draw(RenderContext rendercontext, int i)
    {
        DrawablePrim drawableprim = drawablePrim;
        float af[] = objInfo.worldMatrix;
        PrimFlexibleInfo primflexibleinfo = flexibleInfo;
        if (drawableprim != null && af != null && isInvisible ^ true)
        {
            float f = objCoordsData[objCoordsScale];
            float f1 = objCoordsData[objCoordsScale + 1];
            float f2 = objCoordsData[objCoordsScale + 2];
            rendercontext.glObjWorldPushAndMultMatrixf(af, 0);
            rendercontext.glPushObjectScale(f, f1, f2);
            if (primflexibleinfo != null)
            {
                primflexibleinfo.doFlexibleUpdate(objInfo.getPrimDrawParams().getVolumeParams().FlexiParams, rendercontext.objWorldMatrix.getMatrixData(), rendercontext.objWorldMatrix.getMatrixDataOffset(), f, f1, f2);
            }
            if (rendercontext.hasGL20)
            {
                i = drawableprim.DrawFast20(rendercontext, false, primflexibleinfo, i);
            } else
            {
                i = drawableprim.Draw(rendercontext, false, primflexibleinfo, i);
            }
            rendercontext.glPopObjectScale();
            rendercontext.glObjWorldPopMatrix();
            return i;
        } else
        {
            return 0;
        }
    }

    public void DrawHoverText(RenderContext rendercontext, boolean flag)
    {
        DrawableHoverText drawablehovertext = drawableHoverText;
        HoverText hovertext = hoverText;
        float af[] = objInfo.worldMatrix;
        if (drawablehovertext != null && af != null && hovertext != null)
        {
            float f = objInfo.worldMatrix[12];
            float f1 = objInfo.worldMatrix[13];
            float f2 = objInfo.worldMatrix[14];
            float f3;
            MatrixStack matrixstack;
            if (!flag)
            {
                float f4 = objCoordsData[objCoordsScale];
                float f5 = objCoordsData[objCoordsScale + 1];
                f3 = objCoordsData[objCoordsScale + 2];
                f4 = (Math.max(Math.max(f4, f5), f3) + 0.01F) / 2.0F;
                LLVector3 llvector3 = new LLVector3(rendercontext.frameCamera.x - f, rendercontext.frameCamera.y - f1, rendercontext.frameCamera.z - f2);
                llvector3.normVec();
                llvector3.mul(f4);
                f += llvector3.x;
                f1 += llvector3.y;
                f2 += llvector3.z;
                f3 /= 2.0F;
            } else
            {
                f3 = 0.0F;
            }
            if (flag)
            {
                matrixstack = rendercontext.projectionHUDMatrix;
            } else
            {
                matrixstack = rendercontext.projectionMatrix;
            }
            drawablehovertext.DrawAtWorld(rendercontext, f, f1, f2, f3, matrixstack, true, hovertext.color());
        }
    }

    void DrawIfPicked(RenderContext rendercontext, SLObjectInfo slobjectinfo)
    {
        if (objInfo == slobjectinfo)
        {
            slobjectinfo = drawablePrim;
            float af[] = objInfo.worldMatrix;
            PrimFlexibleInfo primflexibleinfo = flexibleInfo;
            if (slobjectinfo != null && af != null)
            {
                float f = objCoordsData[objCoordsScale];
                float f1 = objCoordsData[objCoordsScale + 1];
                float f2 = objCoordsData[objCoordsScale + 2];
                rendercontext.glObjWorldPushAndMultMatrixf(af, 0);
                if (primflexibleinfo != null)
                {
                    primflexibleinfo.doFlexibleUpdate(objInfo.getPrimDrawParams().getVolumeParams().FlexiParams, rendercontext.objWorldMatrix.getMatrixData(), rendercontext.objWorldMatrix.getMatrixDataOffset(), f, f1, f2);
                }
                rendercontext.glPushObjectScale(f, f1, f2);
                GLES10.glDepthFunc(515);
                slobjectinfo.Draw(rendercontext, true, primflexibleinfo, 3);
                GLES10.glDepthFunc(513);
                rendercontext.glPopObjectScale();
                rendercontext.glObjWorldPopMatrix();
            }
        }
    }

    public final int DrawRigged(RenderContext rendercontext, AvatarSkeleton avatarskeleton, int i)
    {
        DrawablePrim drawableprim = drawablePrim;
        if (drawableprim != null)
        {
            if ((i & 1) != 0)
            {
                drawableprim.UpdateRigged(rendercontext, avatarskeleton);
            }
            if (rendercontext.hasGL20)
            {
                return drawableprim.DrawFast20(rendercontext, false, null, i);
            } else
            {
                return drawableprim.Draw(rendercontext, false, null, i);
            }
        } else
        {
            return 0;
        }
    }

    public final int DrawRigged30(RenderContext rendercontext, int i)
    {
        DrawablePrim drawableprim = drawablePrim;
        if (drawableprim != null && drawableprim.isRiggedMesh())
        {
            return drawableprim.DrawRigged30(rendercontext, i);
        } else
        {
            return 0;
        }
    }

    public void OnResourceReady(Object obj, boolean flag)
    {
        if (obj instanceof DrawablePrim)
        {
            obj = (DrawablePrim)obj;
            drawablePrim = ((DrawablePrim) (obj));
            if (((DrawablePrim) (obj)).isRiggedMesh() && attachedTo != null)
            {
                attachedTo.onRiggedMeshReady(this);
            }
        }
    }

    public ObjectIntersectInfo PickObject(RenderContext rendercontext, float f, float f1, float f2)
    {
        Object obj;
        Object aobj[];
        obj = drawablePrim;
        aobj = objInfo.worldMatrix;
        if (obj == null || aobj == null) goto _L2; else goto _L1
_L1:
        float f3;
        float f4;
        float f5;
        float af[];
        float af1[];
        int ai[];
        ai = rendercontext.viewportRect;
        af = new float[32];
        af1 = new float[6];
        f1 = (float)ai[3] - f1;
        f3 = objCoordsData[objCoordsScale];
        f4 = objCoordsData[objCoordsScale + 1];
        f5 = objCoordsData[objCoordsScale + 2];
        rendercontext.glObjWorldPushAndMultMatrixf(((float []) (aobj)), 0);
        if (!rendercontext.hasGL20) goto _L4; else goto _L3
_L3:
        Matrix.scaleM(af, 0, rendercontext.objWorldMatrix.getMatrixData(), rendercontext.objWorldMatrix.getMatrixDataOffset(), f3, f4, f5);
        RenderContext.gluUnProject(f, f1, 0.0F, af, 0, rendercontext.modelViewMatrix.getMatrixData(), rendercontext.modelViewMatrix.getMatrixDataOffset(), ai, 0, af1, 0);
        RenderContext.gluUnProject(f, f1, 1.0F, af, 0, rendercontext.modelViewMatrix.getMatrixData(), rendercontext.modelViewMatrix.getMatrixDataOffset(), ai, 0, af1, 3);
_L11:
        int i;
        boolean flag1;
        rendercontext.glObjWorldPopMatrix();
        ai = new LLVector3(af1[0], af1[1], af1[2]);
        af1 = new LLVector3(af1[3], af1[4], af1[5]);
        flag1 = false;
        aobj = CollisionBox.getInstance().vertices;
        i = 0;
_L9:
        boolean flag = flag1;
        if (i >= 12) goto _L6; else goto _L5
_L5:
        if (GLRayTrace.intersect_RayTriangle(ai, af1, ((LLVector3 []) (aobj)), i * 3) == null) goto _L8; else goto _L7
_L7:
        flag = true;
_L6:
        if (flag)
        {
            obj = ((DrawablePrim) (obj)).IntersectRay(ai, af1);
            if (obj != null)
            {
                f = GLRayTrace.getIntersectionDepth(rendercontext, ((IntersectInfo) (obj)).intersectPoint, af);
                if (f >= f2)
                {
                    return new ObjectIntersectInfo(((IntersectInfo) (obj)), objInfo, f);
                }
            }
        }
        break; /* Loop/switch isn't completed */
_L4:
        Matrix.scaleM(af, 16, rendercontext.objWorldMatrix.getMatrixData(), rendercontext.objWorldMatrix.getMatrixDataOffset(), f3, f4, f5);
        Matrix.multiplyMM(af, 0, rendercontext.modelViewMatrix.getMatrixData(), rendercontext.modelViewMatrix.getMatrixDataOffset(), af, 16);
        MatrixStack matrixstack = rendercontext.getActiveProjectionMatrix();
        if (matrixstack != null)
        {
            RenderContext.gluUnProject(f, f1, 0.0F, af, 0, matrixstack.getMatrixData(), matrixstack.getMatrixDataOffset(), ai, 0, af1, 0);
            RenderContext.gluUnProject(f, f1, 1.0F, af, 0, matrixstack.getMatrixData(), matrixstack.getMatrixDataOffset(), ai, 0, af1, 3);
        }
        continue; /* Loop/switch isn't completed */
_L8:
        i++;
        if (true) goto _L9; else goto _L2
_L2:
        return null;
        if (true) goto _L11; else goto _L10
_L10:
    }

    final void TestOcclusion(RenderContext rendercontext, float af[])
    {
        float af1[] = objInfo.worldMatrix;
        if (af1 == null) goto _L2; else goto _L1
_L1:
        if (occlusionQuery == null)
        {
            occlusionQuery = new GLQuery(rendercontext.glResourceManager);
        }
        if (isInvisible && invisibleFrames <= 10) goto _L4; else goto _L3
_L3:
        com.lumiyaviewer.lumiya.render.glres.GLQuery.OcclusionQueryResult occlusionqueryresult = occlusionQuery.getOcclusionQueryResult();
        if (occlusionqueryresult != com.lumiyaviewer.lumiya.render.glres.GLQuery.OcclusionQueryResult.Invisible) goto _L6; else goto _L5
_L5:
        invisibleFrames = 0;
        if (!isInvisible)
        {
            invisibleCount = invisibleCount + 1;
            if (invisibleCount > 10)
            {
                int i = OpenJPEG.checkFrustrumOcclusion(af, af1, objCoordsData[objCoordsScale], objCoordsData[objCoordsScale + 1], objCoordsData[objCoordsScale + 2]);
                float f;
                float f1;
                float f2;
                if (i == 0)
                {
                    invisibleCount = 0;
                } else
                {
                    Debug.Printf("Occlusion: object seriously invisible %s (frustrumTest %d)", new Object[] {
                        this, Integer.valueOf(i)
                    });
                    isInvisible = true;
                }
            }
        }
_L7:
        if (!occlusionQuery.isQueryRunning())
        {
            f = objCoordsData[objCoordsScale];
            f1 = objCoordsData[objCoordsScale + 1];
            f2 = objCoordsData[objCoordsScale + 2];
            rendercontext.glObjWorldPushAndMultMatrixf(af1, 0);
            rendercontext.glPushObjectScale(f * 1.001F, f1 * 1.001F, f2 * 1.001F);
            rendercontext.boundingBox.OcclusionQuery(rendercontext, occlusionQuery);
            rendercontext.glPopObjectScale();
            rendercontext.glObjWorldPopMatrix();
        }
_L2:
        return;
_L6:
        if (occlusionqueryresult == com.lumiyaviewer.lumiya.render.glres.GLQuery.OcclusionQueryResult.Visible)
        {
            invisibleCount = 0;
            if (isInvisible)
            {
                Debug.Printf("Occlusion: object visible again %s", new Object[] {
                    this
                });
                isInvisible = false;
            }
        }
        if (true) goto _L7; else goto _L4
_L4:
        invisibleFrames = invisibleFrames + 1;
        return;
    }

    public SLObjectInfo getObjectInfo()
    {
        return objInfo;
    }

    public final boolean hasExtendedBones()
    {
        DrawablePrim drawableprim = drawablePrim;
        if (drawableprim != null)
        {
            return drawableprim.hasExtendedBones();
        } else
        {
            return false;
        }
    }

    public final boolean isRiggedMesh()
    {
        DrawablePrim drawableprim = drawablePrim;
        if (drawableprim != null)
        {
            return drawableprim.isRiggedMesh();
        } else
        {
            return false;
        }
    }

    public void setHoverText(HoverText hovertext)
    {
        if (Objects.equal(hoverText, hovertext)) goto _L2; else goto _L1
_L1:
        if (hovertext != null) goto _L4; else goto _L3
_L3:
        drawableHoverText = null;
_L6:
        hoverText = hovertext;
_L2:
        return;
_L4:
        if (!hovertext.sameText(hoverText))
        {
            drawableHoverText = new DrawableHoverText(drawableStore.textTextureCache, hovertext.text(), 0);
        }
        if (true) goto _L6; else goto _L5
_L5:
    }

    public void setPrimDrawParams(PrimDrawParams primdrawparams)
    {
        drawableStore.primCache.RequestResource(primdrawparams, this);
        if (primdrawparams.getVolumeParams().isFlexible())
        {
            flexibleInfo = new PrimFlexibleInfo();
            return;
        } else
        {
            flexibleInfo = null;
            return;
        }
    }
}
