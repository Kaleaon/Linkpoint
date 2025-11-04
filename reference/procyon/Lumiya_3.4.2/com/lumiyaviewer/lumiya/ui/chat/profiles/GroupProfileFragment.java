// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.content.Intent;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment;
import com.lumiyaviewer.lumiya.react.Subscribable;
import android.os.Parcelable;
import com.lumiyaviewer.lumiya.Debug;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.astuetz.PagerSlidingTabStrip;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import java.util.EnumMap;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.google.common.collect.ImmutableList;
import javax.annotation.Nullable;
import android.support.v4.app.Fragment;
import java.lang.ref.WeakReference;
import java.util.Map;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;

public class GroupProfileFragment extends ChatterReloadableFragment implements OnLoadableDataChangedListener
{
    private final Map<ProfileTab, WeakReference<Fragment>> activeFragments;
    @Nullable
    private ProfilePagerAdapter adapter;
    private final ImmutableList<ProfileTab> generalGroupTabs;
    @Nullable
    private ChatterID lastSelectedChatterID;
    @Nullable
    private ProfileTab lastSelectedTab;
    private final LoadableMonitor loadableMonitor;
    private final SubscriptionData<UUID, AvatarGroupList> myGroupList;
    private final ImmutableList<ProfileTab> myGroupTabs;
    
    public GroupProfileFragment() {
        this.activeFragments = new EnumMap<ProfileTab, WeakReference<Fragment>>(ProfileTab.class);
        this.generalGroupTabs = ImmutableList.of(ProfileTab.MainProfile, ProfileTab.Members);
        this.myGroupTabs = ImmutableList.of(ProfileTab.MainProfile, ProfileTab.Roles, ProfileTab.Members);
        this.myGroupList = new SubscriptionData<UUID, AvatarGroupList>(UIThreadExecutor.getInstance());
        this.loadableMonitor = new LoadableMonitor(new Loadable[] { this.myGroupList }).withDataChangedListener((LoadableMonitor.OnLoadableDataChangedListener)this);
        this.lastSelectedTab = null;
        this.lastSelectedChatterID = null;
    }
    
