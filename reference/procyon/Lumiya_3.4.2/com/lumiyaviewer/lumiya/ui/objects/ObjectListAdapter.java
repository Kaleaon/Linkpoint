// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.objects;

import com.lumiyaviewer.lumiya.slproto.objects.SLPrimObjectDisplayInfo;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.slproto.objects.SLAvatarObjectDisplayInfo;
import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.os.Build$VERSION;
import android.widget.ExpandableListView;
import android.view.View$OnClickListener;
import android.view.ViewGroup;
import android.view.View;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectDisplayInfo;
import com.google.common.collect.ImmutableList;
import android.content.Context;
import android.widget.BaseExpandableListAdapter;

class ObjectListAdapter extends BaseExpandableListAdapter
{
    private static final int HIERARCHY_PADDING_DP = 10;
    private final Context context;
    @Nonnull
    private ImmutableList<SLObjectDisplayInfo> objects;
    
    public ObjectListAdapter(final Context context) {
        this.objects = ImmutableList.of();
        this.context = context;
    }
    
    public SLObjectDisplayInfo getChild(final int n, final int n2) {
        final SLObjectDisplayInfo slObjectDisplayInfo = this.objects.get(n);
        if (slObjectDisplayInfo instanceof SLObjectDisplayInfo.HasChildrenObjects) {
            return (SLObjectDisplayInfo)((SLObjectDisplayInfo.HasChildrenObjects)slObjectDisplayInfo).getChildren().get(n2);
        }
        return null;
    }
    
    public long getChildId(final int n, final int n2) {
        return this.getChild(n, n2).localID;
    }
    
    public View getChildView(final int n, final int n2, final boolean b, View view, final ViewGroup viewGroup) {
        view = this.getView(this.getChild(n, n2), view, viewGroup);
        view.findViewById(2131755573).setVisibility(8);
        view.findViewById(2131755574).setVisibility(4);
        view.findViewById(2131755573).setOnClickListener((View$OnClickListener)null);
        view.findViewById(2131755574).setOnClickListener((View$OnClickListener)null);
        return view;
    }
    
    public int getChildrenCount(final int n) {
        final SLObjectDisplayInfo slObjectDisplayInfo = this.objects.get(n);
        if (slObjectDisplayInfo instanceof SLObjectDisplayInfo.HasChildrenObjects) {
            return ((SLObjectDisplayInfo.HasChildrenObjects)slObjectDisplayInfo).getChildren().size();
        }
        return 0;
    }
    
    @Nonnull
    public ImmutableList<SLObjectDisplayInfo> getData() {
        return this.objects;
    }
    
    public SLObjectDisplayInfo getGroup(final int n) {
        return this.objects.get(n);
    }
    
    public int getGroupCount() {
        return this.objects.size();
    }
    
    public long getGroupId(final int n) {
        return this.getGroup(n).localID;
    }
    
    public View getGroupView(final int n, final boolean b, View view, final ViewGroup viewGroup) {
        view = this.getView(this.getGroup(n), view, viewGroup);
        if (this.getChildrenCount(n) == 0) {
            view.findViewById(2131755573).setVisibility(4);
            view.findViewById(2131755574).setVisibility(8);
        }
        else if (b) {
            view.findViewById(2131755573).setVisibility(8);
            view.findViewById(2131755574).setVisibility(0);
        }
        else {
            view.findViewById(2131755573).setVisibility(0);
            view.findViewById(2131755574).setVisibility(8);
        }
        if (viewGroup instanceof ExpandableListView) {
            final View$OnClickListener view$OnClickListener = (View$OnClickListener)new View$OnClickListener() {
                final /* synthetic */ ExpandableListView val$expandableListView = (ExpandableListView)viewGroup;
                
                public void onClick(final View view) {
                    if (view.getVisibility() == 0) {
                        switch (view.getId()) {
                            case 2131755573: {
                                if (Build$VERSION.SDK_INT >= 14) {
                                    this.val$expandableListView.expandGroup(n, true);
                                    break;
                                }
                                this.val$expandableListView.expandGroup(n);
                                break;
                            }
                            case 2131755574: {
                                this.val$expandableListView.collapseGroup(n);
                                break;
                            }
                        }
                    }
                }
            };
            view.findViewById(2131755573).setOnClickListener((View$OnClickListener)view$OnClickListener);
            view.findViewById(2131755574).setOnClickListener((View$OnClickListener)view$OnClickListener);
        }
        else {
            view.findViewById(2131755573).setOnClickListener((View$OnClickListener)null);
            view.findViewById(2131755574).setOnClickListener((View$OnClickListener)null);
        }
        return view;
    }
    
    public View getView(final SLObjectDisplayInfo slObjectDisplayInfo, View view, final ViewGroup viewGroup) {
        final CharSequence charSequence = null;
        final int n = 0;
        View inflate = view;
        if (view == null) {
            inflate = LayoutInflater.from(this.context).inflate(2130968695, viewGroup, false);
        }
        inflate.findViewById(2131755575).setLayoutParams((ViewGroup$LayoutParams)new LinearLayout$LayoutParams((int)(TypedValue.applyDimension(1, 10.0f, this.context.getResources().getDisplayMetrics()) * slObjectDisplayInfo.hierarchyLevel), -1));
        view = inflate.findViewById(2131755576);
        int visibility;
        if (slObjectDisplayInfo instanceof SLAvatarObjectDisplayInfo) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        view.setVisibility(visibility);
        if (slObjectDisplayInfo.name != null) {
            ((TextView)inflate.findViewById(2131755577)).setText((CharSequence)slObjectDisplayInfo.name);
        }
        else {
            ((TextView)inflate.findViewById(2131755577)).setText(2131296825);
        }
        final TextView textView = (TextView)inflate.findViewById(2131755578);
        CharSequence format = charSequence;
        if (!Float.isNaN(slObjectDisplayInfo.distance)) {
            format = String.format("%d m", Math.round(slObjectDisplayInfo.distance));
        }
        textView.setText(format);
        if (slObjectDisplayInfo instanceof SLPrimObjectDisplayInfo) {
            final SLPrimObjectDisplayInfo slPrimObjectDisplayInfo = (SLPrimObjectDisplayInfo)slObjectDisplayInfo;
            view = inflate.findViewById(2131755580);
            int visibility2;
            if (slPrimObjectDisplayInfo.touchable) {
                visibility2 = 0;
            }
            else {
                visibility2 = 4;
            }
            view.setVisibility(visibility2);
            view = inflate.findViewById(2131755579);
            int visibility3;
            if (slPrimObjectDisplayInfo.payable) {
                visibility3 = n;
            }
            else {
                visibility3 = 4;
            }
            view.setVisibility(visibility3);
        }
        else {
            inflate.findViewById(2131755580).setVisibility(4);
            inflate.findViewById(2131755579).setVisibility(4);
        }
        return inflate;
    }
    
    public boolean hasStableIds() {
        return true;
    }
    
    public boolean isChildSelectable(final int n, final int n2) {
        return true;
    }
    
    public void setData(@Nonnull final ImmutableList<SLObjectDisplayInfo> objects) {
        this.objects = objects;
        this.notifyDataSetChanged();
    }
}
