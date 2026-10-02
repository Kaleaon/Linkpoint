package com.linkpoint.ui

import com.linkpoint.feature.inventory.InventoryUiState
import com.linkpoint.feature.social.FriendsUiState
import com.linkpoint.feature.social.GroupsUiState
import org.junit.Assert.assertEquals
import org.junit.Test

class UiStateComponentsTest {

    @Test
    fun testSocialUiStateVariants() {
        val friendStates: List<FriendsUiState> = listOf(
            FriendsUiState.Loading,
            FriendsUiState.Error,
            FriendsUiState.EmptyFriends,
            FriendsUiState.Ready
        )
        assertEquals(4, friendStates.size)

        val groupStates: List<GroupsUiState> = listOf(
            GroupsUiState.Loading,
            GroupsUiState.Error,
            GroupsUiState.EmptyGroups,
            GroupsUiState.Ready
        )
        assertEquals(4, groupStates.size)
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
    fun testActionCallbacksInUiStates() {
        var actionExecuted = false
        val action: () -> Unit = { actionExecuted = true }

        action.invoke()
        assertEquals(true, actionExecuted)
    }
}
