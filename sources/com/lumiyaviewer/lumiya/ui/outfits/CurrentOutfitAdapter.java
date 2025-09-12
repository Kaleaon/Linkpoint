// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.outfits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.ui.common.DismissableAdapter;
import com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener;

class CurrentOutfitAdapter extends BaseAdapter
    implements DismissableAdapter
{

    private SLAvatarAppearance avatarAppearance;
    private final LayoutInflater inflater;
    private ImmutableList wornItems;

    CurrentOutfitAdapter(Context context)
    {
        wornItems = ImmutableList.of();
        inflater = LayoutInflater.from(context);
    }

    public boolean canDismiss(int i)
    {
        boolean flag = false;
        com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance.WornItem wornitem = getItem(i);
        if (wornitem != null && avatarAppearance != null)
        {
            if (wornitem.getWornOn() != null)
            {
                if (!wornitem.getWornOn().isBodyPart())
                {
                    flag = avatarAppearance.canTakeItemOff(wornitem.getWornOn());
                }
                return flag;
            } else
            {
                return avatarAppearance.canDetachItem(wornitem);
            }
        } else
        {
            return false;
        }
    }

    public int getCount()
    {
        return wornItems.size();
    }

    public com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance.WornItem getItem(int i)
    {
        if (i >= 0 && i < wornItems.size())
        {
            return (com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance.WornItem)wornItems.get(i);
        } else
        {
            return null;
        }
    }

    public volatile Object getItem(int i)
    {
        return getItem(i);
    }

    public long getItemId(int i)
    {
        return (long)i;
    }

    public int getItemViewType(int i)
    {
        return 0;
    }

    public View getView(int i, View view, ViewGroup viewgroup)
    {
        Object obj = null;
        if (view != null && view.getId() != 0x7f100249)
        {
            view = obj;
        }
        if (view == null)
        {
            view = inflater.inflate(0x7f04007e, viewgroup, false);
        }
        viewgroup = (com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance.WornItem)wornItems.get(i);
        ((TextView)view.findViewById(0x7f1001bf)).setText(viewgroup.getName());
        if (viewgroup.getWornOn() != null)
        {
            ((ImageView)view.findViewById(0x7f1001bd)).setImageResource(0x7f0200bf);
            view.findViewById(0x7f10024a).setVisibility(8);
        } else
        {
            ((ImageView)view.findViewById(0x7f1001bd)).setImageResource(0x7f0200c6);
            View view1 = view.findViewById(0x7f10024a);
            if (viewgroup.getIsTouchable())
            {
                i = 0;
            } else
            {
                i = 8;
            }
            view1.setVisibility(i);
        }
        SwipeDismissListViewTouchListener.restoreViewState(view);
        return view;
    }

    public boolean hasStableIds()
    {
        return false;
    }

    public boolean isEmpty()
    {
        return wornItems.isEmpty();
    }

    public void onDismiss(int i)
    {
        com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance.WornItem wornitem;
label0:
        {
            wornitem = getItem(i);
            if (wornitem != null && avatarAppearance != null)
            {
                if (wornitem.getWornOn() == null)
                {
                    break label0;
                }
                avatarAppearance.TakeItemOff(wornitem.itemID());
            }
            return;
        }
        avatarAppearance.DetachItem(wornitem);
    }

    public void setAvatarAppearance(SLAvatarAppearance slavatarappearance)
    {
        avatarAppearance = slavatarappearance;
    }

    public void setData(ImmutableList immutablelist)
    {
        if (immutablelist == null)
        {
            immutablelist = ImmutableList.of();
        }
        wornItems = immutablelist;
        notifyDataSetChanged();
    }
}
