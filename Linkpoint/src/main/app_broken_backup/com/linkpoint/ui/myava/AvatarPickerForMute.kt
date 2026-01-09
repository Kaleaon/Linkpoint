package com.linkpoint.ui.myava

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.linkpoint.R
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.modules.mutelist.MuteListEntry
import com.linkpoint.slproto.modules.mutelist.MuteType
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.avapicker.AvatarPickerFragment
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.DetailsActivity
import java.util.UUID
import androidx.annotation.Nullable

class AvatarPickerForMute : AvatarPickerFragment {
    fun makeArguments(UUID uuid): Bundle {
        Bundle bundle = Bundle()
        ActivityUtils.setActiveAgentID(bundle, uuid)
        return bundle
    }

    fun getTitle(): String {
        return getString(R.string.select_avatar_to_mute)
    }

    /* access modifiers changed from: protected */
    fun onAvatarSelected(ChatterID chatterID, @Nullable String str): Unit {
        UserManager userManager = ActivityUtils.getUserManager(getArguments())
        if (userManager != null) {
            SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit()
            if (activeAgentCircuit != null) {
                activeAgentCircuit.getModules().muteList.Block(MuteListEntry(MuteType.AGENT, chatterID.getOptionalChatterUUID(), str, 15))
            }
            FragmentActivity activity = getActivity()
            if (activity instanceof DetailsActivity) {
                ((DetailsActivity) activity).closeDetailsFragment(this)
            }
        }
    }
}
