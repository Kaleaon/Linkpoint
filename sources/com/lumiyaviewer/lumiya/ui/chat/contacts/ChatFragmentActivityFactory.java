// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.contacts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.ui.chat.ChatNewActivity;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;

public class ChatFragmentActivityFactory
    implements FragmentActivityFactory
{
    private static class InstanceHolder
    {

        private static final ChatFragmentActivityFactory Instance = new ChatFragmentActivityFactory();

        static ChatFragmentActivityFactory _2D_get0()
        {
            return Instance;
        }


        private InstanceHolder()
        {
        }
    }


    public ChatFragmentActivityFactory()
    {
    }

    public static ChatFragmentActivityFactory getInstance()
    {
        return InstanceHolder._2D_get0();
    }

    public Intent createIntent(Context context, Bundle bundle)
    {
        context = new Intent(context, com/lumiyaviewer/lumiya/ui/chat/ChatNewActivity);
        context.putExtra("selection", bundle);
        if (bundle != null && bundle.containsKey("chatterID"))
        {
            bundle = (ChatterID)bundle.getParcelable("chatterID");
            if (bundle != null)
            {
                ActivityUtils.setActiveAgentID(context, ((ChatterID) (bundle)).agentUUID);
            }
        }
        return context;
    }

    public Class getFragmentClass()
    {
        return com/lumiyaviewer/lumiya/ui/chat/ChatFragment;
    }
}
