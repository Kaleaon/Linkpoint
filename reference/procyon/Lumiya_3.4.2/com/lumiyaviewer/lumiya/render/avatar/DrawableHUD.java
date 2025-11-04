// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.utils.InlineList;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import com.lumiyaviewer.lumiya.render.TouchHUDEvent;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.slproto.types.Vector3Array;
import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.utils.LinkedTreeNode;
import com.lumiyaviewer.lumiya.render.spatial.DrawListObjectEntry;
import com.lumiyaviewer.lumiya.render.spatial.DrawListPrimEntry;
import com.lumiyaviewer.lumiya.render.MatrixStack;
import java.util.Map;
import java.util.Collections;
import java.util.IdentityHashMap;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.render.spatial.DrawEntryList;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.render.DrawableObject;
import java.util.Set;
import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint;

public class DrawableHUD
{
    private final DrawableAvatar attachedTo;
    private final SLAttachmentPoint attachmentPoint;
    private final DrawableStore drawableStore;
    private final Set<DrawableObject> hudObjects;
    private final LLVector3 maxPos;
    private final LLVector3 minPos;
    
    public DrawableHUD(final SLAttachmentPoint attachmentPoint, final DrawEntryList list, final SLObjectInfo slObjectInfo, final DrawableStore drawableStore, final DrawableAvatar attachedTo) {
        this.minPos = new LLVector3();
        this.maxPos = new LLVector3();
        this.hudObjects = Collections.newSetFromMap(new IdentityHashMap<DrawableObject, Boolean>());
        this.attachmentPoint = attachmentPoint;
        this.drawableStore = drawableStore;
        this.attachedTo = attachedTo;
        this.addObject(list, slObjectInfo, new MatrixStack(), true);
    }
    
    private void addObject(final DrawEntryList list, final SLObjectInfo slObjectInfo, final MatrixStack matrixStack, final boolean b) {
        matrixStack.glPushMatrix();
        this.processObjectExtents(slObjectInfo, matrixStack, b);
        final DrawListObjectEntry drawListEntry = slObjectInfo.getDrawListEntry();
        ((InlineList<DrawListPrimEntry>)list).addEntry(drawListEntry);
        if (drawListEntry instanceof DrawListPrimEntry) {
            this.hudObjects.add(((DrawListPrimEntry)drawListEntry).getDrawableAttachment(this.drawableStore, this.attachedTo));
        }
        for (LinkedTreeNode<SLObjectInfo> linkedTreeNode = slObjectInfo.treeNode.getFirstChild(); linkedTreeNode != null; linkedTreeNode = linkedTreeNode.getNextChild()) {
            final SLObjectInfo slObjectInfo2 = linkedTreeNode.getDataObject();
            if (slObjectInfo2 != null) {
                this.addObject(list, slObjectInfo2, matrixStack, false);
            }
        }
        matrixStack.glPopMatrix();
    }
    
