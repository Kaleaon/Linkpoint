// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
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
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto:
//            SLAgentCircuit

public class SLParcelInfo
{

    private SLObjectAvatarInfo agentAvatar;
    private final Object agentAvatarLock = new Object();
    public final Map allObjectsNearby = new ConcurrentHashMap(1024, 0.75F, 1);
    private float drawDistance;
    private final Comparator objectDisplayInfoComparator = new _2D_.Lambda._cls1YF5tPpIlUnjvWeNVttYc5eIlFY();
    public final Map objectNamesQueue = Collections.synchronizedMap(new LinkedHashMap());
    private final Map orphanObjects = new HashMap();
    private final Map rootObjects = new ConcurrentHashMap(128, 0.75F, 1);
    private float simSunHour;
    private boolean simSunHourDirty;
    private final Object simSunHourLock = new Object();
    public final TerrainData terrainData = new TerrainData();
    private volatile UserManager userManager;
    public final Map uuidsNearby = new HashMap();

    public SLParcelInfo()
    {
        drawDistance = 0.0F;
        agentAvatar = null;
        simSunHour = 0.5F;
        simSunHourDirty = true;
    }

    private ArrayList addDisplayObjects(Iterable iterable, SLObjectFilterInfo slobjectfilterinfo, ImmutableVector immutablevector, boolean flag, MultipleChatterNameRetriever multiplechatternameretriever, Set set, boolean flag1)
    {
        Object obj;
        Iterator iterator;
        iterator = iterable.iterator();
        obj = null;
_L2:
        if (!iterator.hasNext())
        {
            break; /* Loop/switch isn't completed */
        }
        SLObjectInfo slobjectinfo = (SLObjectInfo)iterator.next();
        Object obj1 = obj;
        if (slobjectinfo == null)
        {
            break MISSING_BLOCK_LABEL_428;
        }
        iterable = slobjectinfo.treeNode;
        float f;
        ArrayList arraylist;
        String s;
        boolean flag2;
        boolean flag3;
        if (iterable.hasChildren())
        {
            boolean flag4;
            if (!slobjectinfo.isAvatar())
            {
                flag3 = flag1;
            } else
            {
                flag3 = true;
            }
            arraylist = addDisplayObjects(iterable, slobjectfilterinfo, immutablevector, false, multiplechatternameretriever, set, flag3);
        } else
        {
            arraylist = null;
        }
        iterable = slobjectinfo.getAbsolutePosition();
        f = immutablevector.distanceTo(((LLVector3) (iterable)).x, ((LLVector3) (iterable)).y, ((LLVector3) (iterable)).z);
        if (arraylist != null)
        {
            flag2 = arraylist.isEmpty() ^ true;
        } else
        {
            flag2 = false;
        }
        flag4 = slobjectfilterinfo.objectMatches(slobjectinfo, f, flag1);
        if (!flag2)
        {
            obj1 = obj;
            if (!flag4)
            {
                break MISSING_BLOCK_LABEL_428;
            }
        }
        s = getKnownName(slobjectinfo, multiplechatternameretriever, set);
        flag3 = slobjectfilterinfo.nameMatches(s);
        if (!flag2)
        {
            obj1 = obj;
            if (!flag3)
            {
                break MISSING_BLOCK_LABEL_428;
            }
        }
        if (flag2)
        {
            if (!flag4)
            {
                flag3 = false;
            }
            flag3 ^= true;
        } else
        {
            flag3 = false;
        }
        iterable = ((Iterable) (obj));
        if (obj == null)
        {
            iterable = new ArrayList();
        }
        if (flag)
        {
            if (slobjectinfo.isAvatar())
            {
                if (arraylist != null)
                {
                    obj = ImmutableList.copyOf(arraylist);
                } else
                {
                    obj = ImmutableList.of();
                }
                iterable.add(new SLAvatarObjectDisplayInfo(s, slobjectinfo, f, ((ImmutableList) (obj)), flag3));
            } else
            if (arraylist == null || arraylist.isEmpty())
            {
                iterable.add(new SLPrimObjectDisplayInfo(slobjectinfo, f));
            } else
            {
                iterable.add(new SLPrimObjectDisplayInfoWithChildren(slobjectinfo, f, ImmutableList.copyOf(arraylist), flag3));
            }
        } else
        {
            if (slobjectinfo.isAvatar())
            {
                obj = new SLAvatarObjectDisplayInfo(s, slobjectinfo, f, ImmutableList.of(), flag3);
            } else
            {
                obj = new SLPrimObjectDisplayInfo(slobjectinfo, f);
            }
            iterable.add(obj);
            obj1 = iterable;
            if (arraylist == null)
            {
                break MISSING_BLOCK_LABEL_428;
            }
            iterable.addAll(arraylist);
        }
_L3:
        obj = iterable;
        if (true) goto _L2; else goto _L1
_L1:
        return ((ArrayList) (obj));
        iterable = ((Iterable) (obj1));
          goto _L3
    }

