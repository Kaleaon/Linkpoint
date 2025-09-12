// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.ui.chat.profiles.GroupProfileFragment;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.search:
//            ParcelInfoFragment, SearchGridFragment

public class SearchGridActivity extends MasterDetailsActivity
{

    public SearchGridActivity()
    {
    }

    protected FragmentActivityFactory getDetailsFragmentFactory()
    {
        return null;
    }

    protected boolean isRootDetailsFragment(Class class1)
    {
        while (class1 == com/lumiyaviewer/lumiya/ui/chat/profiles/UserProfileFragment || class1 == com/lumiyaviewer/lumiya/ui/chat/profiles/GroupProfileFragment || class1 == com/lumiyaviewer/lumiya/ui/search/ParcelInfoFragment) 
        {
            return true;
        }
        return false;
    }

    protected Fragment onCreateMasterFragment(Intent intent, Bundle bundle)
    {
        return SearchGridFragment.newInstance(ActivityUtils.getActiveAgentID(intent));
    }
}
