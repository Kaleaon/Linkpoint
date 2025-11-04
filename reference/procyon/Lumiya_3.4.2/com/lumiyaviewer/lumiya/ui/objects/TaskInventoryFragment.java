// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.objects;

import android.view.MenuItem;
import android.widget.AdapterView$OnItemClickListener;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.Menu;
import android.widget.AdapterView;
import android.os.Bundle;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.ui.inventory.NotecardEditActivity;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.Set;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import java.util.UUID;
import com.google.common.base.Function;
import android.app.ProgressDialog;
import java.util.HashSet;
import android.content.DialogInterface$OnClickListener;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.widget.ListAdapter;
import android.view.View;
import android.widget.TextView;
import android.widget.ListView;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLTaskInventory;
import com.lumiyaviewer.lumiya.react.Subscription;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;

public class TaskInventoryFragment extends FragmentWithTitle
{
    private static final String OBJECT_LOCAL_ID_KEY = "objectLocalId";
    private static final String OBJECT_UUID_KEY = "objectUUID";
    @Nullable
    private SLObjectProfileData objectProfileData;
    private Subscription<Integer, SLObjectProfileData> objectProfileSubscription;
    private final Subscription.OnData<SLObjectProfileData> onObjectProfileData;
    private final Subscription.OnData<SLTaskInventory> onTaskInventoryReceived;
    @Nullable
    private SLTaskInventory taskInventory;
    private Subscription<Integer, SLTaskInventory> taskInventorySubscription;
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues() {
        if (TaskInventoryFragment.-com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues != null) {
            return TaskInventoryFragment.-com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues = new int[SLAssetType.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_ANIMATION.ordinal()] = 3;
                try {
                    -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_BODYPART.ordinal()] = 4;
                    try {
                        -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_CALLINGCARD.ordinal()] = 5;
                        try {
                            -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_CATEGORY.ordinal()] = 6;
                            try {
                                -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_CLOTHING.ordinal()] = 7;
                                try {
                                    -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_GESTURE.ordinal()] = 8;
                                    try {
                                        -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_IMAGE_JPEG.ordinal()] = 9;
                                        try {
                                            -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_IMAGE_TGA.ordinal()] = 10;
                                            try {
                                                -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_LANDMARK.ordinal()] = 11;
                                                try {
                                                    -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_LINK.ordinal()] = 12;
                                                    try {
                                                        -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_LINK_FOLDER.ordinal()] = 13;
                                                        try {
                                                            -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_LSL_BYTECODE.ordinal()] = 14;
                                                            try {
                                                                -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_LSL_TEXT.ordinal()] = 1;
                                                                try {
                                                                    -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_MESH.ordinal()] = 15;
                                                                    try {
                                                                        -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_NOTECARD.ordinal()] = 2;
                                                                        try {
                                                                            -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_OBJECT.ordinal()] = 16;
                                                                            try {
                                                                                -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_SCRIPT.ordinal()] = 17;
                                                                                try {
                                                                                    -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_SIMSTATE.ordinal()] = 18;
                                                                                    try {
                                                                                        -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_SOUND.ordinal()] = 19;
                                                                                        try {
                                                                                            -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_SOUND_WAV.ordinal()] = 20;
                                                                                            try {
                                                                                                -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_TEXTURE.ordinal()] = 21;
                                                                                                try {
                                                                                                    -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_TEXTURE_TGA.ordinal()] = 22;
                                                                                                    try {
                                                                                                        -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_UNKNOWN.ordinal()] = 23;
                                                                                                        try {
                                                                                                            -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues[SLAssetType.AT_WIDGET.ordinal()] = 24;
                                                                                                            return -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues = -com-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues;
                                                                                                        }
                                                                                                        catch (final NoSuchFieldError noSuchFieldError) {}
                                                                                                    }
                                                                                                    catch (final NoSuchFieldError noSuchFieldError2) {}
                                                                                                }
                                                                                                catch (final NoSuchFieldError noSuchFieldError3) {}
                                                                                            }
                                                                                            catch (final NoSuchFieldError noSuchFieldError4) {}
                                                                                        }
                                                                                        catch (final NoSuchFieldError noSuchFieldError5) {}
                                                                                    }
                                                                                    catch (final NoSuchFieldError noSuchFieldError6) {}
                                                                                }
                                                                                catch (final NoSuchFieldError noSuchFieldError7) {}
                                                                            }
                                                                            catch (final NoSuchFieldError noSuchFieldError8) {}
                                                                        }
                                                                        catch (final NoSuchFieldError noSuchFieldError9) {}
                                                                    }
                                                                    catch (final NoSuchFieldError noSuchFieldError10) {}
                                                                }
                                                                catch (final NoSuchFieldError noSuchFieldError11) {}
                                                            }
                                                            catch (final NoSuchFieldError noSuchFieldError12) {}
                                                        }
                                                        catch (final NoSuchFieldError noSuchFieldError13) {}
                                                    }
                                                    catch (final NoSuchFieldError noSuchFieldError14) {}
                                                }
                                                catch (final NoSuchFieldError noSuchFieldError15) {}
                                            }
                                            catch (final NoSuchFieldError noSuchFieldError16) {}
                                        }
                                        catch (final NoSuchFieldError noSuchFieldError17) {}
                                    }
                                    catch (final NoSuchFieldError noSuchFieldError18) {}
                                }
                                catch (final NoSuchFieldError noSuchFieldError19) {}
                            }
                            catch (final NoSuchFieldError noSuchFieldError20) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError21) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError22) {}
                }
                catch (final NoSuchFieldError noSuchFieldError23) {}
            }
            catch (final NoSuchFieldError noSuchFieldError24) {
                continue;
            }
            break;
        }
    }
    
    public TaskInventoryFragment() {
        this.objectProfileData = null;
        this.onTaskInventoryReceived = new Subscription.OnData<SLTaskInventory>() {
            public void onData(final SLTaskInventory data) {
                TaskInventoryFragment.this.taskInventory = data;
                final View view = TaskInventoryFragment.this.getView();
                if (view != null) {
                    final ListAdapter adapter = ((ListView)view.findViewById(2131755660)).getAdapter();
                    if (adapter instanceof TaskInventoryListAdapter) {
                        ((TaskInventoryListAdapter)adapter).setData(data);
                    }
                    ((TextView)view.findViewById(2131755662)).setText(2131296792);
                    view.findViewById(2131755661).setVisibility(8);
                }
            }
        };
        this.onObjectProfileData = new _$Lambda$2R1p9WuPUwPagPVBm9YiYK9KyJ0$3(this);
    }
    
    private boolean canModifyObject() {
        final UserManager userManager = this.getUserManager();
        return this.objectProfileData != null && userManager != null && userManager.getUserID().equals(this.objectProfileData.ownerUUID()) && this.objectProfileData.isModifiable();
    }
    
    private boolean canModifyObjectContents(final SLInventoryEntry slInventoryEntry) {
        final boolean b = true;
        boolean b2 = true;
        final UserManager userManager = this.getUserManager();
        if (userManager == null) {
            return false;
        }
        if (userManager.getUserID().equals(slInventoryEntry.ownerUUID)) {
            if ((slInventoryEntry.ownerMask & 0x4000) == 0x0) {
                b2 = false;
            }
            return b2;
        }
        return (slInventoryEntry.everyoneMask & 0x4000) != 0x0 && b;
    }
    
    private void copyAllToInventory(final boolean b) {
        final int objectLocalID = this.getObjectLocalID();
        final UserManager userManager = this.getUserManager();
        if (this.taskInventory == null || this.objectProfileData == null || userManager == null) {
            return;
        }
        final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (activeAgentCircuit == null) {
            return;
        }
        final SLModules modules = activeAgentCircuit.getModules();
        if (modules == null) {
            return;
        }
        final SLInventory inventory = modules.inventory;
        if (this.taskInventory.entries.size() == 0) {
            return;
        }
        if (!userManager.getUserID().equals(this.objectProfileData.ownerUUID())) {
            Toast.makeText((Context)this.getActivity(), 2131296795, 1).show();
            return;
        }
        if (!b) {
            final Iterator<Object> iterator = this.taskInventory.entries.iterator();
            boolean b2 = false;
            while (iterator.hasNext()) {
                if ((iterator.next().ownerMask & 0x8000) == 0x0) {
                    b2 = true;
                }
            }
            if (b2) {
                final AlertDialog.Builder builder = new AlertDialog.Builder((Context)this.getActivity());
                builder.setMessage(2131296793).setPositiveButton(2131296797, (DialogInterface$OnClickListener)new _$Lambda$2R1p9WuPUwPagPVBm9YiYK9KyJ0$1(this)).setNegativeButton(2131296424, (DialogInterface$OnClickListener)new _$Lambda$2R1p9WuPUwPagPVBm9YiYK9KyJ0());
                builder.create().show();
                return;
            }
        }
        final String s = this.objectProfileData.name().or(this.getString(2131296474));
        final HashSet set = new HashSet();
        final Iterator<Object> iterator2 = this.taskInventory.entries.iterator();
        while (iterator2.hasNext()) {
            set.add(iterator2.next().uuid);
        }
        inventory.CopyObjectContents(s, objectLocalID, set, new Function<UUID, Void>() {
            final /* synthetic */ ProgressDialog val$progressDialog = ProgressDialog.show(this.getContext(), (CharSequence)null, (CharSequence)this.getString(2131296465), true, true);
            
            @Nullable
            @Override
            public Void apply(@Nullable final UUID uuid) {
                UIThreadExecutor.getInstance().execute(new _$Lambda$2R1p9WuPUwPagPVBm9YiYK9KyJ0$4(objectLocalID, this, userManager, this.val$progressDialog, uuid));
                return null;
            }
        });
    }
    
    private int getObjectLocalID() {
        return this.getArguments().getInt("objectLocalId");
    }
    
    private UUID getObjectUUID() {
        return UUID.fromString(this.getArguments().getString("objectUUID"));
    }
    
    @Nullable
    private UserManager getUserManager() {
        return ActivityUtils.getUserManager(this.getArguments());
    }
    
    private void handleObjectClick(final int n) {
        final UserManager userManager = this.getUserManager();
        if (this.getView() != null && userManager != null) {
            final ListAdapter adapter = ((ListView)this.getView().findViewById(2131755660)).getAdapter();
            if (adapter instanceof TaskInventoryListAdapter) {
                final SLInventoryEntry item = ((TaskInventoryListAdapter)adapter).getItem(n);
                if (item != null) {
                    switch (-getcom-lumiyaviewer-lumiya-slproto-inventory-SLAssetTypeSwitchesValues()[SLAssetType.getByType(item.assetType).ordinal()]) {
                        case 2: {
                            if (this.canModifyObject() && this.canModifyObjectContents(item)) {
                                this.startActivity(NotecardEditActivity.createIntent(this.getContext(), userManager.getUserID(), null, item, false, this.getObjectUUID(), this.getObjectLocalID()));
                                break;
                            }
                            break;
                        }
                        case 1: {
                            if (this.canModifyObject() && this.canModifyObjectContents(item)) {
                                this.startActivity(NotecardEditActivity.createIntent(this.getContext(), userManager.getUserID(), null, item, true, this.getObjectUUID(), this.getObjectLocalID()));
                                break;
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
    
    public static Bundle makeSelection(final UUID uuid, final UUID uuid2, final int n) {
        final Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        if (uuid2 != null) {
            bundle.putString("objectUUID", uuid2.toString());
        }
        bundle.putInt("objectLocalId", n);
        return bundle;
    }
    
    @Override
    public void onCreate(@android.support.annotation.Nullable final Bundle bundle) {
        super.onCreate(bundle);
        this.setHasOptionsMenu(true);
    }
    
    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(2131886112, menu);
    }
    
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        final View inflate = layoutInflater.inflate(2130968743, viewGroup, false);
        ((ListView)inflate.findViewById(2131755660)).setAdapter((ListAdapter)new TaskInventoryListAdapter(layoutInflater.getContext()));
        ((ListView)inflate.findViewById(2131755660)).setEmptyView(inflate.findViewById(16908292));
        ((ListView)inflate.findViewById(2131755660)).setOnItemClickListener((AdapterView$OnItemClickListener)new _$Lambda$2R1p9WuPUwPagPVBm9YiYK9KyJ0$2(this));
        return inflate;
    }
    
    @Override
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            default: {
                return super.onOptionsItemSelected(menuItem);
            }
            case 2131755858: {
                this.copyAllToInventory(false);
                return true;
            }
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();
        this.setTitle(this.getString(2131296796), null);
        final UserManager userManager = this.getUserManager();
        if (userManager != null) {
            final int objectLocalID = this.getObjectLocalID();
            this.taskInventorySubscription = userManager.getObjectsManager().getObjectTaskInventory().subscribe(objectLocalID, UIThreadExecutor.getInstance(), this.onTaskInventoryReceived);
            this.objectProfileSubscription = userManager.getObjectsManager().getObjectProfile().subscribe(objectLocalID, UIThreadExecutor.getInstance(), this.onObjectProfileData);
        }
    }
    
    @Override
    public void onStop() {
        if (this.taskInventorySubscription != null) {
            this.taskInventorySubscription.unsubscribe();
            this.taskInventorySubscription = null;
        }
        if (this.objectProfileSubscription != null) {
            this.objectProfileSubscription.unsubscribe();
            this.objectProfileSubscription = null;
        }
        super.onStop();
    }
}
