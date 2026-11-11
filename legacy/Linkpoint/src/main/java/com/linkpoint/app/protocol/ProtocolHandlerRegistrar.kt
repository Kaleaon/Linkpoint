package com.linkpoint.app.protocol

import com.linkpoint.protocol.messages.UDPConnectionFixed

class ProtocolHandlerRegistrar {
    data class Dependencies(
        val udpConnection: UDPConnectionFixed,
        val registerBlock: () -> Unit
    )

    fun registerAll(dispatcher: Any, dependencies: Dependencies) {
        dependencies.registerBlock.invoke()
    }

    companion object {
        val parserSupportedMessageNamesForConformance: Set<String> = setOf(
            "AgentAlertMessage","AgentDataUpdate","AgentMovementComplete","AvatarAnimation","ChangeUserRights",
            "ChatFromSimulator","CoarseLocationUpdate","CrossedRegion","EnableSimulator","FetchInventory",
            "FetchInventoryDescendents","GroupTitlesRequest","HealthMessage","ImprovedInstantMessage",
            "ImprovedTerseObjectUpdate","KillObject","LayerData","MapNameRequest","ObjectProperties","ObjectUpdate",
            "ObjectUpdateCached","ObjectUpdateCompressed","OfflineNotification","OnlineNotification","PacketAck",
            "ParcelOverlay","RequestPayPrice","RegionHandshake","ScriptControlChange","SoundTrigger","StartPingCheck",
            "TeleportFailed","TeleportFinish","TeleportProgress","TeleportStart","AgentPause","AgentResume","DirFindQuery"
        )
    }
}
