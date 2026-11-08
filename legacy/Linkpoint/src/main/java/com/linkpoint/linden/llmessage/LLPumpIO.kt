package com.linkpoint.linden.llmessage

import java.nio.ByteBuffer

fun interface LLIOPipe {
    fun process(context: LLPumpIO.Context): Boolean
}

class LLPumpIO {
    data class Context(
        val input: ByteBuffer,
        val output: ByteBuffer,
        var status: Status = Status.NORMAL
    )

    enum class Status { NORMAL, DONE, ERROR }

    private val chains: MutableMap<String, MutableList<LLIOPipe>> = linkedMapOf()
    private var totalPumped: Long = 0L

    fun addPipe(chainName: String, pipe: LLIOPipe) {
        chains.getOrPut(chainName) { mutableListOf() }.add(pipe)
    }

    fun removePipe(chainName: String, pipe: LLIOPipe) {
        chains[chainName]?.remove(pipe)
    }

    fun pump(chainName: String, inputData: ByteArray): ByteArray {
        val pipes = chains[chainName] ?: return inputData
        var buf = inputData
        for (pipe in pipes) {
            val input  = ByteBuffer.wrap(buf)
            val output = ByteBuffer.allocate(buf.size * 2 + 64)
            val ctx = Context(input, output)
            val ok = runCatching { pipe.process(ctx) }.getOrDefault(false)
            if (!ok || ctx.status == Status.ERROR) break
            output.flip()
            buf = ByteArray(output.remaining()).also { output.get(it) }
        }
        totalPumped++
        return buf
    }

    fun pumpAll(): Int {
        var count = 0
        for (name in chains.keys) { pump(name, ByteArray(0)); count++ }
        return count
    }

    fun clearChain(name: String) = chains.remove(name)

    fun hasChain(name: String): Boolean = name in chains

    fun getTotalPumped(): Long = totalPumped
}
