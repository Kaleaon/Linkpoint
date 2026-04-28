package com.linkpoint.linden.llcommon

import java.io.File
import java.io.InputStream
import java.io.OutputStream

object LLFile {
    fun exists(path: String): Boolean = File(path).exists()
    fun isFile(path: String): Boolean = File(path).isFile
    fun isDirectory(path: String): Boolean = File(path).isDirectory
    fun getLength(path: String): Long = File(path).length()
    fun getLastModified(path: String): Long = File(path).lastModified()

    fun rename(from: String, to: String): Boolean = File(from).renameTo(File(to))
    fun remove(path: String): Boolean = File(path).delete()
    fun mkdir(path: String): Boolean = File(path).mkdir()
    fun mkdirs(path: String): Boolean = File(path).mkdirs()

    fun readBytes(path: String): ByteArray = File(path).readBytes()
    fun writeBytes(path: String, data: ByteArray) = File(path).writeBytes(data)
    fun readText(path: String): String = File(path).readText(Charsets.UTF_8)
    fun writeText(path: String, text: String) = File(path).writeText(text, Charsets.UTF_8)
    fun appendText(path: String, text: String) = File(path).appendText(text, Charsets.UTF_8)

    fun openInputStream(path: String): InputStream = File(path).inputStream()
    fun openOutputStream(path: String, append: Boolean = false): OutputStream =
        File(path).outputStream().let { if (append) java.io.FileOutputStream(path, true) else it }

    fun listFiles(dir: String): List<String> =
        File(dir).listFiles()?.map { it.absolutePath } ?: emptyList()
}

data class LLStat(
    val exists: Boolean,
    val isFile: Boolean,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: Long
) {
    companion object {
        fun of(path: String): LLStat {
            val f = File(path)
            return LLStat(f.exists(), f.isFile, f.isDirectory, f.length(), f.lastModified())
        }
    }
}
