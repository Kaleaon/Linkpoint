package com.lumiyaviewer.lumiya.render;

import android.opengl.GLES10;
import android.opengl.Matrix;
import android.support.v4.view.InputDeviceCompat;
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
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations;
import com.lumiyaviewer.lumiya.slproto.objects.HoverText;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams;
import com.lumiyaviewer.lumiya.slproto.prims.PrimFlexibleInfo;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import javax.annotation.Nullable;

public class DrawableObject implements IntersectPickable, ResourceConsumer {
    private static final int INVISIBLE_FRAMES_APPEAR = 10;
    private static final int INVISIBLE_FRAMES_DISAPPEAR = 10;
    private final DrawableAvatar attachedTo;
    private volatile DrawableHoverText drawableHoverText;
    private volatile DrawablePrim drawablePrim;
    private final DrawableStore drawableStore;
    private volatile PrimFlexibleInfo flexibleInfo;
    private volatile HoverText hoverText;
    private int invisibleCount = 0;
    private int invisibleFrames = 0;
    private boolean isInvisible = false;
    private final float[] objCoordsData;
    private final int objCoordsScale;
    private final SLObjectInfo objInfo;
    private GLQuery occlusionQuery = null;

    public DrawableObject(DrawableStore drawableStore2, SLObjectInfo sLObjectInfo, DrawableAvatar drawableAvatar) {
        this.drawableStore = drawableStore2;
        this.objInfo = sLObjectInfo;
        this.attachedTo = drawableAvatar;
        this.objCoordsData = sLObjectInfo.getObjectCoords().getData();
        this.objCoordsScale = sLObjectInfo.getObjectCoords().getElementOffset(1);
        setPrimDrawParams(sLObjectInfo.getPrimDrawParams());
        setHoverText(sLObjectInfo.getHoverText());
    }

    public final void ApplyJointTranslations(MeshJointTranslations meshJointTranslations) {
        DrawablePrim drawablePrim2 = this.drawablePrim;
        if (drawablePrim2 != null) {
            drawablePrim2.ApplyJointTranslations(meshJointTranslations);
        }
    }

    public final int Draw(RenderContext renderContext, int i) {
        DrawablePrim drawablePrim2 = this.drawablePrim;
        float[] fArr = this.objInfo.worldMatrix;
        PrimFlexibleInfo primFlexibleInfo = this.flexibleInfo;
        if (drawablePrim2 == null || fArr == null || !(!this.isInvisible)) {
            return 0;
        }
        float f = this.objCoordsData[this.objCoordsScale];
        float f2 = this.objCoordsData[this.objCoordsScale + 1];
        float f3 = this.objCoordsData[this.objCoordsScale + 2];
        renderContext.glObjWorldPushAndMultMatrixf(fArr, 0);
        renderContext.glPushObjectScale(f, f2, f3);
        if (primFlexibleInfo != null) {
            primFlexibleInfo.doFlexibleUpdate(this.objInfo.getPrimDrawParams().getVolumeParams().FlexiParams, renderContext.objWorldMatrix.getMatrixData(), renderContext.objWorldMatrix.getMatrixDataOffset(), f, f2, f3);
        }
        int DrawFast20 = renderContext.hasGL20 ? drawablePrim2.DrawFast20(renderContext, false, primFlexibleInfo, i) : drawablePrim2.Draw(renderContext, false, primFlexibleInfo, i);
        renderContext.glPopObjectScale();
        renderContext.glObjWorldPopMatrix();
        return DrawFast20;
    }

    public void DrawHoverText(RenderContext renderContext, boolean z) {
        float f;
        DrawableHoverText drawableHoverText2 = this.drawableHoverText;
        HoverText hoverText2 = this.hoverText;
        float[] fArr = this.objInfo.worldMatrix;
        if (drawableHoverText2 != null && fArr != null && hoverText2 != null) {
            float f2 = this.objInfo.worldMatrix[12];
            float f3 = this.objInfo.worldMatrix[13];
            float f4 = this.objInfo.worldMatrix[14];
            if (!z) {
                float f5 = this.objCoordsData[this.objCoordsScale];
                float f6 = this.objCoordsData[this.objCoordsScale + 1];
                float f7 = this.objCoordsData[this.objCoordsScale + 2];
                LLVector3 lLVector3 = new LLVector3(renderContext.frameCamera.x - f2, renderContext.frameCamera.y - f3, renderContext.frameCamera.z - f4);
                lLVector3.normVec();
                lLVector3.mul((Math.max(Math.max(f5, f6), f7) + 0.01f) / 2.0f);
                f2 += lLVector3.x;
                f3 += lLVector3.y;
                f4 += lLVector3.z;
                f = f7 / 2.0f;
            } else {
                f = 0.0f;
            }
            drawableHoverText2.DrawAtWorld(renderContext, f2, f3, f4, f, z ? renderContext.projectionHUDMatrix : renderContext.projectionMatrix, true, hoverText2.color());
        }
    }

