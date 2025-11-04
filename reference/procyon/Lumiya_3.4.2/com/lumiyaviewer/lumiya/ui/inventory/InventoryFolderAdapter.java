// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.inventory;

import android.widget.CheckBox;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryType;
import android.graphics.Typeface;
import com.google.common.base.Objects;
import android.graphics.Bitmap;
import android.widget.ImageView;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.View;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.google.common.collect.Table;
import java.util.UUID;
import com.google.common.collect.ImmutableMap;
import android.view.LayoutInflater;
import com.lumiyaviewer.lumiya.orm.InventoryDB;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.orm.InventoryEntryList;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import android.view.View$OnClickListener;
import android.widget.BaseAdapter;

public class InventoryFolderAdapter extends BaseAdapter implements View$OnClickListener
{
    @Nullable
    private SLAvatarAppearance avatarAppearance;
    @Nonnull
    private InventoryEntryList data;
    private InventoryDB database;
    private final LayoutInflater inflater;
    @Nullable
    private OnItemCheckboxClickListener onItemCheckboxClickListener;
    @Nullable
    private ImmutableMap<UUID, String> wornAttachments;
    private final boolean wornCheckboxes;
    @Nullable
    private UUID wornOutfitFolder;
    @Nullable
    private Table<SLWearableType, UUID, SLWearable> wornWearables;
    
    public InventoryFolderAdapter(final LayoutInflater inflater, final boolean wornCheckboxes) {
        this.data = new InventoryEntryList();
        this.wornWearables = null;
        this.wornAttachments = null;
        this.onItemCheckboxClickListener = null;
        this.avatarAppearance = null;
        this.inflater = inflater;
        this.wornCheckboxes = wornCheckboxes;
    }
    
    private boolean isItemWorn(final SLInventoryEntry slInventoryEntry) {
        boolean b = false;
        if (slInventoryEntry.whatIsItemWornOn(this.wornAttachments, this.wornWearables, false) != null) {
            b = true;
        }
        return b;
    }
    
    public int getCount() {
        return this.data.size();
    }
    
    public SLInventoryEntry getItem(final int n) {
        return this.data.get(n);
    }
    
    public long getItemId(final int n) {
        final SLInventoryEntry item = this.getItem(n);
        if (item != null) {
            return item.getId();
        }
        return -1L;
    }
    
