package com.linkpoint.linden.llaudio

import com.linkpoint.linden.llcommon.LLUUID

class LLAudioBuffer(val id: LLUUID) {
    private var data: ByteArray? = null
    var isLoaded: Boolean = false
    var playPosition: Float = 0f
    var sampleRate: Int = 44100
    var channels: Int = 1
    var bitsPerSample: Int = 16

    fun loadWAV(rawData: ByteArray): Boolean {
        if (rawData.size < 44) return false
        val header = rawData.copyOfRange(0, 44)
        val riff = String(header, 0, 4)
        if (riff != "RIFF") return false
        val fmt = String(header, 8, 4)
        if (fmt != "WAVE") return false
        channels = (header[22].toInt() and 0xFF) or ((header[23].toInt() and 0xFF) shl 8)
        sampleRate = (header[24].toInt() and 0xFF) or
            ((header[25].toInt() and 0xFF) shl 8) or
            ((header[26].toInt() and 0xFF) shl 16) or
            ((header[27].toInt() and 0xFF) shl 24)
        bitsPerSample = (header[34].toInt() and 0xFF) or ((header[35].toInt() and 0xFF) shl 8)
        data = rawData
        isLoaded = true
        playPosition = 0f
        return true
    }

    fun getData(): ByteArray? = data
    fun getDurationSeconds(): Float {
        val d = data ?: return 0f
        val byteRate = sampleRate * channels * bitsPerSample / 8
        return if (byteRate == 0) 0f else (d.size - 44).toFloat() / byteRate
    }

    fun unload() { data = null; isLoaded = false; playPosition = 0f }
}
