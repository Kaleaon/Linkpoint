package com.lumiyaviewer.lumiya.slproto.auth

data class SLAuthParams(
    val firstName: String = "",
    val lastName: String = "",
    val password: String = "",
    val startLocation: String = "last",
    val version: String = "",
    val author: String = "",
    val macAddress: String = ""
) {
    fun withLocation(location: String): SLAuthParams = copy(startLocation = location)
}
