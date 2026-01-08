package com.lumiyaviewer.lumiya.slproto.objects

import com.lumiyaviewer.lumiya.render.spatial.DrawListObjectEntry
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.slproto.types.Vector3Array
import com.lumiyaviewer.lumiya.utils.LinkedTreeNode
import java.util.UUID

abstract class SLObjectInfo : SLObjectDisplayInfo {
    override var localID: Int = 0
    override var name: String? = null
    override var distance: Float = 0f
    
    var uuid: UUID = UUID(0,0)
    var parentID: Int = 0
    var hierLevel: Int = 0
    var isAttachment: Boolean = false
    var isDead: Boolean = false
    var nameKnown: Boolean = false
    var rotation: LLQuaternion? = null
    var worldMatrix: FloatArray? = null
    
    val treeNode = LinkedTreeNode(this)
    val objectCoords = Vector3Array(4)
    
    override fun getUUID(): UUID = uuid
    fun getId(): UUID = uuid
    
    override fun getPosition(): LLVector3 = getAbsolutePosition()
    
    open fun getAbsolutePosition(): LLVector3 {
        return objectCoords.get(0)
    }
    
    override fun getRotation(): LLQuaternion? = rotation
    
    override fun getScale(): LLVector3 = objectCoords.get(1)

    abstract fun isAvatar(): Boolean
    open fun isMyAvatar(): Boolean = false
    
    fun getParentID(): Int = parentID
    fun getParentObject(): SLObjectInfo? = treeNode.parent?.getDataObject() as? SLObjectInfo
    
    fun addChild(child: SLObjectInfo) {
        treeNode.addChild(child.treeNode)
    }
    
    fun removeChild(child: SLObjectInfo) {
        treeNode.removeChild(child.treeNode)
    }
    
    fun setIsAttachmentAll(isAttach: Boolean) {
        this.isAttachment = isAttach
    }
    
    fun updateSpatialIndex(z: Boolean) {
        // Stub
    }
    
    fun clearDrawListEntry() {}
    fun getExistingDrawListEntry(): DrawListObjectEntry? = null
    fun getDrawListEntry(): DrawListObjectEntry { throw NotImplementedError() } 
    
    fun getObjectCoords(): Vector3Array = objectCoords
    
    fun getAttachedTo(): SLObjectInfo? = null
}
