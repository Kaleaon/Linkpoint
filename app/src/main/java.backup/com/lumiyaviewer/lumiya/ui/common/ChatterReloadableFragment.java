package com.lumiyaviewer.lumiya.ui.common;

import android.content.Intent;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.UUID;

public abstract class ChatterReloadableFragment extends ChatterFragment implements ReloadableFragment {
    public void setFragmentArgs(Intent intent, Bundle bundle) {
        UUID activeAgentID;
        ChatterID chatterID = null;
        if (bundle != null) {
            chatterID = (ChatterID) bundle.getParcelable(ChatterFragment.CHATTER_ID_KEY);
        }
        if (chatterID == null && (activeAgentID = ActivityUtils.getActiveAgentID(intent)) != null) {
            chatterID = ChatterID.getLocalChatterID(activeAgentID);
        }
        if (bundle != null) {
            getArguments().putAll(bundle);
        }
        if (isFragmentStarted()) {
            setNewUser(chatterID);
        }
    }
}
