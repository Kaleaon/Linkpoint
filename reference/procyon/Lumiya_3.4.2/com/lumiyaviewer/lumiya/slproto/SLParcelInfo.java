// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.render.spatial.DrawListObjectEntry;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.List;
import com.lumiyaviewer.lumiya.Debug;
import java.util.HashSet;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAnimation;
import com.google.common.base.Strings;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.utils.LinkedTreeNode;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.slproto.objects.SLPrimObjectDisplayInfoWithChildren;
import com.lumiyaviewer.lumiya.slproto.objects.SLPrimObjectDisplayInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLAvatarObjectDisplayInfo;
import java.util.Collection;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Set;
import com.lumiyaviewer.lumiya.slproto.users.MultipleChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectFilterInfo;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainData;
import java.util.LinkedList;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectDisplayInfo;
import java.util.Comparator;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import java.util.UUID;
import java.util.Map;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;

public class SLParcelInfo
{
    private SLObjectAvatarInfo agentAvatar;
    private final Object agentAvatarLock;
    public final Map<UUID, SLObjectInfo> allObjectsNearby;
    private float drawDistance;
    private final Comparator<SLObjectDisplayInfo> objectDisplayInfoComparator;
    public final Map<UUID, SLObjectInfo> objectNamesQueue;
    private final Map<Integer, LinkedList<SLObjectInfo>> orphanObjects;
    private final Map<Integer, SLObjectInfo> rootObjects;
    private float simSunHour;
    private boolean simSunHourDirty;
    private final Object simSunHourLock;
    public final TerrainData terrainData;
    private volatile UserManager userManager;
    public final Map<Integer, UUID> uuidsNearby;
    
    public SLParcelInfo() {
        this.drawDistance = 0.0f;
        this.terrainData = new TerrainData();
        this.agentAvatarLock = new Object();
        this.agentAvatar = null;
        this.simSunHourLock = new Object();
        this.simSunHour = 0.5f;
        this.simSunHourDirty = true;
        this.uuidsNearby = new HashMap<Integer, UUID>();
        this.allObjectsNearby = new ConcurrentHashMap<UUID, SLObjectInfo>(1024, 0.75f, 1);
        this.rootObjects = new ConcurrentHashMap<Integer, SLObjectInfo>(128, 0.75f, 1);
        this.orphanObjects = new HashMap<Integer, LinkedList<SLObjectInfo>>();
        this.objectNamesQueue = Collections.synchronizedMap(new LinkedHashMap<UUID, SLObjectInfo>());
        this.objectDisplayInfoComparator = new _$Lambda$1YF5tPpIlUnjvWeNVttYc5eIlFY();
    }
    
