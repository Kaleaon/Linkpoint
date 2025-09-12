package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.astuetz.PagerSlidingTabStrip;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import java.lang.ref.WeakReference;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public class GroupProfileFragment extends ChatterReloadableFragment implements LoadableMonitor.OnLoadableDataChangedListener {
    /* access modifiers changed from: private */
    public final Map<ProfileTab, WeakReference<Fragment>> activeFragments = new EnumMap(ProfileTab.class);
    /* access modifiers changed from: private */
    @Nullable
    public ProfilePagerAdapter adapter;
    private final ImmutableList<ProfileTab> generalGroupTabs = ImmutableList.of(ProfileTab.MainProfile, ProfileTab.Members);
    /* access modifiers changed from: private */
    @Nullable
    public ChatterID lastSelectedChatterID = null;
    /* access modifiers changed from: private */
    @Nullable
    public ProfileTab lastSelectedTab = null;
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.myGroupList).withDataChangedListener(this);
    private final SubscriptionData<UUID, AvatarGroupList> myGroupList = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final ImmutableList<ProfileTab> myGroupTabs = ImmutableList.of(ProfileTab.MainProfile, ProfileTab.Roles, ProfileTab.Members);

    private class ProfilePagerAdapter extends FragmentStatePagerAdapter {
        @Nullable
        private ImmutableList<ProfileTab> tabs;

        ProfilePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            ProfileTab profileTab;
            if (!(this.tabs == null || (profileTab = (ProfileTab) this.tabs.get(i)) == null)) {
                GroupProfileFragment.this.activeFragments.remove(profileTab);
            }
            super.destroyItem(viewGroup, i, obj);
        }

        public int getCount() {
            if (this.tabs != null) {
                return this.tabs.size();
            }
            return 0;
        }

        public Fragment getItem(int i) {
            if (this.tabs == null) {
                return null;
            }
            ProfileTab profileTab = (ProfileTab) this.tabs.get(i);
            try {
                Fragment fragment = (Fragment) profileTab.tabClass.newInstance();
                fragment.setArguments(GroupProfileFragment.makeSelection(GroupProfileFragment.this.chatterID));
                GroupProfileFragment.this.activeFragments.put(profileTab, new WeakReference(fragment));
                return fragment;
            } catch (InstantiationException e) {
                return null;
            } catch (IllegalAccessException e2) {
                return null;
            }
        }

        public CharSequence getPageTitle(int i) {
            if (this.tabs != null) {
                return GroupProfileFragment.this.getString(((ProfileTab) this.tabs.get(i)).tabCaption);
            }
            return null;
        }

        /* access modifiers changed from: package-private */
        @Nullable
        public ImmutableList<ProfileTab> getTabs() {
            return this.tabs;
        }

        public Parcelable saveState() {
            return null;
        }

        /* access modifiers changed from: package-private */
        public void setTabs(@Nullable ImmutableList<ProfileTab> immutableList) {
            if (this.tabs != immutableList) {
                this.tabs = immutableList;
                notifyDataSetChanged();
            }
        }
    }

    private enum ProfileTab {
        MainProfile(R.string.profile_tab_caption, GroupMainProfileTab.class),
        Roles(R.string.group_profile_roles_caption, GroupRolesProfileTab.class),
        Members(R.string.group_members_page_title, GroupMembersProfileTab.class);
        
        /* access modifiers changed from: private */
        public final int tabCaption;
        /* access modifiers changed from: private */
        public final Class<? extends Fragment> tabClass;

        private ProfileTab(int i, Class<? extends Fragment> cls) {
            this.tabCaption = i;
            this.tabClass = cls;
        }
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        if (bundle != null) {
            if (bundle.containsKey("lastSelectedTab")) {
                this.lastSelectedTab = ProfileTab.values()[bundle.getInt("lastSelectedTab")];
            }
            if (bundle.containsKey("lastSelectedChatterID")) {
                this.lastSelectedChatterID = (ChatterID) bundle.getParcelable("lastSelectedChatterID");
            }
        }
        View inflate = layoutInflater.inflate(R.layout.group_profile_new, viewGroup, false);
        ViewPager viewPager = (ViewPager) inflate.findViewById(R.id.user_profile_pager);
        this.adapter = new ProfilePagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(this.adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int i) {
            }

            public void onPageScrolled(int i, float f, int i2) {
            }

            public void onPageSelected(int i) {
                ImmutableList<ProfileTab> tabs;
                if (GroupProfileFragment.this.adapter != null && (tabs = GroupProfileFragment.this.adapter.getTabs()) != null && i >= 0 && i < tabs.size()) {
                    ProfileTab unused = GroupProfileFragment.this.lastSelectedTab = (ProfileTab) tabs.get(i);
                    ChatterID unused2 = GroupProfileFragment.this.lastSelectedChatterID = GroupProfileFragment.this.chatterID;
                }
            }
        });
        ((PagerSlidingTabStrip) inflate.findViewById(R.id.user_profile_tabs)).setViewPager(viewPager);
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_group_selected), getString(R.string.group_profile_fail));
        return inflate;
    }

    public void onLoadableDataChanged() {
        int i = 0;
        AvatarGroupList.AvatarGroupEntry avatarGroupEntry = null;
        try {
            if (this.chatterID instanceof ChatterID.ChatterIDGroup) {
                avatarGroupEntry = this.myGroupList.get().Groups.get(((ChatterID.ChatterIDGroup) this.chatterID).getChatterUUID());
            }
            ImmutableList<ProfileTab> immutableList = avatarGroupEntry != null ? this.myGroupTabs : this.generalGroupTabs;
            if (this.adapter != null) {
                this.adapter.setTabs(immutableList);
            }
            View view = getView();
            if (Objects.equal(this.lastSelectedChatterID, this.chatterID) && this.lastSelectedTab != null && view != null) {
                while (true) {
                    if (i >= immutableList.size()) {
                        i = -1;
                        break;
                    } else if (((ProfileTab) immutableList.get(i)).equals(this.lastSelectedTab)) {
                        break;
                    } else {
                        i++;
                    }
                }
                Debug.Printf("GroupProfile tabs: new tabIndex %d", Integer.valueOf(i));
                if (i != -1) {
                    ((ViewPager) view.findViewById(R.id.user_profile_pager)).setCurrentItem(i);
                }
            }
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Debug.Printf("GroupProfile tabs: saving lastSelectedTab %s, lastSelectedChatterID %s", this.lastSelectedTab, this.lastSelectedChatterID);
        if (this.lastSelectedTab != null) {
            bundle.putInt("lastSelectedTab", this.lastSelectedTab.ordinal());
        }
        if (this.lastSelectedChatterID != null) {
            bundle.putParcelable("lastSelectedChatterID", this.lastSelectedChatterID);
        }
    }

    /* access modifiers changed from: protected */
    public void onShowUser(@Nullable ChatterID chatterID) {
        this.myGroupList.unsubscribe();
        if (this.userManager != null && (chatterID instanceof ChatterID.ChatterIDGroup)) {
            this.myGroupList.subscribe(this.userManager.getAvatarGroupLists().getPool(), chatterID.agentUUID);
        } else if (this.adapter != null) {
            this.adapter.setTabs((ImmutableList<ProfileTab>) null);
        }
        for (WeakReference weakReference : this.activeFragments.values()) {
            Fragment fragment = (Fragment) weakReference.get();
            if (fragment instanceof ReloadableFragment) {
                ((ReloadableFragment) fragment).setFragmentArgs(getActivity() != null ? getActivity().getIntent() : null, ChatterReloadableFragment.makeSelection(chatterID));
            }
        }
    }
}
