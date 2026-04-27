// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.myava;

import android.support.v4.app.FragmentActivity;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListEntry;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteType;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import android.os.Bundle;
import java.util.UUID;
import com.lumiyaviewer.lumiya.ui.avapicker.AvatarPickerFragment;

public class AvatarPickerForMute extends AvatarPickerFragment
{
    static Bundle makeArguments(final UUID uuid) {
        final Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        return bundle;
    }
    
    @Override
    public String getTitle() {
        return this.getString(2131297008);
    }
    
    @Override
    protected void onAvatarSelected(final ChatterID chatterID, @Nullable final String s) {
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        if (userManager != null) {
            final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            if (activeAgentCircuit != null) {
                activeAgentCircuit.getModules().muteList.Block(new MuteListEntry(MuteType.AGENT, chatterID.getOptionalChatterUUID(), s, 15));
            }
            final FragmentActivity activity = this.getActivity();
            if (activity instanceof DetailsActivity) {
                ((DetailsActivity)activity).closeDetailsFragment(this);
            }
        }
    }
}
