package com.linkpoint.slproto

import com.google.common.base.Strings
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableList.Builder
import com.linkpoint.Debug
import com.linkpoint.render.spatial.DrawListObjectEntry
import com.linkpoint.slproto.messages.AvatarAnimation
import com.linkpoint.slproto.messages.AvatarAppearance
import com.linkpoint.slproto.modules.SLAvatarControl
import com.linkpoint.slproto.objects.SLAvatarObjectDisplayInfo
import com.linkpoint.slproto.objects.SLObjectAvatarInfo
import com.linkpoint.slproto.objects.SLObjectDisplayInfo
import com.linkpoint.slproto.objects.SLObjectFilterInfo
import com.linkpoint.slproto.objects.SLObjectInfo
import com.linkpoint.slproto.objects.SLPrimObjectDisplayInfo
import com.linkpoint.slproto.objects.SLPrimObjectDisplayInfoWithChildren
import com.linkpoint.slproto.terrain.TerrainData
import com.linkpoint.slproto.types.ImmutableVector
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.users.MultipleChatterNameRetriever
import com.linkpoint.slproto.users.manager.ObjectsManager.ObjectDisplayList
import com.linkpoint.slproto.users.manager.UserManager
import java.util.ArrayList
import java.util.Collection
import java.util.Collections
import java.util.Comparator
import java.util.HashMap
import java.util.HashSet
import java.util.Iterator
import java.util.LinkedHashMap
import java.util.LinkedList
import java.util.Map
import java.util.Set
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import androidx.annotation.Nullable

class SLParcelInfo {
    private var agentAvatar: SLObjectAvatarInfo = null
    private val agentAvatarLock: Any = Any()
    Map<UUID, SLObjectInfo> allObjectsNearby = ConcurrentHashMap(1024, 0.75f, 1)
    private var drawDistance: Float = 0.0f
    private val objectDisplayInfoComparator: Comparator<SLObjectDisplayInfo> = -$Lambda$1YF5tPpIlUnjvWeNVttYc5eIlFY()
    Map<UUID, SLObjectInfo> objectNamesQueue = Collections.synchronizedMap(LinkedHashMap())
    private Map<Int, LinkedList<SLObjectInfo>> orphanObjects = HashMap()
    private val rootObjects: Map<Int, SLObjectInfo> = ConcurrentHashMap(128, 0.75f, 1)
    private var simSunHour: Float = 0.5f
    private var simSunHourDirty: Boolean = true
    private val simSunHourLock: Any = Any()
    TerrainData terrainData = TerrainData()
    private volatile UserManager userManager
    Map<Int, UUID> uuidsNearby = HashMap()