    @Override
    public void onCreate(@Nullable final Bundle bundle) {
        super.onCreate(bundle);
    }
    
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        if (bundle != null) {
            if (bundle.containsKey("lastSelectedTab")) {
                this.lastSelectedTab = ProfileTab.values()[bundle.getInt("lastSelectedTab")];
            }
            if (bundle.containsKey("lastSelectedChatterID")) {
                this.lastSelectedChatterID = (ChatterID)bundle.getParcelable("lastSelectedChatterID");
            }
        }
        final View inflate = layoutInflater.inflate(2130968646, viewGroup, false);
        final ViewPager viewPager = (ViewPager)inflate.findViewById(2131755387);
        viewPager.setAdapter(this.adapter = new ProfilePagerAdapter(this.getChildFragmentManager()));
        viewPager.addOnPageChangeListener((ViewPager.OnPageChangeListener)new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(final int n) {
            }
            
            @Override
            public void onPageScrolled(final int n, final float n2, final int n3) {
            }
            
            @Override
            public void onPageSelected(final int n) {
                if (GroupProfileFragment.this.adapter != null) {
                    final ImmutableList<ProfileTab> tabs = GroupProfileFragment.this.adapter.getTabs();
                    if (tabs != null && n >= 0 && n < tabs.size()) {
                        GroupProfileFragment.this.lastSelectedTab = (ProfileTab)tabs.get(n);
                        GroupProfileFragment.this.lastSelectedChatterID = GroupProfileFragment.this.chatterID;
                    }
                }
            }
        });
        ((PagerSlidingTabStrip)inflate.findViewById(2131755386)).setViewPager(viewPager);
        this.loadableMonitor.setLoadingLayout((LoadingLayout)inflate.findViewById(2131755197), this.getString(2131296736), this.getString(2131296593));
        return inflate;
    }
    
    @Override
    public void onLoadableDataChanged() {
        while (true) {
            int i = 0;
            AvatarGroupList.AvatarGroupEntry avatarGroupEntry = null;
        Label_0124:
            while (true) {
                Label_0181: {
                    try {
                        if (this.chatterID instanceof ChatterID.ChatterIDGroup) {
                            avatarGroupEntry = (AvatarGroupList.AvatarGroupEntry)this.myGroupList.get().Groups.get(((ChatterID.ChatterIDGroup)this.chatterID).getChatterUUID());
                        }
                        ImmutableList<ProfileTab> tabs;
                        if (avatarGroupEntry != null) {
                            tabs = this.myGroupTabs;
                        }
                        else {
                            tabs = this.generalGroupTabs;
                        }
                        if (this.adapter != null) {
                            this.adapter.setTabs(tabs);
                        }
                        final View view = this.getView();
                        if (Objects.equal(this.lastSelectedChatterID, this.chatterID) && this.lastSelectedTab != null && view != null) {
                            while (i < tabs.size()) {
                                if (((ProfileTab)tabs.get(i)).equals(this.lastSelectedTab)) {
                                    break Label_0124;
                                }
                                ++i;
                            }
                            break Label_0181;
                            Debug.Printf("GroupProfile tabs: new tabIndex %d", i);
                            if (i != -1) {
                                ((ViewPager)view.findViewById(2131755387)).setCurrentItem(i);
                            }
                        }
                        return;
                    }
                    catch (final SubscriptionData.DataNotReadyException ex) {
                        Debug.Warning(ex);
                        return;
                    }
                }
                i = -1;
                continue Label_0124;
            }
        }
    }
    
    @Override
    public void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Debug.Printf("GroupProfile tabs: saving lastSelectedTab %s, lastSelectedChatterID %s", this.lastSelectedTab, this.lastSelectedChatterID);
        if (this.lastSelectedTab != null) {
            bundle.putInt("lastSelectedTab", this.lastSelectedTab.ordinal());
        }
        if (this.lastSelectedChatterID != null) {
            bundle.putParcelable("lastSelectedChatterID", (Parcelable)this.lastSelectedChatterID);
        }
    }
    
    @Override
    protected void onShowUser(@Nullable final ChatterID chatterID) {
        this.myGroupList.unsubscribe();
        if (this.userManager != null && chatterID instanceof ChatterID.ChatterIDGroup) {
            this.myGroupList.subscribe(this.userManager.getAvatarGroupLists().getPool(), chatterID.agentUUID);
        }
        else if (this.adapter != null) {
            this.adapter.setTabs(null);
        }
        final Iterator<Object> iterator = this.activeFragments.values().iterator();
        while (iterator.hasNext()) {
            final Fragment fragment = iterator.next().get();
            if (fragment instanceof ReloadableFragment) {
                final Bundle selection = ChatterFragment.makeSelection(chatterID);
                Intent intent;
                if (this.getActivity() != null) {
                    intent = this.getActivity().getIntent();
                }
                else {
                    intent = null;
                }
                ((ReloadableFragment)fragment).setFragmentArgs(intent, selection);
            }
        }
    }
    
    private class ProfilePagerAdapter extends FragmentStatePagerAdapter
    {
        @Nullable
        private ImmutableList<ProfileTab> tabs;
        
        ProfilePagerAdapter(final FragmentManager fragmentManager) {
            super(fragmentManager);
        }
        
        @Override
        public void destroyItem(final ViewGroup viewGroup, final int n, final Object o) {
            if (this.tabs != null) {
                final ProfileTab profileTab = this.tabs.get(n);
                if (profileTab != null) {
                    GroupProfileFragment.this.activeFragments.remove(profileTab);
                }
            }
            super.destroyItem(viewGroup, n, o);
        }
        
        @Override
        public int getCount() {
            if (this.tabs != null) {
                return this.tabs.size();
            }
            return 0;
        }
        
        @Override
        public Fragment getItem(final int n) {
            if (this.tabs != null) {
                final ProfileTab profileTab = this.tabs.get(n);
                try {
                    final Fragment referent = profileTab.tabClass.newInstance();
                    referent.setArguments(ChatterFragment.makeSelection(GroupProfileFragment.this.chatterID));
                    GroupProfileFragment.this.activeFragments.put(profileTab, new WeakReference(referent));
                    return referent;
                }
                catch (final IllegalAccessException ex) {
                    return null;
                }
                catch (final java.lang.InstantiationException ex2) {
                    return null;
                }
            }
            return null;
        }
        
        @Override
        public CharSequence getPageTitle(final int n) {
            if (this.tabs != null) {
                return GroupProfileFragment.this.getString(this.tabs.get(n).tabCaption);
            }
            return null;
        }
        
        @Nullable
        ImmutableList<ProfileTab> getTabs() {
            return this.tabs;
        }
        
        @Override
        public Parcelable saveState() {
            return null;
        }
        
        void setTabs(@Nullable final ImmutableList<ProfileTab> tabs) {
            if (this.tabs != tabs) {
                this.tabs = tabs;
                this.notifyDataSetChanged();
            }
        }
    }
    
    private enum ProfileTab
    {
        MainProfile(2131296919, (Class<? extends Fragment>)GroupMainProfileTab.class), 
        Members(2131296580, (Class<? extends Fragment>)GroupMembersProfileTab.class), 
        Roles(2131296594, (Class<? extends Fragment>)GroupRolesProfileTab.class);
        
        private final int tabCaption;
        private final Class<? extends Fragment> tabClass;
        
        private ProfileTab(final int tabCaption, final Class<? extends Fragment> tabClass) {
            this.tabCaption = tabCaption;
            this.tabClass = tabClass;
        }
    }
}
