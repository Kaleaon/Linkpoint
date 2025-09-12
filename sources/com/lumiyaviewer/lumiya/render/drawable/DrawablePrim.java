// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.drawable;

import android.opengl.GLES10;
import android.opengl.GLES11;
import android.opengl.GLES20;
import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.lumiya.render.picking.IntersectInfo;
import com.lumiyaviewer.lumiya.render.shaders.FlexiPrimProgram;
import com.lumiyaviewer.lumiya.render.shaders.PrimProgram;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations;
import com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams;
import com.lumiyaviewer.lumiya.slproto.prims.PrimFlexibleInfo;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntryFace;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;

// Referenced classes of package com.lumiyaviewer.lumiya.render.drawable:
//            DrawableGeometry, DrawableFaceTexture

public class DrawablePrim
{

    public static final int RENDER_PASS_ALL = 3;
    public static final int RENDER_PASS_OPAQUE = 1;
    public static final int RENDER_PASS_TRANSPARENT = 2;
    private final int FaceColorsIDs[];
    private final int FaceCount;
    private final DrawableFaceTexture FaceTextures[];
    private final float FaceUVMatrices[];
    private boolean drawingTextureEnabled;
    private boolean firstFace;
    private final boolean isRiggedMesh;
    private final boolean isSingleFace;
    private final boolean riggingFitsGL20;
    private final int singleFaceColor;
    private final float singleFaceMatrix[];
    private final DrawableFaceTexture singleFaceTexture;
    private final DrawableGeometry volumeGeometry;

    public DrawablePrim(PrimDrawParams primdrawparams, DrawableGeometry drawablegeometry)
    {
        volumeGeometry = drawablegeometry;
        boolean flag1 = drawablegeometry.isFacesCombined();
        isRiggedMesh = drawablegeometry.isRiggedMesh();
        Object obj;
        boolean flag;
        if (isRiggedMesh)
        {
            flag = drawablegeometry.riggingFitsGL20();
        } else
        {
            flag = false;
        }
        riggingFitsGL20 = flag;
        FaceCount = drawablegeometry.getFaceCount();
        obj = primdrawparams.getTextures();
        if (obj != null)
        {
            primdrawparams = ((SLTextureEntry) (obj)).GetDefaultTexture();
            if (((SLTextureEntry) (obj)).isSingleFace() && flag1)
            {
                isSingleFace = true;
                singleFaceMatrix = new float[16];
                drawablegeometry = ((SLTextureEntry) (obj)).GetFace(0);
                if (drawablegeometry != null)
                {
                    singleFaceColor = drawablegeometry.getRGBA(primdrawparams);
                    obj = drawablegeometry.getTextureID(primdrawparams);
                    if (obj != null)
                    {
                        singleFaceTexture = new DrawableFaceTexture(DrawableTextureParams.create(((java.util.UUID) (obj)), TextureClass.Prim));
                    } else
                    {
                        singleFaceTexture = null;
                    }
                    initFaceUVMatrix(primdrawparams, drawablegeometry, singleFaceMatrix, 0);
                } else
                {
                    singleFaceColor = 0;
                    singleFaceTexture = null;
                }
                FaceColorsIDs = null;
                FaceTextures = null;
                FaceUVMatrices = null;
            } else
            {
                isSingleFace = false;
                singleFaceColor = 0;
                singleFaceTexture = null;
                singleFaceMatrix = null;
                FaceColorsIDs = new int[FaceCount * 2];
                FaceTextures = new DrawableFaceTexture[FaceCount];
                FaceUVMatrices = new float[FaceCount * 16];
                int i = 0;
                int j = 0;
                while (i < FaceCount) 
                {
                    SLTextureEntryFace sltextureentryface = ((SLTextureEntry) (obj)).GetFace(drawablegeometry.getFaceID(i));
                    if (sltextureentryface != null)
                    {
                        FaceColorsIDs[j] = sltextureentryface.getRGBA(primdrawparams);
                        FaceColorsIDs[j + 1] = 0;
                        Object obj1 = sltextureentryface.getTextureID(primdrawparams);
                        if (obj1 != null)
                        {
                            obj1 = DrawableTextureParams.create(((java.util.UUID) (obj1)), TextureClass.Prim);
                            FaceTextures[i] = new DrawableFaceTexture(((DrawableTextureParams) (obj1)));
                        }
                        initFaceUVMatrix(primdrawparams, sltextureentryface, FaceUVMatrices, i * 16);
                    }
                    j += 2;
                    i++;
                }
            }
            return;
        } else
        {
            isSingleFace = false;
            singleFaceColor = 0;
            singleFaceTexture = null;
            singleFaceMatrix = null;
            FaceColorsIDs = new int[FaceCount * 2];
            FaceTextures = new DrawableFaceTexture[FaceCount];
            FaceUVMatrices = new float[FaceCount * 16];
            return;
        }
    }

