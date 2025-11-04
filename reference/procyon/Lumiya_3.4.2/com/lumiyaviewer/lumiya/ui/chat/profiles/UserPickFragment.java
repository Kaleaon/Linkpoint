// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.Context;
import com.lumiyaviewer.lumiya.react.Subscribable;
import android.app.Activity;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import butterknife.ButterKnife;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.Menu;
import android.support.annotation.Nullable;
import butterknife.OnClick;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import android.view.View;
import android.widget.Toast;
import com.lumiyaviewer.lumiya.ui.common.TeleportProgressDialog;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.TextFieldDialogBuilder;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import com.lumiyaviewer.lumiya.Debug;
import android.os.Parcelable;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import butterknife.Unbinder;
import com.lumiyaviewer.lumiya.slproto.messages.PickInfoReply;
import com.lumiyaviewer.lumiya.slproto.users.manager.AvatarPickKey;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import android.widget.TextView;
import android.view.MenuItem;
import butterknife.BindView;
import android.widget.Button;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;

public class UserPickFragment extends FragmentWithTitle
{
    private static final String PICK_ID_KEY = "pickID";
    @BindView(2131755698)
    Button changePicButton;
    private MenuItem menuItemDelete;
    private MenuItem menuItemRename;
    @BindView(2131755699)
    TextView pickDescription;
    private final SubscriptionData<AvatarPickKey, PickInfoReply> pickInfo;
    @BindView(2131755696)
    Button setLocationButton;
    private Unbinder unbinder;
    @BindView(2131755700)
    Button userPickDescEditButton;
    @BindView(2131755694)
    ImageAssetView userPickImageView;
    
    public UserPickFragment() {
        this.pickInfo = new SubscriptionData<AvatarPickKey, PickInfoReply>(UIThreadExecutor.getInstance(), new _$Lambda$pe_zD6dKvPMIxwvN5gLJ2hSMvgo$3(this));
        this.menuItemRename = null;
        this.menuItemDelete = null;
        this.setArguments(new Bundle());
    }
    
