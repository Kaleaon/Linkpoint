package com.linkpoint.utils

object DiagnosticsLogSanitizer {
    private val sensitiveHeaderRegex = Regex(
        "(?im)^\\s*(Authorization|Proxy-Authorization|Cookie|Set-Cookie|X-Auth-Token|X-Api-Key)\\s*:\\s*.*$"
    )
    private val bearerRegex = Regex("(?i)\\b(Bearer\\s+)[A-Za-z0-9._~+\\-/]+=*")
    private val capabilityPathRegex = Regex("(?i)(/CAPS/)[^/\\s?]+")
    private val sensitiveQueryRegex = Regex(
        "([?&][^=&]*(?:token|auth|session|secret|password|sig|key|cap)[^=&]*=)[^&#\\s]+",
        RegexOption.IGNORE_CASE
    )
    private val sensitiveKeyValueRegex = Regex(
        "(?i)\\b(token|auth|password|secret|cookie|session(id)?|api[-_]?key)\\s*[=:]\\s*([^,;\\s]+)"
    )

    fun sanitize(text: String): String {
        return text
            .replace(sensitiveHeaderRegex) { match -> "${match.groupValues[1]}: <redacted>" }
            .replace(bearerRegex, "$1<redacted>")
            .replace(capabilityPathRegex, "$1<redacted>")
            .replace(sensitiveQueryRegex, "$1<redacted>")
            .replace(sensitiveKeyValueRegex) { match -> "${match.groupValues[1]}=<redacted>" }
    }

    fun sanitizeUrl(url: String): String = sanitize(url)

    fun sanitizeHeader(name: String, value: String): String {
        val lowered = name.lowercase()
        return if (
            lowered.contains("auth") ||
            lowered.contains("token") ||
            lowered.contains("cookie") ||
            lowered.contains("secret") ||
            lowered.contains("password") ||
            lowered.contains("api-key")
        ) {
            "<redacted>"
        } else {
            sanitize(value)
        }
    }
}
