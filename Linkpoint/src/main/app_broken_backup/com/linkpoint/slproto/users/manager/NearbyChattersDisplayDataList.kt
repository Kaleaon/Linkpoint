package com.linkpoint.slproto.users.manager

import com.google.common.collect.ImmutableList
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.modules.SLModules
import com.linkpoint.slproto.users.ChatterID
import java.util.List
import androidx.annotation.NonNull

class NearbyChattersDisplayDataList : ChatterDisplayDataList {
    NearbyChattersDisplayDataList(@NonNull UserManager userManager, OnListUpdated onListUpdated) {
        super(userManager, onListUpdated, $Lambda$D8uG4BZ932XmwoX97ZE2tEBU1gE())
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_NearbyChattersDisplayDataList_807  reason: not valid java name */
    /* synthetic */ Int m332lambda$com_lumiyaviewer_lumiya_slproto_users_manager_NearbyChattersDisplayDataList_807(ChatterDisplayData chatterDisplayData, ChatterDisplayData chatterDisplayData2) {
        Int compare = Float.compare(chatterDisplayData.distanceToUser, chatterDisplayData2.distanceToUser)
        return compare != 0 ? compare : chatterDisplayData.compareTo(chatterDisplayData2)
    }

    /* access modifiers changed from: protected */
    fun getChatters(): List<ChatterID> {
        SLModules modules
        List<ChatterID> list = null
        SLAgentCircuit activeAgentCircuit = this.userManager.getActiveAgentCircuit()
        if (!(activeAgentCircuit == null || (modules = activeAgentCircuit.getModules()) == null)) {
            list = modules.minimap.getNearbyChatterList()
        }
        return list == null ? ImmutableList.of() : list
    }
}