    @Nullable
    private fun addDisplayObjects(iterable: Iterable<SLObjectInfo>, sLObjectFilterInfo: SLObjectFilterInfo, immutableVector: ImmutableVector, z: Boolean, multipleChatterNameRetriever: MultipleChatterNameRetriever, set: Set<UUID>, z2: Boolean): ArrayList<SLObjectDisplayInfo> {
        ArrayList<SLObjectDisplayInfo> arrayList = null
        Iterator it = iterable.iterator()
        while (true) {
            ArrayList<SLObjectDisplayInfo> arrayList2 = arrayList
            if (!it.hasNext()) {
                return arrayList2
            }
            SLObjectInfo sLObjectInfo = (SLObjectInfo) it.next()
            if (sLObjectInfo != null) {
                Collection addDisplayObjects
                Iterable iterable2 = sLObjectInfo.treeNode
                if (iterable2.hasChildren()) {
                    addDisplayObjects = addDisplayObjects(iterable2, sLObjectFilterInfo, immutableVector, false, multipleChatterNameRetriever, set, !sLObjectInfo.isAvatar() ? z2 : true)
                } else {
                    addDisplayObjects = null
                }
                LLVector3 absolutePosition = sLObjectInfo.getAbsolutePosition()
                var distanceTo: Float = immutableVector.distanceTo(absolutePosition.x, absolutePosition.y, absolutePosition.z)
                var isEmpty: Int = addDisplayObjects != null ? addDisplayObjects.isEmpty() ^ 1 : 0
                var objectMatches: Boolean = sLObjectFilterInfo.objectMatches(sLObjectInfo, distanceTo, z2)
                if (isEmpty != 0 || objectMatches) {
                    var knownName: String = getKnownName(sLObjectInfo, multipleChatterNameRetriever, set)
                    var nameMatches: Boolean = sLObjectFilterInfo.nameMatches(knownName)
                    if (isEmpty != 0 || nameMatches) {
                        if (isEmpty != 0) {
                            z3 = (objectMatches ? nameMatches : 0) ^ 1
                        } else {
                            z3 = false
                        }
                        if (arrayList2 == null) {
                            arrayList2 = ArrayList()
                        }
                        if (!z) {
                            arrayList2.add(sLObjectInfo.isAvatar() ? SLAvatarObjectDisplayInfo(knownName, sLObjectInfo, distanceTo, ImmutableList.of(), z3) : SLPrimObjectDisplayInfo(sLObjectInfo, distanceTo))
                            if (addDisplayObjects != null) {
                                arrayList2.addAll(addDisplayObjects)
                            }
                        } else if (sLObjectInfo.isAvatar()) {
                            arrayList2.add(SLAvatarObjectDisplayInfo(knownName, sLObjectInfo, distanceTo, addDisplayObjects != null ? ImmutableList.copyOf(addDisplayObjects) : ImmutableList.of(), z3))
                        } else if (addDisplayObjects == null || addDisplayObjects.isEmpty()) {
                            arrayList2.add(SLPrimObjectDisplayInfo(sLObjectInfo, distanceTo))
                        } else {
                            arrayList2.add(SLPrimObjectDisplayInfoWithChildren(sLObjectInfo, distanceTo, ImmutableList.copyOf(addDisplayObjects), z3))
                        }
                    }
                }
            }
            arrayList = arrayList2
        }
    }

    @Nullable
    private fun getKnownName(sLObjectInfo: SLObjectInfo, multipleChatterNameRetriever: MultipleChatterNameRetriever, set: Set<UUID>): String {
        var str: String = null
        if (sLObjectInfo.isAvatar()) {
            UUID id = sLObjectInfo.getId()
            if (id == null) {
                return null
            }
            set.add(id)
            return multipleChatterNameRetriever.addChatter(id)
        }
        if (!(sLObjectInfo.nameKnown || (this.objectNamesQueue.containsKey(sLObjectInfo.getId()) ^ 1) == 0)) {
            this.objectNamesQueue.put(sLObjectInfo.getId(), sLObjectInfo)
        }
        if (sLObjectInfo.nameKnown) {
            str = Strings.nullToEmpty(sLObjectInfo.name)
        }
        return str
    }

    synchronized Unit ApplyAvatarAnimation(AvatarAnimation avatarAnimation, SLAvatarControl sLAvatarControl) {
        SLObjectInfo sLObjectInfo = (SLObjectInfo) this.allObjectsNearby.get(avatarAnimation.Sender_Field.ID)
        if (sLObjectInfo is SLObjectAvatarInfo) {
            SLObjectAvatarInfo sLObjectAvatarInfo = (SLObjectAvatarInfo) sLObjectInfo
            sLObjectAvatarInfo.ApplyAvatarAnimation(avatarAnimation)
            if (sLObjectAvatarInfo.isMyAvatar() && sLAvatarControl != null) {
                sLAvatarControl.ApplyAvatarAnimation(sLObjectAvatarInfo, avatarAnimation)
            }
        }
    }

