package com.linkpoint.linden.llcommon

object LLVersionInfo {
    const val CHANNEL: String = "Firestorm-Kotlin"
    const val VERSION_MAJOR: Int = 7
    const val VERSION_MINOR: Int = 1
    const val VERSION_PATCH: Int = 0
    const val VERSION_BUILD: Int = 0

    fun getVersionString(): String = "$VERSION_MAJOR.$VERSION_MINOR.$VERSION_PATCH.$VERSION_BUILD"
    fun getLongVersionString(): String = "$CHANNEL $VERSION_MAJOR.$VERSION_MINOR.$VERSION_PATCH ($VERSION_BUILD)"
    fun getShortVersionString(): String = "$VERSION_MAJOR.$VERSION_MINOR.$VERSION_PATCH"
}