    private int DrawFace(RenderContext rendercontext, DrawableGeometry drawablegeometry, GLLoadableBuffer glloadablebuffer, boolean flag, int i, int j, DrawableFaceTexture drawablefacetexture, 
            float af[], int k, int l)
    {
        int i1 = getFaceRenderMask(j, drawablefacetexture);
        if ((i1 & l) == 0)
        {
            return i1;
        }
        boolean flag1 = false;
        if (!flag)
        {
            if (rendercontext.hasGL20)
            {
                GLES20.glUniform4f(rendercontext.curPrimProgram.vColor, (float)(255 - (j >> 0 & 0xff)) / 255F, (float)(255 - (j >> 8 & 0xff)) / 255F, (float)(255 - (j >> 16 & 0xff)) / 255F, (float)(255 - (j >> 24 & 0xff)) / 255F);
            } else
            {
                GLES10.glColor4f((float)(255 - (j >> 0 & 0xff)) / 255F, (float)(255 - (j >> 8 & 0xff)) / 255F, (float)(255 - (j >> 16 & 0xff)) / 255F, (float)(255 - (j >> 24 & 0xff)) / 255F);
            }
            flag = flag1;
            if (drawablefacetexture != null)
            {
                flag = flag1;
                if (drawablefacetexture.GLDraw(rendercontext))
                {
                    flag = true;
                }
            }
        } else
        if (rendercontext.hasGL20)
        {
            GLES20.glUniform4f(rendercontext.curPrimProgram.vColor, 1.0F, 0.0F, 0.0F, 0.6F);
            flag = flag1;
        } else
        {
            GLES10.glColor4f(1.0F, 0.0F, 0.0F, 0.6F);
            flag = flag1;
        }
        if (flag != drawingTextureEnabled || firstFace)
        {
            if (rendercontext.hasGL20)
            {
                if (!flag)
                {
                    GLES20.glBindTexture(3553, 0);
                    rendercontext.curPrimProgram.setTextureEnabled(false);
                } else
                {
                    rendercontext.curPrimProgram.setTextureEnabled(true);
                }
            } else
            if (flag)
            {
                GLES10.glEnable(3553);
                GLES10.glEnableClientState(32888);
            } else
            {
                GLES10.glDisable(3553);
                GLES10.glDisableClientState(32888);
            }
            drawingTextureEnabled = flag;
            firstFace = false;
        }
        if (rendercontext.hasGL20)
        {
            GLES20.glUniformMatrix4fv(rendercontext.curPrimProgram.uTexMatrix, 1, false, af, k);
            if (i == -1)
            {
                drawablegeometry.GLDrawAll20(rendercontext);
                return i1;
            } else
            {
                drawablegeometry.GLDrawFace20(rendercontext, i);
                return i1;
            }
        }
        GLES11.glMatrixMode(5890);
        GLES11.glPushMatrix();
        GLES11.glLoadMatrixf(af, k);
        if (i == -1)
        {
            drawablegeometry.GLDrawAll10(rendercontext);
        } else
        {
            drawablegeometry.GLDrawFace10(rendercontext, i, glloadablebuffer);
        }
        GLES11.glPopMatrix();
        GLES11.glMatrixMode(5888);
        return i1;
    }

    private int DrawFaceFast20(RenderContext rendercontext, DrawableGeometry drawablegeometry, int i, int j, DrawableFaceTexture drawablefacetexture, float af[], int k, 
            int l)
    {
        int i1 = getFaceRenderMask(j, drawablefacetexture);
        if ((i1 & l) == 0)
        {
            return i1;
        }
        GLES20.glUniform4f(rendercontext.curPrimProgram.vColor, (float)(255 - (j >> 0 & 0xff)) / 255F, (float)(255 - (j >> 8 & 0xff)) / 255F, (float)(255 - (j >> 16 & 0xff)) / 255F, (float)(255 - (j >> 24 & 0xff)) / 255F);
        rendercontext.bindFaceTexture(drawablefacetexture);
        GLES20.glUniformMatrix4fv(rendercontext.curPrimProgram.uTexMatrix, 1, false, af, k);
        if (i == -1)
        {
            drawablegeometry.GLDrawAll20(rendercontext);
            return i1;
        } else
        {
            drawablegeometry.GLDrawFace20(rendercontext, i);
            return i1;
        }
    }

