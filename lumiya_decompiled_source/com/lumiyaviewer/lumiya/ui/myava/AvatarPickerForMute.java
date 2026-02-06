package com.lumiyaviewer.lumiya.ui.myava;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListEntry;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteType;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.avapicker.AvatarPickerFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import java.util.UUID;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class AvatarPickerForMute extends AvatarPickerFragment {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static Bundle makeArguments(UUID uuid) {
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        return bundle;
    }

    @Override // com.lumiyaviewer.lumiya.ui.avapicker.AvatarPickerFragment, com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle, com.lumiyaviewer.lumiya.ui.common.FragmentHasTitle
    public String getTitle() {
        return getString(R.string.select_avatar_to_mute);
    }

    @Override // com.lumiyaviewer.lumiya.ui.avapicker.AvatarPickerFragment
    protected void onAvatarSelected(ChatterID chatterID, @Nullable String str) {
        UserManager userManager = ActivityUtils.getUserManager(getArguments());
        if (userManager != null) {
            SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            if (activeAgentCircuit != null) {
                activeAgentCircuit.getModules().muteList.Block(new MuteListEntry(MuteType.AGENT, chatterID.getOptionalChatterUUID(), str, 15));
            }
            FragmentActivity activity = getActivity();
            if (activity instanceof DetailsActivity) {
                ((DetailsActivity) activity).closeDetailsFragment(this);
            }
        }
    }
}