    @Nullable
    private ArrayList<SLObjectDisplayInfo> addDisplayObjects(final Iterable<SLObjectInfo> iterable, final SLObjectFilterInfo slObjectFilterInfo, final ImmutableVector immutableVector, final boolean b, final MultipleChatterNameRetriever multipleChatterNameRetriever, final Set<UUID> set, final boolean b2) {
        final Iterator<SLObjectInfo> iterator = iterable.iterator();
        Object o = null;
    Label_0260_Outer:
        while (iterator.hasNext()) {
            final SLObjectInfo slObjectInfo = iterator.next();
            Object o2 = o;
            while (true) {
                Label_0428: {
                    if (slObjectInfo == null) {
                        break Label_0428;
                    }
                    final LinkedTreeNode<SLObjectInfo> treeNode = slObjectInfo.treeNode;
                    ArrayList<SLObjectDisplayInfo> addDisplayObjects;
                    if (treeNode.hasChildren()) {
                        addDisplayObjects = this.addDisplayObjects(treeNode, slObjectFilterInfo, immutableVector, false, multipleChatterNameRetriever, set, slObjectInfo.isAvatar() || b2);
                    }
                    else {
                        addDisplayObjects = null;
                    }
                    final LLVector3 absolutePosition = slObjectInfo.getAbsolutePosition();
                    final float distanceTo = immutableVector.distanceTo(absolutePosition.x, absolutePosition.y, absolutePosition.z);
                    final boolean b3 = addDisplayObjects != null && (addDisplayObjects.isEmpty() ^ true);
                    final boolean objectMatches = slObjectFilterInfo.objectMatches(slObjectInfo, distanceTo, b2);
                    if (!b3) {
                        o2 = o;
                        if (!objectMatches) {
                            break Label_0428;
                        }
                    }
                    final String knownName = this.getKnownName(slObjectInfo, multipleChatterNameRetriever, set);
                    int nameMatches = slObjectFilterInfo.nameMatches(knownName) ? 1 : 0;
                    if (!b3) {
                        o2 = o;
                        if (nameMatches == 0) {
                            break Label_0428;
                        }
                    }
                    int n;
                    if (b3) {
                        if (!objectMatches) {
                            nameMatches = 0;
                        }
                        n = (nameMatches ^ 0x1);
                    }
                    else {
                        n = 0;
                    }
                    Object o3 = o;
                    if (o == null) {
                        o3 = new ArrayList<Object>();
                    }
                    if (b) {
                        if (slObjectInfo.isAvatar()) {
                            ImmutableList<SLObjectDisplayInfo> list;
                            if (addDisplayObjects != null) {
                                list = ImmutableList.copyOf((Collection<? extends SLObjectDisplayInfo>)addDisplayObjects);
                            }
                            else {
                                list = ImmutableList.of();
                            }
                            ((ArrayList<SLAvatarObjectDisplayInfo>)o3).add(new SLAvatarObjectDisplayInfo(knownName, slObjectInfo, distanceTo, list, (boolean)(n != 0)));
                        }
                        else if (addDisplayObjects == null || addDisplayObjects.isEmpty()) {
                            ((ArrayList<SLPrimObjectDisplayInfo>)o3).add(new SLPrimObjectDisplayInfo(slObjectInfo, distanceTo));
                        }
                        else {
                            ((ArrayList<SLPrimObjectDisplayInfoWithChildren>)o3).add(new SLPrimObjectDisplayInfoWithChildren(slObjectInfo, distanceTo, ImmutableList.copyOf((Collection<? extends SLObjectDisplayInfo>)addDisplayObjects), (boolean)(n != 0)));
                        }
                    }
                    else {
                        SLObjectDisplayInfo e;
                        if (slObjectInfo.isAvatar()) {
                            e = new SLAvatarObjectDisplayInfo(knownName, slObjectInfo, distanceTo, ImmutableList.of(), (boolean)(n != 0));
                        }
                        else {
                            e = new SLPrimObjectDisplayInfo(slObjectInfo, distanceTo);
                        }
                        ((ArrayList<SLPrimObjectDisplayInfoWithChildren>)o3).add((SLPrimObjectDisplayInfoWithChildren)e);
                        o2 = o3;
                        if (addDisplayObjects == null) {
                            break Label_0428;
                        }
                        ((ArrayList<Object>)o3).addAll(addDisplayObjects);
                    }
                    o = o3;
                    continue Label_0260_Outer;
                }
                Object o3 = o2;
                continue;
            }
        }
        return (ArrayList<SLObjectDisplayInfo>)o;
    }
    
    @Nullable
    private String getKnownName(final SLObjectInfo slObjectInfo, final MultipleChatterNameRetriever multipleChatterNameRetriever, final Set<UUID> set) {
        final String s = null;
        if (!slObjectInfo.isAvatar()) {
            if (!slObjectInfo.nameKnown && (this.objectNamesQueue.containsKey(slObjectInfo.getId()) ^ true)) {
                this.objectNamesQueue.put(slObjectInfo.getId(), slObjectInfo);
            }
            String nullToEmpty = s;
            if (slObjectInfo.nameKnown) {
                nullToEmpty = Strings.nullToEmpty(slObjectInfo.name);
            }
            return nullToEmpty;
        }
        final UUID id = slObjectInfo.getId();
        if (id != null) {
            set.add(id);
            return multipleChatterNameRetriever.addChatter(id);
        }
        return null;
    }
    
    void ApplyAvatarAnimation(final AvatarAnimation avatarAnimation, final SLAvatarControl slAvatarControl) {
        synchronized (this) {
            final SLObjectInfo slObjectInfo = this.allObjectsNearby.get(avatarAnimation.Sender_Field.ID);
            if (slObjectInfo instanceof SLObjectAvatarInfo) {
                final SLObjectAvatarInfo slObjectAvatarInfo = (SLObjectAvatarInfo)slObjectInfo;
                slObjectAvatarInfo.ApplyAvatarAnimation(avatarAnimation);
                if (slObjectAvatarInfo.isMyAvatar() && slAvatarControl != null) {
                    slAvatarControl.ApplyAvatarAnimation(slObjectAvatarInfo, avatarAnimation);
                }
            }
        }
    }
    
