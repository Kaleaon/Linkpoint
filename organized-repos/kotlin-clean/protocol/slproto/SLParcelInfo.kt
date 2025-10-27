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
import javax.annotation.Nullable

class SLParcelInfo {
    private SLObjectAvatarInfo agentAvatar = null
    private val Object agentAvatarLock = Object()
    val Map<UUID, SLObjectInfo> allObjectsNearby = ConcurrentHashMap(1024, 0.75f, 1)
    private Float drawDistance = 0.0f
    private val Comparator<SLObjectDisplayInfo> objectDisplayInfoComparator = -$Lambda$1YF5tPpIlUnjvWeNVttYc5eIlFY()
    val Map<UUID, SLObjectInfo> objectNamesQueue = Collections.synchronizedMap(LinkedHashMap())
    private val Map<Integer, LinkedList<SLObjectInfo>> orphanObjects = HashMap()
    private val Map<Integer, SLObjectInfo> rootObjects = ConcurrentHashMap(128, 0.75f, 1)
    private Float simSunHour = 0.5f
    private Boolean simSunHourDirty = true
    private val Object simSunHourLock = Object()
    val TerrainData terrainData = TerrainData()
    private volatile UserManager userManager
    val Map<Integer, UUID> uuidsNearby = HashMap()

    private ArrayList<SLObjectDisplayInfo> addDisplayObjects(Iterable<SLObjectInfo> iterable, SLObjectFilterInfo sLObjectFilterInfo, ImmutableVector immutableVector, Boolean z, MultipleChatterNameRetriever multipleChatterNameRetriever, Set<UUID> set, Boolean z2) {
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
                Float distanceTo = immutableVector.distanceTo(absolutePosition.x, absolutePosition.y, absolutePosition.z)
                Int isEmpty = addDisplayObjects != null ? addDisplayObjects.isEmpty() ^ 1 : 0
                Boolean objectMatches = sLObjectFilterInfo.objectMatches(sLObjectInfo, distanceTo, z2)
                if (isEmpty != 0 || objectMatches) {
                    String knownName = getKnownName(sLObjectInfo, multipleChatterNameRetriever, set)
                    Boolean nameMatches = sLObjectFilterInfo.nameMatches(knownName)
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

    private String getKnownName(SLObjectInfo sLObjectInfo, MultipleChatterNameRetriever multipleChatterNameRetriever, Set<UUID> set) {
        String str = null
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
        if (sLObjectInfo instanceof SLObjectAvatarInfo) {
            SLObjectAvatarInfo sLObjectAvatarInfo = (SLObjectAvatarInfo) sLObjectInfo
            sLObjectAvatarInfo.ApplyAvatarAnimation(avatarAnimation)
            if (sLObjectAvatarInfo.isMyAvatar() && sLAvatarControl != null) {
                sLAvatarControl.ApplyAvatarAnimation(sLObjectAvatarInfo, avatarAnimation)
            }
        }
    }

    synchronized Unit ApplyAvatarAppearance(AvatarAppearance avatarAppearance) {
        SLObjectInfo sLObjectInfo = (SLObjectInfo) this.allObjectsNearby.get(avatarAppearance.Sender_Field.ID)
        if (sLObjectInfo instanceof SLObjectAvatarInfo) {
            ((SLObjectAvatarInfo) sLObjectInfo).ApplyAvatarAppearance(avatarAppearance)
        }
    }

    /* DevToolsApp WARNING: Missing block: B:8:0x001f, code:
            return false
     */
    synchronized Boolean addObject(com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo objectInfo) {
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
            Boolean isAttachment = parentObject.isAvatar() || parentObject.isAttachment
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
                Boolean isAttachment = objectInfo.isAttachment
                orphan.setIsAttachmentAll(isAttachment)
                
                objectInfo.addChild(orphan)
            }
        }
        
        // Update spatial index for rendering
        objectInfo.updateSpatialIndex(false)
        
        return true
    }

    public SLObjectAvatarInfo getAgentAvatar() {
        SLObjectAvatarInfo sLObjectAvatarInfo
        synchronized (this.agentAvatarLock) {
            sLObjectAvatarInfo = this.agentAvatar
        }
        return sLObjectAvatarInfo
    }

    public synchronized SLObjectInfo getAvatarObject(UUID uuid) {
        return (SLObjectInfo) this.allObjectsNearby.get(uuid)
    }

