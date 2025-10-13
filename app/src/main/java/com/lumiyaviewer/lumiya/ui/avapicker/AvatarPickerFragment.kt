package com.lumiyaviewer.lumiya.ui.avapicker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.ListAdapter
import android.widget.ListView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.astuetz.PagerSlidingTabStrip
import com.google.common.base.Predicate
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterDisplayData
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatterListSubscriptionAdapter
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle

abstract class AvatarPickerFragment : FragmentWithTitle(), AdapterView.OnItemClickListener {

    abstract override fun getTitle(): String

    protected abstract fun onAvatarSelected(chatterID: ChatterID, displayName: String?)

    protected open fun createExtraView(inflater: LayoutInflater, container: FrameLayout) {
        // Default implementation does nothing
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.avatar_picker, container, false)
        
        val viewPager = view.findViewById<ViewPager>(R.id.avatar_picker_pager)
        viewPager.adapter = AvatarPickerPagerAdapter(inflater.context)
        
        view.findViewById<PagerSlidingTabStrip>(R.id.avatar_picker_tabs)?.setViewPager(viewPager)
        
        val extraContent = view.findViewById<FrameLayout>(R.id.avatar_picker_extra_content)
        createExtraView(inflater, extraContent)
        
        return view
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val item = parent.getItemAtPosition(position)
        if (item is ChatterDisplayInfo) {
            val userManager = ActivityUtils.getUserManager(arguments)
            val chatterID = item.getChatterID(userManager)
            chatterID?.let {
                onAvatarSelected(it, item.displayName)
            }
        }
    }

    private fun createListAdapter(
        context: Context,
        userManager: UserManager,
        contactListType: ContactListType
    ): ListAdapter {
        return when (contactListType) {
            ContactListType.Friends -> {
                ChatterListSubscriptionAdapter(context, userManager, ChatterListType.Friends)
            }
            ContactListType.Nearby -> {
                ChatterListSubscriptionAdapter(context, userManager, ChatterListType.Nearby)
            }
            ContactListType.Recent -> {
                ChatterListSubscriptionAdapter(
                    context,
                    userManager,
                    ChatterListType.Active,
                    UsersOnlyPredicate()
                )
            }
        }
    }

    // Inner class: AvatarPickerPagerAdapter
    inner class AvatarPickerPagerAdapter(private val context: Context) : PagerAdapter() {

        override fun getCount(): Int = ContactListType.values().size

        override fun getPageTitle(position: Int): CharSequence? {
            return ContactListType.values().getOrNull(position)?.name
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val contactListType = ContactListType.values().getOrNull(position) 
                ?: throw IllegalArgumentException("Invalid position: $position")
            
            val listView = ListView(context).apply {
                onItemClickListener = this@AvatarPickerFragment
                adapter = createListAdapter(
                    context,
                    ActivityUtils.getUserManager(arguments),
                    contactListType
                )
            }
            
            container.addView(listView)
            return listView
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            if (obj is View) {
                if (obj is ListView) {
                    obj.adapter = null
                }
                container.removeView(obj)
            }
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj
        }
    }

    // Inner enum: ContactListType
    private enum class ContactListType(val drawableId: Int) {
        Recent(R.drawable.ic_tab_card),
        Friends(R.drawable.ic_tab_contacts),
        Nearby(R.drawable.ic_tab_target)
    }

    // Inner class: UsersOnlyPredicate
    private class UsersOnlyPredicate : Predicate<ChatterDisplayData> {
        override fun apply(data: ChatterDisplayData?): Boolean {
            return data != null && 
                data.chatterID is ChatterID.ChatterIDUser && 
                data.chatterID.isValidUUID()
        }
    }
}
