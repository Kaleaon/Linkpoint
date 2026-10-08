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
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
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
/* loaded from: classes.dex */
public class ContactsFragment extends Fragment {
    private CurrentLocationInfo currentLocationInfo;
    private MenuItem itemLocationDetails = null;
    private MenuItem itemPlayMedia = null;
    private Subscription subscription;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum ContactListType {
        Active,
        Friends,
        Groups,
        Nearby;

        /* renamed from: values  reason: to resolve conflict with enum method */
        public static ContactListType[] valuesCustom() {
            return values();
        }
    }

    /* loaded from: classes.dex */
    private class ContactsPagerAdapter extends FragmentStatePagerAdapter {

        /* renamed from: -com-lumiyaviewer-lumiya-ui-chat-ContactsFragment$ContactListTypeSwitchesValues  reason: not valid java name */
        private static final /* synthetic */ int[] f251x6907e542 = null;
        final /* synthetic */ int[] $SWITCH_TABLE$com$lumiyaviewer$lumiya$ui$chat$ContactsFragment$ContactListType;

        /* renamed from: -getcom-lumiyaviewer-lumiya-ui-chat-ContactsFragment$ContactListTypeSwitchesValues  reason: not valid java name */
        private static /* synthetic */ int[] m431x7c67181e() {
            if (f251x6907e542 != null) {
                return f251x6907e542;
            }
            int[] iArr = new int[ContactListType.valuesCustom().length];
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
            f251x6907e542 = iArr;
            return iArr;
        }

        ContactsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override // android.support.v4.view.PagerAdapter
        public int getCount() {
            return ContactListType.valuesCustom().length;
        }

        @Override // android.support.v4.app.FragmentStatePagerAdapter
        public Fragment getItem(int i) {
            Fragment nearbyUsersFragment;
            switch (m431x7c67181e()[ContactListType.valuesCustom()[i].ordinal()]) {
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
                Bundle makeFragmentArguments = ActivityUtils.makeFragmentArguments(ActivityUtils.getActiveAgentID(arguments), null);
                if (arguments.containsKey(CardboardActivity.VR_MODE_TAG)) {
                    makeFragmentArguments.putBoolean(CardboardActivity.VR_MODE_TAG, arguments.getBoolean(CardboardActivity.VR_MODE_TAG));
                }
                nearbyUsersFragment.setArguments(makeFragmentArguments);
            }
            return nearbyUsersFragment;
        }

        @Override // android.support.v4.view.PagerAdapter
        public CharSequence getPageTitle(int i) {
            int nearbyUsers;
            ContactListType contactListType = ContactListType.valuesCustom()[i];
            String name = contactListType.name();
            return (contactListType != ContactListType.Nearby || ContactsFragment.this.currentLocationInfo == null || (nearbyUsers = ContactsFragment.this.currentLocationInfo.nearbyUsers()) == 0) ? name : name + " (" + Integer.toString(nearbyUsers) + ")";
        }

        @Override // android.support.v4.app.FragmentStatePagerAdapter, android.support.v4.view.PagerAdapter
        public Parcelable saveState() {
            return null;
        }
    }

