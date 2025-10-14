package com.lumiyaviewer.lumiya.ui.users

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.slproto.users.UserProfile
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nullable

/**
 * Converted from Java to Kotlin
 * UserFunctionsFragment - Displays user profile and available functions
 */
class UserFunctionsFragment : Fragment() {

    @BindView(R.id.userDisplayNameText) lateinit var userDisplayNameText: TextView
    @BindView(R.id.userUsernameText) lateinit var userUsernameText: TextView
    @BindView(R.id.userProfileImage) lateinit var userProfileImage: ImageView
    @BindView(R.id.userAboutText) lateinit var userAboutText: TextView
    @BindView(R.id.userBornDateText) lateinit var userBornDateText: TextView
    @BindView(R.id.userGroupsRecyclerView) lateinit var userGroupsRecyclerView: RecyclerView
    @BindView(R.id.addFriendButton) lateinit var addFriendButton: Button
    @BindView(R.id.removeFriendButton) lateinit var removeFriendButton: Button
    @BindView(R.id.imButton) lateinit var imButton: Button
    @BindView(R.id.offerTeleportButton) lateinit var offerTeleportButton: Button
    @BindView(R.id.teleportToButton) lateinit var teleportToButton: Button
    @BindView(R.id.payButton) lateinit var payButton: Button
    @BindView(R.id.muteButton) lateinit var muteButton: Button
    @BindView(R.id.blockButton) lateinit var blockButton: Button

    private var userUUID: UUID? = null
    private var userProfile: UserProfile? = null
    private var userManager: UserManager? = null
    private var isFriend = false
    private var isMuted = false
    private var isBlocked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_functions, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        userGroupsRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    fun setUser(uuid: UUID, profile: UserProfile, manager: UserManager) {
        this.userUUID = uuid
        this.userProfile = profile
        this.userManager = manager
        
        updateUI()
        updateButtonStates()
    }

    private fun updateUI() {
        userProfile?.let { profile ->
            userDisplayNameText.text = profile.displayName
            userUsernameText.text = "@${profile.username}"
            userAboutText.text = profile.aboutText
            userBornDateText.text = "Member since: ${profile.bornDate}"
            
            // Load profile image
            // profileImageLoader.load(profile.imageUUID, userProfileImage)
        }
    }

    private fun updateButtonStates() {
        addFriendButton.visibility = if (isFriend) View.GONE else View.VISIBLE
        removeFriendButton.visibility = if (isFriend) View.VISIBLE else View.GONE
        muteButton.text = if (isMuted) "Unmute" else "Mute"
        blockButton.text = if (isBlocked) "Unblock" else "Block"
    }

    @OnClick(R.id.addFriendButton)
    fun onAddFriendClick() {
        userUUID?.let { uuid ->
            userManager?.sendFriendshipOffer(uuid)
            Toast.makeText(context, "Friendship offer sent", Toast.LENGTH_SHORT).show()
        }
    }

    @OnClick(R.id.removeFriendButton)
    fun onRemoveFriendClick() {
        userUUID?.let { uuid ->
            showConfirmationDialog("Remove Friend", "Remove this person from your friends list?") {
                userManager?.removeFriend(uuid)
                isFriend = false
                updateButtonStates()
                Toast.makeText(context, "Friend removed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @OnClick(R.id.imButton)
    fun onIMClick() {
        userUUID?.let { uuid ->
            // Open IM window
            Toast.makeText(context, "Opening IM...", Toast.LENGTH_SHORT).show()
        }
    }

    @OnClick(R.id.offerTeleportButton)
    fun onOfferTeleportClick() {
        userUUID?.let { uuid ->
            userManager?.offerTeleport(uuid)
            Toast.makeText(context, "Teleport offer sent", Toast.LENGTH_SHORT).show()
        }
    }

    @OnClick(R.id.teleportToButton)
    fun onTeleportToClick() {
        userUUID?.let { uuid ->
            userManager?.requestTeleport(uuid)
            Toast.makeText(context, "Requesting teleport...", Toast.LENGTH_SHORT).show()
        }
    }

    @OnClick(R.id.payButton)
    fun onPayClick() {
        userUUID?.let { uuid ->
            showPayDialog(uuid)
        }
    }

    @OnClick(R.id.muteButton)
    fun onMuteClick() {
        userUUID?.let { uuid ->
            if (isMuted) {
                userManager?.unmuteUser(uuid)
                isMuted = false
                Toast.makeText(context, "User unmuted", Toast.LENGTH_SHORT).show()
            } else {
                userManager?.muteUser(uuid)
                isMuted = true
                Toast.makeText(context, "User muted", Toast.LENGTH_SHORT).show()
            }
            updateButtonStates()
        }
    }

    @OnClick(R.id.blockButton)
    fun onBlockClick() {
        userUUID?.let { uuid ->
            if (isBlocked) {
                userManager?.unblockUser(uuid)
                isBlocked = false
                Toast.makeText(context, "User unblocked", Toast.LENGTH_SHORT).show()
            } else {
                showConfirmationDialog("Block User", "Block this person? They won't be able to contact you.") {
                    userManager?.blockUser(uuid)
                    isBlocked = true
                    Toast.makeText(context, "User blocked", Toast.LENGTH_SHORT).show()
                }
            }
            updateButtonStates()
        }
    }

    private fun showPayDialog(recipientUUID: UUID) {
        val editText = EditText(context)
        editText.hint = "Amount (L$)"
        editText.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        android.app.AlertDialog.Builder(context)
            .setTitle("Pay User")
            .setMessage("Enter amount to pay:")
            .setView(editText)
            .setPositiveButton("Pay") { _, _ ->
                val amount = editText.text.toString().toIntOrNull()
                if (amount != null && amount > 0) {
                    userManager?.payUser(recipientUUID, amount)
                    Toast.makeText(context, "Paid L$ $amount", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Invalid amount", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showConfirmationDialog(title: String, message: String, onConfirm: () -> Unit) {
        android.app.AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                onConfirm()
            }
            .setNegativeButton("No", null)
            .show()
    }

    fun updateFriendshipStatus(isFriend: Boolean) {
        this.isFriend = isFriend
        updateButtonStates()
    }

    fun updateMuteStatus(isMuted: Boolean) {
        this.isMuted = isMuted
        updateButtonStates()
    }

    fun updateBlockStatus(isBlocked: Boolean) {
        this.isBlocked = isBlocked
        updateButtonStates()
    }
}
