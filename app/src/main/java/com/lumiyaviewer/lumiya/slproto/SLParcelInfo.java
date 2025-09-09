package com.lumiyaviewer.lumiya.slproto;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
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
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager.ObjectDisplayList;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public class SLParcelInfo {
    private SLObjectAvatarInfo agentAvatar = null;
    private final Object agentAvatarLock = new Object();
    public final Map<UUID, SLObjectInfo> allObjectsNearby = new ConcurrentHashMap(1024, 0.75f, 1);
    private float drawDistance = 0.0f;
    private final Comparator<SLObjectDisplayInfo> objectDisplayInfoComparator = new -$Lambda$1YF5tPpIlUnjvWeNVttYc5eIlFY();
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
        ArrayList<SLObjectDisplayInfo> arrayList = null;
        Iterator it = iterable.iterator();
        while (true) {
            ArrayList<SLObjectDisplayInfo> arrayList2 = arrayList;
            if (!it.hasNext()) {
                return arrayList2;
            }
            SLObjectInfo sLObjectInfo = (SLObjectInfo) it.next();
            if (sLObjectInfo != null) {
                Collection addDisplayObjects;
                Iterable iterable2 = sLObjectInfo.treeNode;
                if (iterable2.hasChildren()) {
                    addDisplayObjects = addDisplayObjects(iterable2, sLObjectFilterInfo, immutableVector, false, multipleChatterNameRetriever, set, !sLObjectInfo.isAvatar() ? z2 : true);
                } else {
                    addDisplayObjects = null;
                }
                LLVector3 absolutePosition = sLObjectInfo.getAbsolutePosition();
                float distanceTo = immutableVector.distanceTo(absolutePosition.x, absolutePosition.y, absolutePosition.z);
                int isEmpty = addDisplayObjects != null ? addDisplayObjects.isEmpty() ^ 1 : 0;
                boolean objectMatches = sLObjectFilterInfo.objectMatches(sLObjectInfo, distanceTo, z2);
                if (isEmpty != 0 || objectMatches) {
                    String knownName = getKnownName(sLObjectInfo, multipleChatterNameRetriever, set);
                    boolean nameMatches = sLObjectFilterInfo.nameMatches(knownName);
                    if (isEmpty != 0 || nameMatches) {
                        boolean z3;
                        if (isEmpty != 0) {
                            z3 = (objectMatches ? nameMatches : 0) ^ 1;
                        } else {
                            z3 = false;
                        }
                        if (arrayList2 == null) {
                            arrayList2 = new ArrayList();
                        }
                        if (!z) {
                            arrayList2.add(sLObjectInfo.isAvatar() ? new SLAvatarObjectDisplayInfo(knownName, sLObjectInfo, distanceTo, ImmutableList.of(), z3) : new SLPrimObjectDisplayInfo(sLObjectInfo, distanceTo));
                            if (addDisplayObjects != null) {
                                arrayList2.addAll(addDisplayObjects);
                            }
                        } else if (sLObjectInfo.isAvatar()) {
                            arrayList2.add(new SLAvatarObjectDisplayInfo(knownName, sLObjectInfo, distanceTo, addDisplayObjects != null ? ImmutableList.copyOf(addDisplayObjects) : ImmutableList.of(), z3));
                        } else if (addDisplayObjects == null || addDisplayObjects.isEmpty()) {
                            arrayList2.add(new SLPrimObjectDisplayInfo(sLObjectInfo, distanceTo));
                        } else {
                            arrayList2.add(new SLPrimObjectDisplayInfoWithChildren(sLObjectInfo, distanceTo, ImmutableList.copyOf(addDisplayObjects), z3));
                        }
                    }
                }
            }
            arrayList = arrayList2;
        }
    }

    @Nullable
    private String getKnownName(SLObjectInfo sLObjectInfo, MultipleChatterNameRetriever multipleChatterNameRetriever, Set<UUID> set) {
        String str = null;
        if (sLObjectInfo.isAvatar()) {
            UUID id = sLObjectInfo.getId();
            if (id == null) {
                return null;
            }
            set.add(id);
            return multipleChatterNameRetriever.addChatter(id);
        }
        if (!(sLObjectInfo.nameKnown || (this.objectNamesQueue.containsKey(sLObjectInfo.getId()) ^ 1) == 0)) {
            this.objectNamesQueue.put(sLObjectInfo.getId(), sLObjectInfo);
        }
        if (sLObjectInfo.nameKnown) {
            str = Strings.nullToEmpty(sLObjectInfo.name);
        }
        return str;
    }

    synchronized void ApplyAvatarAnimation(AvatarAnimation avatarAnimation, SLAvatarControl sLAvatarControl) {
        SLObjectInfo sLObjectInfo = (SLObjectInfo) this.allObjectsNearby.get(avatarAnimation.Sender_Field.ID);
        if (sLObjectInfo instanceof SLObjectAvatarInfo) {
            SLObjectAvatarInfo sLObjectAvatarInfo = (SLObjectAvatarInfo) sLObjectInfo;
            sLObjectAvatarInfo.ApplyAvatarAnimation(avatarAnimation);
            if (sLObjectAvatarInfo.isMyAvatar() && sLAvatarControl != null) {
                sLAvatarControl.ApplyAvatarAnimation(sLObjectAvatarInfo, avatarAnimation);
            }
        }
    }

    synchronized void ApplyAvatarAppearance(AvatarAppearance avatarAppearance) {
        SLObjectInfo sLObjectInfo = (SLObjectInfo) this.allObjectsNearby.get(avatarAppearance.Sender_Field.ID);
        if (sLObjectInfo instanceof SLObjectAvatarInfo) {
            ((SLObjectAvatarInfo) sLObjectInfo).ApplyAvatarAppearance(avatarAppearance);
        }
    }

    /* DevToolsApp WARNING: Missing block: B:8:0x001f, code:
            return false;
     */
    synchronized boolean addObject(com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo r6) {
        /*
        r5 = this;
        r2 = 1;
        r4 = 0;
        r1 = 0;
        monitor-enter(r5);
        r0 = r5.uuidsNearby;	 Catch:{ all -> 0x009e }
        r3 = r6.localID;	 Catch:{ all -> 0x009e }
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ all -> 0x009e }
        r0 = r0.containsKey(r3);	 Catch:{ all -> 0x009e }
        if (r0 != 0) goto L_0x001e;
    L_0x0012:
        r0 = r5.allObjectsNearby;	 Catch:{ all -> 0x009e }
        r3 = r6.getId();	 Catch:{ all -> 0x009e }
        r0 = r0.containsKey(r3);	 Catch:{ all -> 0x009e }
        if (r0 == 0) goto L_0x0020;
    L_0x001e:
        monitor-exit(r5);
        return r4;
    L_0x0020:
        r0 = r5.uuidsNearby;	 Catch:{ all -> 0x009e }
        r3 = r6.localID;	 Catch:{ all -> 0x009e }
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ all -> 0x009e }
        r4 = r6.getId();	 Catch:{ all -> 0x009e }
        r0.put(r3, r4);	 Catch:{ all -> 0x009e }
        r0 = r5.allObjectsNearby;	 Catch:{ all -> 0x009e }
        r3 = r6.getId();	 Catch:{ all -> 0x009e }
        r0.put(r3, r6);	 Catch:{ all -> 0x009e }
        r0 = r6.parentID;	 Catch:{ all -> 0x009e }
        if (r0 == 0) goto L_0x00c7;
    L_0x003c:
        r0 = r5.uuidsNearby;	 Catch:{ all -> 0x009e }
        r3 = r6.parentID;	 Catch:{ all -> 0x009e }
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ all -> 0x009e }
        r0 = r0.get(r3);	 Catch:{ all -> 0x009e }
        r0 = (java.util.UUID) r0;	 Catch:{ all -> 0x009e }
        if (r0 == 0) goto L_0x0055;
    L_0x004c:
        r1 = r5.allObjectsNearby;	 Catch:{ all -> 0x009e }
        r0 = r1.get(r0);	 Catch:{ all -> 0x009e }
        r0 = (com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo) r0;	 Catch:{ all -> 0x009e }
        r1 = r0;
    L_0x0055:
        if (r1 == 0) goto L_0x00a3;
    L_0x0057:
        r0 = r1.hierLevel;	 Catch:{ all -> 0x009e }
        r0 = r0 + 1;
        r6.hierLevel = r0;	 Catch:{ all -> 0x009e }
        r0 = r1.isAvatar();	 Catch:{ all -> 0x009e }
        if (r0 != 0) goto L_0x00a1;
    L_0x0063:
        r0 = r1.isAttachment;	 Catch:{ all -> 0x009e }
    L_0x0065:
        r6.setIsAttachmentAll(r0);	 Catch:{ all -> 0x009e }
        r1.addChild(r6);	 Catch:{ all -> 0x009e }
    L_0x006b:
        r0 = r5.orphanObjects;	 Catch:{ all -> 0x009e }
        r1 = r6.localID;	 Catch:{ all -> 0x009e }
        r1 = java.lang.Integer.valueOf(r1);	 Catch:{ all -> 0x009e }
        r0 = r0.remove(r1);	 Catch:{ all -> 0x009e }
        r0 = (java.util.LinkedList) r0;	 Catch:{ all -> 0x009e }
        if (r0 == 0) goto L_0x00d5;
    L_0x007b:
        r3 = r0.iterator();	 Catch:{ all -> 0x009e }
    L_0x007f:
        r0 = r3.hasNext();	 Catch:{ all -> 0x009e }
        if (r0 == 0) goto L_0x00d5;
    L_0x0085:
        r0 = r3.next();	 Catch:{ all -> 0x009e }
        r0 = (com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo) r0;	 Catch:{ all -> 0x009e }
        r1 = r6.hierLevel;	 Catch:{ all -> 0x009e }
        r1 = r1 + 1;
        r0.hierLevel = r1;	 Catch:{ all -> 0x009e }
        r1 = r6.isAttachment;	 Catch:{ all -> 0x009e }
        if (r1 != 0) goto L_0x00d3;
    L_0x0095:
        r1 = r6.isAttachment;	 Catch:{ all -> 0x009e }
    L_0x0097:
        r0.setIsAttachmentAll(r1);	 Catch:{ all -> 0x009e }
        r6.addChild(r0);	 Catch:{ all -> 0x009e }
        goto L_0x007f;
    L_0x009e:
        r0 = move-exception;
        monitor-exit(r5);
        throw r0;
    L_0x00a1:
        r0 = r2;
        goto L_0x0065;
    L_0x00a3:
        r0 = r5.orphanObjects;	 Catch:{ all -> 0x009e }
        r1 = r6.parentID;	 Catch:{ all -> 0x009e }
        r1 = java.lang.Integer.valueOf(r1);	 Catch:{ all -> 0x009e }
        r0 = r0.get(r1);	 Catch:{ all -> 0x009e }
        r0 = (java.util.LinkedList) r0;	 Catch:{ all -> 0x009e }
        if (r0 != 0) goto L_0x00c3;
    L_0x00b3:
        r0 = new java.util.LinkedList;	 Catch:{ all -> 0x009e }
        r0.<init>();	 Catch:{ all -> 0x009e }
        r1 = r5.orphanObjects;	 Catch:{ all -> 0x009e }
        r3 = r6.parentID;	 Catch:{ all -> 0x009e }
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ all -> 0x009e }
        r1.put(r3, r0);	 Catch:{ all -> 0x009e }
    L_0x00c3:
        r0.add(r6);	 Catch:{ all -> 0x009e }
        goto L_0x006b;
    L_0x00c7:
        r0 = r5.rootObjects;	 Catch:{ all -> 0x009e }
        r1 = r6.localID;	 Catch:{ all -> 0x009e }
        r1 = java.lang.Integer.valueOf(r1);	 Catch:{ all -> 0x009e }
        r0.put(r1, r6);	 Catch:{ all -> 0x009e }
        goto L_0x006b;
    L_0x00d3:
        r1 = r2;
        goto L_0x0097;
    L_0x00d5:
        r0 = 0;
        r6.updateSpatialIndex(r0);	 Catch:{ all -> 0x009e }
        monitor-exit(r5);
        return r2;
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
        return (SLObjectInfo) this.allObjectsNearby.get(uuid);
    }

    public ObjectDisplayList getDisplayObjects(ImmutableVector immutableVector, SLObjectFilterInfo sLObjectFilterInfo, MultipleChatterNameRetriever multipleChatterNameRetriever) {
        Collection addDisplayObjects;
        int size;
        boolean z = true;
        Set hashSet = new HashSet();
        synchronized (this) {
            addDisplayObjects = addDisplayObjects(this.rootObjects.values(), sLObjectFilterInfo, immutableVector, true, multipleChatterNameRetriever, hashSet, false);
            size = this.objectNamesQueue.size();
        }
        multipleChatterNameRetriever.retainChatters(hashSet);
        String str = "getDisplayObjects: objectList is %s, load queue %d";
        Object[] objArr = new Object[2];
        objArr[0] = addDisplayObjects != null ? Integer.toString(addDisplayObjects.size()) : "null";
        objArr[1] = Integer.valueOf(size);
        Debug.Printf(str, objArr);
        if (addDisplayObjects != null) {
            Collections.sort(addDisplayObjects, this.objectDisplayInfoComparator);
            return new ObjectDisplayList(ImmutableList.copyOf(addDisplayObjects), size != 0);
        }
        ImmutableList of = ImmutableList.of();
        if (size == 0) {
            z = false;
        }
        return new ObjectDisplayList(of, z);
    }

    @Nullable
    public synchronized SLObjectInfo getObjectInfo(int i) {
        UUID uuid = (UUID) this.uuidsNearby.get(Integer.valueOf(i));
        if (uuid == null) {
            return null;
        }
        return (SLObjectInfo) this.allObjectsNearby.get(uuid);
    }

    public int getObjectLocalID(@Nullable UUID uuid) {
        int i;
        synchronized (this) {
            if (uuid != null) {
                SLObjectInfo sLObjectInfo = (SLObjectInfo) this.allObjectsNearby.get(uuid);
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
            uuid = (UUID) this.uuidsNearby.get(Integer.valueOf(i));
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
        Builder builder = ImmutableList.builder();
        synchronized (this) {
            SLObjectInfo sLObjectInfo = (SLObjectInfo) this.allObjectsNearby.get(uuid);
            if (sLObjectInfo != null) {
                try {
                    for (Object obj : sLObjectInfo.treeNode) {
                        if (obj.isTouchable()) {
                            if (!obj.nameKnown) {
                                sLAgentCircuit.RequestObjectName(obj);
                            }
                            builder.add(obj);
                        }
                    }
                } catch (Throwable e) {
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
        } catch (Throwable e) {
            Debug.Warning(e);
        }
        return;
    }

    public synchronized boolean isParentOrSame(UUID uuid, UUID uuid2) {
        if (uuid2.equals(uuid)) {
            return true;
        }
        SLObjectInfo sLObjectInfo = (SLObjectInfo) this.allObjectsNearby.get(uuid2);
        if (sLObjectInfo != null) {
            for (sLObjectInfo = sLObjectInfo.getParentObject(); sLObjectInfo != null; sLObjectInfo = sLObjectInfo.getParentObject()) {
                if (sLObjectInfo.getId().equals(uuid)) {
                    return true;
                }
            }
        }
        return false;
    }

    /* DevToolsApp WARNING: Removed duplicated region for block: B:60:0x00f1  */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:60:0x00f1  */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:64:0x00f7  */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:60:0x00f1  */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:64:0x00f7  */
    boolean killObject(com.lumiyaviewer.lumiya.slproto.SLAgentCircuit r11, int r12) {
        /*
        r10 = this;
        r7 = 1;
        r6 = 0;
        r4 = 0;
        monitor-enter(r10);
        r1 = r10.uuidsNearby;	 Catch:{ all -> 0x0083 }
        r2 = java.lang.Integer.valueOf(r12);	 Catch:{ all -> 0x0083 }
        r1 = r1.remove(r2);	 Catch:{ all -> 0x0083 }
        r1 = (java.util.UUID) r1;	 Catch:{ all -> 0x0083 }
        if (r1 == 0) goto L_0x011c;
    L_0x0012:
        r2 = r10.objectNamesQueue;	 Catch:{ all -> 0x0083 }
        r2.remove(r1);	 Catch:{ all -> 0x0083 }
        r2 = r10.allObjectsNearby;	 Catch:{ all -> 0x0083 }
        r2 = r2.remove(r1);	 Catch:{ all -> 0x0083 }
        r2 = (com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo) r2;	 Catch:{ all -> 0x0083 }
        if (r2 == 0) goto L_0x011c;
    L_0x0021:
        r3 = 1;
        r2.isDead = r3;	 Catch:{ all -> 0x0083 }
        r3 = r2.parentID;	 Catch:{ all -> 0x0083 }
        if (r3 != 0) goto L_0x0056;
    L_0x0028:
        r3 = r10.rootObjects;	 Catch:{ all -> 0x0083 }
        r5 = java.lang.Integer.valueOf(r12);	 Catch:{ all -> 0x0083 }
        r3.remove(r5);	 Catch:{ all -> 0x0083 }
    L_0x0031:
        r3 = r2.treeNode;	 Catch:{ NoSuchElementException -> 0x0112 }
        r5 = r3.iterator();	 Catch:{ NoSuchElementException -> 0x0112 }
    L_0x0037:
        r3 = r5.hasNext();	 Catch:{ NoSuchElementException -> 0x0112 }
        if (r3 == 0) goto L_0x00b2;
    L_0x003d:
        r3 = r5.next();	 Catch:{ NoSuchElementException -> 0x0112 }
        r3 = (com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo) r3;	 Catch:{ NoSuchElementException -> 0x0112 }
        r8 = r3.isAvatar();	 Catch:{ NoSuchElementException -> 0x0112 }
        if (r8 == 0) goto L_0x00ab;
    L_0x0049:
        if (r4 != 0) goto L_0x0050;
    L_0x004b:
        r4 = new java.util.LinkedList;	 Catch:{ NoSuchElementException -> 0x0112 }
        r4.<init>();	 Catch:{ NoSuchElementException -> 0x0112 }
    L_0x0050:
        r4.add(r3);	 Catch:{ NoSuchElementException -> 0x0112 }
        r3 = r4;
    L_0x0054:
        r4 = r3;
        goto L_0x0037;
    L_0x0056:
        r3 = r10.uuidsNearby;	 Catch:{ all -> 0x0083 }
        r5 = r2.parentID;	 Catch:{ all -> 0x0083 }
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ all -> 0x0083 }
        r3 = r3.get(r5);	 Catch:{ all -> 0x0083 }
        r3 = (java.util.UUID) r3;	 Catch:{ all -> 0x0083 }
        if (r3 == 0) goto L_0x0119;
    L_0x0066:
        r5 = r10.allObjectsNearby;	 Catch:{ all -> 0x0083 }
        r3 = r5.get(r3);	 Catch:{ all -> 0x0083 }
        r3 = (com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo) r3;	 Catch:{ all -> 0x0083 }
    L_0x006e:
        if (r3 == 0) goto L_0x0086;
    L_0x0070:
        r3.removeChild(r2);	 Catch:{ all -> 0x0083 }
        r5 = r3 instanceof com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;	 Catch:{ all -> 0x0083 }
        if (r5 == 0) goto L_0x0031;
    L_0x0077:
        r3 = (com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo) r3;	 Catch:{ all -> 0x0083 }
        r5 = r3.isMyAvatar();	 Catch:{ all -> 0x0083 }
        if (r5 == 0) goto L_0x0031;
    L_0x007f:
        r11.processMyAttachmentUpdate(r3);	 Catch:{ all -> 0x0083 }
        goto L_0x0031;
    L_0x0083:
        r1 = move-exception;
        monitor-exit(r10);
        throw r1;
    L_0x0086:
        r3 = r10.orphanObjects;	 Catch:{ all -> 0x0083 }
        r5 = r2.parentID;	 Catch:{ all -> 0x0083 }
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ all -> 0x0083 }
        r3 = r3.get(r5);	 Catch:{ all -> 0x0083 }
        r3 = (java.util.LinkedList) r3;	 Catch:{ all -> 0x0083 }
        if (r3 == 0) goto L_0x0031;
    L_0x0096:
        r3.remove(r2);	 Catch:{ all -> 0x0083 }
        r3 = r3.isEmpty();	 Catch:{ all -> 0x0083 }
        if (r3 == 0) goto L_0x0031;
    L_0x009f:
        r3 = r10.orphanObjects;	 Catch:{ all -> 0x0083 }
        r5 = r2.parentID;	 Catch:{ all -> 0x0083 }
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ all -> 0x0083 }
        r3.remove(r5);	 Catch:{ all -> 0x0083 }
        goto L_0x0031;
    L_0x00ab:
        r3 = r3.localID;	 Catch:{ NoSuchElementException -> 0x0112 }
        r10.killObject(r11, r3);	 Catch:{ NoSuchElementException -> 0x0112 }
        r3 = r4;
        goto L_0x0054;
    L_0x00b2:
        if (r4 == 0) goto L_0x0117;
    L_0x00b4:
        r8 = r4.iterator();	 Catch:{ NoSuchElementException -> 0x0112 }
        r5 = r6;
    L_0x00b9:
        r3 = r8.hasNext();	 Catch:{ NoSuchElementException -> 0x00e6 }
        if (r3 == 0) goto L_0x0115;
    L_0x00bf:
        r3 = r8.next();	 Catch:{ NoSuchElementException -> 0x00e6 }
        r3 = (com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo) r3;	 Catch:{ NoSuchElementException -> 0x00e6 }
        r2.removeChild(r3);	 Catch:{ NoSuchElementException -> 0x00e6 }
        r4 = 0;
        r3.parentID = r4;	 Catch:{ NoSuchElementException -> 0x00e6 }
        r4 = r3 instanceof com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;	 Catch:{ NoSuchElementException -> 0x00e6 }
        if (r4 == 0) goto L_0x00da;
    L_0x00cf:
        r0 = r3;
        r0 = (com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo) r0;	 Catch:{ NoSuchElementException -> 0x00e6 }
        r4 = r0;
        r4 = r4.isMyAvatar();	 Catch:{ NoSuchElementException -> 0x00e6 }
        if (r4 == 0) goto L_0x00da;
    L_0x00d9:
        r5 = r7;
    L_0x00da:
        r4 = r10.rootObjects;	 Catch:{ NoSuchElementException -> 0x00e6 }
        r9 = r3.localID;	 Catch:{ NoSuchElementException -> 0x00e6 }
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ NoSuchElementException -> 0x00e6 }
        r4.put(r9, r3);	 Catch:{ NoSuchElementException -> 0x00e6 }
        goto L_0x00b9;
    L_0x00e6:
        r3 = move-exception;
    L_0x00e7:
        com.lumiyaviewer.lumiya.Debug.Warning(r3);	 Catch:{ all -> 0x0083 }
        r3 = r5;
    L_0x00eb:
        r2.removeFromSpatialIndex();	 Catch:{ all -> 0x0083 }
        r2 = r3;
    L_0x00ef:
        if (r1 == 0) goto L_0x00f2;
    L_0x00f1:
        r6 = r7;
    L_0x00f2:
        monitor-exit(r10);
        r1 = r10.userManager;
        if (r1 == 0) goto L_0x0111;
    L_0x00f7:
        r1 = r10.userManager;
        r1 = r1.getObjectsManager();
        r1.requestObjectProfileUpdate(r12);
        if (r2 == 0) goto L_0x0111;
    L_0x0102:
        r1 = r10.userManager;
        r1 = r1.getObjectsManager();
        r1 = r1.myAvatarState();
        r2 = com.lumiyaviewer.lumiya.react.SubscriptionSingleKey.Value;
        r1.requestUpdate(r2);
    L_0x0111:
        return r6;
    L_0x0112:
        r3 = move-exception;
        r5 = r6;
        goto L_0x00e7;
    L_0x0115:
        r3 = r5;
        goto L_0x00eb;
    L_0x0117:
        r3 = r6;
        goto L_0x00eb;
    L_0x0119:
        r3 = r4;
        goto L_0x006e;
    L_0x011c:
        r2 = r6;
        goto L_0x00ef;
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

    void setSunHour(float f) {
        Debug.Printf("Windlight: Simulator sun hour set to %f", Float.valueOf(f));
        synchronized (this.simSunHourLock) {
            this.simSunHour = f;
            this.simSunHourDirty = true;
        }
    }

    synchronized boolean updateObjectParent(int i, SLObjectInfo sLObjectInfo) {
        SLObjectInfo sLObjectInfo2 = null;
        synchronized (this) {
            if (i == sLObjectInfo.parentID) {
                return false;
            }
            UUID uuid;
            LinkedList linkedList;
            if (i != 0) {
                uuid = (UUID) this.uuidsNearby.get(Integer.valueOf(i));
                SLObjectInfo sLObjectInfo3 = uuid != null ? (SLObjectInfo) this.allObjectsNearby.get(uuid) : null;
                if (sLObjectInfo3 != null) {
                    sLObjectInfo3.removeChild(sLObjectInfo);
                    sLObjectInfo3.updateSpatialIndex(false);
                }
                linkedList = (LinkedList) this.orphanObjects.get(Integer.valueOf(i));
                if (linkedList != null) {
                    linkedList.remove(sLObjectInfo);
                }
            } else {
                this.rootObjects.remove(Integer.valueOf(sLObjectInfo.localID));
            }
            if (sLObjectInfo.parentID != 0) {
                uuid = (UUID) this.uuidsNearby.get(Integer.valueOf(sLObjectInfo.parentID));
                if (uuid != null) {
                    sLObjectInfo2 = (SLObjectInfo) this.allObjectsNearby.get(uuid);
                }
                if (sLObjectInfo2 != null) {
                    sLObjectInfo.hierLevel = sLObjectInfo2.hierLevel + 1;
                    sLObjectInfo.setIsAttachmentAll(!sLObjectInfo2.isAvatar() ? sLObjectInfo2.isAttachment : true);
                    sLObjectInfo2.addChild(sLObjectInfo);
                } else {
                    linkedList = (LinkedList) this.orphanObjects.get(Integer.valueOf(sLObjectInfo.parentID));
                    if (linkedList == null) {
                        linkedList = new LinkedList();
                        this.orphanObjects.put(Integer.valueOf(sLObjectInfo.parentID), linkedList);
                    }
                    linkedList.add(sLObjectInfo);
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
