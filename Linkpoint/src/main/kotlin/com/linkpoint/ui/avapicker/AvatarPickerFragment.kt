package com.linkpoint.ui.avapicker
import java.util.*

import android.content.Context
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
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
import javax.annotation.Nonnull
import javax.annotation.Nullable

abstract class AvatarPickerFragment : FragmentWithTitle() : AdapterView.OnItemClickListener {

    /* renamed from: -com-lumiyaviewer-lumiya-ui-avapicker-AvatarPickerFragment$ContactListTypeSwitchesValues  reason: not valid java name */
    private const val /* synthetic */ IntArray f234comlumiyaviewerlumiyauiavapickerAvatarPickerFragment$ContactListTypeSwitchesValues = null

    class AvatarPickerPagerAdapter : PagerAdapter() {
        private val Context context

        public AvatarPickerPagerAdapter(Context context2) {
            this.context = context2
        }

        fun destroyItem(viewGroup: ViewGroup, i: Int, obj: Object) {
            if (obj instanceof View) {
                if (obj instanceof ListView) {
                    ((ListView) obj).setAdapter((ListAdapter) null)
                }
                viewGroup.removeView((View) obj)
            }
        }

         public fun getCount(): Int {
            return ContactListType.values().length
        }

         public fun getPageTitle(i: Int): CharSequence {
            if (i < 0 || i >= ContactListType.values().length) {
                return null
            }
            return ContactListType.values()[i].toString()
        }

         public fun instantiateItem(viewGroup: ViewGroup, i: Int): Object {
            if (i < 0 || i >= ContactListType.values().length) {
                return null
            }
            val contactListType: ContactListType = ContactListType.values()[i]
            val listView: ListView = ListView(this.context)
            listView.setOnItemClickListener(AvatarPickerFragment.this)
            listView.setAdapter(AvatarPickerFragment.this.createListAdapter(AvatarPickerFragment.this.getContext(), ActivityUtils.getUserManager(AvatarPickerFragment.this.getArguments()), contactListType))
            viewGroup.addView(listView)
            return listView
        }

         public fun isViewFromObject(view: View, obj: Object): Boolean {
            return view == obj
        }
    }

    private enum ContactListType {
        Recent(R.drawable.ic_tab_card),
        Friends(R.drawable.ic_tab_contacts),
        Nearby(R.drawable.ic_tab_target)
        
        val Int drawableId

        private ContactListType(Int i) {
            this.drawableId = i
        }
    }

    @JvmStatic
private class UsersOnlyPredicate : Predicate<ChatterDisplayData> {
        private UsersOnlyPredicate() {
        }

        /* synthetic */ UsersOnlyPredicate(UsersOnlyPredicate usersOnlyPredicate) {
            this()
        }

         public fun apply(chatterDisplayData: ChatterDisplayData): Boolean {
            return chatterDisplayData != null && (chatterDisplayData.chatterID instanceof ChatterID.ChatterIDUser) && chatterDisplayData.chatterID.isValidUUID()
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-ui-avapicker-AvatarPickerFragment$ContactListTypeSwitchesValues  reason: not valid java name */
    @JvmStatic
private /* synthetic */ IntArray m388getcomlumiyaviewerlumiyauiavapickerAvatarPickerFragment$ContactListTypeSwitchesValues() {
        if (f234comlumiyaviewerlumiyauiavapickerAvatarPickerFragment$ContactListTypeSwitchesValues != null) {
            return f234comlumiyaviewerlumiyauiavapickerAvatarPickerFragment$ContactListTypeSwitchesValues
        }
        val iArr: IntArray = Int[ContactListType.values().length]
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
     public fun createListAdapter(context: Context, userManager: UserManager, contactListType: ContactListType): ListAdapter {
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
    fun createExtraView(layoutInflater: LayoutInflater, frameLayout: FrameLayout) {
    }

    public abstract String getTitle()

    /* access modifiers changed from: protected */
    public abstract Unit onAvatarSelected(ChatterID chatterID, String str)

     public fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup, bundle: Bundle): View {
        val inflate: View = layoutInflater.inflate(R.layout.avatar_picker, viewGroup, false)
        val viewPager: ViewPager = (ViewPager) inflate.findViewById(R.id.avatar_picker_pager)
        viewPager.setAdapter(AvatarPickerPagerAdapter(layoutInflater.getContext()))
        ((PagerSlidingTabStrip) inflate.findViewById(R.id.avatar_picker_tabs)).setViewPager(viewPager)
        createExtraView(layoutInflater, (FrameLayout) inflate.findViewById(R.id.avatar_picker_extra_content))
        return inflate
    }

    fun onItemClick(adapterView: AdapterView<?>, view: View, i: Int, j: Long) {
        ChatterID chatterID
        val itemAtPosition: Object = adapterView.getItemAtPosition(i)
        if ((itemAtPosition instanceof ChatterDisplayInfo) && (chatterID = ((ChatterDisplayInfo) itemAtPosition).getChatterID(ActivityUtils.getUserManager(getArguments()))) != null) {
            onAvatarSelected(chatterID, ((ChatterDisplayInfo) itemAtPosition).getDisplayName())
        }
    }
}
