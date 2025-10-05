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

    public Boolean areAllItemsEnabled() {
        return true
    }

    public Int getCount() {
        if (this.data != null) {
            return this.data.size()
        }
        return 0
    }

    public Object getItem(Int i) {
        if (this.data == null || i < 0 || i >= this.data.size()) {
            return null
        }
        return this.data.get(i)
    }

    public Long getItemId(Int i) {
        return 0
    }

    public Boolean hasStableIds() {
        return false
    }

    public Boolean isEmpty() {
        if (this.data != null) {
            return this.data.isEmpty()
        }
        return true
    }

    public Boolean isEnabled(Int i) {
        return true
    }

    /* access modifiers changed from: protected */
    public Unit setData(ImmutableList<? : ChatterDisplayInfo> immutableList) {
        this.data = immutableList
        notifyDataSetChanged()
    }
}
