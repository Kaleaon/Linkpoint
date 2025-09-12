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

class ObjectListAdapter extends BaseExpandableListAdapter {
    private static final int HIERARCHY_PADDING_DP = 10;
    private final Context context;
    @Nonnull
    private ImmutableList<SLObjectDisplayInfo> objects = ImmutableList.of();

    public ObjectListAdapter(Context context2) {
        this.context = context2;
    }

    public SLObjectDisplayInfo getChild(int i, int i2) {
        SLObjectDisplayInfo sLObjectDisplayInfo = (SLObjectDisplayInfo) this.objects.get(i);
        if (sLObjectDisplayInfo instanceof SLObjectDisplayInfo.HasChildrenObjects) {
            return (SLObjectDisplayInfo) ((SLObjectDisplayInfo.HasChildrenObjects) sLObjectDisplayInfo).getChildren().get(i2);
        }
        return null;
    }

    public long getChildId(int i, int i2) {
        return (long) getChild(i, i2).localID;
    }

    public View getChildView(int i, int i2, boolean z, View view, ViewGroup viewGroup) {
        View view2 = getView(getChild(i, i2), view, viewGroup);
        view2.findViewById(R.id.groupIndicatorCollapsed).setVisibility(8);
        view2.findViewById(R.id.groupIndicatorExpanded).setVisibility(4);
        view2.findViewById(R.id.groupIndicatorCollapsed).setOnClickListener((View.OnClickListener) null);
        view2.findViewById(R.id.groupIndicatorExpanded).setOnClickListener((View.OnClickListener) null);
        return view2;
    }

    public int getChildrenCount(int i) {
        SLObjectDisplayInfo sLObjectDisplayInfo = (SLObjectDisplayInfo) this.objects.get(i);
        if (sLObjectDisplayInfo instanceof SLObjectDisplayInfo.HasChildrenObjects) {
            return ((SLObjectDisplayInfo.HasChildrenObjects) sLObjectDisplayInfo).getChildren().size();
        }
        return 0;
    }

    @Nonnull
    public ImmutableList<SLObjectDisplayInfo> getData() {
        return this.objects;
    }

    public SLObjectDisplayInfo getGroup(int i) {
        return (SLObjectDisplayInfo) this.objects.get(i);
    }

    public int getGroupCount() {
        return this.objects.size();
    }

    public long getGroupId(int i) {
        return (long) getGroup(i).localID;
    }

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
            AnonymousClass1 r1 = new View.OnClickListener() {
                public void onClick(View view) {
                    if (view.getVisibility() == 0) {
                        switch (view.getId()) {
                            case R.id.groupIndicatorCollapsed:
                                if (Build.VERSION.SDK_INT >= 14) {
                                    expandableListView.expandGroup(i, true);
                                    return;
                                } else {
                                    expandableListView.expandGroup(i);
                                    return;
                                }
                            case R.id.groupIndicatorExpanded:
                                expandableListView.collapseGroup(i);
                                return;
                            default:
                                return;
                        }
                    }
                }
            };
            view2.findViewById(R.id.groupIndicatorCollapsed).setOnClickListener(r1);
            view2.findViewById(R.id.groupIndicatorExpanded).setOnClickListener(r1);
        } else {
            view2.findViewById(R.id.groupIndicatorCollapsed).setOnClickListener((View.OnClickListener) null);
            view2.findViewById(R.id.groupIndicatorExpanded).setOnClickListener((View.OnClickListener) null);
        }
        return view2;
    }

    public View getView(SLObjectDisplayInfo sLObjectDisplayInfo, View view, ViewGroup viewGroup) {
        String str = null;
        int i = 0;
        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(R.layout.object_list_item, viewGroup, false);
        }
        view.findViewById(R.id.object_hierarchy_padding).setLayoutParams(new LinearLayout.LayoutParams((int) (TypedValue.applyDimension(1, 10.0f, this.context.getResources().getDisplayMetrics()) * ((float) sLObjectDisplayInfo.hierarchyLevel)), -1));
        view.findViewById(R.id.avatarIconView).setVisibility(sLObjectDisplayInfo instanceof SLAvatarObjectDisplayInfo ? 0 : 8);
        if (sLObjectDisplayInfo.name != null) {
            ((TextView) view.findViewById(R.id.objectNameTextView)).setText(sLObjectDisplayInfo.name);
        } else {
            ((TextView) view.findViewById(R.id.objectNameTextView)).setText(R.string.object_name_loading);
        }
        TextView textView = (TextView) view.findViewById(R.id.objectDistanceTextView);
        if (!Float.isNaN(sLObjectDisplayInfo.distance)) {
            str = String.format("%d m", new Object[]{Integer.valueOf(Math.round(sLObjectDisplayInfo.distance))});
        }
        textView.setText(str);
        if (sLObjectDisplayInfo instanceof SLPrimObjectDisplayInfo) {
            SLPrimObjectDisplayInfo sLPrimObjectDisplayInfo = (SLPrimObjectDisplayInfo) sLObjectDisplayInfo;
            view.findViewById(R.id.touchIconView).setVisibility(sLPrimObjectDisplayInfo.touchable ? 0 : 4);
            View findViewById = view.findViewById(R.id.payIconView);
            if (!sLPrimObjectDisplayInfo.payable) {
                i = 4;
            }
            findViewById.setVisibility(i);
        } else {
            view.findViewById(R.id.touchIconView).setVisibility(4);
            view.findViewById(R.id.payIconView).setVisibility(4);
        }
        return view;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    public void setData(@Nonnull ImmutableList<SLObjectDisplayInfo> immutableList) {
        this.objects = immutableList;
        notifyDataSetChanged();
    }
}
