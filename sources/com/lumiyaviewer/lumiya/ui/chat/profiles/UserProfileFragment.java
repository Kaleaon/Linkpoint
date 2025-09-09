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
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.UserFunctionsFragment;
import java.lang.ref.WeakReference;
import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nullable;

public class UserProfileFragment extends UserFunctionsFragment {
    /* access modifiers changed from: private */
    public final Map<ProfileTab, WeakReference<Fragment>> activeFragments = new EnumMap(ProfileTab.class);

    private class ProfilePagerAdapter extends FragmentStatePagerAdapter {
        ProfilePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            ProfileTab profileTab = ProfileTab.values()[i];
            if (profileTab != null) {
                UserProfileFragment.this.activeFragments.remove(profileTab);
            }
            super.destroyItem(viewGroup, i, obj);
        }

        public int getCount() {
            return ProfileTab.values().length;
        }

        public Fragment getItem(int i) {
            ProfileTab profileTab = ProfileTab.values()[i];
            try {
                Fragment fragment = (Fragment) profileTab.tabClass.newInstance();
                fragment.setArguments(UserProfileFragment.makeSelection(UserProfileFragment.this.chatterID));
                UserProfileFragment.this.activeFragments.put(profileTab, new WeakReference(fragment));
                return fragment;
            } catch (InstantiationException e) {
                return null;
            } catch (IllegalAccessException e2) {
                return null;
            }
        }

        public CharSequence getPageTitle(int i) {
            return UserProfileFragment.this.getString(ProfileTab.values()[i].tabCaption);
        }

        public Parcelable saveState() {
            return null;
        }
    }

    private enum ProfileTab {
        MainProfile(R.string.profile_tab_caption, UserMainProfileTab.class),
        Picks(R.string.profile_picks_caption, UserPicksProfileTab.class),
        Groups(R.string.profile_groups_caption, UserGroupsProfileTab.class),
        FirstLife(R.string.profile_1st_caption, UserFirstLifeProfileTab.class);
        
        /* access modifiers changed from: private */
        public final int tabCaption;
        /* access modifiers changed from: private */
        public final Class<? extends Fragment> tabClass;

        private ProfileTab(int i, Class<? extends Fragment> cls) {
            this.tabCaption = i;
            this.tabClass = cls;
        }
    }

    public static Bundle makeSelection(ChatterID chatterID) {
        return UserFunctionsFragment.makeSelection(chatterID);
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.user_profile_new, viewGroup, false);
        ViewPager viewPager = (ViewPager) inflate.findViewById(R.id.user_profile_pager);
        viewPager.setAdapter(new ProfilePagerAdapter(getChildFragmentManager()));
        ((PagerSlidingTabStrip) inflate.findViewById(R.id.user_profile_tabs)).setViewPager(viewPager);
        return inflate;
    }

    /* access modifiers changed from: protected */
    public void onShowUser(@Nullable ChatterID chatterID) {
        for (WeakReference weakReference : this.activeFragments.values()) {
            Fragment fragment = (Fragment) weakReference.get();
            if (fragment instanceof ReloadableFragment) {
                ((ReloadableFragment) fragment).setFragmentArgs(getActivity() != null ? getActivity().getIntent() : null, ChatterReloadableFragment.makeSelection(chatterID));
            }
        }
    }
}
