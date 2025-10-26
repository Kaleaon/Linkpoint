package com.linkpoint.ui.chat.contacts
import java.util.*

import android.content.Context
import com.google.common.collect.ImmutableList
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.ChatterDisplayInfo
import javax.annotation.Nullable

class ChatterListSimpleAdapter : ChatterListAdapter() {
    private ImmutableList<? : ChatterDisplayInfo> data = null

    ChatterListSimpleAdapter(Context context, UserManager userManager) {
        super(context, userManager)
    }

     public fun areAllItemsEnabled(): Boolean {
        return true
    }

     public fun getCount(): Int {
        if (this.data != null) {
            return this.data.size()
        }
        return 0
    }

     public fun getItem(i: Int): Object {
        if (this.data == null || i < 0 || i >= this.data.size()) {
            return null
        }
        return this.data.get(i)
    }

     public fun getItemId(i: Int): Long {
        return 0
    }

     public fun hasStableIds(): Boolean {
        return false
    }

     public fun isEmpty(): Boolean {
        if (this.data != null) {
            return this.data.isEmpty()
        }
        return true
    }

     public fun isEnabled(i: Int): Boolean {
        return true
    }

    /* access modifiers changed from: protected */
    fun setData(immutableList: ImmutableList<? : ChatterDisplayInfo>) {
        this.data = immutableList
        notifyDataSetChanged()
    }
}
