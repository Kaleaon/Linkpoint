package com.lumiyaviewer.lumiya.ui.chat;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
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
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.StreamingMediaService;
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
import com.lumiyaviewer.lumiya.ui.render.CardboardActivity;

public class ContactsFragment extends Fragment {
    /* access modifiers changed from: private */
    public CurrentLocationInfo currentLocationInfo;
    private MenuItem itemLocationDetails = null;
    private MenuItem itemPlayMedia = null;
    private Subscription subscription;

    private enum ContactListType {
        Active,
        Friends,
        Groups,
        Nearby
    }

    private class ContactsPagerAdapter extends FragmentStatePagerAdapter {

        /* renamed from: -com-lumiyaviewer-lumiya-ui-chat-ContactsFragment$ContactListTypeSwitchesValues  reason: not valid java name */
        private static final /* synthetic */ int[] f251comlumiyaviewerlumiyauichatContactsFragment$ContactListTypeSwitchesValues = null;
        final /* synthetic */ int[] $SWITCH_TABLE$com$lumiyaviewer$lumiya$ui$chat$ContactsFragment$ContactListType;

        /* renamed from: -getcom-lumiyaviewer-lumiya-ui-chat-ContactsFragment$ContactListTypeSwitchesValues  reason: not valid java name */
        private static /* synthetic */ int[] m420getcomlumiyaviewerlumiyauichatContactsFragment$ContactListTypeSwitchesValues() {
            if (f251comlumiyaviewerlumiyauichatContactsFragment$ContactListTypeSwitchesValues != null) {
                return f251comlumiyaviewerlumiyauichatContactsFragment$ContactListTypeSwitchesValues;
            }
            int[] iArr = new int[ContactListType.values().length];
            try {
                iArr[ContactListType.Active.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[ContactListType.Friends.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[ContactListType.Groups.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[ContactListType.Nearby.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            f251comlumiyaviewerlumiyauichatContactsFragment$ContactListTypeSwitchesValues = iArr;
            return iArr;
        }

        ContactsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public int getCount() {
            return ContactListType.values().length;
        }

        public Fragment getItem(int i) {
            Fragment nearbyUsersFragment;
            switch (m420getcomlumiyaviewerlumiyauichatContactsFragment$ContactListTypeSwitchesValues()[ContactListType.values()[i].ordinal()]) {
                case 1:
                    nearbyUsersFragment = new ActiveChattersFragment();
                    break;
                case 2:
                    nearbyUsersFragment = new FriendListFragment();
                    break;
                case 3:
                    nearbyUsersFragment = new GroupListFragment();
                    break;
                case 4:
                    nearbyUsersFragment = new NearbyUsersFragment();
                    break;
                default:
                    nearbyUsersFragment = null;
                    break;
            }
            if (nearbyUsersFragment != null) {
                Bundle arguments = ContactsFragment.this.getArguments();
                Bundle makeFragmentArguments = ActivityUtils.makeFragmentArguments(ActivityUtils.getActiveAgentID(arguments), (Bundle) null);
                if (arguments.containsKey(CardboardActivity.VR_MODE_TAG)) {
                    makeFragmentArguments.putBoolean(CardboardActivity.VR_MODE_TAG, arguments.getBoolean(CardboardActivity.VR_MODE_TAG));
                }
                nearbyUsersFragment.setArguments(makeFragmentArguments);
            }
            return nearbyUsersFragment;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:4:0x0016, code lost:
            r1 = com.lumiyaviewer.lumiya.ui.chat.ContactsFragment.m418get0(r3.this$0).nearbyUsers();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.CharSequence getPageTitle(int r4) {
            /*
                r3 = this;
                com.lumiyaviewer.lumiya.ui.chat.ContactsFragment$ContactListType[] r0 = com.lumiyaviewer.lumiya.ui.chat.ContactsFragment.ContactListType.values()
                r1 = r0[r4]
                java.lang.String r0 = r1.name()
                com.lumiyaviewer.lumiya.ui.chat.ContactsFragment$ContactListType r2 = com.lumiyaviewer.lumiya.ui.chat.ContactsFragment.ContactListType.Nearby
                if (r1 != r2) goto L_0x0045
                com.lumiyaviewer.lumiya.ui.chat.ContactsFragment r1 = com.lumiyaviewer.lumiya.ui.chat.ContactsFragment.this
                com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo r1 = r1.currentLocationInfo
                if (r1 == 0) goto L_0x0045
                com.lumiyaviewer.lumiya.ui.chat.ContactsFragment r1 = com.lumiyaviewer.lumiya.ui.chat.ContactsFragment.this
                com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo r1 = r1.currentLocationInfo
                int r1 = r1.nearbyUsers()
                if (r1 == 0) goto L_0x0045
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.StringBuilder r0 = r2.append(r0)
                java.lang.String r2 = " ("
                java.lang.StringBuilder r0 = r0.append(r2)
                java.lang.String r1 = java.lang.Integer.toString(r1)
                java.lang.StringBuilder r0 = r0.append(r1)
                java.lang.String r1 = ")"
                java.lang.StringBuilder r0 = r0.append(r1)
                java.lang.String r0 = r0.toString()
            L_0x0045:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.ui.chat.ContactsFragment.ContactsPagerAdapter.getPageTitle(int):java.lang.CharSequence");
        }

        public Parcelable saveState() {
            return null;
        }
    }

    public static ContactsFragment newInstance(Bundle bundle) {
        ContactsFragment contactsFragment = new ContactsFragment();
        contactsFragment.setArguments(bundle);
        return contactsFragment;
    }

    /* access modifiers changed from: private */
    /* renamed from: onCurrentLocation */
    public void m419com_lumiyaviewer_lumiya_ui_chat_ContactsFragmentmthref0(CurrentLocationInfo currentLocationInfo2) {
        ViewPager viewPager;
        PagerAdapter adapter;
        this.currentLocationInfo = currentLocationInfo2;
        View view = getView();
        if (!(view == null || (viewPager = (ViewPager) view.findViewById(R.id.contact_list_pager)) == null || (adapter = viewPager.getAdapter()) == null)) {
            adapter.notifyDataSetChanged();
        }
        updateOptionsMenu();
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0042  */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x001b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateOptionsMenu() {
        /*
            r4 = this;
            r2 = 1
            r1 = 0
            com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo r0 = r4.currentLocationInfo
            if (r0 == 0) goto L_0x0057
            android.support.v4.app.FragmentActivity r0 = r4.getActivity()
            boolean r3 = r0 instanceof com.lumiyaviewer.lumiya.ui.common.DetailsActivity
            if (r3 == 0) goto L_0x0057
            com.lumiyaviewer.lumiya.ui.common.DetailsActivity r0 = (com.lumiyaviewer.lumiya.ui.common.DetailsActivity) r0
            android.support.v4.app.Fragment r0 = r0.getCurrentDetailsFragment()
            if (r0 == 0) goto L_0x0018
            if (r0 != r4) goto L_0x0040
        L_0x0018:
            r0 = r2
        L_0x0019:
            if (r0 == 0) goto L_0x0042
            android.view.MenuItem r0 = r4.itemLocationDetails
            if (r0 == 0) goto L_0x0024
            android.view.MenuItem r0 = r4.itemLocationDetails
            r0.setVisible(r2)
        L_0x0024:
            com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo r0 = r4.currentLocationInfo
            com.lumiyaviewer.lumiya.slproto.users.ParcelData r0 = r0.parcelData()
            if (r0 == 0) goto L_0x0055
            java.lang.String r0 = r0.getMediaURL()
            boolean r0 = com.google.common.base.Strings.isNullOrEmpty(r0)
            r0 = r0 ^ 1
        L_0x0036:
            android.view.MenuItem r1 = r4.itemPlayMedia
            if (r1 == 0) goto L_0x003f
            android.view.MenuItem r1 = r4.itemPlayMedia
            r1.setVisible(r0)
        L_0x003f:
            return
        L_0x0040:
            r0 = r1
            goto L_0x0019
        L_0x0042:
            android.view.MenuItem r0 = r4.itemLocationDetails
            if (r0 == 0) goto L_0x004b
            android.view.MenuItem r0 = r4.itemLocationDetails
            r0.setVisible(r1)
        L_0x004b:
            android.view.MenuItem r0 = r4.itemPlayMedia
            if (r0 == 0) goto L_0x003f
            android.view.MenuItem r0 = r4.itemPlayMedia
            r0.setVisible(r1)
            goto L_0x003f
        L_0x0055:
            r0 = r1
            goto L_0x0036
        L_0x0057:
            r0 = r1
            goto L_0x0019
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.ui.chat.ContactsFragment.updateOptionsMenu():void");
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.contact_fragment_menu, menu);
        this.itemLocationDetails = menu.findItem(R.id.item_contacts_location_details);
        this.itemPlayMedia = menu.findItem(R.id.item_contacts_play_parcel_media);
        updateOptionsMenu();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.contacts, viewGroup, false);
        ViewPager viewPager = (ViewPager) inflate.findViewById(R.id.contact_list_pager);
        viewPager.setAdapter(new ContactsPagerAdapter(getChildFragmentManager()));
        ((PagerSlidingTabStrip) inflate.findViewById(R.id.contact_list_tabs)).setViewPager(viewPager);
        return inflate;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        CurrentLocationInfo currentLocationInfoSnapshot;
        ParcelData parcelData;
        UserManager userManager = ActivityUtils.getUserManager(getArguments());
        switch (menuItem.getItemId()) {
            case R.id.item_contacts_location_details:
                if (!(userManager == null || (currentLocationInfoSnapshot = userManager.getCurrentLocationInfoSnapshot()) == null || (parcelData = currentLocationInfoSnapshot.parcelData()) == null)) {
                    DetailsActivity.showEmbeddedDetails(getActivity(), ParcelPropertiesFragment.class, ParcelPropertiesFragment.makeSelection(userManager.getUserID(), parcelData));
                }
                return true;
            case R.id.item_contacts_play_parcel_media:
                StreamingMediaService.startStreamingMediaService(getContext(), userManager);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        updateOptionsMenu();
    }

    public void onStart() {
        super.onStart();
        UserManager userManager = ActivityUtils.getUserManager(getArguments());
        if (userManager != null) {
            this.subscription = userManager.getCurrentLocationInfo().subscribe(SubscriptionSingleDataPool.getSingleDataKey(), UIThreadExecutor.getInstance(), new $Lambda$zIl8cGSTO94X3h9h2afeKA4NC_s(this));
        }
    }

    public void onStop() {
        if (this.subscription != null) {
            this.subscription.unsubscribe();
            this.subscription = null;
        }
        super.onStop();
    }
}
