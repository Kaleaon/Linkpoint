// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.rlv;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import com.lumiyaviewer.lumiya.Debug;
import java.util.UUID;
import java.util.EnumMap;
import java.util.Map;

public class RLVRestrictions
{
    private Map<RLVRestrictionType, RLVRestrictionList> restrictions;
    
    public RLVRestrictions() {
        this.restrictions = new EnumMap<RLVRestrictionType, RLVRestrictionList>(RLVRestrictionType.class);
    }
    
    public void addRestriction(final RLVRestrictionType rlvRestrictionType, final UUID uuid, final String s) {
        monitorenter(this);
        String s2 = s;
        if (s == null) {
            s2 = "";
        }
        try {
            Debug.Printf("RLV: adding restriction '%s' for object %s, target '%s'", rlvRestrictionType.toString(), uuid, s2);
            RLVRestrictionList list;
            if ((list = this.restrictions.get(rlvRestrictionType)) == null) {
                list = new RLVRestrictionList(null);
                this.restrictions.put(rlvRestrictionType, list);
            }
            list.addRestriction(uuid, s2.toLowerCase());
        }
        finally {
            monitorexit(this);
        }
    }
    
    public List<RLVRestrictionType> getRestrictionsByObject(final UUID uuid) {
        final LinkedList list;
        synchronized (this) {
            list = new LinkedList();
            for (final Map.Entry<K, RLVRestrictionList> entry : this.restrictions.entrySet()) {
                if (entry.getValue().hasRestrictionsByObject(uuid)) {
                    list.add(entry.getKey());
                }
            }
        }
        monitorexit(this);
        return list;
    }
    
    public Set<String> getTargetsForRestriction(final RLVRestrictionType rlvRestrictionType) {
        synchronized (this) {
            final RLVRestrictionList list = this.restrictions.get(rlvRestrictionType);
            if (list != null) {
                return list.getTargets();
            }
            return null;
        }
    }
    
    public boolean isAllowed(final RLVRestrictionType rlvRestrictionType, final String s, final UUID uuid) {
        synchronized (this) {
            return this.isAllowed(rlvRestrictionType, s, uuid, null);
        }
    }
    
    public boolean isAllowed(final RLVRestrictionType rlvRestrictionType, final String s, final UUID uuid, final UUID uuid2) {
        monitorenter(this);
        String s2 = s;
        if (s == null) {
            s2 = "";
        }
        try {
            final RLVRestrictionList list = this.restrictions.get(rlvRestrictionType);
            if (list != null) {
                return list.isAllowed(rlvRestrictionType.getRuleMatchType(), s2.toLowerCase(), uuid, uuid2);
            }
            return rlvRestrictionType.getRuleMatchType() != RLVRestrictionType.RLVRuleMatchType.TargetSpecifiesAllowance;
        }
        finally {
            monitorexit(this);
        }
    }
    
    public void removeRestriction(final RLVRestrictionType rlvRestrictionType, final UUID uuid, final String s) {
        monitorenter(this);
        String s2 = s;
        if (s == null) {
            s2 = "";
        }
        try {
            Debug.Printf("RLV: removing restriction '%s' for object %s, target '%s'", rlvRestrictionType.toString(), uuid, s2);
            final RLVRestrictionList list = this.restrictions.get(rlvRestrictionType);
            if (list != null) {
                list.removeRestriction(uuid, s2.toLowerCase());
                if (list.isEmpty()) {
                    this.restrictions.remove(rlvRestrictionType);
                }
            }
        }
        finally {
            monitorexit(this);
        }
    }
    
    public void removeRestrictions(final UUID uuid, final Set<RLVRestrictionType> set) {
        synchronized (this) {
            Debug.Printf("RLV: removing %d restrictions for object %s", set.size(), uuid);
            for (final RLVRestrictionType rlvRestrictionType : set) {
                final RLVRestrictionList list = this.restrictions.get(rlvRestrictionType);
                if (list != null) {
                    list.removeAllForObject(uuid);
                    if (!list.isEmpty()) {
                        continue;
                    }
                    this.restrictions.remove(rlvRestrictionType);
                }
            }
        }
        monitorexit(this);
    }
    
    private static class RLVRestrictionList
    {
        private Map<String, HashSet<UUID>> restMap;
        
