package com.lumiyaviewer.lumiya.ui.avapicker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.astuetz.PagerSlidingTabStrip;
import com.google.common.base.Predicate;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterDisplayData;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatterListSubscriptionAdapter;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AvatarPickerFragment extends FragmentWithTitle implements AdapterView.OnItemClickListener {

    /* renamed from: -com-lumiyaviewer-lumiya-ui-avapicker-AvatarPickerFragment$ContactListTypeSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f234comlumiyaviewerlumiyauiavapickerAvatarPickerFragment$ContactListTypeSwitchesValues = null;

    public class AvatarPickerPagerAdapter extends PagerAdapter {
        private final Context context;

        public AvatarPickerPagerAdapter(Context context2) {
            this.context = context2;
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            if (obj instanceof View) {
                if (obj instanceof ListView) {
                    ((ListView) obj).setAdapter((ListAdapter) null);
                }
                viewGroup.removeView((View) obj);
            }
        }

        public int getCount() {
            return ContactListType.values().length;
        }

        public CharSequence getPageTitle(int i) {
            if (i < 0 || i >= ContactListType.values().length) {
                return null;
            }
            return ContactListType.values()[i].toString();
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            if (i < 0 || i >= ContactListType.values().length) {
                return null;
            }
            ContactListType contactListType = ContactListType.values()[i];
            ListView listView = new ListView(this.context);
            listView.setOnItemClickListener(AvatarPickerFragment.this);
            listView.setAdapter(AvatarPickerFragment.this.createListAdapter(AvatarPickerFragment.this.getContext(), ActivityUtils.getUserManager(AvatarPickerFragment.this.getArguments()), contactListType));
            viewGroup.addView(listView);
            return listView;
        }

        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }
    }

    private enum ContactListType {
        Recent(R.drawable.ic_tab_card),
        Friends(R.drawable.ic_tab_contacts),
        Nearby(R.drawable.ic_tab_target);
        
        public final int drawableId;

        private ContactListType(int i) {
            this.drawableId = i;
        }
    }

    private static class UsersOnlyPredicate implements Predicate<ChatterDisplayData> {
        private UsersOnlyPredicate() {
        }

        /* synthetic */ UsersOnlyPredicate(UsersOnlyPredicate usersOnlyPredicate) {
            this();
        }

        public boolean apply(@Nullable ChatterDisplayData chatterDisplayData) {
            return chatterDisplayData != null && (chatterDisplayData.chatterID instanceof ChatterID.ChatterIDUser) && chatterDisplayData.chatterID.isValidUUID();
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-ui-avapicker-AvatarPickerFragment$ContactListTypeSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m388getcomlumiyaviewerlumiyauiavapickerAvatarPickerFragment$ContactListTypeSwitchesValues() {
        if (f234comlumiyaviewerlumiyauiavapickerAvatarPickerFragment$ContactListTypeSwitchesValues != null) {
            return f234comlumiyaviewerlumiyauiavapickerAvatarPickerFragment$ContactListTypeSwitchesValues;
        }
        int[] iArr = new int[ContactListType.values().length];
        try {
            iArr[ContactListType.Friends.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ContactListType.Nearby.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ContactListType.Recent.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        f234comlumiyaviewerlumiyauiavapickerAvatarPickerFragment$ContactListTypeSwitchesValues = iArr;
        return iArr;
    }

    /* access modifiers changed from: private */
    @Nonnull
    public ListAdapter createListAdapter(Context context, UserManager userManager, @Nonnull ContactListType contactListType) {
        switch (m388getcomlumiyaviewerlumiyauiavapickerAvatarPickerFragment$ContactListTypeSwitchesValues()[contactListType.ordinal()]) {
            case 1:
                return new ChatterListSubscriptionAdapter(context, userManager, ChatterListType.Friends);
            case 2:
                return new ChatterListSubscriptionAdapter(context, userManager, ChatterListType.Nearby);
            case 3:
                return new ChatterListSubscriptionAdapter(context, userManager, ChatterListType.Active, new UsersOnlyPredicate((UsersOnlyPredicate) null));
            default:
                throw new IllegalArgumentException("Unknown contact list type");
        }
    }

    /* access modifiers changed from: protected */
    public void createExtraView(LayoutInflater layoutInflater, FrameLayout frameLayout) {
    }

    public abstract String getTitle();

    /* access modifiers changed from: protected */
    public abstract void onAvatarSelected(ChatterID chatterID, @Nullable String str);

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.avatar_picker, viewGroup, false);
        ViewPager viewPager = (ViewPager) inflate.findViewById(R.id.avatar_picker_pager);
        viewPager.setAdapter(new AvatarPickerPagerAdapter(layoutInflater.getContext()));
        ((PagerSlidingTabStrip) inflate.findViewById(R.id.avatar_picker_tabs)).setViewPager(viewPager);
        createExtraView(layoutInflater, (FrameLayout) inflate.findViewById(R.id.avatar_picker_extra_content));
        return inflate;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        ChatterID chatterID;
        Object itemAtPosition = adapterView.getItemAtPosition(i);
        if ((itemAtPosition instanceof ChatterDisplayInfo) && (chatterID = ((ChatterDisplayInfo) itemAtPosition).getChatterID(ActivityUtils.getUserManager(getArguments()))) != null) {
            onAvatarSelected(chatterID, ((ChatterDisplayInfo) itemAtPosition).getDisplayName());
        }
    }
}
