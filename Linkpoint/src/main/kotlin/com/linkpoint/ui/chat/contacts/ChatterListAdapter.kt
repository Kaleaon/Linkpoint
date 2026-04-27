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

abstract class ChatterListAdapter : BaseAdapter() : ListAdapter {
    protected val Context context
    private val LayoutInflater inflater
    private Boolean userDistanceInline = true
    protected val UserManager userManager
    private val ChatterItemViewBuilder viewBuilder = ChatterItemViewBuilder()

    ChatterListAdapter(Context context2, UserManager userManager2) {
        this.context = context2
        this.userManager = userManager2
        this.inflater = LayoutInflater.from(context2)
    }

     public fun getView(i: Int, view: View, viewGroup: ViewGroup): View {
        val item: Object = getItem(i)
        if (!(item instanceof ChatterDisplayInfo)) {
            return null
        }
        this.viewBuilder.reset()
        ((ChatterDisplayInfo) item).buildView(this.context, this.viewBuilder, this.userManager)
        return this.viewBuilder.getView(this.inflater, view, viewGroup, this.userDistanceInline)
    }

    /* access modifiers changed from: package-private */
    fun setUserDistanceInline(z: Boolean) {
        this.userDistanceInline = z
    }
}
