// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.astuetz.PagerSlidingTabStrip;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.StreamingMediaService;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ActiveChattersFragment;
import com.lumiyaviewer.lumiya.ui.chat.contacts.FriendListFragment;
import com.lumiyaviewer.lumiya.ui.chat.contacts.GroupListFragment;
import com.lumiyaviewer.lumiya.ui.chat.contacts.NearbyUsersFragment;
import com.lumiyaviewer.lumiya.ui.chat.profiles.ParcelPropertiesFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;

public class ContactsFragment extends Fragment
{
    private static final class ContactListType extends Enum
    {

        private static final ContactListType $VALUES[];
        public static final ContactListType Active;
        public static final ContactListType Friends;
        public static final ContactListType Groups;
        public static final ContactListType Nearby;

        public static ContactListType valueOf(String s)
        {
            return (ContactListType)Enum.valueOf(com/lumiyaviewer/lumiya/ui/chat/ContactsFragment$ContactListType, s);
        }

        public static ContactListType[] values()
        {
            return $VALUES;
        }

        static 
        {
            Active = new ContactListType("Active", 0);
            Friends = new ContactListType("Friends", 1);
            Groups = new ContactListType("Groups", 2);
            Nearby = new ContactListType("Nearby", 3);
            $VALUES = (new ContactListType[] {
                Active, Friends, Groups, Nearby
            });
        }

        private ContactListType(String s, int i)
        {
            super(s, i);
        }
    }

