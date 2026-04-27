package com.linkpoint.render.avatar

import android.opengl.Matrix
import com.linkpoint.Debug
import com.linkpoint.render.DrawableObject
import com.linkpoint.render.DrawableStore
import com.linkpoint.render.MatrixStack
import com.linkpoint.render.RenderContext
import com.linkpoint.render.TouchHUDEvent
import com.linkpoint.render.picking.ObjectIntersectInfo
import com.linkpoint.render.spatial.DrawEntryList
import com.linkpoint.render.spatial.DrawListPrimEntry
import com.linkpoint.slproto.avatar.SLAttachmentPoint
import com.linkpoint.slproto.objects.SLObjectInfo
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.types.Vector3Array
import com.linkpoint.utils.InlineListEntry
import com.linkpoint.utils.LinkedTreeNode
import java.util.Collections
import java.util.IdentityHashMap
import java.util.Iterator
import java.util.Set

class DrawableHUD {
    private val DrawableAvatar attachedTo
    private val SLAttachmentPoint attachmentPoint
    private val DrawableStore drawableStore
    private val Set<DrawableObject> hudObjects = Collections.newSetFromMap(IdentityHashMap())
    private val LLVector3 maxPos = LLVector3()
    private val LLVector3 minPos = LLVector3()

    public DrawableHUD(SLAttachmentPoint sLAttachmentPoint, DrawEntryList drawEntryList, SLObjectInfo sLObjectInfo, DrawableStore drawableStore, DrawableAvatar drawableAvatar) {
        this.attachmentPoint = sLAttachmentPoint
        this.drawableStore = drawableStore
        this.attachedTo = drawableAvatar
        addObject(drawEntryList, sLObjectInfo, MatrixStack(), true)
    }

     private fun addObject(drawEntryList: DrawEntryList, sLObjectInfo: SLObjectInfo, matrixStack: MatrixStack, z: Boolean) {
        matrixStack.glPushMatrix()
        processObjectExtents(sLObjectInfo, matrixStack, z)
        val drawListEntry: InlineListEntry = sLObjectInfo.getDrawListEntry()
        drawEntryList.addEntry(drawListEntry)
        if (drawListEntry instanceof DrawListPrimEntry) {
            this.hudObjects.add(((DrawListPrimEntry) drawListEntry).getDrawableAttachment(this.drawableStore, this.attachedTo))
        }
        for (LinkedTreeNode firstChild = sLObjectInfo.treeNode.getFirstChild(); firstChild != null; firstChild = firstChild.getNextChild()) {
            val sLObjectInfo2: SLObjectInfo = (SLObjectInfo) firstChild.getDataObject()
            if (sLObjectInfo2 != null) {
                addObject(drawEntryList, sLObjectInfo2, matrixStack, false)
            }
        }
        matrixStack.glPopMatrix()
    }

     private fun processObjectExtents(sLObjectInfo: SLObjectInfo, matrixStack: MatrixStack, z: Boolean) {
        r0 = Float[8]
        val objectCoords: Vector3Array = sLObjectInfo.getObjectCoords()
        val elementOffset: Int = objectCoords.getElementOffset(0)
        val elementOffset2: Int = objectCoords.getElementOffset(1)
        val data: FloatArray = objectCoords.getData()
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

    public fun Draw(renderContext: RenderContext, f: Float, f2: Float, f3: Float, touchHUDEvent: TouchHUDEvent, z: Boolean): ObjectIntersectInfo {
        ObjectIntersectInfo objectIntersectInfo
        val objectIntersectInfo2: ObjectIntersectInfo = null
        renderContext.glModelPushMatrix()
        val f4: Float = (this.minPos.y + this.maxPos.y) / 2.0f
        val f5: Float = (this.minPos.z + this.maxPos.z) / 2.0f
        val max: Float = Math.max(this.maxPos.y - this.minPos.y, this.maxPos.z - this.minPos.z)
        if (max > 0.001f) {
            max = (1.0f / max) * f
            renderContext.glModelScalef(1.0f, max, max)
        }
        renderContext.glModelTranslatef(-this.minPos.x, (-f4) + f2, (-f5) + f3)
        val it: Iterator = this.hudObjects.iterator()
        while (true) {
            objectIntersectInfo = objectIntersectInfo2
            if (!it.hasNext()) {
                break
            }
            val drawableObject: DrawableObject = (DrawableObject) it.next()
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
            Debug.Printf("TouchHUD event: pickDepth %f objID %d", Float.valueOf(objectIntersectInfo.pickDepth), Integer.valueOf(objectIntersectInfo.objInfo.localID))
            if (objectIntersectInfo.intersectInfo != null) {
                Debug.Printf("TouchHUD event: intersect face %d uv (%f, %f) st (%f, %f)", Integer.valueOf(objectIntersectInfo.intersectInfo.faceID), Float.valueOf(objectIntersectInfo.intersectInfo.u), Float.valueOf(objectIntersectInfo.intersectInfo.v), Float.valueOf(objectIntersectInfo.intersectInfo.s), Float.valueOf(objectIntersectInfo.intersectInfo.t))
            }
        }
        return objectIntersectInfo
    }

     public fun getAttachmentPoint(): SLAttachmentPoint {
        return this.attachmentPoint
    }
}
