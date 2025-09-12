// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objects;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.chat.generic.OnChatEventListener;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.finance.SLFinancialInfo;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListEntry;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteType;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.SLMuteList;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.objects.PayInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectProfileData;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject;
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.BalanceManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList;
import com.lumiyaviewer.lumiya.slproto.users.manager.MyAvatarState;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.ChatterNameDisplayer;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle;
import com.lumiyaviewer.lumiya.ui.common.ReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.objects:
//            TaskInventoryFragment, ObjectDerezDialog

public class ObjectDetailsFragment extends FragmentWithTitle
    implements ReloadableFragment, android.view.View.OnClickListener, com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
{

    private static final String LOCAL_ID_KEY = "localID";
    private static final int objectPayButtons[] = {
        0x7f100224, 0x7f100225, 0x7f100226, 0x7f100227
    };
    private final SubscriptionData balanceSubscription = new SubscriptionData(UIThreadExecutor.getInstance());
    private final OnChatEventListener chatEventListener = new _2D_.Lambda.IbcMrpWxKnmu4WU7ZN8rETVfqs8._cls3(this);
    private final LoadableMonitor loadableMonitor;
    private MenuItem menuItemObjectBlock;
    private MenuItem menuItemObjectDelete;
    private MenuItem menuItemObjectTake;
    private MenuItem menuItemObjectTakeCopy;
    private final SubscriptionData myAvatarState = new SubscriptionData(UIThreadExecutor.getInstance());
    private int objectLocalID;
    private final SubscriptionData objectProfile = new SubscriptionData(UIThreadExecutor.getInstance());
    private SLObjectProfileData objectProfileData;
    private final ChatterNameDisplayer ownerNameDisplayer = new ChatterNameDisplayer();

    public ObjectDetailsFragment()
    {
        objectProfileData = null;
        menuItemObjectTake = null;
        menuItemObjectTakeCopy = null;
        menuItemObjectDelete = null;
        menuItemObjectBlock = null;
        objectLocalID = 0;
        loadableMonitor = (new LoadableMonitor(new Loadable[] {
            objectProfile
        })).withOptionalLoadables(new Loadable[] {
            balanceSubscription, myAvatarState
        }).withDataChangedListener(this);
    }

    private void buyObject()
    {
        SLObjectProfileData slobjectprofiledata = objectProfileData;
        UserManager usermanager = getUserManager();
        int i = objectLocalID;
        if (slobjectprofiledata != null && usermanager != null)
        {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
            builder.setMessage(String.format(getString(0x7f090213), new Object[] {
                slobjectprofiledata.name().or(getString(0x7f090239)), Integer.valueOf(slobjectprofiledata.salePrice())
            })).setCancelable(false).setPositiveButton("Yes", new _2D_.Lambda.IbcMrpWxKnmu4WU7ZN8rETVfqs8._cls5(i, usermanager, slobjectprofiledata)).setNegativeButton("No", new _2D_.Lambda.IbcMrpWxKnmu4WU7ZN8rETVfqs8());
            builder.show();
        }
    }

    private UserManager getUserManager()
    {
        return ActivityUtils.getUserManager(getArguments());
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_11291(SLAgentCircuit slagentcircuit, SLObjectProfileData slobjectprofiledata, String s, DialogInterface dialoginterface, int i)
    {
        slagentcircuit.getModules().muteList.Block(new MuteListEntry(MuteType.OBJECT, slobjectprofiledata.objectUUID(), s, 15));
        dialoginterface.dismiss();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_11639(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_24012(UserManager usermanager, int i, SLObjectProfileData slobjectprofiledata, DialogInterface dialoginterface, int j)
    {
        usermanager = usermanager.getActiveAgentCircuit();
        if (usermanager != null)
        {
            usermanager.BuyObject(i, slobjectprofiledata.saleType(), slobjectprofiledata.salePrice());
        }
        dialoginterface.dismiss();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_24413(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_25106(UserManager usermanager, SLObjectProfileData slobjectprofiledata, int i, DialogInterface dialoginterface, int j)
    {
        usermanager = usermanager.getActiveAgentCircuit();
        if (usermanager != null)
        {
            usermanager = usermanager.getModules();
            if (usermanager != null)
            {
                ((SLModules) (usermanager)).financialInfo.DoPayObject(slobjectprofiledata.objectUUID(), i);
            }
        }
        dialoginterface.dismiss();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_25649(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    public static Bundle makeSelection(UUID uuid, int i)
    {
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        bundle.putInt("localID", i);
        return bundle;
    }

    private void openObjectContents()
    {
        UserManager usermanager = getUserManager();
        if (objectProfileData != null && objectLocalID != 0 && usermanager != null)
        {
            DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/objects/TaskInventoryFragment, TaskInventoryFragment.makeSelection(usermanager.getUserID(), objectProfileData.objectUUID(), objectLocalID));
        }
    }

    private void payObject(int i)
    {
        SLObjectProfileData slobjectprofiledata = objectProfileData;
        UserManager usermanager = getUserManager();
        if (slobjectprofiledata != null && usermanager != null)
        {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
            builder.setMessage(String.format(getString(0x7f090240), new Object[] {
                slobjectprofiledata.name().or(getString(0x7f090239)), Integer.valueOf(i)
            })).setCancelable(false).setPositiveButton("Yes", new _2D_.Lambda.IbcMrpWxKnmu4WU7ZN8rETVfqs8._cls6(i, usermanager, slobjectprofiledata)).setNegativeButton("No", new _2D_.Lambda.IbcMrpWxKnmu4WU7ZN8rETVfqs8._cls2());
            builder.show();
        }
    }

    private void payObjectQuick(int i)
    {
        if (objectProfileData != null)
        {
            Object obj = objectProfileData.payInfo();
            if (obj != null)
            {
                obj = ((PayInfo) (obj)).payPrices();
                if (obj != null && i >= 0 && i < ((ImmutableList) (obj)).size())
                {
                    i = ((Integer)((ImmutableList) (obj)).get(i)).intValue();
                    if (i != 0)
                    {
                        payObject(i);
                    }
                }
            }
        }
    }

    private void showDeadObject()
    {
        View view = getView();
        if (view != null)
        {
            view.findViewById(0x7f10020c).setVisibility(8);
            view.findViewById(0x7f10020d).setVisibility(0);
            ((TextView)view.findViewById(0x7f10020d)).setText(0x7f09021e);
            view.findViewById(0x7f10020e).setVisibility(8);
        }
    }

    private void showObject(int i)
    {
        objectLocalID = i;
        UserManager usermanager = getUserManager();
        if (usermanager != null)
        {
            objectProfile.subscribe(usermanager.getObjectsManager().getObjectProfile(), Integer.valueOf(i));
            myAvatarState.subscribe(usermanager.getObjectsManager().myAvatarState(), SubscriptionSingleKey.Value);
            balanceSubscription.subscribe(usermanager.getBalanceManager().getBalance(), SubscriptionSingleKey.Value);
        }
    }

    private void showObjectNotLoaded()
    {
        View view = getView();
        if (view != null)
        {
            view.findViewById(0x7f10020c).setVisibility(8);
            view.findViewById(0x7f10020d).setVisibility(0);
            ((TextView)view.findViewById(0x7f10020d)).setText(0x7f090242);
            view.findViewById(0x7f10020e).setVisibility(8);
        }
    }

    private void showObjectOwnerInfo()
    {
        Object obj = getUserManager();
        if (objectProfileData != null && obj != null)
        {
            UUID uuid = objectProfileData.ownerUUID();
            if (uuid != null)
            {
                obj = ChatterID.getUserChatterID(((UserManager) (obj)).getUserID(), uuid);
                DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/UserProfileFragment, UserProfileFragment.makeSelection(((ChatterID) (obj))));
            }
        }
    }

    private void showObjectProfile(SLObjectProfileData slobjectprofiledata)
    {
        UserManager usermanager;
        Object obj1;
        objectProfileData = slobjectprofiledata;
        obj1 = getView();
        usermanager = getUserManager();
        if (obj1 == null) goto _L2; else goto _L1
_L1:
        ((View) (obj1)).findViewById(0x7f10020c).setVisibility(8);
        if (!slobjectprofiledata.isDead()) goto _L4; else goto _L3
_L3:
        ((View) (obj1)).findViewById(0x7f10020d).setVisibility(0);
        ((View) (obj1)).findViewById(0x7f10020e).setVisibility(8);
        ((TextView)((View) (obj1)).findViewById(0x7f10020d)).setText(0x7f09021e);
_L2:
        if (usermanager != null && slobjectprofiledata.isDead() ^ true)
        {
            if (slobjectprofiledata.isPayable() && slobjectprofiledata.payInfo() == null)
            {
                UUID uuid = slobjectprofiledata.objectUUID();
                obj1 = usermanager.getActiveAgentCircuit();
                if (obj1 != null && uuid != null)
                {
                    ((SLAgentCircuit) (obj1)).DoRequestPayPrice(uuid);
                }
            }
            slobjectprofiledata = slobjectprofiledata.ownerUUID();
            if (slobjectprofiledata != null)
            {
                slobjectprofiledata = ChatterID.getUserChatterID(usermanager.getUserID(), slobjectprofiledata);
                ownerNameDisplayer.setChatterID(slobjectprofiledata);
            }
        }
        updateOptionsMenu();
        return;
_L4:
        Object obj;
        int i;
        int j;
        boolean flag;
        ((View) (obj1)).findViewById(0x7f10020e).setVisibility(0);
        ((View) (obj1)).findViewById(0x7f10020d).setVisibility(8);
        obj = ((View) (obj1)).findViewById(0x7f100104);
        Object obj2;
        if (slobjectprofiledata.isTouchable())
        {
            i = 0;
        } else
        {
            i = 8;
        }
        ((View) (obj)).setVisibility(i);
        obj2 = (Button)((View) (obj1)).findViewById(0x7f100104);
        if (Strings.isNullOrEmpty(slobjectprofiledata.touchName()))
        {
            obj = getString(0x7f09024a);
        } else
        {
            obj = slobjectprofiledata.touchName();
        }
        ((Button) (obj2)).setText(((CharSequence) (obj)));
        if (usermanager == null)
        {
            break; /* Loop/switch isn't completed */
        }
        obj = usermanager.getActiveAgentCircuit();
        if (obj == null)
        {
            break; /* Loop/switch isn't completed */
        }
        obj = ((SLAgentCircuit) (obj)).getModules();
        if (obj == null)
        {
            break; /* Loop/switch isn't completed */
        }
        flag = ((SLModules) (obj)).rlvController.canSit();
_L6:
        obj = (MyAvatarState)myAvatarState.getData();
        if (obj != null)
        {
            if (((MyAvatarState) (obj)).isSitting() && ((MyAvatarState) (obj)).sittingOn() == objectLocalID)
            {
                i = 1;
                flag = false;
            } else
            {
                i = 0;
            }
        } else
        {
            i = 0;
        }
        obj = ((View) (obj1)).findViewById(0x7f100105);
        if (flag)
        {
            j = 0;
        } else
        {
            j = 8;
        }
        ((View) (obj)).setVisibility(j);
        obj = ((View) (obj1)).findViewById(0x7f100212);
        if (i != 0)
        {
            i = 0;
        } else
        {
            i = 8;
        }
        ((View) (obj)).setVisibility(i);
        ((TextView)((View) (obj1)).findViewById(0x7f100210)).setText((CharSequence)slobjectprofiledata.name().or(getString(0x7f090239)));
        ((TextView)((View) (obj1)).findViewById(0x7f100211)).setText((CharSequence)slobjectprofiledata.description().or(""));
        obj = ((View) (obj1)).findViewById(0x7f100214);
        if (slobjectprofiledata.ownerUUID() != null)
        {
            i = 0;
        } else
        {
            i = 8;
        }
        ((View) (obj)).setVisibility(i);
        obj = ((View) (obj1)).findViewById(0x7f100217);
        if (slobjectprofiledata.floatingText().isPresent())
        {
            i = 0;
        } else
        {
            i = 8;
        }
        ((View) (obj)).setVisibility(i);
        ((TextView)((View) (obj1)).findViewById(0x7f100218)).setText((CharSequence)slobjectprofiledata.floatingText().or(""));
        obj = ((View) (obj1)).findViewById(0x7f100219);
        if (slobjectprofiledata.saleType() != 0)
        {
            i = 0;
        } else
        {
            i = 8;
        }
        ((View) (obj)).setVisibility(i);
        ((TextView)((View) (obj1)).findViewById(0x7f10021a)).setText(getString(0x7f090215, new Object[] {
            Integer.valueOf(slobjectprofiledata.salePrice())
        }));
        obj = (Integer)balanceSubscription.getData();
        if (obj != null)
        {
            ((TextView)((View) (obj1)).findViewById(0x7f10021c)).setText(getString(0x7f09020f, new Object[] {
                obj
            }));
        } else
        {
            ((TextView)((View) (obj1)).findViewById(0x7f10021c)).setText("");
        }
        if (slobjectprofiledata.isPayable())
        {
            obj = slobjectprofiledata.payInfo();
        } else
        {
            obj = null;
        }
        if (obj != null)
        {
            obj2 = ((PayInfo) (obj)).payPrices();
            if (obj2 != null)
            {
                i = 0;
                j = 0;
                while (i < objectPayButtons.length && i < ((ImmutableList) (obj2)).size()) 
                {
                    int k = ((Integer)((ImmutableList) (obj2)).get(i)).intValue();
                    if (k == -2)
                    {
                        k = ((PayInfo) (obj)).defaultPayPrice();
                    }
                    if (k <= 0)
                    {
                        ((View) (obj1)).findViewById(objectPayButtons[i]).setVisibility(8);
                        ((View) (obj1)).findViewById(objectPayButtons[i]).setTag(0x7f100020, Integer.valueOf(0));
                    } else
                    {
                        ((Button)((View) (obj1)).findViewById(objectPayButtons[i])).setText(String.format(getString(0x7f090263), new Object[] {
                            Integer.valueOf(k)
                        }));
                        ((View) (obj1)).findViewById(objectPayButtons[i]).setVisibility(0);
                        ((View) (obj1)).findViewById(objectPayButtons[i]).setTag(0x7f100020, Integer.valueOf(k));
                        j++;
                    }
                    i++;
                }
                View view = ((View) (obj1)).findViewById(0x7f100223);
                if (j != 0)
                {
                    i = 0;
                } else
                {
                    i = 8;
                }
                view.setVisibility(i);
            } else
            {
                ((View) (obj1)).findViewById(0x7f100223).setVisibility(8);
            }
            if (((PayInfo) (obj)).defaultPayPrice() != -1)
            {
                if (((EditText)((View) (obj1)).findViewById(0x7f100221)).getText().toString().equals(""))
                {
                    if (((PayInfo) (obj)).defaultPayPrice() > 0)
                    {
                        ((EditText)((View) (obj1)).findViewById(0x7f100221)).setText(getString(0x7f09023d, new Object[] {
                            Integer.valueOf(((PayInfo) (obj)).defaultPayPrice())
                        }));
                    } else
                    {
                        ((EditText)((View) (obj1)).findViewById(0x7f100221)).setText("");
                    }
                }
                ((View) (obj1)).findViewById(0x7f10021f).setVisibility(0);
            } else
            {
                ((View) (obj1)).findViewById(0x7f10021f).setVisibility(8);
            }
            ((View) (obj1)).findViewById(0x7f10021d).setVisibility(0);
        } else
        {
            ((View) (obj1)).findViewById(0x7f10021d).setVisibility(8);
        }
        if (true) goto _L2; else goto _L5
_L5:
        flag = false;
          goto _L6
        if (true) goto _L2; else goto _L7
_L7:
    }

    private void sitOnObject()
    {
        Object obj = getUserManager();
        if (obj != null && objectProfileData != null)
        {
            obj = ((UserManager) (obj)).getActiveAgentCircuit();
            if (obj != null)
            {
                obj = ((SLAgentCircuit) (obj)).getModules();
                if (obj != null)
                {
                    ((SLModules) (obj)).avatarControl.SitOnObject(objectProfileData.objectUUID());
                }
            }
        }
    }

    private void standUp()
    {
        Object obj = getUserManager();
        if (obj != null && objectProfileData != null)
        {
            obj = ((UserManager) (obj)).getActiveAgentCircuit();
            if (obj != null)
            {
                obj = ((SLAgentCircuit) (obj)).getModules();
                if (obj != null)
                {
                    ((SLModules) (obj)).avatarControl.Stand();
                }
            }
        }
    }

    private void touchObject()
    {
        Object obj = getUserManager();
        if (obj != null && objectLocalID != 0)
        {
            obj = ((UserManager) (obj)).getActiveAgentCircuit();
            if (obj != null)
            {
                ((SLAgentCircuit) (obj)).TouchObject(objectLocalID);
            }
        }
    }

    private void updateOptionsMenu()
    {
        boolean flag3 = false;
        boolean flag = false;
        UserManager usermanager = getUserManager();
        boolean flag1;
        boolean flag2;
        if (usermanager != null && objectProfileData != null)
        {
            if (objectProfileData.isDead() ^ true)
            {
                flag2 = usermanager.getUserID().equals(objectProfileData.ownerUUID());
                if (usermanager.getUserID().equals(objectProfileData.ownerUUID()))
                {
                    flag = objectProfileData.isCopyable();
                }
                flag3 = usermanager.getUserID().equals(objectProfileData.ownerUUID());
                boolean flag4 = true;
                flag1 = flag;
                flag = flag3;
                flag3 = flag4;
            } else
            {
                flag = false;
                flag1 = false;
                flag2 = false;
            }
        } else
        {
            flag = false;
            flag1 = false;
            flag2 = false;
        }
        if (menuItemObjectTake != null)
        {
            menuItemObjectTake.setVisible(flag2);
        }
        if (menuItemObjectTakeCopy != null)
        {
            menuItemObjectTakeCopy.setVisible(flag1);
        }
        if (menuItemObjectDelete != null)
        {
            menuItemObjectDelete.setVisible(flag);
        }
        if (menuItemObjectBlock != null)
        {
            menuItemObjectBlock.setVisible(flag3);
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_objects_ObjectDetailsFragment_6633(SLChatEvent slchatevent)
    {
        if (isFragmentVisible())
        {
            UserManager usermanager = getUserManager();
            ChatMessageSource chatmessagesource = slchatevent.getSource();
            if ((chatmessagesource instanceof ChatMessageSourceObject) && objectProfileData != null && usermanager != null && Objects.equal(chatmessagesource.getSourceUUID(), objectProfileData.objectUUID()))
            {
                Toast.makeText(getContext(), slchatevent.getPlainTextMessage(getContext(), usermanager, false), 1).show();
            }
        }
    }

    public void onClick(View view)
    {
        int j;
        j = view.getId();
        for (int i = 0; i < objectPayButtons.length; i++)
        {
            if (objectPayButtons[i] == j)
            {
                payObjectQuick(i);
                return;
            }
        }

        j;
        JVM INSTR lookupswitch 7: default 104
    //                   2131755268: 105
    //                   2131755269: 110
    //                   2131755538: 115
    //                   2131755539: 171
    //                   2131755542: 120
    //                   2131755547: 125
    //                   2131755554: 130;
           goto _L1 _L2 _L3 _L4 _L5 _L6 _L7 _L8
_L1:
        return;
_L2:
        touchObject();
        return;
_L3:
        sitOnObject();
        return;
_L4:
        standUp();
        return;
_L6:
        showObjectOwnerInfo();
        return;
_L7:
        buyObject();
        return;
_L8:
        view = getView();
        if (view != null)
        {
            try
            {
                payObject(Integer.parseInt(((EditText)view.findViewById(0x7f100221)).getText().toString()));
                return;
            }
            // Misplaced declaration of an exception variable
            catch (View view)
            {
                view.printStackTrace();
            }
            return;
        }
          goto _L1
_L5:
        openObjectContents();
        return;
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater)
    {
        super.onCreateOptionsMenu(menu, menuinflater);
        menuinflater.inflate(0x7f120015, menu);
        menuItemObjectTake = menu.findItem(0x7f100333);
        menuItemObjectTakeCopy = menu.findItem(0x7f100334);
        menuItemObjectDelete = menu.findItem(0x7f100335);
        menuItemObjectBlock = menu.findItem(0x7f100336);
        updateOptionsMenu();
    }

    public View onCreateView(LayoutInflater layoutinflater, final ViewGroup objectPayButton, Bundle bundle)
    {
        super.onCreateView(layoutinflater, objectPayButton, bundle);
        Debug.Log("ObjectDetailsFragment: onCreateView called");
        layoutinflater = layoutinflater.inflate(0x7f040075, objectPayButton, false);
        ownerNameDisplayer.bindViews((TextView)layoutinflater.findViewById(0x7f100215), (ChatterPicView)layoutinflater.findViewById(0x7f10013f));
        layoutinflater.findViewById(0x7f10020c).setVisibility(0);
        layoutinflater.findViewById(0x7f10020d).setVisibility(8);
        layoutinflater.findViewById(0x7f10020e).setVisibility(8);
        layoutinflater.findViewById(0x7f100104).setOnClickListener(this);
        layoutinflater.findViewById(0x7f100105).setOnClickListener(this);
        layoutinflater.findViewById(0x7f100212).setOnClickListener(this);
        layoutinflater.findViewById(0x7f100216).setOnClickListener(this);
        layoutinflater.findViewById(0x7f10021b).setOnClickListener(this);
        layoutinflater.findViewById(0x7f100222).setOnClickListener(this);
        layoutinflater.findViewById(0x7f100213).setOnClickListener(this);
        objectPayButton = objectPayButtons;
        int j = objectPayButton.length;
        for (int i = 0; i < j; i++)
        {
            layoutinflater.findViewById(objectPayButton[i]).setOnClickListener(this);
        }

        objectPayButton = (Button)layoutinflater.findViewById(0x7f100222);
        ((EditText)layoutinflater.findViewById(0x7f100221)).addTextChangedListener(new TextWatcher() {

            final ObjectDetailsFragment this$0;
            final Button val$objectPayButton;

            public void afterTextChanged(Editable editable)
            {
                try
                {
                    Integer.parseInt(editable.toString());
                    objectPayButton.setEnabled(true);
                    return;
                }
                // Misplaced declaration of an exception variable
                catch (Editable editable)
                {
                    objectPayButton.setEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence charsequence, int k, int l, int i1)
            {
            }

            public void onTextChanged(CharSequence charsequence, int k, int l, int i1)
            {
            }

            
            {
                this$0 = ObjectDetailsFragment.this;
                objectPayButton = button;
                super();
            }
        });
        return layoutinflater;
    }

    public void onDestroyView()
    {
        ownerNameDisplayer.unbindViews();
        super.onDestroyView();
    }

    public void onLoadableDataChanged()
    {
        Throwable throwable = objectProfile.getError();
        SLObjectProfileData slobjectprofiledata = (SLObjectProfileData)objectProfile.getData();
        if (throwable instanceof com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager.ObjectDoesNotExistException)
        {
            showDeadObject();
            return;
        }
        if (throwable != null || slobjectprofiledata == null)
        {
            showObjectNotLoaded();
            return;
        } else
        {
            showObjectProfile(slobjectprofiledata);
            return;
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        Object obj;
        Object obj1;
        int i;
        obj = null;
        obj1 = getUserManager();
        i = getArguments().getInt("localID");
        if (obj1 == null || objectLocalID == 0) goto _L2; else goto _L1
_L1:
        menuitem.getItemId();
        JVM INSTR tableswitch 2131755827 2131755830: default 64
    //                   2131755827 70
    //                   2131755828 88
    //                   2131755829 106
    //                   2131755830 124;
           goto _L2 _L3 _L4 _L5 _L6
_L2:
        return super.onOptionsItemSelected(menuitem);
_L3:
        ObjectDerezDialog.askForObjectDerez(getContext(), ObjectDerezDialog.DerezAction.Take, ((UserManager) (obj1)).getUserID(), i);
        return true;
_L4:
        ObjectDerezDialog.askForObjectDerez(getContext(), ObjectDerezDialog.DerezAction.TakeCopy, ((UserManager) (obj1)).getUserID(), i);
        return true;
_L5:
        ObjectDerezDialog.askForObjectDerez(getContext(), ObjectDerezDialog.DerezAction.Delete, ((UserManager) (obj1)).getUserID(), i);
        return true;
_L6:
        obj1 = ((UserManager) (obj1)).getActiveAgentCircuit();
        SLObjectProfileData slobjectprofiledata = (SLObjectProfileData)objectProfile.getData();
        menuitem = obj;
        if (slobjectprofiledata != null)
        {
            menuitem = (String)slobjectprofiledata.name().orNull();
        }
        if (obj1 != null && slobjectprofiledata != null && menuitem != null)
        {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
            builder.setMessage(0x7f090210);
            builder.setPositiveButton("Yes", new _2D_.Lambda.IbcMrpWxKnmu4WU7ZN8rETVfqs8._cls4(obj1, slobjectprofiledata, menuitem));
            builder.setNegativeButton("No", new _2D_.Lambda.IbcMrpWxKnmu4WU7ZN8rETVfqs8._cls1());
            builder.setCancelable(true);
            builder.create().show();
        }
        return true;
    }

    public void onPause()
    {
        UserManager usermanager = getUserManager();
        if (usermanager != null)
        {
            usermanager.getChatterList().getActiveChattersManager().removeObjectMessageListener(chatEventListener);
        }
        super.onPause();
    }

    public void onResume()
    {
        super.onResume();
        UserManager usermanager = getUserManager();
        if (usermanager != null)
        {
            usermanager.getChatterList().getActiveChattersManager().addObjectMessageListener(chatEventListener, UIThreadExecutor.getInstance());
        }
    }

    public void onStart()
    {
        super.onStart();
        setTitle(getString(0x7f090224), null);
        showObject(getArguments().getInt("localID"));
    }

    public void onStop()
    {
        loadableMonitor.unsubscribeAll();
        ownerNameDisplayer.setChatterID(null);
        super.onStop();
    }

    public void setFragmentArgs(Intent intent, Bundle bundle)
    {
        if (bundle != null)
        {
            getArguments().putAll(bundle);
            int i = bundle.getInt("localID");
            if (isFragmentStarted())
            {
                showObject(i);
            }
        }
    }

}
