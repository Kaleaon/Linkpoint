// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat;

import android.os.Parcelable;
import com.lumiyaviewer.lumiya.ui.chat.contacts.NearbyUsersFragment;
import com.lumiyaviewer.lumiya.ui.chat.contacts.GroupListFragment;
import com.lumiyaviewer.lumiya.ui.chat.contacts.FriendListFragment;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ActiveChattersFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.StreamingMediaService;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.chat.profiles.ParcelPropertiesFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.astuetz.PagerSlidingTabStrip;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.Menu;
import android.support.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import android.support.v4.app.FragmentActivity;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.react.Subscription;
import android.view.MenuItem;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import android.support.v4.app.Fragment;

public class ContactsFragment extends Fragment
{
    private CurrentLocationInfo currentLocationInfo;
    private MenuItem itemLocationDetails;
    private MenuItem itemPlayMedia;
    private Subscription subscription;
    
    public ContactsFragment() {
        this.itemLocationDetails = null;
        this.itemPlayMedia = null;
    }
    
    public static ContactsFragment newInstance(final Bundle arguments) {
        final ContactsFragment contactsFragment = new ContactsFragment();
        contactsFragment.setArguments(arguments);
        return contactsFragment;
    }
    
    private void onCurrentLocation(final CurrentLocationInfo currentLocationInfo) {
        this.currentLocationInfo = currentLocationInfo;
        final View view = this.getView();
        if (view != null) {
            final ViewPager viewPager = (ViewPager)view.findViewById(2131755337);
            if (viewPager != null) {
                final PagerAdapter adapter = viewPager.getAdapter();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        }
        this.updateOptionsMenu();
    }
    
    private void updateOptionsMenu() {
        while (true) {
            Label_0150: {
                if (this.currentLocationInfo == null) {
                    break Label_0150;
                }
                final FragmentActivity activity = this.getActivity();
                if (!(activity instanceof DetailsActivity)) {
                    break Label_0150;
                }
                final Fragment currentDetailsFragment = ((DetailsActivity)activity).getCurrentDetailsFragment();
                int n;
                if (currentDetailsFragment == null || currentDetailsFragment == this) {
                    n = 1;
                }
                else {
                    n = 0;
                }
                if (n != 0) {
                    if (this.itemLocationDetails != null) {
                        this.itemLocationDetails.setVisible(true);
                    }
                    final ParcelData parcelData = this.currentLocationInfo.parcelData();
                    final boolean visible = parcelData != null && (Strings.isNullOrEmpty(parcelData.getMediaURL()) ^ true);
                    if (this.itemPlayMedia != null) {
                        this.itemPlayMedia.setVisible(visible);
                    }
                }
                else {
                    if (this.itemLocationDetails != null) {
                        this.itemLocationDetails.setVisible(false);
                    }
                    if (this.itemPlayMedia != null) {
                        this.itemPlayMedia.setVisible(false);
                    }
                }
                return;
            }
            int n = 0;
            continue;
        }
    }
    
    @Override
    public void onCreate(@Nullable final Bundle bundle) {
        super.onCreate(bundle);
        this.setHasOptionsMenu(true);
    }
    
    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(2131886083, menu);
        this.itemLocationDetails = menu.findItem(2131755772);
        this.itemPlayMedia = menu.findItem(2131755773);
        this.updateOptionsMenu();
    }
    
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        final View inflate = layoutInflater.inflate(2130968621, viewGroup, false);
        final ViewPager viewPager = (ViewPager)inflate.findViewById(2131755337);
        viewPager.setAdapter(new ContactsPagerAdapter(this.getChildFragmentManager()));
        ((PagerSlidingTabStrip)inflate.findViewById(2131755336)).setViewPager(viewPager);
        return inflate;
    }
    
    @Override
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        switch (menuItem.getItemId()) {
            default: {
                return super.onOptionsItemSelected(menuItem);
            }
            case 2131755772: {
                if (userManager != null) {
                    final CurrentLocationInfo currentLocationInfoSnapshot = userManager.getCurrentLocationInfoSnapshot();
                    if (currentLocationInfoSnapshot != null) {
                        final ParcelData parcelData = currentLocationInfoSnapshot.parcelData();
                        if (parcelData != null) {
                            DetailsActivity.showEmbeddedDetails(this.getActivity(), ParcelPropertiesFragment.class, ParcelPropertiesFragment.makeSelection(userManager.getUserID(), parcelData));
                        }
                    }
                }
                return true;
            }
            case 2131755773: {
                StreamingMediaService.startStreamingMediaService(this.getContext(), userManager);
                return true;
            }
        }
    }
    
    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        this.updateOptionsMenu();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        if (userManager != null) {
            this.subscription = userManager.getCurrentLocationInfo().subscribe(SubscriptionSingleDataPool.getSingleDataKey(), UIThreadExecutor.getInstance(), new _$Lambda$zIl8cGSTO94X3h9h2afeKA4NC_s(this));
        }
    }
    
    @Override
    public void onStop() {
        if (this.subscription != null) {
            this.subscription.unsubscribe();
            this.subscription = null;
        }
        super.onStop();
    }
    
    private enum ContactListType
    {
        Active("Active", 0), 
        Friends("Friends", 1), 
        Groups("Groups", 2), 
        Nearby("Nearby", 3);
        
        private ContactListType(final String name, final int ordinal) {
        }
    }
    
    private class ContactsPagerAdapter extends FragmentStatePagerAdapter
    {
        private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-ui-chat-ContactsFragment$ContactListTypeSwitchesValues() {
            if (ContactsPagerAdapter.-com-lumiyaviewer-lumiya-ui-chat-ContactsFragment$ContactListTypeSwitchesValues != null) {
                return ContactsPagerAdapter.-com-lumiyaviewer-lumiya-ui-chat-ContactsFragment$ContactListTypeSwitchesValues;
            }
            int[] -com-lumiyaviewer-lumiya-ui-chat-ContactsFragment$ContactListTypeSwitchesValues = new int[ContactListType.values().length];
            while (true) {
                try {
                    -com-lumiyaviewer-lumiya-ui-chat-ContactsFragment$ContactListTypeSwitchesValues[ContactListType.Active.ordinal()] = 1;
                    try {
                        -com-lumiyaviewer-lumiya-ui-chat-ContactsFragment$ContactListTypeSwitchesValues[ContactListType.Friends.ordinal()] = 2;
                        try {
                            -com-lumiyaviewer-lumiya-ui-chat-ContactsFragment$ContactListTypeSwitchesValues[ContactListType.Groups.ordinal()] = 3;
                            try {
                                -com-lumiyaviewer-lumiya-ui-chat-ContactsFragment$ContactListTypeSwitchesValues[ContactListType.Nearby.ordinal()] = 4;
                                return -com-lumiyaviewer-lumiya-ui-chat-ContactsFragment$ContactListTypeSwitchesValues = -com-lumiyaviewer-lumiya-ui-chat-ContactsFragment$ContactListTypeSwitchesValues;
                            }
                            catch (final NoSuchFieldError noSuchFieldError) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError2) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError3) {}
                }
                catch (final NoSuchFieldError noSuchFieldError4) {
                    continue;
                }
                break;
            }
        }
        
        ContactsPagerAdapter(final FragmentManager fragmentManager) {
            super(fragmentManager);
        }
        
        @Override
        public int getCount() {
            return ContactListType.values().length;
        }
        
        @Override
        public Fragment getItem(final int n) {
            Fragment fragment = null;
            switch (-getcom-lumiyaviewer-lumiya-ui-chat-ContactsFragment$ContactListTypeSwitchesValues()[ContactListType.values()[n].ordinal()]) {
                default: {
                    fragment = null;
                    break;
                }
                case 1: {
                    fragment = new ActiveChattersFragment();
                    break;
                }
                case 2: {
                    fragment = new FriendListFragment();
                    break;
                }
                case 3: {
                    fragment = new GroupListFragment();
                    break;
                }
                case 4: {
                    fragment = new NearbyUsersFragment();
                    break;
                }
            }
            if (fragment != null) {
                final Bundle arguments = ContactsFragment.this.getArguments();
                final Bundle fragmentArguments = ActivityUtils.makeFragmentArguments(ActivityUtils.getActiveAgentID(arguments), null);
                if (arguments.containsKey("vrMode")) {
                    fragmentArguments.putBoolean("vrMode", arguments.getBoolean("vrMode"));
                }
                fragment.setArguments(fragmentArguments);
            }
            return fragment;
        }
        
        @Override
        public CharSequence getPageTitle(int nearbyUsers) {
            final ContactListType contactListType = ContactListType.values()[nearbyUsers];
            String s;
            final String str = s = contactListType.name();
            if (contactListType == ContactListType.Nearby) {
                s = str;
                if (ContactsFragment.this.currentLocationInfo != null) {
                    nearbyUsers = ContactsFragment.this.currentLocationInfo.nearbyUsers();
                    s = str;
                    if (nearbyUsers != 0) {
                        s = str + " (" + Integer.toString(nearbyUsers) + ")";
                    }
                }
            }
            return s;
        }
        
        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}
