// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.avapicker;

import android.widget.ListView;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;
import android.widget.AdapterView;
import com.astuetz.PagerSlidingTabStrip;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.os.Bundle;
import android.view.ViewGroup;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.widget.FrameLayout;
import android.view.LayoutInflater;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterDisplayData;
import com.google.common.base.Predicate;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatterListSubscriptionAdapter;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType;
import javax.annotation.Nonnull;
import android.widget.ListAdapter;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.Context;
import android.widget.AdapterView$OnItemClickListener;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;

public abstract class AvatarPickerFragment extends FragmentWithTitle implements AdapterView$OnItemClickListener
{
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-ui-avapicker-AvatarPickerFragment$ContactListTypeSwitchesValues() {
        if (AvatarPickerFragment.-com-lumiyaviewer-lumiya-ui-avapicker-AvatarPickerFragment$ContactListTypeSwitchesValues != null) {
            return AvatarPickerFragment.-com-lumiyaviewer-lumiya-ui-avapicker-AvatarPickerFragment$ContactListTypeSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-ui-avapicker-AvatarPickerFragment$ContactListTypeSwitchesValues = new int[ContactListType.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-ui-avapicker-AvatarPickerFragment$ContactListTypeSwitchesValues[ContactListType.Friends.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-ui-avapicker-AvatarPickerFragment$ContactListTypeSwitchesValues[ContactListType.Nearby.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-ui-avapicker-AvatarPickerFragment$ContactListTypeSwitchesValues[ContactListType.Recent.ordinal()] = 3;
                        return -com-lumiyaviewer-lumiya-ui-avapicker-AvatarPickerFragment$ContactListTypeSwitchesValues = -com-lumiyaviewer-lumiya-ui-avapicker-AvatarPickerFragment$ContactListTypeSwitchesValues;
                    }
                    catch (final NoSuchFieldError noSuchFieldError) {}
                }
                catch (final NoSuchFieldError noSuchFieldError2) {}
            }
            catch (final NoSuchFieldError noSuchFieldError3) {
                continue;
            }
            break;
        }
    }
    
    @Nonnull
    private ListAdapter createListAdapter(final Context context, final UserManager userManager, @Nonnull final ContactListType contactListType) {
        switch (-getcom-lumiyaviewer-lumiya-ui-avapicker-AvatarPickerFragment$ContactListTypeSwitchesValues()[contactListType.ordinal()]) {
            default: {
                throw new IllegalArgumentException("Unknown contact list type");
            }
            case 3: {
                return (ListAdapter)new ChatterListSubscriptionAdapter(context, userManager, ChatterListType.Active, new UsersOnlyPredicate(null));
            }
            case 1: {
                return (ListAdapter)new ChatterListSubscriptionAdapter(context, userManager, ChatterListType.Friends);
            }
            case 2: {
                return (ListAdapter)new ChatterListSubscriptionAdapter(context, userManager, ChatterListType.Nearby);
            }
        }
    }
    
    protected void createExtraView(final LayoutInflater layoutInflater, final FrameLayout frameLayout) {
    }
    
    @Override
    public abstract String getTitle();
    
    protected abstract void onAvatarSelected(final ChatterID p0, @Nullable final String p1);
    
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        final View inflate = layoutInflater.inflate(2130968607, viewGroup, false);
        final ViewPager viewPager = (ViewPager)inflate.findViewById(2131755251);
        viewPager.setAdapter(new AvatarPickerPagerAdapter(layoutInflater.getContext()));
        ((PagerSlidingTabStrip)inflate.findViewById(2131755250)).setViewPager(viewPager);
        this.createExtraView(layoutInflater, (FrameLayout)inflate.findViewById(2131755252));
        return inflate;
    }
    
    public void onItemClick(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
        final Object itemAtPosition = adapterView.getItemAtPosition(n);
        if (itemAtPosition instanceof ChatterDisplayInfo) {
            final ChatterID chatterID = ((ChatterDisplayInfo)itemAtPosition).getChatterID(ActivityUtils.getUserManager(this.getArguments()));
            if (chatterID != null) {
                this.onAvatarSelected(chatterID, ((ChatterDisplayInfo)itemAtPosition).getDisplayName());
            }
        }
    }
    
    public class AvatarPickerPagerAdapter extends PagerAdapter
    {
        private final Context context;
        
        public AvatarPickerPagerAdapter(final Context context) {
            this.context = context;
        }
        
        @Override
        public void destroyItem(final ViewGroup viewGroup, final int n, final Object o) {
            if (o instanceof View) {
                if (o instanceof ListView) {
                    ((ListView)o).setAdapter((ListAdapter)null);
                }
                viewGroup.removeView((View)o);
            }
        }
        
        @Override
        public int getCount() {
            return ContactListType.values().length;
        }
        
        @Override
        public CharSequence getPageTitle(final int n) {
            if (n >= 0 && n < ContactListType.values().length) {
                return ContactListType.values()[n].toString();
            }
            return null;
        }
        
        @Override
        public Object instantiateItem(final ViewGroup viewGroup, final int n) {
            if (n >= 0 && n < ContactListType.values().length) {
                final ContactListType contactListType = ContactListType.values()[n];
                final ListView listView = new ListView(this.context);
                listView.setOnItemClickListener((AdapterView$OnItemClickListener)AvatarPickerFragment.this);
                listView.setAdapter(AvatarPickerFragment.this.createListAdapter(AvatarPickerFragment.this.getContext(), ActivityUtils.getUserManager(AvatarPickerFragment.this.getArguments()), contactListType));
                viewGroup.addView((View)listView);
                return listView;
            }
            return null;
        }
        
        @Override
        public boolean isViewFromObject(final View view, final Object o) {
            return view == o;
        }
    }
    
    private enum ContactListType
    {
        Friends("Friends", 1, 2130837630), 
        Nearby("Nearby", 2, 2130837635), 
        Recent("Recent", 0, 2130837629);
        
        public final int drawableId;
        
        private ContactListType(final String name, final int ordinal, final int drawableId) {
            this.drawableId = drawableId;
        }
    }
    
    private static class UsersOnlyPredicate implements Predicate<ChatterDisplayData>
    {
        @Override
        public boolean apply(@Nullable final ChatterDisplayData chatterDisplayData) {
            return chatterDisplayData != null && chatterDisplayData.chatterID instanceof ChatterID.ChatterIDUser && chatterDisplayData.chatterID.isValidUUID();
        }
    }
}
