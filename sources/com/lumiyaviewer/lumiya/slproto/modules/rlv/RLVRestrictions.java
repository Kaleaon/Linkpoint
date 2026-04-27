// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.rlv;

import com.lumiyaviewer.lumiya.Debug;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.rlv:
//            RLVRestrictionType

public class RLVRestrictions
{
    private static class RLVRestrictionList
    {

        private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_rlv_2D_RLVRestrictionType$RLVRuleMatchTypeSwitchesValues[];
        private Map restMap;

        private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_rlv_2D_RLVRestrictionType$RLVRuleMatchTypeSwitchesValues()
        {
            if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_rlv_2D_RLVRestrictionType$RLVRuleMatchTypeSwitchesValues != null)
            {
                return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_rlv_2D_RLVRestrictionType$RLVRuleMatchTypeSwitchesValues;
            }
            int ai[] = new int[RLVRestrictionType.RLVRuleMatchType.values().length];
            try
            {
                ai[RLVRestrictionType.RLVRuleMatchType.TargetNoExceptions.ordinal()] = 1;
            }
            catch (NoSuchFieldError nosuchfielderror3) { }
            try
            {
                ai[RLVRestrictionType.RLVRuleMatchType.TargetSpecifiesAllowance.ordinal()] = 4;
            }
            catch (NoSuchFieldError nosuchfielderror2) { }
            try
            {
                ai[RLVRestrictionType.RLVRuleMatchType.TargetSpecifiesException.ordinal()] = 2;
            }
            catch (NoSuchFieldError nosuchfielderror1) { }
            try
            {
                ai[RLVRestrictionType.RLVRuleMatchType.TargetSpecifiesRestriction.ordinal()] = 3;
            }
            catch (NoSuchFieldError nosuchfielderror) { }
            _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_rlv_2D_RLVRestrictionType$RLVRuleMatchTypeSwitchesValues = ai;
            return ai;
        }

        public void addRestriction(UUID uuid, String s)
        {
            HashSet hashset1 = (HashSet)restMap.get(s);
            HashSet hashset = hashset1;
            if (hashset1 == null)
            {
                hashset = new HashSet();
                restMap.put(s, hashset);
            }
            hashset.add(uuid);
        }

        public Set getTargets()
        {
            return restMap.keySet();
        }

        public boolean hasRestrictionsByObject(UUID uuid)
        {
            if (uuid == null)
            {
                return restMap.isEmpty() ^ true;
            }
            for (Iterator iterator = restMap.entrySet().iterator(); iterator.hasNext();)
            {
                if (((HashSet)((java.util.Map.Entry)iterator.next()).getValue()).contains(uuid))
                {
                    return true;
                }
            }

            return false;
        }

        public boolean isAllowed(RLVRestrictionType.RLVRuleMatchType rlvrulematchtype, String s, UUID uuid, UUID uuid1)
        {
            if (rlvrulematchtype == RLVRestrictionType.RLVRuleMatchType.TargetSpecifiesAllowance)
            {
                if (restMap.containsKey(""))
                {
                    return true;
                }
                return !s.equals("") && restMap.containsKey(s);
            }
            if (restMap.isEmpty())
            {
                return true;
            }
            _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_modules_2D_rlv_2D_RLVRestrictionType$RLVRuleMatchTypeSwitchesValues()[rlvrulematchtype.ordinal()];
            JVM INSTR tableswitch 1 3: default 96
        //                       1 196
        //                       2 98
        //                       3 138;
               goto _L1 _L2 _L3 _L4
_L1:
            return true;
_L3:
            if (restMap.containsKey(""))
            {
                return !s.equals("") && restMap.containsKey(s);
            }
            continue; /* Loop/switch isn't completed */
_L4:
            if (restMap.containsKey(s))
            {
                return false;
            }
            if (!restMap.containsKey(""))
            {
                continue; /* Loop/switch isn't completed */
            }
            if (uuid == null)
            {
                return false;
            }
            if (((HashSet)restMap.get("")).contains(uuid))
            {
                return false;
            }
            continue; /* Loop/switch isn't completed */
_L2:
            if (restMap.isEmpty())
            {
                continue; /* Loop/switch isn't completed */
            }
            if (uuid1 == null)
            {
                return false;
            }
            rlvrulematchtype = restMap.values().iterator();
            do
            {
                if (!rlvrulematchtype.hasNext())
                {
                    continue; /* Loop/switch isn't completed */
                }
                s = (HashSet)rlvrulematchtype.next();
            } while (s.size() == 1 && s.contains(uuid1));
            break; /* Loop/switch isn't completed */
            if (true) goto _L1; else goto _L5
_L5:
            return false;
        }