    synchronized Unit ApplyAvatarAppearance(AvatarAppearance avatarAppearance) {
        SLObjectInfo sLObjectInfo = (SLObjectInfo) this.allObjectsNearby.get(avatarAppearance.Sender_Field.ID)
        if (sLObjectInfo is SLObjectAvatarInfo) {
            ((SLObjectAvatarInfo) sLObjectInfo).ApplyAvatarAppearance(avatarAppearance)
        }
    }

    /* DevToolsApp WARNING: Missing block: B:8:0x001f, code:
            return false
     */
    synchronized Boolean addObject(com.linkpoint.slproto.objects.SLObjectInfo objectInfo) {
        // Check if object already exists by local ID or UUID
        if (this.uuidsNearby.containsKey(objectInfo.localID) || 
            this.allObjectsNearby.containsKey(objectInfo.getId())) {
            return false
        }
        
        // Add to both maps
        this.uuidsNearby.put(objectInfo.localID, objectInfo.getId())
        this.allObjectsNearby.put(objectInfo.getId(), objectInfo)
        
        // Handle parent-child relationships
        SLObjectInfo parentObject = null
        if (objectInfo.parentID != null && objectInfo.parentID != 0) {
            UUID parentUUID = this.uuidsNearby.get(objectInfo.parentID)
            if (parentUUID != null) {
                parentObject = this.allObjectsNearby.get(parentUUID)
            }
        }
        
        if (parentObject != null) {
            // Set hierarchy level based on parent
            objectInfo.hierLevel = parentObject.hierLevel + 1
            
            // Handle attachment flag propagation
            var isAttachment: Boolean = parentObject.isAvatar() || parentObject.isAttachment
            objectInfo.setIsAttachmentAll(isAttachment)
            
            // Add to parent's children
            parentObject.addChild(objectInfo)
        } else if (objectInfo.parentID != null && objectInfo.parentID != 0) {
            // This is an orphan - parent not found yet
            LinkedList<SLObjectInfo> orphanList = this.orphanObjects.get(objectInfo.parentID)
            if (orphanList == null) {
                orphanList = LinkedList<>()
                this.orphanObjects.put(objectInfo.parentID, orphanList)
            }
            orphanList.add(objectInfo)
        } else {
            // This is a root object
            this.rootObjects.put(objectInfo.localID, objectInfo)
        }
        
        // Process any orphan objects that were waiting for this object as parent
        LinkedList<SLObjectInfo> orphans = this.orphanObjects.remove(objectInfo.localID)
        if (orphans != null) {
            for (SLObjectInfo orphan : orphans) {
                orphan.hierLevel = objectInfo.hierLevel + 1
                
                // Handle attachment propagation for orphans
                var isAttachment: Boolean = objectInfo.isAttachment
                orphan.setIsAttachmentAll(isAttachment)
                
                objectInfo.addChild(orphan)
            }
        }
        
        // Update spatial index for rendering
        objectInfo.updateSpatialIndex(false)
        
        return true
    }

    @Nullable
    fun getAgentAvatar(): SLObjectAvatarInfo {
        SLObjectAvatarInfo sLObjectAvatarInfo
        synchronized (this.agentAvatarLock) {
            sLObjectAvatarInfo = this.agentAvatar
        }
        return sLObjectAvatarInfo
    }

    fun snapshotAvatarObjects(): List<SLObjectAvatarInfo> {
        val result = ArrayList<SLObjectAvatarInfo>()
        synchronized(this) {
            for (entry in allObjectsNearby.values) {
                if (entry is SLObjectAvatarInfo) {
                    result.add(entry)
                }
            }
        }
        return result
    }

    synchronized SLObjectInfo getAvatarObject(UUID uuid) {
        return (SLObjectInfo) this.allObjectsNearby.get(uuid)
    }

