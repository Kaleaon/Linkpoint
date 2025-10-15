package com.lumiyaviewer.lumiya.render.avatar

import android.opengl.Matrix
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.render.DrawableObject
import com.lumiyaviewer.lumiya.render.DrawableStore
import com.lumiyaviewer.lumiya.render.MatrixStack
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.TouchHUDEvent
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo
import com.lumiyaviewer.lumiya.render.spatial.DrawEntryList
import com.lumiyaviewer.lumiya.render.spatial.DrawListPrimEntry
import com.lumiyaviewer.lumiya.slproto.avatar.SLAttachmentPoint
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.slproto.types.Vector3Array
import com.lumiyaviewer.lumiya.utils.InlineListEntry
import com.lumiyaviewer.lumiya.utils.LinkedTreeNode
import java.util.Collections
import java.util.IdentityHashMap
import java.util.Iterator
import java.util.Set

class DrawableHUD {
    private DrawableAvatar attachedTo
    private SLAttachmentPoint attachmentPoint
    private DrawableStore drawableStore
    private val hudObjects: Set<DrawableObject> = Collections.newSetFromMap(IdentityHashMap())
    private val maxPos: LLVector3 = LLVector3()
    private val minPos: LLVector3 = LLVector3()

    constructor(sLAttachmentPoint: SLAttachmentPoint, drawEntryList: DrawEntryList, sLObjectInfo: SLObjectInfo, drawableStore: DrawableStore, drawableAvatar: DrawableAvatar) {
        this.attachmentPoint = sLAttachmentPoint
        this.drawableStore = drawableStore
        this.attachedTo = drawableAvatar
        addObject(drawEntryList, sLObjectInfo, MatrixStack(), true)
    }

    private fun addObject(drawEntryList: DrawEntryList, sLObjectInfo: SLObjectInfo, matrixStack: MatrixStack, z: Boolean): Unit {
        matrixStack.glPushMatrix()
        processObjectExtents(sLObjectInfo, matrixStack, z)
        InlineListEntry drawListEntry = sLObjectInfo.getDrawListEntry()
        drawEntryList.addEntry(drawListEntry)
        if (drawListEntry instanceof DrawListPrimEntry) {
            this.hudObjects.add(((DrawListPrimEntry) drawListEntry).getDrawableAttachment(this.drawableStore, this.attachedTo))
        }
        for (LinkedTreeNode firstChild = sLObjectInfo.treeNode.getFirstChild(); firstChild != null; firstChild = firstChild.getNextChild()) {
            SLObjectInfo sLObjectInfo2 = (SLObjectInfo) firstChild.getDataObject()
            if (sLObjectInfo2 != null) {
                addObject(drawEntryList, sLObjectInfo2, matrixStack, false)
            }
        }
        matrixStack.glPopMatrix()
    }

    private fun processObjectExtents(sLObjectInfo: SLObjectInfo, matrixStack: MatrixStack, z: Boolean): Unit {
        r0 = Float[8]
        Vector3Array objectCoords = sLObjectInfo.getObjectCoords()
        Int elementOffset = objectCoords.getElementOffset(0)
        Int elementOffset2 = objectCoords.getElementOffset(1)
        Float[] data = objectCoords.getData()
        matrixStack.glTranslatef(data[elementOffset + 0], data[elementOffset + 1], data[elementOffset + 2])
        matrixStack.glMultMatrixf(sLObjectInfo.getRotation().getInverseMatrix(), 0)
        r0[0] = (-data[elementOffset2 + 0]) / 2.0f
        r0[1] = (-data[elementOffset2 + 1]) / 2.0f
        r0[2] = (-data[elementOffset2 + 2]) / 2.0f
        r0[3] = 1.0f
        Matrix.multiplyMV(r0, 4, matrixStack.getMatrixData(), matrixStack.getMatrixDataOffset(), r0, 0)
        if (z) {
            this.minPos.x = r0[4]
            this.minPos.y = r0[5]
            this.minPos.z = r0[6]
            this.maxPos.x = r0[4]
            this.maxPos.y = r0[5]
            this.maxPos.z = r0[6]
        } else {
            this.minPos.x = Math.min(this.minPos.x, r0[4])
            this.minPos.y = Math.min(this.minPos.y, r0[5])
            this.minPos.z = Math.min(this.minPos.z, r0[6])
            this.maxPos.x = Math.max(this.maxPos.x, r0[4])
            this.maxPos.y = Math.max(this.maxPos.y, r0[5])
            this.maxPos.z = Math.max(this.maxPos.z, r0[6])
        }
        r0[0] = data[elementOffset2 + 0] / 2.0f
        r0[1] = data[elementOffset2 + 1] / 2.0f
        r0[2] = data[elementOffset2 + 2] / 2.0f
        r0[3] = 1.0f
        Matrix.multiplyMV(r0, 4, matrixStack.getMatrixData(), matrixStack.getMatrixDataOffset(), r0, 0)
        this.minPos.x = Math.min(this.minPos.x, r0[4])
        this.minPos.y = Math.min(this.minPos.y, r0[5])
        this.minPos.z = Math.min(this.minPos.z, r0[6])
        this.maxPos.x = Math.max(this.maxPos.x, r0[4])
        this.maxPos.y = Math.max(this.maxPos.y, r0[5])
        this.maxPos.z = Math.max(this.maxPos.z, r0[6])
    }

