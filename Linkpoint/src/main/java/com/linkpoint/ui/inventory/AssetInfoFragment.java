package com.lumiyaviewer.lumiya.ui.inventory;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryType;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AssetInfoFragment extends FragmentWithTitle implements ReloadableFragment, View.OnClickListener, LoadableMonitor.OnLoadableDataChangedListener {
    private static final String ITEM_UUID_KEY = "itemUUID";
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private ChatterNameRetriever creatorNameRetriever = null;
    private final SubscriptionData<UUID, SLInventoryEntry> entrySubscription = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final InventoryFragmentHelper inventoryFragmentHelper = new InventoryFragmentHelper(this);
    private ChatterNameRetriever lastOwnerNameRetriever = null;
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.entrySubscription).withOptionalLoadables(this.wornAttachments, this.wornWearables, this.agentCircuit, this.runningAnimations).withDataChangedListener(this);
    private MenuItem menuItemCopy;
    private MenuItem menuItemCut;
    private MenuItem menuItemDelete;
    private MenuItem menuItemRename;
    private MenuItem menuItemShare;
    private final ChatterNameRetriever.OnChatterNameUpdated onNameUpdated = new ChatterNameRetriever.OnChatterNameUpdated(this) {

        /* renamed from: -$f0 */
        private final /* synthetic */ Object f430$f0;

        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$OIe5MtmKyVPF26gruCQoZkxXroQ.1.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):void, dex: classes.dex
        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$OIe5MtmKyVPF26gruCQoZkxXroQ.1.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):void, class status: UNLOADED
        	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
        	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
        	at java.util.ArrayList.forEach(ArrayList.java:1259)
        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
        	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
        	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
        	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
        	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
        
