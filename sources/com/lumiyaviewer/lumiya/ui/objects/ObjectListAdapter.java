// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objects;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.objects.SLAvatarObjectDisplayInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectDisplayInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLPrimObjectDisplayInfo;

class ObjectListAdapter extends BaseExpandableListAdapter
{

    private static final int HIERARCHY_PADDING_DP = 10;
    private final Context context;
    private ImmutableList objects;

    public ObjectListAdapter(Context context1)
    {
        objects = ImmutableList.of();
        context = context1;
    }

    public SLObjectDisplayInfo getChild(int i, int j)
    {
        SLObjectDisplayInfo slobjectdisplayinfo = (SLObjectDisplayInfo)objects.get(i);
        if (slobjectdisplayinfo instanceof com.lumiyaviewer.lumiya.slproto.objects.SLObjectDisplayInfo.HasChildrenObjects)
        {
            return (SLObjectDisplayInfo)((com.lumiyaviewer.lumiya.slproto.objects.SLObjectDisplayInfo.HasChildrenObjects)slobjectdisplayinfo).getChildren().get(j);
        } else
        {
            return null;
        }
    }

    public volatile Object getChild(int i, int j)
    {
        return getChild(i, j);
    }

    public long getChildId(int i, int j)
    {
        return (long)getChild(i, j).localID;
    }

    public View getChildView(int i, int j, boolean flag, View view, ViewGroup viewgroup)
    {
        view = getView(getChild(i, j), view, viewgroup);
        view.findViewById(0x7f100235).setVisibility(8);
        view.findViewById(0x7f100236).setVisibility(4);
        view.findViewById(0x7f100235).setOnClickListener(null);
        view.findViewById(0x7f100236).setOnClickListener(null);
        return view;
    }

    public int getChildrenCount(int i)
    {
        SLObjectDisplayInfo slobjectdisplayinfo = (SLObjectDisplayInfo)objects.get(i);
        if (slobjectdisplayinfo instanceof com.lumiyaviewer.lumiya.slproto.objects.SLObjectDisplayInfo.HasChildrenObjects)
        {
            return ((com.lumiyaviewer.lumiya.slproto.objects.SLObjectDisplayInfo.HasChildrenObjects)slobjectdisplayinfo).getChildren().size();
        } else
        {
            return 0;
        }
    }

    public ImmutableList getData()
    {
        return objects;
    }

    public SLObjectDisplayInfo getGroup(int i)
    {
        return (SLObjectDisplayInfo)objects.get(i);
    }

    public volatile Object getGroup(int i)
    {
        return getGroup(i);
    }

    public int getGroupCount()
    {
        return objects.size();
    }

    public long getGroupId(int i)
    {
        return (long)getGroup(i).localID;
    }

    public View getGroupView(int i, boolean flag, View view, ViewGroup viewgroup)
    {
        view = getView(getGroup(i), view, viewgroup);
        if (getChildrenCount(i) == 0)
        {
            view.findViewById(0x7f100235).setVisibility(4);
            view.findViewById(0x7f100236).setVisibility(8);
        } else
        if (flag)
        {
            view.findViewById(0x7f100235).setVisibility(8);
            view.findViewById(0x7f100236).setVisibility(0);
        } else
        {
            view.findViewById(0x7f100235).setVisibility(0);
            view.findViewById(0x7f100236).setVisibility(8);
        }
        if (viewgroup instanceof ExpandableListView)
        {
            viewgroup = new android.view.View.OnClickListener() {

                final ObjectListAdapter this$0;
                final ExpandableListView val$expandableListView;
                final int val$groupPosition;

                public void onClick(View view1)
                {
                    if (view1.getVisibility() != 0) goto _L2; else goto _L1
_L1:
                    view1.getId();
                    JVM INSTR tableswitch 2131755573 2131755574: default 32
                //                               2131755573 33
                //                               2131755574 68;
                       goto _L2 _L3 _L4
_L2:
                    return;
_L3:
                    if (android.os.Build.VERSION.SDK_INT >= 14)
                    {
                        expandableListView.expandGroup(groupPosition, true);
                        return;
                    } else
                    {
                        expandableListView.expandGroup(groupPosition);
                        return;
                    }
_L4:
                    expandableListView.collapseGroup(groupPosition);
                    return;
                }

            
            {
                this$0 = ObjectListAdapter.this;
                expandableListView = expandablelistview;
                groupPosition = i;
                super();
            }
            };
            view.findViewById(0x7f100235).setOnClickListener(viewgroup);
            view.findViewById(0x7f100236).setOnClickListener(viewgroup);
            return view;
        } else
        {
            view.findViewById(0x7f100235).setOnClickListener(null);
            view.findViewById(0x7f100236).setOnClickListener(null);
            return view;
        }
    }

    public View getView(SLObjectDisplayInfo slobjectdisplayinfo, View view, ViewGroup viewgroup)
    {
        Object obj = null;
        boolean flag = false;
        View view1 = view;
        if (view == null)
        {
            view1 = LayoutInflater.from(context).inflate(0x7f040077, viewgroup, false);
        }
        float f = TypedValue.applyDimension(1, 10F, context.getResources().getDisplayMetrics());
        float f1 = slobjectdisplayinfo.hierarchyLevel;
        view1.findViewById(0x7f100237).setLayoutParams(new android.widget.LinearLayout.LayoutParams((int)(f * f1), -1));
        view = view1.findViewById(0x7f100238);
        int i;
        if (slobjectdisplayinfo instanceof SLAvatarObjectDisplayInfo)
        {
            i = 0;
        } else
        {
            i = 8;
        }
        view.setVisibility(i);
        if (slobjectdisplayinfo.name != null)
        {
            ((TextView)view1.findViewById(0x7f100239)).setText(slobjectdisplayinfo.name);
        } else
        {
            ((TextView)view1.findViewById(0x7f100239)).setText(0x7f090239);
        }
        viewgroup = (TextView)view1.findViewById(0x7f10023a);
        view = obj;
        if (!Float.isNaN(slobjectdisplayinfo.distance))
        {
            view = String.format("%d m", new Object[] {
                Integer.valueOf(Math.round(slobjectdisplayinfo.distance))
            });
        }
        viewgroup.setText(view);
        if (slobjectdisplayinfo instanceof SLPrimObjectDisplayInfo)
        {
            slobjectdisplayinfo = (SLPrimObjectDisplayInfo)slobjectdisplayinfo;
            view = view1.findViewById(0x7f10023c);
            if (((SLPrimObjectDisplayInfo) (slobjectdisplayinfo)).touchable)
            {
                i = 0;
            } else
            {
                i = 4;
            }
            view.setVisibility(i);
            view = view1.findViewById(0x7f10023b);
            if (((SLPrimObjectDisplayInfo) (slobjectdisplayinfo)).payable)
            {
                i = ((flag) ? 1 : 0);
            } else
            {
                i = 4;
            }
            view.setVisibility(i);
            return view1;
        } else
        {
            view1.findViewById(0x7f10023c).setVisibility(4);
            view1.findViewById(0x7f10023b).setVisibility(4);
            return view1;
        }
    }

    public boolean hasStableIds()
    {
        return true;
    }

    public boolean isChildSelectable(int i, int j)
    {
        return true;
    }

    public void setData(ImmutableList immutablelist)
    {
        objects = immutablelist;
        notifyDataSetChanged();
    }
}