    private void deletePick() {
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        final AvatarPickKey pickKey = this.getPickKey();
        if (userManager != null && pickKey != null) {
            final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(this.getContext());
            alertDialog$Builder.setMessage((CharSequence)this.getString(2131296480)).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$pe_zD6dKvPMIxwvN5gLJ2hSMvgo$4(this, userManager, pickKey)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$pe_zD6dKvPMIxwvN5gLJ2hSMvgo());
            alertDialog$Builder.create().show();
        }
    }
    
    private AvatarPickKey getPickKey() {
        return (AvatarPickKey)this.getArguments().getParcelable("pickID");
    }
    
    public static Bundle makeSelection(final UUID uuid, final AvatarPickKey avatarPickKey) {
        final Bundle bundle = new Bundle();
        bundle.putString("activeAgentUUID", uuid.toString());
        bundle.putParcelable("pickID", (Parcelable)avatarPickKey);
        return bundle;
    }
    
    private void onPickInfo(final PickInfoReply pickInfoReply) {
        if (pickInfoReply != null) {
            final LLVector3d posGlobal = pickInfoReply.Data_Field.PosGlobal;
            Debug.Printf("GlobalPos: got pick global pos %f, %f, %f", posGlobal.x, posGlobal.y, posGlobal.z);
            this.setTitle(SLMessage.stringFromVariableOEM(pickInfoReply.Data_Field.Name), null);
        }
        this.updateMenuItems();
        if (this.getView() != null && pickInfoReply != null) {
            this.pickDescription.setText((CharSequence)SLMessage.stringFromVariableUTF(pickInfoReply.Data_Field.Desc));
            this.userPickImageView.setAssetID(pickInfoReply.Data_Field.SnapshotID);
        }
    }
    
    private void renamePick() {
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        final AvatarPickKey pickKey = this.getPickKey();
        final PickInfoReply pickInfoReply = this.pickInfo.getData();
        if (userManager != null && pickKey != null && pickInfoReply != null) {
            final TextFieldDialogBuilder textFieldDialogBuilder = new TextFieldDialogBuilder(this.getContext());
            textFieldDialogBuilder.setTitle(this.getString(2131296885));
            textFieldDialogBuilder.setDefaultText(SLMessage.stringFromVariableOEM(pickInfoReply.Data_Field.Name));
            textFieldDialogBuilder.setOnTextEnteredListener((TextFieldDialogBuilder.OnTextEnteredListener)new _$Lambda$pe_zD6dKvPMIxwvN5gLJ2hSMvgo$6(userManager, pickKey, pickInfoReply));
            textFieldDialogBuilder.show();
        }
    }
    
    private void updateMenuItems() {
        final boolean b = false;
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        final AvatarPickKey pickKey = this.getPickKey();
        boolean equals = b;
        if (userManager != null) {
            equals = b;
            if (pickKey != null) {
                equals = userManager.getUserID().equals(pickKey.avatarID);
            }
        }
        if (this.menuItemRename != null) {
            this.menuItemRename.setVisible(equals);
        }
        if (this.menuItemDelete != null) {
            this.menuItemDelete.setVisible(equals);
        }
    }
    
    @OnClick({ 2131755698 })
    protected void onChangePic(final View view) {
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        final AvatarPickKey pickKey = this.getPickKey();
        final PickInfoReply pickInfoReply = this.pickInfo.getData();
        if (userManager != null && pickKey != null && pickInfoReply != null) {
            final Bundle bundle = new Bundle();
            bundle.putParcelable("oldPickData", (Parcelable)pickInfoReply);
            this.getContext().startActivity(InventoryActivity.makeSelectActionIntent(this.getContext(), userManager.getUserID(), InventoryActivity.SelectAction.applyPickImage, bundle, SLAssetType.AT_TEXTURE));
        }
    }
    
    @Override
    public void onCreate(@Nullable final Bundle bundle) {
        super.onCreate(bundle);
        this.setHasOptionsMenu(true);
    }
    
    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(2131886106, menu);
        this.menuItemRename = menu.findItem(2131755844);
        this.menuItemDelete = menu.findItem(2131755845);
        this.updateMenuItems();
    }
    
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        final View inflate = layoutInflater.inflate(2130968756, viewGroup, false);
        this.unbinder = ButterKnife.bind(this, inflate);
        this.userPickImageView.setAlignTop(true);
        this.userPickImageView.setVerticalFit(true);
        return inflate;
    }
    
    @OnClick({ 2131755700 })
    protected void onDescEdit(final View view) {
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        final AvatarPickKey pickKey = this.getPickKey();
        if (userManager != null && pickKey != null) {
            DetailsActivity.showEmbeddedDetails(this.getActivity(), PickDescriptionEditFragment.class, PickDescriptionEditFragment.makeSelection(ChatterID.getUserChatterID(userManager.getUserID(), pickKey.avatarID), pickKey));
        }
    }
    
    @Override
    public void onDestroyView() {
        if (this.unbinder != null) {
            this.unbinder.unbind();
            this.unbinder = null;
        }
        super.onDestroyView();
    }
    
    @Override
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 2131755844: {
                this.renamePick();
                break;
            }
            case 2131755845: {
                this.deletePick();
                break;
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }
    
    @OnClick({ 2131755696 })
    protected void onSetLocation(final View view) {
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        final AvatarPickKey pickKey = this.getPickKey();
        final PickInfoReply pickInfoReply = this.pickInfo.getData();
        if (userManager != null && pickKey != null && pickInfoReply != null) {
            final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(this.getContext());
            alertDialog$Builder.setMessage((CharSequence)this.getString(2131297024)).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$pe_zD6dKvPMIxwvN5gLJ2hSMvgo$7(this, userManager, pickKey, pickInfoReply)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$pe_zD6dKvPMIxwvN5gLJ2hSMvgo$1());
            alertDialog$Builder.create().show();
        }
    }
    
    @Override
    public void onStart() {
        final int n = 0;
        super.onStart();
        this.setTitle(this.getString(2131296712), null);
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        if (userManager != null) {
            final AvatarPickKey pickKey = this.getPickKey();
            final boolean equals = userManager.getUserID().equals(pickKey.avatarID);
            final Button userPickDescEditButton = this.userPickDescEditButton;
            int visibility;
            if (equals) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            userPickDescEditButton.setVisibility(visibility);
            final Button changePicButton = this.changePicButton;
            int visibility2;
            if (equals) {
                visibility2 = 0;
            }
            else {
                visibility2 = 8;
            }
            changePicButton.setVisibility(visibility2);
            final Button setLocationButton = this.setLocationButton;
            int visibility3;
            if (equals) {
                visibility3 = n;
            }
            else {
                visibility3 = 8;
            }
            setLocationButton.setVisibility(visibility3);
            this.pickInfo.subscribe(userManager.getAvatarPickInfos().getPool(), pickKey);
        }
        else {
            this.pickInfo.unsubscribe();
        }
    }
    
    @Override
    public void onStop() {
        this.pickInfo.unsubscribe();
        super.onStop();
    }
    
    @OnClick({ 2131755697 })
    protected void onTeleportToPickClick(final View view) {
        final UserManager userManager = ActivityUtils.getUserManager(this.getArguments());
        final PickInfoReply pickInfoReply = this.pickInfo.getData();
        if (pickInfoReply != null && userManager != null) {
            final LLVector3 llVector3 = new LLVector3((float)pickInfoReply.Data_Field.PosGlobal.x, (float)pickInfoReply.Data_Field.PosGlobal.y, (float)pickInfoReply.Data_Field.PosGlobal.z);
            final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder((Context)this.getActivity());
            alertDialog$Builder.setMessage((CharSequence)this.getActivity().getString(2131297099)).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$pe_zD6dKvPMIxwvN5gLJ2hSMvgo$5(this, userManager, llVector3)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$pe_zD6dKvPMIxwvN5gLJ2hSMvgo$2());
            alertDialog$Builder.create().show();
        }
    }
}