        private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-modules-rlv-RLVRestrictionType$RLVRuleMatchTypeSwitchesValues() {
            if (RLVRestrictionList.-com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVRestrictionType$RLVRuleMatchTypeSwitchesValues != null) {
                return RLVRestrictionList.-com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVRestrictionType$RLVRuleMatchTypeSwitchesValues;
            }
            int[] -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVRestrictionType$RLVRuleMatchTypeSwitchesValues = new int[RLVRestrictionType.RLVRuleMatchType.values().length];
            while (true) {
                try {
                    -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVRestrictionType$RLVRuleMatchTypeSwitchesValues[RLVRestrictionType.RLVRuleMatchType.TargetNoExceptions.ordinal()] = 1;
                    try {
                        -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVRestrictionType$RLVRuleMatchTypeSwitchesValues[RLVRestrictionType.RLVRuleMatchType.TargetSpecifiesAllowance.ordinal()] = 4;
                        try {
                            -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVRestrictionType$RLVRuleMatchTypeSwitchesValues[RLVRestrictionType.RLVRuleMatchType.TargetSpecifiesException.ordinal()] = 2;
                            try {
                                -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVRestrictionType$RLVRuleMatchTypeSwitchesValues[RLVRestrictionType.RLVRuleMatchType.TargetSpecifiesRestriction.ordinal()] = 3;
                                return -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVRestrictionType$RLVRuleMatchTypeSwitchesValues = -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVRestrictionType$RLVRuleMatchTypeSwitchesValues;
                            }
                            catch (final NoSuchFieldError noSuchFieldError) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError2) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError3) {}
                }
                catch (final NoSuchFieldError noSuchFieldError4) {
                    continue;
                }
                break;
            }
        }
        
        private RLVRestrictionList() {
            this.restMap = new HashMap<String, HashSet<UUID>>();
        }
        
        public void addRestriction(final UUID e, final String s) {
            HashSet set;
            if ((set = this.restMap.get(s)) == null) {
                set = new HashSet();
                this.restMap.put(s, set);
            }
            set.add(e);
        }
        
        public Set<String> getTargets() {
            return this.restMap.keySet();
        }
        
        public boolean hasRestrictionsByObject(final UUID o) {
            if (o == null) {
                return this.restMap.isEmpty() ^ true;
            }
            final Iterator<Object> iterator = this.restMap.entrySet().iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getValue().contains(o)) {
                    return true;
                }
            }
            return false;
        }
        
        public boolean isAllowed(final RLVRestrictionType.RLVRuleMatchType rlvRuleMatchType, final String s, final UUID o, final UUID o2) {
            if (rlvRuleMatchType == RLVRestrictionType.RLVRuleMatchType.TargetSpecifiesAllowance) {
                return this.restMap.containsKey("") || (!s.equals("") && this.restMap.containsKey(s));
            }
            if (this.restMap.isEmpty()) {
                return true;
            }
            switch (-getcom-lumiyaviewer-lumiya-slproto-modules-rlv-RLVRestrictionType$RLVRuleMatchTypeSwitchesValues()[rlvRuleMatchType.ordinal()]) {
                case 2: {
                    if (this.restMap.containsKey("")) {
                        return !s.equals("") && this.restMap.containsKey(s);
                    }
                    break;
                }
                case 3: {
                    if (this.restMap.containsKey(s)) {
                        return false;
                    }
                    if (!this.restMap.containsKey("")) {
                        break;
                    }
                    if (o == null) {
                        return false;
                    }
                    if (this.restMap.get("").contains(o)) {
                        return false;
                    }
                    break;
                }
                case 1: {
                    if (this.restMap.isEmpty()) {
                        break;
                    }
                    if (o2 == null) {
                        return false;
                    }
                    for (final HashSet set : this.restMap.values()) {
                        if (set.size() != 1 || !set.contains(o2)) {
                            return false;
                        }
                    }
                    break;
                }
            }
            return true;
        }
        
        public boolean isEmpty() {
            return this.restMap.isEmpty();
        }
        
        public void removeAllForObject(final UUID o) {
            final HashSet set = new HashSet();
            for (final Map.Entry<K, HashSet> entry : this.restMap.entrySet()) {
                entry.getValue().remove(o);
                if (entry.getValue().isEmpty()) {
                    set.add(entry.getKey());
                }
            }
            final Iterator iterator2 = set.iterator();
            while (iterator2.hasNext()) {
                this.restMap.remove(iterator2.next());
            }
        }
        
        public void removeRestriction(final UUID o, final String s) {
            final HashSet set = this.restMap.get(s);
            if (set != null) {
                set.remove(o);
                if (set.isEmpty()) {
                    this.restMap.remove(s);
                }
            }
        }
    }
}