    private void processObjectExtents(final SLObjectInfo slObjectInfo, final MatrixStack matrixStack, final boolean b) {
        final float[] array = new float[8];
        final Vector3Array objectCoords = slObjectInfo.getObjectCoords();
        final int elementOffset = objectCoords.getElementOffset(0);
        final int elementOffset2 = objectCoords.getElementOffset(1);
        final float[] data = objectCoords.getData();
        matrixStack.glTranslatef(data[elementOffset + 0], data[elementOffset + 1], data[elementOffset + 2]);
        matrixStack.glMultMatrixf(slObjectInfo.getRotation().getInverseMatrix(), 0);
        array[0] = -data[elementOffset2 + 0] / 2.0f;
        array[1] = -data[elementOffset2 + 1] / 2.0f;
        array[2] = -data[elementOffset2 + 2] / 2.0f;
        array[3] = 1.0f;
        Matrix.multiplyMV(array, 4, matrixStack.getMatrixData(), matrixStack.getMatrixDataOffset(), array, 0);
        if (b) {
            this.minPos.x = array[4];
            this.minPos.y = array[5];
            this.minPos.z = array[6];
            this.maxPos.x = array[4];
            this.maxPos.y = array[5];
            this.maxPos.z = array[6];
        }
        else {
            this.minPos.x = Math.min(this.minPos.x, array[4]);
            this.minPos.y = Math.min(this.minPos.y, array[5]);
            this.minPos.z = Math.min(this.minPos.z, array[6]);
            this.maxPos.x = Math.max(this.maxPos.x, array[4]);
            this.maxPos.y = Math.max(this.maxPos.y, array[5]);
            this.maxPos.z = Math.max(this.maxPos.z, array[6]);
        }
        array[0] = data[elementOffset2 + 0] / 2.0f;
        array[1] = data[elementOffset2 + 1] / 2.0f;
        array[2] = data[elementOffset2 + 2] / 2.0f;
        array[3] = 1.0f;
        Matrix.multiplyMV(array, 4, matrixStack.getMatrixData(), matrixStack.getMatrixDataOffset(), array, 0);
        this.minPos.x = Math.min(this.minPos.x, array[4]);
        this.minPos.y = Math.min(this.minPos.y, array[5]);
        this.minPos.z = Math.min(this.minPos.z, array[6]);
        this.maxPos.x = Math.max(this.maxPos.x, array[4]);
        this.maxPos.y = Math.max(this.maxPos.y, array[5]);
        this.maxPos.z = Math.max(this.maxPos.z, array[6]);
    }
    
    public ObjectIntersectInfo Draw(final RenderContext renderContext, float n, final float n2, final float n3, final TouchHUDEvent touchHUDEvent, final boolean b) {
        renderContext.glModelPushMatrix();
        final float n4 = (this.minPos.y + this.maxPos.y) / 2.0f;
        final float n5 = (this.minPos.z + this.maxPos.z) / 2.0f;
        final float max = Math.max(this.maxPos.y - this.minPos.y, this.maxPos.z - this.minPos.z);
        if (max > 0.001f) {
            n *= 1.0f / max;
            renderContext.glModelScalef(1.0f, n, n);
        }
        renderContext.glModelTranslatef(-this.minPos.x, -n4 + n2, -n5 + n3);
        final Iterator<Object> iterator = this.hudObjects.iterator();
        ObjectIntersectInfo objectIntersectInfo = null;
        while (iterator.hasNext()) {
            final DrawableObject drawableObject = iterator.next();
            ObjectIntersectInfo objectIntersectInfo2 = null;
            Label_0174: {
                if (b) {
                    drawableObject.DrawHoverText(renderContext, true);
                    objectIntersectInfo2 = objectIntersectInfo;
                }
                else {
                    drawableObject.Draw(renderContext, 3);
                    if (touchHUDEvent != null) {
                        final ObjectIntersectInfo pickObject = drawableObject.PickObject(renderContext, touchHUDEvent.x, touchHUDEvent.y, Float.NEGATIVE_INFINITY);
                        if (pickObject != null) {
                            objectIntersectInfo2 = pickObject;
                            if (objectIntersectInfo == null) {
                                break Label_0174;
                            }
                            objectIntersectInfo2 = pickObject;
                            if (pickObject.pickDepth < objectIntersectInfo.pickDepth) {
                                break Label_0174;
                            }
                        }
                    }
                    objectIntersectInfo2 = objectIntersectInfo;
                }
            }
            objectIntersectInfo = objectIntersectInfo2;
        }
        renderContext.glModelPopMatrix();
        if (touchHUDEvent != null && objectIntersectInfo != null) {
            Debug.Printf("TouchHUD event: pickDepth %f objID %d", objectIntersectInfo.pickDepth, objectIntersectInfo.objInfo.localID);
            if (objectIntersectInfo.intersectInfo != null) {
                Debug.Printf("TouchHUD event: intersect face %d uv (%f, %f) st (%f, %f)", objectIntersectInfo.intersectInfo.faceID, objectIntersectInfo.intersectInfo.u, objectIntersectInfo.intersectInfo.v, objectIntersectInfo.intersectInfo.s, objectIntersectInfo.intersectInfo.t);
            }
        }
        return objectIntersectInfo;
    }
    
    public SLAttachmentPoint getAttachmentPoint() {
        return this.attachmentPoint;
    }
}
