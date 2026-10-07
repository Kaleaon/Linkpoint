package com.linkpoint.groups.provider

import android.util.Log
import com.linkpoint.groups.GroupNotice
import com.linkpoint.groups.GroupRole
import com.linkpoint.groups.GroupTitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URI
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * OpenSim Flotsam groups.php XML-RPC client.
 * Executes XML-RPC requests to GroupServerURI endpoints using OkHttp connection pools.
 */
class FlotsamXmlRpcGroupClient(
    val groupServerUri: String,
    private val agentId: UUID,
    private val sessionId: UUID = UUID(0L, 0L),
    client: OkHttpClient? = null
) : GroupServiceProvider {

    companion object {
        private const val TAG = "FlotsamXmlRpcGroup"
        private const val TIMEOUT_SECONDS = 10L
        private val XML_MEDIA_TYPE = "text/xml".toMediaType()
    }

    private val okHttpClient: OkHttpClient = client ?: OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    override val providerType: String = "FlotsamXmlRpc"

    override val isAvailable: Boolean
        get() = isValidUri(groupServerUri)

    private fun isValidUri(uriStr: String): Boolean {
        if (uriStr.isBlank()) return false
        return try {
            val uri = URI(uriStr.trim())
            val scheme = uri.scheme?.lowercase()
            (scheme == "http" || scheme == "https") && !uri.host.isNullOrEmpty()
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getGroupMembers(groupId: UUID): Result<List<GroupMemberRecord>> =
        withContext(Dispatchers.IO) {
            if (!isAvailable) {
                return@withContext Result.failure(IllegalArgumentException("Invalid GroupServerURI: $groupServerUri"))
            }

            try {
                val xmlPayload = buildXmlRpcRequest("groups.getGroupMembers", groupId)
                val responseXml = executeRequest(xmlPayload)
                val members = parseMembersResponse(responseXml)
                Result.success(members)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching group members for $groupId", e)
                Result.failure(e)
            }
        }

    override suspend fun getGroupRoles(groupId: UUID): Result<List<GroupRole>> =
        withContext(Dispatchers.IO) {
            if (!isAvailable) {
                return@withContext Result.failure(IllegalArgumentException("Invalid GroupServerURI: $groupServerUri"))
            }

            try {
                val xmlPayload = buildXmlRpcRequest("groups.getGroupRoles", groupId)
                val responseXml = executeRequest(xmlPayload)
                val roles = parseRolesResponse(responseXml)
                Result.success(roles)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching group roles for $groupId", e)
                Result.failure(e)
            }
        }

    override suspend fun getGroupTitles(groupId: UUID): Result<List<GroupTitle>> =
        withContext(Dispatchers.IO) {
            if (!isAvailable) {
                return@withContext Result.failure(IllegalArgumentException("Invalid GroupServerURI: $groupServerUri"))
            }

            try {
                val xmlPayload = buildXmlRpcRequest("groups.getGroupTitles", groupId)
                val responseXml = executeRequest(xmlPayload)
                val titles = parseTitlesResponse(responseXml)
                Result.success(titles)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching group titles for $groupId", e)
                Result.failure(e)
            }
        }

    override suspend fun getGroupNotices(groupId: UUID): Result<List<GroupNotice>> =
        withContext(Dispatchers.IO) {
            if (!isAvailable) {
                return@withContext Result.failure(IllegalArgumentException("Invalid GroupServerURI: $groupServerUri"))
            }

            try {
                val xmlPayload = buildXmlRpcRequest("groups.getGroupNotices", groupId)
                val responseXml = executeRequest(xmlPayload)
                val notices = parseNoticesResponse(responseXml, groupId)
                Result.success(notices)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching group notices for $groupId", e)
                Result.failure(e)
            }
        }

    fun buildXmlRpcRequest(methodName: String, groupId: UUID): String {
        return buildString {
            append("<?xml version=\"1.0\"?>")
            append("<methodCall>")
            append("<methodName>$methodName</methodName>")
            append("<params><param><value><struct>")
            append("<member><name>RequestingAgentID</name><value><string>$agentId</string></value></member>")
            append("<member><name>RequestingSessionID</name><value><string>$sessionId</string></value></member>")
            append("<member><name>GroupID</name><value><string>$groupId</string></value></member>")
            append("</struct></value></param></params>")
            append("</methodCall>")
        }
    }

    private fun executeRequest(xmlPayload: String): String {
        val requestBytes = xmlPayload.toByteArray(Charsets.UTF_8)
        val request = Request.Builder()
            .url(groupServerUri)
            .post(requestBytes.toRequestBody(XML_MEDIA_TYPE))
            .header("Content-Type", "text/xml")
            .header("User-Agent", "Linkpoint/1.0.0 FlotsamXmlRpcGroupClient")
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            val code = response.code
            response.close()
            throw IllegalStateException("XML-RPC request failed with HTTP status $code")
        }

        val bodyStr = response.body?.string().orEmpty()
        response.close()
        return bodyStr
    }

    fun parseMembersResponse(xml: String): List<GroupMemberRecord> {
        val members = mutableListOf<GroupMemberRecord>()
        val structs = extractStructs(xml)

        for (structXml in structs) {
            val agentIdStr = extractMemberValue(structXml, "AgentID")
                ?: extractMemberValue(structXml, "agent_id")
                ?: continue

            val memberUuid = try {
                UUID.fromString(agentIdStr)
            } catch (e: Exception) {
                continue
            }

            val title = extractMemberValue(structXml, "Title")
                ?: extractMemberValue(structXml, "title")
                ?: ""

            val powersStr = extractMemberValue(structXml, "AgentPowers")
                ?: extractMemberValue(structXml, "powers")
            val powers = powersStr?.toLongOrNull() ?: 0L

            val isOwnerVal = extractMemberValue(structXml, "IsOwner")
                ?: extractMemberValue(structXml, "is_owner")
            val isOwner = isOwnerVal?.equals("true", ignoreCase = true) == true ||
                isOwnerVal == "1" || (powers and 1L) != 0L

            val onlineVal = extractMemberValue(structXml, "OnlineStatus")
                ?: extractMemberValue(structXml, "IsOnline")
                ?: extractMemberValue(structXml, "online")
            val isOnline = onlineVal?.equals("true", ignoreCase = true) == true || onlineVal == "1"

            val contributionStr = extractMemberValue(structXml, "Contribution")
            val contribution = contributionStr?.toIntOrNull() ?: 0

            val acceptNoticesVal = extractMemberValue(structXml, "AcceptNotices")
            val acceptNotices = acceptNoticesVal == null || acceptNoticesVal.equals("true", ignoreCase = true) || acceptNoticesVal == "1"

            val listInProfileVal = extractMemberValue(structXml, "ListInProfile")
            val listInProfile = listInProfileVal == null || listInProfileVal.equals("true", ignoreCase = true) || listInProfileVal == "1"

            members.add(
                GroupMemberRecord(
                    agentId = memberUuid,
                    title = title,
                    isOwner = isOwner,
                    isOnline = isOnline,
                    powers = powers,
                    contribution = contribution,
                    acceptNotices = acceptNotices,
                    listInProfile = listInProfile
                )
            )
        }

        return members
    }

    fun parseRolesResponse(xml: String): List<GroupRole> {
        val roles = mutableListOf<GroupRole>()
        val structs = extractStructs(xml)

        for (structXml in structs) {
            val roleIdStr = extractMemberValue(structXml, "RoleID")
                ?: extractMemberValue(structXml, "role_id")
                ?: continue

            val roleUuid = try {
                UUID.fromString(roleIdStr)
            } catch (e: Exception) {
                continue
            }

            val name = extractMemberValue(structXml, "Name")
                ?: extractMemberValue(structXml, "name")
                ?: "Role"

            val title = extractMemberValue(structXml, "Title")
                ?: extractMemberValue(structXml, "title")
                ?: ""

            val description = extractMemberValue(structXml, "Description")
                ?: extractMemberValue(structXml, "description")
                ?: ""

            val powersStr = extractMemberValue(structXml, "Powers")
                ?: extractMemberValue(structXml, "powers")
            val powers = powersStr?.toLongOrNull() ?: 0L

            val membersStr = extractMemberValue(structXml, "Members")
                ?: extractMemberValue(structXml, "members")
            val members = membersStr?.toIntOrNull() ?: 0

            roles.add(
                GroupRole(
                    roleId = roleUuid,
                    name = name,
                    title = title,
                    description = description,
                    powers = powers,
                    members = members
                )
            )
        }

        return roles
    }

    fun parseTitlesResponse(xml: String): List<GroupTitle> {
        val titles = mutableListOf<GroupTitle>()
        val structs = extractStructs(xml)

        for (structXml in structs) {
            val titleStr = extractMemberValue(structXml, "Title")
                ?: extractMemberValue(structXml, "title")
                ?: continue

            val roleIdStr = extractMemberValue(structXml, "RoleID")
                ?: extractMemberValue(structXml, "role_id")
                ?: continue

            val roleUuid = try {
                UUID.fromString(roleIdStr)
            } catch (e: Exception) {
                continue
            }

            val selectedVal = extractMemberValue(structXml, "Selected")
                ?: extractMemberValue(structXml, "selected")
            val selected = selectedVal?.equals("true", ignoreCase = true) == true || selectedVal == "1"

            titles.add(
                GroupTitle(
                    title = titleStr,
                    roleId = roleUuid,
                    selected = selected
                )
            )
        }

        return titles
    }

    fun parseNoticesResponse(xml: String, groupId: UUID): List<GroupNotice> {
        val notices = mutableListOf<GroupNotice>()
        val structs = extractStructs(xml)

        for (structXml in structs) {
            val noticeIdStr = extractMemberValue(structXml, "NoticeID")
                ?: extractMemberValue(structXml, "notice_id")

            val noticeUuid = try {
                if (noticeIdStr != null) UUID.fromString(noticeIdStr) else UUID.randomUUID()
            } catch (e: Exception) {
                UUID.randomUUID()
            }

            val senderIdStr = extractMemberValue(structXml, "SenderID")
                ?: extractMemberValue(structXml, "sender_id")
            val senderUuid = try {
                if (senderIdStr != null) UUID.fromString(senderIdStr) else UUID(0L, 0L)
            } catch (e: Exception) {
                UUID(0L, 0L)
            }

            val senderName = extractMemberValue(structXml, "FromName")
                ?: extractMemberValue(structXml, "sender_name")
                ?: "Group Notice"

            val subject = extractMemberValue(structXml, "Subject")
                ?: extractMemberValue(structXml, "subject")
                ?: ""

            val message = extractMemberValue(structXml, "Message")
                ?: extractMemberValue(structXml, "message")
                ?: ""

            val timestampStr = extractMemberValue(structXml, "Timestamp")
                ?: extractMemberValue(structXml, "timestamp")
            val timestamp = timestampStr?.toLongOrNull() ?: System.currentTimeMillis()

            val hasAttachmentVal = extractMemberValue(structXml, "HasAttachment")
            val hasAttachment = hasAttachmentVal?.equals("true", ignoreCase = true) == true || hasAttachmentVal == "1"

            val attachmentName = extractMemberValue(structXml, "AttachmentName")

            notices.add(
                GroupNotice(
                    noticeId = noticeUuid,
                    groupId = groupId,
                    senderId = senderUuid,
                    senderName = senderName,
                    subject = subject,
                    message = message,
                    timestamp = timestamp,
                    hasAttachment = hasAttachment,
                    attachmentName = attachmentName
                )
            )
        }

        return notices
    }

    private fun extractStructs(xml: String): List<String> {
        val structs = mutableListOf<String>()
        val pattern = Pattern.compile("<struct>(.*?)</struct>", Pattern.DOTALL)
        val matcher = pattern.matcher(xml)
        while (matcher.find()) {
            matcher.group(1)?.let { structs.add(it) }
        }
        return structs
    }

    private fun extractMemberValue(structXml: String, name: String): String? {
        // String
        val strRegex = """<name>$name</name>\s*<value>\s*<string>([^<]*)</string>""".toRegex(RegexOption.IGNORE_CASE)
        strRegex.find(structXml)?.groupValues?.get(1)?.let { return it.trim() }

        // UUID / String tag
        val uuidRegex = """<name>$name</name>\s*<value>\s*<(?:uuid|string)>([^<]*)</(?:uuid|string)>""".toRegex(RegexOption.IGNORE_CASE)
        uuidRegex.find(structXml)?.groupValues?.get(1)?.let { return it.trim() }

        // Int / i4 / boolean
        val intRegex = """<name>$name</name>\s*<value>\s*<(?:i4|int|integer|boolean)>([^<]*)</(?:i4|int|integer|boolean)>""".toRegex(RegexOption.IGNORE_CASE)
        intRegex.find(structXml)?.groupValues?.get(1)?.let { return it.trim() }

        return null
    }
}