    private int getFaceRenderMask(int i, DrawableFaceTexture drawablefacetexture)
    {
        boolean flag = true;
        boolean flag1 = false;
        if ((i & 0xff000000) == 0xff000000)
        {
            return 0;
        }
        if ((i & 0xff000000) != 0)
        {
            flag1 = true;
        }
        boolean flag2 = flag1;
        if (!flag1)
        {
            flag2 = flag1;
            if (drawablefacetexture != null)
            {
                flag2 = drawablefacetexture.hasAlphaLayer();
            }
        }
        i = ((flag) ? 1 : 0);
        if (flag2)
        {
            i = 2;
        }
        return i;
    }

    private void initFaceUVMatrix(SLTextureEntryFace sltextureentryface, SLTextureEntryFace sltextureentryface1, float af[], int i)
    {
        float af1[] = new float[16];
        Matrix.setIdentityM(af1, 0);
        Matrix.translateM(af1, 0, sltextureentryface1.getOffsetU(sltextureentryface) + 0.5F, sltextureentryface1.getOffsetV(sltextureentryface) + 0.5F, 0.0F);
        Matrix.scaleM(af1, 0, sltextureentryface1.getRepeatU(sltextureentryface), sltextureentryface1.getRepeatV(sltextureentryface), 1.0F);
        Matrix.rotateM(af, i, af1, 0, sltextureentryface1.getRotation(sltextureentryface) / 0.01745329F, 0.0F, 0.0F, -1F);
        Matrix.translateM(af, i, -0.5F, -0.5F, 0.0F);
    }

    public final void ApplyJointTranslations(MeshJointTranslations meshjointtranslations)
    {
        if (isRiggedMesh)
        {
            volumeGeometry.ApplyJointTranslations(meshjointtranslations);
        }
    }

    public final int Draw(RenderContext rendercontext, boolean flag, PrimFlexibleInfo primflexibleinfo, int i)
    {
        DrawableGeometry drawablegeometry = volumeGeometry;
        firstFace = true;
        if (rendercontext.hasGL20)
        {
            float af[];
            if (primflexibleinfo != null)
            {
                af = primflexibleinfo.getMatrices();
            } else
            {
                af = null;
            }
            if (isRiggedMesh && riggingFitsGL20)
            {
                primflexibleinfo = rendercontext.riggedMeshProgram;
            } else
            if (af != null)
            {
                primflexibleinfo = rendercontext.flexiPrimProgram;
            } else
            {
                primflexibleinfo = rendercontext.primProgram;
            }
            rendercontext.curPrimProgram = primflexibleinfo;
            GLES20.glUseProgram(rendercontext.curPrimProgram.getHandle());
            rendercontext.glModelApplyMatrix(rendercontext.curPrimProgram.uMVPMatrix);
            rendercontext.glObjWorldApplyMatrix(rendercontext.curPrimProgram.uObjWorldMatrix);
            rendercontext.glObjScaleApplyVector(rendercontext.curPrimProgram.uObjCoordScale);
            if (af != null && (rendercontext.curPrimProgram instanceof FlexiPrimProgram))
            {
                primflexibleinfo = (FlexiPrimProgram)rendercontext.curPrimProgram;
                GLES20.glUniform1i(((FlexiPrimProgram) (primflexibleinfo)).uNumSectionMatrices, af.length / 16);
                GLES20.glUniformMatrix4fv(((FlexiPrimProgram) (primflexibleinfo)).uSectionMatrices, af.length / 16, false, af, 0);
            }
            primflexibleinfo = drawablegeometry.GLBindBuffers20(rendercontext);
        } else
        {
            primflexibleinfo = drawablegeometry.GLBindBuffers10(rendercontext, primflexibleinfo);
        }
        drawingTextureEnabled = false;
        if (isSingleFace)
        {
            return DrawFace(rendercontext, drawablegeometry, primflexibleinfo, flag, -1, singleFaceColor, singleFaceTexture, singleFaceMatrix, 0, i);
        }
        int k = 0;
        int j;
        int l;
        for (j = 0; k < FaceCount; j = l | j)
        {
            l = DrawFace(rendercontext, drawablegeometry, primflexibleinfo, flag, k, FaceColorsIDs[k * 2], FaceTextures[k], FaceUVMatrices, k * 16, i);
            k++;
        }

        return j;
    }

