// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.astuetz.PagerSlidingTabStrip;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.UserFunctionsFragment;
import java.lang.ref.WeakReference;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            UserMainProfileTab, UserPicksProfileTab, UserGroupsProfileTab, UserFirstLifeProfileTab

public class UserProfileFragment extends UserFunctionsFragment
{
    private class ProfilePagerAdapter extends FragmentStatePagerAdapter
    {

        final UserProfileFragment this$0;

        public void destroyItem(ViewGroup viewgroup, int i, Object obj)
        {
            ProfileTab profiletab = ProfileTab.values()[i];
            if (profiletab != null)
            {
                UserProfileFragment._2D_get0(UserProfileFragment.this).remove(profiletab);
            }
            super.destroyItem(viewgroup, i, obj);
        }

        public int getCount()
        {
            return ProfileTab.values().length;
        }

        public Fragment getItem(int i)
        {
            ProfileTab profiletab = ProfileTab.values()[i];
            Fragment fragment;
            try
            {
                fragment = (Fragment)ProfileTab._2D_get1(profiletab).newInstance();
                fragment.setArguments(UserProfileFragment.makeSelection(UserProfileFragment._2D_get1(UserProfileFragment.this)));
                UserProfileFragment._2D_get0(UserProfileFragment.this).put(profiletab, new WeakReference(fragment));
            }
            catch (InstantiationException instantiationexception)
            {
                return null;
            }
            catch (IllegalAccessException illegalaccessexception)
            {
                return null;
            }
            return fragment;
        }

        public CharSequence getPageTitle(int i)
        {
            ProfileTab profiletab = ProfileTab.values()[i];
            return getString(ProfileTab._2D_get0(profiletab));
        }

        public Parcelable saveState()
        {
            return null;
        }

        ProfilePagerAdapter(FragmentManager fragmentmanager)
        {
            this$0 = UserProfileFragment.this;
            super(fragmentmanager);
        }
    }

    private static final class ProfileTab extends Enum
    {

        private static final ProfileTab $VALUES[];
        public static final ProfileTab FirstLife;
        public static final ProfileTab Groups;
        public static final ProfileTab MainProfile;
        public static final ProfileTab Picks;
        private final int tabCaption;
        private final Class tabClass;

        static int _2D_get0(ProfileTab profiletab)
        {
            return profiletab.tabCaption;
        }

        static Class _2D_get1(ProfileTab profiletab)
        {
            return profiletab.tabClass;
        }

        public static ProfileTab valueOf(String s)
        {
            return (ProfileTab)Enum.valueOf(com/lumiyaviewer/lumiya/ui/chat/profiles/UserProfileFragment$ProfileTab, s);
        }

        public static ProfileTab[] values()
        {
            return $VALUES;
        }

        static 
        {
            MainProfile = new ProfileTab("MainProfile", 0, 0x7f090297, com/lumiyaviewer/lumiya/ui/chat/profiles/UserMainProfileTab);
            Picks = new ProfileTab("Picks", 1, 0x7f090296, com/lumiyaviewer/lumiya/ui/chat/profiles/UserPicksProfileTab);
            Groups = new ProfileTab("Groups", 2, 0x7f090290, com/lumiyaviewer/lumiya/ui/chat/profiles/UserGroupsProfileTab);
            FirstLife = new ProfileTab("FirstLife", 3, 0x7f09028a, com/lumiyaviewer/lumiya/ui/chat/profiles/UserFirstLifeProfileTab);
            $VALUES = (new ProfileTab[] {
                MainProfile, Picks, Groups, FirstLife
            });
        }

        private ProfileTab(String s, int i, int j, Class class1)
        {
            super(s, i);
            tabCaption = j;
            tabClass = class1;
        }
    }


    private final Map activeFragments = new EnumMap(com/lumiyaviewer/lumiya/ui/chat/profiles/UserProfileFragment$ProfileTab);

    static Map _2D_get0(UserProfileFragment userprofilefragment)
    {
        return userprofilefragment.activeFragments;
    }

    static ChatterID _2D_get1(UserProfileFragment userprofilefragment)
    {
        return userprofilefragment.chatterID;
    }

    public UserProfileFragment()
    {
    }

    public static Bundle makeSelection(ChatterID chatterid)
    {
        return UserFunctionsFragment.makeSelection(chatterid);
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        super.onCreateView(layoutinflater, viewgroup, bundle);
        layoutinflater = layoutinflater.inflate(0x7f0400b5, viewgroup, false);
        viewgroup = (ViewPager)layoutinflater.findViewById(0x7f10017b);
        viewgroup.setAdapter(new ProfilePagerAdapter(getChildFragmentManager()));
        ((PagerSlidingTabStrip)layoutinflater.findViewById(0x7f10017a)).setViewPager(viewgroup);
        return layoutinflater;
    }

    protected void onShowUser(ChatterID chatterid)
    {
        Iterator iterator = activeFragments.values().iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            Fragment fragment = (Fragment)((WeakReference)iterator.next()).get();
            if (fragment instanceof ReloadableFragment)
            {
                Bundle bundle = ChatterReloadableFragment.makeSelection(chatterid);
                android.content.Intent intent;
                if (getActivity() != null)
                {
                    intent = getActivity().getIntent();
                } else
                {
                    intent = null;
                }
                ((ReloadableFragment)fragment).setFragmentArgs(intent, bundle);
            }
        } while (true);
    }
}