    fun getDisplayObjects(immutableVector: ImmutableVector, sLObjectFilterInfo: SLObjectFilterInfo, multipleChatterNameRetriever: MultipleChatterNameRetriever): ObjectDisplayList {
        Collection addDisplayObjects
        Int size
        var z: Boolean = true
        Set hashSet = HashSet()
        synchronized (this) {
            addDisplayObjects = addDisplayObjects(this.rootObjects.values(), sLObjectFilterInfo, immutableVector, true, multipleChatterNameRetriever, hashSet, false)
            size = this.objectNamesQueue.size()
        }
        multipleChatterNameRetriever.retainChatters(hashSet)
        var str: String = "getDisplayObjects: objectList is %s, load queue %d"
        Any[] objArr = Any[2]
        objArr[0] = addDisplayObjects != null ? Int.toString(addDisplayObjects.size()) : "null"
        objArr[1] = Int.valueOf(size)
        Debug.Printf(str, objArr)
        if (addDisplayObjects != null) {
            Collections.sort(addDisplayObjects, this.objectDisplayInfoComparator)
            return ObjectDisplayList(ImmutableList.copyOf(addDisplayObjects), size != 0)
        }
        ImmutableList of = ImmutableList.of()
        if (size == 0) {
            z = false
        }
        return ObjectDisplayList(of, z)
    }

    @Nullable
    synchronized SLObjectInfo getObjectInfo(Int i) {
        UUID uuid = (UUID) this.uuidsNearby.get(Int.valueOf(i))
        if (uuid == null) {
            return null
        }
        return (SLObjectInfo) this.allObjectsNearby.get(uuid)
    }

    fun getObjectLocalID(uuid: UUID): Int {
        synchronized (this) {
            if (uuid != null) {
                SLObjectInfo sLObjectInfo = (SLObjectInfo) this.allObjectsNearby.get(uuid)
                if (sLObjectInfo != null) {
                    i = sLObjectInfo.localID
                }
            }
            i = -1
        }
        return i
    }

    @Nullable
    fun getObjectUUID(i: Int): UUID {
        UUID uuid
        synchronized (this) {
            uuid = (UUID) this.uuidsNearby.get(Int.valueOf(i))
        }
        return uuid
    }

    fun getSunHour(fArr: FloatArray, z: Boolean): Boolean {
        synchronized (this.simSunHourLock) {
            if (this.simSunHourDirty || z) {
                fArr[0] = this.simSunHour
                this.simSunHourDirty = false
                return true
            }
            return false
        }
    }

    fun getUserTouchableObjects(sLAgentCircuit: SLAgentCircuit, uuid: UUID): ImmutableList<SLObjectInfo> {
        Builder builder = ImmutableList.builder()
        synchronized (this) {
            SLObjectInfo sLObjectInfo = (SLObjectInfo) this.allObjectsNearby.get(uuid)
            if (sLObjectInfo != null) {
                try {
                    for (Any obj : sLObjectInfo.treeNode) {
                        if (obj.isTouchable()) {
                            if (!obj.nameKnown) {
                                sLAgentCircuit.RequestObjectName(obj)
                            }
                            builder.add(obj)
                        }
                    }
                } catch (Throwable e) {
                    Debug.Warning(e)
                }
            }
        }
        return builder.build()
    }

    synchronized Unit initSpatialIndex() {
        try {
            for (SLObjectInfo updateSpatialIndex : this.rootObjects.values()) {
                updateSpatialIndex.updateSpatialIndex(true)
            }
        } catch (Throwable e) {
            Debug.Warning(e)
        }
        return
    }

    synchronized Boolean isParentOrSame(UUID uuid, UUID uuid2) {
        if (uuid2 == uuid) {
            return true
        }
        SLObjectInfo sLObjectInfo = (SLObjectInfo) this.allObjectsNearby.get(uuid2)
        if (sLObjectInfo != null) {
            for (sLObjectInfo = sLObjectInfo.getParentObject(); sLObjectInfo != null; sLObjectInfo = sLObjectInfo.getParentObject()) {
                if (sLObjectInfo.getId().equals(uuid)) {
                    return true
                }
            }
        }
        return false
    }