    private class ContactsPagerAdapter extends FragmentStatePagerAdapter
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
            int ai[] = new int[ContactListType.values().length];
            try
            {
                ai[ContactListType.Active.ordinal()] = 1;
            }
            catch (NoSuchFieldError nosuchfielderror3) { }
            try
            {
                ai[ContactListType.Friends.ordinal()] = 2;
            }
            catch (NoSuchFieldError nosuchfielderror2) { }
            try
            {
                ai[ContactListType.Groups.ordinal()] = 3;
            }
            catch (NoSuchFieldError nosuchfielderror1) { }
            try
            {
                ai[ContactListType.Nearby.ordinal()] = 4;
            }
            catch (NoSuchFieldError nosuchfielderror) { }
            _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_chat_2D_ContactsFragment$ContactListTypeSwitchesValues = ai;
            return ai;
        }

        public int getCount()
        {
            return ContactListType.values().length;
        }

        public Fragment getItem(int i)
        {
            Object obj = ContactListType.values()[i];
            _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_chat_2D_ContactsFragment$ContactListTypeSwitchesValues()[((ContactListType) (obj)).ordinal()];
            JVM INSTR tableswitch 1 4: default 44
        //                       1 98
        //                       2 109
        //                       3 120
        //                       4 131;
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
            ContactListType contactlisttype = ContactListType.values()[i];
            String s1 = contactlisttype.name();
            String s = s1;
            if (contactlisttype == ContactListType.Nearby)
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

        ContactsPagerAdapter(FragmentManager fragmentmanager)
        {
            this$0 = ContactsFragment.this;
            super(fragmentmanager);
        }
    }


    private CurrentLocationInfo currentLocationInfo;
    private MenuItem itemLocationDetails;
    private MenuItem itemPlayMedia;
    private Subscription subscription;

    static CurrentLocationInfo _2D_get0(ContactsFragment contactsfragment)
    {
        return contactsfragment.currentLocationInfo;
    }

    public ContactsFragment()
    {
        itemLocationDetails = null;
        itemPlayMedia = null;
    }

    public static ContactsFragment newInstance(Bundle bundle)
    {
        ContactsFragment contactsfragment = new ContactsFragment();
        contactsfragment.setArguments(bundle);
        return contactsfragment;
    }

    private void onCurrentLocation(CurrentLocationInfo currentlocationinfo)
    {
        currentLocationInfo = currentlocationinfo;
        currentlocationinfo = getView();
        if (currentlocationinfo != null)
        {
            currentlocationinfo = (ViewPager)currentlocationinfo.findViewById(0x7f100149);
            if (currentlocationinfo != null)
            {
                currentlocationinfo = currentlocationinfo.getAdapter();
                if (currentlocationinfo != null)
                {
                    currentlocationinfo.notifyDataSetChanged();
                }
            }
        }
        updateOptionsMenu();
    }

    private void updateOptionsMenu()
    {
        if (currentLocationInfo == null) goto _L2; else goto _L1
_L1:
        Object obj = getActivity();
        if (!(obj instanceof DetailsActivity)) goto _L2; else goto _L3
_L3:
        boolean flag;
        obj = ((DetailsActivity)obj).getCurrentDetailsFragment();
        if (obj == null || obj == this)
        {
            flag = true;
        } else
        {
            flag = false;
        }
_L5:
label0:
        {
            {
                if (!flag)
                {
                    break label0;
                }
                if (itemLocationDetails != null)
                {
                    itemLocationDetails.setVisible(true);
                }
                obj = currentLocationInfo.parcelData();
                boolean flag1;
                if (obj != null)
                {
                    flag1 = Strings.isNullOrEmpty(((ParcelData) (obj)).getMediaURL()) ^ true;
                } else
                {
                    flag1 = false;
                }
                if (itemPlayMedia != null)
                {
                    itemPlayMedia.setVisible(flag1);
                }
            }
            return;
        }
        if (itemLocationDetails != null)
        {
            itemLocationDetails.setVisible(false);
        }
        if (itemPlayMedia != null)
        {
            itemPlayMedia.setVisible(false);
            return;
        } else
        {
            break MISSING_BLOCK_LABEL_100;
        }
_L2:
        flag = false;
        if (true) goto _L5; else goto _L4
_L4:
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_ContactsFragment_2D_mthref_2D_0(CurrentLocationInfo currentlocationinfo)
    {
        onCurrentLocation(currentlocationinfo);
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater)
    {
        super.onCreateOptionsMenu(menu, menuinflater);
        menuinflater.inflate(0x7f120003, menu);
        itemLocationDetails = menu.findItem(0x7f1002fc);
        itemPlayMedia = menu.findItem(0x7f1002fd);
        updateOptionsMenu();
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        super.onCreateView(layoutinflater, viewgroup, bundle);
        layoutinflater = layoutinflater.inflate(0x7f04002d, viewgroup, false);
        viewgroup = (ViewPager)layoutinflater.findViewById(0x7f100149);
        viewgroup.setAdapter(new ContactsPagerAdapter(getChildFragmentManager()));
        ((PagerSlidingTabStrip)layoutinflater.findViewById(0x7f100148)).setViewPager(viewgroup);
        return layoutinflater;
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        UserManager usermanager = ActivityUtils.getUserManager(getArguments());
        switch (menuitem.getItemId())
        {
        default:
            return super.onOptionsItemSelected(menuitem);

        case 2131755772: 
            if (usermanager != null)
            {
                menuitem = usermanager.getCurrentLocationInfoSnapshot();
                if (menuitem != null)
                {
                    menuitem = menuitem.parcelData();
                    if (menuitem != null)
                    {
                        DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/ParcelPropertiesFragment, ParcelPropertiesFragment.makeSelection(usermanager.getUserID(), menuitem));
                    }
                }
            }
            return true;

        case 2131755773: 
            StreamingMediaService.startStreamingMediaService(getContext(), usermanager);
            return true;
        }
    }

    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        updateOptionsMenu();
    }

    public void onStart()
    {
        super.onStart();
        UserManager usermanager = ActivityUtils.getUserManager(getArguments());
        if (usermanager != null)
        {
            subscription = usermanager.getCurrentLocationInfo().subscribe(SubscriptionSingleDataPool.getSingleDataKey(), UIThreadExecutor.getInstance(), new _2D_.Lambda.zIl8cGSTO94X3h9h2afeKA4NC_s(this));
        }
    }

    public void onStop()
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
            subscription = null;
        }
        super.onStop();
    }
}