    public static ContactsFragment newInstance(Bundle bundle) {
        ContactsFragment contactsFragment = new ContactsFragment();
        contactsFragment.setArguments(bundle);
        return contactsFragment;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onCurrentLocation */
    public void m430com_lumiyaviewer_lumiya_ui_chat_ContactsFragmentmthref0(CurrentLocationInfo currentLocationInfo) {
        ViewPager viewPager;
        PagerAdapter adapter;
        this.currentLocationInfo = currentLocationInfo;
        View view = getView();
        if (view != null && (viewPager = (ViewPager) view.findViewById(R.id.contact_list_pager)) != null && (adapter = viewPager.getAdapter()) != null) {
            adapter.notifyDataSetChanged();
        }
        updateOptionsMenu();
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x001b  */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0042  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateOptionsMenu() {
        /*
            r4 = this;
            r2 = 1
            r1 = 0
            com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo r0 = r4.currentLocationInfo
            if (r0 == 0) goto L57
            android.support.v4.app.FragmentActivity r0 = r4.getActivity()
            boolean r3 = r0 instanceof com.lumiyaviewer.lumiya.ui.common.DetailsActivity
            if (r3 == 0) goto L57
            com.lumiyaviewer.lumiya.ui.common.DetailsActivity r0 = (com.lumiyaviewer.lumiya.ui.common.DetailsActivity) r0
            android.support.v4.app.Fragment r0 = r0.getCurrentDetailsFragment()
            if (r0 == 0) goto L18
            if (r0 != r4) goto L40
        L18:
            r0 = r2
        L19:
            if (r0 == 0) goto L42
            android.view.MenuItem r0 = r4.itemLocationDetails
            if (r0 == 0) goto L24
            android.view.MenuItem r0 = r4.itemLocationDetails
            r0.setVisible(r2)
        L24:
            com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo r0 = r4.currentLocationInfo
            com.lumiyaviewer.lumiya.slproto.users.ParcelData r0 = r0.parcelData()
            if (r0 == 0) goto L55
            java.lang.String r0 = r0.getMediaURL()
            boolean r0 = com.google.common.base.Strings.isNullOrEmpty(r0)
            r0 = r0 ^ 1
        L36:
            android.view.MenuItem r1 = r4.itemPlayMedia
            if (r1 == 0) goto L3f
            android.view.MenuItem r1 = r4.itemPlayMedia
            r1.setVisible(r0)
        L3f:
            return
        L40:
            r0 = r1
            goto L19
        L42:
            android.view.MenuItem r0 = r4.itemLocationDetails
            if (r0 == 0) goto L4b
            android.view.MenuItem r0 = r4.itemLocationDetails
            r0.setVisible(r1)
        L4b:
            android.view.MenuItem r0 = r4.itemPlayMedia
            if (r0 == 0) goto L3f
            android.view.MenuItem r0 = r4.itemPlayMedia
            r0.setVisible(r1)
            goto L3f
        L55:
            r0 = r1
            goto L36
        L57:
            r0 = r1
            goto L19
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.ui.chat.ContactsFragment.updateOptionsMenu():void");
    }

    @Override // android.support.v4.app.Fragment
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    @Override // android.support.v4.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.contact_fragment_menu, menu);
        this.itemLocationDetails = menu.findItem(R.id.item_contacts_location_details);
        this.itemPlayMedia = menu.findItem(R.id.item_contacts_play_parcel_media);
        updateOptionsMenu();
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.contacts, viewGroup, false);
        ViewPager viewPager = (ViewPager) inflate.findViewById(R.id.contact_list_pager);
        viewPager.setAdapter(new ContactsPagerAdapter(getChildFragmentManager()));
        ((PagerSlidingTabStrip) inflate.findViewById(R.id.contact_list_tabs)).setViewPager(viewPager);
        return inflate;
    }

    @Override // android.support.v4.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        CurrentLocationInfo currentLocationInfoSnapshot;
        ParcelData parcelData;
        UserManager userManager = ActivityUtils.getUserManager(getArguments());
        switch (menuItem.getItemId()) {
            case R.id.item_contacts_location_details /* 2131755772 */:
                if (userManager != null && (currentLocationInfoSnapshot = userManager.getCurrentLocationInfoSnapshot()) != null && (parcelData = currentLocationInfoSnapshot.parcelData()) != null) {
                    DetailsActivity.showEmbeddedDetails(getActivity(), ParcelPropertiesFragment.class, ParcelPropertiesFragment.makeSelection(userManager.getUserID(), parcelData));
                }
                return true;
            case R.id.item_contacts_play_parcel_media /* 2131755773 */:
                StreamingMediaService.startStreamingMediaService(getContext(), userManager);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        updateOptionsMenu();
    }

    @Override // android.support.v4.app.Fragment
    public void onStart() {
        super.onStart();
        UserManager userManager = ActivityUtils.getUserManager(getArguments());
        if (userManager != null) {
            this.subscription = userManager.getCurrentLocationInfo().subscribe((Subscribable<SubscriptionSingleKey, CurrentLocationInfo>) SubscriptionSingleDataPool.getSingleDataKey(), UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.chat.-$Lambda$zIl8cGSTO94X3h9h2afeKA4NC_s
                private final /* synthetic */ void $m$0(Object obj) {
                    ((ContactsFragment) this).m430com_lumiyaviewer_lumiya_ui_chat_ContactsFragmentmthref0((CurrentLocationInfo) obj);
                }

                @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
                public final void onData(Object obj) {
                    $m$0(obj);
                }
            });
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onStop() {
        if (this.subscription != null) {
            this.subscription.unsubscribe();
            this.subscription = null;
        }
        super.onStop();
    }
}