    void ApplyAvatarAppearance(final AvatarAppearance avatarAppearance) {
        synchronized (this) {
            final SLObjectInfo slObjectInfo = this.allObjectsNearby.get(avatarAppearance.Sender_Field.ID);
            if (slObjectInfo instanceof SLObjectAvatarInfo) {
                ((SLObjectAvatarInfo)slObjectInfo).ApplyAvatarAppearance(avatarAppearance);
            }
        }
    }
    
    boolean addObject(final SLObjectInfo e) {
        Object o;
        Object iterator;
        boolean isAttachment;
        boolean isAttachment2;
        Label_0165_Outer:Label_0240_Outer:
        while (true) {
            o = null;
            while (true) {
            Label_0348:
                while (true) {
                    Label_0327: {
                        Label_0263: {
                            while (true) {
                                synchronized (this) {
                                    if (this.uuidsNearby.containsKey(e.localID) || this.allObjectsNearby.containsKey(e.getId())) {
                                        return false;
                                    }
                                    this.uuidsNearby.put(e.localID, e.getId());
                                    this.allObjectsNearby.put(e.getId(), e);
                                    if (e.parentID == 0) {
                                        break Label_0327;
                                    }
                                    iterator = this.uuidsNearby.get(e.parentID);
                                    if (iterator != null) {
                                        o = this.allObjectsNearby.get(iterator);
                                    }
                                    if (o == null) {
                                        break Label_0263;
                                    }
                                    e.hierLevel = ((SLObjectInfo)o).hierLevel + 1;
                                    if (!((SLObjectInfo)o).isAvatar()) {
                                        isAttachment = ((SLObjectInfo)o).isAttachment;
                                        e.setIsAttachmentAll(isAttachment);
                                        ((SLObjectInfo)o).addChild(e);
                                        o = this.orphanObjects.remove(e.localID);
                                        if (o != null) {
                                            iterator = ((Iterable<Object>)o).iterator();
                                            while (((Iterator)iterator).hasNext()) {
                                                o = ((Iterator)iterator).next();
                                                ((SLObjectInfo)o).hierLevel = e.hierLevel + 1;
                                                if (e.isAttachment) {
                                                    break Label_0348;
                                                }
                                                isAttachment2 = e.isAttachment;
                                                ((SLObjectInfo)o).setIsAttachmentAll(isAttachment2);
                                                e.addChild((SLObjectInfo)o);
                                            }
                                            break;
                                        }
                                        break;
                                    }
                                }
                                isAttachment = true;
                                continue Label_0165_Outer;
                            }
                        }
                        iterator = (o = this.orphanObjects.get(e.parentID));
                        if (iterator == null) {
                            o = new LinkedList();
                            this.orphanObjects.put(e.parentID, (LinkedList<SLObjectInfo>)o);
                        }
                        ((LinkedList<SLObjectInfo>)o).add(e);
                        continue Label_0240_Outer;
                    }
                    this.rootObjects.put(e.localID, e);
                    continue Label_0240_Outer;
                }
                isAttachment2 = true;
                continue;
            }
        }
        e.updateSpatialIndex(false);
        monitorexit(this);
        return true;
    }
    
    @Nullable
    public SLObjectAvatarInfo getAgentAvatar() {
        synchronized (this.agentAvatarLock) {
            return this.agentAvatar;
        }
    }
    
    public SLObjectInfo getAvatarObject(final UUID uuid) {
        synchronized (this) {
            return this.allObjectsNearby.get(uuid);
        }
    }
    
