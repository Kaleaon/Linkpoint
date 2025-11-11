package com.linkpoint.slproto.messages

object GeneratedMessageRegistrar {
    fun registerAll() {
        SLMessageFactory.registerMessage(0x00000013) { FeatureDisabledMessage() }
        SLMessageFactory.registerMessage(0x00000086) { AlertMessage() }
        SLMessageFactory.registerMessage(0x00000088) { MeanCollisionAlertMessage() }
        SLMessageFactory.registerMessage(0x00000089) { ViewerFrozenMessage() }
        SLMessageFactory.registerMessage(0x0000008A) { HealthMessage() }
        SLMessageFactory.registerMessage(0x0000008C) { SimStatsMessage() }
    }
}
