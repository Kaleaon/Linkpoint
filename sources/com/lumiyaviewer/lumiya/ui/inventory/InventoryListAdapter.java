// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;

public class InventoryListAdapter extends CursorAdapter
{

    private SLAvatarAppearance avatarAppearance;

    public InventoryListAdapter(SLAvatarAppearance slavatarappearance, Context context, Cursor cursor)
    {
        super(context, cursor);
        avatarAppearance = slavatarappearance;
    }

    public void bindView(View view, Context context, Cursor cursor)
    {
        context = new SLInventoryEntry(cursor);
        ((TextView)view.findViewById(0x7f1001bf)).setText(((SLInventoryEntry) (context)).name);
        int i = context.getDrawableResource();
        if (i >= 0)
        {
            ((ImageView)view.findViewById(0x7f1001bd)).setImageResource(i);
            i = context.getSubtypeDrawableResource();
            if (i >= 0)
            {
                ((ImageView)view.findViewById(0x7f1001be)).setImageResource(i);
            } else
            {
                ((ImageView)view.findViewById(0x7f1001be)).setImageBitmap(null);
            }
        } else
        {
            ((ImageView)view.findViewById(0x7f1001bd)).setImageBitmap(null);
            ((ImageView)view.findViewById(0x7f1001be)).setImageBitmap(null);
        }
        if (avatarAppearance != null)
        {
            view = view.findViewById(0x7f1001c0);
            int j;
            if (avatarAppearance.isItemWorn(context))
            {
                j = 0;
            } else
            {
                j = 8;
            }
            view.setVisibility(j);
            return;
        } else
        {
            view.findViewById(0x7f1001c0).setVisibility(8);
            return;
        }
    }

    public View newView(Context context, Cursor cursor, ViewGroup viewgroup)
    {
        return ((LayoutInflater)context.getSystemService("layout_inflater")).inflate(0x7f040053, viewgroup, false);
    }
}
