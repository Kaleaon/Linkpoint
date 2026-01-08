package com.linkpoint.slproto.users.manager

import com.google.common.collect.ImmutableList
import com.linkpoint.slproto.modules.groups.AvatarGroupList
import com.linkpoint.slproto.users.ChatterID
import java.util.Comparator
import java.util.List
import androidx.annotation.NonNull

class GroupDisplayDataList : ChatterDisplayDataList {
    GroupDisplayDataList(@NonNull UserManager userManager, OnListUpdated onListUpdated) {
        super(userManager, onListUpdated, (Comparator<? super ChatterDisplayData>) null)
    }

    /* access modifiers changed from: protected */
    fun getChatters(): List<ChatterID> {
        AvatarGroupList avatarGroupList = this.userManager.getChatterList().getGroupManager().getAvatarGroupList()
        if (avatarGroupList == null) {
            return ImmutableList.of()
        }
        ImmutableList.Builder builder = ImmutableList.Builder()
        for (AvatarGroupList.AvatarGroupEntry avatarGroupEntry : avatarGroupList.Groups.values()) {
            builder.add((ChatterID as Any).getGroupChatterID(this.userManager.getUserID(), avatarGroupEntry.GroupID))
        }
        return builder.build()
    }
}
