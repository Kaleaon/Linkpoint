package com.lumiyaviewer.lumiya.ui.chat.contacts
import java.util.*

import android.content.Context
import com.google.common.collect.ImmutableList
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo
import androidx.annotation.Nullable

class ChatterListSimpleAdapter : ChatterListAdapter {
    @Nullable
    private ImmutableList<? : ChatterDisplayInfo> data = null

    ChatterListSimpleAdapter(Context context, UserManager userManager) {
        super(context, userManager)
    }

    Boolean areAllItemsEnabled() {
        return true
    }

    Int getCount() {
        if (this.data != null) {
            return this.data.size()
        }
        return 0
    }

    Any getItem(Int i) {
        if (this.data == null || i < 0 || i >= this.data.size()) {
            return null
        }
        return this.data.get(i)
    }

    Long getItemId(Int i) {
        return 0
    }

    Boolean hasStableIds() {
        return false
    }

    Boolean isEmpty() {
        if (this.data != null) {
            return this.data.isEmpty()
        }
        return true
    }

    Boolean isEnabled(Int i) {
        return true
    }

    /* access modifiers changed from: protected */
    Unit setData(@Nullable ImmutableList<? : ChatterDisplayInfo> immutableList) {
        this.data = immutableList
        notifyDataSetChanged()
    }
}
