package com.linkpoint.modern.auth

data class OAuth2AuthManager(
    var accessToken: String,
    var refreshToken: String,
    var tokenExpiryTime: Long = 0L,
    var sessionId: String,
    var agentId: String,
    var firstName: String,
    var lastName: String,
    var useTestGrid: Boolean = false,
)
