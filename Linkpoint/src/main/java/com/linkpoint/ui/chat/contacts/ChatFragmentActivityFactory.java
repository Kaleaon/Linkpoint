package com.linkpoint.ui.chat.contacts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.linkpoint.slproto.users.ChatterID;
import com.linkpoint.ui.chat.ChatFragment;
import com.linkpoint.ui.chat.ChatNewActivity;
import com.linkpoint.ui.common.ActivityUtils;
import com.linkpoint.ui.common.ChatterFragment;
import com.linkpoint.ui.common.FragmentActivityFactory;
import com.linkpoint.ui.common.MasterDetailsActivity;

public class ChatFragmentActivityFactory implements FragmentActivityFactory {

    private static class InstanceHolder {
        /* access modifiers changed from: private */
        public static final ChatFragmentActivityFactory Instance = new ChatFragmentActivityFactory();

        private InstanceHolder() {
        }
    }

    public static ChatFragmentActivityFactory getInstance() {
        return InstanceHolder.Instance;
    }

    public Intent createIntent(Context context, Bundle bundle) {
        ChatterID chatterID;
        Intent intent = new Intent(context, ChatNewActivity.class);
        intent.putExtra(MasterDetailsActivity.INTENT_SELECTION_KEY, bundle);
        if (!(bundle == null || !bundle.containsKey(ChatterFragment.CHATTER_ID_KEY) || (chatterID = (ChatterID) bundle.getParcelable(ChatterFragment.CHATTER_ID_KEY)) == null)) {
            ActivityUtils.setActiveAgentID(intent, chatterID.agentUUID);
        }
        return intent;
    }

    public Class<? extends Fragment> getFragmentClass() {
        return ChatFragment.class;
    }
}
