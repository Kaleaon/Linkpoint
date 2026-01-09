package com.linkpoint.ui.chat.profiles

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.core.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.astuetz.PagerSlidingTabStrip
import com.linkpoint.R
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.ui.common.ChatterReloadableFragment
import com.linkpoint.ui.common.ReloadableFragment
import com.linkpoint.ui.common.UserFunctionsFragment
import java.lang.ref.WeakReference
import java.util.EnumMap
import java.util.Map
import androidx.annotation.Nullable

class UserProfileFragment : UserFunctionsFragment {
    /* access modifiers changed from: private */
    Map<ProfileTab, WeakReference<Fragment>> activeFragments = EnumMap(ProfileTab.class)

    private class ProfilePagerAdapter : FragmentStatePagerAdapter {
        ProfilePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager)
        }

        fun destroyItem(ViewGroup viewGroup, Int i, Any obj)  {
            ProfileTab profileTab = ProfileTab.values()[i]
            if (profileTab != null) {
                UserProfileFragment.this.activeFragments.remove(profileTab)
            }
            super.destroyItem(viewGroup, i, obj)
        }

        fun getCount(): Int {
            return ProfileTab.values().size
        }

        fun getItem(Int i): Fragment {
            ProfileTab profileTab = ProfileTab.values()[i]
            try {
                Fragment fragment = (profileTab as Fragment).tabClass.newInstance()
                fragment.setArguments(UserProfileFragment.makeSelection(UserProfileFragment.this.chatterID))
                UserProfileFragment.this.activeFragments.put(profileTab, WeakReference(fragment))
                return fragment
            } catch (InstantiationException e) {
                return null
            } catch (IllegalAccessException e2) {
                return null
            }
        }

        fun getPageTitle(Int i): CharSequence {
            return UserProfileFragment.this.getString(ProfileTab.values()[i].tabCaption)
        }

        fun saveState(): Parcelable {
            return null
        }
    }

    private enum ProfileTab {
        MainProfile(R.string.profile_tab_caption, UserMainProfileTab.class),
        Picks(R.string.profile_picks_caption, UserPicksProfileTab.class),
        Groups(R.string.profile_groups_caption, UserGroupsProfileTab.class),
        FirstLife(R.string.profile_1st_caption, UserFirstLifeProfileTab.class)
        
        /* access modifiers changed from: private */
        Int tabCaption
        /* access modifiers changed from: private */
        Class<? : Fragment> tabClass

        private ProfileTab(Int i, Class<? : Fragment> cls) {
            this.tabCaption = i
            this.tabClass = cls
        }
    }

    fun makeSelection(ChatterID chatterID): Bundle {
        return UserFunctionsFragment.makeSelection(chatterID)
    }

    fun onCreate(@Nullable Bundle bundle)  {
        super.onCreate(bundle)
    }

    fun onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle): View {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        View inflate = layoutInflater.inflate(R.layout.user_profile_new, viewGroup, false)
        ViewPager viewPager = (inflate as ViewPager).findViewById(R.id.user_profile_pager)
        viewPager.setAdapter(ProfilePagerAdapter(getChildFragmentManager()))
        ((inflate as PagerSlidingTabStrip).findViewById(R.id.user_profile_tabs)).setViewPager(viewPager)
        return inflate
    }

    /* access modifiers changed from: protected */
    fun onShowUser(@Nullable ChatterID chatterID)  {
        for (WeakReference weakReference : this.activeFragments.values()) {
            Fragment fragment = (weakReference as Fragment).get()
            if (fragment is ReloadableFragment) {
                ((ReloadableFragment) fragment).setFragmentArgs(getActivity() != null ? getActivity().getIntent() : null, ChatterReloadableFragment.makeSelection(chatterID))
            }
        }
    }
}