    private String getKnownName(SLObjectInfo slobjectinfo, MultipleChatterNameRetriever multiplechatternameretriever, Set set)
    {
        Object obj = null;
        if (slobjectinfo.isAvatar())
        {
            slobjectinfo = slobjectinfo.getId();
            if (slobjectinfo != null)
            {
                set.add(slobjectinfo);
                return multiplechatternameretriever.addChatter(slobjectinfo);
            } else
            {
                return null;
            }
        }
        if (!slobjectinfo.nameKnown && objectNamesQueue.containsKey(slobjectinfo.getId()) ^ true)
        {
            objectNamesQueue.put(slobjectinfo.getId(), slobjectinfo);
        }
        multiplechatternameretriever = obj;
        if (slobjectinfo.nameKnown)
        {
            multiplechatternameretriever = Strings.nullToEmpty(slobjectinfo.name);
        }
        return multiplechatternameretriever;
    }

    static int lambda$_2D_com_lumiyaviewer_lumiya_slproto_SLParcelInfo_17694(SLObjectDisplayInfo slobjectdisplayinfo, SLObjectDisplayInfo slobjectdisplayinfo1)
    {
        return Float.compare(slobjectdisplayinfo.distance, slobjectdisplayinfo1.distance);
    }

    void ApplyAvatarAnimation(AvatarAnimation avataranimation, SLAvatarControl slavatarcontrol)
    {
        this;
        JVM INSTR monitorenter ;
        Object obj;
        obj = (SLObjectInfo)allObjectsNearby.get(avataranimation.Sender_Field.ID);
        if (!(obj instanceof SLObjectAvatarInfo))
        {
            break MISSING_BLOCK_LABEL_56;
        }
        obj = (SLObjectAvatarInfo)obj;
        ((SLObjectAvatarInfo) (obj)).ApplyAvatarAnimation(avataranimation);
        if (!((SLObjectAvatarInfo) (obj)).isMyAvatar() || slavatarcontrol == null)
        {
            break MISSING_BLOCK_LABEL_56;
        }
        slavatarcontrol.ApplyAvatarAnimation(((SLObjectAvatarInfo) (obj)), avataranimation);
        this;
        JVM INSTR monitorexit ;
        return;
        avataranimation;
        throw avataranimation;
    }

    void ApplyAvatarAppearance(AvatarAppearance avatarappearance)
    {
        this;
        JVM INSTR monitorenter ;
        SLObjectInfo slobjectinfo = (SLObjectInfo)allObjectsNearby.get(avatarappearance.Sender_Field.ID);
        if (slobjectinfo instanceof SLObjectAvatarInfo)
        {
            ((SLObjectAvatarInfo)slobjectinfo).ApplyAvatarAppearance(avatarappearance);
        }
        this;
        JVM INSTR monitorexit ;
        return;
        avatarappearance;
        throw avatarappearance;
    }

