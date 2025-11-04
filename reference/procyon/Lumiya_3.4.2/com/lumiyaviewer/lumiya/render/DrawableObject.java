// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render;

import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.slproto.prims.PrimDrawParams;
import com.google.common.base.Objects;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.picking.IntersectInfo;
import com.lumiyaviewer.lumiya.render.picking.GLRayTrace;
import com.lumiyaviewer.lumiya.render.picking.CollisionBox;
import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton;
import android.opengl.GLES10;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.mesh.MeshJointTranslations;
import com.lumiyaviewer.lumiya.render.glres.GLQuery;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.objects.HoverText;
import com.lumiyaviewer.lumiya.slproto.prims.PrimFlexibleInfo;
import com.lumiyaviewer.lumiya.render.drawable.DrawablePrim;
import com.lumiyaviewer.lumiya.render.avatar.DrawableHoverText;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.render.picking.IntersectPickable;

public class DrawableObject implements IntersectPickable, ResourceConsumer
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
    private final float[] objCoordsData;
    private final int objCoordsScale;
    private final SLObjectInfo objInfo;
    private GLQuery occlusionQuery;
    
    public DrawableObject(final DrawableStore drawableStore, final SLObjectInfo objInfo, final DrawableAvatar attachedTo) {
        this.occlusionQuery = null;
        this.isInvisible = false;
        this.invisibleCount = 0;
        this.invisibleFrames = 0;
        this.drawableStore = drawableStore;
        this.objInfo = objInfo;
        this.attachedTo = attachedTo;
        this.objCoordsData = objInfo.getObjectCoords().getData();
        this.objCoordsScale = objInfo.getObjectCoords().getElementOffset(1);
        this.setPrimDrawParams(objInfo.getPrimDrawParams());
        this.setHoverText(objInfo.getHoverText());
    }
    
    public final void ApplyJointTranslations(final MeshJointTranslations meshJointTranslations) {
        final DrawablePrim drawablePrim = this.drawablePrim;
        if (drawablePrim != null) {
            drawablePrim.ApplyJointTranslations(meshJointTranslations);
        }
    }
    
    public final int Draw(final RenderContext renderContext, int n) {
        final DrawablePrim drawablePrim = this.drawablePrim;
        final float[] worldMatrix = this.objInfo.worldMatrix;
        final PrimFlexibleInfo flexibleInfo = this.flexibleInfo;
        if (drawablePrim != null && worldMatrix != null && (this.isInvisible ^ true)) {
            final float n2 = this.objCoordsData[this.objCoordsScale];
            final float n3 = this.objCoordsData[this.objCoordsScale + 1];
            final float n4 = this.objCoordsData[this.objCoordsScale + 2];
            renderContext.glObjWorldPushAndMultMatrixf(worldMatrix, 0);
            renderContext.glPushObjectScale(n2, n3, n4);
            if (flexibleInfo != null) {
                flexibleInfo.doFlexibleUpdate(this.objInfo.getPrimDrawParams().getVolumeParams().FlexiParams, renderContext.objWorldMatrix.getMatrixData(), renderContext.objWorldMatrix.getMatrixDataOffset(), n2, n3, n4);
            }
            if (renderContext.hasGL20) {
                n = drawablePrim.DrawFast20(renderContext, false, flexibleInfo, n);
            }
            else {
                n = drawablePrim.Draw(renderContext, false, flexibleInfo, n);
            }
            renderContext.glPopObjectScale();
            renderContext.glObjWorldPopMatrix();
            return n;
        }
        return 0;
    }
    
    public void DrawHoverText(final RenderContext renderContext, final boolean b) {
        final DrawableHoverText drawableHoverText = this.drawableHoverText;
        final HoverText hoverText = this.hoverText;
        final float[] worldMatrix = this.objInfo.worldMatrix;
        if (drawableHoverText != null && worldMatrix != null && hoverText != null) {
            float n = this.objInfo.worldMatrix[12];
            float n2 = this.objInfo.worldMatrix[13];
            float n3 = this.objInfo.worldMatrix[14];
            float n5;
            if (!b) {
                final float a = this.objCoordsData[this.objCoordsScale];
                final float b2 = this.objCoordsData[this.objCoordsScale + 1];
                final float b3 = this.objCoordsData[this.objCoordsScale + 2];
                final float n4 = (Math.max(Math.max(a, b2), b3) + 0.01f) / 2.0f;
                final LLVector3 llVector3 = new LLVector3(renderContext.frameCamera.x - n, renderContext.frameCamera.y - n2, renderContext.frameCamera.z - n3);
                llVector3.normVec();
                llVector3.mul(n4);
                n += llVector3.x;
                n2 += llVector3.y;
                n3 += llVector3.z;
                n5 = b3 / 2.0f;
            }
            else {
                n5 = 0.0f;
            }
            MatrixStack matrixStack;
            if (b) {
                matrixStack = renderContext.projectionHUDMatrix;
            }
            else {
                matrixStack = renderContext.projectionMatrix;
            }
            drawableHoverText.DrawAtWorld(renderContext, n, n2, n3, n5, matrixStack, true, hoverText.color());
        }
    }
    
    void DrawIfPicked(final RenderContext renderContext, final SLObjectInfo slObjectInfo) {
        if (this.objInfo == slObjectInfo) {
            final DrawablePrim drawablePrim = this.drawablePrim;
            final float[] worldMatrix = this.objInfo.worldMatrix;
            final PrimFlexibleInfo flexibleInfo = this.flexibleInfo;
            if (drawablePrim != null && worldMatrix != null) {
                final float n = this.objCoordsData[this.objCoordsScale];
                final float n2 = this.objCoordsData[this.objCoordsScale + 1];
                final float n3 = this.objCoordsData[this.objCoordsScale + 2];
                renderContext.glObjWorldPushAndMultMatrixf(worldMatrix, 0);
                if (flexibleInfo != null) {
                    flexibleInfo.doFlexibleUpdate(this.objInfo.getPrimDrawParams().getVolumeParams().FlexiParams, renderContext.objWorldMatrix.getMatrixData(), renderContext.objWorldMatrix.getMatrixDataOffset(), n, n2, n3);
                }
                renderContext.glPushObjectScale(n, n2, n3);
                GLES10.glDepthFunc(515);
                drawablePrim.Draw(renderContext, true, flexibleInfo, 3);
                GLES10.glDepthFunc(513);
                renderContext.glPopObjectScale();
                renderContext.glObjWorldPopMatrix();
            }
        }
    }
    
    public final int DrawRigged(final RenderContext renderContext, final AvatarSkeleton avatarSkeleton, final int n) {
        final DrawablePrim drawablePrim = this.drawablePrim;
        if (drawablePrim == null) {
            return 0;
        }
        if ((n & 0x1) != 0x0) {
            drawablePrim.UpdateRigged(renderContext, avatarSkeleton);
        }
        if (renderContext.hasGL20) {
            return drawablePrim.DrawFast20(renderContext, false, null, n);
        }
        return drawablePrim.Draw(renderContext, false, null, n);
    }
    
    public final int DrawRigged30(final RenderContext renderContext, final int n) {
        final DrawablePrim drawablePrim = this.drawablePrim;
        if (drawablePrim != null && drawablePrim.isRiggedMesh()) {
            return drawablePrim.DrawRigged30(renderContext, n);
        }
        return 0;
    }
    
    @Override
    public void OnResourceReady(final Object o, final boolean b) {
        if (o instanceof DrawablePrim) {
            final DrawablePrim drawablePrim = (DrawablePrim)o;
            this.drawablePrim = drawablePrim;
            if (drawablePrim.isRiggedMesh() && this.attachedTo != null) {
                this.attachedTo.onRiggedMeshReady(this);
            }
        }
    }
    
    @Override
    public ObjectIntersectInfo PickObject(final RenderContext renderContext, float intersectionDepth, float n, final float n2) {
        final DrawablePrim drawablePrim = this.drawablePrim;
        final float[] worldMatrix = this.objInfo.worldMatrix;
        if (drawablePrim != null && worldMatrix != null) {
            final int[] viewportRect = renderContext.viewportRect;
            final float[] array = new float[32];
            final float[] array2 = new float[6];
            final float n3 = viewportRect[3] - n;
            final float n4 = this.objCoordsData[this.objCoordsScale];
            n = this.objCoordsData[this.objCoordsScale + 1];
            final float n5 = this.objCoordsData[this.objCoordsScale + 2];
            renderContext.glObjWorldPushAndMultMatrixf(worldMatrix, 0);
            if (renderContext.hasGL20) {
                Matrix.scaleM(array, 0, renderContext.objWorldMatrix.getMatrixData(), renderContext.objWorldMatrix.getMatrixDataOffset(), n4, n, n5);
                RenderContext.gluUnProject(intersectionDepth, n3, 0.0f, array, 0, renderContext.modelViewMatrix.getMatrixData(), renderContext.modelViewMatrix.getMatrixDataOffset(), viewportRect, 0, array2, 0);
                RenderContext.gluUnProject(intersectionDepth, n3, 1.0f, array, 0, renderContext.modelViewMatrix.getMatrixData(), renderContext.modelViewMatrix.getMatrixDataOffset(), viewportRect, 0, array2, 3);
            }
            else {
                Matrix.scaleM(array, 16, renderContext.objWorldMatrix.getMatrixData(), renderContext.objWorldMatrix.getMatrixDataOffset(), n4, n, n5);
                Matrix.multiplyMM(array, 0, renderContext.modelViewMatrix.getMatrixData(), renderContext.modelViewMatrix.getMatrixDataOffset(), array, 16);
                final MatrixStack activeProjectionMatrix = renderContext.getActiveProjectionMatrix();
                if (activeProjectionMatrix != null) {
                    RenderContext.gluUnProject(intersectionDepth, n3, 0.0f, array, 0, activeProjectionMatrix.getMatrixData(), activeProjectionMatrix.getMatrixDataOffset(), viewportRect, 0, array2, 0);
                    RenderContext.gluUnProject(intersectionDepth, n3, 1.0f, array, 0, activeProjectionMatrix.getMatrixData(), activeProjectionMatrix.getMatrixDataOffset(), viewportRect, 0, array2, 3);
                }
            }
            renderContext.glObjWorldPopMatrix();
            final LLVector3 llVector3 = new LLVector3(array2[0], array2[1], array2[2]);
            final LLVector3 llVector4 = new LLVector3(array2[3], array2[4], array2[5]);
            final int n6 = 0;
            final LLVector3[] vertices = CollisionBox.getInstance().vertices;
            int n7 = 0;
            int n8;
            while (true) {
                n8 = n6;
                if (n7 >= 12) {
                    break;
                }
                if (GLRayTrace.intersect_RayTriangle(llVector3, llVector4, vertices, n7 * 3) != null) {
                    n8 = 1;
                    break;
                }
                ++n7;
            }
            if (n8 != 0) {
                final IntersectInfo intersectRay = drawablePrim.IntersectRay(llVector3, llVector4);
                if (intersectRay != null) {
                    intersectionDepth = GLRayTrace.getIntersectionDepth(renderContext, intersectRay.intersectPoint, array);
                    if (intersectionDepth >= n2) {
                        return new ObjectIntersectInfo(intersectRay, this.objInfo, intersectionDepth);
                    }
                }
            }
        }
        return null;
    }
    
    final void TestOcclusion(final RenderContext renderContext, final float[] array) {
        final float[] worldMatrix = this.objInfo.worldMatrix;
        if (worldMatrix != null) {
            if (this.occlusionQuery == null) {
                this.occlusionQuery = new GLQuery(renderContext.glResourceManager);
            }
            if (!this.isInvisible || this.invisibleFrames > 10) {
                final GLQuery.OcclusionQueryResult occlusionQueryResult = this.occlusionQuery.getOcclusionQueryResult();
                if (occlusionQueryResult == GLQuery.OcclusionQueryResult.Invisible) {
                    this.invisibleFrames = 0;
                    if (!this.isInvisible) {
                        ++this.invisibleCount;
                        if (this.invisibleCount > 10) {
                            final int checkFrustrumOcclusion = OpenJPEG.checkFrustrumOcclusion(array, worldMatrix, this.objCoordsData[this.objCoordsScale], this.objCoordsData[this.objCoordsScale + 1], this.objCoordsData[this.objCoordsScale + 2]);
                            if (checkFrustrumOcclusion == 0) {
                                this.invisibleCount = 0;
                            }
                            else {
                                Debug.Printf("Occlusion: object seriously invisible %s (frustrumTest %d)", this, checkFrustrumOcclusion);
                                this.isInvisible = true;
                            }
                        }
                    }
                }
                else if (occlusionQueryResult == GLQuery.OcclusionQueryResult.Visible) {
                    this.invisibleCount = 0;
                    if (this.isInvisible) {
                        Debug.Printf("Occlusion: object visible again %s", this);
                        this.isInvisible = false;
                    }
                }
                if (!this.occlusionQuery.isQueryRunning()) {
                    final float n = this.objCoordsData[this.objCoordsScale];
                    final float n2 = this.objCoordsData[this.objCoordsScale + 1];
                    final float n3 = this.objCoordsData[this.objCoordsScale + 2];
                    renderContext.glObjWorldPushAndMultMatrixf(worldMatrix, 0);
                    renderContext.glPushObjectScale(n * 1.001f, n2 * 1.001f, n3 * 1.001f);
                    renderContext.boundingBox.OcclusionQuery(renderContext, this.occlusionQuery);
                    renderContext.glPopObjectScale();
                    renderContext.glObjWorldPopMatrix();
                }
            }
            else {
                ++this.invisibleFrames;
            }
        }
    }
    
    public SLObjectInfo getObjectInfo() {
        return this.objInfo;
    }
    
    public final boolean hasExtendedBones() {
        final DrawablePrim drawablePrim = this.drawablePrim;
        return drawablePrim != null && drawablePrim.hasExtendedBones();
    }
    
    public final boolean isRiggedMesh() {
        final DrawablePrim drawablePrim = this.drawablePrim;
        return drawablePrim != null && drawablePrim.isRiggedMesh();
    }
    
    public void setHoverText(@Nullable final HoverText hoverText) {
        if (!Objects.equal(this.hoverText, hoverText)) {
            if (hoverText == null) {
                this.drawableHoverText = null;
            }
            else if (!hoverText.sameText(this.hoverText)) {
                this.drawableHoverText = new DrawableHoverText(this.drawableStore.textTextureCache, hoverText.text(), 0);
            }
            this.hoverText = hoverText;
        }
    }
    
    public void setPrimDrawParams(final PrimDrawParams primDrawParams) {
        ((ResourceMemoryCache<PrimDrawParams, ResourceType>)this.drawableStore.primCache).RequestResource(primDrawParams, this);
        if (primDrawParams.getVolumeParams().isFlexible()) {
            this.flexibleInfo = new PrimFlexibleInfo();
        }
        else {
            this.flexibleInfo = null;
        }
    }
}
