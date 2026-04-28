package com.linkpoint.linden.llcommon

object LLMemory {
    fun getMaxHeapKB(): Long = Runtime.getRuntime().maxMemory() / 1024L
    fun getTotalHeapKB(): Long = Runtime.getRuntime().totalMemory() / 1024L
    fun getFreeHeapKB(): Long = Runtime.getRuntime().freeMemory() / 1024L
    fun getUsedHeapKB(): Long = getTotalHeapKB() - getFreeHeapKB()
    fun getAvailableMemoryKB(): Long = getMaxHeapKB() - getUsedHeapKB()
    fun logMemoryInfo() {
        println("Max heap: ${getMaxHeapKB()} KB  Used: ${getUsedHeapKB()} KB  Free: ${getFreeHeapKB()} KB")
    }
}

data class LLMemoryInfo(
    val maxHeapKB: Long,
    val totalHeapKB: Long,
    val freeHeapKB: Long
) {
    val usedHeapKB: Long get() = totalHeapKB - freeHeapKB
    val availableKB: Long get() = maxHeapKB - usedHeapKB

    companion object {
        fun capture(): LLMemoryInfo = LLMemoryInfo(
            maxHeapKB = LLMemory.getMaxHeapKB(),
            totalHeapKB = LLMemory.getTotalHeapKB(),
            freeHeapKB = LLMemory.getFreeHeapKB()
        )
    }
}
