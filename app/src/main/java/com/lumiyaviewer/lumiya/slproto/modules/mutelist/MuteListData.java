package com.lumiyaviewer.lumiya.slproto.modules.mutelist;

import java.util.Map;

public class MuteListData {
    
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_4795  reason: not valid java name */
    static /* synthetic */ boolean m228lambda$com_lumiyaviewer_lumiya_slproto_modules_mutelist_MuteListData_4795(MuteListEntry muteListEntry, Map.Entry entry) {
        if (entry != null && muteListEntry != null && muteListEntry.name != null) {
            return !((String) entry.getKey()).equals(muteListEntry.name);
        }
        return false;
    }
}