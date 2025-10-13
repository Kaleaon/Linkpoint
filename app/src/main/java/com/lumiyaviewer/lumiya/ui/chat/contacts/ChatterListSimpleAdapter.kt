package com.lumiyaviewer.lumiya.ui.chat.contacts

import android.content.Context
import com.google.common.collect.ImmutableList
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo

internal open class ChatterListSimpleAdapter(
    context: Context,
    userManager: UserManager
) : ChatterListAdapter(context, userManager) {

    private var data: ImmutableList<out ChatterDisplayInfo>? = null

    override fun areAllItemsEnabled(): Boolean = true

    override fun getCount(): Int = data?.size ?: 0

    override fun getItem(position: Int): Any? {
        val currentData = data ?: return null
        if (position < 0 || position >= currentData.size) {
            return null
        }
        return currentData[position]
    }

    override fun getItemId(position: Int): Long = 0

    override fun hasStableIds(): Boolean = false

    override fun isEmpty(): Boolean = data?.isEmpty() ?: true

    override fun isEnabled(position: Int): Boolean = true

    protected fun setData(newData: ImmutableList<out ChatterDisplayInfo>?) {
        data = newData
        notifyDataSetChanged()
    }
}
