package com.linkpoint.protocol.messages.diagnostics

class PacketDiagnosticsRecorder<T>(
    private val historySize: Int = DiagnosticsConstants.DEFAULT_PACKET_HISTORY_SIZE,
    private val clock: () -> Long = { System.currentTimeMillis() }
) {
    private val history = ArrayDeque<T>()

    fun record(entry: T) {
        history.addLast(entry)
        while (history.size > historySize) history.removeFirst()
    }

    fun snapshot(): List<T> = history.toList()
    fun now(): Long = clock()
}