    /* DevToolsApp WARNING: Removed duplicated region for block: B:60:0x00f1  */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:60:0x00f1  */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:64:0x00f7  */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:60:0x00f1  */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:64:0x00f7  */
    fun killObject(com.linkpoint.slproto.SLAgentCircuit agentCircuit, Int localID): Boolean {
        var wasMyAvatarUpdated: Boolean = false
        var returnValue: Boolean = false
        
        synchronized (this) {
            // Remove the object from UUID mapping
            UUID objectUUID = this.uuidsNearby.remove(localID)
            if (objectUUID == null) {
                return false
            }
            
            // Remove from name queue and main object map
            this.objectNamesQueue.remove(objectUUID)
            SLObjectInfo objectInfo = this.allObjectsNearby.remove(objectUUID)
            if (objectInfo == null) {
                return false
            }
            
            returnValue = true
            objectInfo.isDead = true
            
            // Handle parent relationship removal
            if (objectInfo.parentID == null || objectInfo.parentID == 0) {
                // This is a root object
                this.rootObjects.remove(localID)
            } else {
                // Remove from parent object
                UUID parentUUID = this.uuidsNearby.get(objectInfo.parentID)
                SLObjectInfo parentObject = null
                if (parentUUID != null) {
                    parentObject = this.allObjectsNearby.get(parentUUID)
                }
                
                parentObject?.removeChild(objectInfo)
                    
                    // Check if parent is my avatar for attachment updates
                    if (parentObject is SLObjectAvatarInfo) {
                        SLObjectAvatarInfo avatarInfo = (SLObjectAvatarInfo) parentObject
                        if (avatarInfo.isMyAvatar()) {
                            agentCircuit.processMyAttachmentUpdate(avatarInfo)
                        }
                    }
                } else {
                    // Remove from orphan objects if parent wasn't found
                    LinkedList<SLObjectInfo> orphanList = this.orphanObjects.get(objectInfo.parentID)
                    orphanList?.remove(objectInfo)
                        if (orphanList.isEmpty()) {
                            this.orphanObjects.remove(objectInfo.parentID)
                        }
                    }
                }
            }
            
            // Handle children - collect avatars separately for special processing
            LinkedList<SLObjectInfo> avatarChildren = null
            try {
                Iterator<SLObjectInfo> childIterator = objectInfo.treeNode.iterator()
                while (childIterator.hasNext()) {
                    SLObjectInfo child = childIterator.next()
                    
                    if (child.isAvatar()) {
                        // Collect avatar children for special handling
                        if (avatarChildren == null) {
                            avatarChildren = LinkedList<>()
                        }
                        avatarChildren.add(child)
                    } else {
                        // Recursively kill non-avatar children
                        killObject(agentCircuit, child.localID)
                    }
                }
                
                // Handle avatar children specially
                if (avatarChildren != null) {
                    for (SLObjectInfo avatarChild : avatarChildren) {
                        objectInfo.removeChild(avatarChild)
                        avatarChild.parentID = null
                        
                        // Check if this is my avatar
                        if (avatarChild is SLObjectAvatarInfo) {
                            SLObjectAvatarInfo avatarInfo = (SLObjectAvatarInfo) avatarChild
                            if (avatarInfo.isMyAvatar()) {
                                wasMyAvatarUpdated = true
                            }
                        }
                        
                        // Make avatar a root object
                        this.rootObjects.put(avatarChild.localID, avatarChild)
                    }
                }
            } catch (java.util.NoSuchElementException e) {
                Debug.Warning(e)
            }
            
