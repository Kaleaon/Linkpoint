package com.linkpoint.slproto.users.manager

import com.linkpoint.slproto.users.ParcelData
import com.linkpoint.voice.common.model.VoiceChannelInfo

data class CurrentLocationInfo(
    val parcelData: ParcelData?,
    val inChatRangeUsers: Int,
    val nearbyUsers: Int,
    val parcelVoiceChannel: VoiceChannelInfo?,
) {
    companion object {
        @JvmStatic
        fun create(
            parcelData: ParcelData?,
            i: Int,
            i2: Int,
            voiceChannelInfo: VoiceChannelInfo?,
        ): CurrentLocationInfo {
            return CurrentLocationInfo(parcelData, i, i2, voiceChannelInfo)
        }
    }
}