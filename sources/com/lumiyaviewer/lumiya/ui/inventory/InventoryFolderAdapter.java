// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.inventory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import com.lumiyaviewer.lumiya.orm.InventoryDB;
import com.lumiyaviewer.lumiya.orm.InventoryEntryList;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryType;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import java.util.UUID;

public class InventoryFolderAdapter extends BaseAdapter
    implements android.view.View.OnClickListener
{
    public static interface OnItemCheckboxClickListener
    {

        public abstract void onItemCheckboxClicked(SLInventoryEntry slinventoryentry);
    }


    private SLAvatarAppearance avatarAppearance;
    private InventoryEntryList data;
    private InventoryDB database;
    private final LayoutInflater inflater;
    private OnItemCheckboxClickListener onItemCheckboxClickListener;
    private ImmutableMap wornAttachments;
    private final boolean wornCheckboxes;
    private UUID wornOutfitFolder;
    private Table wornWearables;

    public InventoryFolderAdapter(LayoutInflater layoutinflater, boolean flag)
    {
        data = new InventoryEntryList();
        wornWearables = null;
        wornAttachments = null;
        onItemCheckboxClickListener = null;
        avatarAppearance = null;
        inflater = layoutinflater;
        wornCheckboxes = flag;
    }

    private boolean isItemWorn(SLInventoryEntry slinventoryentry)
    {
        boolean flag = false;
        if (slinventoryentry.whatIsItemWornOn(wornAttachments, wornWearables, false) != null)
        {
            flag = true;
        }
        return flag;
    }

    public int getCount()
    {
        return data.size();
    }

    public SLInventoryEntry getItem(int i)
    {
        return data.get(i);
    }

    public volatile Object getItem(int i)
    {
        return getItem(i);
    }

    public long getItemId(int i)
    {
        SLInventoryEntry slinventoryentry = getItem(i);
        if (slinventoryentry != null)
        {
            return slinventoryentry.getId();
        } else
        {
            return -1L;
        }
    }

    public View getView(int i, View view, ViewGroup viewgroup)
    {
label0:
        {
            boolean flag4 = true;
            boolean flag = false;
            View view1 = view;
            if (view == null)
            {
                view1 = inflater.inflate(0x7f040053, viewgroup, false);
            }
            viewgroup = getItem(i);
            if (viewgroup != null)
            {
                Object obj = (TextView)view1.findViewById(0x7f1001bf);
                ((TextView) (obj)).setText(((SLInventoryEntry) (viewgroup)).name);
                int j = -1;
                i = -1;
                int k;
                boolean flag1;
                boolean flag2;
                boolean flag3;
                if (((SLInventoryEntry) (viewgroup)).assetType == SLAssetType.AT_LINK.getTypeCode() && database != null)
                {
                    view = database.resolveLink(viewgroup);
                    if (view != null)
                    {
                        j = view.getDrawableResource();
                        i = 0x7f0200c4;
                    } else
                    {
                        view = viewgroup;
                    }
                } else
                {
                    view = viewgroup;
                }
                if (j < 0)
                {
                    i = viewgroup.getDrawableResource();
                    j = viewgroup.getSubtypeDrawableResource();
                    k = i;
                    i = j;
                } else
                {
                    k = j;
                }
                if (k >= 0)
                {
                    ((ImageView)view1.findViewById(0x7f1001bd)).setImageResource(k);
                    if (i >= 0)
                    {
                        ((ImageView)view1.findViewById(0x7f1001be)).setImageResource(i);
                    } else
                    {
                        ((ImageView)view1.findViewById(0x7f1001be)).setImageBitmap(null);
                    }
                } else
                {
                    ((ImageView)view1.findViewById(0x7f1001bd)).setImageBitmap(null);
                    ((ImageView)view1.findViewById(0x7f1001be)).setImageBitmap(null);
                }
                if (wornOutfitFolder != null && Objects.equal(wornOutfitFolder, ((SLInventoryEntry) (viewgroup)).uuid))
                {
                    ((TextView) (obj)).setTypeface(null, 1);
                } else
                {
                    ((TextView) (obj)).setTypeface(null, 0);
                }
                if (!wornCheckboxes)
                {
                    break MISSING_BLOCK_LABEL_542;
                }
                if (((SLInventoryEntry) (viewgroup)).assetType == SLAssetType.AT_OBJECT.getTypeCode() || viewgroup.isLink() && ((SLInventoryEntry) (viewgroup)).invType == SLInventoryType.IT_OBJECT.getTypeCode() || viewgroup.isWearable() || ((SLInventoryEntry) (view)).assetType == SLAssetType.AT_OBJECT.getTypeCode())
                {
                    flag1 = true;
                } else
                {
                    flag1 = view.isWearable();
                }
                break label0;
            }
        }
          goto _L1
        if (!flag1)
        {
            break MISSING_BLOCK_LABEL_514;
        }
        obj = view.whatIsItemWornOn(wornAttachments, wornWearables, false);
        if (obj != null)
        {
            flag2 = true;
        } else
        {
            flag2 = false;
        }
        if (obj instanceof SLWearableType)
        {
            flag3 = ((SLWearableType)obj).isBodyPart();
        } else
        {
            flag3 = false;
        }
        if (avatarAppearance == null) goto _L3; else goto _L2
_L2:
        flag1 = flag4;
        if (!flag2) goto _L5; else goto _L4
_L4:
        if (!view.isWearable())
        {
            break MISSING_BLOCK_LABEL_501;
        }
        if (!avatarAppearance.canTakeItemOff(view)) goto _L3; else goto _L6
_L6:
        flag1 = flag3 ^ true;
_L5:
        view1.findViewById(0x7f1001c1).setVisibility(0);
        view1.findViewById(0x7f1001c1).setTag(0x7f10002d, viewgroup);
        ((CheckBox)view1.findViewById(0x7f1001c1)).setChecked(flag2);
        view1.findViewById(0x7f1001c1).setEnabled(flag1);
        view1.findViewById(0x7f1001c1).setOnClickListener(this);
_L1:
        return view1;
_L3:
        flag1 = false;
        continue; /* Loop/switch isn't completed */
        flag1 = avatarAppearance.canDetachItem(view);
        if (true) goto _L5; else goto _L7
_L7:
        view1.findViewById(0x7f1001c1).setVisibility(8);
        view1.findViewById(0x7f1001c1).setTag(0x7f10002d, null);
        return view1;
        view1.findViewById(0x7f1001c1).setVisibility(8);
        view = view1.findViewById(0x7f1001c0);
        if (isItemWorn(viewgroup))
        {
            i = ((flag) ? 1 : 0);
        } else
        {
            i = 8;
        }
        view.setVisibility(i);
        return view1;
    }

    public boolean hasStableIds()
    {
        return true;
    }

    public void onClick(View view)
    {
        if (onItemCheckboxClickListener != null)
        {
            view = ((View) (view.getTag(0x7f10002d)));
            if (view instanceof SLInventoryEntry)
            {
                onItemCheckboxClickListener.onItemCheckboxClicked((SLInventoryEntry)view);
            }
        }
    }

    public void setAvatarAppearance(SLAvatarAppearance slavatarappearance)
    {
        avatarAppearance = slavatarappearance;
        notifyDataSetChanged();
    }

    public void setData(InventoryEntryList inventoryentrylist)
    {
        if (inventoryentrylist == null)
        {
            inventoryentrylist = new InventoryEntryList();
        }
        data = inventoryentrylist;
        notifyDataSetChanged();
    }

    public void setDatabase(InventoryDB inventorydb)
    {
        database = inventorydb;
        notifyDataSetChanged();
    }

    public void setOnItemCheckboxClickListener(OnItemCheckboxClickListener onitemcheckboxclicklistener)
    {
        onItemCheckboxClickListener = onitemcheckboxclicklistener;
    }

    public void setWornAttachments(ImmutableMap immutablemap)
    {
        wornAttachments = immutablemap;
        notifyDataSetChanged();
    }

    public void setWornOutfitFolder(UUID uuid)
    {
        wornOutfitFolder = uuid;
        notifyDataSetChanged();
    }

    public void setWornWearables(Table table)
    {
        wornWearables = table;
        notifyDataSetChanged();
    }
}