    boolean addObject(SLObjectInfo slobjectinfo)
    {
        Object obj = null;
        this;
        JVM INSTR monitorenter ;
        boolean flag;
        if (uuidsNearby.containsKey(Integer.valueOf(slobjectinfo.localID)))
        {
            break MISSING_BLOCK_LABEL_43;
        }
        flag = allObjectsNearby.containsKey(slobjectinfo.getId());
        if (!flag)
        {
            break MISSING_BLOCK_LABEL_47;
        }
        this;
        JVM INSTR monitorexit ;
        return false;
        uuidsNearby.put(Integer.valueOf(slobjectinfo.localID), slobjectinfo.getId());
        allObjectsNearby.put(slobjectinfo.getId(), slobjectinfo);
        if (slobjectinfo.parentID == 0) goto _L2; else goto _L1
_L1:
        Object obj1 = (UUID)uuidsNearby.get(Integer.valueOf(slobjectinfo.parentID));
        if (obj1 == null)
        {
            break MISSING_BLOCK_LABEL_128;
        }
        obj = (SLObjectInfo)allObjectsNearby.get(obj1);
        if (obj == null) goto _L4; else goto _L3
_L3:
        slobjectinfo.hierLevel = ((SLObjectInfo) (obj)).hierLevel + 1;
        if (((SLObjectInfo) (obj)).isAvatar()) goto _L6; else goto _L5
_L5:
        flag = ((SLObjectInfo) (obj)).isAttachment;
_L10:
        slobjectinfo.setIsAttachmentAll(flag);
        ((SLObjectInfo) (obj)).addChild(slobjectinfo);
_L11:
        obj = (LinkedList)orphanObjects.remove(Integer.valueOf(slobjectinfo.localID));
        if (obj == null) goto _L8; else goto _L7
_L7:
        obj = ((Iterable) (obj)).iterator();
_L9:
        if (!((Iterator) (obj)).hasNext())
        {
            break; /* Loop/switch isn't completed */
        }
        obj1 = (SLObjectInfo)((Iterator) (obj)).next();
        obj1.hierLevel = slobjectinfo.hierLevel + 1;
        if (slobjectinfo.isAttachment)
        {
            break MISSING_BLOCK_LABEL_355;
        }
        flag = slobjectinfo.isAttachment;
_L12:
        ((SLObjectInfo) (obj1)).setIsAttachmentAll(flag);
        slobjectinfo.addChild(((SLObjectInfo) (obj1)));
        if (true) goto _L9; else goto _L8
        slobjectinfo;
        throw slobjectinfo;
_L6:
        flag = true;
          goto _L10
_L4:
        obj1 = (LinkedList)orphanObjects.get(Integer.valueOf(slobjectinfo.parentID));
        obj = obj1;
        if (obj1 != null)
        {
            break MISSING_BLOCK_LABEL_316;
        }
        obj = new LinkedList();
        orphanObjects.put(Integer.valueOf(slobjectinfo.parentID), obj);
        ((LinkedList) (obj)).add(slobjectinfo);
          goto _L11
_L2:
        rootObjects.put(Integer.valueOf(slobjectinfo.localID), slobjectinfo);
          goto _L11
_L8:
        slobjectinfo.updateSpatialIndex(false);
        this;
        JVM INSTR monitorexit ;
        return true;
        flag = true;
          goto _L12
    }

    public SLObjectAvatarInfo getAgentAvatar()
    {
        Object obj = agentAvatarLock;
        obj;
        JVM INSTR monitorenter ;
        SLObjectAvatarInfo slobjectavatarinfo = agentAvatar;
        obj;
        JVM INSTR monitorexit ;
        return slobjectavatarinfo;
        Exception exception;
        exception;
        throw exception;
    }

    public SLObjectInfo getAvatarObject(UUID uuid)
    {
        this;
        JVM INSTR monitorenter ;
        uuid = (SLObjectInfo)allObjectsNearby.get(uuid);
        this;
        JVM INSTR monitorexit ;
        return uuid;
        uuid;
        throw uuid;
    }

