package com.linkpoint.slproto.auth

/**
 * Parameters for Second Life authentication
 */
data class SLAuthParams(
    val loginUri: String,
    val loginName: String,
    val passwordHash: String,
    val startLocation: String? = null,
    val channel: String = "Linkpoint",
    val version: String = "1.0.0",
    val macAddress: String = "00:00:00:00:00:00",
    val id0: String = "",
    val skipOptionalUpdate: Boolean = false,
)
