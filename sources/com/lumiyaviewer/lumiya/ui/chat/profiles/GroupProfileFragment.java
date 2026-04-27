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
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.SerializableResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import java.lang.ref.WeakReference;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            GroupMainProfileTab, GroupRolesProfileTab, GroupMembersProfileTab

public class GroupProfileFragment extends ChatterReloadableFragment
    implements com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
{
    private class ProfilePagerAdapter extends FragmentStatePagerAdapter
    {

        private ImmutableList tabs;
        final GroupProfileFragment this$0;

        public void destroyItem(ViewGroup viewgroup, int i, Object obj)
        {
            if (tabs != null)
            {
                ProfileTab profiletab = (ProfileTab)tabs.get(i);
                if (profiletab != null)
                {
                    GroupProfileFragment._2D_get0(GroupProfileFragment.this).remove(profiletab);
                }
            }
            super.destroyItem(viewgroup, i, obj);
        }

        public int getCount()
        {
            if (tabs != null)
            {
                return tabs.size();
            } else
            {
                return 0;
            }
        }

        public Fragment getItem(int i)
        {
            if (tabs != null)
            {
                ProfileTab profiletab = (ProfileTab)tabs.get(i);
                Fragment fragment;
                try
                {
                    fragment = (Fragment)ProfileTab._2D_get1(profiletab).newInstance();
                    fragment.setArguments(GroupProfileFragment.makeSelection(GroupProfileFragment._2D_get2(GroupProfileFragment.this)));
                    GroupProfileFragment._2D_get0(GroupProfileFragment.this).put(profiletab, new WeakReference(fragment));
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
            } else
            {
                return null;
            }
        }

        public CharSequence getPageTitle(int i)
        {
            if (tabs != null)
            {
                ProfileTab profiletab = (ProfileTab)tabs.get(i);
                return getString(ProfileTab._2D_get0(profiletab));
            } else
            {
                return null;
            }
        }

        ImmutableList getTabs()
        {
            return tabs;
        }

        public Parcelable saveState()
        {
            return null;
        }

        void setTabs(ImmutableList immutablelist)
        {
            if (tabs != immutablelist)
            {
                tabs = immutablelist;
                notifyDataSetChanged();
            }
        }

        ProfilePagerAdapter(FragmentManager fragmentmanager)
        {
            this$0 = GroupProfileFragment.this;
            super(fragmentmanager);
        }
    }

    private static final class ProfileTab extends Enum
    {

        private static final ProfileTab $VALUES[];
        public static final ProfileTab MainProfile;
        public static final ProfileTab Members;
        public static final ProfileTab Roles;
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
            return (ProfileTab)Enum.valueOf(com/lumiyaviewer/lumiya/ui/chat/profiles/GroupProfileFragment$ProfileTab, s);
        }

        public static ProfileTab[] values()
        {
            return $VALUES;
        }

        static 
        {
            MainProfile = new ProfileTab("MainProfile", 0, 0x7f090297, com/lumiyaviewer/lumiya/ui/chat/profiles/GroupMainProfileTab);
            Roles = new ProfileTab("Roles", 1, 0x7f090152, com/lumiyaviewer/lumiya/ui/chat/profiles/GroupRolesProfileTab);
            Members = new ProfileTab("Members", 2, 0x7f090144, com/lumiyaviewer/lumiya/ui/chat/profiles/GroupMembersProfileTab);
            $VALUES = (new ProfileTab[] {
                MainProfile, Roles, Members
            });
        }

        private ProfileTab(String s, int i, int j, Class class1)
        {
            super(s, i);
            tabCaption = j;
            tabClass = class1;
        }
    }


    private final Map activeFragments = new EnumMap(com/lumiyaviewer/lumiya/ui/chat/profiles/GroupProfileFragment$ProfileTab);
    private ProfilePagerAdapter adapter;
    private final ImmutableList generalGroupTabs;
    private ChatterID lastSelectedChatterID;
    private ProfileTab lastSelectedTab;
    private final LoadableMonitor loadableMonitor;
    private final SubscriptionData myGroupList = new SubscriptionData(UIThreadExecutor.getInstance());
    private final ImmutableList myGroupTabs;

    static Map _2D_get0(GroupProfileFragment groupprofilefragment)
    {
        return groupprofilefragment.activeFragments;
    }

    static ProfilePagerAdapter _2D_get1(GroupProfileFragment groupprofilefragment)
    {
        return groupprofilefragment.adapter;
    }

    static ChatterID _2D_get2(GroupProfileFragment groupprofilefragment)
    {
        return groupprofilefragment.chatterID;
    }

    static ChatterID _2D_set0(GroupProfileFragment groupprofilefragment, ChatterID chatterid)
    {
        groupprofilefragment.lastSelectedChatterID = chatterid;
        return chatterid;
    }

    static ProfileTab _2D_set1(GroupProfileFragment groupprofilefragment, ProfileTab profiletab)
    {
        groupprofilefragment.lastSelectedTab = profiletab;
        return profiletab;
    }

    public GroupProfileFragment()
    {
        generalGroupTabs = ImmutableList.of(ProfileTab.MainProfile, ProfileTab.Members);
        myGroupTabs = ImmutableList.of(ProfileTab.MainProfile, ProfileTab.Roles, ProfileTab.Members);
        loadableMonitor = (new LoadableMonitor(new Loadable[] {
            myGroupList
        })).withDataChangedListener(this);
        lastSelectedTab = null;
        lastSelectedChatterID = null;
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        super.onCreateView(layoutinflater, viewgroup, bundle);
        if (bundle != null)
        {
            if (bundle.containsKey("lastSelectedTab"))
            {
                lastSelectedTab = ProfileTab.values()[bundle.getInt("lastSelectedTab")];
            }
            if (bundle.containsKey("lastSelectedChatterID"))
            {
                lastSelectedChatterID = (ChatterID)bundle.getParcelable("lastSelectedChatterID");
            }
        }
        layoutinflater = layoutinflater.inflate(0x7f040046, viewgroup, false);
        viewgroup = (ViewPager)layoutinflater.findViewById(0x7f10017b);
        adapter = new ProfilePagerAdapter(getChildFragmentManager());
        viewgroup.setAdapter(adapter);
        viewgroup.addOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {

            final GroupProfileFragment this$0;

            public void onPageScrollStateChanged(int i)
            {
            }

            public void onPageScrolled(int i, float f, int j)
            {
            }

            public void onPageSelected(int i)
            {
                if (GroupProfileFragment._2D_get1(GroupProfileFragment.this) != null)
                {
                    ImmutableList immutablelist = GroupProfileFragment._2D_get1(GroupProfileFragment.this).getTabs();
                    if (immutablelist != null && i >= 0 && i < immutablelist.size())
                    {
                        GroupProfileFragment._2D_set1(GroupProfileFragment.this, (ProfileTab)immutablelist.get(i));
                        GroupProfileFragment._2D_set0(GroupProfileFragment.this, GroupProfileFragment._2D_get2(GroupProfileFragment.this));
                    }
                }
            }

            
            {
                this$0 = GroupProfileFragment.this;
                super();
            }
        });
        ((PagerSlidingTabStrip)layoutinflater.findViewById(0x7f10017a)).setViewPager(viewgroup);
        loadableMonitor.setLoadingLayout((LoadingLayout)layoutinflater.findViewById(0x7f1000bd), getString(0x7f0901e0), getString(0x7f090151));
        return layoutinflater;
    }

    public void onLoadableDataChanged()
    {
        Object obj;
        int i;
        i = 0;
        obj = null;
        if (chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)
        {
            obj = (com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry)((AvatarGroupList)myGroupList.get()).Groups.get(((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)chatterID).getChatterUUID());
        }
        if (obj == null) goto _L2; else goto _L1
_L1:
        obj = myGroupTabs;
_L11:
        View view;
        if (adapter != null)
        {
            adapter.setTabs(((ImmutableList) (obj)));
        }
        view = getView();
        if (!Objects.equal(lastSelectedChatterID, chatterID) || lastSelectedTab == null || view == null) goto _L4; else goto _L3
_L3:
        if (i >= ((ImmutableList) (obj)).size()) goto _L6; else goto _L5
_L5:
        if (!((ProfileTab)((ImmutableList) (obj)).get(i)).equals(lastSelectedTab)) goto _L8; else goto _L7
_L7:
        Debug.Printf("GroupProfile tabs: new tabIndex %d", new Object[] {
            Integer.valueOf(i)
        });
          goto _L9
_L2:
        obj = generalGroupTabs;
        continue; /* Loop/switch isn't completed */
_L8:
        i++;
          goto _L3
_L9:
        if (i != -1)
        {
            try
            {
                ((ViewPager)view.findViewById(0x7f10017b)).setCurrentItem(i);
                return;
            }
            catch (com.lumiyaviewer.lumiya.react.SubscriptionData.DataNotReadyException datanotreadyexception)
            {
                Debug.Warning(datanotreadyexception);
            }
        }
        return;
_L6:
        i = -1;
          goto _L7
_L4:
        return;
        if (true) goto _L11; else goto _L10
_L10:
    }

    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        Debug.Printf("GroupProfile tabs: saving lastSelectedTab %s, lastSelectedChatterID %s", new Object[] {
            lastSelectedTab, lastSelectedChatterID
        });
        if (lastSelectedTab != null)
        {
            bundle.putInt("lastSelectedTab", lastSelectedTab.ordinal());
        }
        if (lastSelectedChatterID != null)
        {
            bundle.putParcelable("lastSelectedChatterID", lastSelectedChatterID);
        }
    }

    protected void onShowUser(ChatterID chatterid)
    {
        myGroupList.unsubscribe();
        if (userManager == null || !(chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)) goto _L2; else goto _L1
_L1:
        myGroupList.subscribe(userManager.getAvatarGroupLists().getPool(), chatterid.agentUUID);
_L4:
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
        break; /* Loop/switch isn't completed */
_L2:
        if (adapter != null)
        {
            adapter.setTabs(null);
        }
        if (true) goto _L4; else goto _L3
_L3:
    }
}