    public com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager.ObjectDisplayList getDisplayObjects(ImmutableVector immutablevector, SLObjectFilterInfo slobjectfilterinfo, MultipleChatterNameRetriever multiplechatternameretriever)
    {
        boolean flag = true;
        HashSet hashset = new HashSet();
        this;
        JVM INSTR monitorenter ;
        int i;
        slobjectfilterinfo = addDisplayObjects(rootObjects.values(), slobjectfilterinfo, immutablevector, true, multiplechatternameretriever, hashset, false);
        i = objectNamesQueue.size();
        this;
        JVM INSTR monitorexit ;
        multiplechatternameretriever.retainChatters(hashset);
        if (slobjectfilterinfo != null)
        {
            immutablevector = Integer.toString(slobjectfilterinfo.size());
        } else
        {
            immutablevector = "null";
        }
        Debug.Printf("getDisplayObjects: objectList is %s, load queue %d", new Object[] {
            immutablevector, Integer.valueOf(i)
        });
        if (slobjectfilterinfo != null)
        {
            Collections.sort(slobjectfilterinfo, objectDisplayInfoComparator);
            immutablevector = ImmutableList.copyOf(slobjectfilterinfo);
            if (i != 0)
            {
                flag = true;
            } else
            {
                flag = false;
            }
            return new com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager.ObjectDisplayList(immutablevector, flag);
        }
        break MISSING_BLOCK_LABEL_142;
        immutablevector;
        throw immutablevector;
        immutablevector = ImmutableList.of();
        if (i == 0)
        {
            flag = false;
        }
        return new com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager.ObjectDisplayList(immutablevector, flag);
    }

    public SLObjectInfo getObjectInfo(int i)
    {
        this;
        JVM INSTR monitorenter ;
        Object obj = (UUID)uuidsNearby.get(Integer.valueOf(i));
        if (obj != null)
        {
            break MISSING_BLOCK_LABEL_27;
        }
        this;
        JVM INSTR monitorexit ;
        return null;
        obj = (SLObjectInfo)allObjectsNearby.get(obj);
        this;
        JVM INSTR monitorexit ;
        return ((SLObjectInfo) (obj));
        Exception exception;
        exception;
        throw exception;
    }

    public int getObjectLocalID(UUID uuid)
    {
        this;
        JVM INSTR monitorenter ;
        if (uuid == null) goto _L2; else goto _L1
_L1:
        uuid = (SLObjectInfo)allObjectsNearby.get(uuid);
        if (uuid == null) goto _L2; else goto _L3
_L3:
        int i = ((SLObjectInfo) (uuid)).localID;
_L5:
        this;
        JVM INSTR monitorexit ;
        return i;
        uuid;
        throw uuid;
_L2:
        i = -1;
        if (true) goto _L5; else goto _L4
_L4:
    }

