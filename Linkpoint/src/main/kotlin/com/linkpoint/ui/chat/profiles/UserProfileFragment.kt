package com.linkpoint.ui.chat.profiles

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
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
import javax.annotation.Nullable

class UserProfileFragment : UserFunctionsFragment() {
    /* access modifiers changed from: private */
    val Map<ProfileTab, WeakReference<Fragment>> activeFragments = EnumMap(ProfileTab.class)

    private class ProfilePagerAdapter : FragmentStatePagerAdapter() {
        ProfilePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager)
        }

        fun destroyItem(viewGroup: ViewGroup, i: Int, obj: Object) {
            val profileTab: ProfileTab = ProfileTab.values()[i]
            if (profileTab != null) {
                UserProfileFragment.this.activeFragments.remove(profileTab)
            }
            super.destroyItem(viewGroup, i, obj)
        }

         public fun getCount(): Int {
            return ProfileTab.values().length
        }

         public fun getItem(i: Int): Fragment {
            val profileTab: ProfileTab = ProfileTab.values()[i]
            try {
                val fragment: Fragment = (Fragment) profileTab.tabClass.newInstance()
                fragment.setArguments(UserProfileFragment.makeSelection(UserProfileFragment.this.chatterID))
                UserProfileFragment.this.activeFragments.put(profileTab, WeakReference(fragment))
                return fragment
            } catch (InstantiationException e) {
                return null
            } catch (IllegalAccessException e2) {
                return null
            }
        }

         public fun getPageTitle(i: Int): CharSequence {
            return UserProfileFragment.this.getString(ProfileTab.values()[i].tabCaption)
        }

         public fun saveState(): Parcelable {
            return null
        }
    }

    private enum ProfileTab {
        MainProfile(R.string.profile_tab_caption, UserMainProfileTab.class),
        Picks(R.string.profile_picks_caption, UserPicksProfileTab.class),
        Groups(R.string.profile_groups_caption, UserGroupsProfileTab.class),
        FirstLife(R.string.profile_1st_caption, UserFirstLifeProfileTab.class)
        
        /* access modifiers changed from: private */
        val Int tabCaption
        /* access modifiers changed from: private */
        val Class<? : Fragment> tabClass

        private ProfileTab(Int i, Class<? : Fragment> cls) {
            this.tabCaption = i
            this.tabClass = cls
        }
    }

    @JvmStatic
     fun makeSelection(chatterID: ChatterID): Bundle {
        return UserFunctionsFragment.makeSelection(chatterID)
    }

    override fun onCreate(bundle: Bundle) {
        super.onCreate(bundle)
    }

     public override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup, bundle: Bundle): View {
        super.onCreateView(layoutInflater, viewGroup, bundle)
        val inflate: View = layoutInflater.inflate(R.layout.user_profile_new, viewGroup, false)
        val viewPager: ViewPager = (ViewPager) inflate.findViewById(R.id.user_profile_pager)
        viewPager.setAdapter(ProfilePagerAdapter(getChildFragmentManager()))
        ((PagerSlidingTabStrip) inflate.findViewById(R.id.user_profile_tabs)).setViewPager(viewPager)
        return inflate
    }

    /* access modifiers changed from: protected */
    fun onShowUser(chatterID: ChatterID) {
        for (WeakReference weakReference : this.activeFragments.values()) {
            val fragment: Fragment = (Fragment) weakReference.get()
            if (fragment instanceof ReloadableFragment) {
                ((ReloadableFragment) fragment).setFragmentArgs(getActivity() != null ? getActivity().getIntent() : null, ChatterReloadableFragment.makeSelection(chatterID))
            }
        }
    }
}
