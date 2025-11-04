// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.List;
import java.util.Comparator;
import javax.annotation.Nonnull;

class NearbyChattersDisplayDataList extends ChatterDisplayDataList
{
    public NearbyChattersDisplayDataList(@Nonnull final UserManager userManager, final OnListUpdated onListUpdated) {
        super(userManager, onListUpdated, new _$Lambda$D8uG4BZ932XmwoX97ZE2tEBU1gE());
    }
    
    @Override
    protected List<ChatterID> getChatters() {
        final List list = null;
        final SLAgentCircuit activeAgentCircuit = this.userManager.getActiveAgentCircuit();
        List<ChatterID> nearbyChatterList = list;
        if (activeAgentCircuit != null) {
            final SLModules modules = activeAgentCircuit.getModules();
            nearbyChatterList = list;
            if (modules != null) {
                nearbyChatterList = modules.minimap.getNearbyChatterList();
            }
        }
        Object of;
        if ((of = nearbyChatterList) == null) {
            of = ImmutableList.of();
        }
        return (List<ChatterID>)of;
    }
}
