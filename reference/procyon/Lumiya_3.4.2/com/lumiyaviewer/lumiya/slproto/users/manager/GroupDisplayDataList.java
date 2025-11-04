// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import java.util.Iterator;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.List;
import java.util.Comparator;
import javax.annotation.Nonnull;

class GroupDisplayDataList extends ChatterDisplayDataList
{
    public GroupDisplayDataList(@Nonnull final UserManager userManager, final OnListUpdated onListUpdated) {
        super(userManager, onListUpdated, null);
    }
    
    @Override
    protected List<ChatterID> getChatters() {
        final AvatarGroupList avatarGroupList = this.userManager.getChatterList().getGroupManager().getAvatarGroupList();
        if (avatarGroupList == null) {
            return (List<ChatterID>)ImmutableList.of();
        }
        final ImmutableList.Builder<ChatterID> builder = new ImmutableList.Builder<ChatterID>();
        final Iterator<Object> iterator = avatarGroupList.Groups.values().iterator();
        while (iterator.hasNext()) {
            builder.add(ChatterID.getGroupChatterID(this.userManager.getUserID(), iterator.next().GroupID));
        }
        return builder.build();
    }
}
