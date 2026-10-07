package com.linkpoint.groups.provider

import android.util.Log
import com.linkpoint.groups.GroupNotice
import com.linkpoint.groups.GroupRole
import com.linkpoint.groups.GroupTitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URI
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * SimianGrid REST Group client.
 * Executes REST POST requests to GroupServerURI endpoints for Simian group modules.
 */
class SimianRestGroupClient(
    val groupServerUri: String,
    private val agentId: UUID,
    private val sessionId: UUID = UUID(0L, 0L),
    client: OkHttpClient? = null
) : GroupServiceProvider {

    companion object {
        private const val TAG = "SimianRestGroup"
        private const val TIMEOUT_SECONDS = 10L
    }

    private val okHttpClient: OkHttpClient = client ?: OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    override val providerType: String = "SimianRest"

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
                val params = mapOf(
                    "RequestMethod" to "GetGroupMembers",
                    "GroupID" to groupId.toString(),
                    "AgentID" to agentId.toString(),
                    "SessionID" to sessionId.toString()
                )
                val responseStr = executeRestPost(params)
                val members = parseMembersResponse(responseStr)
                Result.success(members)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching group members via REST for $groupId", e)
                Result.failure(e)
            }
        }

    override suspend fun getGroupRoles(groupId: UUID): Result<List<GroupRole>> =
        withContext(Dispatchers.IO) {
            if (!isAvailable) {
                return@withContext Result.failure(IllegalArgumentException("Invalid GroupServerURI: $groupServerUri"))
            }

            try {
                val params = mapOf(
                    "RequestMethod" to "GetGroupRoles",
                    "GroupID" to groupId.toString(),
                    "AgentID" to agentId.toString(),
                    "SessionID" to sessionId.toString()
                )
                val responseStr = executeRestPost(params)
                val roles = parseRolesResponse(responseStr)
                Result.success(roles)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching group roles via REST for $groupId", e)
                Result.failure(e)
            }
        }

    override suspend fun getGroupTitles(groupId: UUID): Result<List<GroupTitle>> =
        withContext(Dispatchers.IO) {
            if (!isAvailable) {
                return@withContext Result.failure(IllegalArgumentException("Invalid GroupServerURI: $groupServerUri"))
            }

            try {
                val params = mapOf(
                    "RequestMethod" to "GetGroupTitles",
                    "GroupID" to groupId.toString(),
                    "AgentID" to agentId.toString(),
                    "SessionID" to sessionId.toString()
                )
                val responseStr = executeRestPost(params)
                val titles = parseTitlesResponse(responseStr)
                Result.success(titles)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching group titles via REST for $groupId", e)
                Result.failure(e)
            }
        }

    override suspend fun getGroupNotices(groupId: UUID): Result<List<GroupNotice>> =
        withContext(Dispatchers.IO) {
            if (!isAvailable) {
                return@withContext Result.failure(IllegalArgumentException("Invalid GroupServerURI: $groupServerUri"))
            }

            try {
                val params = mapOf(
                    "RequestMethod" to "GetGroupNotices",
                    "GroupID" to groupId.toString(),
                    "AgentID" to agentId.toString(),
                    "SessionID" to sessionId.toString()
                )
                val responseStr = executeRestPost(params)
                val notices = parseNoticesResponse(responseStr, groupId)
                Result.success(notices)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching group notices via REST for $groupId", e)
                Result.failure(e)
            }
        }

    private fun executeRestPost(params: Map<String, String>): String {
        val formBuilder = FormBody.Builder()
        for ((key, value) in params) {
            formBuilder.add(key, value)
        }

        val request = Request.Builder()
            .url(groupServerUri)
            .post(formBuilder.build())
            .header("User-Agent", "Linkpoint/1.0.0 SimianRestGroupClient")
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            val code = response.code
            response.close()
            throw IllegalStateException("REST request failed with HTTP status $code")
        }

        val bodyStr = response.body?.string().orEmpty()
        response.close()
        return bodyStr
    }

    fun parseMembersResponse(body: String): List<GroupMemberRecord> {
        val members = mutableListOf<GroupMemberRecord>()
        // Simple regex-based field parsing supporting JSON/LLSD/Form-array structures
        val agentIdPattern = Pattern.compile("[\"']?(?:AgentID|agent_id)[\"']?\\s*[:=]\\s*[\"']?([a-fA-F0-9-]{36})[\"']?", Pattern.CASE_INSENSITIVE)
        val matcher = agentIdPattern.matcher(body)

        while (matcher.find()) {
            val agentIdStr = matcher.group(1) ?: continue
            val agentUuid = try {
                UUID.fromString(agentIdStr)
            } catch (e: Exception) {
                continue
            }

            members.add(
                GroupMemberRecord(
                    agentId = agentUuid,
                    title = "",
                    isOwner = false,
                    isOnline = false
                )
            )
        }

        return members
    }

    fun parseRolesResponse(body: String): List<GroupRole> {
        val roles = mutableListOf<GroupRole>()
        val roleIdPattern = Pattern.compile("[\"']?(?:RoleID|role_id)[\"']?\\s*[:=]\\s*[\"']?([a-fA-F0-9-]{36})[\"']?", Pattern.CASE_INSENSITIVE)
        val matcher = roleIdPattern.matcher(body)

        while (matcher.find()) {
            val roleIdStr = matcher.group(1) ?: continue
            val roleUuid = try {
                UUID.fromString(roleIdStr)
            } catch (e: Exception) {
                continue
            }

            roles.add(
                GroupRole(
                    roleId = roleUuid,
                    name = "Role",
                    title = "",
                    description = "",
                    powers = 0L,
                    members = 0
                )
            )
        }

        return roles
    }

    fun parseTitlesResponse(body: String): List<GroupTitle> {
        val titles = mutableListOf<GroupTitle>()
        val roleIdPattern = Pattern.compile("[\"']?(?:RoleID|role_id)[\"']?\\s*[:=]\\s*[\"']?([a-fA-F0-9-]{36})[\"']?", Pattern.CASE_INSENSITIVE)
        val matcher = roleIdPattern.matcher(body)

        while (matcher.find()) {
            val roleIdStr = matcher.group(1) ?: continue
            val roleUuid = try {
                UUID.fromString(roleIdStr)
            } catch (e: Exception) {
                continue
            }

            titles.add(
                GroupTitle(
                    title = "Title",
                    roleId = roleUuid,
                    selected = false
                )
            )
        }

        return titles
    }

    fun parseNoticesResponse(body: String, groupId: UUID): List<GroupNotice> {
        val notices = mutableListOf<GroupNotice>()
        val noticeIdPattern = Pattern.compile("[\"']?(?:NoticeID|notice_id)[\"']?\\s*[:=]\\s*[\"']?([a-fA-F0-9-]{36})[\"']?", Pattern.CASE_INSENSITIVE)
        val matcher = noticeIdPattern.matcher(body)

        while (matcher.find()) {
            val noticeIdStr = matcher.group(1) ?: continue
            val noticeUuid = try {
                UUID.fromString(noticeIdStr)
            } catch (e: Exception) {
                UUID.randomUUID()
            }

            notices.add(
                GroupNotice(
                    noticeId = noticeUuid,
                    groupId = groupId,
                    senderId = UUID(0L, 0L),
                    senderName = "Group Notice",
                    subject = "",
                    message = "",
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        return notices
    }
}
