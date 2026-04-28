package com.linkpoint.linden.llaudio

abstract class LLMusicStreamPlugin {
    enum class State { STOPPED, PLAYING, PAUSED, BUFFERING, ERROR }
    protected var state: State = State.STOPPED
    protected var url: String = ""

    abstract fun play(url: String)
    abstract fun stop()
    abstract fun pause()
    abstract fun unpause()

    open fun setVolume(vol: Float) {}
    open fun getMetadata(key: String): String? = null
}

class LLMusicStreamPluginNull : LLMusicStreamPlugin() {
    override fun play(url: String) { this.url = url; state = State.PLAYING }
    override fun stop() { url = ""; state = State.STOPPED }
    override fun pause() { if (state == State.PLAYING) state = State.PAUSED }
    override fun unpause() { if (state == State.PAUSED) state = State.PLAYING }
}
