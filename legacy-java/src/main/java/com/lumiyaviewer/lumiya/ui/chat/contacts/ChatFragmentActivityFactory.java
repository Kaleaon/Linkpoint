package com.lumiyaviewer.lumiya.ui.chat.contacts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.ui.chat.ChatNewActivity;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity;

/* loaded from: classes.dex */
public class ChatFragmentActivityFactory implements FragmentActivityFactory {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class InstanceHolder {
        private static final ChatFragmentActivityFactory Instance = new ChatFragmentActivityFactory();

        private InstanceHolder() {
        }
    }

    public static ChatFragmentActivityFactory getInstance() {
        return InstanceHolder.Instance;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory
    public Intent createIntent(Context context, Bundle bundle) {
        ChatterID chatterID;
        Intent intent = new Intent(context, (Class<?>) ChatNewActivity.class);
        intent.putExtra(MasterDetailsActivity.INTENT_SELECTION_KEY, bundle);
        if (bundle != null && bundle.containsKey(ChatterFragment.CHATTER_ID_KEY) && (chatterID = (ChatterID) bundle.getParcelable(ChatterFragment.CHATTER_ID_KEY)) != null) {
            ActivityUtils.setActiveAgentID(intent, chatterID.agentUUID);
        }
        return intent;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory
    public Class<? extends Fragment> getFragmentClass() {
        return ChatFragment.class;
    }
}