    /* access modifiers changed from: package-private */
    public void DrawIfPicked(RenderContext renderContext, SLObjectInfo sLObjectInfo) {
        if (this.objInfo == sLObjectInfo) {
            DrawablePrim drawablePrim2 = this.drawablePrim;
            float[] fArr = this.objInfo.worldMatrix;
            PrimFlexibleInfo primFlexibleInfo = this.flexibleInfo;
            if (drawablePrim2 != null && fArr != null) {
                float f = this.objCoordsData[this.objCoordsScale];
                float f2 = this.objCoordsData[this.objCoordsScale + 1];
                float f3 = this.objCoordsData[this.objCoordsScale + 2];
                renderContext.glObjWorldPushAndMultMatrixf(fArr, 0);
                if (primFlexibleInfo != null) {
                    primFlexibleInfo.doFlexibleUpdate(this.objInfo.getPrimDrawParams().getVolumeParams().FlexiParams, renderContext.objWorldMatrix.getMatrixData(), renderContext.objWorldMatrix.getMatrixDataOffset(), f, f2, f3);
                }
                renderContext.glPushObjectScale(f, f2, f3);
                GLES10.glDepthFunc(515);
                drawablePrim2.Draw(renderContext, true, primFlexibleInfo, 3);
                GLES10.glDepthFunc(InputDeviceCompat.SOURCE_DPAD);
                renderContext.glPopObjectScale();
                renderContext.glObjWorldPopMatrix();
            }
        }
    }

    public final int DrawRigged(RenderContext renderContext, AvatarSkeleton avatarSkeleton, int i) {
        DrawablePrim drawablePrim2 = this.drawablePrim;
        if (drawablePrim2 == null) {
            return 0;
        }
        if ((i & 1) != 0) {
            drawablePrim2.UpdateRigged(renderContext, avatarSkeleton);
        }
        return renderContext.hasGL20 ? drawablePrim2.DrawFast20(renderContext, false, (PrimFlexibleInfo) null, i) : drawablePrim2.Draw(renderContext, false, (PrimFlexibleInfo) null, i);
    }

    public final int DrawRigged30(RenderContext renderContext, int i) {
        DrawablePrim drawablePrim2 = this.drawablePrim;
        if (drawablePrim2 == null || !drawablePrim2.isRiggedMesh()) {
            return 0;
        }
        return drawablePrim2.DrawRigged30(renderContext, i);
    }

    public void OnResourceReady(Object obj, boolean z) {
        if (obj instanceof DrawablePrim) {
            DrawablePrim drawablePrim2 = (DrawablePrim) obj;
            this.drawablePrim = drawablePrim2;
            if (drawablePrim2.isRiggedMesh() && this.attachedTo != null) {
                this.attachedTo.onRiggedMeshReady(this);
            }
        }
    }

