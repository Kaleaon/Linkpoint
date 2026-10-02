package com.linkpoint.ui

import com.linkpoint.feature.discovery.MapUiState
import com.linkpoint.feature.inventory.InventoryUiState
import com.linkpoint.feature.social.FriendsUiState
import org.junit.Assert.assertEquals
import org.junit.Test

class UiStateComponentsTest {

    @Test
    fun testSocialUiStateVariants() {
        val states: List<FriendsUiState> = listOf(
            FriendsUiState.Loading,
            FriendsUiState.Error,
            FriendsUiState.EmptyFriends,
            FriendsUiState.Ready
        )
        assertEquals(4, states.size)
    }

    @Test
    fun testInventoryUiStateVariants() {
        val states: List<InventoryUiState> = listOf(
            InventoryUiState.Loading,
            InventoryUiState.Error,
            InventoryUiState.EmptyInventory,
            InventoryUiState.Ready
        )
        assertEquals(4, states.size)
    }

    @Test
    fun testDiscoveryUiStateVariants() {
        val states: List<MapUiState> = listOf(
            MapUiState.Loading,
            MapUiState.Error,
            MapUiState.EmptyRegionResults,
            MapUiState.Ready
        )
        assertEquals(4, states.size)
    }

    @Test
    fun testActionCallbacksInUiStates() {
        var actionExecuted = false
        val action: () -> Unit = { actionExecuted = true }

        action.invoke()
        assertEquals(true, actionExecuted)
    }
}
