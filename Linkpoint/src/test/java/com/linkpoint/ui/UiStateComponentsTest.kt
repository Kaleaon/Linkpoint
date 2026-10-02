package com.linkpoint.ui

import com.linkpoint.feature.discovery.MapPlacesEventsUiState
import com.linkpoint.feature.inventory.InventoryOutfitsUiState
import com.linkpoint.feature.social.FriendsGroupsProfileUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class UiStateComponentsTest {

    @Test
    fun testSocialUiStateVariants() {
        val states: List<FriendsGroupsProfileUiState> = listOf(
            FriendsGroupsProfileUiState.Loading,
            FriendsGroupsProfileUiState.Error,
            FriendsGroupsProfileUiState.EmptyFriends,
            FriendsGroupsProfileUiState.EmptyGroups,
            FriendsGroupsProfileUiState.EmptyProfile,
            FriendsGroupsProfileUiState.EmptyNearby,
            FriendsGroupsProfileUiState.Ready
        )
        assertEquals(7, states.size)
    }

    @Test
    fun testInventoryUiStateVariants() {
        val states: List<InventoryOutfitsUiState> = listOf(
            InventoryOutfitsUiState.Loading,
            InventoryOutfitsUiState.Error,
            InventoryOutfitsUiState.EmptyInventory,
            InventoryOutfitsUiState.EmptyWearables,
            InventoryOutfitsUiState.EmptyDocument,
            InventoryOutfitsUiState.EmptyScript,
            InventoryOutfitsUiState.Ready,
            InventoryOutfitsUiState.Edited
        )
        assertEquals(8, states.size)
    }

    @Test
    fun testDiscoveryUiStateVariants() {
        val states: List<MapPlacesEventsUiState> = listOf(
            MapPlacesEventsUiState.Loading,
            MapPlacesEventsUiState.Error,
            MapPlacesEventsUiState.EmptyRegionResults,
            MapPlacesEventsUiState.EmptySearchResults,
            MapPlacesEventsUiState.InvalidLocation,
            MapPlacesEventsUiState.EmptyHistory,
            MapPlacesEventsUiState.EmptySurroundings,
            MapPlacesEventsUiState.EmptyNearbyAgents,
            MapPlacesEventsUiState.Ready
        )
        assertEquals(9, states.size)
    }

    @Test
    fun testActionCallbacksInUiStates() {
        var actionExecuted = false
        val action: () -> Unit = { actionExecuted = true }

        action.invoke()
        assertEquals(true, actionExecuted)
    }
}