    public ObjectIntersectInfo PickObject(RenderContext renderContext, float f, float f2, float f3) {
        IntersectInfo IntersectRay;
        DrawablePrim drawablePrim2 = this.drawablePrim;
        float[] fArr = this.objInfo.worldMatrix;
        if (drawablePrim2 == null || fArr == null) {
            return null;
        }
        int[] iArr = renderContext.viewportRect;
        float[] fArr2 = new float[32];
        float[] fArr3 = new float[6];
        float f4 = ((float) iArr[3]) - f2;
        float f5 = this.objCoordsData[this.objCoordsScale];
        float f6 = this.objCoordsData[this.objCoordsScale + 1];
        float f7 = this.objCoordsData[this.objCoordsScale + 2];
        renderContext.glObjWorldPushAndMultMatrixf(fArr, 0);
        if (renderContext.hasGL20) {
            Matrix.scaleM(fArr2, 0, renderContext.objWorldMatrix.getMatrixData(), renderContext.objWorldMatrix.getMatrixDataOffset(), f5, f6, f7);
            RenderContext.gluUnProject(f, f4, 0.0f, fArr2, 0, renderContext.modelViewMatrix.getMatrixData(), renderContext.modelViewMatrix.getMatrixDataOffset(), iArr, 0, fArr3, 0);
            RenderContext.gluUnProject(f, f4, 1.0f, fArr2, 0, renderContext.modelViewMatrix.getMatrixData(), renderContext.modelViewMatrix.getMatrixDataOffset(), iArr, 0, fArr3, 3);
        } else {
            Matrix.scaleM(fArr2, 16, renderContext.objWorldMatrix.getMatrixData(), renderContext.objWorldMatrix.getMatrixDataOffset(), f5, f6, f7);
            Matrix.multiplyMM(fArr2, 0, renderContext.modelViewMatrix.getMatrixData(), renderContext.modelViewMatrix.getMatrixDataOffset(), fArr2, 16);
            MatrixStack activeProjectionMatrix = renderContext.getActiveProjectionMatrix();
            if (activeProjectionMatrix != null) {
                RenderContext.gluUnProject(f, f4, 0.0f, fArr2, 0, activeProjectionMatrix.getMatrixData(), activeProjectionMatrix.getMatrixDataOffset(), iArr, 0, fArr3, 0);
                RenderContext.gluUnProject(f, f4, 1.0f, fArr2, 0, activeProjectionMatrix.getMatrixData(), activeProjectionMatrix.getMatrixDataOffset(), iArr, 0, fArr3, 3);
            }
        }
        renderContext.glObjWorldPopMatrix();
        LLVector3 lLVector3 = new LLVector3(fArr3[0], fArr3[1], fArr3[2]);
        LLVector3 lLVector32 = new LLVector3(fArr3[3], fArr3[4], fArr3[5]);
        boolean z = false;
        LLVector3[] lLVector3Arr = CollisionBox.getInstance().vertices;
        int i = 0;
        while (true) {
            if (i >= 12) {
                break;
            } else if (GLRayTrace.intersect_RayTriangle(lLVector3, lLVector32, lLVector3Arr, i * 3) != null) {
                z = true;
                break;
            } else {
                i++;
            }
        }
        if (!z || (IntersectRay = drawablePrim2.IntersectRay(lLVector3, lLVector32)) == null) {
            return null;
        }
        float intersectionDepth = GLRayTrace.getIntersectionDepth(renderContext, IntersectRay.intersectPoint, fArr2);
        if (intersectionDepth >= f3) {
            return new ObjectIntersectInfo(IntersectRay, this.objInfo, intersectionDepth);
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public final void TestOcclusion(RenderContext renderContext, float[] fArr) {
        float[] fArr2 = this.objInfo.worldMatrix;
        if (fArr2 != null) {
            if (this.occlusionQuery == null) {
                this.occlusionQuery = new GLQuery(renderContext.glResourceManager);
            }
            if (!this.isInvisible || this.invisibleFrames > 10) {
                GLQuery.OcclusionQueryResult occlusionQueryResult = this.occlusionQuery.getOcclusionQueryResult();
                if (occlusionQueryResult == GLQuery.OcclusionQueryResult.Invisible) {
                    this.invisibleFrames = 0;
                    if (!this.isInvisible) {
                        this.invisibleCount++;
                        if (this.invisibleCount > 10) {
                            int checkFrustrumOcclusion = OpenJPEG.checkFrustrumOcclusion(fArr, fArr2, this.objCoordsData[this.objCoordsScale], this.objCoordsData[this.objCoordsScale + 1], this.objCoordsData[this.objCoordsScale + 2]);
                            if (checkFrustrumOcclusion == 0) {
                                this.invisibleCount = 0;
                            } else {
                                Debug.Printf("Occlusion: object seriously invisible %s (frustrumTest %d)", this, Integer.valueOf(checkFrustrumOcclusion));
                                this.isInvisible = true;
                            }
                        }
                    }
                } else if (occlusionQueryResult == GLQuery.OcclusionQueryResult.Visible) {
                    this.invisibleCount = 0;
                    if (this.isInvisible) {
                        Debug.Printf("Occlusion: object visible again %s", this);
                        this.isInvisible = false;
                    }
                }
                if (!this.occlusionQuery.isQueryRunning()) {
                    renderContext.glObjWorldPushAndMultMatrixf(fArr2, 0);
                    renderContext.glPushObjectScale(this.objCoordsData[this.objCoordsScale] * 1.001f, this.objCoordsData[this.objCoordsScale + 1] * 1.001f, this.objCoordsData[this.objCoordsScale + 2] * 1.001f);
                    renderContext.boundingBox.OcclusionQuery(renderContext, this.occlusionQuery);
                    renderContext.glPopObjectScale();
                    renderContext.glObjWorldPopMatrix();
                    return;
                }
                return;
            }
            this.invisibleFrames++;
        }
    }

    public SLObjectInfo getObjectInfo() {
        return this.objInfo;
    }

    public final boolean hasExtendedBones() {
        DrawablePrim drawablePrim2 = this.drawablePrim;
        if (drawablePrim2 != null) {
            return drawablePrim2.hasExtendedBones();
        }
        return false;
    }

    public final boolean isRiggedMesh() {
        DrawablePrim drawablePrim2 = this.drawablePrim;
        if (drawablePrim2 != null) {
            return drawablePrim2.isRiggedMesh();
        }
        return false;
    }

    public void setHoverText(@Nullable HoverText hoverText2) {
        if (!Objects.equal(this.hoverText, hoverText2)) {
            if (hoverText2 == null) {
                this.drawableHoverText = null;
            } else if (!hoverText2.sameText(this.hoverText)) {
                this.drawableHoverText = new DrawableHoverText(this.drawableStore.textTextureCache, hoverText2.text(), 0);
            }
            this.hoverText = hoverText2;
        }
    }

    public void setPrimDrawParams(PrimDrawParams primDrawParams) {
        this.drawableStore.primCache.RequestResource(primDrawParams, this);
        if (primDrawParams.getVolumeParams().isFlexible()) {
            this.flexibleInfo = new PrimFlexibleInfo();
        } else {
            this.flexibleInfo = null;
        }
    }
}