*/

    };
    private ChatterNameRetriever ownerNameRetriever = null;
    private final SubscriptionData<SubscriptionSingleKey, ImmutableSet<UUID>> runningAnimations = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<SubscriptionSingleKey, ImmutableMap<UUID, String>> wornAttachments = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<SubscriptionSingleKey, Table<SLWearableType, UUID, SLWearable>> wornWearables = new SubscriptionData<>(UIThreadExecutor.getInstance());

    private void applyEditedPermissions(Dialog dialog) {
        SLInventory inventory;
        SLInventoryEntry data = this.entrySubscription.getData();
        if (data != null && (inventory = this.inventoryFragmentHelper.getInventory()) != null) {
            data.nextOwnerMask = data.ownerMask & getCheckboxes(dialog, R.id.asset_permission_cb_next_owner_copy, R.id.asset_permission_cb_next_owner_modify, R.id.asset_permission_cb_next_owner_transfer);
            data.groupMask = data.ownerMask & getCheckboxes(dialog, R.id.asset_permission_cb_group_copy, R.id.asset_permission_cb_group_modify, R.id.asset_permission_cb_group_transfer);
            data.everyoneMask = data.ownerMask & getCheckboxes(dialog, R.id.asset_permission_cb_everyone_copy, R.id.asset_permission_cb_everyone_modify, R.id.asset_permission_cb_everyone_transfer);
            inventory.UpdateStoreInventoryItem(data);
            showEntryInfo(data);
        }
    }

    private void attachObject(SLInventoryEntry sLInventoryEntry) {
        try {
            this.agentCircuit.get().getModules().avatarAppearance.AttachInventoryItem(sLInventoryEntry, 0, false);
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
    }

    private void detachObject(SLInventoryEntry sLInventoryEntry) {
        try {
            this.agentCircuit.get().getModules().avatarAppearance.DetachInventoryItem(sLInventoryEntry);
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
    }

    private int getCheckboxes(Dialog dialog, int i, int i2, int i3) {
        int i4 = 0;
        if (((CheckBox) dialog.findViewById(i)).isChecked()) {
            i4 = 32768;
        }
        if (((CheckBox) dialog.findViewById(i2)).isChecked()) {
            i4 |= 16384;
        }
        return ((CheckBox) dialog.findViewById(i3)).isChecked() ? i4 | 8192 : i4;
    }

    public static Bundle makeSelection(UUID uuid, UUID uuid2) {
        Bundle bundle = new Bundle();
        if (uuid != null) {
            bundle.putString("activeAgentUUID", uuid.toString());
        }
        if (uuid2 != null) {
            bundle.putString(ITEM_UUID_KEY, uuid2.toString());
        }
        return bundle;
    }

    private void playAnimation(SLInventoryEntry sLInventoryEntry, boolean z) {
        SLAgentCircuit data = this.agentCircuit.getData();
        if (data != null) {
            data.getModules().avatarControl.playAnimation(sLInventoryEntry.assetUUID, z);
        }
    }

    private void setCheckboxes(Dialog dialog, int i, int i2, int i3, int i4, int i5, boolean z) {
        boolean z2 = false;
        ((CheckBox) dialog.findViewById(i3)).setChecked((i & 32768) != 0);
        ((CheckBox) dialog.findViewById(i4)).setChecked((i & 16384) != 0);
        ((CheckBox) dialog.findViewById(i5)).setChecked((i & 8192) != 0);
        dialog.findViewById(i3).setEnabled(z && (i2 & 32768) != 0);
        dialog.findViewById(i4).setEnabled(z && (i2 & 16384) != 0);
        View findViewById = dialog.findViewById(i5);
        if (z && (i2 & 8192) != 0) {
            z2 = true;
        }
        findViewById.setEnabled(z2);
    }

    private void showEditPermissionsDialog() {
        SLInventoryEntry data = this.entrySubscription.getData();
        if (data != null) {
            Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.asset_permissions);
            dialog.setTitle(R.string.edit_permissions_title);
            setCheckboxes(dialog, data.ownerMask, data.ownerMask, R.id.asset_permission_cb_owner_copy, R.id.asset_permission_cb_owner_modify, R.id.asset_permission_cb_owner_transfer, false);
            setCheckboxes(dialog, data.nextOwnerMask, data.ownerMask, R.id.asset_permission_cb_next_owner_copy, R.id.asset_permission_cb_next_owner_modify, R.id.asset_permission_cb_next_owner_transfer, true);
            setCheckboxes(dialog, data.groupMask, data.ownerMask, R.id.asset_permission_cb_group_copy, R.id.asset_permission_cb_group_modify, R.id.asset_permission_cb_group_transfer, true);
            setCheckboxes(dialog, data.everyoneMask, data.ownerMask, R.id.asset_permission_cb_everyone_copy, R.id.asset_permission_cb_everyone_modify, R.id.asset_permission_cb_everyone_transfer, true);
            ((CheckBox) dialog.findViewById(R.id.asset_permission_cb_everyone_modify)).setChecked(false);
            dialog.findViewById(R.id.asset_permission_cb_everyone_modify).setEnabled(false);
            dialog.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener(this, dialog) {

                /* renamed from: -$f0 */
                private final /* synthetic */ Object f432$f0;

                /* renamed from: -$f1 */
                private final /* synthetic */ Object f433$f1;

                private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$OIe5MtmKyVPF26gruCQoZkxXroQ.3.$m$0(android.view.View):void, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$OIe5MtmKyVPF26gruCQoZkxXroQ.3.$m$0(android.view.View):void, class status: UNLOADED
                	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
                	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                
*/

            dialog.findViewById(R.id.cancelButton).setOnClickListener(new $Lambda$OIe5MtmKyVPF26gruCQoZkxXroQ(dialog));
            dialog.show();
        }
    }

    private void showEntry(@Nullable UUID uuid) {
        this.loadableMonitor.unsubscribeAll();
        UserManager userManager = ActivityUtils.getUserManager(getArguments());
        if (uuid == null || userManager == null) {
            if (this.creatorNameRetriever != null) {
                this.creatorNameRetriever.dispose();
            }
            if (this.ownerNameRetriever != null) {
                this.ownerNameRetriever.dispose();
            }
            if (this.lastOwnerNameRetriever != null) {
                this.lastOwnerNameRetriever.dispose();
                return;
            }
            return;
        }
        this.agentCircuit.subscribe(UserManager.agentCircuits(), userManager.getUserID());
        this.entrySubscription.subscribe(userManager.getInventoryManager().getFolderEntryPool(), uuid);
        this.wornAttachments.subscribe(userManager.getWornAttachmentsPool(), SubscriptionSingleKey.Value);
        this.wornWearables.subscribe(userManager.getWornWearablesPool(), SubscriptionSingleKey.Value);
        this.runningAnimations.subscribe(userManager.getObjectsManager().runningAnimations(), SubscriptionSingleKey.Value);
    }

    private void showEntryInfo(@Nonnull SLInventoryEntry sLInventoryEntry) {
        int i = 0;
        int i2 = 8;
        View view = getView();
        if (view != null) {
            ((TextView) view.findViewById(R.id.asset_info_name)).setText(sLInventoryEntry.name);
            ((TextView) view.findViewById(R.id.asset_info_description)).setText(!Strings.isNullOrEmpty(sLInventoryEntry.description) ? sLInventoryEntry.description : getResources().getString(R.string.asset_no_description));
            ((TextView) view.findViewById(R.id.asset_info_type)).setText(sLInventoryEntry.getTypeDescriptionResId());
            int drawableResource = sLInventoryEntry.getDrawableResource();
            if (drawableResource >= 0) {
                ((ImageView) view.findViewById(R.id.asset_info_icon)).setImageResource(drawableResource);
            } else {
                ((ImageView) view.findViewById(R.id.asset_info_icon)).setImageBitmap((Bitmap) null);
            }
            int actionDescriptionResId = sLInventoryEntry.getActionDescriptionResId();
            if (actionDescriptionResId >= 0) {
                ((Button) view.findViewById(R.id.asset_action_button)).setText(actionDescriptionResId);
                view.findViewById(R.id.asset_action_button).setVisibility(0);
                view.findViewById(R.id.asset_action_button).setEnabled(this.inventoryFragmentHelper.isActionAllowed(sLInventoryEntry, actionDescriptionResId));
            } else {
                view.findViewById(R.id.asset_action_button).setVisibility(8);
            }
            view.findViewById(R.id.edit_permissions_button).setVisibility((sLInventoryEntry.ownerMask & 16384) != 0 ? 0 : 8);
            showPermissions(sLInventoryEntry.ownerMask, R.id.asset_permission_owner_copy, R.id.asset_permission_owner_modify, R.id.asset_permission_owner_transfer);
            showPermissions(sLInventoryEntry.groupMask, R.id.asset_permission_group_copy, R.id.asset_permission_group_modify, R.id.asset_permission_group_transfer);
            showPermissions(sLInventoryEntry.everyoneMask, R.id.asset_permission_everyone_copy, R.id.asset_permission_everyone_modify, R.id.asset_permission_everyone_transfer);
            showPermissions(sLInventoryEntry.nextOwnerMask, R.id.asset_permission_next_owner_copy, R.id.asset_permission_next_owner_modify, R.id.asset_permission_next_owner_transfer);
            SLAgentCircuit data = this.agentCircuit.getData();
            boolean z = data != null;
            if (!z || !(sLInventoryEntry.assetType == SLAssetType.AT_OBJECT.getTypeCode() || (sLInventoryEntry.assetType == SLAssetType.AT_LINK.getTypeCode() && sLInventoryEntry.invType == SLInventoryType.IT_OBJECT.getTypeCode()))) {
                view.findViewById(R.id.asset_attach_button).setVisibility(8);
                view.findViewById(R.id.asset_detach_button).setVisibility(8);
            } else {
                boolean z2 = sLInventoryEntry.whatIsItemWornOn(this.wornAttachments.getData(), this.wornWearables.getData(), false) != null;
                boolean canDetachItem = z2 ? data.getModules().avatarAppearance.canDetachItem(sLInventoryEntry) : false;
                view.findViewById(R.id.asset_attach_button).setVisibility(z2 ? 8 : 0);
                view.findViewById(R.id.asset_detach_button).setVisibility(canDetachItem ? 0 : 8);
            }
            if (!z || !sLInventoryEntry.isAnimation()) {
                view.findViewById(R.id.asset_play_anim_button).setVisibility(8);
                view.findViewById(R.id.asset_stop_anim_button).setVisibility(8);
            } else {
                ImmutableSet data2 = this.runningAnimations.getData();
                view.findViewById(R.id.asset_play_anim_button).setVisibility((data2 == null || !(data2.contains(sLInventoryEntry.assetUUID) ^ true)) ? 8 : 0);
                view.findViewById(R.id.asset_stop_anim_button).setVisibility((data2 == null || !data2.contains(sLInventoryEntry.assetUUID)) ? 8 : 0);
            }
            if (!z || !sLInventoryEntry.isWearable()) {
                view.findViewById(R.id.asset_wear_button).setVisibility(8);
                view.findViewById(R.id.asset_take_off_button).setVisibility(8);
                view.findViewById(R.id.asset_worn_text).setVisibility(8);
            } else {
                Object whatIsItemWornOn = sLInventoryEntry.whatIsItemWornOn(this.wornAttachments.getData(), this.wornWearables.getData(), false);
                if (whatIsItemWornOn instanceof SLWearableType) {
                    view.findViewById(R.id.asset_wear_button).setVisibility(8);
                    if (((SLWearableType) whatIsItemWornOn).isBodyPart()) {
                        view.findViewById(R.id.asset_take_off_button).setVisibility(8);
                    } else {
                        view.findViewById(R.id.asset_take_off_button).setVisibility(data.getModules().avatarAppearance.canTakeItemOff((SLWearableType) whatIsItemWornOn) ? 0 : 8);
                    }
                    View findViewById = view.findViewById(R.id.asset_worn_text);
                    if (view.findViewById(R.id.asset_take_off_button).getVisibility() != 0) {
                        i2 = 0;
                    }
                    findViewById.setVisibility(i2);
                } else {
                    view.findViewById(R.id.asset_take_off_button).setVisibility(8);
                    boolean canWearItem = data.getModules().avatarAppearance.canWearItem(sLInventoryEntry);
                    View findViewById2 = view.findViewById(R.id.asset_wear_button);
                    if (!canWearItem) {
                        i = 8;
                    }
                    findViewById2.setVisibility(i);
                    view.findViewById(R.id.asset_worn_text).setVisibility(8);
                }
            }
        }
        updateMenuItems();
    }

    private void showPermissions(int i, int i2, int i3, int i4) {
        View view = getView();
        if (view != null) {
            TextView textView = (TextView) view.findViewById(i2);
            TextView textView2 = (TextView) view.findViewById(i3);
            TextView textView3 = (TextView) view.findViewById(i4);
            if ((32768 & i) != 0) {
                textView.setPaintFlags(textView.getPaintFlags() & -17);
            } else {
                textView.setPaintFlags(textView.getPaintFlags() | 16);
            }
            if ((i & 16384) != 0) {
                textView2.setPaintFlags(textView2.getPaintFlags() & -17);
            } else {
                textView2.setPaintFlags(textView2.getPaintFlags() | 16);
            }
            if ((i & 8192) != 0) {
                textView3.setPaintFlags(textView3.getPaintFlags() & -17);
            } else {
                textView3.setPaintFlags(textView3.getPaintFlags() | 16);
            }
        }
    }

    private void showProfile(UUID uuid) {
        UserManager userManager = ActivityUtils.getUserManager(getArguments());
        if (uuid != null && (!Objects.equal(uuid, UUIDPool.ZeroUUID)) && userManager != null) {
            DetailsActivity.showEmbeddedDetails(getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(ChatterID.getUserChatterID(userManager.getUserID(), uuid)));
        }
    }

    private void showUserInfo(UUID uuid, ChatterNameRetriever chatterNameRetriever, int i, int i2, int i3) {
        View view = getView();
        if (view == null) {
            return;
        }
        if (uuid == null || Objects.equal(uuid, UUIDPool.ZeroUUID) || chatterNameRetriever == null) {
            view.findViewById(i).setVisibility(8);
            return;
        }
        view.findViewById(i).setVisibility(0);
        String resolvedName = chatterNameRetriever.getResolvedName();
        ((TextView) view.findViewById(i2)).setText(resolvedName != null ? resolvedName : getString(R.string.name_loading_title));
        ((ChatterPicView) view.findViewById(i3)).setChatterID(chatterNameRetriever.chatterID, resolvedName);
    }

    private void takeOffObject(SLInventoryEntry sLInventoryEntry) {
        try {
            this.agentCircuit.get().getModules().avatarAppearance.TakeItemOff(sLInventoryEntry);
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
    }

    private void updateMenuItems() {
        if (this.menuItemCopy != null && this.menuItemCut != null && this.menuItemShare != null && this.menuItemRename != null && this.menuItemDelete != null) {
            try {
                this.agentCircuit.assertHasData();
                SLInventoryEntry sLInventoryEntry = this.entrySubscription.get();
                this.menuItemDelete.setVisible(true);
                this.menuItemRename.setVisible(((sLInventoryEntry.baseMask & sLInventoryEntry.ownerMask) & 16384) != 0);
                this.menuItemShare.setVisible(((sLInventoryEntry.ownerMask & sLInventoryEntry.baseMask) & 8192) != 0);
                this.menuItemCut.setVisible(true);
                this.menuItemCopy.setVisible(true);
            } catch (SubscriptionData.DataNotReadyException e) {
                this.menuItemDelete.setVisible(false);
                this.menuItemRename.setVisible(false);
                this.menuItemShare.setVisible(false);
                this.menuItemCut.setVisible(false);
                this.menuItemCopy.setVisible(false);
            }
        }
    }

    private void wearObject(SLInventoryEntry sLInventoryEntry) {
        try {
            this.agentCircuit.get().getModules().avatarAppearance.WearItem(sLInventoryEntry, false);
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_AssetInfoFragment_10987  reason: not valid java name */
    public /* synthetic */ void m583lambda$com_lumiyaviewer_lumiya_ui_inventory_AssetInfoFragment_10987(ChatterNameRetriever chatterNameRetriever) {
        SLInventoryEntry data = this.entrySubscription.getData();
        if (data != null) {
            showUserInfo(data.ownerUUID, this.ownerNameRetriever, R.id.asset_owner_card_view, R.id.asset_owner_name, R.id.asset_owner_pic);
            showUserInfo(data.creatorUUID, this.creatorNameRetriever, R.id.asset_creator_card_view, R.id.asset_creator_name, R.id.asset_creator_pic);
            showUserInfo(data.lastOwnerUUID, this.lastOwnerNameRetriever, R.id.asset_last_owner_card_view, R.id.asset_last_owner_name, R.id.asset_last_owner_pic);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_AssetInfoFragment_20027  reason: not valid java name */
    public /* synthetic */ void m584lambda$com_lumiyaviewer_lumiya_ui_inventory_AssetInfoFragment_20027() {
        FragmentActivity activity = getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).closeDetailsFragment(this);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_inventory_AssetInfoFragment_26713  reason: not valid java name */
    public /* synthetic */ void m585lambda$com_lumiyaviewer_lumiya_ui_inventory_AssetInfoFragment_26713(Dialog dialog, View view) {
        applyEditedPermissions(dialog);
        dialog.dismiss();
    }

    public void onClick(View view) {
        SLInventoryEntry data = this.entrySubscription.getData();
        if (data != null) {
            switch (view.getId()) {
                case R.id.asset_action_button:
                    int actionDescriptionResId = data.getActionDescriptionResId();
                    if (actionDescriptionResId >= 0 && this.inventoryFragmentHelper.isActionAllowed(data, actionDescriptionResId)) {
                        this.inventoryFragmentHelper.PerformInventoryAction(data, actionDescriptionResId);
                        return;
                    }
                    return;
                case R.id.asset_attach_button:
                    attachObject(data);
                    return;
                case R.id.asset_detach_button:
                    detachObject(data);
                    return;
                case R.id.asset_wear_button:
                    wearObject(data);
                    return;
                case R.id.asset_take_off_button:
                    takeOffObject(data);
                    return;
                case R.id.asset_play_anim_button:
                    playAnimation(data, true);
                    return;
                case R.id.asset_stop_anim_button:
                    playAnimation(data, false);
                    return;
                case R.id.edit_permissions_button:
                    showEditPermissionsDialog();
                    return;
                case R.id.asset_owner_button:
                    showProfile(data.ownerUUID);
                    return;
                case R.id.asset_creator_button:
                    showProfile(data.creatorUUID);
                    return;
                case R.id.asset_last_owner_button:
                    showProfile(data.lastOwnerUUID);
                    return;
                default:
                    return;
            }
        }
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.inventory_item_menu, menu);
        this.menuItemDelete = menu.findItem(R.id.inventory_item_delete_item);
        this.menuItemRename = menu.findItem(R.id.inventory_item_rename_item);
        this.menuItemShare = menu.findItem(R.id.inventory_item_share_item);
        this.menuItemCut = menu.findItem(R.id.inventory_item_cut_item);
        this.menuItemCopy = menu.findItem(R.id.inventory_item_copy_item);
        updateMenuItems();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(R.layout.asset_info, viewGroup, false);
        ((LoadingLayout) inflate.findViewById(R.id.loading_layout)).setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_item_selected), getString(R.string.inventorY_item_loading_fail));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        inflate.findViewById(R.id.asset_creator_button).setOnClickListener(this);
        inflate.findViewById(R.id.asset_owner_button).setOnClickListener(this);
        inflate.findViewById(R.id.asset_last_owner_button).setOnClickListener(this);
        inflate.findViewById(R.id.asset_action_button).setOnClickListener(this);
        inflate.findViewById(R.id.edit_permissions_button).setOnClickListener(this);
        inflate.findViewById(R.id.edit_permissions_button).setVisibility(8);
        inflate.findViewById(R.id.asset_attach_button).setOnClickListener(this);
        inflate.findViewById(R.id.asset_detach_button).setOnClickListener(this);
        inflate.findViewById(R.id.asset_wear_button).setOnClickListener(this);
        inflate.findViewById(R.id.asset_take_off_button).setOnClickListener(this);
        inflate.findViewById(R.id.asset_play_anim_button).setOnClickListener(this);
        inflate.findViewById(R.id.asset_stop_anim_button).setOnClickListener(this);
        return inflate;
    }

    public void onLoadableDataChanged() {
        UserManager userManager = ActivityUtils.getUserManager(getArguments());
        try {
            showEntryInfo(this.entrySubscription.get());
            if (userManager != null) {
                this.creatorNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(userManager.getUserID(), this.entrySubscription.get().creatorUUID), this.onNameUpdated, UIThreadExecutor.getInstance());
                this.ownerNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(userManager.getUserID(), this.entrySubscription.get().ownerUUID), this.onNameUpdated, UIThreadExecutor.getInstance());
                this.lastOwnerNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(userManager.getUserID(), this.entrySubscription.get().lastOwnerUUID), this.onNameUpdated, UIThreadExecutor.getInstance());
            }
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        UserManager userManager = ActivityUtils.getUserManager(getArguments());
        if (userManager != null) {
            try {
                SLInventoryEntry sLInventoryEntry = this.entrySubscription.get();
                switch (menuItem.getItemId()) {
                    case R.id.inventory_item_delete_item:
                        this.inventoryFragmentHelper.DeleteInventoryEntry(sLInventoryEntry, new Runnable(this) {

                            /* renamed from: -$f0 */
                            private final /* synthetic */ Object f431$f0;

                            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$OIe5MtmKyVPF26gruCQoZkxXroQ.2.$m$0():void, dex: classes.dex
                            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.ui.inventory.-$Lambda$OIe5MtmKyVPF26gruCQoZkxXroQ.2.$m$0():void, class status: UNLOADED
                            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
                            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                            	at jadx.core.codegen.RegionGen.makeSwitch(RegionGen.java:298)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:64)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                            	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:311)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:68)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                            	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                            
*/

                        return true;
                    case R.id.inventory_item_rename_item:
                        this.inventoryFragmentHelper.RenameInventoryEntry(sLInventoryEntry);
                        return true;
                    case R.id.inventory_item_share_item:
                        this.inventoryFragmentHelper.ShareInventoryEntry(sLInventoryEntry);
                        return true;
                    case R.id.inventory_item_cut_item:
                        userManager.getInventoryManager().copyToClipboard(new InventoryManager.InventoryClipboardEntry(true, sLInventoryEntry));
                        Toast.makeText(getContext(), R.string.copied_to_clipboard, 1).show();
                        return true;
                    case R.id.inventory_item_copy_item:
                        userManager.getInventoryManager().copyToClipboard(new InventoryManager.InventoryClipboardEntry(false, sLInventoryEntry));
                        Toast.makeText(getContext(), R.string.copied_to_clipboard, 1).show();
                        return true;
                }
            } catch (SubscriptionData.DataNotReadyException e) {
                Debug.Warning(e);
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        updateMenuItems();
    }

    public void onStart() {
        super.onStart();
        showEntry(UUIDPool.getUUID(getArguments().getString(ITEM_UUID_KEY)));
    }

    public void onStop() {
        showEntry((UUID) null);
        super.onStop();
    }

    public void setFragmentArgs(Intent intent, Bundle bundle) {
        UUID activeAgentID = ActivityUtils.getActiveAgentID(intent);
        if (activeAgentID != null) {
            getArguments().putString("activeAgentUUID", activeAgentID.toString());
        }
        if (bundle != null) {
            getArguments().putAll(bundle);
        }
        if (isFragmentStarted()) {
            showEntry(UUIDPool.getUUID(getArguments().getString(ITEM_UUID_KEY)));
        }
    }
}
