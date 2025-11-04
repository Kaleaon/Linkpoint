package com.linkpoint.slproto.auth

import com.linkpoint.utils.HashUtils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

/**
 * Handles authentication against the Second Life login service.
 */
class SLAuth(
    private val httpClient: OkHttpClient = defaultClient
) {

    @Throws(IOException::class)
    fun login(params: SLAuthParams): SLAuthReply {
        val requestXml = buildLoginRequest(params)
        val request = Request.Builder()
            .url(params.loginUrl)
            .post(requestXml.toRequestBody(XML_MEDIA_TYPE))
            .header("Content-Type", "text/xml")
            .header("Connection", "close")
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Login failed: HTTP ${response.code}")
            }

            val body = response.body?.string() ?: throw IOException("Empty login response")
            return SLAuthReply.fromXml(params.gridName, params.loginUrl, body)
        }
    }

    private fun buildLoginRequest(params: SLAuthParams): String {
        val (firstName, lastName) = splitLegacyName(params.loginName)
        val channel = DEFAULT_CHANNEL
        val version = DEFAULT_VERSION
        val platformVersion = android.os.Build.VERSION.RELEASE ?: "unknown"

        val builder = StringBuilder()
        builder.append("""<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n""")
        builder.append("<methodCall>\n")
        builder.append("  <methodName>login_to_simulator</methodName>\n")
        builder.append("  <params>\n")
        builder.append("    <param>\n")
        builder.append("      <value>\n")
        builder.append("        <struct>\n")
        appendMember(builder, "first", firstName)
        appendMember(builder, "last", lastName)
        appendMember(builder, "passwd", params.passwordHash)
        appendMember(builder, "start", params.startLocation)
        appendMember(builder, "channel", channel)
        appendMember(builder, "version", version)
        appendMember(builder, "platform", PLATFORM)
        appendMember(builder, "platform_version", platformVersion)
        appendMember(builder, "mac", HashUtils.MD5_Hash("android_id"))
        appendMember(builder, "user-agent", USER_AGENT)
        appendMember(builder, "id0", params.clientId.toString())
        appendMember(builder, "agree_to_tos", "true")
        appendMember(builder, "viewer_digest", VIEWER_DIGEST)
        appendOptions(builder)
        builder.append("        </struct>\n")
        builder.append("      </value>\n")
        builder.append("    </param>\n")
        builder.append("  </params>\n")
        builder.append("</methodCall>\n")
        return builder.toString()
    }

    private fun appendOptions(builder: StringBuilder) {
        builder.append("        <member>\n")
        builder.append("          <name>options</name>\n")
        builder.append("          <value>\n")
        builder.append("            <array>\n")
        builder.append("              <data>\n")
        for (option in LOGIN_OPTIONS) {
            builder.append("                <value><string>")
            builder.append(escapeXml(option))
            builder.append("</string></value>\n")
        }
        builder.append("              </data>\n")
        builder.append("            </array>\n")
        builder.append("          </value>\n")
        builder.append("        </member>\n")
    }

    private fun appendMember(builder: StringBuilder, name: String, value: String) {
        builder.append("        <member>\n")
        builder.append("          <name>")
        builder.append(escapeXml(name))
        builder.append("</name>\n")
        builder.append("          <value><string>")
        builder.append(escapeXml(value))
        builder.append("</string></value>\n")
        builder.append("        </member>\n")
    }

    private fun splitLegacyName(loginName: String): Pair<String, String> {
        val trimmed = loginName.trim()
        if (trimmed.isEmpty()) {
            return DEFAULT_FIRST to DEFAULT_LAST
        }
        val separators = charArrayOf(' ', '.', '_')
        val index = trimmed.indexOfAny(separators).takeIf { it >= 0 } ?: trimmed.length
        val first = trimmed.substring(0, index).ifBlank { DEFAULT_FIRST }
        val last = trimmed.substring(index).trim().takeIf { it.isNotEmpty() } ?: DEFAULT_LAST
        return first to last
    }

    companion object {
        private const val PLATFORM = "Android"
        private const val USER_AGENT = "Linkpoint"
        private const val VIEWER_DIGEST = "f50cfcc3-d6ce-4f16-a822-b91271de4c48"
        private const val DEFAULT_CHANNEL = "Linkpoint"
        private const val DEFAULT_VERSION = "1.0.0"
        private const val DEFAULT_FIRST = "First"
        private const val DEFAULT_LAST = "Resident"
        private val LOGIN_OPTIONS = listOf(
            "buddy-list",
            "display_names",
            "inventory-root",
            "inventory-lib-root",
            "max-agent-groups"
        )

        private val XML_MEDIA_TYPE = "text/xml; charset=utf-8".toMediaType()

        private val defaultClient: OkHttpClient by lazy {
            OkHttpClient.Builder().build()
        }

        fun getPasswordHash(password: String): String {
            var trimmed = password.trim()
            if (trimmed.length > 16) {
                trimmed = trimmed.substring(0, 16)
            }
            return "${'$'}1${'$'}" + HashUtils.MD5_Hash(trimmed)
        }

        private fun escapeXml(value: String): String {
            return buildString(value.length) {
                value.forEach { ch ->
                    when (ch) {
                        '&' -> append("&amp;")
                        '<' -> append("&lt;")
                        '>' -> append("&gt;")
                        '"' -> append("&quot;")
                        '\'' -> append("&apos;")
                        else -> append(ch)
                    }
                }
            }
        }
    }
}
