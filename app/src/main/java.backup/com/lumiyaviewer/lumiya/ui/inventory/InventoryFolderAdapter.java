package com.lumiyaviewer.lumiya.ui.inventory;

import android.graphics.Bitmap;
import android.graphics.Typeface;
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
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.orm.InventoryDB;
import com.lumiyaviewer.lumiya.orm.InventoryEntryList;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryType;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InventoryFolderAdapter extends BaseAdapter implements View.OnClickListener {
    @Nullable
    private SLAvatarAppearance avatarAppearance = null;
    @Nonnull
    private InventoryEntryList data = new InventoryEntryList();
    private InventoryDB database;
    private final LayoutInflater inflater;
    @Nullable
    private OnItemCheckboxClickListener onItemCheckboxClickListener = null;
    @Nullable
    private ImmutableMap<UUID, String> wornAttachments = null;
    private final boolean wornCheckboxes;
    @Nullable
    private UUID wornOutfitFolder;
    @Nullable
    private Table<SLWearableType, UUID, SLWearable> wornWearables = null;

    public interface OnItemCheckboxClickListener {
        void onItemCheckboxClicked(SLInventoryEntry sLInventoryEntry);
    }

    public InventoryFolderAdapter(LayoutInflater layoutInflater, boolean z) {
        this.inflater = layoutInflater;
        this.wornCheckboxes = z;
    }

    private boolean isItemWorn(SLInventoryEntry sLInventoryEntry) {
        return sLInventoryEntry.whatIsItemWornOn(this.wornAttachments, this.wornWearables, false) != null;
    }

    public int getCount() {
        return this.data.size();
    }

    public SLInventoryEntry getItem(int i) {
        return this.data.get(i);
    }

    public long getItemId(int i) {
        SLInventoryEntry item = getItem(i);
        if (item != null) {
            return item.getId();
        }
        return -1;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        SLInventoryEntry sLInventoryEntry;
        int i2;
        int i3;
        boolean z = true;
        int i4 = 0;
        if (view == null) {
            view = this.inflater.inflate(R.layout.inventory_item, viewGroup, false);
        }
        SLInventoryEntry item = getItem(i);
        if (item != null) {
            TextView textView = (TextView) view.findViewById(R.id.itemNameTextView);
            textView.setText(item.name);
            int i5 = -1;
            int i6 = -1;
            if (item.assetType != SLAssetType.AT_LINK.getTypeCode() || this.database == null) {
                sLInventoryEntry = item;
            } else {
                SLInventoryEntry resolveLink = this.database.resolveLink(item);
                if (resolveLink != null) {
                    i5 = resolveLink.getDrawableResource();
                    i6 = R.drawable.inv_link;
                    sLInventoryEntry = resolveLink;
                } else {
                    sLInventoryEntry = item;
                }
            }
            if (i5 < 0) {
                i2 = item.getDrawableResource();
                i3 = item.getSubtypeDrawableResource();
            } else {
                i2 = i5;
                i3 = i6;
            }
            if (i2 >= 0) {
                ((ImageView) view.findViewById(R.id.itemTypeIconView)).setImageResource(i2);
                if (i3 >= 0) {
                    ((ImageView) view.findViewById(R.id.itemSubTypeIconView)).setImageResource(i3);
                } else {
                    ((ImageView) view.findViewById(R.id.itemSubTypeIconView)).setImageBitmap((Bitmap) null);
                }
            } else {
                ((ImageView) view.findViewById(R.id.itemTypeIconView)).setImageBitmap((Bitmap) null);
                ((ImageView) view.findViewById(R.id.itemSubTypeIconView)).setImageBitmap((Bitmap) null);
            }
            if (this.wornOutfitFolder == null || !Objects.equal(this.wornOutfitFolder, item.uuid)) {
                textView.setTypeface((Typeface) null, 0);
            } else {
                textView.setTypeface((Typeface) null, 1);
            }
            if (this.wornCheckboxes) {
                if ((item.assetType == SLAssetType.AT_OBJECT.getTypeCode() || (item.isLink() && item.invType == SLInventoryType.IT_OBJECT.getTypeCode()) || item.isWearable() || sLInventoryEntry.assetType == SLAssetType.AT_OBJECT.getTypeCode()) ? true : sLInventoryEntry.isWearable()) {
                    Object whatIsItemWornOn = sLInventoryEntry.whatIsItemWornOn(this.wornAttachments, this.wornWearables, false);
                    boolean z2 = whatIsItemWornOn != null;
                    boolean isBodyPart = whatIsItemWornOn instanceof SLWearableType ? ((SLWearableType) whatIsItemWornOn).isBodyPart() : false;
                    if (this.avatarAppearance != null) {
                        if (z2) {
                            if (!sLInventoryEntry.isWearable()) {
                                z = this.avatarAppearance.canDetachItem(sLInventoryEntry);
                            } else if (this.avatarAppearance.canTakeItemOff(sLInventoryEntry)) {
                                z = !isBodyPart;
                            }
                        }
                        view.findViewById(R.id.item_worn_checkbox).setVisibility(0);
                        view.findViewById(R.id.item_worn_checkbox).setTag(R.id.tag_outfit_object, item);
                        ((CheckBox) view.findViewById(R.id.item_worn_checkbox)).setChecked(z2);
                        view.findViewById(R.id.item_worn_checkbox).setEnabled(z);
                        view.findViewById(R.id.item_worn_checkbox).setOnClickListener(this);
                    }
                    z = false;
                    view.findViewById(R.id.item_worn_checkbox).setVisibility(0);
                    view.findViewById(R.id.item_worn_checkbox).setTag(R.id.tag_outfit_object, item);
                    ((CheckBox) view.findViewById(R.id.item_worn_checkbox)).setChecked(z2);
                    view.findViewById(R.id.item_worn_checkbox).setEnabled(z);
                    view.findViewById(R.id.item_worn_checkbox).setOnClickListener(this);
                } else {
                    view.findViewById(R.id.item_worn_checkbox).setVisibility(8);
                    view.findViewById(R.id.item_worn_checkbox).setTag(R.id.tag_outfit_object, (Object) null);
                }
            } else {
                view.findViewById(R.id.item_worn_checkbox).setVisibility(8);
                View findViewById = view.findViewById(R.id.itemWornIcon);
                if (!isItemWorn(item)) {
                    i4 = 8;
                }
                findViewById.setVisibility(i4);
            }
        }
        return view;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onClick(View view) {
        if (this.onItemCheckboxClickListener != null) {
            Object tag = view.getTag(R.id.tag_outfit_object);
            if (tag instanceof SLInventoryEntry) {
                this.onItemCheckboxClickListener.onItemCheckboxClicked((SLInventoryEntry) tag);
            }
        }
    }

    public void setAvatarAppearance(@Nullable SLAvatarAppearance sLAvatarAppearance) {
        this.avatarAppearance = sLAvatarAppearance;
        notifyDataSetChanged();
    }

    public void setData(@Nullable InventoryEntryList inventoryEntryList) {
        if (inventoryEntryList == null) {
            inventoryEntryList = new InventoryEntryList();
        }
        this.data = inventoryEntryList;
        notifyDataSetChanged();
    }

    public void setDatabase(InventoryDB inventoryDB) {
        this.database = inventoryDB;
        notifyDataSetChanged();
    }

    public void setOnItemCheckboxClickListener(@Nullable OnItemCheckboxClickListener onItemCheckboxClickListener2) {
        this.onItemCheckboxClickListener = onItemCheckboxClickListener2;
    }

    public void setWornAttachments(@Nullable ImmutableMap<UUID, String> immutableMap) {
        this.wornAttachments = immutableMap;
        notifyDataSetChanged();
    }

    public void setWornOutfitFolder(@Nullable UUID uuid) {
        this.wornOutfitFolder = uuid;
        notifyDataSetChanged();
    }

    public void setWornWearables(@Nullable Table<SLWearableType, UUID, SLWearable> table) {
        this.wornWearables = table;
        notifyDataSetChanged();
    }
}
