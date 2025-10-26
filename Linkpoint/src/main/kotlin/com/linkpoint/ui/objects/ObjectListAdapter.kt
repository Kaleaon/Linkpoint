package com.linkpoint.ui.objects
import java.util.*

import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.common.collect.ImmutableList
import com.linkpoint.R
import com.linkpoint.slproto.objects.SLAvatarObjectDisplayInfo
import com.linkpoint.slproto.objects.SLObjectDisplayInfo
import com.linkpoint.slproto.objects.SLPrimObjectDisplayInfo
import javax.annotation.Nonnull

class ObjectListAdapter : BaseExpandableListAdapter() {
    private const val HIERARCHY_PADDING_DP: Int = 10
    private val Context context
    private ImmutableList<SLObjectDisplayInfo> objects = ImmutableList.of()

    public ObjectListAdapter(Context context2) {
        this.context = context2
    }

     public fun getChild(i: Int, i2: Int): SLObjectDisplayInfo {
        val sLObjectDisplayInfo: SLObjectDisplayInfo = (SLObjectDisplayInfo) this.objects.get(i)
        if (sLObjectDisplayInfo instanceof SLObjectDisplayInfo.HasChildrenObjects) {
            return (SLObjectDisplayInfo) ((SLObjectDisplayInfo.HasChildrenObjects) sLObjectDisplayInfo).getChildren().get(i2)
        }
        return null
    }

     public fun getChildId(i: Int, i2: Int): Long {
        return (Long) getChild(i, i2).localID
    }

     public fun getChildView(i: Int, i2: Int, z: Boolean, view: View, viewGroup: ViewGroup): View {
        val view2: View = getView(getChild(i, i2), view, viewGroup)
        view2.findViewById(R.id.groupIndicatorCollapsed).setVisibility(8)
        view2.findViewById(R.id.groupIndicatorExpanded).setVisibility(4)
        view2.findViewById(R.id.groupIndicatorCollapsed).setOnClickListener((View.OnClickListener) null)
        view2.findViewById(R.id.groupIndicatorExpanded).setOnClickListener((View.OnClickListener) null)
        return view2
    }

     public fun getChildrenCount(i: Int): Int {
        val sLObjectDisplayInfo: SLObjectDisplayInfo = (SLObjectDisplayInfo) this.objects.get(i)
        if (sLObjectDisplayInfo instanceof SLObjectDisplayInfo.HasChildrenObjects) {
            return ((SLObjectDisplayInfo.HasChildrenObjects) sLObjectDisplayInfo).getChildren().size()
        }
        return 0
    }

    public ImmutableList<SLObjectDisplayInfo> getData() {
        return this.objects
    }

     public fun getGroup(i: Int): SLObjectDisplayInfo {
        return (SLObjectDisplayInfo) this.objects.get(i)
    }

     public fun getGroupCount(): Int {
        return this.objects.size()
    }

     public fun getGroupId(i: Int): Long {
        return (Long) getGroup(i).localID
    }

     public fun getGroupView(final Int i, z: Boolean, view: View, viewGroup: ViewGroup): View {
        val view2: View = getView(getGroup(i), view, viewGroup)
        if (getChildrenCount(i) == 0) {
            view2.findViewById(R.id.groupIndicatorCollapsed).setVisibility(4)
            view2.findViewById(R.id.groupIndicatorExpanded).setVisibility(8)
        } else if (z) {
            view2.findViewById(R.id.groupIndicatorCollapsed).setVisibility(8)
            view2.findViewById(R.id.groupIndicatorExpanded).setVisibility(0)
        } else {
            view2.findViewById(R.id.groupIndicatorCollapsed).setVisibility(0)
            view2.findViewById(R.id.groupIndicatorExpanded).setVisibility(8)
        }
        if (viewGroup instanceof ExpandableListView) {
            final ExpandableListView expandableListView = (ExpandableListView) viewGroup
            val r1: AnonymousClass1 = View.OnClickListener() {
                fun onClick(view: View) {
                    if (view.getVisibility() == 0) {
                        switch (view.getId()) {
                            case R.id.groupIndicatorCollapsed:
                                if (Build.VERSION.SDK_INT >= 14) {
                                    expandableListView.expandGroup(i, true)
                                    return
                                } else {
                                    expandableListView.expandGroup(i)
                                    return
                                }
                            case R.id.groupIndicatorExpanded:
                                expandableListView.collapseGroup(i)
                                return
                            default:
                                return
                        }
                    }
                }
            }
            view2.findViewById(R.id.groupIndicatorCollapsed).setOnClickListener(r1)
            view2.findViewById(R.id.groupIndicatorExpanded).setOnClickListener(r1)
        } else {
            view2.findViewById(R.id.groupIndicatorCollapsed).setOnClickListener((View.OnClickListener) null)
            view2.findViewById(R.id.groupIndicatorExpanded).setOnClickListener((View.OnClickListener) null)
        }
        return view2
    }

     public fun getView(sLObjectDisplayInfo: SLObjectDisplayInfo, view: View, viewGroup: ViewGroup): View {
        val str: String = null
        val i: Int = 0
        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(R.layout.object_list_item, viewGroup, false)
        }
        view.findViewById(R.id.object_hierarchy_padding).setLayoutParams(LinearLayout.LayoutParams((Int) (TypedValue.applyDimension(1, 10.0f, this.context.getResources().getDisplayMetrics()) * ((Float) sLObjectDisplayInfo.hierarchyLevel)), -1))
        view.findViewById(R.id.avatarIconView).setVisibility(sLObjectDisplayInfo instanceof SLAvatarObjectDisplayInfo ? 0 : 8)
        if (sLObjectDisplayInfo.name != null) {
            ((TextView) view.findViewById(R.id.objectNameTextView)).setText(sLObjectDisplayInfo.name)
        } else {
            ((TextView) view.findViewById(R.id.objectNameTextView)).setText(R.string.object_name_loading)
        }
        val textView: TextView = (TextView) view.findViewById(R.id.objectDistanceTextView)
        if (!Float.isNaN(sLObjectDisplayInfo.distance)) {
            str = String.format("%d m", Array<Any>{Integer.valueOf(Math.round(sLObjectDisplayInfo.distance))})
        }
        textView.setText(str)
        if (sLObjectDisplayInfo instanceof SLPrimObjectDisplayInfo) {
            val sLPrimObjectDisplayInfo: SLPrimObjectDisplayInfo = (SLPrimObjectDisplayInfo) sLObjectDisplayInfo
            view.findViewById(R.id.touchIconView).setVisibility(sLPrimObjectDisplayInfo.touchable ? 0 : 4)
            val findViewById: View = view.findViewById(R.id.payIconView)
            if (!sLPrimObjectDisplayInfo.payable) {
                i = 4
            }
            findViewById.setVisibility(i)
        } else {
            view.findViewById(R.id.touchIconView).setVisibility(4)
            view.findViewById(R.id.payIconView).setVisibility(4)
        }
        return view
    }

     public fun hasStableIds(): Boolean {
        return true
    }

     public fun isChildSelectable(i: Int, i2: Int): Boolean {
        return true
    }

    fun setData(immutableList: ImmutableList<SLObjectDisplayInfo>) {
        this.objects = immutableList
        notifyDataSetChanged()
    }
}
