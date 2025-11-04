// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.objects;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import android.support.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.inventory.SLTaskInventory;
import android.content.Context;
import android.widget.BaseAdapter;

public class TaskInventoryListAdapter extends BaseAdapter
{
    private final Context context;
    @Nullable
    private SLTaskInventory taskInventory;
    
    public TaskInventoryListAdapter(final Context context) {
        this.context = context;
        this.taskInventory = null;
    }
    
    public int getCount() {
        if (this.taskInventory != null) {
            return this.taskInventory.entries.size();
        }
        return 0;
    }
    
    public SLInventoryEntry getItem(final int n) {
        if (this.taskInventory != null) {
            return this.taskInventory.entries.get(n);
        }
        return null;
    }
    
    public long getItemId(final int n) {
        return n;
    }
    
    public View getView(int n, final View view, final ViewGroup viewGroup) {
        View inflate = view;
        if (view == null) {
            inflate = ((LayoutInflater)this.context.getSystemService("layout_inflater")).inflate(2130968659, viewGroup, false);
        }
        final SLInventoryEntry item = this.getItem(n);
        ((TextView)inflate.findViewById(2131755455)).setText((CharSequence)item.name);
        n = item.getDrawableResource();
        if (n >= 0) {
            ((ImageView)inflate.findViewById(2131755453)).setImageResource(n);
            n = item.getSubtypeDrawableResource();
            if (n >= 0) {
                ((ImageView)inflate.findViewById(2131755454)).setImageResource(n);
            }
            else {
                ((ImageView)inflate.findViewById(2131755454)).setImageBitmap((Bitmap)null);
            }
        }
        else {
            ((ImageView)inflate.findViewById(2131755453)).setImageBitmap((Bitmap)null);
            ((ImageView)inflate.findViewById(2131755454)).setImageBitmap((Bitmap)null);
        }
        inflate.findViewById(2131755456).setVisibility(8);
        return inflate;
    }
    
    public boolean hasStableIds() {
        return false;
    }
    
    public void setData(final SLTaskInventory taskInventory) {
        this.taskInventory = taskInventory;
        this.notifyDataSetChanged();
    }
}
