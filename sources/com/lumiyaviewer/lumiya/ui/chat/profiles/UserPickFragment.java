// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.messages.PickInfoReply;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.SLUserProfiles;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.manager.AvatarPickKey;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.common.ImageAssetView;
import com.lumiyaviewer.lumiya.ui.common.TeleportProgressDialog;
import com.lumiyaviewer.lumiya.ui.common.TextFieldDialogBuilder;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            PickDescriptionEditFragment

public class UserPickFragment extends FragmentWithTitle
{

    private static final String PICK_ID_KEY = "pickID";
    Button changePicButton;
    private MenuItem menuItemDelete;
    private MenuItem menuItemRename;
    TextView pickDescription;
    private final SubscriptionData pickInfo = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.pe_zD6dKvPMIxwvN5gLJ2hSMvgo._cls3(this));
    Button setLocationButton;
    private Unbinder unbinder;
    Button userPickDescEditButton;
    ImageAssetView userPickImageView;

    public UserPickFragment()
    {
        menuItemRename = null;
        menuItemDelete = null;
        setArguments(new Bundle());
    }

    private void deletePick()
    {
        UserManager usermanager = ActivityUtils.getUserManager(getArguments());
        AvatarPickKey avatarpickkey = getPickKey();
        if (usermanager != null && avatarpickkey != null)
        {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
            builder.setMessage(getString(0x7f0900e0)).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda.pe_zD6dKvPMIxwvN5gLJ2hSMvgo._cls4(this, usermanager, avatarpickkey)).setNegativeButton("No", new _2D_.Lambda.pe_zD6dKvPMIxwvN5gLJ2hSMvgo());
            builder.create().show();
        }
    }

    private AvatarPickKey getPickKey()
    {
        return (AvatarPickKey)getArguments().getParcelable("pickID");
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_UserPickFragment_11235(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_UserPickFragment_12813(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_UserPickFragment_13525(UserManager usermanager, AvatarPickKey avatarpickkey, PickInfoReply pickinforeply, String s)
    {
        usermanager = usermanager.getActiveAgentCircuit();
        if (usermanager != null)
        {
            usermanager.getModules().userProfiles.UpdatePickInfo(avatarpickkey.pickID, pickinforeply.Data_Field.CreatorID, pickinforeply.Data_Field.ParcelID, s, SLMessage.stringFromVariableUTF(pickinforeply.Data_Field.Desc), pickinforeply.Data_Field.SnapshotID, pickinforeply.Data_Field.PosGlobal, pickinforeply.Data_Field.SortOrder, pickinforeply.Data_Field.Enabled);
        }
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_UserPickFragment_7788(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    public static Bundle makeSelection(UUID uuid, AvatarPickKey avatarpickkey)
    {
        Bundle bundle = new Bundle();
        bundle.putString("activeAgentUUID", uuid.toString());
        bundle.putParcelable("pickID", avatarpickkey);
        return bundle;
    }

    private void onPickInfo(PickInfoReply pickinforeply)
    {
        if (pickinforeply != null)
        {
            LLVector3d llvector3d = pickinforeply.Data_Field.PosGlobal;
            Debug.Printf("GlobalPos: got pick global pos %f, %f, %f", new Object[] {
                Double.valueOf(llvector3d.x), Double.valueOf(llvector3d.y), Double.valueOf(llvector3d.z)
            });
            setTitle(SLMessage.stringFromVariableOEM(pickinforeply.Data_Field.Name), null);
        }
        updateMenuItems();
        if (getView() != null && pickinforeply != null)
        {
            pickDescription.setText(SLMessage.stringFromVariableUTF(pickinforeply.Data_Field.Desc));
            userPickImageView.setAssetID(pickinforeply.Data_Field.SnapshotID);
        }
    }

    private void renamePick()
    {
        UserManager usermanager = ActivityUtils.getUserManager(getArguments());
        AvatarPickKey avatarpickkey = getPickKey();
        PickInfoReply pickinforeply = (PickInfoReply)pickInfo.getData();
        if (usermanager != null && avatarpickkey != null && pickinforeply != null)
        {
            TextFieldDialogBuilder textfielddialogbuilder = new TextFieldDialogBuilder(getContext());
            textfielddialogbuilder.setTitle(getString(0x7f090275));
            textfielddialogbuilder.setDefaultText(SLMessage.stringFromVariableOEM(pickinforeply.Data_Field.Name));
            textfielddialogbuilder.setOnTextEnteredListener(new _2D_.Lambda.pe_zD6dKvPMIxwvN5gLJ2hSMvgo._cls6(usermanager, avatarpickkey, pickinforeply));
            textfielddialogbuilder.show();
        }
    }

    private void updateMenuItems()
    {
        boolean flag1 = false;
        UserManager usermanager = ActivityUtils.getUserManager(getArguments());
        AvatarPickKey avatarpickkey = getPickKey();
        boolean flag = flag1;
        if (usermanager != null)
        {
            flag = flag1;
            if (avatarpickkey != null)
            {
                flag = usermanager.getUserID().equals(avatarpickkey.avatarID);
            }
        }
        if (menuItemRename != null)
        {
            menuItemRename.setVisible(flag);
        }
        if (menuItemDelete != null)
        {
            menuItemDelete.setVisible(flag);
        }
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_profiles_UserPickFragment_2D_mthref_2D_0(PickInfoReply pickinforeply)
    {
        onPickInfo(pickinforeply);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_UserPickFragment_12153(UserManager usermanager, AvatarPickKey avatarpickkey, DialogInterface dialoginterface, int i)
    {
        usermanager = usermanager.getActiveAgentCircuit();
        if (usermanager != null)
        {
            usermanager.getModules().userProfiles.DeletePick(avatarpickkey.pickID);
            usermanager = getActivity();
            if (usermanager instanceof DetailsActivity)
            {
                ((DetailsActivity)usermanager).closeDetailsFragment(this);
            }
        }
        dialoginterface.dismiss();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_UserPickFragment_7285(UserManager usermanager, LLVector3 llvector3, DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        dialoginterface = usermanager.getActiveAgentCircuit();
        if (dialoginterface != null)
        {
            (new TeleportProgressDialog(getContext(), usermanager, 0x7f090350)).show();
            dialoginterface.TeleportToGlobalPosition(llvector3);
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_UserPickFragment_9741(UserManager usermanager, AvatarPickKey avatarpickkey, PickInfoReply pickinforeply, DialogInterface dialoginterface, int i)
    {
        usermanager = usermanager.getActiveAgentCircuit();
        if (usermanager != null)
        {
            LLVector3d llvector3d = usermanager.getAgentGlobalPosition();
            if (llvector3d != null)
            {
                Debug.Printf("GlobalPos: setting pick to global pos %f %f %f", new Object[] {
                    Double.valueOf(llvector3d.x), Double.valueOf(llvector3d.y), Double.valueOf(llvector3d.z)
                });
                usermanager.getModules().userProfiles.UpdatePickInfo(avatarpickkey.pickID, pickinforeply.Data_Field.CreatorID, pickinforeply.Data_Field.ParcelID, SLMessage.stringFromVariableOEM(pickinforeply.Data_Field.Name), SLMessage.stringFromVariableUTF(pickinforeply.Data_Field.Desc), pickinforeply.Data_Field.SnapshotID, llvector3d, pickinforeply.Data_Field.SortOrder, pickinforeply.Data_Field.Enabled);
            }
        }
        dialoginterface.dismiss();
        Toast.makeText(getContext(), 0x7f090274, 0).show();
    }

    protected void onChangePic(View view)
    {
        view = ActivityUtils.getUserManager(getArguments());
        AvatarPickKey avatarpickkey = getPickKey();
        PickInfoReply pickinforeply = (PickInfoReply)pickInfo.getData();
        if (view != null && avatarpickkey != null && pickinforeply != null)
        {
            Bundle bundle = new Bundle();
            bundle.putParcelable("oldPickData", pickinforeply);
            view = InventoryActivity.makeSelectActionIntent(getContext(), view.getUserID(), com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity.SelectAction.applyPickImage, bundle, SLAssetType.AT_TEXTURE);
            getContext().startActivity(view);
        }
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater)
    {
        super.onCreateOptionsMenu(menu, menuinflater);
        menuinflater.inflate(0x7f12001a, menu);
        menuItemRename = menu.findItem(0x7f100344);
        menuItemDelete = menu.findItem(0x7f100345);
        updateMenuItems();
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        super.onCreateView(layoutinflater, viewgroup, bundle);
        layoutinflater = layoutinflater.inflate(0x7f0400b4, viewgroup, false);
        unbinder = ButterKnife.bind(this, layoutinflater);
        userPickImageView.setAlignTop(true);
        userPickImageView.setVerticalFit(true);
        return layoutinflater;
    }

    protected void onDescEdit(View view)
    {
        view = ActivityUtils.getUserManager(getArguments());
        AvatarPickKey avatarpickkey = getPickKey();
        if (view != null && avatarpickkey != null)
        {
            DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/PickDescriptionEditFragment, PickDescriptionEditFragment.makeSelection(ChatterID.getUserChatterID(view.getUserID(), avatarpickkey.avatarID), avatarpickkey));
        }
    }

    public void onDestroyView()
    {
        if (unbinder != null)
        {
            unbinder.unbind();
            unbinder = null;
        }
        super.onDestroyView();
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        menuitem.getItemId();
        JVM INSTR tableswitch 2131755844 2131755845: default 28
    //                   2131755844 34
    //                   2131755845 41;
           goto _L1 _L2 _L3
_L1:
        return super.onOptionsItemSelected(menuitem);
_L2:
        renamePick();
        continue; /* Loop/switch isn't completed */
_L3:
        deletePick();
        if (true) goto _L1; else goto _L4
_L4:
    }

    protected void onSetLocation(View view)
    {
        view = ActivityUtils.getUserManager(getArguments());
        AvatarPickKey avatarpickkey = getPickKey();
        PickInfoReply pickinforeply = (PickInfoReply)pickInfo.getData();
        if (view != null && avatarpickkey != null && pickinforeply != null)
        {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
            builder.setMessage(getString(0x7f090300)).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda.pe_zD6dKvPMIxwvN5gLJ2hSMvgo._cls7(this, view, avatarpickkey, pickinforeply)).setNegativeButton("No", new _2D_.Lambda.pe_zD6dKvPMIxwvN5gLJ2hSMvgo._cls1());
            builder.create().show();
        }
    }

    public void onStart()
    {
        boolean flag = false;
        super.onStart();
        setTitle(getString(0x7f0901c8), null);
        UserManager usermanager = ActivityUtils.getUserManager(getArguments());
        if (usermanager != null)
        {
            AvatarPickKey avatarpickkey = getPickKey();
            boolean flag1 = usermanager.getUserID().equals(avatarpickkey.avatarID);
            Button button = userPickDescEditButton;
            int i;
            if (flag1)
            {
                i = 0;
            } else
            {
                i = 8;
            }
            button.setVisibility(i);
            button = changePicButton;
            if (flag1)
            {
                i = 0;
            } else
            {
                i = 8;
            }
            button.setVisibility(i);
            button = setLocationButton;
            if (flag1)
            {
                i = ((flag) ? 1 : 0);
            } else
            {
                i = 8;
            }
            button.setVisibility(i);
            pickInfo.subscribe(usermanager.getAvatarPickInfos().getPool(), avatarpickkey);
            return;
        } else
        {
            pickInfo.unsubscribe();
            return;
        }
    }

    public void onStop()
    {
        pickInfo.unsubscribe();
        super.onStop();
    }

    protected void onTeleportToPickClick(View view)
    {
        view = ActivityUtils.getUserManager(getArguments());
        Object obj = (PickInfoReply)pickInfo.getData();
        if (obj != null && view != null)
        {
            obj = new LLVector3((float)((PickInfoReply) (obj)).Data_Field.PosGlobal.x, (float)((PickInfoReply) (obj)).Data_Field.PosGlobal.y, (float)((PickInfoReply) (obj)).Data_Field.PosGlobal.z);
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
            builder.setMessage(getActivity().getString(0x7f09034b)).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda.pe_zD6dKvPMIxwvN5gLJ2hSMvgo._cls5(this, view, obj)).setNegativeButton("No", new _2D_.Lambda.pe_zD6dKvPMIxwvN5gLJ2hSMvgo._cls2());
            builder.create().show();
        }
    }
}