    fun Draw(renderContext: RenderContext, f: Float, f2: Float, f3: Float, touchHUDEvent: TouchHUDEvent, z: Boolean): ObjectIntersectInfo {
        ObjectIntersectInfo objectIntersectInfo
        ObjectIntersectInfo objectIntersectInfo2 = null
        renderContext.glModelPushMatrix()
        Float f4 = (this.minPos.y + this.maxPos.y) / 2.0f
        Float f5 = (this.minPos.z + this.maxPos.z) / 2.0f
        Float max = Math.max(this.maxPos.y - this.minPos.y, this.maxPos.z - this.minPos.z)
        if (max > 0.001f) {
            max = (1.0f / max) * f
            renderContext.glModelScalef(1.0f, max, max)
        }
        renderContext.glModelTranslatef(-this.minPos.x, (-f4) + f2, (-f5) + f3)
        Iterator it = this.hudObjects.iterator()
        while (true) {
            objectIntersectInfo = objectIntersectInfo2
            if (!it.hasNext()) {
                break
            }
            DrawableObject drawableObject = (DrawableObject) it.next()
            if (z) {
                drawableObject.DrawHoverText(renderContext, true)
                objectIntersectInfo2 = objectIntersectInfo
            } else {
                drawableObject.Draw(renderContext, 3)
                if (touchHUDEvent != null) {
                    objectIntersectInfo2 = drawableObject.PickObject(renderContext, touchHUDEvent.x, touchHUDEvent.y, Float.NEGATIVE_INFINITY)
                    if (objectIntersectInfo2 != null) {
                        if (objectIntersectInfo != null) {
                            if (objectIntersectInfo2.pickDepth < objectIntersectInfo.pickDepth) {
                            }
                        }
                    }
                }
                objectIntersectInfo2 = objectIntersectInfo
            }
        }
        renderContext.glModelPopMatrix()
        if (!(touchHUDEvent == null || objectIntersectInfo == null)) {
            Debug.Printf("TouchHUD event: pickDepth %f objID %d", Float.valueOf(objectIntersectInfo.pickDepth), Int.valueOf(objectIntersectInfo.objInfo.localID))
            if (objectIntersectInfo.intersectInfo != null) {
                Debug.Printf("TouchHUD event: intersect face %d uv (%f, %f) st (%f, %f)", Int.valueOf(objectIntersectInfo.intersectInfo.faceID), Float.valueOf(objectIntersectInfo.intersectInfo.u), Float.valueOf(objectIntersectInfo.intersectInfo.v), Float.valueOf(objectIntersectInfo.intersectInfo.s), Float.valueOf(objectIntersectInfo.intersectInfo.t))
            }
        }
        return objectIntersectInfo
    }

    fun getAttachmentPoint(): SLAttachmentPoint {
        return this.attachmentPoint
    }
}
