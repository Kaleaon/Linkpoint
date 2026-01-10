package com.linkpoint.ui.chat.contacts
import java.util.*

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListAdapter
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.ChatterDisplayInfo

abstract class ChatterListAdapter : BaseAdapter : ListAdapter {
    protected Context context
    private LayoutInflater inflater
    private Boolean userDistanceInline = true
    protected UserManager userManager
    private ChatterItemViewBuilder viewBuilder = ChatterItemViewBuilder()

    ChatterListAdapter(Context context2, UserManager userManager2) {
        this.context = context2
        this.userManager = userManager2
        this.inflater = LayoutInflater.from(context2)
    }

    fun getView(Int i, View view, ViewGroup viewGroup): View {
        Any item = getItem(i)
        if (!(item is ChatterDisplayInfo)) {
            return null
        }
        this.viewBuilder.reset()
        ((ChatterDisplayInfo) item).buildView(this.context, this.viewBuilder, this.userManager)
        return this.viewBuilder.getView(this.inflater, view, viewGroup, this.userDistanceInline)
    }

    /* access modifiers changed from: package-private */
    fun setUserDistanceInline(Boolean z)  {
        this.userDistanceInline = z
    }
}
