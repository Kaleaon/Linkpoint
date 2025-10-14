package com.lumiyaviewer.lumiya.slproto.users.manager

import com.google.common.collect.ImmutableList
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import java.util.Comparator
import java.util.List
import javax.annotation.Nonnull

class GroupDisplayDataList : ChatterDisplayDataList {
    GroupDisplayDataList(@Nonnull UserManager userManager, OnListUpdated onListUpdated) {
        super(userManager, onListUpdated, (Comparator<? super ChatterDisplayData>) null)
    }

    /* access modifiers changed from: protected */
    List<ChatterID> getChatters() {
        AvatarGroupList avatarGroupList = this.userManager.getChatterList().getGroupManager().getAvatarGroupList()
        if (avatarGroupList == null) {
            return ImmutableList.of()
        }
        ImmutableList.Builder builder = ImmutableList.Builder()
        for (AvatarGroupList.AvatarGroupEntry avatarGroupEntry : avatarGroupList.Groups.values()) {
            builder.add((Any) ChatterID.getGroupChatterID(this.userManager.getUserID(), avatarGroupEntry.GroupID))
        }
        return builder.build()
    }
}
