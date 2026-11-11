package com.linkpoint.linden.llaudio

abstract class LLStreamingAudio {
    protected var currentUrl: String = ""
    @set:JvmName("setGainProp")
    protected var gain: Float = 1f
    protected var playing: Boolean = false

    abstract fun startStream(url: String)
    abstract fun stopStream()
    abstract fun isPlaying(): Boolean

    open fun setGain(g: Float) { gain = g.coerceIn(0f, 1f) }
    open fun getUrl(): String = currentUrl
    open fun update() {}
}

class LLStreamingAudioNull : LLStreamingAudio() {
    override fun startStream(url: String) { currentUrl = url; playing = true }
    override fun stopStream() { currentUrl = ""; playing = false }
    override fun isPlaying(): Boolean = playing
}
