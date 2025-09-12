// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ActiveChattersFragment;
import com.lumiyaviewer.lumiya.ui.chat.contacts.FriendListFragment;
import com.lumiyaviewer.lumiya.ui.chat.contacts.GroupListFragment;
import com.lumiyaviewer.lumiya.ui.chat.contacts.NearbyUsersFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat:
//            ContactsFragment

private class this._cls0 extends FragmentStatePagerAdapter
{

    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_chat_2D_ContactsFragment$ContactListTypeSwitchesValues[];
    final int $SWITCH_TABLE$com$lumiyaviewer$lumiya$ui$chat$ContactsFragment$ContactListType[];
    final ContactsFragment this$0;

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_chat_2D_ContactsFragment$ContactListTypeSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_chat_2D_ContactsFragment$ContactListTypeSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_chat_2D_ContactsFragment$ContactListTypeSwitchesValues;
        }
        int ai[] = new int[s().length];
        try
        {
            ai[e.al()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror3) { }
        try
        {
            ai[ds.al()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[s.al()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[y.al()] = 4;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_chat_2D_ContactsFragment$ContactListTypeSwitchesValues = ai;
        return ai;
    }

    public int getCount()
    {
        return s().length;
    }

    public Fragment getItem(int i)
    {
        Object obj = s()[i];
        _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_chat_2D_ContactsFragment$ContactListTypeSwitchesValues()[((itchesValues) (obj)).al()];
        JVM INSTR tableswitch 1 4: default 44
    //                   1 98
    //                   2 109
    //                   3 120
    //                   4 131;
           goto _L1 _L2 _L3 _L4 _L5
_L1:
        obj = null;
_L7:
        if (obj != null)
        {
            Bundle bundle = getArguments();
            Bundle bundle1 = ActivityUtils.makeFragmentArguments(ActivityUtils.getActiveAgentID(bundle), null);
            if (bundle.containsKey("vrMode"))
            {
                bundle1.putBoolean("vrMode", bundle.getBoolean("vrMode"));
            }
            ((Fragment) (obj)).setArguments(bundle1);
        }
        return ((Fragment) (obj));
_L2:
        obj = new ActiveChattersFragment();
        continue; /* Loop/switch isn't completed */
_L3:
        obj = new FriendListFragment();
        continue; /* Loop/switch isn't completed */
_L4:
        obj = new GroupListFragment();
        continue; /* Loop/switch isn't completed */
_L5:
        obj = new NearbyUsersFragment();
        if (true) goto _L7; else goto _L6
_L6:
    }

    public CharSequence getPageTitle(int i)
    {
          = s()[i];
        String s1 = .s();
        String s = s1;
        if ( == y)
        {
            s = s1;
            if (ContactsFragment._2D_get0(ContactsFragment.this) != null)
            {
                i = ContactsFragment._2D_get0(ContactsFragment.this).nearbyUsers();
                s = s1;
                if (i != 0)
                {
                    s = (new StringBuilder()).append(s1).append(" (").append(Integer.toString(i)).append(")").toString();
                }
            }
        }
        return s;
    }

    public Parcelable saveState()
    {
        return null;
    }

    (FragmentManager fragmentmanager)
    {
        this$0 = ContactsFragment.this;
        super(fragmentmanager);
    }
}
