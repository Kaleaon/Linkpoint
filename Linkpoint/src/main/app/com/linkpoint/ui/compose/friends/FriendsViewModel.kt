package com.linkpoint.ui.compose.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linkpoint.database.entities.FriendEntity
import com.linkpoint.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * ViewModel for Friends Screen
 * 
 * Handles:
 * - Loading friends list
 * - Managing friend status
 * - Real-time updates
 */
@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _friends = MutableStateFlow<List<FriendEntity>>(emptyList())
    val friends: StateFlow<List<FriendEntity>> = _friends.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadFriends()
    }

    /**
     * Load friends list
     */
    private fun loadFriends() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Get all friends
                val friendsList = userRepository.getAllFriends()
                _friends.value = friendsList.sortedWith(compareBy<FriendEntity> { !it.isOnline }.thenBy { it.lastName })
            } catch (e: Exception) {
                android.util.Log.e("FriendsViewModel", "Error loading friends", e)
                // In case of error, show empty list
                _friends.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Add a new friend
     */
    fun addFriend(uuid: UUID, firstName: String, lastName: String) {
        viewModelScope.launch {
            try {
                val friend = FriendEntity(
                    id = 0, // Room will auto-generate
                    uuid = uuid,
                    firstName = firstName,
                    lastName = lastName,
                    isOnline = false,
                    lastSeen = System.currentTimeMillis(),
                    canSeeOnline = true,
                    canSeeMap = false,
                    canModifyObjects = false
                )
                
                userRepository.addFriend(friend)
                loadFriends() // Refresh list
            } catch (e: Exception) {
                android.util.Log.e("FriendsViewModel", "Error adding friend", e)
            }
        }
    }

    /**
     * Remove a friend
     */
    fun removeFriend(friend: FriendEntity) {
        viewModelScope.launch {
            try {
                userRepository.removeFriend(friend.uuid)
                loadFriends() // Refresh list
            } catch (e: Exception) {
                android.util.Log.e("FriendsViewModel", "Error removing friend", e)
            }
        }
    }

    /**
     * Update friend rights
     */
    fun updateFriendRights(
        friend: FriendEntity,
        canSeeOnline: Boolean,
        canSeeMap: Boolean,
        canModifyObjects: Boolean
    ) {
        viewModelScope.launch {
            try {
                val updatedFriend = friend.copy(
                    canSeeOnline = canSeeOnline,
                    canSeeMap = canSeeMap,
                    canModifyObjects = canModifyObjects
                )
                userRepository.updateFriend(updatedFriend)
                loadFriends() // Refresh list
            } catch (e: Exception) {
                android.util.Log.e("FriendsViewModel", "Error updating friend rights", e)
            }
        }
    }

    /**
     * Refresh friends list
     */
    fun refresh() {
        loadFriends()
    }
}