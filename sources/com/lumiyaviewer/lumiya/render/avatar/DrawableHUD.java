package com.lumiyaviewer.lumiya.render.avatar;

import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.DrawableObject;
import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.render.MatrixStack;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.TouchHUDEvent;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import com.lumiyaviewer.lumiya.render.spatial.DrawEntryList;
import com.lumiyaviewer.lumiya.render.spatial.DrawListObjectEntry;
import com.lumiyaviewer.lumiya.render.spatial.DrawListPrimEntry;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.Vector3Array;
import com.lumiyaviewer.lumiya.utils.LinkedTreeNode;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;

public class DrawableHUD {
    private final DrawableAvatar attachedTo;
    private final SLAttachmentPoint attachmentPoint;
    private final DrawableStore drawableStore;
    private final Set<DrawableObject> hudObjects = Collections.newSetFromMap(new IdentityHashMap());
    private final LLVector3 maxPos = new LLVector3();
    private final LLVector3 minPos = new LLVector3();

    public DrawableHUD(SLAttachmentPoint sLAttachmentPoint, DrawEntryList drawEntryList, SLObjectInfo sLObjectInfo, DrawableStore drawableStore2, DrawableAvatar drawableAvatar) {
        this.attachmentPoint = sLAttachmentPoint;
        this.drawableStore = drawableStore2;
        this.attachedTo = drawableAvatar;
        addObject(drawEntryList, sLObjectInfo, new MatrixStack(), true);
    }

    private void addObject(DrawEntryList drawEntryList, SLObjectInfo sLObjectInfo, MatrixStack matrixStack, boolean z) {
        matrixStack.glPushMatrix();
        processObjectExtents(sLObjectInfo, matrixStack, z);
        DrawListObjectEntry drawListEntry = sLObjectInfo.getDrawListEntry();
        drawEntryList.addEntry(drawListEntry);
        if (drawListEntry instanceof DrawListPrimEntry) {
            this.hudObjects.add(((DrawListPrimEntry) drawListEntry).getDrawableAttachment(this.drawableStore, this.attachedTo));
        }
        for (LinkedTreeNode<SLObjectInfo> firstChild = sLObjectInfo.treeNode.getFirstChild(); firstChild != null; firstChild = firstChild.getNextChild()) {
            SLObjectInfo dataObject = firstChild.getDataObject();
            if (dataObject != null) {
                addObject(drawEntryList, dataObject, matrixStack, false);
            }
        }
        matrixStack.glPopMatrix();
    }

    private void processObjectExtents(SLObjectInfo sLObjectInfo, MatrixStack matrixStack, boolean z) {
        float[] fArr = new float[8];
        Vector3Array objectCoords = sLObjectInfo.getObjectCoords();
        int elementOffset = objectCoords.getElementOffset(0);
        int elementOffset2 = objectCoords.getElementOffset(1);
        float[] data = objectCoords.getData();
        matrixStack.glTranslatef(data[elementOffset + 0], data[elementOffset + 1], data[elementOffset + 2]);
        matrixStack.glMultMatrixf(sLObjectInfo.getRotation().getInverseMatrix(), 0);
        fArr[0] = (-data[elementOffset2 + 0]) / 2.0f;
        fArr[1] = (-data[elementOffset2 + 1]) / 2.0f;
        fArr[2] = (-data[elementOffset2 + 2]) / 2.0f;
        fArr[3] = 1.0f;
        Matrix.multiplyMV(fArr, 4, matrixStack.getMatrixData(), matrixStack.getMatrixDataOffset(), fArr, 0);
        if (z) {
            this.minPos.x = fArr[4];
            this.minPos.y = fArr[5];
            this.minPos.z = fArr[6];
            this.maxPos.x = fArr[4];
            this.maxPos.y = fArr[5];
            this.maxPos.z = fArr[6];
        } else {
            this.minPos.x = Math.min(this.minPos.x, fArr[4]);
            this.minPos.y = Math.min(this.minPos.y, fArr[5]);
            this.minPos.z = Math.min(this.minPos.z, fArr[6]);
            this.maxPos.x = Math.max(this.maxPos.x, fArr[4]);
            this.maxPos.y = Math.max(this.maxPos.y, fArr[5]);
            this.maxPos.z = Math.max(this.maxPos.z, fArr[6]);
        }
        fArr[0] = data[elementOffset2 + 0] / 2.0f;
        fArr[1] = data[elementOffset2 + 1] / 2.0f;
        fArr[2] = data[elementOffset2 + 2] / 2.0f;
        fArr[3] = 1.0f;
        Matrix.multiplyMV(fArr, 4, matrixStack.getMatrixData(), matrixStack.getMatrixDataOffset(), fArr, 0);
        this.minPos.x = Math.min(this.minPos.x, fArr[4]);
        this.minPos.y = Math.min(this.minPos.y, fArr[5]);
        this.minPos.z = Math.min(this.minPos.z, fArr[6]);
        this.maxPos.x = Math.max(this.maxPos.x, fArr[4]);
        this.maxPos.y = Math.max(this.maxPos.y, fArr[5]);
        this.maxPos.z = Math.max(this.maxPos.z, fArr[6]);
    }

