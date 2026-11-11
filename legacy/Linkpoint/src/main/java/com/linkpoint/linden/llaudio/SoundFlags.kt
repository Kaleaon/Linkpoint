package com.linkpoint.linden.llaudio

// Sound flags used in the viewer protocol and LSL.
// Source: inferred from llsoundflags.h conventions and llaudioengine.h usage.
object SoundFlags {
    // Trigger flags sent with sound packets
    const val LOOP: UByte          = 0x01u  // play sound in a loop
    const val SYNC_MASTER: UByte   = 0x02u  // this source is a sync master
    const val SYNC_SLAVE: UByte    = 0x04u  // this source syncs to a master
    const val QUEUE: UByte         = 0x08u  // queue sounds rather than replace

    // Modifier flags
    const val STOP: UByte          = 0x10u  // stop the sound (not play)

    // Type flags (match LLAudioEngine::LLAudioType ordinals)
    const val TYPE_NONE: Int    = 0
    const val TYPE_SFX: Int     = 1
    const val TYPE_UI: Int      = 2
    const val TYPE_AMBIENT: Int = 3

    // Play-state values (match LLAudioEngine::LLAudioPlayState)
    const val STATE_STOPPED: Int = 0
    const val STATE_PLAYING: Int = 1
    const val STATE_PAUSED: Int  = 2

    // Engine limits
    const val MAX_CHANNELS: Int = 30
    const val MAX_BUFFERS: Int  = 60

    // Timing constants (seconds)
    const val WIND_UPDATE_INTERVAL: Float       = 0.1f
    const val WIND_UNDERWATER_CENTER_FREQ: Float = 20f
    const val ATTACHED_OBJECT_TIMEOUT: Float    = 5.0f
    const val DEFAULT_MIN_DISTANCE: Float       = 2.0f
}
