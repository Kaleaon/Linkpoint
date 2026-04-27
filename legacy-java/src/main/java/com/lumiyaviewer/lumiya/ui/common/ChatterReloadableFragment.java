package com.lumiyaviewer.lumiya.ui.common;

import android.content.Intent;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.UUID;

/* loaded from: classes.dex */
public abstract class ChatterReloadableFragment extends ChatterFragment implements ReloadableFragment {
    @Override // com.lumiyaviewer.lumiya.ui.common.ReloadableFragment
    public void setFragmentArgs(Intent intent, Bundle bundle) {
        UUID activeAgentID;
        ChatterID chatterID = bundle != null ? (ChatterID) bundle.getParcelable(ChatterFragment.CHATTER_ID_KEY) : null;
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
