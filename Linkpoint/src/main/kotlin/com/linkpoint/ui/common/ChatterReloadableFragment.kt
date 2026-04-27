package com.linkpoint.ui.common

import android.content.Intent
import android.os.Bundle
import com.linkpoint.slproto.users.ChatterID
import java.util.UUID

abstract class ChatterReloadableFragment : ChatterFragment() : ReloadableFragment {
    fun setFragmentArgs(intent: Intent, bundle: Bundle) {
        UUID activeAgentID
        val chatterID: ChatterID = null
        if (bundle != null) {
            chatterID = (ChatterID) bundle.getParcelable(ChatterFragment.CHATTER_ID_KEY)
        }
        if (chatterID == null && (activeAgentID = ActivityUtils.getActiveAgentID(intent)) != null) {
            chatterID = ChatterID.getLocalChatterID(activeAgentID)
        }
        if (bundle != null) {
            getArguments().putAll(bundle)
        }
        if (isFragmentStarted()) {
            setNewUser(chatterID)
        }
    }
}
