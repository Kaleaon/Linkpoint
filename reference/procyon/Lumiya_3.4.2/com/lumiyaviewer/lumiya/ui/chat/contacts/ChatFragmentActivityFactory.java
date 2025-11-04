// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.contacts;

import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.chat.ChatNewActivity;
import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;

public class ChatFragmentActivityFactory implements FragmentActivityFactory
{
    public static ChatFragmentActivityFactory getInstance() {
        return InstanceHolder.Instance;
    }
    
    @Override
    public Intent createIntent(final Context context, final Bundle bundle) {
        final Intent intent = new Intent(context, (Class)ChatNewActivity.class);
        intent.putExtra("selection", bundle);
        if (bundle != null && bundle.containsKey("chatterID")) {
            final ChatterID chatterID = (ChatterID)bundle.getParcelable("chatterID");
            if (chatterID != null) {
                ActivityUtils.setActiveAgentID(intent, chatterID.agentUUID);
            }
        }
        return intent;
    }
    
    @Override
    public Class<? extends Fragment> getFragmentClass() {
        return ChatFragment.class;
    }
    
    private static class InstanceHolder
    {
        private static final ChatFragmentActivityFactory Instance;
        
        static {
            Instance = new ChatFragmentActivityFactory();
        }
    }
}