    public ObjectDisplayList getDisplayObjects(ImmutableVector immutableVector, SLObjectFilterInfo sLObjectFilterInfo, MultipleChatterNameRetriever multipleChatterNameRetriever) {
        Collection addDisplayObjects
        Int size
        Boolean z = true
        Set hashSet = HashSet()
        synchronized (this) {
            addDisplayObjects = addDisplayObjects(this.rootObjects.values(), sLObjectFilterInfo, immutableVector, true, multipleChatterNameRetriever, hashSet, false)
            size = this.objectNamesQueue.size()
        }
        multipleChatterNameRetriever.retainChatters(hashSet)
        String str = "getDisplayObjects: objectList is %s, load queue %d"
        Array<Any> objArr = Object[2]
        objArr[0] = addDisplayObjects != null ? Integer.toString(addDisplayObjects.size()) : "null"
        objArr[1] = Integer.valueOf(size)
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

    public synchronized SLObjectInfo getObjectInfo(Int i) {
        UUID uuid = (UUID) this.uuidsNearby.get(Integer.valueOf(i))
        if (uuid == null) {
            return null
        }
        return (SLObjectInfo) this.allObjectsNearby.get(uuid)
    }

    public Int getObjectLocalID(UUID uuid) {
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

    public UUID getObjectUUID(Int i) {
        UUID uuid
        synchronized (this) {
            uuid = (UUID) this.uuidsNearby.get(Integer.valueOf(i))
        }
        return uuid
    }

    public Boolean getSunHour(FloatArray fArr, Boolean z) {
        synchronized (this.simSunHourLock) {
            if (this.simSunHourDirty || z) {
                fArr[0] = this.simSunHour
                this.simSunHourDirty = false
                return true
            }
            return false
        }
    }

    public ImmutableList<SLObjectInfo> getUserTouchableObjects(SLAgentCircuit sLAgentCircuit, UUID uuid) {
        Builder builder = ImmutableList.builder()
        synchronized (this) {
            SLObjectInfo sLObjectInfo = (SLObjectInfo) this.allObjectsNearby.get(uuid)
            if (sLObjectInfo != null) {
                try {
                    for (Object obj : sLObjectInfo.treeNode) {
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

    public synchronized Unit initSpatialIndex() {
        try {
            for (SLObjectInfo updateSpatialIndex : this.rootObjects.values()) {
                updateSpatialIndex.updateSpatialIndex(true)
            }
        } catch (Throwable e) {
            Debug.Warning(e)
        }
        return
    }

    public synchronized Boolean isParentOrSame(UUID uuid, UUID uuid2) {
        if (uuid2.equals(uuid)) {
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
    Boolean killObject(com.lumiyaviewer.lumiya.slproto.SLAgentCircuit agentCircuit, Int localID) {
        Boolean wasMyAvatarUpdated = false
        Boolean returnValue = false
        
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
                
                if (parentObject != null) {
                    parentObject.removeChild(objectInfo)
                    
                    // Check if parent is my avatar for attachment updates
                    if (parentObject instanceof SLObjectAvatarInfo) {
                        SLObjectAvatarInfo avatarInfo = (SLObjectAvatarInfo) parentObject
                        if (avatarInfo.isMyAvatar()) {
                            agentCircuit.processMyAttachmentUpdate(avatarInfo)
                        }
                    }
                } else {
                    // Remove from orphan objects if parent wasn't found
                    LinkedList<SLObjectInfo> orphanList = this.orphanObjects.get(objectInfo.parentID)
                    if (orphanList != null) {
                        orphanList.remove(objectInfo)
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
                        if (avatarChild instanceof SLObjectAvatarInfo) {
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
                    .requestUpdate(com.lumiyaviewer.lumiya.react.SubscriptionSingleKey.Value)
            }
        }
        
        return returnValue
    }
    }

    public synchronized Unit reset(UserManager userManager) {
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
            if (existingDrawListEntry != null) {
                existingDrawListEntry.requestEntryRemoval()
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

    fun setAgentAvatar(SLObjectAvatarInfo sLObjectAvatarInfo) {
        synchronized (this.agentAvatarLock) {
            this.agentAvatar = sLObjectAvatarInfo
        }
    }

    fun setDrawDistance(Float f) {
        synchronized (this) {
            if (this.drawDistance != f) {
                this.drawDistance = f
            }
        }
    }

    Unit setSunHour(Float f) {
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
                uuid = (UUID) this.uuidsNearby.get(Integer.valueOf(i))
                SLObjectInfo sLObjectInfo3 = uuid != null ? (SLObjectInfo) this.allObjectsNearby.get(uuid) : null
                if (sLObjectInfo3 != null) {
                    sLObjectInfo3.removeChild(sLObjectInfo)
                    sLObjectInfo3.updateSpatialIndex(false)
                }
                linkedList = (LinkedList) this.orphanObjects.get(Integer.valueOf(i))
                if (linkedList != null) {
                    linkedList.remove(sLObjectInfo)
                }
            } else {
                this.rootObjects.remove(Integer.valueOf(sLObjectInfo.localID))
            }
            if (sLObjectInfo.parentID != 0) {
                uuid = (UUID) this.uuidsNearby.get(Integer.valueOf(sLObjectInfo.parentID))
                if (uuid != null) {
                    sLObjectInfo2 = (SLObjectInfo) this.allObjectsNearby.get(uuid)
                }
                if (sLObjectInfo2 != null) {
                    sLObjectInfo.hierLevel = sLObjectInfo2.hierLevel + 1
                    sLObjectInfo.setIsAttachmentAll(!sLObjectInfo2.isAvatar() ? sLObjectInfo2.isAttachment : true)
                    sLObjectInfo2.addChild(sLObjectInfo)
                } else {
                    linkedList = (LinkedList) this.orphanObjects.get(Integer.valueOf(sLObjectInfo.parentID))
                    if (linkedList == null) {
                        linkedList = LinkedList()
                        this.orphanObjects.put(Integer.valueOf(sLObjectInfo.parentID), linkedList)
                    }
                    linkedList.add(sLObjectInfo)
                }
            } else {
                sLObjectInfo.hierLevel = 0
                sLObjectInfo.setIsAttachmentAll(false)
                this.rootObjects.put(Integer.valueOf(sLObjectInfo.localID), sLObjectInfo)
            }
            sLObjectInfo.updateSpatialIndex(false)
            return true
        }
    }
}
