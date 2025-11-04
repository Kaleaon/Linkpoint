// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.content.Intent;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment;
import com.astuetz.PagerSlidingTabStrip;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import android.os.Bundle;
import java.util.EnumMap;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.support.v4.app.Fragment;
import java.lang.ref.WeakReference;
import java.util.Map;
import com.lumiyaviewer.lumiya.ui.common.UserFunctionsFragment;

public class UserProfileFragment extends UserFunctionsFragment
{
    private final Map<ProfileTab, WeakReference<Fragment>> activeFragments;
    
    public UserProfileFragment() {
        this.activeFragments = new EnumMap<ProfileTab, WeakReference<Fragment>>(ProfileTab.class);
    }
    
    public static Bundle makeSelection(final ChatterID chatterID) {
        return ChatterFragment.makeSelection(chatterID);
    }
    
    @Override
    public void onCreate(@Nullable final Bundle bundle) {
        super.onCreate(bundle);
    }
    
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        final View inflate = layoutInflater.inflate(2130968757, viewGroup, false);
        final ViewPager viewPager = (ViewPager)inflate.findViewById(2131755387);
        viewPager.setAdapter(new ProfilePagerAdapter(this.getChildFragmentManager()));
        ((PagerSlidingTabStrip)inflate.findViewById(2131755386)).setViewPager(viewPager);
        return inflate;
    }
    
    @Override
    protected void onShowUser(@Nullable final ChatterID chatterID) {
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
        ProfilePagerAdapter(final FragmentManager fragmentManager) {
            super(fragmentManager);
        }
        
        @Override
        public void destroyItem(final ViewGroup viewGroup, final int n, final Object o) {
            final ProfileTab profileTab = ProfileTab.values()[n];
            if (profileTab != null) {
                UserProfileFragment.this.activeFragments.remove(profileTab);
            }
            super.destroyItem(viewGroup, n, o);
        }
        
        @Override
        public int getCount() {
            return ProfileTab.values().length;
        }
        
        @Override
        public Fragment getItem(final int n) {
            final ProfileTab profileTab = ProfileTab.values()[n];
            try {
                final Fragment referent = profileTab.tabClass.newInstance();
                referent.setArguments(UserProfileFragment.makeSelection(UserProfileFragment.this.chatterID));
                UserProfileFragment.this.activeFragments.put(profileTab, new WeakReference(referent));
                return referent;
            }
            catch (final IllegalAccessException ex) {
                return null;
            }
            catch (final java.lang.InstantiationException ex2) {
                return null;
            }
        }
        
        @Override
        public CharSequence getPageTitle(final int n) {
            return UserProfileFragment.this.getString(ProfileTab.values()[n].tabCaption);
        }
        
        @Override
        public Parcelable saveState() {
            return null;
        }
    }
    
    private enum ProfileTab
    {
        FirstLife(2131296906, (Class<? extends Fragment>)UserFirstLifeProfileTab.class), 
        Groups(2131296912, (Class<? extends Fragment>)UserGroupsProfileTab.class), 
        MainProfile(2131296919, (Class<? extends Fragment>)UserMainProfileTab.class), 
        Picks(2131296918, (Class<? extends Fragment>)UserPicksProfileTab.class);
        
        private final int tabCaption;
        private final Class<? extends Fragment> tabClass;
        
        private ProfileTab(final int tabCaption, final Class<? extends Fragment> tabClass) {
            this.tabCaption = tabCaption;
            this.tabClass = tabClass;
        }
    }
}
