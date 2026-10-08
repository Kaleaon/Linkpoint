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
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class GroupProfileFragment extends ChatterReloadableFragment implements LoadableMonitor.OnLoadableDataChangedListener {
    @Nullable
    private ProfilePagerAdapter adapter;
    private final Map<ProfileTab, WeakReference<Fragment>> activeFragments = new EnumMap(ProfileTab.class);
    private final ImmutableList<ProfileTab> generalGroupTabs = ImmutableList.of(ProfileTab.MainProfile, ProfileTab.Members);
    private final ImmutableList<ProfileTab> myGroupTabs = ImmutableList.of(ProfileTab.MainProfile, ProfileTab.Roles, ProfileTab.Members);
    private final SubscriptionData<UUID, AvatarGroupList> myGroupList = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.myGroupList).withDataChangedListener(this);
    @Nullable
    private ProfileTab lastSelectedTab = null;
    @Nullable
    private ChatterID lastSelectedChatterID = null;

    /* loaded from: classes.dex */
    private class ProfilePagerAdapter extends FragmentStatePagerAdapter {
        @Nullable
        private ImmutableList<ProfileTab> tabs;

        ProfilePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override // android.support.v4.app.FragmentStatePagerAdapter, android.support.v4.view.PagerAdapter
        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            ProfileTab profileTab;
            if (this.tabs != null && (profileTab = this.tabs.get(i)) != null) {
                GroupProfileFragment.this.activeFragments.remove(profileTab);
            }
            super.destroyItem(viewGroup, i, obj);
        }

        @Override // android.support.v4.view.PagerAdapter
        public int getCount() {
            if (this.tabs != null) {
                return this.tabs.size();
            }
            return 0;
        }

        @Override // android.support.v4.app.FragmentStatePagerAdapter
        public Fragment getItem(int i) {
            if (this.tabs != null) {
                ProfileTab profileTab = this.tabs.get(i);
                try {
                    Fragment fragment = (Fragment) profileTab.tabClass.newInstance();
                    fragment.setArguments(GroupProfileFragment.makeSelection(GroupProfileFragment.this.chatterID));
                    GroupProfileFragment.this.activeFragments.put(profileTab, new WeakReference(fragment));
                    return fragment;
                } catch (IllegalAccessException e) {
                    return null;
                } catch (InstantiationException e2) {
                    return null;
                }
            }
            return null;
        }

        @Override // android.support.v4.view.PagerAdapter
        public CharSequence getPageTitle(int i) {
            if (this.tabs != null) {
                return GroupProfileFragment.this.getString(this.tabs.get(i).tabCaption);
            }
            return null;
        }

        @Nullable
        ImmutableList<ProfileTab> getTabs() {
            return this.tabs;
        }

        @Override // android.support.v4.app.FragmentStatePagerAdapter, android.support.v4.view.PagerAdapter
        public Parcelable saveState() {
            return null;
        }

        void setTabs(@Nullable ImmutableList<ProfileTab> immutableList) {
            if (this.tabs != immutableList) {
                this.tabs = immutableList;
                notifyDataSetChanged();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum ProfileTab {
        MainProfile(R.string.profile_tab_caption, GroupMainProfileTab.class),
        Roles(R.string.group_profile_roles_caption, GroupRolesProfileTab.class),
        Members(R.string.group_members_page_title, GroupMembersProfileTab.class);
        
        private final int tabCaption;
        private final Class<? extends Fragment> tabClass;

        ProfileTab(int i, Class cls) {
            this.tabCaption = i;
            this.tabClass = cls;
        }

        /* renamed from: values  reason: to resolve conflict with enum method */
        public static ProfileTab[] valuesCustom() {
            return values();
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle, android.support.v4.app.Fragment
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        if (bundle != null) {
            if (bundle.containsKey("lastSelectedTab")) {
                this.lastSelectedTab = ProfileTab.valuesCustom()[bundle.getInt("lastSelectedTab")];
            }
            if (bundle.containsKey("lastSelectedChatterID")) {
                this.lastSelectedChatterID = (ChatterID) bundle.getParcelable("lastSelectedChatterID");
            }
        }
        View inflate = layoutInflater.inflate(R.layout.group_profile_new, viewGroup, false);
        ViewPager viewPager = (ViewPager) inflate.findViewById(R.id.user_profile_pager);
        this.adapter = new ProfilePagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(this.adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.GroupProfileFragment.1
            @Override // android.support.v4.view.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int i) {
            }

            @Override // android.support.v4.view.ViewPager.OnPageChangeListener
            public void onPageScrolled(int i, float f, int i2) {
            }

            @Override // android.support.v4.view.ViewPager.OnPageChangeListener
            public void onPageSelected(int i) {
                ImmutableList<ProfileTab> tabs;
                if (GroupProfileFragment.this.adapter == null || (tabs = GroupProfileFragment.this.adapter.getTabs()) == null || i < 0 || i >= tabs.size()) {
                    return;
                }
                GroupProfileFragment.this.lastSelectedTab = tabs.get(i);
                GroupProfileFragment.this.lastSelectedChatterID = GroupProfileFragment.this.chatterID;
            }
        });
        ((PagerSlidingTabStrip) inflate.findViewById(R.id.user_profile_tabs)).setViewPager(viewPager);
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_group_selected), getString(R.string.group_profile_fail));
        return inflate;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
    public void onLoadableDataChanged() {
        int i = 0;
        try {
            ImmutableList<ProfileTab> immutableList = (this.chatterID instanceof ChatterID.ChatterIDGroup ? this.myGroupList.get().Groups.get(((ChatterID.ChatterIDGroup) this.chatterID).getChatterUUID()) : null) != null ? this.myGroupTabs : this.generalGroupTabs;
            if (this.adapter != null) {
                this.adapter.setTabs(immutableList);
            }
            View view = getView();
            if (!Objects.equal(this.lastSelectedChatterID, this.chatterID) || this.lastSelectedTab == null || view == null) {
                return;
            }
            while (true) {
                if (i >= immutableList.size()) {
                    i = -1;
                    break;
                } else if (immutableList.get(i).equals(this.lastSelectedTab)) {
                    break;
                } else {
                    i++;
                }
            }
            Debug.Printf("GroupProfile tabs: new tabIndex %d", Integer.valueOf(i));
            if (i != -1) {
                ((ViewPager) view.findViewById(R.id.user_profile_pager)).setCurrentItem(i);
            }
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle, android.support.v4.app.Fragment
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

    @Override // com.lumiyaviewer.lumiya.ui.common.ChatterFragment
    protected void onShowUser(@Nullable ChatterID chatterID) {
        this.myGroupList.unsubscribe();
        if (this.userManager != null && (chatterID instanceof ChatterID.ChatterIDGroup)) {
            this.myGroupList.subscribe(this.userManager.getAvatarGroupLists().getPool(), chatterID.agentUUID);
        } else if (this.adapter != null) {
            this.adapter.setTabs(null);
        }
        Iterator<T> it = this.activeFragments.values().iterator();
        while (it.hasNext()) {
            Fragment fragment = (Fragment) ((WeakReference) it.next()).get();
            if (fragment instanceof ReloadableFragment) {
                ((ReloadableFragment) fragment).setFragmentArgs(getActivity() != null ? getActivity().getIntent() : null, ChatterReloadableFragment.makeSelection(chatterID));
            }
        }
    }
}
