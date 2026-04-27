// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.inventory;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
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
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryType;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.inventory:
//            InventoryFragmentHelper

public class AssetInfoFragment extends FragmentWithTitle
    implements ReloadableFragment, android.view.View.OnClickListener, com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
{

    private static final String ITEM_UUID_KEY = "itemUUID";
    private final SubscriptionData agentCircuit = new SubscriptionData(UIThreadExecutor.getInstance());
    private ChatterNameRetriever creatorNameRetriever;
    private final SubscriptionData entrySubscription = new SubscriptionData(UIThreadExecutor.getInstance());
    private final InventoryFragmentHelper inventoryFragmentHelper = new InventoryFragmentHelper(this);
    private ChatterNameRetriever lastOwnerNameRetriever;
    private final LoadableMonitor loadableMonitor;
    private MenuItem menuItemCopy;
    private MenuItem menuItemCut;
    private MenuItem menuItemDelete;
    private MenuItem menuItemRename;
    private MenuItem menuItemShare;
    private final com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated onNameUpdated = new _2D_.Lambda.OIe5MtmKyVPF26gruCQoZkxXroQ._cls1(this);
    private ChatterNameRetriever ownerNameRetriever;
    private final SubscriptionData runningAnimations = new SubscriptionData(UIThreadExecutor.getInstance());
    private final SubscriptionData wornAttachments = new SubscriptionData(UIThreadExecutor.getInstance());
    private final SubscriptionData wornWearables = new SubscriptionData(UIThreadExecutor.getInstance());

    public AssetInfoFragment()
    {
        loadableMonitor = (new LoadableMonitor(new Loadable[] {
            entrySubscription
        })).withOptionalLoadables(new Loadable[] {
            wornAttachments, wornWearables, agentCircuit, runningAnimations
        }).withDataChangedListener(this);
        ownerNameRetriever = null;
        creatorNameRetriever = null;
        lastOwnerNameRetriever = null;
    }

    private void applyEditedPermissions(Dialog dialog)
    {
        SLInventoryEntry slinventoryentry = (SLInventoryEntry)entrySubscription.getData();
        if (slinventoryentry != null)
        {
            SLInventory slinventory = inventoryFragmentHelper.getInventory();
            if (slinventory != null)
            {
                slinventoryentry.nextOwnerMask = slinventoryentry.ownerMask & getCheckboxes(dialog, 0x7f1000e9, 0x7f1000ea, 0x7f1000eb);
                slinventoryentry.groupMask = slinventoryentry.ownerMask & getCheckboxes(dialog, 0x7f1000ec, 0x7f1000ed, 0x7f1000ee);
                slinventoryentry.everyoneMask = slinventoryentry.ownerMask & getCheckboxes(dialog, 0x7f1000ef, 0x7f1000f0, 0x7f1000f1);
                slinventory.UpdateStoreInventoryItem(slinventoryentry);
                showEntryInfo(slinventoryentry);
            }
        }
    }

    private void attachObject(SLInventoryEntry slinventoryentry)
    {
        try
        {
            ((SLAgentCircuit)agentCircuit.get()).getModules().avatarAppearance.AttachInventoryItem(slinventoryentry, 0, false);
            return;
        }
        // Misplaced declaration of an exception variable
        catch (SLInventoryEntry slinventoryentry)
        {
            Debug.Warning(slinventoryentry);
        }
    }

    private void detachObject(SLInventoryEntry slinventoryentry)
    {
        try
        {
            ((SLAgentCircuit)agentCircuit.get()).getModules().avatarAppearance.DetachInventoryItem(slinventoryentry);
            return;
        }
        // Misplaced declaration of an exception variable
        catch (SLInventoryEntry slinventoryentry)
        {
            Debug.Warning(slinventoryentry);
        }
    }

    private int getCheckboxes(Dialog dialog, int i, int j, int k)
    {
        int l = 0;
        if (((CheckBox)dialog.findViewById(i)).isChecked())
        {
            l = 32768;
        }
        i = l;
        if (((CheckBox)dialog.findViewById(j)).isChecked())
        {
            i = l | 0x4000;
        }
        j = i;
        if (((CheckBox)dialog.findViewById(k)).isChecked())
        {
            j = i | 0x2000;
        }
        return j;
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_AssetInfoFragment_26901(Dialog dialog, View view)
    {
        dialog.dismiss();
    }

    public static Bundle makeSelection(UUID uuid, UUID uuid1)
    {
        Bundle bundle = new Bundle();
        if (uuid != null)
        {
            bundle.putString("activeAgentUUID", uuid.toString());
        }
        if (uuid1 != null)
        {
            bundle.putString("itemUUID", uuid1.toString());
        }
        return bundle;
    }

    private void playAnimation(SLInventoryEntry slinventoryentry, boolean flag)
    {
        SLAgentCircuit slagentcircuit = (SLAgentCircuit)agentCircuit.getData();
        if (slagentcircuit != null)
        {
            slagentcircuit.getModules().avatarControl.playAnimation(slinventoryentry.assetUUID, flag);
        }
    }

    private void setCheckboxes(Dialog dialog, int i, int j, int k, int l, int i1, boolean flag)
    {
        boolean flag2 = false;
        Object obj = (CheckBox)dialog.findViewById(k);
        boolean flag1;
        if ((i & 0x8000) != 0)
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        ((CheckBox) (obj)).setChecked(flag1);
        obj = (CheckBox)dialog.findViewById(l);
        if ((i & 0x4000) != 0)
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        ((CheckBox) (obj)).setChecked(flag1);
        obj = (CheckBox)dialog.findViewById(i1);
        if ((i & 0x2000) != 0)
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        ((CheckBox) (obj)).setChecked(flag1);
        obj = dialog.findViewById(k);
        if (flag && (j & 0x8000) != 0)
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        ((View) (obj)).setEnabled(flag1);
        obj = dialog.findViewById(l);
        if (flag && (j & 0x4000) != 0)
        {
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        ((View) (obj)).setEnabled(flag1);
        dialog = dialog.findViewById(i1);
        flag1 = flag2;
        if (flag)
        {
            flag1 = flag2;
            if ((j & 0x2000) != 0)
            {
                flag1 = true;
            }
        }
        dialog.setEnabled(flag1);
    }

    private void showEditPermissionsDialog()
    {
        SLInventoryEntry slinventoryentry = (SLInventoryEntry)entrySubscription.getData();
        if (slinventoryentry != null)
        {
            Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(0x7f04001e);
            dialog.setTitle(0x7f09010a);
            setCheckboxes(dialog, slinventoryentry.ownerMask, slinventoryentry.ownerMask, 0x7f1000e6, 0x7f1000e7, 0x7f1000e8, false);
            setCheckboxes(dialog, slinventoryentry.nextOwnerMask, slinventoryentry.ownerMask, 0x7f1000e9, 0x7f1000ea, 0x7f1000eb, true);
            setCheckboxes(dialog, slinventoryentry.groupMask, slinventoryentry.ownerMask, 0x7f1000ec, 0x7f1000ed, 0x7f1000ee, true);
            setCheckboxes(dialog, slinventoryentry.everyoneMask, slinventoryentry.ownerMask, 0x7f1000ef, 0x7f1000f0, 0x7f1000f1, true);
            ((CheckBox)dialog.findViewById(0x7f1000f0)).setChecked(false);
            dialog.findViewById(0x7f1000f0).setEnabled(false);
            dialog.findViewById(0x7f1000b6).setOnClickListener(new _2D_.Lambda.OIe5MtmKyVPF26gruCQoZkxXroQ._cls3(this, dialog));
            dialog.findViewById(0x7f1000b7).setOnClickListener(new _2D_.Lambda.OIe5MtmKyVPF26gruCQoZkxXroQ(dialog));
            dialog.show();
        }
    }

    private void showEntry(UUID uuid)
    {
        loadableMonitor.unsubscribeAll();
        UserManager usermanager = ActivityUtils.getUserManager(getArguments());
        if (uuid != null && usermanager != null)
        {
            agentCircuit.subscribe(UserManager.agentCircuits(), usermanager.getUserID());
            entrySubscription.subscribe(usermanager.getInventoryManager().getFolderEntryPool(), uuid);
            wornAttachments.subscribe(usermanager.getWornAttachmentsPool(), SubscriptionSingleKey.Value);
            wornWearables.subscribe(usermanager.getWornWearablesPool(), SubscriptionSingleKey.Value);
            runningAnimations.subscribe(usermanager.getObjectsManager().runningAnimations(), SubscriptionSingleKey.Value);
        } else
        {
            if (creatorNameRetriever != null)
            {
                creatorNameRetriever.dispose();
            }
            if (ownerNameRetriever != null)
            {
                ownerNameRetriever.dispose();
            }
            if (lastOwnerNameRetriever != null)
            {
                lastOwnerNameRetriever.dispose();
                return;
            }
        }
    }

    private void showEntryInfo(SLInventoryEntry slinventoryentry)
    {
        boolean flag = false;
        byte byte0 = 8;
        View view = getView();
        if (view != null)
        {
            ((TextView)view.findViewById(0x7f1000c0)).setText(slinventoryentry.name);
            TextView textview = (TextView)view.findViewById(0x7f1000cb);
            Object obj;
            int i;
            int k;
            if (!Strings.isNullOrEmpty(slinventoryentry.description))
            {
                obj = slinventoryentry.description;
            } else
            {
                obj = getResources().getString(0x7f09005b);
            }
            textview.setText(((CharSequence) (obj)));
            ((TextView)view.findViewById(0x7f1000bf)).setText(slinventoryentry.getTypeDescriptionResId());
            i = slinventoryentry.getDrawableResource();
            if (i >= 0)
            {
                ((ImageView)view.findViewById(0x7f1000c1)).setImageResource(i);
            } else
            {
                ((ImageView)view.findViewById(0x7f1000c1)).setImageBitmap(null);
            }
            i = slinventoryentry.getActionDescriptionResId();
            if (i >= 0)
            {
                ((Button)view.findViewById(0x7f1000c2)).setText(i);
                view.findViewById(0x7f1000c2).setVisibility(0);
                view.findViewById(0x7f1000c2).setEnabled(inventoryFragmentHelper.isActionAllowed(slinventoryentry, i));
            } else
            {
                view.findViewById(0x7f1000c2).setVisibility(8);
            }
            obj = view.findViewById(0x7f1000d9);
            if ((slinventoryentry.ownerMask & 0x4000) != 0)
            {
                i = 0;
            } else
            {
                i = 8;
            }
            ((View) (obj)).setVisibility(i);
            showPermissions(slinventoryentry.ownerMask, 0x7f1000cd, 0x7f1000ce, 0x7f1000cf);
            showPermissions(slinventoryentry.groupMask, 0x7f1000d3, 0x7f1000d4, 0x7f1000d5);
            showPermissions(slinventoryentry.everyoneMask, 0x7f1000d6, 0x7f1000d7, 0x7f1000d8);
            showPermissions(slinventoryentry.nextOwnerMask, 0x7f1000d0, 0x7f1000d1, 0x7f1000d2);
            obj = (SLAgentCircuit)agentCircuit.getData();
            if (obj != null)
            {
                i = 1;
            } else
            {
                i = 0;
            }
            if (i != 0 && (slinventoryentry.assetType == SLAssetType.AT_OBJECT.getTypeCode() || slinventoryentry.assetType == SLAssetType.AT_LINK.getTypeCode() && slinventoryentry.invType == SLInventoryType.IT_OBJECT.getTypeCode()))
            {
                Object obj1;
                View view1;
                boolean flag1;
                if (slinventoryentry.whatIsItemWornOn((ImmutableMap)wornAttachments.getData(), (Table)wornWearables.getData(), false) != null)
                {
                    k = 1;
                } else
                {
                    k = 0;
                }
                if (k != 0)
                {
                    flag1 = ((SLAgentCircuit) (obj)).getModules().avatarAppearance.canDetachItem(slinventoryentry);
                } else
                {
                    flag1 = false;
                }
                obj1 = view.findViewById(0x7f1000c3);
                if (k != 0)
                {
                    k = 8;
                } else
                {
                    k = 0;
                }
                ((View) (obj1)).setVisibility(k);
                obj1 = view.findViewById(0x7f1000c4);
                if (flag1)
                {
                    k = 0;
                } else
                {
                    k = 8;
                }
                ((View) (obj1)).setVisibility(k);
            } else
            {
                view.findViewById(0x7f1000c3).setVisibility(8);
                view.findViewById(0x7f1000c4).setVisibility(8);
            }
            if (i != 0 && slinventoryentry.isAnimation())
            {
                obj1 = (ImmutableSet)runningAnimations.getData();
                view1 = view.findViewById(0x7f1000c7);
                if (obj1 != null && ((ImmutableSet) (obj1)).contains(slinventoryentry.assetUUID) ^ true)
                {
                    k = 0;
                } else
                {
                    k = 8;
                }
                view1.setVisibility(k);
                view1 = view.findViewById(0x7f1000c8);
                if (obj1 != null && ((ImmutableSet) (obj1)).contains(slinventoryentry.assetUUID))
                {
                    k = 0;
                } else
                {
                    k = 8;
                }
                view1.setVisibility(k);
            } else
            {
                view.findViewById(0x7f1000c7).setVisibility(8);
                view.findViewById(0x7f1000c8).setVisibility(8);
            }
            if (i != 0 && slinventoryentry.isWearable())
            {
                Object obj2 = slinventoryentry.whatIsItemWornOn((ImmutableMap)wornAttachments.getData(), (Table)wornWearables.getData(), false);
                if (obj2 instanceof SLWearableType)
                {
                    view.findViewById(0x7f1000c5).setVisibility(8);
                    if (((SLWearableType)obj2).isBodyPart())
                    {
                        view.findViewById(0x7f1000c6).setVisibility(8);
                    } else
                    {
                        boolean flag2 = ((SLAgentCircuit) (obj)).getModules().avatarAppearance.canTakeItemOff((SLWearableType)obj2);
                        slinventoryentry = view.findViewById(0x7f1000c6);
                        if (flag2)
                        {
                            i = 0;
                        } else
                        {
                            i = 8;
                        }
                        slinventoryentry.setVisibility(i);
                    }
                    slinventoryentry = view.findViewById(0x7f1000c9);
                    if (view.findViewById(0x7f1000c6).getVisibility() == 0)
                    {
                        i = byte0;
                    } else
                    {
                        i = 0;
                    }
                    slinventoryentry.setVisibility(i);
                } else
                {
                    view.findViewById(0x7f1000c6).setVisibility(8);
                    boolean flag3 = ((SLAgentCircuit) (obj)).getModules().avatarAppearance.canWearItem(slinventoryentry);
                    slinventoryentry = view.findViewById(0x7f1000c5);
                    int j;
                    if (flag3)
                    {
                        j = ((flag) ? 1 : 0);
                    } else
                    {
                        j = 8;
                    }
                    slinventoryentry.setVisibility(j);
                    view.findViewById(0x7f1000c9).setVisibility(8);
                }
            } else
            {
                view.findViewById(0x7f1000c5).setVisibility(8);
                view.findViewById(0x7f1000c6).setVisibility(8);
                view.findViewById(0x7f1000c9).setVisibility(8);
            }
        }
        updateMenuItems();
    }

    private void showPermissions(int i, int j, int k, int l)
    {
        Object obj;
label0:
        {
            obj = getView();
            if (obj != null)
            {
                TextView textview = (TextView)((View) (obj)).findViewById(j);
                TextView textview1 = (TextView)((View) (obj)).findViewById(k);
                obj = (TextView)((View) (obj)).findViewById(l);
                if ((0x8000 & i) != 0)
                {
                    textview.setPaintFlags(textview.getPaintFlags() & 0xffffffef);
                } else
                {
                    textview.setPaintFlags(textview.getPaintFlags() | 0x10);
                }
                if ((i & 0x4000) != 0)
                {
                    textview1.setPaintFlags(textview1.getPaintFlags() & 0xffffffef);
                } else
                {
                    textview1.setPaintFlags(textview1.getPaintFlags() | 0x10);
                }
                if ((i & 0x2000) == 0)
                {
                    break label0;
                }
                ((TextView) (obj)).setPaintFlags(((TextView) (obj)).getPaintFlags() & 0xffffffef);
            }
            return;
        }
        ((TextView) (obj)).setPaintFlags(((TextView) (obj)).getPaintFlags() | 0x10);
    }

    private void showProfile(UUID uuid)
    {
        UserManager usermanager = ActivityUtils.getUserManager(getArguments());
        if (uuid != null && Objects.equal(uuid, UUIDPool.ZeroUUID) ^ true && usermanager != null)
        {
            uuid = ChatterID.getUserChatterID(usermanager.getUserID(), uuid);
            DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/UserProfileFragment, UserProfileFragment.makeSelection(uuid));
        }
    }

    private void showUserInfo(UUID uuid, ChatterNameRetriever chatternameretriever, int i, int j, int k)
    {
        View view;
label0:
        {
            view = getView();
            if (view != null)
            {
                if (uuid != null && !Objects.equal(uuid, UUIDPool.ZeroUUID) && chatternameretriever != null)
                {
                    break label0;
                }
                view.findViewById(i).setVisibility(8);
            }
            return;
        }
        view.findViewById(i).setVisibility(0);
        String s = chatternameretriever.getResolvedName();
        TextView textview = (TextView)view.findViewById(j);
        if (s != null)
        {
            uuid = s;
        } else
        {
            uuid = getString(0x7f0901c8);
        }
        textview.setText(uuid);
        ((ChatterPicView)view.findViewById(k)).setChatterID(chatternameretriever.chatterID, s);
    }

    private void takeOffObject(SLInventoryEntry slinventoryentry)
    {
        try
        {
            ((SLAgentCircuit)agentCircuit.get()).getModules().avatarAppearance.TakeItemOff(slinventoryentry);
            return;
        }
        // Misplaced declaration of an exception variable
        catch (SLInventoryEntry slinventoryentry)
        {
            Debug.Warning(slinventoryentry);
        }
    }

    private void updateMenuItems()
    {
        if (menuItemCopy == null || menuItemCut == null || menuItemShare == null || menuItemRename == null || menuItemDelete == null)
        {
            break MISSING_BLOCK_LABEL_154;
        }
        SLInventoryEntry slinventoryentry;
        MenuItem menuitem;
        agentCircuit.assertHasData();
        slinventoryentry = (SLInventoryEntry)entrySubscription.get();
        menuItemDelete.setVisible(true);
        menuitem = menuItemRename;
        int i;
        boolean flag;
        if ((slinventoryentry.baseMask & slinventoryentry.ownerMask & 0x4000) != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        menuitem.setVisible(flag);
        menuitem = menuItemShare;
        i = slinventoryentry.baseMask;
        if ((slinventoryentry.ownerMask & i & 0x2000) != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        menuitem.setVisible(flag);
        menuItemCut.setVisible(true);
        menuItemCopy.setVisible(true);
        return;
        com.lumiyaviewer.lumiya.react.SubscriptionData.DataNotReadyException datanotreadyexception;
        datanotreadyexception;
        menuItemDelete.setVisible(false);
        menuItemRename.setVisible(false);
        menuItemShare.setVisible(false);
        menuItemCut.setVisible(false);
        menuItemCopy.setVisible(false);
        return;
    }

    private void wearObject(SLInventoryEntry slinventoryentry)
    {
        try
        {
            ((SLAgentCircuit)agentCircuit.get()).getModules().avatarAppearance.WearItem(slinventoryentry, false);
            return;
        }
        // Misplaced declaration of an exception variable
        catch (SLInventoryEntry slinventoryentry)
        {
            Debug.Warning(slinventoryentry);
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_AssetInfoFragment_10987(ChatterNameRetriever chatternameretriever)
    {
        chatternameretriever = (SLInventoryEntry)entrySubscription.getData();
        if (chatternameretriever != null)
        {
            showUserInfo(((SLInventoryEntry) (chatternameretriever)).ownerUUID, ownerNameRetriever, 0x7f1000da, 0x7f1000db, 0x7f1000dc);
            showUserInfo(((SLInventoryEntry) (chatternameretriever)).creatorUUID, creatorNameRetriever, 0x7f1000de, 0x7f1000df, 0x7f1000e0);
            showUserInfo(((SLInventoryEntry) (chatternameretriever)).lastOwnerUUID, lastOwnerNameRetriever, 0x7f1000e2, 0x7f1000e3, 0x7f1000e4);
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_AssetInfoFragment_20027()
    {
        android.support.v4.app.FragmentActivity fragmentactivity = getActivity();
        if (fragmentactivity instanceof DetailsActivity)
        {
            ((DetailsActivity)fragmentactivity).closeDetailsFragment(this);
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_inventory_AssetInfoFragment_26713(Dialog dialog, View view)
    {
        applyEditedPermissions(dialog);
        dialog.dismiss();
    }

    public void onClick(View view)
    {
        SLInventoryEntry slinventoryentry = (SLInventoryEntry)entrySubscription.getData();
        if (slinventoryentry == null) goto _L2; else goto _L1
_L1:
        view.getId();
        JVM INSTR lookupswitch 11: default 116
    //                   2131755202: 144
    //                   2131755203: 175
    //                   2131755204: 195
    //                   2131755205: 201
    //                   2131755206: 207
    //                   2131755207: 181
    //                   2131755208: 188
    //                   2131755225: 213
    //                   2131755229: 117
    //                   2131755233: 126
    //                   2131755237: 135;
           goto _L3 _L4 _L5 _L6 _L7 _L8 _L9 _L10 _L11 _L12 _L13 _L14
_L9:
        break MISSING_BLOCK_LABEL_181;
_L10:
        break MISSING_BLOCK_LABEL_188;
_L6:
        break MISSING_BLOCK_LABEL_195;
_L7:
        break MISSING_BLOCK_LABEL_201;
_L8:
        break MISSING_BLOCK_LABEL_207;
_L3:
        break; /* Loop/switch isn't completed */
_L11:
        break MISSING_BLOCK_LABEL_213;
_L2:
        return;
_L12:
        showProfile(slinventoryentry.ownerUUID);
        return;
_L13:
        showProfile(slinventoryentry.creatorUUID);
        return;
_L14:
        int i;
        showProfile(slinventoryentry.lastOwnerUUID);
        return;
_L4:
        if ((i = slinventoryentry.getActionDescriptionResId()) >= 0 && inventoryFragmentHelper.isActionAllowed(slinventoryentry, i))
        {
            inventoryFragmentHelper.PerformInventoryAction(slinventoryentry, i);
            return;
        }
        if (true) goto _L2; else goto _L5
_L5:
        attachObject(slinventoryentry);
        return;
        playAnimation(slinventoryentry, true);
        return;
        playAnimation(slinventoryentry, false);
        return;
        detachObject(slinventoryentry);
        return;
        wearObject(slinventoryentry);
        return;
        takeOffObject(slinventoryentry);
        return;
        showEditPermissionsDialog();
        return;
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater)
    {
        menuinflater.inflate(0x7f12000c, menu);
        menuItemDelete = menu.findItem(0x7f100325);
        menuItemRename = menu.findItem(0x7f100326);
        menuItemShare = menu.findItem(0x7f100327);
        menuItemCut = menu.findItem(0x7f100328);
        menuItemCopy = menu.findItem(0x7f100329);
        updateMenuItems();
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        super.onCreateView(layoutinflater, viewgroup, bundle);
        layoutinflater = layoutinflater.inflate(0x7f04001d, viewgroup, false);
        ((LoadingLayout)layoutinflater.findViewById(0x7f1000bd)).setSwipeRefreshLayout((SwipeRefreshLayout)layoutinflater.findViewById(0x7f1000bb));
        loadableMonitor.setLoadingLayout((LoadingLayout)layoutinflater.findViewById(0x7f1000bd), getString(0x7f0901e8), getString(0x7f090164));
        loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout)layoutinflater.findViewById(0x7f1000bb));
        layoutinflater.findViewById(0x7f1000e1).setOnClickListener(this);
        layoutinflater.findViewById(0x7f1000dd).setOnClickListener(this);
        layoutinflater.findViewById(0x7f1000e5).setOnClickListener(this);
        layoutinflater.findViewById(0x7f1000c2).setOnClickListener(this);
        layoutinflater.findViewById(0x7f1000d9).setOnClickListener(this);
        layoutinflater.findViewById(0x7f1000d9).setVisibility(8);
        layoutinflater.findViewById(0x7f1000c3).setOnClickListener(this);
        layoutinflater.findViewById(0x7f1000c4).setOnClickListener(this);
        layoutinflater.findViewById(0x7f1000c5).setOnClickListener(this);
        layoutinflater.findViewById(0x7f1000c6).setOnClickListener(this);
        layoutinflater.findViewById(0x7f1000c7).setOnClickListener(this);
        layoutinflater.findViewById(0x7f1000c8).setOnClickListener(this);
        return layoutinflater;
    }

    public void onLoadableDataChanged()
    {
        UserManager usermanager = ActivityUtils.getUserManager(getArguments());
        try
        {
            showEntryInfo((SLInventoryEntry)entrySubscription.get());
        }
        catch (com.lumiyaviewer.lumiya.react.SubscriptionData.DataNotReadyException datanotreadyexception)
        {
            Debug.Warning(datanotreadyexception);
            return;
        }
        if (usermanager == null)
        {
            break MISSING_BLOCK_LABEL_140;
        }
        creatorNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(usermanager.getUserID(), ((SLInventoryEntry)entrySubscription.get()).creatorUUID), onNameUpdated, UIThreadExecutor.getInstance());
        ownerNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(usermanager.getUserID(), ((SLInventoryEntry)entrySubscription.get()).ownerUUID), onNameUpdated, UIThreadExecutor.getInstance());
        lastOwnerNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(usermanager.getUserID(), ((SLInventoryEntry)entrySubscription.get()).lastOwnerUUID), onNameUpdated, UIThreadExecutor.getInstance());
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        UserManager usermanager = ActivityUtils.getUserManager(getArguments());
        if (usermanager == null) goto _L2; else goto _L1
_L1:
        SLInventoryEntry slinventoryentry;
        int i;
        slinventoryentry = (SLInventoryEntry)entrySubscription.get();
        i = menuitem.getItemId();
        i;
        JVM INSTR tableswitch 2131755813 2131755817: default 68
    //                   2131755813 74
    //                   2131755814 92
    //                   2131755815 102
    //                   2131755816 112
    //                   2131755817 144;
           goto _L2 _L3 _L4 _L5 _L6 _L7
_L2:
        return super.onOptionsItemSelected(menuitem);
_L3:
        inventoryFragmentHelper.DeleteInventoryEntry(slinventoryentry, new _2D_.Lambda.OIe5MtmKyVPF26gruCQoZkxXroQ._cls2(this));
        return true;
_L4:
        inventoryFragmentHelper.RenameInventoryEntry(slinventoryentry);
        return true;
_L5:
        inventoryFragmentHelper.ShareInventoryEntry(slinventoryentry);
        return true;
_L6:
        usermanager.getInventoryManager().copyToClipboard(new com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager.InventoryClipboardEntry(true, slinventoryentry));
        Toast.makeText(getContext(), 0x7f0900cd, 1).show();
        return true;
_L7:
        usermanager.getInventoryManager().copyToClipboard(new com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager.InventoryClipboardEntry(false, slinventoryentry));
        Toast.makeText(getContext(), 0x7f0900cd, 1).show();
        return true;
        com.lumiyaviewer.lumiya.react.SubscriptionData.DataNotReadyException datanotreadyexception;
        datanotreadyexception;
        Debug.Warning(datanotreadyexception);
        if (true) goto _L2; else goto _L8
_L8:
    }

    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        updateMenuItems();
    }

    public void onStart()
    {
        super.onStart();
        showEntry(UUIDPool.getUUID(getArguments().getString("itemUUID")));
    }

    public void onStop()
    {
        showEntry(null);
        super.onStop();
    }

    public void setFragmentArgs(Intent intent, Bundle bundle)
    {
        intent = ActivityUtils.getActiveAgentID(intent);
        if (intent != null)
        {
            getArguments().putString("activeAgentUUID", intent.toString());
        }
        if (bundle != null)
        {
            getArguments().putAll(bundle);
        }
        if (isFragmentStarted())
        {
            showEntry(UUIDPool.getUUID(getArguments().getString("itemUUID")));
        }
    }
}
