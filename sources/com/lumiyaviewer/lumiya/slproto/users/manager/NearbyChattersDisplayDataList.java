package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.List;
import javax.annotation.Nonnull;

class NearbyChattersDisplayDataList extends ChatterDisplayDataList {
    public NearbyChattersDisplayDataList(@Nonnull UserManager userManager, OnListUpdated onListUpdated) {
        super(userManager, onListUpdated, new $Lambda$D8uG4BZ932XmwoX97ZE2tEBU1gE());
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_NearbyChattersDisplayDataList_807  reason: not valid java name */
    static /* synthetic */ int m332lambda$com_lumiyaviewer_lumiya_slproto_users_manager_NearbyChattersDisplayDataList_807(ChatterDisplayData chatterDisplayData, ChatterDisplayData chatterDisplayData2) {
        int compare = Float.compare(chatterDisplayData.distanceToUser, chatterDisplayData2.distanceToUser);
        return compare != 0 ? compare : chatterDisplayData.compareTo(chatterDisplayData2);
    }

    /* access modifiers changed from: protected */
    public List<ChatterID> getChatters() {
        SLModules modules;
        List<ChatterID> list = null;
        SLAgentCircuit activeAgentCircuit = this.userManager.getActiveAgentCircuit();
        if (!(activeAgentCircuit == null || (modules = activeAgentCircuit.getModules()) == null)) {
            list = modules.minimap.getNearbyChatterList();
        }
        return list == null ? ImmutableList.of() : list;
    }
}
