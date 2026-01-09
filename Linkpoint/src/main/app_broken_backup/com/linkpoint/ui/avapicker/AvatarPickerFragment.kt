package com.linkpoint.ui.avapicker
import java.util.*

import android.content.Context
import android.os.Bundle
import androidx.core.view.PagerAdapter
import androidx.core.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.ListAdapter
import android.widget.ListView
import com.astuetz.PagerSlidingTabStrip
import com.google.common.base.Predicate
import com.linkpoint.R
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.manager.ChatterDisplayData
import com.linkpoint.slproto.users.manager.ChatterListType
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.ChatterDisplayInfo
import com.linkpoint.ui.chat.contacts.ChatterListSubscriptionAdapter
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.FragmentWithTitle
import androidx.annotation.NonNull
import androidx.annotation.Nullable

abstract class AvatarPickerFragment : FragmentWithTitle : AdapterView.OnItemClickListener {

    /* renamed from: -com-lumiyaviewer-lumiya-ui-avapicker-AvatarPickerFragment$ContactListTypeSwitchesValues  reason: not valid java name */
    private /* synthetic */ IntArray f234comlumiyaviewerlumiyauiavapickerAvatarPickerFragment$ContactListTypeSwitchesValues = null

    class AvatarPickerPagerAdapter : PagerAdapter {
        private Context context

        AvatarPickerPagerAdapter(Context context2) {
            this.context = context2
        }

        fun destroyItem(ViewGroup viewGroup, Int i, Any obj)  {
            if (obj is View) {
                if (obj is ListView) {
                    ((ListView) obj).setAdapter((ListAdapter) null)
                }
                viewGroup.removeView((View) obj)
            }
        }

        fun getCount(): Int {
            return ContactListType.values().size
        }

        fun getPageTitle(Int i): CharSequence {
            if (i < 0 || i >= ContactListType.values().size) {
                return null
            }
            return ContactListType.values()[i].toString()
        }

        fun instantiateItem(ViewGroup viewGroup, Int i): Any {
            if (i < 0 || i >= ContactListType.values().size) {
                return null
            }
            ContactListType contactListType = ContactListType.values()[i]
            ListView listView = ListView(this.context)
            listView.setOnItemClickListener(AvatarPickerFragment.this)
            listView.setAdapter(AvatarPickerFragment.this.createListAdapter(AvatarPickerFragment.this.getContext(), ActivityUtils.getUserManager(AvatarPickerFragment.this.getArguments()), contactListType))
            viewGroup.addView(listView)
            return listView
        }

        fun isViewFromObject(View view, Any obj): Boolean {
            return view == obj
        }
    }

    private enum ContactListType {
        Recent(R.drawable.ic_tab_card),
        Friends(R.drawable.ic_tab_contacts),
        Nearby(R.drawable.ic_tab_target)
        
        Int drawableId

        private ContactListType(Int i) {
            this.drawableId = i
        }
    }

    private class UsersOnlyPredicate : Predicate<ChatterDisplayData> {
        private UsersOnlyPredicate() {
        }

        /* synthetic */ UsersOnlyPredicate(UsersOnlyPredicate usersOnlyPredicate) {
            this()
        }

        fun apply(@Nullable ChatterDisplayData chatterDisplayData): Boolean {
            return chatterDisplayData != null && (chatterDisplayData.chatterID is ChatterID.ChatterIDUser) && chatterDisplayData.chatterID.isValidUUID()
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-ui-avapicker-AvatarPickerFragment$ContactListTypeSwitchesValues  reason: not valid java name */
    private /* synthetic */ IntArray m388getcomlumiyaviewerlumiyauiavapickerAvatarPickerFragment$ContactListTypeSwitchesValues() {
        if (f234comlumiyaviewerlumiyauiavapickerAvatarPickerFragment$ContactListTypeSwitchesValues != null) {
            return f234comlumiyaviewerlumiyauiavapickerAvatarPickerFragment$ContactListTypeSwitchesValues
        }
        IntArray iArr = Int[ContactListType.values().size]
        try {
            iArr[ContactListType.Friends.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[ContactListType.Nearby.ordinal()] = 2
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[ContactListType.Recent.ordinal()] = 3
        } catch (NoSuchFieldError e3) {
        }
        f234comlumiyaviewerlumiyauiavapickerAvatarPickerFragment$ContactListTypeSwitchesValues = iArr
        return iArr
    }

    /* access modifiers changed from: private */
    @NonNull
    fun createListAdapter(Context context, UserManager userManager, @NonNull ContactListType contactListType): ListAdapter {
        switch (m388getcomlumiyaviewerlumiyauiavapickerAvatarPickerFragment$ContactListTypeSwitchesValues()[contactListType.ordinal()]) {
            case 1:
                return ChatterListSubscriptionAdapter(context, userManager, ChatterListType.Friends)
            case 2:
                return ChatterListSubscriptionAdapter(context, userManager, ChatterListType.Nearby)
            case 3:
                return ChatterListSubscriptionAdapter(context, userManager, ChatterListType.Active, UsersOnlyPredicate((UsersOnlyPredicate) null))
            default:
                throw IllegalArgumentException("Unknown contact list type")
        }
    }

    /* access modifiers changed from: protected */
    fun createExtraView(LayoutInflater layoutInflater, FrameLayout frameLayout)  {
    }

    abstract String getTitle()

    /* access modifiers changed from: protected */
    abstract Unit onAvatarSelected(ChatterID chatterID, @Nullable String str)

    fun onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle): View {
        View inflate = layoutInflater.inflate(R.layout.avatar_picker, viewGroup, false)
        ViewPager viewPager = (inflate as ViewPager).findViewById(R.id.avatar_picker_pager)
        viewPager.setAdapter(AvatarPickerPagerAdapter(layoutInflater.getContext()))
        ((inflate as PagerSlidingTabStrip).findViewById(R.id.avatar_picker_tabs)).setViewPager(viewPager)
        createExtraView(layoutInflater, (inflate as FrameLayout).findViewById(R.id.avatar_picker_extra_content))
        return inflate
    }

    fun onItemClick(AdapterView<?> adapterView, View view, Int i, Long j)  {
        ChatterID chatterID
        Any itemAtPosition = adapterView.getItemAtPosition(i)
        if ((itemAtPosition is ChatterDisplayInfo) && (chatterID = ((ChatterDisplayInfo) itemAtPosition).getChatterID(ActivityUtils.getUserManager(getArguments()))) != null) {
            onAvatarSelected(chatterID, ((ChatterDisplayInfo) itemAtPosition).getDisplayName())
        }
    }
}
