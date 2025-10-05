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
import javax.annotation.Nonnull
import javax.annotation.Nullable

class TouchableObjectListAdapter : BaseAdapter() {
    private val Context context
    private ImmutableList<SLObjectInfo> objects = ImmutableList.of()

    TouchableObjectListAdapter(Context context2) {
        this.context = context2
    }

    public Int getCount() {
        return this.objects.size()
    }

    public SLObjectInfo getItem(Int i) {
        if (i < 0 || i >= this.objects.size()) {
            return null
        }
        return (SLObjectInfo) this.objects.get(i)
    }

    public Long getItemId(Int i) {
        SLObjectInfo item = getItem(i)
        if (item != null) {
            return (Long) item.localID
        }
        return -1
    }

    public View getView(Int i, View view, ViewGroup viewGroup) {
        View view2 = null
        SLObjectInfo item = getItem(i)
        if (item == null) {
            return null
        }
        if (view == null || view.getId() == R.id.touchable_object_list_item) {
            view2 = view
        }
        View inflate = view2 == null ? ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(R.layout.touchable_object_list_item, viewGroup, false) : view2
        ((TextView) inflate.findViewById(R.id.touchable_objectNameTextView)).setText(item.getName())
        inflate.findViewById(R.id.touchable_touchIconView).setVisibility(item.isTouchable() ? 0 : 4)
        return inflate
    }

    public Boolean hasStableIds() {
        return true
    }

    public Boolean isEmpty() {
        return this.objects.isEmpty()
    }

    fun setData(ImmutableList<SLObjectInfo> immutableList) {
        if (immutableList == null) {
            immutableList = ImmutableList.of()
        }
        this.objects = immutableList
        notifyDataSetChanged()
    }
}
