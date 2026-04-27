// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.StreamingMediaService;
import com.lumiyaviewer.lumiya.dao.Chatter;
import com.lumiyaviewer.lumiya.dao.Friend;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.modules.SLMinimap;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.SLWorldMap;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteListEntry;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.MuteType;
import com.lumiyaviewer.lumiya.slproto.modules.mutelist.SLMuteList;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.modules.voice.SLVoice;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.FriendManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.ui.chat.ChatNewActivity;
import com.lumiyaviewer.lumiya.ui.chat.GroupNoticeFragment;
import com.lumiyaviewer.lumiya.ui.chat.PayUserFragment;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatFragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.chat.profiles.GroupProfileFragment;
import com.lumiyaviewer.lumiya.ui.chat.profiles.ParcelPropertiesFragment;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.ui.inventory.InventoryActivity;
import com.lumiyaviewer.lumiya.voiceintf.VoicePluginServiceConnection;
import java.util.concurrent.atomic.AtomicInteger;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            ChatterReloadableFragment, ReloadableFragment, TextFieldDialogBuilder, DetailsActivity, 
//            TeleportProgressDialog

public abstract class UserFunctionsFragment extends ChatterReloadableFragment
    implements ReloadableFragment
{

    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues[];
    protected final SubscriptionData currentLocationInfo = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.EGu4GUNsisO_OSWWZeAugrk47Ks._cls10(this));
    private final SubscriptionData voiceLoggedIn = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.EGu4GUNsisO_OSWWZeAugrk47Ks._cls9(this));

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues;
        }
        int ai[] = new int[com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.values().length];
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Group.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Local.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.User.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues = ai;
        return ai;
    }

    public UserFunctionsFragment()
    {
    }

    private void handleEnableVoice()
    {
        if (!VoicePluginServiceConnection.checkPluginInstalled(getContext()))
        {
            (new android.support.v7.app.AlertDialog.Builder(getContext())).setTitle(0x7f090113).setMessage(getContext().getString(0x7f090114, new Object[] {
                "Google Play"
            })).setPositiveButton("Yes", new _2D_.Lambda.EGu4GUNsisO_OSWWZeAugrk47Ks._cls7(this)).setNegativeButton("No", new _2D_.Lambda.EGu4GUNsisO_OSWWZeAugrk47Ks()).setCancelable(true).create().show();
            return;
        } else
        {
            (new android.support.v7.app.AlertDialog.Builder(getContext())).setMessage(getContext().getString(0x7f090115)).setPositiveButton("Yes", new _2D_.Lambda.EGu4GUNsisO_OSWWZeAugrk47Ks._cls1()).setNegativeButton("No", new _2D_.Lambda.EGu4GUNsisO_OSWWZeAugrk47Ks._cls2()).setCancelable(true).create().show();
            return;
        }
    }

    private void handlePlayParcelMedia()
    {
        StreamingMediaService.startStreamingMediaService(getContext(), userManager);
    }

    private void handleTeleportTo(SLAgentCircuit slagentcircuit, com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser)
    {
        if (slagentcircuit != null)
        {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
            builder.setMessage(getString(0x7f09034d)).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda.EGu4GUNsisO_OSWWZeAugrk47Ks._cls15(this, slagentcircuit, chatteriduser)).setNegativeButton("No", new _2D_.Lambda.EGu4GUNsisO_OSWWZeAugrk47Ks._cls3());
            builder.create().show();
        }
    }

    private void handleUserAddFriend(SLAgentCircuit slagentcircuit, com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser)
    {
        TextFieldDialogBuilder textfielddialogbuilder = new TextFieldDialogBuilder(getContext());
        textfielddialogbuilder.setTitle(getString(0x7f09024e));
        textfielddialogbuilder.setDefaultText(getString(0x7f0900d8));
        textfielddialogbuilder.setOnTextEnteredListener(new _2D_.Lambda.EGu4GUNsisO_OSWWZeAugrk47Ks._cls12(slagentcircuit, chatteriduser));
        textfielddialogbuilder.show();
    }

    private void handleUserCloseChat(ChatterID chatterid, boolean flag)
    {
        boolean flag2 = true;
        if (chatterid != null)
        {
            UserManager usermanager = chatterid.getUserManager();
            if (usermanager != null)
            {
                ActiveChattersManager activechattersmanager = usermanager.getChatterList().getActiveChattersManager();
                boolean flag1 = flag2;
                if (!flag)
                {
                    if (chatterid.getChatterType() == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Group)
                    {
                        flag1 = flag2;
                    } else
                    {
                        flag1 = false;
                    }
                }
                activechattersmanager.markChatterInactive(chatterid, flag1);
                if (this instanceof ChatFragment)
                {
                    chatterid = getActivity();
                    if ((chatterid instanceof DetailsActivity) && !((DetailsActivity)chatterid).closeDetailsFragment(this) && (chatterid instanceof ChatNewActivity))
                    {
                        DetailsActivity.showDetails(chatterid, ChatFragmentActivityFactory.getInstance(), ChatFragment.makeSelection(ChatterID.getLocalChatterID(usermanager.getUserID())));
                    }
                }
            }
        }
    }

    private void handleUserMute(ChatterID chatterid)
    {
        String s = null;
        UserManager usermanager = userManager;
        if (chatterid != null && usermanager != null)
        {
            if (nameRetriever != null)
            {
                s = nameRetriever.getResolvedName();
            }
            CharSequence acharsequence[];
            android.app.AlertDialog.Builder builder;
            AtomicInteger atomicinteger;
            if (s == null)
            {
                s = getString(0x7f0901c8);
            }
            builder = new android.app.AlertDialog.Builder(getContext());
            builder.setTitle(getString(0x7f09007f, new Object[] {
                s
            })).setCancelable(true);
            if (usermanager.getActiveAgentCircuit() != null)
            {
                acharsequence = new CharSequence[2];
                acharsequence[0] = getString(0x7f0901bf);
                acharsequence[1] = getString(0x7f09007e);
            } else
            {
                acharsequence = new CharSequence[1];
                acharsequence[0] = getString(0x7f0901bf);
            }
            atomicinteger = new AtomicInteger(0);
            builder.setSingleChoiceItems(acharsequence, 0, new _2D_.Lambda.EGu4GUNsisO_OSWWZeAugrk47Ks._cls8(atomicinteger));
            builder.setPositiveButton("OK", new _2D_.Lambda.EGu4GUNsisO_OSWWZeAugrk47Ks._cls17(this, atomicinteger, chatterid, usermanager, s));
            builder.setNegativeButton("Cancel", new _2D_.Lambda.EGu4GUNsisO_OSWWZeAugrk47Ks._cls4());
            builder.create().show();
        }
    }

    private void handleUserOfferTeleport(UserManager usermanager, SLAgentCircuit slagentcircuit, com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser)
    {
        Object obj1 = usermanager.getCurrentLocationInfoSnapshot();
        Object obj = "";
        usermanager = ((UserManager) (obj));
        if (obj1 != null)
        {
            obj1 = ((CurrentLocationInfo) (obj1)).parcelData();
            usermanager = ((UserManager) (obj));
            if (obj1 != null)
            {
                usermanager = Strings.nullToEmpty(((ParcelData) (obj1)).getName());
            }
        }
        obj = new TextFieldDialogBuilder(getContext());
        ((TextFieldDialogBuilder) (obj)).setTitle(getString(0x7f09024f));
        ((TextFieldDialogBuilder) (obj)).setDefaultText((new StringBuilder()).append("Join me in ").append(usermanager).toString());
        ((TextFieldDialogBuilder) (obj)).setOnTextEnteredListener(new _2D_.Lambda.EGu4GUNsisO_OSWWZeAugrk47Ks._cls13(slagentcircuit, chatteriduser));
        ((TextFieldDialogBuilder) (obj)).show();
    }

    private void handleUserOpenChat(ChatterID chatterid)
    {
        DetailsActivity.showDetails(getActivity(), ChatFragmentActivityFactory.getInstance(), ChatFragment.makeSelection(chatterid));
    }

    private void handleUserPayUser(com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser)
    {
        DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/PayUserFragment, PayUserFragment.makeSelection(chatteriduser));
    }

    private void handleUserRemoveFriend(SLAgentCircuit slagentcircuit, com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser)
    {
        String s = null;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        if (nameRetriever != null)
        {
            s = nameRetriever.getResolvedName();
        }
        if (s == null)
        {
            s = getString(0x7f0901c8);
        }
        builder.setMessage(String.format(getString(0x7f0900dc), new Object[] {
            s
        })).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda.EGu4GUNsisO_OSWWZeAugrk47Ks._cls11(slagentcircuit, chatteriduser)).setNegativeButton("No", new _2D_.Lambda.EGu4GUNsisO_OSWWZeAugrk47Ks._cls5());
        builder.create().show();
    }

    private void handleUserRequestTeleport(SLAgentCircuit slagentcircuit, com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser)
    {
        TextFieldDialogBuilder textfielddialogbuilder = new TextFieldDialogBuilder(getContext());
        textfielddialogbuilder.setTitle(getString(0x7f0902a5));
        textfielddialogbuilder.setOnTextEnteredListener(new _2D_.Lambda.EGu4GUNsisO_OSWWZeAugrk47Ks._cls14(slagentcircuit, chatteriduser));
        textfielddialogbuilder.show();
    }

    private void handleUserShareObject(com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser)
    {
        String s = null;
        if (nameRetriever != null)
        {
            s = nameRetriever.getResolvedName();
        }
        startActivity(InventoryActivity.makeTransferIntent(getContext(), chatteriduser.agentUUID, chatteriduser.getChatterUUID(), s));
    }

    private void handleUserUnblock(ChatterID chatterid)
    {
        String s = null;
        UserManager usermanager = userManager;
        if (chatterid != null && usermanager != null)
        {
            if (nameRetriever != null)
            {
                s = nameRetriever.getResolvedName();
            }
            if (s == null)
            {
                s = getString(0x7f0901c8);
            }
            (new android.app.AlertDialog.Builder(getContext())).setMessage(getString(0x7f090360, new Object[] {
                s
            })).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda.EGu4GUNsisO_OSWWZeAugrk47Ks._cls16(usermanager, chatterid, s)).setNegativeButton("No", new _2D_.Lambda.EGu4GUNsisO_OSWWZeAugrk47Ks._cls6()).create().show();
        }
    }

    private void handleUserUnmute(ChatterID chatterid)
    {
        UserManager usermanager = userManager;
        if (chatterid != null && usermanager != null)
        {
            usermanager.getChatterList().getActiveChattersManager().unmuteChatter(chatterid);
        }
    }

    private void handleViewLocationDetails()
    {
        if (userManager != null)
        {
            Object obj = userManager.getCurrentLocationInfoSnapshot();
            if (obj != null)
            {
                obj = ((CurrentLocationInfo) (obj)).parcelData();
                if (obj != null)
                {
                    DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/ParcelPropertiesFragment, ParcelPropertiesFragment.makeSelection(userManager.getUserID(), ((ParcelData) (obj))));
                }
            }
        }
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_19926(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_20370(DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        GlobalOptions.getInstance().enableVoice();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_20590(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_23327(UserManager usermanager, ChatterID chatterid, String s, DialogInterface dialoginterface, int i)
    {
        usermanager.getChatterList().getActiveChattersManager().unmuteChatter(chatterid);
        usermanager = usermanager.getActiveAgentCircuit();
        if (usermanager != null)
        {
            if (chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)
            {
                usermanager.getModules().muteList.Unblock(new MuteListEntry(MuteType.AGENT, ((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterid).getChatterUUID(), s, 15));
            } else
            if (chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)
            {
                usermanager.getModules().muteList.Unblock(new MuteListEntry(MuteType.GROUP, ((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)chatterid).getChatterUUID(), s, 15));
                return;
            }
        }
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_24282(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_25645(AtomicInteger atomicinteger, DialogInterface dialoginterface, int i)
    {
        atomicinteger.set(i);
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_26799(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_28847(SLAgentCircuit slagentcircuit, com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser, String s)
    {
        slagentcircuit.AddFriend(chatteriduser.getChatterUUID(), s);
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_29514(SLAgentCircuit slagentcircuit, com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser, DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        slagentcircuit.RemoveFriend(chatteriduser.getChatterUUID());
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_29707(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_30583(SLAgentCircuit slagentcircuit, com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser, String s)
    {
        slagentcircuit.OfferTeleport(chatteriduser.getChatterUUID(), s);
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_31028(SLAgentCircuit slagentcircuit, com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser, String s)
    {
        slagentcircuit.RequestTeleport(chatteriduser.getChatterUUID(), s);
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_31740(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    private void performTeleportTo(SLAgentCircuit slagentcircuit, com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser)
    {
        if (slagentcircuit != null)
        {
            com.lumiyaviewer.lumiya.slproto.types.LLVector3 llvector3 = slagentcircuit.getModules().minimap.getNearbyAgentLocation(chatteriduser.getChatterUUID());
            if (llvector3 != null)
            {
                if (slagentcircuit.TeleportToLocalPosition(llvector3))
                {
                    (new TeleportProgressDialog(getContext(), userManager, 0x7f090350)).show();
                }
            } else
            if (slagentcircuit.getModules().worldMap.TeleportToAgent(chatteriduser.getChatterUUID()))
            {
                (new TeleportProgressDialog(getContext(), userManager, 0x7f090350)).show();
                return;
            }
        }
    }

    void _2D_com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_2D_mthref_2D_0(Boolean boolean1)
    {
        onVoiceLoginStatusChanged(boolean1);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_2D_mthref_2D_1(CurrentLocationInfo currentlocationinfo)
    {
        onCurrentLocationChanged(currentlocationinfo);
    }

    protected void handleStartVoice(ChatterID chatterid)
    {
        SLAgentCircuit slagentcircuit = null;
        UserManager usermanager;
        if (chatterid != null)
        {
            usermanager = chatterid.getUserManager();
        } else
        {
            usermanager = null;
        }
        if (usermanager != null)
        {
            slagentcircuit = usermanager.getActiveAgentCircuit();
        }
        if (chatterid != null && usermanager != null && slagentcircuit != null)
        {
            if (chatterid.getChatterType() == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.User)
            {
                slagentcircuit.getModules().voice.userVoiceChatRequest(chatterid.getOptionalChatterUUID());
            } else
            {
                if (chatterid.getChatterType() == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Group)
                {
                    slagentcircuit.getModules().voice.groupVoiceChatRequest(chatterid.getOptionalChatterUUID());
                    return;
                }
                if (chatterid.getChatterType() == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Local)
                {
                    chatterid = usermanager.getCurrentLocationInfoSnapshot();
                    if (chatterid != null)
                    {
                        chatterid = chatterid.parcelVoiceChannel();
                        if (chatterid != null)
                        {
                            slagentcircuit.getModules().voice.nearbyVoiceChatRequest(chatterid);
                            return;
                        }
                    }
                }
            }
        }
    }

    protected void handleUserViewProfile(ChatterID chatterid)
    {
        if (chatterid == null || !chatterid.isValidUUID()) goto _L2; else goto _L1
_L1:
        _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_ChatterID$ChatterTypeSwitchesValues()[chatterid.getChatterType().ordinal()];
        JVM INSTR tableswitch 1 2: default 44
    //                   1 61
    //                   2 45;
           goto _L2 _L3 _L4
_L2:
        return;
_L4:
        DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/UserProfileFragment, UserProfileFragment.makeSelection(chatterid));
        return;
_L3:
        DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/GroupProfileFragment, GroupProfileFragment.makeSelection(chatterid));
        return;
    }

    protected boolean isVoiceLoggedIn()
    {
        Boolean boolean1 = (Boolean)voiceLoggedIn.getData();
        if (boolean1 != null)
        {
            return boolean1.booleanValue();
        } else
        {
            return false;
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_19348(DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        VoicePluginServiceConnection.setInstallOfferDisplayed(true);
        GlobalOptions.getInstance().enableVoice();
        dialoginterface = new Intent("android.intent.action.VIEW");
        dialoginterface.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya.voice"));
        getContext().startActivity(dialoginterface);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_25742(AtomicInteger atomicinteger, ChatterID chatterid, UserManager usermanager, String s, DialogInterface dialoginterface, int i)
    {
        if (atomicinteger.get() != 0) goto _L2; else goto _L1
_L1:
        handleUserCloseChat(chatterid, true);
_L4:
        return;
_L2:
        if (atomicinteger.get() != 1) goto _L4; else goto _L3
_L3:
        atomicinteger = usermanager.getActiveAgentCircuit();
        if (atomicinteger == null) goto _L6; else goto _L5
_L5:
        if (!(chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)) goto _L8; else goto _L7
_L7:
        atomicinteger.getModules().muteList.Block(new MuteListEntry(MuteType.AGENT, ((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterid).getChatterUUID(), s, 15));
_L6:
        handleUserCloseChat(chatterid, true);
        return;
_L8:
        if (chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)
        {
            atomicinteger.getModules().muteList.Block(new MuteListEntry(MuteType.GROUP, ((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)chatterid).getChatterUUID(), s, 15));
        }
        if (true) goto _L6; else goto _L9
_L9:
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_UserFunctionsFragment_31539(SLAgentCircuit slagentcircuit, com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser, DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        performTeleportTo(slagentcircuit, chatteriduser);
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater)
    {
        super.onCreateOptionsMenu(menu, menuinflater);
        menuinflater.inflate(0x7f120022, menu);
    }

    protected void onCurrentLocationChanged(CurrentLocationInfo currentlocationinfo)
    {
        currentlocationinfo = getActivity();
        if (currentlocationinfo != null)
        {
            currentlocationinfo.supportInvalidateOptionsMenu();
        }
    }

    public void onGlobalOptionsChanged(com.lumiyaviewer.lumiya.GlobalOptions.GlobalOptionsChangedEvent globaloptionschangedevent)
    {
        globaloptionschangedevent = getActivity();
        if (globaloptionschangedevent != null)
        {
            globaloptionschangedevent.supportInvalidateOptionsMenu();
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        UserManager usermanager;
        SLAgentCircuit slagentcircuit;
        if (chatterID != null)
        {
            usermanager = chatterID.getUserManager();
        } else
        {
            usermanager = null;
        }
        if (chatterID == null || usermanager == null) goto _L2; else goto _L1
_L1:
        slagentcircuit = usermanager.getActiveAgentCircuit();
        menuitem.getItemId();
        JVM INSTR lookupswitch 18: default 192
    //                   2131755779: 450
    //                   2131755781: 511
    //                   2131755846: 481
    //                   2131755860: 203
    //                   2131755861: 213
    //                   2131755862: 571
    //                   2131755863: 223
    //                   2131755864: 229
    //                   2131755865: 235
    //                   2131755866: 255
    //                   2131755867: 284
    //                   2131755868: 312
    //                   2131755869: 340
    //                   2131755870: 367
    //                   2131755871: 394
    //                   2131755872: 422
    //                   2131755873: 581
    //                   2131755874: 541;
           goto _L2 _L3 _L4 _L5 _L6 _L7 _L8 _L9 _L10 _L11 _L12 _L13 _L14 _L15 _L16 _L17 _L18 _L19 _L20
_L2:
        return super.onOptionsItemSelected(menuitem);
_L6:
        handleUserOpenChat(chatterID);
        return true;
_L7:
        handleUserViewProfile(chatterID);
        return true;
_L9:
        handleViewLocationDetails();
        return true;
_L10:
        handlePlayParcelMedia();
        return true;
_L11:
        DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/GroupNoticeFragment, GroupNoticeFragment.makeSelection(chatterID));
        return true;
_L12:
        if ((chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser) && slagentcircuit != null)
        {
            handleUserOfferTeleport(usermanager, slagentcircuit, (com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterID);
        }
        return true;
_L13:
        if ((chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser) && slagentcircuit != null)
        {
            handleUserRequestTeleport(slagentcircuit, (com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterID);
        }
        return true;
_L14:
        if ((chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser) && slagentcircuit != null)
        {
            handleTeleportTo(slagentcircuit, (com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterID);
        }
        return true;
_L15:
        if ((chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser) && slagentcircuit != null)
        {
            handleUserPayUser((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterID);
        }
        return true;
_L16:
        if ((chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser) && slagentcircuit != null)
        {
            handleUserShareObject((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterID);
        }
        return true;
_L17:
        if ((chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser) && slagentcircuit != null)
        {
            handleUserAddFriend(slagentcircuit, (com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterID);
        }
        return true;
_L18:
        if ((chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser) && slagentcircuit != null)
        {
            handleUserRemoveFriend(slagentcircuit, (com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterID);
        }
        return true;
_L3:
        if ((chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser) || (chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup))
        {
            handleUserCloseChat(chatterID, false);
        }
        return true;
_L5:
        if ((chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser) || (chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup))
        {
            handleUserMute(chatterID);
        }
        return true;
_L4:
        if ((chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser) || (chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup))
        {
            handleUserUnmute(chatterID);
        }
        return true;
_L20:
        if ((chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser) || (chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup))
        {
            handleUserUnblock(chatterID);
        }
        return true;
_L8:
        handleStartVoice(chatterID);
        return true;
_L19:
        handleEnableVoice();
        return true;
    }

    public void onPrepareOptionsMenu(Menu menu)
    {
        Object obj;
        int ai[];
        super.onPrepareOptionsMenu(menu);
        ai = new int[18];
        ai;
        ai[0] = 0x7f100354;
        ai[1] = 0x7f100355;
        ai[2] = 0x7f100357;
        ai[3] = 0x7f100358;
        ai[4] = 0x7f100359;
        ai[5] = 0x7f10035a;
        ai[6] = 0x7f10035b;
        ai[7] = 0x7f10035c;
        ai[8] = 0x7f10035d;
        ai[9] = 0x7f10035e;
        ai[10] = 0x7f10035f;
        ai[11] = 0x7f100360;
        ai[12] = 0x7f100303;
        ai[13] = 0x7f100346;
        ai[14] = 0x7f100305;
        ai[15] = 0x7f100362;
        ai[16] = 0x7f100356;
        ai[17] = 0x7f100361;
        CurrentLocationInfo currentlocationinfo;
        Object obj1;
        SLAgentCircuit slagentcircuit;
        SLMuteList slmutelist;
        java.util.UUID uuid;
        int k;
        int i1;
        boolean flag5;
        boolean flag8;
        boolean flag10;
        if (chatterID != null)
        {
            obj = chatterID.getUserManager();
        } else
        {
            obj = null;
        }
        if (chatterID == null || obj == null) goto _L2; else goto _L1
_L1:
        Debug.Printf("UserMenu: item type %s", new Object[] {
            chatterID.getChatterType()
        });
        slagentcircuit = ((UserManager) (obj)).getActiveAgentCircuit();
        currentlocationinfo = (CurrentLocationInfo)currentLocationInfo.getData();
        Friend friend;
        boolean flag;
        boolean flag1;
        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag6;
        boolean flag7;
        boolean flag9;
        boolean flag11;
        boolean flag12;
        boolean flag13;
        boolean flag14;
        boolean flag15;
        boolean flag17;
        boolean flag18;
        boolean flag19;
        boolean flag20;
        if (slagentcircuit != null)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        flag17 = chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDLocal;
        flag18 = chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser;
        flag19 = chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup;
        flag4 = false;
        flag1 = false;
        flag7 = false;
        flag8 = false;
        flag10 = GlobalOptions.getInstance().getVoiceEnabled();
        flag5 = isVoiceLoggedIn();
        flag3 = false;
        if (!flag10)
        {
            flag2 = VoicePluginServiceConnection.isPluginSupported();
        } else
        {
            flag2 = false;
        }
        int j;
        int l;
        boolean flag16;
        if (flag17 && currentlocationinfo != null)
        {
            obj1 = currentlocationinfo.parcelData();
            flag7 = flag8;
            if (obj1 != null)
            {
                flag1 = true;
                flag7 = Strings.isNullOrEmpty(((ParcelData) (obj1)).getMediaURL()) ^ true;
            }
            if (flag10 && flag5 && currentlocationinfo.parcelVoiceChannel() != null)
            {
                flag3 = true;
            } else
            {
                flag3 = false;
            }
            if (flag2 && currentlocationinfo.parcelVoiceChannel() != null)
            {
                flag9 = true;
                flag2 = flag3;
            } else
            {
                flag9 = false;
                flag2 = flag3;
            }
        } else
        {
            flag9 = flag2;
            flag1 = false;
            flag2 = flag3;
        }
        if (flag19 || flag18)
        {
            if (flag10)
            {
                flag2 = flag5;
            } else
            {
                flag2 = false;
            }
        }
        if (flag18)
        {
            friend = ((UserManager) (obj)).getChatterList().getFriendManager().getFriend(chatterID.getOptionalChatterUUID());
        } else
        {
            friend = null;
        }
        flag11 = false;
        if (flag18 || flag19)
        {
            obj1 = ((UserManager) (obj)).getChatterList().getActiveChattersManager().getChatter(chatterID);
            if (obj1 != null)
            {
                flag11 = ((Chatter) (obj1)).getActive();
            } else
            {
                flag11 = false;
            }
            if (obj1 != null)
            {
                flag3 = ((Chatter) (obj1)).getMuted();
            } else
            {
                flag3 = false;
            }
            if (slagentcircuit != null)
            {
                slmutelist = slagentcircuit.getModules().muteList;
                uuid = chatterID.getOptionalChatterUUID();
                MuteType mutetype;
                if (flag19)
                {
                    mutetype = MuteType.GROUP;
                } else
                {
                    mutetype = MuteType.AGENT;
                }
                flag4 = slmutelist.isMuted(uuid, mutetype);
                flag12 = flag3;
                flag3 = flag4;
            } else
            {
                flag4 = false;
                flag12 = flag3;
                flag3 = flag4;
            }
        } else
        {
            flag12 = false;
            flag3 = flag4;
        }
        if (flag18 && friend != null)
        {
            flag13 = true;
        } else
        {
            flag13 = false;
        }
        flag20 = com/lumiyaviewer/lumiya/ui/chat/ChatFragment.isInstance(this);
        if (flag18)
        {
            flag14 = com/lumiyaviewer/lumiya/ui/chat/profiles/UserProfileFragment.isInstance(this);
        } else
        if (flag19)
        {
            flag14 = com/lumiyaviewer/lumiya/ui/chat/profiles/GroupProfileFragment.isInstance(this);
        } else
        {
            flag14 = false;
        }
        if (flag18 && flag)
        {
            flag15 = slagentcircuit.getModules().rlvController.canTeleportToLocation();
        } else
        {
            flag15 = false;
        }
        flag5 = false;
        flag4 = flag5;
        if (flag18)
        {
            flag4 = flag5;
            if (flag)
            {
                flag4 = flag5;
                if (flag15)
                {
                    if (slagentcircuit.getModules().minimap.getNearbyAgentLocation(chatterID.getOptionalChatterUUID()) != null)
                    {
                        flag6 = true;
                    } else
                    {
                        flag6 = false;
                    }
                    flag4 = flag6;
                    if (friend != null)
                    {
                        int i;
                        if ((friend.getRightsHas() & 2) != 0)
                        {
                            i = 1;
                        } else
                        {
                            i = 0;
                        }
                        flag4 = flag6 | i;
                    }
                }
            }
        }
        if (!flag19) goto _L4; else goto _L3
_L3:
        if (!flag) goto _L6; else goto _L5
_L5:
        obj = ((UserManager) (obj)).getChatterList().getGroupManager().getAvatarGroupList();
        if (obj == null) goto _L6; else goto _L7
_L7:
        obj = (com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry)((AvatarGroupList) (obj)).Groups.get(chatterID.getOptionalChatterUUID());
        if (obj == null || (((com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry) (obj)).GroupPowers & 0x40000000000L) == 0L) goto _L6; else goto _L8
_L8:
        flag6 = true;
_L33:
        k = ai.length;
        i = 0;
_L31:
        if (i >= k) goto _L10; else goto _L9
_L9:
        i1 = ai[i];
        obj = menu.findItem(i1);
        if (obj == null) goto _L12; else goto _L11
_L11:
        i1;
        JVM INSTR lookupswitch 18: default 888
    //                   2131755779: 1387
    //                   2131755781: 1483
    //                   2131755846: 1424
    //                   2131755860: 1017
    //                   2131755861: 1051
    //                   2131755862: 1559
    //                   2131755863: 1085
    //                   2131755864: 1112
    //                   2131755865: 1144
    //                   2131755866: 1176
    //                   2131755867: 1203
    //                   2131755868: 1230
    //                   2131755869: 1267
    //                   2131755870: 1294
    //                   2131755871: 1321
    //                   2131755872: 1355
    //                   2131755873: 1586
    //                   2131755874: 1522;
           goto _L12 _L13 _L14 _L15 _L16 _L17 _L18 _L19 _L20 _L21 _L22 _L23 _L24 _L25 _L26 _L27 _L28 _L29 _L30
_L12:
        i++;
          goto _L31
_L4:
        flag6 = false;
        continue; /* Loop/switch isn't completed */
_L16:
        if (flag18 || flag19)
        {
            flag16 = flag20 ^ true;
        } else
        {
            flag16 = false;
        }
        ((MenuItem) (obj)).setVisible(flag16);
          goto _L12
_L17:
        if (flag18 || flag19)
        {
            flag16 = flag14 ^ true;
        } else
        {
            flag16 = false;
        }
        ((MenuItem) (obj)).setVisible(flag16);
          goto _L12
_L19:
        if (flag17)
        {
            flag16 = flag1;
        } else
        {
            flag16 = false;
        }
        ((MenuItem) (obj)).setVisible(flag16);
          goto _L12
_L20:
        if (flag17 && flag1)
        {
            flag16 = flag7;
        } else
        {
            flag16 = false;
        }
        ((MenuItem) (obj)).setVisible(flag16);
          goto _L12
_L21:
        if (flag19 && flag)
        {
            flag16 = flag6;
        } else
        {
            flag16 = false;
        }
        ((MenuItem) (obj)).setVisible(flag16);
          goto _L12
_L22:
        if (flag18)
        {
            flag16 = flag;
        } else
        {
            flag16 = false;
        }
        ((MenuItem) (obj)).setVisible(flag16);
          goto _L12
_L23:
        if (flag18)
        {
            flag16 = flag;
        } else
        {
            flag16 = false;
        }
        ((MenuItem) (obj)).setVisible(flag16);
          goto _L12
_L24:
        if (flag18 && flag && flag15)
        {
            flag16 = flag4;
        } else
        {
            flag16 = false;
        }
        ((MenuItem) (obj)).setVisible(flag16);
          goto _L12
_L25:
        if (flag18)
        {
            flag16 = flag;
        } else
        {
            flag16 = false;
        }
        ((MenuItem) (obj)).setVisible(flag16);
          goto _L12
_L26:
        if (flag18)
        {
            flag16 = flag;
        } else
        {
            flag16 = false;
        }
        ((MenuItem) (obj)).setVisible(flag16);
          goto _L12
_L27:
        if (flag18 && flag)
        {
            flag16 = flag13 ^ true;
        } else
        {
            flag16 = false;
        }
        ((MenuItem) (obj)).setVisible(flag16);
          goto _L12
_L28:
        if (flag18 && flag)
        {
            flag16 = flag13;
        } else
        {
            flag16 = false;
        }
        ((MenuItem) (obj)).setVisible(flag16);
          goto _L12
_L13:
        if ((flag18 || flag19) && flag20)
        {
            flag16 = flag11;
        } else
        {
            flag16 = false;
        }
        ((MenuItem) (obj)).setVisible(flag16);
          goto _L12
_L15:
        if ((flag18 || flag19) && flag20 && flag11)
        {
            if (flag12)
            {
                flag16 = flag3;
            } else
            {
                flag16 = false;
            }
            flag16 ^= true;
        } else
        {
            flag16 = false;
        }
        ((MenuItem) (obj)).setVisible(flag16);
          goto _L12
_L14:
        if ((flag18 || flag19) && flag12)
        {
            flag16 = flag3 ^ true;
        } else
        {
            flag16 = false;
        }
        ((MenuItem) (obj)).setVisible(flag16);
          goto _L12
_L30:
        if ((flag18 || flag19) && flag3)
        {
            flag16 = flag;
        } else
        {
            flag16 = false;
        }
        ((MenuItem) (obj)).setVisible(flag16);
          goto _L12
_L18:
        if (flag)
        {
            flag16 = flag2;
        } else
        {
            flag16 = false;
        }
        ((MenuItem) (obj)).setVisible(flag16);
          goto _L12
_L29:
        ((MenuItem) (obj)).setVisible(flag9);
          goto _L12
_L2:
        j = 0;
        for (l = ai.length; j < l; j++)
        {
            obj = menu.findItem(ai[j]);
            if (obj != null)
            {
                ((MenuItem) (obj)).setVisible(false);
            }
        }

_L10:
        return;
_L6:
        flag6 = false;
        if (true) goto _L33; else goto _L32
_L32:
    }

    public void onStart()
    {
        UserManager usermanager = null;
        super.onStart();
        if (chatterID != null)
        {
            usermanager = chatterID.getUserManager();
        }
        if (usermanager != null)
        {
            voiceLoggedIn.subscribe(usermanager.getVoiceLoggedIn(), SubscriptionSingleKey.Value);
            currentLocationInfo.subscribe(usermanager.getCurrentLocationInfo(), SubscriptionSingleKey.Value);
        } else
        {
            voiceLoggedIn.unsubscribe();
            currentLocationInfo.unsubscribe();
        }
        EventBus.getInstance().subscribe(this);
    }

    public void onStop()
    {
        voiceLoggedIn.unsubscribe();
        currentLocationInfo.unsubscribe();
        EventBus.getInstance().unsubscribe(this);
        super.onStop();
    }

    protected void onVoiceLoginStatusChanged(Boolean boolean1)
    {
        boolean1 = getActivity();
        if (boolean1 != null)
        {
            boolean1.supportInvalidateOptionsMenu();
        }
    }
}
