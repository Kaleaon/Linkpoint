package com.linkpoint.ui.people

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.ui.chat.ChatActivity
import com.linkpoint.ui.profile.ProfileActivity
import com.linkpoint.ui.theme.LinkpointTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

/**
 * Activity for viewing nearby people (avatars in the current region).
 *
 * Compose migration status:
 * - Legacy Fragment/RecyclerView UI removed from this entry point.
 * - Screen now renders via NearbyPeopleScreen.
 */
class NearbyPeopleActivity : AppCompatActivity() {

    private val worldMap by lazy { LinkpointApp.getInstance().worldMap }
    private val friendsManager by lazy { LinkpointApp.getInstance().friendsManager }
    private val imManager by lazy { LinkpointApp.getInstance().imManager }

    private var people: List<NearbyPerson> by mutableStateOf(emptyList())
    private var selectedFilter: NearbyPeopleFilter by mutableStateOf(NearbyPeopleFilter.ALL)
    private var isLoading: Boolean by mutableStateOf(false)
    private var emptyMessageOverride: String? by mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LinkpointTheme(darkTheme = true) {
                NearbyPeopleScreen(
                    people = people,
                    selectedFilter = selectedFilter,
                    onFilterChange = { selectedFilter = it },
                    isLoading = isLoading,
                    emptyMessage = emptyMessageOverride,
                    onRefresh = ::loadNearbyPeople,
                    onSendIM = ::sendIM,
                    onAddFriend = ::addFriend,
                    onViewProfile = ::viewProfile,
                    onNavigateBack = ::finish
                )
            }
        }

        loadNearbyPeople()
    }

    private fun loadNearbyPeople() {
        isLoading = true
        emptyMessageOverride = null

        lifecycleScope.launch {
            try {
                people = worldMap.getNearbyUsers()
                    .map {
                        NearbyPerson(
                            id = it.agentId,
                            name = it.name,
                            distance = it.distance,
                            isFriend = it.isFriend
                        )
                    }
            } catch (e: Exception) {
                people = emptyList()
                emptyMessageOverride = getString(R.string.error_loading_nearby_people, e.message ?: "unknown")
            } finally {
                isLoading = false
            }
        }
    }

    private fun sendIM(person: NearbyPerson) {
        val sessionId = imManager.startP2PSession(person.id, person.name)
        imManager.markAsRead(sessionId)

        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra(ChatActivity.EXTRA_IM_SESSION_ID, sessionId.toString())
        }
        startActivity(intent)
    }

    private fun addFriend(person: NearbyPerson) {
        lifecycleScope.launch {
            try {
                friendsManager.offerFriendship(person.id)
                Toast.makeText(
                    this@NearbyPeopleActivity,
                    getString(R.string.nearby_friendship_offer_sent, person.name),
                    Toast.LENGTH_SHORT
                ).show()
                loadNearbyPeople()
            } catch (e: Exception) {
                Toast.makeText(
                    this@NearbyPeopleActivity,
                    getString(R.string.nearby_failed_to_add_friend, person.name),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun viewProfile(person: NearbyPerson) {
        val intent = Intent(this, ProfileActivity::class.java).apply {
            putExtra(ProfileActivity.EXTRA_AGENT_ID, person.id.toString())
            putExtra(ProfileActivity.EXTRA_IS_OWN, false)
        }
        startActivity(intent)
    }
}
