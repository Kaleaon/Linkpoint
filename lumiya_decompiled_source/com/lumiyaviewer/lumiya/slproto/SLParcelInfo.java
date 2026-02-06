package com.lumiyaviewer.lumiya.slproto;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.spatial.DrawListObjectEntry;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAnimation;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl;
import com.lumiyaviewer.lumiya.slproto.objects.SLAvatarObjectDisplayInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectDisplayInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectFilterInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLPrimObjectDisplayInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLPrimObjectDisplayInfoWithChildren;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainData;
import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.users.MultipleChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.LinkedTreeNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class SLParcelInfo {
    private volatile UserManager userManager;
    private float drawDistance = 0.0f;
    public final TerrainData terrainData = new TerrainData();
    private final Object agentAvatarLock = new Object();
    private SLObjectAvatarInfo agentAvatar = null;
    private final Object simSunHourLock = new Object();
    private float simSunHour = 0.5f;
    private boolean simSunHourDirty = true;
    public final Map<Integer, UUID> uuidsNearby = new HashMap();
    public final Map<UUID, SLObjectInfo> allObjectsNearby = new ConcurrentHashMap(1024, 0.75f, 1);
    private final Map<Integer, SLObjectInfo> rootObjects = new ConcurrentHashMap(128, 0.75f, 1);
    private final Map<Integer, LinkedList<SLObjectInfo>> orphanObjects = new HashMap();
    public final Map<UUID, SLObjectInfo> objectNamesQueue = Collections.synchronizedMap(new LinkedHashMap());
    private final Comparator<SLObjectDisplayInfo> objectDisplayInfoComparator = new Comparator() { // from class: com.lumiyaviewer.lumiya.slproto.-$Lambda$1YF5tPpIlUnjvWeNVttYc5eIlFY
        private final /* synthetic */ int $m$0(Object obj, Object obj2) {
            int compare;
            compare = Float.compare(((SLObjectDisplayInfo) obj).distance, ((SLObjectDisplayInfo) obj2).distance);
            return compare;
        }

        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            return $m$0(obj, obj2);
        }
    };

    @Nullable
    private ArrayList<SLObjectDisplayInfo> addDisplayObjects(Iterable<SLObjectInfo> iterable, SLObjectFilterInfo sLObjectFilterInfo, ImmutableVector immutableVector, boolean z, MultipleChatterNameRetriever multipleChatterNameRetriever, Set<UUID> set, boolean z2) {
        ArrayList<SLObjectDisplayInfo> arrayList;
        boolean z3;
        ArrayList<SLObjectDisplayInfo> arrayList2 = null;
        Iterator<SLObjectInfo> it = iterable.iterator();
        while (true) {
            ArrayList<SLObjectDisplayInfo> arrayList3 = arrayList2;
            if (!it.hasNext()) {
                return arrayList3;
            }
            SLObjectInfo next = it.next();
            if (next != null) {
                LinkedTreeNode<SLObjectInfo> linkedTreeNode = next.treeNode;
                if (linkedTreeNode.hasChildren()) {
                    arrayList = addDisplayObjects(linkedTreeNode, sLObjectFilterInfo, immutableVector, false, multipleChatterNameRetriever, set, !next.isAvatar() ? z2 : true);
                } else {
                    arrayList = null;
                }
                LLVector3 absolutePosition = next.getAbsolutePosition();
                float distanceTo = immutableVector.distanceTo(absolutePosition.x, absolutePosition.y, absolutePosition.z);
                boolean z4 = arrayList != null ? !arrayList.isEmpty() : false;
                boolean objectMatches = sLObjectFilterInfo.objectMatches(next, distanceTo, z2);
                if (z4 || objectMatches) {
                    String knownName = getKnownName(next, multipleChatterNameRetriever, set);
                    boolean nameMatches = sLObjectFilterInfo.nameMatches(knownName);
                    if (z4 || nameMatches) {
                        if (z4) {
                            z3 = !(objectMatches ? nameMatches : false);
                        } else {
                            z3 = false;
                        }
                        if (arrayList3 == null) {
                            arrayList3 = new ArrayList<>();
                        }
                        if (!z) {
                            arrayList3.add(next.isAvatar() ? new SLAvatarObjectDisplayInfo(knownName, next, distanceTo, ImmutableList.of(), z3) : new SLPrimObjectDisplayInfo(next, distanceTo));
                            if (arrayList != null) {
                                arrayList3.addAll(arrayList);
                            }
                        } else if (next.isAvatar()) {
                            arrayList3.add(new SLAvatarObjectDisplayInfo(knownName, next, distanceTo, arrayList != null ? ImmutableList.copyOf((Collection) arrayList) : ImmutableList.of(), z3));
                        } else if (arrayList == null || arrayList.isEmpty()) {
                            arrayList3.add(new SLPrimObjectDisplayInfo(next, distanceTo));
                        } else {
                            arrayList3.add(new SLPrimObjectDisplayInfoWithChildren(next, distanceTo, ImmutableList.copyOf((Collection) arrayList), z3));
                        }
                    }
                }
            }
            arrayList2 = arrayList3;
        }
    }

    @Nullable
    private String getKnownName(SLObjectInfo sLObjectInfo, MultipleChatterNameRetriever multipleChatterNameRetriever, Set<UUID> set) {
        if (sLObjectInfo.isAvatar()) {
            UUID id = sLObjectInfo.getId();
            if (id != null) {
                set.add(id);
                return multipleChatterNameRetriever.addChatter(id);
            }
            return null;
        }
        if (!sLObjectInfo.nameKnown && (!this.objectNamesQueue.containsKey(sLObjectInfo.getId()))) {
            this.objectNamesQueue.put(sLObjectInfo.getId(), sLObjectInfo);
        }
        if (sLObjectInfo.nameKnown) {
            return Strings.nullToEmpty(sLObjectInfo.name);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void ApplyAvatarAnimation(AvatarAnimation avatarAnimation, SLAvatarControl sLAvatarControl) {
        SLObjectInfo sLObjectInfo = this.allObjectsNearby.get(avatarAnimation.Sender_Field.ID);
        if (sLObjectInfo instanceof SLObjectAvatarInfo) {
            SLObjectAvatarInfo sLObjectAvatarInfo = (SLObjectAvatarInfo) sLObjectInfo;
            sLObjectAvatarInfo.ApplyAvatarAnimation(avatarAnimation);
            if (sLObjectAvatarInfo.isMyAvatar() && sLAvatarControl != null) {
                sLAvatarControl.ApplyAvatarAnimation(sLObjectAvatarInfo, avatarAnimation);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void ApplyAvatarAppearance(AvatarAppearance avatarAppearance) {
        SLObjectInfo sLObjectInfo = this.allObjectsNearby.get(avatarAppearance.Sender_Field.ID);
        if (sLObjectInfo instanceof SLObjectAvatarInfo) {
            ((SLObjectAvatarInfo) sLObjectInfo).ApplyAvatarAppearance(avatarAppearance);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean addObject(SLObjectInfo sLObjectInfo) {
        synchronized (this) {
            if (this.uuidsNearby.containsKey(Integer.valueOf(sLObjectInfo.localID)) || this.allObjectsNearby.containsKey(sLObjectInfo.getId())) {
                return false;
            }
            this.uuidsNearby.put(Integer.valueOf(sLObjectInfo.localID), sLObjectInfo.getId());
            this.allObjectsNearby.put(sLObjectInfo.getId(), sLObjectInfo);
            if (sLObjectInfo.parentID != 0) {
                UUID uuid = this.uuidsNearby.get(Integer.valueOf(sLObjectInfo.parentID));
                SLObjectInfo sLObjectInfo2 = uuid != null ? this.allObjectsNearby.get(uuid) : null;
                if (sLObjectInfo2 != null) {
                    sLObjectInfo.hierLevel = sLObjectInfo2.hierLevel + 1;
                    sLObjectInfo.setIsAttachmentAll(!sLObjectInfo2.isAvatar() ? sLObjectInfo2.isAttachment : true);
                    sLObjectInfo2.addChild(sLObjectInfo);
                } else {
                    LinkedList<SLObjectInfo> linkedList = this.orphanObjects.get(Integer.valueOf(sLObjectInfo.parentID));
                    if (linkedList == null) {
                        linkedList = new LinkedList<>();
                        this.orphanObjects.put(Integer.valueOf(sLObjectInfo.parentID), linkedList);
                    }
                    linkedList.add(sLObjectInfo);
                }
            } else {
                this.rootObjects.put(Integer.valueOf(sLObjectInfo.localID), sLObjectInfo);
            }
            LinkedList<SLObjectInfo> remove = this.orphanObjects.remove(Integer.valueOf(sLObjectInfo.localID));
            if (remove != null) {
                for (SLObjectInfo sLObjectInfo3 : remove) {
                    sLObjectInfo3.hierLevel = sLObjectInfo.hierLevel + 1;
                    sLObjectInfo3.setIsAttachmentAll(!sLObjectInfo.isAttachment ? sLObjectInfo.isAttachment : true);
                    sLObjectInfo.addChild(sLObjectInfo3);
                }
            }
            sLObjectInfo.updateSpatialIndex(false);
            return true;
        }
    }

    @Nullable
    public SLObjectAvatarInfo getAgentAvatar() {
        SLObjectAvatarInfo sLObjectAvatarInfo;
        synchronized (this.agentAvatarLock) {
            sLObjectAvatarInfo = this.agentAvatar;
        }
        return sLObjectAvatarInfo;
    }

    public synchronized SLObjectInfo getAvatarObject(UUID uuid) {
        return this.allObjectsNearby.get(uuid);
    }

    public ObjectsManager.ObjectDisplayList getDisplayObjects(ImmutableVector immutableVector, SLObjectFilterInfo sLObjectFilterInfo, MultipleChatterNameRetriever multipleChatterNameRetriever) {
        ArrayList<SLObjectDisplayInfo> addDisplayObjects;
        int size;
        HashSet hashSet = new HashSet();
        synchronized (this) {
            addDisplayObjects = addDisplayObjects(this.rootObjects.values(), sLObjectFilterInfo, immutableVector, true, multipleChatterNameRetriever, hashSet, false);
            size = this.objectNamesQueue.size();
        }
        multipleChatterNameRetriever.retainChatters(hashSet);
        Object[] objArr = new Object[2];
        objArr[0] = addDisplayObjects != null ? Integer.toString(addDisplayObjects.size()) : "null";
        objArr[1] = Integer.valueOf(size);
        Debug.Printf("getDisplayObjects: objectList is %s, load queue %d", objArr);
        if (addDisplayObjects == null) {
            return new ObjectsManager.ObjectDisplayList(ImmutableList.of(), size != 0);
        }
        Collections.sort(addDisplayObjects, this.objectDisplayInfoComparator);
        return new ObjectsManager.ObjectDisplayList(ImmutableList.copyOf((Collection) addDisplayObjects), size != 0);
    }

    @Nullable
    public synchronized SLObjectInfo getObjectInfo(int i) {
        UUID uuid = this.uuidsNearby.get(Integer.valueOf(i));
        if (uuid == null) {
            return null;
        }
        return this.allObjectsNearby.get(uuid);
    }

    public int getObjectLocalID(@Nullable UUID uuid) {
        int i;
        synchronized (this) {
            if (uuid != null) {
                SLObjectInfo sLObjectInfo = this.allObjectsNearby.get(uuid);
                if (sLObjectInfo != null) {
                    i = sLObjectInfo.localID;
                }
            }
            i = -1;
        }
        return i;
    }

    @Nullable
    public UUID getObjectUUID(int i) {
        UUID uuid;
        synchronized (this) {
            uuid = this.uuidsNearby.get(Integer.valueOf(i));
        }
        return uuid;
    }

    public boolean getSunHour(float[] fArr, boolean z) {
        synchronized (this.simSunHourLock) {
            if (this.simSunHourDirty || z) {
                fArr[0] = this.simSunHour;
                this.simSunHourDirty = false;
                return true;
            }
            return false;
        }
    }

    public ImmutableList<SLObjectInfo> getUserTouchableObjects(SLAgentCircuit sLAgentCircuit, UUID uuid) {
        ImmutableList.Builder builder = ImmutableList.builder();
        synchronized (this) {
            SLObjectInfo sLObjectInfo = this.allObjectsNearby.get(uuid);
            if (sLObjectInfo != null) {
                try {
                    for (SLObjectInfo sLObjectInfo2 : sLObjectInfo.treeNode) {
                        if (sLObjectInfo2.isTouchable()) {
                            if (!sLObjectInfo2.nameKnown) {
                                sLAgentCircuit.RequestObjectName(sLObjectInfo2);
                            }
                            builder.add((ImmutableList.Builder) sLObjectInfo2);
                        }
                    }
                } catch (NoSuchElementException e) {
                    Debug.Warning(e);
                }
            }
        }
        return builder.build();
    }

    public synchronized void initSpatialIndex() {
        try {
            for (SLObjectInfo sLObjectInfo : this.rootObjects.values()) {
                sLObjectInfo.updateSpatialIndex(true);
            }
        } catch (ConcurrentModificationException e) {
            Debug.Warning(e);
        }
    }

    public synchronized boolean isParentOrSame(UUID uuid, UUID uuid2) {
        if (uuid2.equals(uuid)) {
            return true;
        }
        SLObjectInfo sLObjectInfo = this.allObjectsNearby.get(uuid2);
        if (sLObjectInfo != null) {
            for (SLObjectInfo parentObject = sLObjectInfo.getParentObject(); parentObject != null; parentObject = parentObject.getParentObject()) {
                if (parentObject.getId().equals(uuid)) {
                    return true;
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:58:0x00f1  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x00f7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean killObject(com.lumiyaviewer.lumiya.slproto.SLAgentCircuit r11, int r12) {
        /*
            Method dump skipped, instructions count: 286
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.SLParcelInfo.killObject(com.lumiyaviewer.lumiya.slproto.SLAgentCircuit, int):boolean");
    }

    public synchronized void reset(UserManager userManager) {
        if (userManager != this.userManager) {
            if (this.userManager != null) {
                this.userManager.getObjectsManager().clearParcelInfo(this);
            }
            this.userManager = userManager;
            if (this.userManager != null) {
                this.userManager.getObjectsManager().setParcelInfo(this);
            }
        }
        this.uuidsNearby.clear();
        for (SLObjectInfo sLObjectInfo : this.allObjectsNearby.values()) {
            DrawListObjectEntry existingDrawListEntry = sLObjectInfo.getExistingDrawListEntry();
            if (existingDrawListEntry != null) {
                existingDrawListEntry.requestEntryRemoval();
            }
            sLObjectInfo.clearDrawListEntry();
        }
        this.allObjectsNearby.clear();
        this.rootObjects.clear();
        this.orphanObjects.clear();
        this.objectNamesQueue.clear();
        this.terrainData.reset();
        this.simSunHour = 0.5f;
        this.simSunHourDirty = false;
    }

    public void setAgentAvatar(SLObjectAvatarInfo sLObjectAvatarInfo) {
        synchronized (this.agentAvatarLock) {
            this.agentAvatar = sLObjectAvatarInfo;
        }
    }

    public void setDrawDistance(float f) {
        synchronized (this) {
            if (this.drawDistance != f) {
                this.drawDistance = f;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSunHour(float f) {
        Debug.Printf("Windlight: Simulator sun hour set to %f", Float.valueOf(f));
        synchronized (this.simSunHourLock) {
            this.simSunHour = f;
            this.simSunHourDirty = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean updateObjectParent(int i, SLObjectInfo sLObjectInfo) {
        synchronized (this) {
            if (i == sLObjectInfo.parentID) {
                return false;
            }
            if (i != 0) {
                UUID uuid = this.uuidsNearby.get(Integer.valueOf(i));
                SLObjectInfo sLObjectInfo2 = uuid != null ? this.allObjectsNearby.get(uuid) : null;
                if (sLObjectInfo2 != null) {
                    sLObjectInfo2.removeChild(sLObjectInfo);
                    sLObjectInfo2.updateSpatialIndex(false);
                }
                LinkedList<SLObjectInfo> linkedList = this.orphanObjects.get(Integer.valueOf(i));
                if (linkedList != null) {
                    linkedList.remove(sLObjectInfo);
                }
            } else {
                this.rootObjects.remove(Integer.valueOf(sLObjectInfo.localID));
            }
            if (sLObjectInfo.parentID != 0) {
                UUID uuid2 = this.uuidsNearby.get(Integer.valueOf(sLObjectInfo.parentID));
                SLObjectInfo sLObjectInfo3 = uuid2 != null ? this.allObjectsNearby.get(uuid2) : null;
                if (sLObjectInfo3 != null) {
                    sLObjectInfo.hierLevel = sLObjectInfo3.hierLevel + 1;
                    sLObjectInfo.setIsAttachmentAll(!sLObjectInfo3.isAvatar() ? sLObjectInfo3.isAttachment : true);
                    sLObjectInfo3.addChild(sLObjectInfo);
                } else {
                    LinkedList<SLObjectInfo> linkedList2 = this.orphanObjects.get(Integer.valueOf(sLObjectInfo.parentID));
                    if (linkedList2 == null) {
                        linkedList2 = new LinkedList<>();
                        this.orphanObjects.put(Integer.valueOf(sLObjectInfo.parentID), linkedList2);
                    }
                    linkedList2.add(sLObjectInfo);
                }
            } else {
                sLObjectInfo.hierLevel = 0;
                sLObjectInfo.setIsAttachmentAll(false);
                this.rootObjects.put(Integer.valueOf(sLObjectInfo.localID), sLObjectInfo);
            }
            sLObjectInfo.updateSpatialIndex(false);
            return true;
        }
    }
}