    public ObjectsManager.ObjectDisplayList getDisplayObjects(final ImmutableVector immutableVector, SLObjectFilterInfo addDisplayObjects, final MultipleChatterNameRetriever multipleChatterNameRetriever) {
        boolean b = false;
        int size = 0;
    Label_0113_Outer:
        while (true) {
            b = true;
            final HashSet set = new HashSet();
            while (true) {
            Label_0136:
                while (true) {
                    synchronized (this) {
                        addDisplayObjects = (SLObjectFilterInfo)this.addDisplayObjects(this.rootObjects.values(), addDisplayObjects, immutableVector, true, multipleChatterNameRetriever, set, false);
                        size = this.objectNamesQueue.size();
                        monitorexit(this);
                        multipleChatterNameRetriever.retainChatters(set);
                        if (addDisplayObjects != null) {
                            final String string = Integer.toString(((ArrayList)addDisplayObjects).size());
                            Debug.Printf("getDisplayObjects: objectList is %s, load queue %d", string, size);
                            if (addDisplayObjects == null) {
                                break;
                            }
                            Collections.sort((List<Object>)addDisplayObjects, (Comparator<? super Object>)this.objectDisplayInfoComparator);
                            final ImmutableList<Object> copy = (ImmutableList<Object>)ImmutableList.copyOf((Collection<? extends SLObjectDisplayInfo>)addDisplayObjects);
                            if (size != 0) {
                                b = true;
                                return new ObjectsManager.ObjectDisplayList((ImmutableList<SLObjectDisplayInfo>)copy, b);
                            }
                            break Label_0136;
                        }
                    }
                    final String string = "null";
                    continue Label_0113_Outer;
                }
                b = false;
                continue;
            }
        }
        final ImmutableList<Object> of = (ImmutableList<Object>)ImmutableList.of();
        if (size == 0) {
            b = false;
        }
        return new ObjectsManager.ObjectDisplayList((ImmutableList<SLObjectDisplayInfo>)of, b);
    }
    
    @Nullable
    public SLObjectInfo getObjectInfo(final int i) {
        synchronized (this) {
            final UUID uuid = this.uuidsNearby.get(i);
            if (uuid == null) {
                return null;
            }
            return this.allObjectsNearby.get(uuid);
        }
    }
    
    public int getObjectLocalID(@Nullable final UUID uuid) {
        monitorenter(this);
        if (uuid == null) {
            return -1;
        }
        try {
            final SLObjectInfo slObjectInfo = this.allObjectsNearby.get(uuid);
            if (slObjectInfo != null) {
                return slObjectInfo.localID;
            }
        }
        finally {
            monitorexit(this);
        }
        return -1;
    }
    
    @Nullable
    public UUID getObjectUUID(final int i) {
        synchronized (this) {
            return this.uuidsNearby.get(i);
        }
    }
    
    public boolean getSunHour(final float[] array, final boolean b) {
        synchronized (this.simSunHourLock) {
            if (this.simSunHourDirty || b) {
                array[0] = this.simSunHour;
                this.simSunHourDirty = false;
                return true;
            }
            return false;
        }
    }
    
    public ImmutableList<SLObjectInfo> getUserTouchableObjects(final SLAgentCircuit slAgentCircuit, final UUID uuid) {
        final ImmutableList.Builder<SLObjectInfo> builder = ImmutableList.builder();
        synchronized (this) {
            final SLObjectInfo slObjectInfo = this.allObjectsNearby.get(uuid);
            if (slObjectInfo != null) {
                try {
                    for (final SLObjectInfo slObjectInfo2 : slObjectInfo.treeNode) {
                        if (slObjectInfo2.isTouchable()) {
                            if (!slObjectInfo2.nameKnown) {
                                slAgentCircuit.RequestObjectName(slObjectInfo2);
                            }
                            builder.add(slObjectInfo2);
                        }
                    }
                }
                catch (final NoSuchElementException ex) {
                    Debug.Warning(ex);
                }
            }
            return builder.build();
        }
    }
    
    public void initSpatialIndex() {
        synchronized (this) {
            try {
                final Iterator<Object> iterator = this.rootObjects.values().iterator();
                while (iterator.hasNext()) {
                    iterator.next().updateSpatialIndex(true);
                }
            }
            catch (final ConcurrentModificationException ex) {
                Debug.Warning(ex);
            }
        }
    }
    
