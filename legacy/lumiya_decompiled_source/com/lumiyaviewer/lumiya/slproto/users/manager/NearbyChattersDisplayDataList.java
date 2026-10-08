package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nonnull;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class NearbyChattersDisplayDataList extends ChatterDisplayDataList {
    public NearbyChattersDisplayDataList(@Nonnull UserManager userManager, OnListUpdated onListUpdated) {
        super(userManager, onListUpdated, new Comparator() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$D8uG4BZ932XmwoX97ZE2tEBU1gE
            private final /* synthetic */ int $m$0(Object obj, Object obj2) {
                return NearbyChattersDisplayDataList.m343x73a7db48((ChatterDisplayData) obj, (ChatterDisplayData) obj2);
            }

            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return $m$0(obj, obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_NearbyChattersDisplayDataList_807  reason: not valid java name */
    public static /* synthetic */ int m343x73a7db48(ChatterDisplayData chatterDisplayData, ChatterDisplayData chatterDisplayData2) {
        int compare = Float.compare(chatterDisplayData.distanceToUser, chatterDisplayData2.distanceToUser);
        return compare != 0 ? compare : chatterDisplayData.compareTo(chatterDisplayData2);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.ChatterDisplayDataList
    protected List<ChatterID> getChatters() {
        SLModules modules;
        List<ChatterID> list = null;
        SLAgentCircuit activeAgentCircuit = this.userManager.getActiveAgentCircuit();
        if (activeAgentCircuit != null && (modules = activeAgentCircuit.getModules()) != null) {
            list = modules.minimap.getNearbyChatterList();
        }
        return list == null ? ImmutableList.of() : list;
    }
}
