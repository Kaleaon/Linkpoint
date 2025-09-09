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

public class SLParcelInfo {
    private SLObjectAvatarInfo agentAvatar = null;
    private final Object agentAvatarLock = new Object();
    public final Map<UUID, SLObjectInfo> allObjectsNearby = new ConcurrentHashMap(1024, 0.75f, 1);
    private float drawDistance = 0.0f;
    private final Comparator<SLObjectDisplayInfo> objectDisplayInfoComparator = new $Lambda$1YF5tPpIlUnjvWeNVttYc5eIlFY();
    public final Map<UUID, SLObjectInfo> objectNamesQueue = Collections.synchronizedMap(new LinkedHashMap());
    private final Map<Integer, LinkedList<SLObjectInfo>> orphanObjects = new HashMap();
    private final Map<Integer, SLObjectInfo> rootObjects = new ConcurrentHashMap(128, 0.75f, 1);
    private float simSunHour = 0.5f;
    private boolean simSunHourDirty = true;
    private final Object simSunHourLock = new Object();
    public final TerrainData terrainData = new TerrainData();
    private volatile UserManager userManager;
    public final Map<Integer, UUID> uuidsNearby = new HashMap();

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
                            arrayList3.add(new SLAvatarObjectDisplayInfo(knownName, next, distanceTo, arrayList != null ? ImmutableList.copyOf(arrayList) : ImmutableList.of(), z3));
                        } else if (arrayList == null || arrayList.isEmpty()) {
                            arrayList3.add(new SLPrimObjectDisplayInfo(next, distanceTo));
                        } else {
                            arrayList3.add(new SLPrimObjectDisplayInfoWithChildren(next, distanceTo, ImmutableList.copyOf(arrayList), z3));
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
            if (id == null) {
                return null;
            }
            set.add(id);
            return multipleChatterNameRetriever.addChatter(id);
        }
        if (!sLObjectInfo.nameKnown && (!this.objectNamesQueue.containsKey(sLObjectInfo.getId()))) {
            this.objectNamesQueue.put(sLObjectInfo.getId(), sLObjectInfo);
        }
        if (sLObjectInfo.nameKnown) {
            return Strings.nullToEmpty(sLObjectInfo.name);
        }
        return null;
    }

    /* access modifiers changed from: package-private */
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

    /* access modifiers changed from: package-private */
    public synchronized void ApplyAvatarAppearance(AvatarAppearance avatarAppearance) {
        SLObjectInfo sLObjectInfo = this.allObjectsNearby.get(avatarAppearance.Sender_Field.ID);
        if (sLObjectInfo instanceof SLObjectAvatarInfo) {
            ((SLObjectAvatarInfo) sLObjectInfo).ApplyAvatarAppearance(avatarAppearance);
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x001f, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean addObject(com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r6) {
        /*
            r5 = this;
            r2 = 1
            r4 = 0
            r1 = 0
            monitor-enter(r5)
            java.util.Map<java.lang.Integer, java.util.UUID> r0 = r5.uuidsNearby     // Catch:{ all -> 0x009e }
            int r3 = r6.localID     // Catch:{ all -> 0x009e }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ all -> 0x009e }
            boolean r0 = r0.containsKey(r3)     // Catch:{ all -> 0x009e }
            if (r0 != 0) goto L_0x001e
            java.util.Map<java.util.UUID, com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo> r0 = r5.allObjectsNearby     // Catch:{ all -> 0x009e }
            java.util.UUID r3 = r6.getId()     // Catch:{ all -> 0x009e }
            boolean r0 = r0.containsKey(r3)     // Catch:{ all -> 0x009e }
            if (r0 == 0) goto L_0x0020
        L_0x001e:
            monitor-exit(r5)
            return r4
        L_0x0020:
            java.util.Map<java.lang.Integer, java.util.UUID> r0 = r5.uuidsNearby     // Catch:{ all -> 0x009e }
            int r3 = r6.localID     // Catch:{ all -> 0x009e }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ all -> 0x009e }
            java.util.UUID r4 = r6.getId()     // Catch:{ all -> 0x009e }
            r0.put(r3, r4)     // Catch:{ all -> 0x009e }
            java.util.Map<java.util.UUID, com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo> r0 = r5.allObjectsNearby     // Catch:{ all -> 0x009e }
            java.util.UUID r3 = r6.getId()     // Catch:{ all -> 0x009e }
            r0.put(r3, r6)     // Catch:{ all -> 0x009e }
            int r0 = r6.parentID     // Catch:{ all -> 0x009e }
            if (r0 == 0) goto L_0x00c7
            java.util.Map<java.lang.Integer, java.util.UUID> r0 = r5.uuidsNearby     // Catch:{ all -> 0x009e }
            int r3 = r6.parentID     // Catch:{ all -> 0x009e }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ all -> 0x009e }
            java.lang.Object r0 = r0.get(r3)     // Catch:{ all -> 0x009e }
            java.util.UUID r0 = (java.util.UUID) r0     // Catch:{ all -> 0x009e }
            if (r0 == 0) goto L_0x0055
            java.util.Map<java.util.UUID, com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo> r1 = r5.allObjectsNearby     // Catch:{ all -> 0x009e }
            java.lang.Object r0 = r1.get(r0)     // Catch:{ all -> 0x009e }
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r0 = (com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo) r0     // Catch:{ all -> 0x009e }
            r1 = r0
        L_0x0055:
            if (r1 == 0) goto L_0x00a3
            int r0 = r1.hierLevel     // Catch:{ all -> 0x009e }
            int r0 = r0 + 1
            r6.hierLevel = r0     // Catch:{ all -> 0x009e }
            boolean r0 = r1.isAvatar()     // Catch:{ all -> 0x009e }
            if (r0 != 0) goto L_0x00a1
            boolean r0 = r1.isAttachment     // Catch:{ all -> 0x009e }
        L_0x0065:
            r6.setIsAttachmentAll(r0)     // Catch:{ all -> 0x009e }
            r1.addChild(r6)     // Catch:{ all -> 0x009e }
        L_0x006b:
            java.util.Map<java.lang.Integer, java.util.LinkedList<com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo>> r0 = r5.orphanObjects     // Catch:{ all -> 0x009e }
            int r1 = r6.localID     // Catch:{ all -> 0x009e }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x009e }
            java.lang.Object r0 = r0.remove(r1)     // Catch:{ all -> 0x009e }
            java.util.LinkedList r0 = (java.util.LinkedList) r0     // Catch:{ all -> 0x009e }
            if (r0 == 0) goto L_0x00d5
            java.util.Iterator r3 = r0.iterator()     // Catch:{ all -> 0x009e }
        L_0x007f:
            boolean r0 = r3.hasNext()     // Catch:{ all -> 0x009e }
            if (r0 == 0) goto L_0x00d5
            java.lang.Object r0 = r3.next()     // Catch:{ all -> 0x009e }
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r0 = (com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo) r0     // Catch:{ all -> 0x009e }
            int r1 = r6.hierLevel     // Catch:{ all -> 0x009e }
            int r1 = r1 + 1
            r0.hierLevel = r1     // Catch:{ all -> 0x009e }
            boolean r1 = r6.isAttachment     // Catch:{ all -> 0x009e }
            if (r1 != 0) goto L_0x00d3
            boolean r1 = r6.isAttachment     // Catch:{ all -> 0x009e }
        L_0x0097:
            r0.setIsAttachmentAll(r1)     // Catch:{ all -> 0x009e }
            r6.addChild(r0)     // Catch:{ all -> 0x009e }
            goto L_0x007f
        L_0x009e:
            r0 = move-exception
            monitor-exit(r5)
            throw r0
        L_0x00a1:
            r0 = r2
            goto L_0x0065
        L_0x00a3:
            java.util.Map<java.lang.Integer, java.util.LinkedList<com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo>> r0 = r5.orphanObjects     // Catch:{ all -> 0x009e }
            int r1 = r6.parentID     // Catch:{ all -> 0x009e }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x009e }
            java.lang.Object r0 = r0.get(r1)     // Catch:{ all -> 0x009e }
            java.util.LinkedList r0 = (java.util.LinkedList) r0     // Catch:{ all -> 0x009e }
            if (r0 != 0) goto L_0x00c3
            java.util.LinkedList r0 = new java.util.LinkedList     // Catch:{ all -> 0x009e }
            r0.<init>()     // Catch:{ all -> 0x009e }
            java.util.Map<java.lang.Integer, java.util.LinkedList<com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo>> r1 = r5.orphanObjects     // Catch:{ all -> 0x009e }
            int r3 = r6.parentID     // Catch:{ all -> 0x009e }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ all -> 0x009e }
            r1.put(r3, r0)     // Catch:{ all -> 0x009e }
        L_0x00c3:
            r0.add(r6)     // Catch:{ all -> 0x009e }
            goto L_0x006b
        L_0x00c7:
            java.util.Map<java.lang.Integer, com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo> r0 = r5.rootObjects     // Catch:{ all -> 0x009e }
            int r1 = r6.localID     // Catch:{ all -> 0x009e }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x009e }
            r0.put(r1, r6)     // Catch:{ all -> 0x009e }
            goto L_0x006b
        L_0x00d3:
            r1 = r2
            goto L_0x0097
        L_0x00d5:
            r0 = 0
            r6.updateSpatialIndex(r0)     // Catch:{ all -> 0x009e }
            monitor-exit(r5)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.SLParcelInfo.addObject(com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo):boolean");
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
        boolean z = true;
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
        if (addDisplayObjects != null) {
            Collections.sort(addDisplayObjects, this.objectDisplayInfoComparator);
            return new ObjectsManager.ObjectDisplayList(ImmutableList.copyOf(addDisplayObjects), size != 0);
        }
        ImmutableList of = ImmutableList.of();
        if (size == 0) {
            z = false;
        }
        return new ObjectsManager.ObjectDisplayList(of, z);
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
            if (!this.simSunHourDirty && !z) {
                return false;
            }
            fArr[0] = this.simSunHour;
            this.simSunHourDirty = false;
            return true;
        }
    }

    public ImmutableList<SLObjectInfo> getUserTouchableObjects(SLAgentCircuit sLAgentCircuit, UUID uuid) {
        ImmutableList.Builder builder = ImmutableList.builder();
        synchronized (this) {
            SLObjectInfo sLObjectInfo = this.allObjectsNearby.get(uuid);
            if (sLObjectInfo != null) {
                try {
                    for (SLObjectInfo next : sLObjectInfo.treeNode) {
                        if (next.isTouchable()) {
                            if (!next.nameKnown) {
                                sLAgentCircuit.RequestObjectName(next);
                            }
                            builder.add((Object) next);
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
            for (SLObjectInfo updateSpatialIndex : this.rootObjects.values()) {
                updateSpatialIndex.updateSpatialIndex(true);
            }
        } catch (ConcurrentModificationException e) {
            Debug.Warning(e);
        }
        return;
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

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00f1  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x00f7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean killObject(com.lumiyaviewer.lumiya.slproto.SLAgentCircuit r11, int r12) {
        /*
            r10 = this;
            r7 = 1
            r6 = 0
            r4 = 0
            monitor-enter(r10)
            java.util.Map<java.lang.Integer, java.util.UUID> r1 = r10.uuidsNearby     // Catch:{ all -> 0x0083 }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r12)     // Catch:{ all -> 0x0083 }
            java.lang.Object r1 = r1.remove(r2)     // Catch:{ all -> 0x0083 }
            java.util.UUID r1 = (java.util.UUID) r1     // Catch:{ all -> 0x0083 }
            if (r1 == 0) goto L_0x011c
            java.util.Map<java.util.UUID, com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo> r2 = r10.objectNamesQueue     // Catch:{ all -> 0x0083 }
            r2.remove(r1)     // Catch:{ all -> 0x0083 }
            java.util.Map<java.util.UUID, com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo> r2 = r10.allObjectsNearby     // Catch:{ all -> 0x0083 }
            java.lang.Object r2 = r2.remove(r1)     // Catch:{ all -> 0x0083 }
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r2 = (com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo) r2     // Catch:{ all -> 0x0083 }
            if (r2 == 0) goto L_0x011c
            r3 = 1
            r2.isDead = r3     // Catch:{ all -> 0x0083 }
            int r3 = r2.parentID     // Catch:{ all -> 0x0083 }
            if (r3 != 0) goto L_0x0056
            java.util.Map<java.lang.Integer, com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo> r3 = r10.rootObjects     // Catch:{ all -> 0x0083 }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r12)     // Catch:{ all -> 0x0083 }
            r3.remove(r5)     // Catch:{ all -> 0x0083 }
        L_0x0031:
            com.lumiyaviewer.lumiya.utils.LinkedTreeNode<com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo> r3 = r2.treeNode     // Catch:{ NoSuchElementException -> 0x0112 }
            java.util.Iterator r5 = r3.iterator()     // Catch:{ NoSuchElementException -> 0x0112 }
        L_0x0037:
            boolean r3 = r5.hasNext()     // Catch:{ NoSuchElementException -> 0x0112 }
            if (r3 == 0) goto L_0x00b2
            java.lang.Object r3 = r5.next()     // Catch:{ NoSuchElementException -> 0x0112 }
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r3 = (com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo) r3     // Catch:{ NoSuchElementException -> 0x0112 }
            boolean r8 = r3.isAvatar()     // Catch:{ NoSuchElementException -> 0x0112 }
            if (r8 == 0) goto L_0x00ab
            if (r4 != 0) goto L_0x0050
            java.util.LinkedList r4 = new java.util.LinkedList     // Catch:{ NoSuchElementException -> 0x0112 }
            r4.<init>()     // Catch:{ NoSuchElementException -> 0x0112 }
        L_0x0050:
            r4.add(r3)     // Catch:{ NoSuchElementException -> 0x0112 }
            r3 = r4
        L_0x0054:
            r4 = r3
            goto L_0x0037
        L_0x0056:
            java.util.Map<java.lang.Integer, java.util.UUID> r3 = r10.uuidsNearby     // Catch:{ all -> 0x0083 }
            int r5 = r2.parentID     // Catch:{ all -> 0x0083 }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x0083 }
            java.lang.Object r3 = r3.get(r5)     // Catch:{ all -> 0x0083 }
            java.util.UUID r3 = (java.util.UUID) r3     // Catch:{ all -> 0x0083 }
            if (r3 == 0) goto L_0x0119
            java.util.Map<java.util.UUID, com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo> r5 = r10.allObjectsNearby     // Catch:{ all -> 0x0083 }
            java.lang.Object r3 = r5.get(r3)     // Catch:{ all -> 0x0083 }
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r3 = (com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo) r3     // Catch:{ all -> 0x0083 }
        L_0x006e:
            if (r3 == 0) goto L_0x0086
            r3.removeChild(r2)     // Catch:{ all -> 0x0083 }
            boolean r5 = r3 instanceof com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo     // Catch:{ all -> 0x0083 }
            if (r5 == 0) goto L_0x0031
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo r3 = (com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo) r3     // Catch:{ all -> 0x0083 }
            boolean r5 = r3.isMyAvatar()     // Catch:{ all -> 0x0083 }
            if (r5 == 0) goto L_0x0031
            r11.processMyAttachmentUpdate(r3)     // Catch:{ all -> 0x0083 }
            goto L_0x0031
        L_0x0083:
            r1 = move-exception
            monitor-exit(r10)
            throw r1
        L_0x0086:
            java.util.Map<java.lang.Integer, java.util.LinkedList<com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo>> r3 = r10.orphanObjects     // Catch:{ all -> 0x0083 }
            int r5 = r2.parentID     // Catch:{ all -> 0x0083 }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x0083 }
            java.lang.Object r3 = r3.get(r5)     // Catch:{ all -> 0x0083 }
            java.util.LinkedList r3 = (java.util.LinkedList) r3     // Catch:{ all -> 0x0083 }
            if (r3 == 0) goto L_0x0031
            r3.remove(r2)     // Catch:{ all -> 0x0083 }
            boolean r3 = r3.isEmpty()     // Catch:{ all -> 0x0083 }
            if (r3 == 0) goto L_0x0031
            java.util.Map<java.lang.Integer, java.util.LinkedList<com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo>> r3 = r10.orphanObjects     // Catch:{ all -> 0x0083 }
            int r5 = r2.parentID     // Catch:{ all -> 0x0083 }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x0083 }
            r3.remove(r5)     // Catch:{ all -> 0x0083 }
            goto L_0x0031
        L_0x00ab:
            int r3 = r3.localID     // Catch:{ NoSuchElementException -> 0x0112 }
            r10.killObject(r11, r3)     // Catch:{ NoSuchElementException -> 0x0112 }
            r3 = r4
            goto L_0x0054
        L_0x00b2:
            if (r4 == 0) goto L_0x0117
            java.util.Iterator r8 = r4.iterator()     // Catch:{ NoSuchElementException -> 0x0112 }
            r5 = r6
        L_0x00b9:
            boolean r3 = r8.hasNext()     // Catch:{ NoSuchElementException -> 0x00e6 }
            if (r3 == 0) goto L_0x0115
            java.lang.Object r3 = r8.next()     // Catch:{ NoSuchElementException -> 0x00e6 }
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r3 = (com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo) r3     // Catch:{ NoSuchElementException -> 0x00e6 }
            r2.removeChild(r3)     // Catch:{ NoSuchElementException -> 0x00e6 }
            r4 = 0
            r3.parentID = r4     // Catch:{ NoSuchElementException -> 0x00e6 }
            boolean r4 = r3 instanceof com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo     // Catch:{ NoSuchElementException -> 0x00e6 }
            if (r4 == 0) goto L_0x00da
            r0 = r3
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo r0 = (com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo) r0     // Catch:{ NoSuchElementException -> 0x00e6 }
            r4 = r0
            boolean r4 = r4.isMyAvatar()     // Catch:{ NoSuchElementException -> 0x00e6 }
            if (r4 == 0) goto L_0x00da
            r5 = r7
        L_0x00da:
            java.util.Map<java.lang.Integer, com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo> r4 = r10.rootObjects     // Catch:{ NoSuchElementException -> 0x00e6 }
            int r9 = r3.localID     // Catch:{ NoSuchElementException -> 0x00e6 }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ NoSuchElementException -> 0x00e6 }
            r4.put(r9, r3)     // Catch:{ NoSuchElementException -> 0x00e6 }
            goto L_0x00b9
        L_0x00e6:
            r3 = move-exception
        L_0x00e7:
            com.lumiyaviewer.lumiya.Debug.Warning(r3)     // Catch:{ all -> 0x0083 }
            r3 = r5
        L_0x00eb:
            r2.removeFromSpatialIndex()     // Catch:{ all -> 0x0083 }
            r2 = r3
        L_0x00ef:
            if (r1 == 0) goto L_0x00f2
            r6 = r7
        L_0x00f2:
            monitor-exit(r10)
            com.lumiyaviewer.lumiya.slproto.users.manager.UserManager r1 = r10.userManager
            if (r1 == 0) goto L_0x0111
            com.lumiyaviewer.lumiya.slproto.users.manager.UserManager r1 = r10.userManager
            com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager r1 = r1.getObjectsManager()
            r1.requestObjectProfileUpdate(r12)
            if (r2 == 0) goto L_0x0111
            com.lumiyaviewer.lumiya.slproto.users.manager.UserManager r1 = r10.userManager
            com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager r1 = r1.getObjectsManager()
            com.lumiyaviewer.lumiya.react.SubscriptionPool r1 = r1.myAvatarState()
            com.lumiyaviewer.lumiya.react.SubscriptionSingleKey r2 = com.lumiyaviewer.lumiya.react.SubscriptionSingleKey.Value
            r1.requestUpdate(r2)
        L_0x0111:
            return r6
        L_0x0112:
            r3 = move-exception
            r5 = r6
            goto L_0x00e7
        L_0x0115:
            r3 = r5
            goto L_0x00eb
        L_0x0117:
            r3 = r6
            goto L_0x00eb
        L_0x0119:
            r3 = r4
            goto L_0x006e
        L_0x011c:
            r2 = r6
            goto L_0x00ef
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.SLParcelInfo.killObject(com.lumiyaviewer.lumiya.slproto.SLAgentCircuit, int):boolean");
    }

    public synchronized void reset(UserManager userManager2) {
        if (userManager2 != this.userManager) {
            if (this.userManager != null) {
                this.userManager.getObjectsManager().clearParcelInfo(this);
            }
            this.userManager = userManager2;
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

    /* access modifiers changed from: package-private */
    public void setSunHour(float f) {
        Debug.Printf("Windlight: Simulator sun hour set to %f", Float.valueOf(f));
        synchronized (this.simSunHourLock) {
            this.simSunHour = f;
            this.simSunHourDirty = true;
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized boolean updateObjectParent(int i, SLObjectInfo sLObjectInfo) {
        SLObjectInfo sLObjectInfo2 = null;
        synchronized (this) {
            if (i == sLObjectInfo.parentID) {
                return false;
            }
            if (i != 0) {
                UUID uuid = this.uuidsNearby.get(Integer.valueOf(i));
                SLObjectInfo sLObjectInfo3 = uuid != null ? this.allObjectsNearby.get(uuid) : null;
                if (sLObjectInfo3 != null) {
                    sLObjectInfo3.removeChild(sLObjectInfo);
                    sLObjectInfo3.updateSpatialIndex(false);
                }
                LinkedList linkedList = this.orphanObjects.get(Integer.valueOf(i));
                if (linkedList != null) {
                    linkedList.remove(sLObjectInfo);
                }
            } else {
                this.rootObjects.remove(Integer.valueOf(sLObjectInfo.localID));
            }
            if (sLObjectInfo.parentID != 0) {
                UUID uuid2 = this.uuidsNearby.get(Integer.valueOf(sLObjectInfo.parentID));
                if (uuid2 != null) {
                    sLObjectInfo2 = this.allObjectsNearby.get(uuid2);
                }
                if (sLObjectInfo2 != null) {
                    sLObjectInfo.hierLevel = sLObjectInfo2.hierLevel + 1;
                    sLObjectInfo.setIsAttachmentAll(!sLObjectInfo2.isAvatar() ? sLObjectInfo2.isAttachment : true);
                    sLObjectInfo2.addChild(sLObjectInfo);
                } else {
                    LinkedList linkedList2 = this.orphanObjects.get(Integer.valueOf(sLObjectInfo.parentID));
                    if (linkedList2 == null) {
                        linkedList2 = new LinkedList();
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
