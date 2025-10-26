package com.lumiyaviewer.lumiya.ui.objects
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
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.slproto.objects.SLAvatarObjectDisplayInfo
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectDisplayInfo
import com.lumiyaviewer.lumiya.slproto.objects.SLPrimObjectDisplayInfo
import javax.annotation.Nonnull

class ObjectListAdapter : BaseExpandableListAdapter {
    private val HIERARCHY_PADDING_DP: Int = 10
    private Context context
    @Nonnull
    private ImmutableList<SLObjectDisplayInfo> objects = ImmutableList.of()

    ObjectListAdapter(Context context2) {
        this.context = context2
    }

    SLObjectDisplayInfo getChild(Int i, Int i2) {
        SLObjectDisplayInfo sLObjectDisplayInfo = (SLObjectDisplayInfo) this.objects.get(i)
        if (sLObjectDisplayInfo instanceof SLObjectDisplayInfo.HasChildrenObjects) {
            return (SLObjectDisplayInfo) ((SLObjectDisplayInfo.HasChildrenObjects) sLObjectDisplayInfo).getChildren().get(i2)
        }
        return null
    }

    Long getChildId(Int i, Int i2) {
        return (Long) getChild(i, i2).localID
    }

    View getChildView(Int i, Int i2, Boolean z, View view, ViewGroup viewGroup) {
        View view2 = getView(getChild(i, i2), view, viewGroup)
        view2.findViewById(R.id.groupIndicatorCollapsed).setVisibility(8)
        view2.findViewById(R.id.groupIndicatorExpanded).setVisibility(4)
        view2.findViewById(R.id.groupIndicatorCollapsed).setOnClickListener((View.OnClickListener) null)
        view2.findViewById(R.id.groupIndicatorExpanded).setOnClickListener((View.OnClickListener) null)
        return view2
    }

    Int getChildrenCount(Int i) {
        SLObjectDisplayInfo sLObjectDisplayInfo = (SLObjectDisplayInfo) this.objects.get(i)
        if (sLObjectDisplayInfo instanceof SLObjectDisplayInfo.HasChildrenObjects) {
            return ((SLObjectDisplayInfo.HasChildrenObjects) sLObjectDisplayInfo).getChildren().size()
        }
        return 0
    }

    @Nonnull
    ImmutableList<SLObjectDisplayInfo> getData() {
        return this.objects
    }

    SLObjectDisplayInfo getGroup(Int i) {
        return (SLObjectDisplayInfo) this.objects.get(i)
    }

    Int getGroupCount() {
        return this.objects.size()
    }

    Long getGroupId(Int i) {
        return (Long) getGroup(i).localID
    }

    View getGroupView(Int i, Boolean z, View view, ViewGroup viewGroup) {
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
                Unit onClick(View view) {
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

    View getView(SLObjectDisplayInfo sLObjectDisplayInfo, View view, ViewGroup viewGroup) {
        String str = null
        Int i = 0
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

    Boolean hasStableIds() {
        return true
    }

    Boolean isChildSelectable(Int i, Int i2) {
        return true
    }

    Unit setData(@Nonnull ImmutableList<SLObjectDisplayInfo> immutableList) {
        this.objects = immutableList
        notifyDataSetChanged()
    }
}
