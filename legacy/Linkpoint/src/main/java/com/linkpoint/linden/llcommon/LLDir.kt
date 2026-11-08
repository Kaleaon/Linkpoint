package com.linkpoint.linden.llcommon

import java.io.File
import java.nio.file.Paths

object LLDir {
    private val userHome: String get() = System.getProperty("user.home") ?: ""
    private var appDir: String = userHome

    fun getUserDir(): String = userHome
    fun getOSUserDir(): String = userHome
    fun getAppDir(): String = appDir
    fun setAppDir(dir: String) { appDir = dir }

    fun getCacheDir(): String = Paths.get(userHome, ".firestorm", "cache").toString()
    fun getLogDir(): String = Paths.get(userHome, ".firestorm", "logs").toString()

    fun getExpandedFilename(dir: String, filename: String): String =
        Paths.get(dir, filename).toAbsolutePath().toString()

    fun getDirName(path: String): String = File(path).parent ?: ""
    fun getBaseName(path: String): String = File(path).name
    fun getExtension(path: String): String = File(path).extension

    fun deleteDir(path: String): Boolean = File(path).deleteRecursively()

    fun makePath(vararg parts: String): String =
        parts.fold("") { acc, part -> if (acc.isEmpty()) part else Paths.get(acc, part).toString() }
}