    public boolean isParentOrSame(final UUID uuid, final UUID uuid2) {
        synchronized (this) {
            if (uuid2.equals(uuid)) {
                return true;
            }
            final SLObjectInfo slObjectInfo = this.allObjectsNearby.get(uuid2);
            if (slObjectInfo != null) {
                for (SLObjectInfo slObjectInfo2 = slObjectInfo.getParentObject(); slObjectInfo2 != null; slObjectInfo2 = slObjectInfo2.getParentObject()) {
                    if (slObjectInfo2.getId().equals(uuid)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    boolean killObject(final SLAgentCircuit p0, final int p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     2: aconst_null    
        //     3: astore          4
        //     5: aload_0        
        //     6: monitorenter   
        //     7: aload_0        
        //     8: getfield        com/lumiyaviewer/lumiya/slproto/SLParcelInfo.uuidsNearby:Ljava/util/Map;
        //    11: iload_2        
        //    12: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //    15: invokeinterface java/util/Map.remove:(Ljava/lang/Object;)Ljava/lang/Object;
        //    20: checkcast       Ljava/util/UUID;
        //    23: astore          5
        //    25: aload           5
        //    27: ifnull          563
        //    30: aload_0        
        //    31: getfield        com/lumiyaviewer/lumiya/slproto/SLParcelInfo.objectNamesQueue:Ljava/util/Map;
        //    34: aload           5
        //    36: invokeinterface java/util/Map.remove:(Ljava/lang/Object;)Ljava/lang/Object;
        //    41: pop            
        //    42: aload_0        
        //    43: getfield        com/lumiyaviewer/lumiya/slproto/SLParcelInfo.allObjectsNearby:Ljava/util/Map;
        //    46: aload           5
        //    48: invokeinterface java/util/Map.remove:(Ljava/lang/Object;)Ljava/lang/Object;
        //    53: checkcast       Lcom/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo;
        //    56: astore          6
        //    58: aload           6
        //    60: ifnull          563
        //    63: aload           6
        //    65: iconst_1       
        //    66: putfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.isDead:Z
        //    69: aload           6
        //    71: getfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.parentID:I
        //    74: ifne            173
        //    77: aload_0        
        //    78: getfield        com/lumiyaviewer/lumiya/slproto/SLParcelInfo.rootObjects:Ljava/util/Map;
        //    81: iload_2        
        //    82: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //    85: invokeinterface java/util/Map.remove:(Ljava/lang/Object;)Ljava/lang/Object;
        //    90: pop            
        //    91: aload           6
        //    93: getfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.treeNode:Lcom/lumiyaviewer/lumiya/utils/LinkedTreeNode;
        //    96: invokeinterface java/lang/Iterable.iterator:()Ljava/util/Iterator;
        //   101: astore          7
        //   103: aload           4
        //   105: astore          8
        //   107: aload           7
        //   109: invokeinterface java/util/Iterator.hasNext:()Z
        //   114: ifeq            343
        //   117: aload           7
        //   119: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   124: checkcast       Lcom/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo;
        //   127: astore          9
        //   129: aload           9
        //   131: invokevirtual   com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.isAvatar:()Z
        //   134: ifeq            329
        //   137: aload           8
        //   139: astore          4
        //   141: aload           8
        //   143: ifnonnull       156
        //   146: new             Ljava/util/LinkedList;
        //   149: astore          4
        //   151: aload           4
        //   153: invokespecial   java/util/LinkedList.<init>:()V
        //   156: aload           4
        //   158: aload           9
        //   160: invokeinterface java/util/List.add:(Ljava/lang/Object;)Z
        //   165: pop            
        //   166: aload           4
        //   168: astore          8
        //   170: goto            107
        //   173: aload_0        
        //   174: getfield        com/lumiyaviewer/lumiya/slproto/SLParcelInfo.uuidsNearby:Ljava/util/Map;
        //   177: aload           6
        //   179: getfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.parentID:I
        //   182: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   185: invokeinterface java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   190: checkcast       Ljava/util/UUID;
        //   193: astore          8
        //   195: aload           8
        //   197: ifnull          557
        //   200: aload_0        
        //   201: getfield        com/lumiyaviewer/lumiya/slproto/SLParcelInfo.allObjectsNearby:Ljava/util/Map;
        //   204: aload           8
        //   206: invokeinterface java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   211: checkcast       Lcom/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo;
        //   214: astore          8
        //   216: aload           8
        //   218: ifnull          265
        //   221: aload           8
        //   223: aload           6
        //   225: invokevirtual   com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.removeChild:(Lcom/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo;)V
        //   228: aload           8
        //   230: instanceof      Lcom/lumiyaviewer/lumiya/slproto/objects/SLObjectAvatarInfo;
        //   233: ifeq            91
        //   236: aload           8
        //   238: checkcast       Lcom/lumiyaviewer/lumiya/slproto/objects/SLObjectAvatarInfo;
        //   241: astore          8
        //   243: aload           8
        //   245: invokevirtual   com/lumiyaviewer/lumiya/slproto/objects/SLObjectAvatarInfo.isMyAvatar:()Z
        //   248: ifeq            91
        //   251: aload_1        
        //   252: aload           8
        //   254: invokevirtual   com/lumiyaviewer/lumiya/slproto/SLAgentCircuit.processMyAttachmentUpdate:(Lcom/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo;)V
        //   257: goto            91
        //   260: astore_1       
        //   261: aload_0        
        //   262: monitorexit    
        //   263: aload_1        
        //   264: athrow         
        //   265: aload_0        
        //   266: getfield        com/lumiyaviewer/lumiya/slproto/SLParcelInfo.orphanObjects:Ljava/util/Map;
        //   269: aload           6
        //   271: getfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.parentID:I
        //   274: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   277: invokeinterface java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   282: checkcast       Ljava/util/LinkedList;
        //   285: astore          8
        //   287: aload           8
        //   289: ifnull          91
        //   292: aload           8
        //   294: aload           6
        //   296: invokevirtual   java/util/LinkedList.remove:(Ljava/lang/Object;)Z
        //   299: pop            
        //   300: aload           8
        //   302: invokevirtual   java/util/LinkedList.isEmpty:()Z
        //   305: ifeq            91
        //   308: aload_0        
        //   309: getfield        com/lumiyaviewer/lumiya/slproto/SLParcelInfo.orphanObjects:Ljava/util/Map;
        //   312: aload           6
        //   314: getfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.parentID:I
        //   317: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   320: invokeinterface java/util/Map.remove:(Ljava/lang/Object;)Ljava/lang/Object;
        //   325: pop            
        //   326: goto            91
        //   329: aload_0        
        //   330: aload_1        
        //   331: aload           9
        //   333: getfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.localID:I
        //   336: invokevirtual   com/lumiyaviewer/lumiya/slproto/SLParcelInfo.killObject:(Lcom/lumiyaviewer/lumiya/slproto/SLAgentCircuit;I)Z
        //   339: pop            
        //   340: goto            170
        //   343: aload           8
        //   345: ifnull          551
        //   348: aload           8
        //   350: invokeinterface java/lang/Iterable.iterator:()Ljava/util/Iterator;
        //   355: astore_1       
        //   356: iconst_0       
        //   357: istore          10
        //   359: iload           10
        //   361: istore          11
        //   363: aload_1        
        //   364: invokeinterface java/util/Iterator.hasNext:()Z
        //   369: ifeq            548
        //   372: iload           10
        //   374: istore          11
        //   376: aload_1        
        //   377: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   382: checkcast       Lcom/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo;
        //   385: astore          8
        //   387: iload           10
        //   389: istore          11
        //   391: aload           6
        //   393: aload           8
        //   395: invokevirtual   com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.removeChild:(Lcom/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo;)V
        //   398: iload           10
        //   400: istore          11
        //   402: aload           8
        //   404: iconst_0       
        //   405: putfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.parentID:I
        //   408: iload           10
        //   410: istore          12
        //   412: iload           10
        //   414: istore          11
        //   416: aload           8
        //   418: instanceof      Lcom/lumiyaviewer/lumiya/slproto/objects/SLObjectAvatarInfo;
        //   421: ifeq            446
        //   424: iload           10
        //   426: istore          12
        //   428: iload           10
        //   430: istore          11
        //   432: aload           8
        //   434: checkcast       Lcom/lumiyaviewer/lumiya/slproto/objects/SLObjectAvatarInfo;
        //   437: invokevirtual   com/lumiyaviewer/lumiya/slproto/objects/SLObjectAvatarInfo.isMyAvatar:()Z
        //   440: ifeq            446
        //   443: iconst_1       
        //   444: istore          12
        //   446: iload           12
        //   448: istore          11
        //   450: aload_0        
        //   451: getfield        com/lumiyaviewer/lumiya/slproto/SLParcelInfo.rootObjects:Ljava/util/Map;
        //   454: aload           8
        //   456: getfield        com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.localID:I
        //   459: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   462: aload           8
        //   464: invokeinterface java/util/Map.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   469: pop            
        //   470: iload           12
        //   472: istore          10
        //   474: goto            359
        //   477: astore_1       
        //   478: aload_1        
        //   479: invokestatic    com/lumiyaviewer/lumiya/Debug.Warning:(Ljava/lang/Throwable;)V
        //   482: iload           11
        //   484: istore          10
        //   486: aload           6
        //   488: invokevirtual   com/lumiyaviewer/lumiya/slproto/objects/SLObjectInfo.removeFromSpatialIndex:()V
        //   491: aload           5
        //   493: ifnull          498
        //   496: iconst_1       
        //   497: istore_3       
        //   498: aload_0        
        //   499: monitorexit    
        //   500: aload_0        
        //   501: getfield        com/lumiyaviewer/lumiya/slproto/SLParcelInfo.userManager:Lcom/lumiyaviewer/lumiya/slproto/users/manager/UserManager;
        //   504: ifnull          539
        //   507: aload_0        
        //   508: getfield        com/lumiyaviewer/lumiya/slproto/SLParcelInfo.userManager:Lcom/lumiyaviewer/lumiya/slproto/users/manager/UserManager;
        //   511: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/manager/UserManager.getObjectsManager:()Lcom/lumiyaviewer/lumiya/slproto/users/manager/ObjectsManager;
        //   514: iload_2        
        //   515: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/manager/ObjectsManager.requestObjectProfileUpdate:(I)V
        //   518: iload           10
        //   520: ifeq            539
        //   523: aload_0        
        //   524: getfield        com/lumiyaviewer/lumiya/slproto/SLParcelInfo.userManager:Lcom/lumiyaviewer/lumiya/slproto/users/manager/UserManager;
        //   527: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/manager/UserManager.getObjectsManager:()Lcom/lumiyaviewer/lumiya/slproto/users/manager/ObjectsManager;
        //   530: invokevirtual   com/lumiyaviewer/lumiya/slproto/users/manager/ObjectsManager.myAvatarState:()Lcom/lumiyaviewer/lumiya/react/SubscriptionPool;
        //   533: getstatic       com/lumiyaviewer/lumiya/react/SubscriptionSingleKey.Value:Lcom/lumiyaviewer/lumiya/react/SubscriptionSingleKey;
        //   536: invokevirtual   com/lumiyaviewer/lumiya/react/SubscriptionPool.requestUpdate:(Ljava/lang/Object;)V
        //   539: iload_3        
        //   540: ireturn        
        //   541: astore_1       
        //   542: iconst_0       
        //   543: istore          11
        //   545: goto            478
        //   548: goto            486
        //   551: iconst_0       
        //   552: istore          10
        //   554: goto            486
        //   557: aconst_null    
        //   558: astore          8
        //   560: goto            216
        //   563: iconst_0       
        //   564: istore          10
        //   566: goto            491
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                              
        //  -----  -----  -----  -----  ----------------------------------
        //  7      25     260    265    Any
        //  30     58     260    265    Any
        //  63     91     260    265    Any
        //  91     103    541    548    Ljava/util/NoSuchElementException;
        //  91     103    260    265    Any
        //  107    137    541    548    Ljava/util/NoSuchElementException;
        //  107    137    260    265    Any
        //  146    156    541    548    Ljava/util/NoSuchElementException;
        //  146    156    260    265    Any
        //  156    166    541    548    Ljava/util/NoSuchElementException;
        //  156    166    260    265    Any
        //  173    195    260    265    Any
        //  200    216    260    265    Any
        //  221    257    260    265    Any
        //  265    287    260    265    Any
        //  292    326    260    265    Any
        //  329    340    541    548    Ljava/util/NoSuchElementException;
        //  329    340    260    265    Any
        //  348    356    541    548    Ljava/util/NoSuchElementException;
        //  348    356    260    265    Any
        //  363    372    477    478    Ljava/util/NoSuchElementException;
        //  363    372    260    265    Any
        //  376    387    477    478    Ljava/util/NoSuchElementException;
        //  376    387    260    265    Any
        //  391    398    477    478    Ljava/util/NoSuchElementException;
        //  391    398    260    265    Any
        //  402    408    477    478    Ljava/util/NoSuchElementException;
        //  402    408    260    265    Any
        //  416    424    477    478    Ljava/util/NoSuchElementException;
        //  416    424    260    265    Any
        //  432    443    477    478    Ljava/util/NoSuchElementException;
        //  432    443    260    265    Any
        //  450    470    477    478    Ljava/util/NoSuchElementException;
        //  450    470    260    265    Any
        //  478    482    260    265    Any
        //  486    491    260    265    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0091:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void reset(final UserManager userManager) {
        synchronized (this) {
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
            for (final SLObjectInfo slObjectInfo : this.allObjectsNearby.values()) {
                final DrawListObjectEntry existingDrawListEntry = slObjectInfo.getExistingDrawListEntry();
                if (existingDrawListEntry != null) {
                    existingDrawListEntry.requestEntryRemoval();
                }
                slObjectInfo.clearDrawListEntry();
            }
        }
        this.allObjectsNearby.clear();
        this.rootObjects.clear();
        this.orphanObjects.clear();
        this.objectNamesQueue.clear();
        this.terrainData.reset();
        this.simSunHour = 0.5f;
        this.simSunHourDirty = false;
        monitorexit(this);
    }
    
    public void setAgentAvatar(final SLObjectAvatarInfo agentAvatar) {
        synchronized (this.agentAvatarLock) {
            this.agentAvatar = agentAvatar;
        }
    }
    
    public void setDrawDistance(final float drawDistance) {
        synchronized (this) {
            if (this.drawDistance != drawDistance) {
                this.drawDistance = drawDistance;
            }
        }
    }
    
    void setSunHour(final float n) {
        Debug.Printf("Windlight: Simulator sun hour set to %f", n);
        synchronized (this.simSunHourLock) {
            this.simSunHour = n;
            this.simSunHourDirty = true;
        }
    }
    
    boolean updateObjectParent(final int n, final SLObjectInfo slObjectInfo) {
        while (true) {
            LinkedList<SLObjectInfo> list = null;
        Label_0205_Outer:
            while (true) {
                Label_0342: {
                    while (true) {
                        Label_0311: {
                            Label_0245: {
                                while (true) {
                                    synchronized (this) {
                                        if (n == slObjectInfo.parentID) {
                                            return false;
                                        }
                                        if (n != 0) {
                                            Object o = this.uuidsNearby.get(n);
                                            if (o == null) {
                                                break Label_0342;
                                            }
                                            o = this.allObjectsNearby.get(o);
                                            if (o != null) {
                                                ((SLObjectInfo)o).removeChild(slObjectInfo);
                                                ((SLObjectInfo)o).updateSpatialIndex(false);
                                            }
                                            o = this.orphanObjects.get(n);
                                            if (o != null) {
                                                ((LinkedList)o).remove(slObjectInfo);
                                            }
                                        }
                                        else {
                                            this.rootObjects.remove(slObjectInfo.localID);
                                        }
                                        if (slObjectInfo.parentID == 0) {
                                            break Label_0311;
                                        }
                                        final UUID uuid = this.uuidsNearby.get(slObjectInfo.parentID);
                                        Object o = list;
                                        if (uuid != null) {
                                            o = this.allObjectsNearby.get(uuid);
                                        }
                                        if (o == null) {
                                            break Label_0245;
                                        }
                                        slObjectInfo.hierLevel = ((SLObjectInfo)o).hierLevel + 1;
                                        if (!((SLObjectInfo)o).isAvatar()) {
                                            final boolean isAttachment = ((SLObjectInfo)o).isAttachment;
                                            slObjectInfo.setIsAttachmentAll(isAttachment);
                                            ((SLObjectInfo)o).addChild(slObjectInfo);
                                            slObjectInfo.updateSpatialIndex(false);
                                            return true;
                                        }
                                    }
                                    final boolean isAttachment = true;
                                    continue Label_0205_Outer;
                                }
                            }
                            Object o;
                            list = (LinkedList<SLObjectInfo>)(o = this.orphanObjects.get(slObjectInfo.parentID));
                            if (list == null) {
                                o = new LinkedList<SLObjectInfo>();
                                this.orphanObjects.put(slObjectInfo.parentID, (LinkedList<SLObjectInfo>)o);
                            }
                            ((LinkedList<SLObjectInfo>)o).add(slObjectInfo);
                            continue;
                        }
                        slObjectInfo.hierLevel = 0;
                        slObjectInfo.setIsAttachmentAll(false);
                        this.rootObjects.put(slObjectInfo.localID, slObjectInfo);
                        continue;
                    }
                }
                Object o = null;
                continue;
            }
        }
    }
}