    public UUID getObjectUUID(int i)
    {
        this;
        JVM INSTR monitorenter ;
        UUID uuid = (UUID)uuidsNearby.get(Integer.valueOf(i));
        this;
        JVM INSTR monitorexit ;
        return uuid;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean getSunHour(float af[], boolean flag)
    {
        Object obj = simSunHourLock;
        obj;
        JVM INSTR monitorenter ;
        if (!simSunHourDirty && !flag)
        {
            break MISSING_BLOCK_LABEL_34;
        }
        af[0] = simSunHour;
        simSunHourDirty = false;
        obj;
        JVM INSTR monitorexit ;
        return true;
        obj;
        JVM INSTR monitorexit ;
        return false;
        af;
        throw af;
    }

    public ImmutableList getUserTouchableObjects(SLAgentCircuit slagentcircuit, UUID uuid)
    {
        com.google.common.collect.ImmutableList.Builder builder = ImmutableList.builder();
        this;
        JVM INSTR monitorenter ;
        uuid = (SLObjectInfo)allObjectsNearby.get(uuid);
        if (uuid == null)
        {
            break MISSING_BLOCK_LABEL_91;
        }
        try
        {
            uuid = ((SLObjectInfo) (uuid)).treeNode.iterator();
            do
            {
                if (!uuid.hasNext())
                {
                    break;
                }
                SLObjectInfo slobjectinfo = (SLObjectInfo)uuid.next();
                if (slobjectinfo.isTouchable())
                {
                    if (!slobjectinfo.nameKnown)
                    {
                        slagentcircuit.RequestObjectName(slobjectinfo);
                    }
                    builder.add(slobjectinfo);
                }
            } while (true);
            break MISSING_BLOCK_LABEL_91;
        }
        // Misplaced declaration of an exception variable
        catch (SLAgentCircuit slagentcircuit) { }
        finally
        {
            throw slagentcircuit;
        }
        Debug.Warning(slagentcircuit);
        this;
        JVM INSTR monitorexit ;
        return builder.build();
    }

    public void initSpatialIndex()
    {
        this;
        JVM INSTR monitorenter ;
        for (Iterator iterator = rootObjects.values().iterator(); iterator.hasNext(); ((SLObjectInfo)iterator.next()).updateSpatialIndex(true)) { }
        break MISSING_BLOCK_LABEL_47;
        Object obj;
        obj;
        Debug.Warning(((Throwable) (obj)));
        this;
        JVM INSTR monitorexit ;
        return;
        obj;
        throw obj;
    }

    public boolean isParentOrSame(UUID uuid, UUID uuid1)
    {
        this;
        JVM INSTR monitorenter ;
        boolean flag = uuid1.equals(uuid);
        if (!flag)
        {
            break MISSING_BLOCK_LABEL_16;
        }
        this;
        JVM INSTR monitorexit ;
        return true;
        uuid1 = (SLObjectInfo)allObjectsNearby.get(uuid1);
        if (uuid1 == null) goto _L2; else goto _L1
_L1:
        uuid1 = uuid1.getParentObject();
_L3:
        if (uuid1 == null)
        {
            break; /* Loop/switch isn't completed */
        }
        flag = uuid1.getId().equals(uuid);
        if (!flag)
        {
            break MISSING_BLOCK_LABEL_60;
        }
        this;
        JVM INSTR monitorexit ;
        return true;
        uuid1 = uuid1.getParentObject();
        if (true) goto _L3; else goto _L2
_L2:
        this;
        JVM INSTR monitorexit ;
        return false;
        uuid;
        throw uuid;
    }

    boolean killObject(SLAgentCircuit slagentcircuit, int i)
    {
        boolean flag3 = false;
        Object obj1 = null;
        this;
        JVM INSTR monitorenter ;
        UUID uuid = (UUID)uuidsNearby.remove(Integer.valueOf(i));
        if (uuid == null)
        {
            break MISSING_BLOCK_LABEL_536;
        }
        SLObjectInfo slobjectinfo;
        objectNamesQueue.remove(uuid);
        slobjectinfo = (SLObjectInfo)allObjectsNearby.remove(uuid);
        if (slobjectinfo == null)
        {
            break MISSING_BLOCK_LABEL_536;
        }
        slobjectinfo.isDead = true;
        if (slobjectinfo.parentID != 0) goto _L2; else goto _L1
_L1:
        rootObjects.remove(Integer.valueOf(i));
_L10:
        Iterator iterator = slobjectinfo.treeNode.iterator();
        Object obj = obj1;
_L7:
        if (!iterator.hasNext()) goto _L4; else goto _L3
_L3:
        SLObjectInfo slobjectinfo1 = (SLObjectInfo)iterator.next();
        if (!slobjectinfo1.isAvatar()) goto _L6; else goto _L5
_L5:
        obj1 = obj;
        if (obj != null)
        {
            break MISSING_BLOCK_LABEL_153;
        }
        obj1 = new LinkedList();
        ((List) (obj1)).add(slobjectinfo1);
        obj = obj1;
          goto _L7
_L2:
        obj = (UUID)uuidsNearby.get(Integer.valueOf(slobjectinfo.parentID));
        if (obj == null) goto _L9; else goto _L8
_L8:
        obj = (SLObjectInfo)allObjectsNearby.get(obj);
_L17:
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_250;
        }
        ((SLObjectInfo) (obj)).removeChild(slobjectinfo);
        if (obj instanceof SLObjectAvatarInfo)
        {
            obj = (SLObjectAvatarInfo)obj;
            if (((SLObjectAvatarInfo) (obj)).isMyAvatar())
            {
                slagentcircuit.processMyAttachmentUpdate(((SLObjectInfo) (obj)));
            }
        }
          goto _L10
        slagentcircuit;
        throw slagentcircuit;
        obj = (LinkedList)orphanObjects.get(Integer.valueOf(slobjectinfo.parentID));
        if (obj == null) goto _L10; else goto _L11
_L11:
        ((LinkedList) (obj)).remove(slobjectinfo);
        if (((LinkedList) (obj)).isEmpty())
        {
            orphanObjects.remove(Integer.valueOf(slobjectinfo.parentID));
        }
          goto _L10
_L6:
        killObject(slagentcircuit, slobjectinfo1.localID);
          goto _L7
_L4:
        if (obj == null) goto _L13; else goto _L12
_L12:
        slagentcircuit = ((Iterable) (obj)).iterator();
        boolean flag = false;
_L15:
        boolean flag2 = flag;
        if (!slagentcircuit.hasNext())
        {
            break; /* Loop/switch isn't completed */
        }
        flag2 = flag;
        obj = (SLObjectInfo)slagentcircuit.next();
        flag2 = flag;
        slobjectinfo.removeChild(((SLObjectInfo) (obj)));
        flag2 = flag;
        obj.parentID = 0;
        boolean flag1;
        flag1 = flag;
        flag2 = flag;
        if (!(obj instanceof SLObjectAvatarInfo))
        {
            break MISSING_BLOCK_LABEL_420;
        }
        flag1 = flag;
        flag2 = flag;
        if (((SLObjectAvatarInfo)obj).isMyAvatar())
        {
            flag1 = true;
        }
        flag2 = flag1;
        rootObjects.put(Integer.valueOf(((SLObjectInfo) (obj)).localID), obj);
        flag = flag1;
        if (true) goto _L15; else goto _L14
        slagentcircuit;
_L16:
        Debug.Warning(slagentcircuit);
        flag = flag2;
_L14:
        slobjectinfo.removeFromSpatialIndex();
_L18:
        if (uuid != null)
        {
            flag3 = true;
        }
        this;
        JVM INSTR monitorexit ;
        if (userManager != null)
        {
            userManager.getObjectsManager().requestObjectProfileUpdate(i);
            if (flag)
            {
                userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value);
            }
        }
        return flag3;
        slagentcircuit;
        flag2 = false;
          goto _L16
_L13:
        flag = false;
          goto _L14
_L9:
        obj = null;
          goto _L17
        flag = false;
          goto _L18
    }

    public void reset(UserManager usermanager)
    {
        this;
        JVM INSTR monitorenter ;
        if (usermanager != userManager)
        {
            if (userManager != null)
            {
                userManager.getObjectsManager().clearParcelInfo(this);
            }
            userManager = usermanager;
            if (userManager != null)
            {
                userManager.getObjectsManager().setParcelInfo(this);
            }
        }
        uuidsNearby.clear();
        usermanager = allObjectsNearby.values().iterator();
_L1:
        SLObjectInfo slobjectinfo;
        DrawListObjectEntry drawlistobjectentry;
        if (!usermanager.hasNext())
        {
            break MISSING_BLOCK_LABEL_119;
        }
        slobjectinfo = (SLObjectInfo)usermanager.next();
        drawlistobjectentry = slobjectinfo.getExistingDrawListEntry();
        if (drawlistobjectentry == null)
        {
            break MISSING_BLOCK_LABEL_107;
        }
        drawlistobjectentry.requestEntryRemoval();
        slobjectinfo.clearDrawListEntry();
          goto _L1
        usermanager;
        throw usermanager;
        allObjectsNearby.clear();
        rootObjects.clear();
        orphanObjects.clear();
        objectNamesQueue.clear();
        terrainData.reset();
        simSunHour = 0.5F;
        simSunHourDirty = false;
        this;
        JVM INSTR monitorexit ;
    }

    public void setAgentAvatar(SLObjectAvatarInfo slobjectavatarinfo)
    {
        Object obj = agentAvatarLock;
        obj;
        JVM INSTR monitorenter ;
        agentAvatar = slobjectavatarinfo;
        obj;
        JVM INSTR monitorexit ;
        return;
        slobjectavatarinfo;
        throw slobjectavatarinfo;
    }

    public void setDrawDistance(float f)
    {
        this;
        JVM INSTR monitorenter ;
        if (drawDistance != f)
        {
            drawDistance = f;
        }
        this;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    void setSunHour(float f)
    {
        Debug.Printf("Windlight: Simulator sun hour set to %f", new Object[] {
            Float.valueOf(f)
        });
        Object obj = simSunHourLock;
        obj;
        JVM INSTR monitorenter ;
        simSunHour = f;
        simSunHourDirty = true;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    boolean updateObjectParent(int i, SLObjectInfo slobjectinfo)
    {
        LinkedList linkedlist = null;
        this;
        JVM INSTR monitorenter ;
        int j = slobjectinfo.parentID;
        if (i != j)
        {
            break MISSING_BLOCK_LABEL_21;
        }
        this;
        JVM INSTR monitorexit ;
        return false;
        if (i == 0) goto _L2; else goto _L1
_L1:
        Object obj = (UUID)uuidsNearby.get(Integer.valueOf(i));
        if (obj == null) goto _L4; else goto _L3
_L3:
        obj = (SLObjectInfo)allObjectsNearby.get(obj);
_L15:
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_74;
        }
        ((SLObjectInfo) (obj)).removeChild(slobjectinfo);
        ((SLObjectInfo) (obj)).updateSpatialIndex(false);
        obj = (LinkedList)orphanObjects.get(Integer.valueOf(i));
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_101;
        }
        ((LinkedList) (obj)).remove(slobjectinfo);
_L11:
        if (slobjectinfo.parentID == 0) goto _L6; else goto _L5
_L5:
        UUID uuid = (UUID)uuidsNearby.get(Integer.valueOf(slobjectinfo.parentID));
        obj = linkedlist;
        if (uuid == null)
        {
            break MISSING_BLOCK_LABEL_152;
        }
        obj = (SLObjectInfo)allObjectsNearby.get(uuid);
        if (obj == null) goto _L8; else goto _L7
_L7:
        slobjectinfo.hierLevel = ((SLObjectInfo) (obj)).hierLevel + 1;
        if (((SLObjectInfo) (obj)).isAvatar()) goto _L10; else goto _L9
_L9:
        boolean flag = ((SLObjectInfo) (obj)).isAttachment;
_L12:
        slobjectinfo.setIsAttachmentAll(flag);
        ((SLObjectInfo) (obj)).addChild(slobjectinfo);
_L13:
        slobjectinfo.updateSpatialIndex(false);
        this;
        JVM INSTR monitorexit ;
        return true;
_L2:
        rootObjects.remove(Integer.valueOf(slobjectinfo.localID));
          goto _L11
        slobjectinfo;
        throw slobjectinfo;
_L10:
        flag = true;
          goto _L12
_L8:
        linkedlist = (LinkedList)orphanObjects.get(Integer.valueOf(slobjectinfo.parentID));
        obj = linkedlist;
        if (linkedlist != null)
        {
            break MISSING_BLOCK_LABEL_285;
        }
        obj = new LinkedList();
        orphanObjects.put(Integer.valueOf(slobjectinfo.parentID), obj);
        ((LinkedList) (obj)).add(slobjectinfo);
          goto _L13
_L6:
        slobjectinfo.hierLevel = 0;
        slobjectinfo.setIsAttachmentAll(false);
        rootObjects.put(Integer.valueOf(slobjectinfo.localID), slobjectinfo);
          goto _L13
_L4:
        obj = null;
        if (true) goto _L15; else goto _L14
_L14:
    }
}
