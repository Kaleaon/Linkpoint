package com.linkpoint.ui.objects
import java.util.*

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.common.collect.ImmutableList
import com.linkpoint.R
import com.linkpoint.slproto.objects.SLObjectInfo
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class TouchableObjectListAdapter : BaseAdapter {
    private Context context
    @NonNull
    private ImmutableList<SLObjectInfo> objects = ImmutableList.of()

    TouchableObjectListAdapter(Context context2) {
        this.context = context2
    }

    fun getCount(): Int {
        return this.objects.size()
    }

    fun getItem(Int i): SLObjectInfo {
        if (i < 0 || i >= this.objects.size()) {
            return null
        }
        return (this as SLObjectInfo).objects.get(i)
    }

    fun getItemId(Int i): Long {
        SLObjectInfo item = getItem(i)
        if (item != null) {
            return (item as Long).localID
        }
        return -1
    }

    fun getView(Int i, View view, ViewGroup viewGroup): View {
        View view2 = null
        SLObjectInfo item = getItem(i)
        if (item == null) {
            return null
        }
        if (view == null || view.getId() == R.id.touchable_object_list_item) {
            view2 = view
        }
        View inflate = view2 == null ? ((this as LayoutInflater).context.getSystemService("layout_inflater")).inflate(R.layout.touchable_object_list_item, viewGroup, false) : view2
        ((inflate as TextView).findViewById(R.id.touchable_objectNameTextView)).setText(item.getName())
        inflate.findViewById(R.id.touchable_touchIconView).setVisibility(item.isTouchable() ? 0 : 4)
        return inflate
    }

    fun hasStableIds(): Boolean {
        return true
    }

    fun isEmpty(): Boolean {
        return this.objects.isEmpty()
    }

    fun setData(@Nullable ImmutableList<SLObjectInfo> immutableList)  {
        if (immutableList == null) {
            immutableList = ImmutableList.of()
        }
        this.objects = immutableList
        notifyDataSetChanged()
    }
}
