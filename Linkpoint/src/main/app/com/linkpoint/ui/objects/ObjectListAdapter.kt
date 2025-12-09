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
import androidx.annotation.NonNull

class ObjectListAdapter : BaseExpandableListAdapter {
    private val HIERARCHY_PADDING_DP: Int = 10
    private Context context
    @NonNull
    private ImmutableList<SLObjectDisplayInfo> objects = ImmutableList.of()

    ObjectListAdapter(Context context2) {
        this.context = context2
    }

    fun getChild(Int i, Int i2): SLObjectDisplayInfo {
        SLObjectDisplayInfo sLObjectDisplayInfo = (SLObjectDisplayInfo) this.objects.get(i)
        if (sLObjectDisplayInfo instanceof SLObjectDisplayInfo.HasChildrenObjects) {
            return (SLObjectDisplayInfo) ((SLObjectDisplayInfo.HasChildrenObjects) sLObjectDisplayInfo).getChildren().get(i2)
        }
        return null
    }

    fun getChildId(Int i, Int i2): Long {
        return (Long) getChild(i, i2).localID
    }

    fun getChildView(Int i, Int i2, Boolean z, View view, ViewGroup viewGroup): View {
        View view2 = getView(getChild(i, i2), view, viewGroup)
        view2.findViewById(R.id.groupIndicatorCollapsed).setVisibility(8)
        view2.findViewById(R.id.groupIndicatorExpanded).setVisibility(4)
        view2.findViewById(R.id.groupIndicatorCollapsed).setOnClickListener((View.OnClickListener) null)
        view2.findViewById(R.id.groupIndicatorExpanded).setOnClickListener((View.OnClickListener) null)
        return view2
    }

    fun getChildrenCount(Int i): Int {
        SLObjectDisplayInfo sLObjectDisplayInfo = (SLObjectDisplayInfo) this.objects.get(i)
        if (sLObjectDisplayInfo instanceof SLObjectDisplayInfo.HasChildrenObjects) {
            return ((SLObjectDisplayInfo.HasChildrenObjects) sLObjectDisplayInfo).getChildren().size()
        }
        return 0
    }

    @NonNull
    fun getData(): ImmutableList<SLObjectDisplayInfo> {
        return this.objects
    }

    fun getGroup(Int i): SLObjectDisplayInfo {
        return (SLObjectDisplayInfo) this.objects.get(i)
    }

    fun getGroupCount(): Int {
        return this.objects.size()
    }

    fun getGroupId(Int i): Long {
        return (Long) getGroup(i).localID
    }

    fun getGroupView(Int i, Boolean z, View view, ViewGroup viewGroup): View {
        View view2 = getView(getGroup(i), view, viewGroup)
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
            ExpandableListView expandableListView = (ExpandableListView) viewGroup
            AnonymousClass1 r1 = View.OnClickListener() {
                fun onClick(View view): Unit {
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

    fun getView(SLObjectDisplayInfo sLObjectDisplayInfo, View view, ViewGroup viewGroup): View {
        String str = null
        Int i = 0
        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(R.layout.object_list_item, viewGroup, false)
        }
        view.findViewById(R.id.object_hierarchy_padding).setLayoutParams(LinearLayout.LayoutParams((Int) (TypedValue.applyDimension(1, 10.0f, this.context.getResources().getDisplayMetrics()) * (sLObjectDisplayInfo.toFloat().hierarchyLevel)), -1))
        view.findViewById(R.id.avatarIconView).setVisibility(sLObjectDisplayInfo instanceof SLAvatarObjectDisplayInfo ? 0 : 8)
        if (sLObjectDisplayInfo.name != null) {
            ((TextView) view.findViewById(R.id.objectNameTextView)).setText(sLObjectDisplayInfo.name)
        } else {
            ((TextView) view.findViewById(R.id.objectNameTextView)).setText(R.string.object_name_loading)
        }
        TextView textView = (TextView) view.findViewById(R.id.objectDistanceTextView)
        if (!Float.isNaN(sLObjectDisplayInfo.distance)) {
            str = String.format("%d m", Any[]{Int.valueOf(Math.round(sLObjectDisplayInfo.distance))})
        }
        textView.setText(str)
        if (sLObjectDisplayInfo instanceof SLPrimObjectDisplayInfo) {
            SLPrimObjectDisplayInfo sLPrimObjectDisplayInfo = (SLPrimObjectDisplayInfo) sLObjectDisplayInfo
            view.findViewById(R.id.touchIconView).setVisibility(sLPrimObjectDisplayInfo.touchable ? 0 : 4)
            View findViewById = view.findViewById(R.id.payIconView)
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

    fun hasStableIds(): Boolean {
        return true
    }

    fun isChildSelectable(Int i, Int i2): Boolean {
        return true
    }

    fun setData(@NonNull ImmutableList<SLObjectDisplayInfo> immutableList): Unit {
        this.objects = immutableList
        notifyDataSetChanged()
    }
}
