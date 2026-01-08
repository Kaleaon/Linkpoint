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
import kotlin.math.max
import kotlin.math.min

class DrawableHUD(
    private val attachmentPoint: SLAttachmentPoint,
    drawEntryList: DrawEntryList,
    sLObjectInfo: SLObjectInfo,
    private val drawableStore: DrawableStore,
    private val attachedTo: DrawableAvatar
) {
    private val hudObjects: MutableSet<DrawableObject> = Collections.newSetFromMap(IdentityHashMap())
    private val maxPos: LLVector3 = LLVector3()
    private val minPos: LLVector3 = LLVector3()

    init {
        addObject(drawEntryList, sLObjectInfo, MatrixStack(), true)
    }

    private fun addObject(drawEntryList: DrawEntryList, sLObjectInfo: SLObjectInfo, matrixStack: MatrixStack, z: Boolean) {
        matrixStack.glPushMatrix()
        processObjectExtents(sLObjectInfo, matrixStack, z)
        val drawListEntry = sLObjectInfo.getDrawListEntry()
        drawEntryList.addEntry(drawListEntry)
        
        if (drawListEntry is DrawListPrimEntry) {
            val attachment = drawListEntry.getDrawableAttachment(drawableStore, attachedTo)
            if (attachment != null) {
                hudObjects.add(attachment)
            }
        }
        
        var firstChild = sLObjectInfo.treeNode.getFirstChild()
        while (firstChild != null) {
            val childInfo = firstChild.getDataObject() as? SLObjectInfo
            if (childInfo != null) {
                addObject(drawEntryList, childInfo, matrixStack, false)
            }
            firstChild = firstChild.getNextChild()
        }
        matrixStack.glPopMatrix()
    }

    private fun processObjectExtents(sLObjectInfo: SLObjectInfo, matrixStack: MatrixStack, z: Boolean) {
        val r0 = FloatArray(8)
        val objectCoords = sLObjectInfo.getObjectCoords()
        val elementOffset = objectCoords.getElementOffset(0)
        val elementOffset2 = objectCoords.getElementOffset(1)
        val data = objectCoords.getData()
        
        matrixStack.glTranslatef(data[elementOffset + 0], data[elementOffset + 1], data[elementOffset + 2])
        
        // Fix: Ensure rotation is not null using !!
        matrixStack.glMultMatrixf(sLObjectInfo.getRotation()!!.getInverseMatrix(), 0)
        
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
            this.minPos.x = min(this.minPos.x, r0[4])
            this.minPos.y = min(this.minPos.y, r0[5])
            this.minPos.z = min(this.minPos.z, r0[6])
            this.maxPos.x = max(this.maxPos.x, r0[4])
            this.maxPos.y = max(this.maxPos.y, r0[5])
            this.maxPos.z = max(this.maxPos.z, r0[6])
        }
        
        r0[0] = data[elementOffset2 + 0] / 2.0f
        r0[1] = data[elementOffset2 + 1] / 2.0f
        r0[2] = data[elementOffset2 + 2] / 2.0f
        r0[3] = 1.0f
        
        Matrix.multiplyMV(r0, 4, matrixStack.getMatrixData(), matrixStack.getMatrixDataOffset(), r0, 0)
        
        this.minPos.x = min(this.minPos.x, r0[4])
        this.minPos.y = min(this.minPos.y, r0[5])
        this.minPos.z = min(this.minPos.z, r0[6])
        this.maxPos.x = max(this.maxPos.x, r0[4])
        this.maxPos.y = max(this.maxPos.y, r0[5])
        this.maxPos.z = max(this.maxPos.z, r0[6])
    }

    fun Draw(renderContext: RenderContext, f: Float, f2: Float, f3: Float, touchHUDEvent: TouchHUDEvent?, z: Boolean): ObjectIntersectInfo? {
        var bestIntersect: ObjectIntersectInfo? = null
        renderContext.glModelPushMatrix()
        
        val f4 = (this.minPos.y + this.maxPos.y) / 2.0f
        val f5 = (this.minPos.z + this.maxPos.z) / 2.0f
        var max = max(this.maxPos.y - this.minPos.y, this.maxPos.z - this.minPos.z)
        
        if (max > 0.001f) {
            max = (1.0f / max) * f
            renderContext.glModelScalef(1.0f, max, max)
        }
        renderContext.glModelTranslatef(-this.minPos.x, (-f4) + f2, (-f5) + f3)
        
        for (drawableObject in hudObjects) {
            if (z) {
                // drawableObject.DrawHoverText(renderContext, true) // Stub
            } else {
                drawableObject.Draw(renderContext, 3)
                if (touchHUDEvent != null) {
                    // val intersect = drawableObject.PickObject(renderContext, touchHUDEvent.x, touchHUDEvent.y, Float.NEGATIVE_INFINITY) // Stub
                }
            }
        }
        renderContext.glModelPopMatrix()
        
        if (touchHUDEvent != null && bestIntersect != null) {
            Debug.Log("TouchHUD event: pickDepth ${bestIntersect.pickDepth} objID ${bestIntersect.objInfo?.localID}")
        }
        return bestIntersect
    }

    fun getAttachmentPoint(): SLAttachmentPoint {
        return this.attachmentPoint
    }
}
