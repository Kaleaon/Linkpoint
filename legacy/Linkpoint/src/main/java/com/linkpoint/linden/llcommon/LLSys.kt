package com.linkpoint.linden.llcommon

object LLSys {
    fun getOSString(): String {
        val name = System.getProperty("os.name") ?: "Unknown"
        val version = System.getProperty("os.version") ?: ""
        val arch = System.getProperty("os.arch") ?: ""
        return "$name $version ($arch)".trim()
    }

    fun getCPUString(): String {
        val processors = Runtime.getRuntime().availableProcessors()
        val arch = System.getProperty("os.arch") ?: "unknown"
        return "$arch x$processors"
    }

    fun getProcessorCount(): Int = Runtime.getRuntime().availableProcessors()

    fun getRAMInfo(): String {
        val info = LLMemoryInfo.capture()
        return "Max: ${info.maxHeapKB / 1024} MB  Used: ${info.usedHeapKB / 1024} MB"
    }

    fun getJavaVersion(): String = System.getProperty("java.version") ?: "unknown"

    fun getUserName(): String = System.getProperty("user.name") ?: "unknown"
}
