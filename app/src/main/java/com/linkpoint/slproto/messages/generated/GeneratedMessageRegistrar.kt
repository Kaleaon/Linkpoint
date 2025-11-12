package com.linkpoint.slproto.messages

object GeneratedMessageRegistrar {
    fun registerAll() {
        SLMessageFactory.registerMessage(0x0000000C) { SimStatusMessage() }
        SLMessageFactory.registerMessage(0x00000013) { FeatureDisabledMessage() }
        SLMessageFactory.registerMessage(0x00000018) { EdgeDataPacketMessage() }
        SLMessageFactory.registerMessage(0x0000001C) { AtomicPassObjectMessage() }
        SLMessageFactory.registerMessage(0x00000086) { AlertMessage() }
        SLMessageFactory.registerMessage(0x00000088) { MeanCollisionAlertMessage() }
        SLMessageFactory.registerMessage(0x00000089) { ViewerFrozenMessage() }
        SLMessageFactory.registerMessage(0x0000008A) { HealthMessage() }
        SLMessageFactory.registerMessage(0x0000008C) { SimStatsMessage() }
        SLMessageFactory.registerMessage(0x000000F3) { GetScriptRunningMessage() }
        SLMessageFactory.registerMessage(0x000000F4) { ScriptRunningReplyMessage() }
        SLMessageFactory.registerMessage(0x000000F5) { SetScriptRunningMessage() }
        SLMessageFactory.registerMessage(0x000000F6) { ScriptResetMessage() }
        SLMessageFactory.registerMessage(0x000000F7) { ScriptSensorRequestMessage() }
        SLMessageFactory.registerMessage(0x000000F8) { ScriptSensorReplyMessage() }
    }
}
