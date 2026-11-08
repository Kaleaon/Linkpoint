package com.linkpoint.groups.provider

import com.linkpoint.groups.GroupNotice
import com.linkpoint.groups.GroupRole
import com.linkpoint.groups.GroupTitle
import java.util.UUID

/**
 * Unified interface for fetching group roster, roles, titles, and notices
 * across Linden Lab Second Life (UDP/LLSD) and OpenSim grids (XML-RPC / REST).
 */
interface GroupServiceProvider {
    /** Protocol provider type label */
    val providerType: String

    /** Indicates whether this provider is active/valid */
    val isAvailable: Boolean

    /** Fetch group member roster */
    suspend fun getGroupMembers(groupId: UUID): Result<List<GroupMemberRecord>>

    /** Fetch roles defined for the group */
    suspend fun getGroupRoles(groupId: UUID): Result<List<GroupRole>>

    /** Fetch titles available for the agent in the group */
    suspend fun getGroupTitles(groupId: UUID): Result<List<GroupTitle>>

    /** Fetch recent notices for the group */
    suspend fun getGroupNotices(groupId: UUID): Result<List<GroupNotice>>
}
