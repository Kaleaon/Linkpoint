package com.linkpoint.slproto.users.manager

import com.google.common.collect.ImmutableList
import com.linkpoint.slproto.users.ChatterID

internal class NearbyChattersDisplayDataList(
    userManager: UserManager,
    onListUpdated: OnListUpdated
) : ChatterDisplayDataList(
    userManager, 
    onListUpdated,
    Comparator { data1, data2 ->
        val distanceCompare = Float.compare(data1.distanceToUser, data2.distanceToUser)
        if (distanceCompare != 0) distanceCompare else data1.compareTo(data2)
    }
) {
    override fun getChatters(): List<ChatterID> {
        val activeCircuit = userManager.activeAgentCircuit
        val modules = activeCircuit?.modules
        val nearbyList = modules?.minimap?.nearbyChatterList
        return nearbyList ?: ImmutableList.of()
    }
}