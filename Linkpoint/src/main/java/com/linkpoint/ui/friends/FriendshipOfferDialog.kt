package com.linkpoint.ui.friends

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.world.FriendshipOffer
import com.linkpoint.world.FriendsManager
import kotlinx.coroutines.launch

/**
 * Dialog showing friendship offer
 */
class FriendshipOfferDialog : DialogFragment() {

    private lateinit var offer: FriendshipOffer

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        offer = requireArguments().getParcelable("offer")!!

        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_friendship_offer, null)

        view.findViewById<TextView>(R.id.offer_from).text = offer.fromName
        view.findViewById<TextView>(R.id.offer_message).text = 
            offer.message.ifEmpty { getString(R.string.no_message) }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.friendship_offer)
            .setView(view)
            .setPositiveButton(R.string.accept) { _, _ ->
                acceptOffer()
            }
            .setNegativeButton(R.string.decline) { _, _ ->
                declineOffer()
            }
            .create()
    }

    private fun acceptOffer() {
        val friendsManager = LinkpointApp.getInstance().friendsManager
        lifecycleScope.launch {
            friendsManager.acceptFriendship(offer.transactionId)
            dismiss()
        }
    }

    private fun declineOffer() {
        val friendsManager = LinkpointApp.getInstance().friendsManager
        lifecycleScope.launch {
            friendsManager.declineFriendship(offer.transactionId)
            dismiss()
        }
    }

    companion object {
        fun newInstance(offer: FriendshipOffer): FriendshipOfferDialog {
            return FriendshipOfferDialog().apply {
                arguments = Bundle().apply {
                    putParcelable("offer", offer)
                }
            }
        }
    }
}