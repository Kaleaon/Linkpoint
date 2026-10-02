package com.linkpoint.ui

import com.linkpoint.feature.discovery.MapUiState
import com.linkpoint.feature.discovery.MinimapUiState
import com.linkpoint.feature.discovery.RadarUiState
import com.linkpoint.feature.discovery.SLURLUiState
import com.linkpoint.feature.discovery.SearchUiState
import com.linkpoint.feature.discovery.TeleportHistoryUiState
import com.linkpoint.feature.inventory.InventoryUiState
import com.linkpoint.feature.inventory.MyAvatarUiState
import com.linkpoint.feature.inventory.NotecardEditorUiState
import com.linkpoint.feature.inventory.ScriptEditorUiState
import com.linkpoint.feature.social.FriendsUiState
import com.linkpoint.feature.social.GroupsUiState
import com.linkpoint.feature.social.NearbyPeopleUiState
import com.linkpoint.feature.social.ProfileUiState
import org.junit.Assert.assertEquals
import org.junit.Test

class UiStateComponentsTest {

    @Test
    fun testSocialUiStateVariants() {
        val friendsStates: List<FriendsUiState> = listOf(
            FriendsUiState.Loading,
            FriendsUiState.Error,
            FriendsUiState.EmptyFriends,
            FriendsUiState.Ready
        )
        assertEquals(4, friendsStates.size)

        val groupsStates: List<GroupsUiState> = listOf(
            GroupsUiState.Loading,
            GroupsUiState.Error,
            GroupsUiState.EmptyGroups,
            GroupsUiState.Ready
        )
        assertEquals(4, groupsStates.size)

        val profileStates: List<ProfileUiState> = listOf(
            ProfileUiState.Loading,
            ProfileUiState.Error,
            ProfileUiState.EmptyProfile,
            ProfileUiState.Ready
        )
        assertEquals(4, profileStates.size)

        val nearbyStates: List<NearbyPeopleUiState> = listOf(
            NearbyPeopleUiState.Loading,
            NearbyPeopleUiState.Error,
            NearbyPeopleUiState.EmptyNearby,
            NearbyPeopleUiState.Ready
        )
        assertEquals(4, nearbyStates.size)
    }

    @Test
    fun testInventoryUiStateVariants() {
        val inventoryStates: List<InventoryUiState> = listOf(
            InventoryUiState.Loading,
            InventoryUiState.Error,
            InventoryUiState.EmptyInventory,
            InventoryUiState.Ready
        )
        assertEquals(4, inventoryStates.size)

        val avatarStates: List<MyAvatarUiState> = listOf(
            MyAvatarUiState.Loading,
            MyAvatarUiState.Error,
            MyAvatarUiState.EmptyWearables,
            MyAvatarUiState.Ready
        )
        assertEquals(4, avatarStates.size)

        val notecardStates: List<NotecardEditorUiState> = listOf(
            NotecardEditorUiState.Loading,
            NotecardEditorUiState.Error,
            NotecardEditorUiState.EmptyDocument,
            NotecardEditorUiState.Edited
        )
        assertEquals(4, notecardStates.size)

        val scriptStates: List<ScriptEditorUiState> = listOf(
            ScriptEditorUiState.Loading,
            ScriptEditorUiState.Error,
            ScriptEditorUiState.EmptyScript,
            ScriptEditorUiState.Edited
        )
        assertEquals(4, scriptStates.size)
    }

    @Test
    fun testDiscoveryUiStateVariants() {
        val mapStates: List<MapUiState> = listOf(
            MapUiState.Loading,
            MapUiState.Error,
            MapUiState.EmptyRegionResults,
            MapUiState.Ready
        )
        assertEquals(4, mapStates.size)

        val radarStates: List<RadarUiState> = listOf(
            RadarUiState.Loading,
            RadarUiState.Error,
            RadarUiState.EmptyNearbyAgents,
            RadarUiState.Ready
        )
        assertEquals(4, radarStates.size)

        val slurlStates: List<SLURLUiState> = listOf(
            SLURLUiState.Loading,
            SLURLUiState.Error,
            SLURLUiState.InvalidLocation,
            SLURLUiState.Ready
        )
        assertEquals(4, slurlStates.size)

        val historyStates: List<TeleportHistoryUiState> = listOf(
            TeleportHistoryUiState.Loading,
            TeleportHistoryUiState.Error,
            TeleportHistoryUiState.EmptyHistory,
            TeleportHistoryUiState.Ready
        )
        assertEquals(4, historyStates.size)

        val minimapStates: List<MinimapUiState> = listOf(
            MinimapUiState.Loading,
            MinimapUiState.Error,
            MinimapUiState.EmptySurroundings,
            MinimapUiState.Ready
        )
        assertEquals(4, minimapStates.size)

        val searchStates: List<SearchUiState> = listOf(
            SearchUiState.Loading,
            SearchUiState.Error,
            SearchUiState.EmptySearchResults,
            SearchUiState.Ready
        )
        assertEquals(4, searchStates.size)
    }

    @Test
    fun testActionCallbacksInUiStates() {
        var actionExecuted = false
        val action: () -> Unit = { actionExecuted = true }

        action.invoke()
        assertEquals(true, actionExecuted)
    }
}
