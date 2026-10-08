package com.linkpoint.groups.provider

import com.linkpoint.groups.GroupNotice
import com.linkpoint.groups.GroupRole
import com.linkpoint.groups.GroupTitle
import java.util.UUID

/**
 * Fallback provider that signals default handling via Second Life UDP packets
 * and LLSD capabilities when GroupServerURI is empty or invalid.
 */
class DefaultLlsdUdpGroupProvider : GroupServiceProvider {
    override val providerType: String = "DefaultLLSD"
    override val isAvailable: Boolean = false

    override suspend fun getGroupMembers(groupId: UUID): Result<List<GroupMemberRecord>> {
        return Result.failure(UnsupportedOperationException("Default LLSD/UDP handles group requests asynchronously via packets"))
    }

    override suspend fun getGroupRoles(groupId: UUID): Result<List<GroupRole>> {
        return Result.failure(UnsupportedOperationException("Default LLSD/UDP handles role requests via GroupRoleDataRequest packet"))
    }

    override suspend fun getGroupTitles(groupId: UUID): Result<List<GroupTitle>> {
        return Result.failure(UnsupportedOperationException("Default LLSD/UDP handles title requests via GroupTitlesRequest packet"))
    }

    override suspend fun getGroupNotices(groupId: UUID): Result<List<GroupNotice>> {
        return Result.failure(UnsupportedOperationException("Default LLSD/UDP handles notice requests via GroupNoticesListRequest packet"))
    }
}
