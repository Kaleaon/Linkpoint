package com.lumiyaviewer.lumiya.slproto.modules.rlv;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RLVRestrictions {
    private Map<RLVRestrictionType, RLVRestrictionList> restrictions = new EnumMap(RLVRestrictionType.class);

    private static class RLVRestrictionList {

        /* renamed from: -com-lumiyaviewer-lumiya-slproto-modules-rlv-RLVRestrictionType$RLVRuleMatchTypeSwitchesValues  reason: not valid java name */
        private static final /* synthetic */ int[] f127comlumiyaviewerlumiyaslprotomodulesrlvRLVRestrictionType$RLVRuleMatchTypeSwitchesValues = null;
        private Map<String, HashSet<UUID>> restMap;

        /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-modules-rlv-RLVRestrictionType$RLVRuleMatchTypeSwitchesValues  reason: not valid java name */
        private static /* synthetic */ int[] m231getcomlumiyaviewerlumiyaslprotomodulesrlvRLVRestrictionType$RLVRuleMatchTypeSwitchesValues() {
            if (f127comlumiyaviewerlumiyaslprotomodulesrlvRLVRestrictionType$RLVRuleMatchTypeSwitchesValues != null) {
                return f127comlumiyaviewerlumiyaslprotomodulesrlvRLVRestrictionType$RLVRuleMatchTypeSwitchesValues;
            }
            int[] iArr = new int[RLVRestrictionType.RLVRuleMatchType.values().length];
            try {
                iArr[RLVRestrictionType.RLVRuleMatchType.TargetNoExceptions.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[RLVRestrictionType.RLVRuleMatchType.TargetSpecifiesAllowance.ordinal()] = 4;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[RLVRestrictionType.RLVRuleMatchType.TargetSpecifiesException.ordinal()] = 2;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[RLVRestrictionType.RLVRuleMatchType.TargetSpecifiesRestriction.ordinal()] = 3;
            } catch (NoSuchFieldError e4) {
            }
            f127comlumiyaviewerlumiyaslprotomodulesrlvRLVRestrictionType$RLVRuleMatchTypeSwitchesValues = iArr;
            return iArr;
        }

        private RLVRestrictionList() {
            this.restMap = new HashMap();
        }

        /* synthetic */ RLVRestrictionList(RLVRestrictionList rLVRestrictionList) {
            this();
        }

        public void addRestriction(UUID uuid, String str) {
            HashSet hashSet = this.restMap.get(str);
            if (hashSet == null) {
                hashSet = new HashSet();
                this.restMap.put(str, hashSet);
            }
            hashSet.add(uuid);
        }

        public Set<String> getTargets() {
            return this.restMap.keySet();
        }

        public boolean hasRestrictionsByObject(UUID uuid) {
            if (uuid == null) {
                return !this.restMap.isEmpty();
            }
            for (Map.Entry value : this.restMap.entrySet()) {
                if (((HashSet) value.getValue()).contains(uuid)) {
                    return true;
                }
            }
            return false;
        }

        /* JADX WARNING: Removed duplicated region for block: B:42:0x009e  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean isAllowed(com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType.RLVRuleMatchType r6, java.lang.String r7, java.util.UUID r8, java.util.UUID r9) {
            /*
                r5 = this;
                r4 = 1
                r3 = 0
                com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType$RLVRuleMatchType r0 = com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType.RLVRuleMatchType.TargetSpecifiesAllowance
                if (r6 != r0) goto L_0x0025
                java.util.Map<java.lang.String, java.util.HashSet<java.util.UUID>> r0 = r5.restMap
                java.lang.String r1 = ""
                boolean r0 = r0.containsKey(r1)
                if (r0 == 0) goto L_0x0012
                return r4
            L_0x0012:
                java.lang.String r0 = ""
                boolean r0 = r7.equals(r0)
                if (r0 != 0) goto L_0x0024
                java.util.Map<java.lang.String, java.util.HashSet<java.util.UUID>> r0 = r5.restMap
                boolean r0 = r0.containsKey(r7)
                if (r0 == 0) goto L_0x0024
                return r4
            L_0x0024:
                return r3
            L_0x0025:
                java.util.Map<java.lang.String, java.util.HashSet<java.util.UUID>> r0 = r5.restMap
                boolean r0 = r0.isEmpty()
                if (r0 == 0) goto L_0x002e
                return r4
            L_0x002e:
                int[] r0 = m231getcomlumiyaviewerlumiyaslprotomodulesrlvRLVRestrictionType$RLVRuleMatchTypeSwitchesValues()
                int r1 = r6.ordinal()
                r0 = r0[r1]
                switch(r0) {
                    case 1: goto L_0x0083;
                    case 2: goto L_0x003c;
                    case 3: goto L_0x005a;
                    default: goto L_0x003b;
                }
            L_0x003b:
                return r4
            L_0x003c:
                java.util.Map<java.lang.String, java.util.HashSet<java.util.UUID>> r0 = r5.restMap
                java.lang.String r1 = ""
                boolean r0 = r0.containsKey(r1)
                if (r0 == 0) goto L_0x003b
                java.lang.String r0 = ""
                boolean r0 = r7.equals(r0)
                if (r0 != 0) goto L_0x0059
                java.util.Map<java.lang.String, java.util.HashSet<java.util.UUID>> r0 = r5.restMap
                boolean r0 = r0.containsKey(r7)
                if (r0 == 0) goto L_0x0059
                return r4
            L_0x0059:
                return r3
            L_0x005a:
                java.util.Map<java.lang.String, java.util.HashSet<java.util.UUID>> r0 = r5.restMap
                boolean r0 = r0.containsKey(r7)
                if (r0 == 0) goto L_0x0063
                return r3
            L_0x0063:
                java.util.Map<java.lang.String, java.util.HashSet<java.util.UUID>> r0 = r5.restMap
                java.lang.String r1 = ""
                boolean r0 = r0.containsKey(r1)
                if (r0 == 0) goto L_0x003b
                if (r8 != 0) goto L_0x0071
                return r3
            L_0x0071:
                java.util.Map<java.lang.String, java.util.HashSet<java.util.UUID>> r0 = r5.restMap
                java.lang.String r1 = ""
                java.lang.Object r0 = r0.get(r1)
                java.util.HashSet r0 = (java.util.HashSet) r0
                boolean r0 = r0.contains(r8)
                if (r0 == 0) goto L_0x003b
                return r3
            L_0x0083:
                java.util.Map<java.lang.String, java.util.HashSet<java.util.UUID>> r0 = r5.restMap
                boolean r0 = r0.isEmpty()
                if (r0 != 0) goto L_0x003b
                if (r9 != 0) goto L_0x008e
                return r3
            L_0x008e:
                java.util.Map<java.lang.String, java.util.HashSet<java.util.UUID>> r0 = r5.restMap
                java.util.Collection r0 = r0.values()
                java.util.Iterator r1 = r0.iterator()
            L_0x0098:
                boolean r0 = r1.hasNext()
                if (r0 == 0) goto L_0x003b
                java.lang.Object r0 = r1.next()
                java.util.HashSet r0 = (java.util.HashSet) r0
                int r2 = r0.size()
                if (r2 != r4) goto L_0x00b0
                boolean r0 = r0.contains(r9)
                if (r0 != 0) goto L_0x0098
            L_0x00b0:
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictions.RLVRestrictionList.isAllowed(com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVRestrictionType$RLVRuleMatchType, java.lang.String, java.util.UUID, java.util.UUID):boolean");
        }

        public boolean isEmpty() {
            return this.restMap.isEmpty();
        }

        public void removeAllForObject(UUID uuid) {
            HashSet<String> hashSet = new HashSet<>();
            for (Map.Entry entry : this.restMap.entrySet()) {
                ((HashSet) entry.getValue()).remove(uuid);
                if (((HashSet) entry.getValue()).isEmpty()) {
                    hashSet.add((String) entry.getKey());
                }
            }
            for (String remove : hashSet) {
                this.restMap.remove(remove);
            }
        }

        public void removeRestriction(UUID uuid, String str) {
            HashSet hashSet = this.restMap.get(str);
            if (hashSet != null) {
                hashSet.remove(uuid);
                if (hashSet.isEmpty()) {
                    this.restMap.remove(str);
                }
            }
        }
    }

    public synchronized void addRestriction(RLVRestrictionType rLVRestrictionType, UUID uuid, String str) {
        if (str == null) {
            str = "";
        }
        Debug.Printf("RLV: adding restriction '%s' for object %s, target '%s'", rLVRestrictionType.toString(), uuid, str);
        RLVRestrictionList rLVRestrictionList = this.restrictions.get(rLVRestrictionType);
        if (rLVRestrictionList == null) {
            rLVRestrictionList = new RLVRestrictionList((RLVRestrictionList) null);
            this.restrictions.put(rLVRestrictionType, rLVRestrictionList);
        }
        rLVRestrictionList.addRestriction(uuid, str.toLowerCase());
    }

    public synchronized List<RLVRestrictionType> getRestrictionsByObject(UUID uuid) {
        LinkedList linkedList;
        linkedList = new LinkedList();
        for (Map.Entry entry : this.restrictions.entrySet()) {
            if (((RLVRestrictionList) entry.getValue()).hasRestrictionsByObject(uuid)) {
                linkedList.add((RLVRestrictionType) entry.getKey());
            }
        }
        return linkedList;
    }

    public synchronized Set<String> getTargetsForRestriction(RLVRestrictionType rLVRestrictionType) {
        RLVRestrictionList rLVRestrictionList = this.restrictions.get(rLVRestrictionType);
        if (rLVRestrictionList == null) {
            return null;
        }
        return rLVRestrictionList.getTargets();
    }

    public synchronized boolean isAllowed(RLVRestrictionType rLVRestrictionType, String str, UUID uuid) {
        return isAllowed(rLVRestrictionType, str, uuid, (UUID) null);
    }

    public synchronized boolean isAllowed(RLVRestrictionType rLVRestrictionType, String str, UUID uuid, UUID uuid2) {
        if (str == null) {
            str = "";
        }
        RLVRestrictionList rLVRestrictionList = this.restrictions.get(rLVRestrictionType);
        if (rLVRestrictionList == null) {
            return rLVRestrictionType.getRuleMatchType() != RLVRestrictionType.RLVRuleMatchType.TargetSpecifiesAllowance;
        }
        return rLVRestrictionList.isAllowed(rLVRestrictionType.getRuleMatchType(), str.toLowerCase(), uuid, uuid2);
    }

    public synchronized void removeRestriction(RLVRestrictionType rLVRestrictionType, UUID uuid, String str) {
        if (str == null) {
            str = "";
        }
        Debug.Printf("RLV: removing restriction '%s' for object %s, target '%s'", rLVRestrictionType.toString(), uuid, str);
        RLVRestrictionList rLVRestrictionList = this.restrictions.get(rLVRestrictionType);
        if (rLVRestrictionList != null) {
            rLVRestrictionList.removeRestriction(uuid, str.toLowerCase());
            if (rLVRestrictionList.isEmpty()) {
                this.restrictions.remove(rLVRestrictionType);
            }
        }
    }

    public synchronized void removeRestrictions(UUID uuid, Set<RLVRestrictionType> set) {
        Debug.Printf("RLV: removing %d restrictions for object %s", Integer.valueOf(set.size()), uuid);
        for (RLVRestrictionType rLVRestrictionType : set) {
            RLVRestrictionList rLVRestrictionList = this.restrictions.get(rLVRestrictionType);
            if (rLVRestrictionList != null) {
                rLVRestrictionList.removeAllForObject(uuid);
                if (rLVRestrictionList.isEmpty()) {
                    this.restrictions.remove(rLVRestrictionType);
                }
            }
        }
    }
}
