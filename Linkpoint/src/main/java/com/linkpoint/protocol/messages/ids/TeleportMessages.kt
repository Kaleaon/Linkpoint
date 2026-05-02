package com.linkpoint.protocol.messages.ids

object TeleportMessages {
    const val TELEPORT_LANDMARK_REQUEST = (0xFFFF0041).toInt()
    const val TELEPORT_HOME_REQUEST = (0xFFFF023A).toInt()
    const val TELEPORT_LOCATION_REQUEST = (0xFFFF003F).toInt()
    const val TELEPORT_LURE_REQUEST = (0xFFFF0047).toInt()
    const val START_LURE = (0xFFFF0046).toInt()
    const val TELEPORT_FINISH = (0xFFFF0045).toInt()
    const val TELEPORT_FAILED = (0xFFFF004A).toInt()
    const val TELEPORT_PROGRESS = (0xFFFF0042).toInt()
    const val TELEPORT_START = (0xFFFF0049).toInt()
}