    public ObjectIntersectInfo Draw(RenderContext renderContext, float f, float f2, float f3, TouchHUDEvent touchHUDEvent, boolean z) {
        ObjectIntersectInfo objectIntersectInfo;
        ObjectIntersectInfo objectIntersectInfo2 = null;
        renderContext.glModelPushMatrix();
        float f4 = (this.minPos.y + this.maxPos.y) / 2.0f;
        float f5 = (this.minPos.z + this.maxPos.z) / 2.0f;
        float max = Math.max(this.maxPos.y - this.minPos.y, this.maxPos.z - this.minPos.z);
        if (max > 0.001f) {
            float f6 = (1.0f / max) * f;
            renderContext.glModelScalef(1.0f, f6, f6);
        }
        renderContext.glModelTranslatef(-this.minPos.x, (-f4) + f2, (-f5) + f3);
        Iterator<T> it = this.hudObjects.iterator();
        while (true) {
            objectIntersectInfo = objectIntersectInfo2;
            if (!it.hasNext()) {
                break;
            }
            DrawableObject drawableObject = (DrawableObject) it.next();
            if (z) {
                drawableObject.DrawHoverText(renderContext, true);
                objectIntersectInfo2 = objectIntersectInfo;
            } else {
                drawableObject.Draw(renderContext, 3);
                if (touchHUDEvent == null || (objectIntersectInfo2 = drawableObject.PickObject(renderContext, touchHUDEvent.x, touchHUDEvent.y, Float.NEGATIVE_INFINITY)) == null || (objectIntersectInfo != null && objectIntersectInfo2.pickDepth >= objectIntersectInfo.pickDepth)) {
                    objectIntersectInfo2 = objectIntersectInfo;
                }
            }
        }
        renderContext.glModelPopMatrix();
        if (!(touchHUDEvent == null || objectIntersectInfo == null)) {
            Debug.Printf("TouchHUD event: pickDepth %f objID %d", Float.valueOf(objectIntersectInfo.pickDepth), Integer.valueOf(objectIntersectInfo.objInfo.localID));
            if (objectIntersectInfo.intersectInfo != null) {
                Debug.Printf("TouchHUD event: intersect face %d uv (%f, %f) st (%f, %f)", Integer.valueOf(objectIntersectInfo.intersectInfo.faceID), Float.valueOf(objectIntersectInfo.intersectInfo.u), Float.valueOf(objectIntersectInfo.intersectInfo.v), Float.valueOf(objectIntersectInfo.intersectInfo.s), Float.valueOf(objectIntersectInfo.intersectInfo.t));
            }
        }
        return objectIntersectInfo;
    }

    public SLAttachmentPoint getAttachmentPoint() {
        return this.attachmentPoint;
    }
}