    public View getView(int subtypeDrawableResource, View viewById, final ViewGroup viewGroup) {
        final boolean b = true;
        final int n = 0;
        View inflate = viewById;
        if (viewById == null) {
            inflate = this.inflater.inflate(2130968659, viewGroup, false);
        }
        final SLInventoryEntry item = this.getItem(subtypeDrawableResource);
        if (item != null) {
            final TextView textView = (TextView)inflate.findViewById(2131755455);
            textView.setText((CharSequence)item.name);
            int imageResource = -1;
            subtypeDrawableResource = -1;
            SLInventoryEntry resolveLink;
            if (item.assetType == SLAssetType.AT_LINK.getTypeCode() && this.database != null) {
                resolveLink = this.database.resolveLink(item);
                if (resolveLink != null) {
                    imageResource = resolveLink.getDrawableResource();
                    subtypeDrawableResource = 2130837700;
                }
                else {
                    resolveLink = item;
                }
            }
            else {
                resolveLink = item;
            }
            if (imageResource < 0) {
                imageResource = item.getDrawableResource();
                subtypeDrawableResource = item.getSubtypeDrawableResource();
            }
            if (imageResource >= 0) {
                ((ImageView)inflate.findViewById(2131755453)).setImageResource(imageResource);
                if (subtypeDrawableResource >= 0) {
                    ((ImageView)inflate.findViewById(2131755454)).setImageResource(subtypeDrawableResource);
                }
                else {
                    ((ImageView)inflate.findViewById(2131755454)).setImageBitmap((Bitmap)null);
                }
            }
            else {
                ((ImageView)inflate.findViewById(2131755453)).setImageBitmap((Bitmap)null);
                ((ImageView)inflate.findViewById(2131755454)).setImageBitmap((Bitmap)null);
            }
            if (this.wornOutfitFolder != null && Objects.equal(this.wornOutfitFolder, item.uuid)) {
                textView.setTypeface((Typeface)null, 1);
            }
            else {
                textView.setTypeface((Typeface)null, 0);
            }
            if (this.wornCheckboxes) {
                if (item.assetType == SLAssetType.AT_OBJECT.getTypeCode() || (item.isLink() && item.invType == SLInventoryType.IT_OBJECT.getTypeCode()) || item.isWearable() || resolveLink.assetType == SLAssetType.AT_OBJECT.getTypeCode() || resolveLink.isWearable()) {
                    final Object whatIsItemWornOn = resolveLink.whatIsItemWornOn(this.wornAttachments, this.wornWearables, false);
                    final boolean checked = whatIsItemWornOn != null;
                    final boolean b2 = whatIsItemWornOn instanceof SLWearableType && ((SLWearableType)whatIsItemWornOn).isBodyPart();
                    while (true) {
                        Label_0489: {
                            if (this.avatarAppearance == null) {
                                break Label_0489;
                            }
                            boolean canDetachItem = b;
                            if (checked) {
                                if (resolveLink.isWearable()) {
                                    if (!this.avatarAppearance.canTakeItemOff(resolveLink)) {
                                        break Label_0489;
                                    }
                                    canDetachItem = (b2 ^ true);
                                }
                                else {
                                    canDetachItem = this.avatarAppearance.canDetachItem(resolveLink);
                                }
                            }
                            inflate.findViewById(2131755457).setVisibility(0);
                            inflate.findViewById(2131755457).setTag(2131755053, (Object)item);
                            ((CheckBox)inflate.findViewById(2131755457)).setChecked(checked);
                            inflate.findViewById(2131755457).setEnabled(canDetachItem);
                            inflate.findViewById(2131755457).setOnClickListener((View$OnClickListener)this);
                            return inflate;
                        }
                        boolean canDetachItem = false;
                        continue;
                    }
                }
                inflate.findViewById(2131755457).setVisibility(8);
                inflate.findViewById(2131755457).setTag(2131755053, (Object)null);
            }
            else {
                inflate.findViewById(2131755457).setVisibility(8);
                viewById = inflate.findViewById(2131755456);
                if (this.isItemWorn(item)) {
                    subtypeDrawableResource = n;
                }
                else {
                    subtypeDrawableResource = 8;
                }
                viewById.setVisibility(subtypeDrawableResource);
            }
        }
        return inflate;
    }
    
    public boolean hasStableIds() {
        return true;
    }
    
    public void onClick(final View view) {
        if (this.onItemCheckboxClickListener != null) {
            final Object tag = view.getTag(2131755053);
            if (tag instanceof SLInventoryEntry) {
                this.onItemCheckboxClickListener.onItemCheckboxClicked((SLInventoryEntry)tag);
            }
        }
    }
    
    public void setAvatarAppearance(@Nullable final SLAvatarAppearance avatarAppearance) {
        this.avatarAppearance = avatarAppearance;
        this.notifyDataSetChanged();
    }
    
    public void setData(@Nullable InventoryEntryList data) {
        if (data == null) {
            data = new InventoryEntryList();
        }
        this.data = data;
        this.notifyDataSetChanged();
    }
    
    public void setDatabase(final InventoryDB database) {
        this.database = database;
        this.notifyDataSetChanged();
    }
    
    public void setOnItemCheckboxClickListener(@Nullable final OnItemCheckboxClickListener onItemCheckboxClickListener) {
        this.onItemCheckboxClickListener = onItemCheckboxClickListener;
    }
    
    public void setWornAttachments(@Nullable final ImmutableMap<UUID, String> wornAttachments) {
        this.wornAttachments = wornAttachments;
        this.notifyDataSetChanged();
    }
    
    public void setWornOutfitFolder(@Nullable final UUID wornOutfitFolder) {
        this.wornOutfitFolder = wornOutfitFolder;
        this.notifyDataSetChanged();
    }
    
    public void setWornWearables(@Nullable final Table<SLWearableType, UUID, SLWearable> wornWearables) {
        this.wornWearables = wornWearables;
        this.notifyDataSetChanged();
    }
    
    public interface OnItemCheckboxClickListener
    {
        void onItemCheckboxClicked(final SLInventoryEntry p0);
    }
}
