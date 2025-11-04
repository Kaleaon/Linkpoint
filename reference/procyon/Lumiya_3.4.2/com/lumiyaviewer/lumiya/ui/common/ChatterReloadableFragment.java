// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.os.Bundle;
import android.content.Intent;

public abstract class ChatterReloadableFragment extends ChatterFragment implements ReloadableFragment
{
    @Override
    public void setFragmentArgs(final Intent intent, final Bundle bundle) {
        ChatterID chatterID = null;
        if (bundle != null) {
            chatterID = (ChatterID)bundle.getParcelable("chatterID");
        }
        ChatterID localChatterID;
        if ((localChatterID = chatterID) == null) {
            final UUID activeAgentID = ActivityUtils.getActiveAgentID(intent);
            localChatterID = chatterID;
            if (activeAgentID != null) {
                localChatterID = ChatterID.getLocalChatterID(activeAgentID);
            }
        }
        if (bundle != null) {
            this.getArguments().putAll(bundle);
        }
        if (this.isFragmentStarted()) {
            this.setNewUser(localChatterID);
        }
    }
}
