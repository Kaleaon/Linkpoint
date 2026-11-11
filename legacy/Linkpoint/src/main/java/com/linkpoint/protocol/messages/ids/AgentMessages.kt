package com.linkpoint.protocol.messages.ids

object AgentMessages {
    const val AGENT_UPDATE = 4
    const val AGENT_ANIMATION = (0xFFFF021F).toInt()
    const val AGENT_SET_APPEARANCE = (0xFFFF0054).toInt()
    const val AGENT_IS_NOW_WEARING = (0xFFFF017F).toInt()
    const val AGENT_REQUEST_SIT = 6
    const val AGENT_SIT = 7
    const val AGENT_DATA_UPDATE = (0xFFFF0183).toInt()
    const val AGENT_THROTTLE = (0xFFFF0051).toInt()
    const val AGENT_MOVEMENT_COMPLETE = (0xFFFF00FA).toInt()
    const val COMPLETE_AGENT_MOVEMENT = (0xFFFF00F9).toInt()
    const val AGENT_PAUSE = (0xFFFF004E).toInt()
    const val AGENT_RESUME = (0xFFFF004F).toInt()
    const val AGENT_WEARABLES_REQUEST = (0xFFFF017D).toInt()
}