            // Remove from spatial index
            objectInfo.removeFromSpatialIndex()
        }
        
        // Update object manager outside of synchronized block
        if (this.userManager != null) {
            this.userManager.getObjectsManager().requestObjectProfileUpdate(localID)
            
            if (wasMyAvatarUpdated) {
                this.userManager.getObjectsManager().myAvatarState()
                    .requestUpdate(com.linkpoint.react.SubscriptionSingleKey.Value)
            }
        }
        
        return returnValue
    }
    }

    synchronized Unit reset(UserManager userManager) {
        if (userManager != this.userManager) {
            if (this.userManager != null) {
                this.userManager.getObjectsManager().clearParcelInfo(this)
            }
            this.userManager = userManager
            if (this.userManager != null) {
                this.userManager.getObjectsManager().setParcelInfo(this)
            }
        }
        this.uuidsNearby.clear()
        for (SLObjectInfo sLObjectInfo : this.allObjectsNearby.values()) {
            DrawListObjectEntry existingDrawListEntry = sLObjectInfo.getExistingDrawListEntry()
            existingDrawListEntry?.requestEntryRemoval()
            }
            sLObjectInfo.clearDrawListEntry()
        }
        this.allObjectsNearby.clear()
        this.rootObjects.clear()
        this.orphanObjects.clear()
        this.objectNamesQueue.clear()
        this.terrainData.reset()
        this.simSunHour = 0.5f
        this.simSunHourDirty = false
    }

    fun setAgentAvatar(sLObjectAvatarInfo: SLObjectAvatarInfo)  {
        synchronized (this.agentAvatarLock) {
            this.agentAvatar = sLObjectAvatarInfo
        }
    }

    fun setDrawDistance(f: Float)  {
        synchronized (this) {
            if (this.drawDistance != f) {
                this.drawDistance = f
            }
        }
    }

    fun setSunHour(Float f)  {
        Debug.Printf("Windlight: Simulator sun hour set to %f", Float.valueOf(f))
        synchronized (this.simSunHourLock) {
            this.simSunHour = f
            this.simSunHourDirty = true
        }
    }

    synchronized Boolean updateObjectParent(Int i, SLObjectInfo sLObjectInfo) {
        SLObjectInfo sLObjectInfo2 = null
        synchronized (this) {
            if (i == sLObjectInfo.parentID) {
                return false
            }
            UUID uuid
            LinkedList linkedList
            if (i != 0) {
                uuid = (UUID) this.uuidsNearby.get(Int.valueOf(i))
                SLObjectInfo sLObjectInfo3 = uuid != null ? (SLObjectInfo) this.allObjectsNearby.get(uuid) : null
                sLObjectInfo3?.removeChild(sLObjectInfo)
                    sLObjectInfo3.updateSpatialIndex(false)
                }
                linkedList = (LinkedList) this.orphanObjects.get(Int.valueOf(i))
                linkedList?.remove(sLObjectInfo)
                }
            } else {
                this.rootObjects.remove(Int.valueOf(sLObjectInfo.localID))
            }
            if (sLObjectInfo.parentID != 0) {
                uuid = (UUID) this.uuidsNearby.get(Int.valueOf(sLObjectInfo.parentID))
                if (uuid != null) {
                    sLObjectInfo2 = (SLObjectInfo) this.allObjectsNearby.get(uuid)
                }
                if (sLObjectInfo2 != null) {
                    sLObjectInfo.hierLevel = sLObjectInfo2.hierLevel + 1
                    sLObjectInfo.setIsAttachmentAll(!sLObjectInfo2.isAvatar() ? sLObjectInfo2.isAttachment : true)
                    sLObjectInfo2.addChild(sLObjectInfo)
                } else {
                    linkedList = (LinkedList) this.orphanObjects.get(Int.valueOf(sLObjectInfo.parentID))
                    if (linkedList == null) {
                        linkedList = LinkedList()
                        this.orphanObjects.put(Int.valueOf(sLObjectInfo.parentID), linkedList)
                    }
                    linkedList.add(sLObjectInfo)
                }
            } else {
                sLObjectInfo.hierLevel = 0
                sLObjectInfo.setIsAttachmentAll(false)
                this.rootObjects.put(Int.valueOf(sLObjectInfo.localID), sLObjectInfo)
            }
            sLObjectInfo.updateSpatialIndex(false)
            return true
        }
    }
}
