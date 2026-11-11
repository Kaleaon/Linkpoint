package com.lumiyaviewer.lumiya.ui.objects;

import android.content.Context;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.slproto.objects.SLAvatarObjectDisplayInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectDisplayInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLPrimObjectDisplayInfo;
import javax.annotation.Nonnull;
/* loaded from: classes.dex */
class ObjectListAdapter extends BaseExpandableListAdapter {
    private static final int HIERARCHY_PADDING_DP = 10;
    private final Context context;
    @Nonnull
    private ImmutableList<SLObjectDisplayInfo> objects = ImmutableList.of();

    public ObjectListAdapter(Context context) {
        this.context = context;
    }

    @Override // android.widget.ExpandableListAdapter
    public SLObjectDisplayInfo getChild(int i, int i2) {
        SLObjectDisplayInfo sLObjectDisplayInfo = this.objects.get(i);
        if (sLObjectDisplayInfo instanceof SLObjectDisplayInfo.HasChildrenObjects) {
            return ((SLObjectDisplayInfo.HasChildrenObjects) sLObjectDisplayInfo).getChildren().get(i2);
        }
        return null;
    }

    @Override // android.widget.ExpandableListAdapter
    public long getChildId(int i, int i2) {
        return getChild(i, i2).localID;
    }

    @Override // android.widget.ExpandableListAdapter
    public View getChildView(int i, int i2, boolean z, View view, ViewGroup viewGroup) {
        View view2 = getView(getChild(i, i2), view, viewGroup);
        view2.findViewById(R.id.groupIndicatorCollapsed).setVisibility(8);
        view2.findViewById(R.id.groupIndicatorExpanded).setVisibility(4);
        view2.findViewById(R.id.groupIndicatorCollapsed).setOnClickListener(null);
        view2.findViewById(R.id.groupIndicatorExpanded).setOnClickListener(null);
        return view2;
    }

    @Override // android.widget.ExpandableListAdapter
    public int getChildrenCount(int i) {
        SLObjectDisplayInfo sLObjectDisplayInfo = this.objects.get(i);
        if (sLObjectDisplayInfo instanceof SLObjectDisplayInfo.HasChildrenObjects) {
            return ((SLObjectDisplayInfo.HasChildrenObjects) sLObjectDisplayInfo).getChildren().size();
        }
        return 0;
    }

    @Nonnull
    public ImmutableList<SLObjectDisplayInfo> getData() {
        return this.objects;
    }

    @Override // android.widget.ExpandableListAdapter
    public SLObjectDisplayInfo getGroup(int i) {
        return this.objects.get(i);
    }

    @Override // android.widget.ExpandableListAdapter
    public int getGroupCount() {
        return this.objects.size();
    }

    @Override // android.widget.ExpandableListAdapter
    public long getGroupId(int i) {
        return getGroup(i).localID;
    }

    @Override // android.widget.ExpandableListAdapter
    public View getGroupView(final int i, boolean z, View view, ViewGroup viewGroup) {
        View view2 = getView(getGroup(i), view, viewGroup);
        if (getChildrenCount(i) == 0) {
            view2.findViewById(R.id.groupIndicatorCollapsed).setVisibility(4);
            view2.findViewById(R.id.groupIndicatorExpanded).setVisibility(8);
        } else if (z) {
            view2.findViewById(R.id.groupIndicatorCollapsed).setVisibility(8);
            view2.findViewById(R.id.groupIndicatorExpanded).setVisibility(0);
        } else {
            view2.findViewById(R.id.groupIndicatorCollapsed).setVisibility(0);
            view2.findViewById(R.id.groupIndicatorExpanded).setVisibility(8);
        }
        if (viewGroup instanceof ExpandableListView) {
            final ExpandableListView expandableListView = (ExpandableListView) viewGroup;
            View.OnClickListener onClickListener = new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.objects.ObjectListAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view3) {
                    if (view3.getVisibility() == 0) {
                        switch (view3.getId()) {
                            case R.id.groupIndicatorCollapsed /* 2131755573 */:
                                if (Build.VERSION.SDK_INT >= 14) {
                                    expandableListView.expandGroup(i, true);
                                    return;
                                } else {
                                    expandableListView.expandGroup(i);
                                    return;
                                }
                            case R.id.groupIndicatorExpanded /* 2131755574 */:
                                expandableListView.collapseGroup(i);
                                return;
                            default:
                                return;
                        }
                    }
                }
            };
            view2.findViewById(R.id.groupIndicatorCollapsed).setOnClickListener(onClickListener);
            view2.findViewById(R.id.groupIndicatorExpanded).setOnClickListener(onClickListener);
        } else {
            view2.findViewById(R.id.groupIndicatorCollapsed).setOnClickListener(null);
            view2.findViewById(R.id.groupIndicatorExpanded).setOnClickListener(null);
        }
        return view2;
    }

    public View getView(SLObjectDisplayInfo sLObjectDisplayInfo, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(R.layout.object_list_item, viewGroup, false);
        }
        view.findViewById(R.id.object_hierarchy_padding).setLayoutParams(new LinearLayout.LayoutParams((int) (TypedValue.applyDimension(1, 10.0f, this.context.getResources().getDisplayMetrics()) * sLObjectDisplayInfo.hierarchyLevel), -1));
        view.findViewById(R.id.avatarIconView).setVisibility(sLObjectDisplayInfo instanceof SLAvatarObjectDisplayInfo ? 0 : 8);
        if (sLObjectDisplayInfo.name != null) {
            ((TextView) view.findViewById(R.id.objectNameTextView)).setText(sLObjectDisplayInfo.name);
        } else {
            ((TextView) view.findViewById(R.id.objectNameTextView)).setText(R.string.object_name_loading);
        }
        ((TextView) view.findViewById(R.id.objectDistanceTextView)).setText(Float.isNaN(sLObjectDisplayInfo.distance) ? null : String.format("%d m", Integer.valueOf(Math.round(sLObjectDisplayInfo.distance))));
        if (sLObjectDisplayInfo instanceof SLPrimObjectDisplayInfo) {
            SLPrimObjectDisplayInfo sLPrimObjectDisplayInfo = (SLPrimObjectDisplayInfo) sLObjectDisplayInfo;
            view.findViewById(R.id.touchIconView).setVisibility(sLPrimObjectDisplayInfo.touchable ? 0 : 4);
            view.findViewById(R.id.payIconView).setVisibility(sLPrimObjectDisplayInfo.payable ? 0 : 4);
        } else {
            view.findViewById(R.id.touchIconView).setVisibility(4);
            view.findViewById(R.id.payIconView).setVisibility(4);
        }
        return view;
    }

    @Override // android.widget.ExpandableListAdapter
    public boolean hasStableIds() {
        return true;
    }

    @Override // android.widget.ExpandableListAdapter
    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    public void setData(@Nonnull ImmutableList<SLObjectDisplayInfo> immutableList) {
        this.objects = immutableList;
        notifyDataSetChanged();
    }
}