    public final int DrawFast20(RenderContext rendercontext, boolean flag, PrimFlexibleInfo primflexibleinfo, int i)
    {
        DrawableGeometry drawablegeometry;
        int l;
        boolean flag1 = true;
        float af[] = null;
        drawablegeometry = volumeGeometry;
        if (primflexibleinfo != null)
        {
            af = primflexibleinfo.getMatrices();
        }
        if (i != 1)
        {
            flag1 = false;
        }
        if (isRiggedMesh && riggingFitsGL20)
        {
            primflexibleinfo = rendercontext.riggedMeshProgram;
        } else
        if (af != null)
        {
            if (flag1)
            {
                primflexibleinfo = rendercontext.flexiPrimOpaqueProgram;
            } else
            {
                primflexibleinfo = rendercontext.flexiPrimProgram;
            }
        } else
        if (flag1)
        {
            primflexibleinfo = rendercontext.primOpaqueProgram;
        } else
        {
            primflexibleinfo = rendercontext.primProgram;
        }
        if (rendercontext.curPrimProgram != primflexibleinfo)
        {
            rendercontext.curPrimProgram = primflexibleinfo;
            GLES20.glUseProgram(rendercontext.curPrimProgram.getHandle());
            rendercontext.glModelApplyMatrix(rendercontext.curPrimProgram.uMVPMatrix);
        }
        rendercontext.glObjWorldApplyMatrix(rendercontext.curPrimProgram.uObjWorldMatrix);
        rendercontext.glObjScaleApplyVector(rendercontext.curPrimProgram.uObjCoordScale);
        if (af != null)
        {
            GLES20.glUniform1i(rendercontext.flexiPrimProgram.uNumSectionMatrices, af.length / 16);
            GLES20.glUniformMatrix4fv(rendercontext.flexiPrimProgram.uSectionMatrices, af.length / 16, false, af, 0);
        }
        drawablegeometry.GLBindBuffers20(rendercontext);
        if (!isSingleFace) goto _L2; else goto _L1
_L1:
        l = DrawFaceFast20(rendercontext, drawablegeometry, -1, singleFaceColor, singleFaceTexture, singleFaceMatrix, 0, i);
_L4:
        return l;
_L2:
        int k = 0;
        int j = 0;
        do
        {
            l = j;
            if (k >= FaceCount)
            {
                continue;
            }
            l = DrawFaceFast20(rendercontext, drawablegeometry, k, FaceColorsIDs[k * 2], FaceTextures[k], FaceUVMatrices, k * 16, i);
            k++;
            j |= l;
        } while (true);
        if (true) goto _L4; else goto _L3
_L3:
    }

    public final int DrawRigged30(RenderContext rendercontext, int i)
    {
        DrawableGeometry drawablegeometry = volumeGeometry;
        int l = 0;
        int j = 0;
        int k;
        int i1;
        for (k = 0; l < FaceCount; k = i1)
        {
            int j1 = FaceColorsIDs[l * 2];
            DrawableFaceTexture drawablefacetexture = FaceTextures[l];
            int k1 = getFaceRenderMask(j1, drawablefacetexture);
            i1 = k | k1;
            k = j;
            if ((k1 & i) != 0)
            {
                k = j;
                if (j == 0)
                {
                    drawablegeometry.GLBindBuffersRigged30(rendercontext);
                    k = 1;
                }
                GLES20.glUniform4f(rendercontext.curPrimProgram.vColor, (float)(255 - (j1 >> 0 & 0xff)) / 255F, (float)(255 - (j1 >> 8 & 0xff)) / 255F, (float)(255 - (j1 >> 16 & 0xff)) / 255F, (float)(255 - (j1 >> 24 & 0xff)) / 255F);
                rendercontext.bindFaceTexture(drawablefacetexture);
                GLES20.glUniformMatrix4fv(rendercontext.curPrimProgram.uTexMatrix, 1, false, FaceUVMatrices, l * 16);
                drawablegeometry.GLDrawRiggedFace30(rendercontext, l);
            }
            l++;
            j = k;
        }

        return k;
    }

    public IntersectInfo IntersectRay(LLVector3 llvector3, LLVector3 llvector3_1)
    {
        llvector3 = volumeGeometry.IntersectRay(llvector3, llvector3_1);
        if (llvector3 != null && ((IntersectInfo) (llvector3)).faceKnown)
        {
            if (isSingleFace && singleFaceMatrix != null)
            {
                return new IntersectInfo(llvector3, singleFaceMatrix, 0);
            }
            if (!isSingleFace && FaceUVMatrices != null)
            {
                return new IntersectInfo(llvector3, FaceUVMatrices, ((IntersectInfo) (llvector3)).faceID * 16);
            } else
            {
                return llvector3;
            }
        } else
        {
            return llvector3;
        }
    }

    public final boolean UpdateRigged(RenderContext rendercontext, AvatarSkeleton avatarskeleton)
    {
        if (isRiggedMesh)
        {
            return volumeGeometry.UpdateRigged(rendercontext, avatarskeleton);
        } else
        {
            return false;
        }
    }

    public boolean hasExtendedBones()
    {
        return volumeGeometry.hasExtendedBones();
    }

    public final boolean isRiggedMesh()
    {
        return isRiggedMesh;
    }
}
