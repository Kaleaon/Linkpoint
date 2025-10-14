package com.lumiyaviewer.lumiya.ui.chat.contacts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment
import com.lumiyaviewer.lumiya.ui.chat.ChatNewActivity
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity

class ChatFragmentActivityFactory : FragmentActivityFactory {

    private class InstanceHolder {
        /* access modifiers changed from: private */
        ChatFragmentActivityFactory Instance = ChatFragmentActivityFactory()

        private InstanceHolder() {
        }
    }

    ChatFragmentActivityFactory getInstance() {
        return InstanceHolder.Instance
    }

    Intent createIntent(Context context, Bundle bundle) {
        ChatterID chatterID
        Intent intent = Intent(context, ChatNewActivity.class)
        intent.putExtra(MasterDetailsActivity.INTENT_SELECTION_KEY, bundle)
        if (!(bundle == null || !bundle.containsKey(ChatterFragment.CHATTER_ID_KEY) || (chatterID = (ChatterID) bundle.getParcelable(ChatterFragment.CHATTER_ID_KEY)) == null)) {
            ActivityUtils.setActiveAgentID(intent, chatterID.agentUUID)
        }
        return intent
    }

    Class<? : Fragment> getFragmentClass() {
        return ChatFragment.class
    }
}