        public boolean isEmpty()
        {
            return restMap.isEmpty();
        }

        public void removeAllForObject(UUID uuid)
        {
            Object obj = new HashSet();
            Iterator iterator = restMap.entrySet().iterator();
            do
            {
                if (!iterator.hasNext())
                {
                    break;
                }
                java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
                ((HashSet)entry.getValue()).remove(uuid);
                if (((HashSet)entry.getValue()).isEmpty())
                {
                    ((Set) (obj)).add((String)entry.getKey());
                }
            } while (true);
            for (uuid = ((Iterable) (obj)).iterator(); uuid.hasNext(); restMap.remove(obj))
            {
                obj = (String)uuid.next();
            }

        }

        public void removeRestriction(UUID uuid, String s)
        {
            HashSet hashset = (HashSet)restMap.get(s);
            if (hashset != null)
            {
                hashset.remove(uuid);
                if (hashset.isEmpty())
                {
                    restMap.remove(s);
                }
            }
        }

        private RLVRestrictionList()
        {
            restMap = new HashMap();
        }

        RLVRestrictionList(RLVRestrictionList rlvrestrictionlist)
        {
            this();
        }
    }


    private Map restrictions;

    public RLVRestrictions()
    {
        restrictions = new EnumMap(com/lumiyaviewer/lumiya/slproto/modules/rlv/RLVRestrictionType);
    }

    public void addRestriction(RLVRestrictionType rlvrestrictiontype, UUID uuid, String s)
    {
        this;
        JVM INSTR monitorenter ;
        String s1;
        s1 = s;
        if (s == null)
        {
            s1 = "";
        }
        RLVRestrictionList rlvrestrictionlist;
        Debug.Printf("RLV: adding restriction '%s' for object %s, target '%s'", new Object[] {
            rlvrestrictiontype.toString(), uuid, s1
        });
        rlvrestrictionlist = (RLVRestrictionList)restrictions.get(rlvrestrictiontype);
        s = rlvrestrictionlist;
        if (rlvrestrictionlist != null)
        {
            break MISSING_BLOCK_LABEL_82;
        }
        s = new RLVRestrictionList(null);
        restrictions.put(rlvrestrictiontype, s);
        s.addRestriction(uuid, s1.toLowerCase());
        this;
        JVM INSTR monitorexit ;
        return;
        rlvrestrictiontype;
        throw rlvrestrictiontype;
    }

    public List getRestrictionsByObject(UUID uuid)
    {
        this;
        JVM INSTR monitorenter ;
        LinkedList linkedlist;
        linkedlist = new LinkedList();
        Iterator iterator = restrictions.entrySet().iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            if (((RLVRestrictionList)entry.getValue()).hasRestrictionsByObject(uuid))
            {
                linkedlist.add((RLVRestrictionType)entry.getKey());
            }
        } while (true);
        break MISSING_BLOCK_LABEL_87;
        uuid;
        throw uuid;
        this;
        JVM INSTR monitorexit ;
        return linkedlist;
    }

    public Set getTargetsForRestriction(RLVRestrictionType rlvrestrictiontype)
    {
        this;
        JVM INSTR monitorenter ;
        rlvrestrictiontype = (RLVRestrictionList)restrictions.get(rlvrestrictiontype);
        if (rlvrestrictiontype == null)
        {
            break MISSING_BLOCK_LABEL_29;
        }
        rlvrestrictiontype = rlvrestrictiontype.getTargets();
        this;
        JVM INSTR monitorexit ;
        return rlvrestrictiontype;
        this;
        JVM INSTR monitorexit ;
        return null;
        rlvrestrictiontype;
        throw rlvrestrictiontype;
    }

    public boolean isAllowed(RLVRestrictionType rlvrestrictiontype, String s, UUID uuid)
    {
        this;
        JVM INSTR monitorenter ;
        boolean flag = isAllowed(rlvrestrictiontype, s, uuid, null);
        this;
        JVM INSTR monitorexit ;
        return flag;
        rlvrestrictiontype;
        throw rlvrestrictiontype;
    }

    public boolean isAllowed(RLVRestrictionType rlvrestrictiontype, String s, UUID uuid, UUID uuid1)
    {
        this;
        JVM INSTR monitorenter ;
        String s1;
        s1 = s;
        if (s == null)
        {
            s1 = "";
        }
        s = (RLVRestrictionList)restrictions.get(rlvrestrictiontype);
        if (s == null)
        {
            break MISSING_BLOCK_LABEL_54;
        }
        boolean flag = s.isAllowed(rlvrestrictiontype.getRuleMatchType(), s1.toLowerCase(), uuid, uuid1);
        this;
        JVM INSTR monitorexit ;
        return flag;
        rlvrestrictiontype = rlvrestrictiontype.getRuleMatchType();
        s = RLVRestrictionType.RLVRuleMatchType.TargetSpecifiesAllowance;
        if (rlvrestrictiontype == s)
        {
            return false;
        }
        this;
        JVM INSTR monitorexit ;
        return true;
        rlvrestrictiontype;
        throw rlvrestrictiontype;
    }

    public void removeRestriction(RLVRestrictionType rlvrestrictiontype, UUID uuid, String s)
    {
        this;
        JVM INSTR monitorenter ;
        String s1;
        s1 = s;
        if (s == null)
        {
            s1 = "";
        }
        Debug.Printf("RLV: removing restriction '%s' for object %s, target '%s'", new Object[] {
            rlvrestrictiontype.toString(), uuid, s1
        });
        s = (RLVRestrictionList)restrictions.get(rlvrestrictiontype);
        if (s == null)
        {
            break MISSING_BLOCK_LABEL_84;
        }
        s.removeRestriction(uuid, s1.toLowerCase());
        if (s.isEmpty())
        {
            restrictions.remove(rlvrestrictiontype);
        }
        this;
        JVM INSTR monitorexit ;
        return;
        rlvrestrictiontype;
        throw rlvrestrictiontype;
    }

    public void removeRestrictions(UUID uuid, Set set)
    {
        this;
        JVM INSTR monitorenter ;
        Debug.Printf("RLV: removing %d restrictions for object %s", new Object[] {
            Integer.valueOf(set.size()), uuid
        });
        set = set.iterator();
_L2:
        RLVRestrictionType rlvrestrictiontype;
        RLVRestrictionList rlvrestrictionlist;
        do
        {
            if (!set.hasNext())
            {
                break MISSING_BLOCK_LABEL_106;
            }
            rlvrestrictiontype = (RLVRestrictionType)set.next();
            rlvrestrictionlist = (RLVRestrictionList)restrictions.get(rlvrestrictiontype);
        } while (rlvrestrictionlist == null);
        rlvrestrictionlist.removeAllForObject(uuid);
        if (rlvrestrictionlist.isEmpty())
        {
            restrictions.remove(rlvrestrictiontype);
        }
        if (true) goto _L2; else goto _L1
_L1:
        uuid;
        throw uuid;
        this;
        JVM INSTR monitorexit ;
    }
}
